/**
 * 
 */
package caarray.legacy.client.test.suite;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.search.CaArrayFileSearch;
import caarray.legacy.client.test.search.ExampleSearch;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class CaArrayFileTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "CaArrayFile.csv";

    private static final String NAME = "Name";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, NAME, EXPECTED_RESULTS, MIN_RESULTS};

    /**
     * @param apiFacade
     */
    public CaArrayFileTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.legacy.client.test.search.ExampleSearch, caarray.legacy.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayObject> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        CaArrayFileSearch fileSearch = (CaArrayFileSearch)search;
        List<CaArrayFile> fileResults = (List<CaArrayFile>)resultsList;
        int namedResults = 0;
        for (CaArrayFile file : fileResults)
        {
            if (file.getName() != null)
                namedResults++;
        }
        if (fileSearch.getExpectedResults() != null)
        {
            
            if (namedResults != fileSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + fileSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getMinResults() != null)
        {
            
            if (namedResults < fileSearch.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected minimum: "
                        + fileSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new CaArrayFileSearch();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        // N/A
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        CaArrayFileSearch search = (CaArrayFileSearch)exampleSearch;
        CaArrayFile example = new CaArrayFile();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        
        search.setCaArrayFile(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer
                    .parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
       return "CaArrayFile";
    }

}
