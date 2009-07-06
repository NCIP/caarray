/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.QuantitationTypeSearch;

/**
 *  Encapsulates a collection of QuantitationType search-by-example tests.
 *  
 * @author vaughng
 * Jul 2, 2009
 */
public class QuantitationTypeTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "QuantitationType.csv";

    private static final String NAME = "Name";
    private static final String EXPECTED_NAME = "Expected Name";
    private static final String DATA_TYPE = "Data Type";
    private static final String EXPECTED_DATA_TYPE = "Expected Data Type";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, MIN_RESULTS, EXPECTED_NAME,
            DATA_TYPE, EXPECTED_DATA_TYPE, EXPECTED_RESULTS };
    /**
     * @param apiFacade
     */
    public QuantitationTypeTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.client.test.search.ExampleSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        QuantitationTypeSearch quantitationTypeSearch = (QuantitationTypeSearch)search;
        List<QuantitationType> quantitationResults = (List<QuantitationType>)resultsList;
        int namedResults = 0;
        for (QuantitationType quantitationType : quantitationResults)
        {
            if (quantitationType.getName() != null && quantitationType.getDataType() != null)
                namedResults++;
        }
        if (quantitationTypeSearch.getExpectedResults() != null)
        {
            
            if (namedResults != quantitationTypeSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + quantitationTypeSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, quantitationTypeSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (quantitationTypeSearch.getMinResults() != null)
        {
            
            if (namedResults < quantitationTypeSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + quantitationTypeSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, quantitationTypeSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (!quantitationTypeSearch.getExpectedNames().isEmpty())
        {
            
            for (String expectedName : quantitationTypeSearch.getExpectedNames())
            {
                boolean foundName = false;
                for (QuantitationType quantitationType : quantitationResults)
                {
                    if (quantitationType.getName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected name: " + expectedName;
                    setTestResultFailure(testResult, quantitationTypeSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!quantitationTypeSearch.getExpectedDataTypes().isEmpty())
        {
            
            for (String expectedDataType : quantitationTypeSearch.getExpectedDataTypes())
            {
                boolean foundName = false;
                for (QuantitationType quantitationType : quantitationResults)
                {
                    if (quantitationType.getDataType().equals(DataType.valueOf(expectedDataType.toUpperCase())))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected data type: " + expectedDataType;
                    setTestResultFailure(testResult, quantitationTypeSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected data type: " + expectedDataType;
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
        return new QuantitationTypeSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        QuantitationTypeSearch search = (QuantitationTypeSearch)exampleSearch;
        
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)]
                                               .trim());
        
            
        if (headerIndexMap.get(EXPECTED_DATA_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_TYPE)].equals(""))
            search.addExpectedDataType(input[headerIndexMap.get(EXPECTED_DATA_TYPE)]
                    .trim());
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        QuantitationTypeSearch search = (QuantitationTypeSearch) exampleSearch;
        QuantitationType example = new QuantitationType();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length
                && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(DATA_TYPE) < input.length
                && !input[headerIndexMap.get(DATA_TYPE)].equals(""))
            example.setDataType(DataType.valueOf(input[headerIndexMap.get(DATA_TYPE)].trim().toUpperCase()));

        search.setQuantitationType(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap
                    .get(TEST_CASE)].trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer.parseInt(input[headerIndexMap
                    .get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer.parseInt(input[headerIndexMap
                    .get(MIN_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)]
                    .trim());
        if (headerIndexMap.get(EXPECTED_DATA_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_TYPE)].equals(""))
            search.addExpectedDataType(input[headerIndexMap.get(EXPECTED_DATA_TYPE)]
                    .trim());
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
        return "QuantitationType";
    }

}
