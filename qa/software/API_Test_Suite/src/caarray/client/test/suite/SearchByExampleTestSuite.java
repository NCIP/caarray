//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of search-by-example test searches, some
 * or all of which may be parameterized via a configuration spreadsheet.
 * 
 * @author vaughng 
 * Jun 26, 2009
 */
public abstract class SearchByExampleTestSuite extends ConfigurableTestSuite
{
    protected static final String MIN_RESULTS = "Min Results";
    protected static final String PAGES = "Pages";
    protected List<ExampleSearch> configuredSearches = new ArrayList<ExampleSearch>();
    protected SearchByExampleTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        ExampleSearch search = null;
    
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = getExampleSearch();
                boolean skip = false;
                if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
                {
                    search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                                                              .trim()));
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                }
                if (!skip)
                {
                    
                    populateSearch(input, search);  
                } 
                else
                {
                    search = null;
                }
            }
            else
            {
                boolean skip = false;
                if (search != null)
                {
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!skip)
                    {
                        populateAdditionalSearchValues(input, search);
                    } 
                }
                                  
            }
    
            if (search != null)
                configuredSearches.add(search);
    
            index++;
            if (index < spreadsheetRows.size())
            {
                row = spreadsheetRows.get(index);
            }
            else
            {
                row = null;
            }
        }
        filterSearches();
    }

    protected void executeTests(TestResultReport resultReport)
    {
        for (ExampleSearch search : configuredSearches)
        {
            TestResult testResult = new TestResult();
            try
            {
                if (search.getApi() == null)
                {
                    setTestResultFailure(testResult, search,
                            "No API indicated for test case: "
                                    + search.getTestCase());
                    resultReport.addTestResult(testResult);
                    continue;
                }

                
                ExampleSearchCriteria<AbstractCaArrayEntity> criteria = new ExampleSearchCriteria<AbstractCaArrayEntity>();
                criteria.setExample(search.getExample());
                if (search.getMatchMode() != null)
                {
                   criteria.setMatchMode(search.getMatchMode()); 
                }
                criteria.setExcludeZeroes(search.isExcludeZeros());
                List<AbstractCaArrayEntity> resultsList = new ArrayList<AbstractCaArrayEntity>();
                log.debug("Executing test: " + search.getTestCase());
                long startTime = System.currentTimeMillis();
                try
                {
                    if (search.isEnumerate())
                    {
                        resultsList.addAll(apiFacade.enumerateByExample(search.getApi(), criteria, search.getExample().getClass()));
                    }
                    else if (search.isApiUtil())
                    {
                        resultsList.addAll(apiFacade.searchByExampleUtils(search.getApi(), criteria));
                    }
                    else
                    {
                        LimitOffset offset = null;
                        // Set either the results desired per page
                        if (search.getResultsPerLimitOffset() != null)
                        {
                            offset = new LimitOffset(search.getResultsPerLimitOffset(),0);        
                        }
                        // or the point at which the search should be terminated
                        else if (search.getStopResults() != null)
                        {
                            offset = new LimitOffset(search.getStopResults(),0);
                        }
                        SearchResult<? extends AbstractCaArrayEntity> results = getSearchResults(search.getApi(), criteria, offset);
                        search.addPageReturned(results.getResults().size());
                        resultsList.addAll(results.getResults());
                        boolean fullResults = results.isFullResult();
                        boolean stopResults = search.getStopResults() != null && results.getResults().size() >= search.getStopResults();
                        if (!stopResults)
                        {
                            // Continue searching while more results remain
                            while ((!fullResults && results.getResults().size() > 0) || (offset != null && search.getResultsPerLimitOffset() != null 
                                    && results.getResults().size() == search.getResultsPerLimitOffset()))
                            {
                                offset = new LimitOffset();
                                if (search.getResultsPerLimitOffset() != null)
                                    offset.setLimit(search.getResultsPerLimitOffset());
                                offset.setOffset(results.getResults().size()
                                        + results.getFirstResultOffset());
                                
                                results = getSearchResults(search.getApi(), criteria, offset);
                                resultsList.addAll(results.getResults());
                                search.addPageReturned(results.getResults().size());
                                fullResults = results.isFullResult();
                            }
                        }
                        
                    }
                    
                }
                catch (Throwable t)
                {
                    String detail = "An exception occured executing " + getType() + " search-by-example: "
                    + t.getClass() + " " + t.getLocalizedMessage();
                    testResult.addDetail(detail);
                    t.printStackTrace();
                    search.setExceptionClass(t.getClass().toString());
                    log.error("Exception encountered:",t);
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                log.debug("Test " + search.getTestCase() + " completed.");
                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null)
                    testResult.setTestCase(search.getTestCase());

                evaluateResults(resultsList, search, testResult);
            }
            catch (Throwable t)
            {

                setTestResultFailure(testResult, search,
                        "An exception occured executing an " + getType() + " search-by-example: "
                                + t.getClass() + " " + t.getLocalizedMessage());
                t.printStackTrace();
                log.error("Exception encountered:",t);
            }

            resultReport.addTestResult(testResult);
        }
        
        System.out.println(getType() + " tests complete ...");
    }
    
    /**
     * Filters the list of tests to be executed to only those either explicitly requested
     * or only those not explicitly excluded.
     */
    private void filterSearches()
    {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL))
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float)Math.floor(search.getTestCase())))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches; 
        }
    }

    /**
     * Convenience method for adding an unexpected error message to a test result.
     * 
     * @param testResult
     * @param search
     * @param errorMessage
     */
    protected void setTestResultFailure(TestResult testResult,
            ExampleSearch search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
    }
    
    /**
     * Populates an ExampleSearch bean with values taken from a configuration spreadsheet.
     * 
     * @param input Input row taken from a configuration spreadsheet.
     * @param exampleSearch ExampleSearch bean to be populated.
     */
    protected abstract void populateSearch(String[] input, ExampleSearch exampleSearch);
    
    /**
     * For test cases configured in multiple rows of a spreadsheet, populates search
     * with the values entered in continuation rows.
     * @param input Input row taken from a configuration spreadsheet.
     * @param exampleSearch ExampleSearch bean to be populated.
     */
    protected abstract void populateAdditionalSearchValues(String[] input, ExampleSearch exampleSearch);
    
    /**
     * Determines the pass/fail status of a test based on the given search results.
     * 
     * @param resultsList The results of a test search.
     * @param search ExampleSearch specifying the expected results of the test search.
     * @param testResult TestResult to which a status will be added.
     */
    protected abstract void evaluateResults(List<? extends AbstractCaArrayEntity> resultsList, ExampleSearch search, TestResult testResult);

    /**
     * Returns a new, type-specific ExampleSearch object to be populated.
     * @return a new, type-specific ExampleSearch object to be populated.
     */
    protected abstract ExampleSearch getExampleSearch();
}
