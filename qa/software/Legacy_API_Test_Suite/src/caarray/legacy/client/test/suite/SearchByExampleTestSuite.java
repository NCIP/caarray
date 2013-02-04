//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.suite;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.ArrayList;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;
import caarray.legacy.client.test.search.ExampleSearch;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public abstract class SearchByExampleTestSuite extends ConfigurableTestSuite {

    protected static final String MIN_RESULTS = "Min Results";
    protected static final String PAGES = "Pages";
    protected List<ExampleSearch> configuredSearches = new ArrayList<ExampleSearch>();

    protected SearchByExampleTestSuite(ApiFacade apiFacade) {
        super(apiFacade);
    }

    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();

        ExampleSearch search = null;

        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null) {
            String[] input = TestUtils.split(row, DELIMITER);
            boolean skip = false;
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input)) {
                search = getExampleSearch();                
                if (headerIndexMap.get(TEST_CASE) < input.length && !input[headerIndexMap.get(TEST_CASE)].equals("")) {
                    search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)].trim()));
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float) Math.floor(search.getTestCase())))) {
                        skip = true;
                    }
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase()) && !includeTests.contains((float) Math.floor(search.getTestCase())))) {
                        skip = true;
                    }
                }
                if (!skip) {

                    populateSearch(input, search);
                }
            } else {
                if (search == null) {
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
                }
                if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float) Math.floor(search.getTestCase())))) {
                    skip = true;
                }
                if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase()) && !includeTests.contains((float) Math.floor(search.getTestCase())))) {
                    skip = true;
                }
                if (!skip) {
                    populateAdditionalSearchValues(input, search);
                }

            }

            if (search != null && !skip) {
                configuredSearches.add(search);
            }

            index++;
            if (index < spreadsheetRows.size()) {
                row = spreadsheetRows.get(index);
            } else {
                row = null;
            }
        }
        filterSearches();
    }

    protected void executeConfiguredTests(TestResultReport resultReport) {
        for (ExampleSearch search : configuredSearches) {
            TestResult testResult = new TestResult();
            try {
                if (search.getApi() == null) {
                    setTestResultFailure(testResult, search,
                            "No API indicated for test case: "
                            + search.getTestCase());
                    resultReport.addTestResult(testResult);
                    continue;
                } else { //MT
                    testResult.setType(search.getApi()); //MT
                } //MT


                List<AbstractCaArrayObject> resultsList = new ArrayList<AbstractCaArrayObject>();
                long startTime = System.currentTimeMillis();
                try {

                    if (search.isLogin()) {
                        List<AbstractCaArrayObject> nonLoginResults = new ArrayList<AbstractCaArrayObject>();
                        nonLoginResults.addAll(apiFacade.searchByExample(search.getApi(), search.getExample(), false));
                        startTime = System.currentTimeMillis();
                        resultsList.addAll(apiFacade.searchByExample(search.getApi(), search.getExample(), true));
                        if (resultsList.size() <= nonLoginResults.size()) {
                            String errorMessage = "Login search did not return more results than non-login search. Login: "
                                    + resultsList.size() + ", non-login: " + nonLoginResults.size();
                            setTestResultFailure(testResult, search, errorMessage);
                        } else {
                            String detail = "Login search returned more results than non-login search: " + resultsList.size()
                                    + ", " + nonLoginResults.size();
                            testResult.addDetail(detail);
                        }
                    } else {
                        resultsList.addAll(apiFacade.searchByExample(search.getApi(), search.getExample(), false));
                    }


                } catch (Throwable t) {
                    String detail = "An exception occured executing " + getType() + " search-by-example: "
                            + t.getClass() + " " + t.getLocalizedMessage();
                    testResult.addDetail(detail);
                    testResult.setThrowable(t); //MT
//                    t.printStackTrace();
                }
                long elapsedTime = System.currentTimeMillis() - startTime;

                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null) {
                    testResult.setTestCase(search.getTestCase());
                }

                evaluateResults(resultsList, search, testResult);
            } catch (Throwable t) {

                setTestResultFailure(testResult, search,
                        "An exception occured executing an " + getType() + " search-by-example: "
                        + t.getClass() + " " + t.getLocalizedMessage());
                testResult.setThrowable(t); //MT
                t.printStackTrace();
            }

            resultReport.addTestResult(testResult);
        }

        System.out.println(getType() + " tests complete ...");
    }

    private void filterSearches() {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL)) {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches) {
                if (search.getApi().equalsIgnoreCase(api)) {
                    filteredSearches.add(search);
                }
            }
            configuredSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty()) {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches) {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase())) {
                    filteredSearches.add(search);
                }
            }
            configuredSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty()) {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches) {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float) Math.floor(search.getTestCase()))) {
                    filteredSearches.add(search);
                }
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
            ExampleSearch search, String errorMessage) {
        testResult.setPassed(false);
        if (search.getTestCase() != null) {
            testResult.setTestCase(search.getTestCase());
        }
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
    protected abstract void evaluateResults(List<? extends AbstractCaArrayObject> resultsList, ExampleSearch search, TestResult testResult);

    /**
     * Returns a new, type-specific ExampleSearch object to be populated.
     * @return a new, type-specific ExampleSearch object to be populated.
     */
    protected abstract ExampleSearch getExampleSearch();
}
