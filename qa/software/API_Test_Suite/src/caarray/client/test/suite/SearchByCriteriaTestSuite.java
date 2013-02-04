//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 *
 */
public abstract class SearchByCriteriaTestSuite extends ConfigurableTestSuite
{
    protected static final String MAX_TIME = "Max Time";
    protected List<CriteriaSearch> configuredSearches = new ArrayList<CriteriaSearch>();
    /**
     * @param apiFacade
     */
    public SearchByCriteriaTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#constructSearches(java.util.List)
     */
    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        CriteriaSearch search = null;
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
    
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = getCriteriaSearch();
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
                    try
                    {
                        populateSearch(input, search);  
                        configuredSearches.add(search);
                    }
                    catch (Exception e)
                    {
                        log.error("Exception encountered:",e);
                        throw new TestConfigurationException("Exception constructing test case " + search.getTestCase() + ": "+ e.getClass());
                    }
                }              
                
            }
            else
            {
                boolean skip = false;
                if (search == null)
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
                if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!skip)
                {try
                {
                    populateAdditionalSearchValues(input, search);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    log.error("Exception encountered:",e);
                    throw new TestConfigurationException("Expection constructing test case: " + e);
                }
                    
                }            
            }
    
            
    
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

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.client.test.TestResultReport)
     */
    @Override
    protected void executeTests(TestResultReport resultReport)
    {
        for (CriteriaSearch search : configuredSearches)
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

                log.debug("Executing test: " + search.getTestCase());
                long startTime = System.currentTimeMillis();
                Object resultsList = executeSearch(search, testResult);
                long elapsedTime = System.currentTimeMillis() - startTime;
                log.debug("Test " + search.getTestCase() + " completed in: " + elapsedTime);
                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null)
                    testResult.setTestCase(search.getTestCase());
                System.out.println("evaluating " + search.getTestCase() + " ...");
               evaluateResults(resultsList, search, testResult);
            }
            catch (Throwable t)
            {

                setTestResultFailure(testResult, search,
                        "An exception occured executing an " + getType() + " search: "
                                + t.getMessage());
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
            List<CriteriaSearch> filteredSearches = new ArrayList<CriteriaSearch>();
            for (CriteriaSearch search : configuredSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<CriteriaSearch> filteredSearches = new ArrayList<CriteriaSearch>();
            for (CriteriaSearch search : configuredSearches)
            {
                if (!excludedTests.contains(search.getTestCase())&& !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<CriteriaSearch> filteredSearches = new ArrayList<CriteriaSearch>();
            for (CriteriaSearch search : configuredSearches)
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
            TestBean search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
        System.out.println("test failed with " + errorMessage);
    }
    
    /**
     * Populates a CriteriaSearch bean with values taken from a configuration spreadsheet.
     * 
     * @param input Input row taken from a configuration spreadsheet.
     * @param criteriaSearch CriteriaSearch bean to be populated.
     * @throws Exception TODO
     */
    protected abstract void populateSearch(String[] input, TestBean criteriaSearch) throws Exception;
    
    /**
     * For test cases configured in multiple rows of a spreadsheet, populates search
     * with the values entered in continuation rows.
     * @param input Input row taken from a configuration spreadsheet.
     * @param criteriaSearch CriteriaSearch bean to be populated.
     * @throws Exception TODO
     */
    protected abstract void populateAdditionalSearchValues(String[] input, TestBean criteriaSearch) throws Exception;
    
    /**
     * Determines the pass/fail status of a test based on the given search results.
     * 
     * @param resultsList The results of a test search.
     * @param search ExampleSearch specifying the expected results of the test search.
     * @param testResult TestResult to which a status will be added.
     */
    protected abstract void evaluateResults(Object resultsList, TestBean search, TestResult testResult);

    /**
     * Returns a new, type-specific CriteriaSearch object to be populated.
     * @return a new, type-specific CriteriaSearch object to be populated.
     */
    protected abstract CriteriaSearch getCriteriaSearch();
    
    /**
     * Executes a type-specific criteria search.
     * 
     * @param search CriteriaSearch that will be used to execute the search.
     * @param testResult TODO
     * @return The results of the search.
     * @throws Exception TODO
     */
    protected abstract Object executeSearch(CriteriaSearch search, TestResult testResult) throws Exception;

}
