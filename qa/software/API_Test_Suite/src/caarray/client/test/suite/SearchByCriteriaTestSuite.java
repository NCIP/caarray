/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;
import caarray.client.test.search.CriteriaSearch;

/**
 * @author vaughng
 *
 */
public abstract class SearchByCriteriaTestSuite extends ConfigurableTestSuite
{
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
    
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = getCriteriaSearch();
                populateSearch(input, search);  
            }
            else
            {
                if (search == null)
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
    
                populateAdditionalSearchValues(input, search);
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
        filterSearchesByAPI();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        // TODO Auto-generated method stub

    }
    
    private void filterSearchesByAPI()
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
    }
    
    /**
     * Populates a CriteriaSearch bean with values taken from a configuration spreadsheet.
     * 
     * @param input Input row taken from a configuration spreadsheet.
     * @param criteriaSearch CriteriaSearch bean to be populated.
     */
    protected abstract void populateSearch(String[] input, CriteriaSearch criteriaSearch);
    
    /**
     * For test cases configured in multiple rows of a spreadsheet, populates search
     * with the values entered in continuation rows.
     * @param input Input row taken from a configuration spreadsheet.
     * @param criteriaSearch CriteriaSearch bean to be populated.
     */
    protected abstract void populateAdditionalSearchValues(String[] input, CriteriaSearch criteriaSearch);
    
    /**
     * Determines the pass/fail status of a test based on the given search results.
     * 
     * @param resultsList The results of a test search.
     * @param search ExampleSearch specifying the expected results of the test search.
     * @param testResult TestResult to which a status will be added.
     */
    protected abstract void evaluateResults(List<? extends AbstractCaArrayEntity> resultsList, CriteriaSearch search, TestResult testResult);

    /**
     * Returns a new, type-specific CriteriaSearch object to be populated.
     * @return a new, type-specific CriteriaSearch object to be populated.
     */
    protected abstract CriteriaSearch getCriteriaSearch();

}
