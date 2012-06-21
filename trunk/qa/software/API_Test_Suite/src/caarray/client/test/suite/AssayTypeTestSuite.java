/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.AssayTypeSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of AssayType search-by-example tests.
 * 
 * @author vaughng
 *
 */
public class AssayTypeTestSuite extends SearchByExampleTestSuite
{
    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
        + File.separator + "AssayType.csv";
    
    private static final String NAME = "Name";
    private static final String EXPECTED_NAME = "Expected Name";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, NAME, EXPECTED_NAME, EXPECTED_RESULTS };
    /**
     * @param apiFacade
     */
    public AssayTypeTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        AssayTypeSearch search = (AssayTypeSearch)exampleSearch;
        AssayType example = new AssayType();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
    
        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        
        search.setAssayType(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)].trim());
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
        {
            AssayTypeSearch search = (AssayTypeSearch)exampleSearch;
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)]);
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.client.test.search.ExampleSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        AssayTypeSearch assaySearch = (AssayTypeSearch)search;
        List<AssayType> assayResults = (List<AssayType>)resultsList;
        if (assaySearch.getExpectedResults() != null)
        {
            int namedResults = 0;
            for (AssayType assayType : assayResults)
            {
                if (assayType.getName() != null)
                    namedResults++;
            }
            if (namedResults != assaySearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + assaySearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, assaySearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (testResult.isPassed() && !assaySearch.getExpectedNames().isEmpty())
        {
            
            for (String expectedName : assaySearch.getExpectedNames())
            {
                boolean foundName = false;
                for (AssayType assayType : assayResults)
                {
                    if (assayType.getName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected name: " + expectedName;
                    setTestResultFailure(testResult, assaySearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new AssayTypeSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "AssayType";
    }

}
