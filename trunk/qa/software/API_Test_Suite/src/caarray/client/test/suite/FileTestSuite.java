/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileMetadata;
import gov.nih.nci.caarray.external.v1_0.data.FileType;

import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.FileSearch;

/**
 * Encapsulates a collection of File search-by-example tests.
 * 
 * @author Ye Wu
 *
 */
public class FileTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + java.io.File.separator + "File.csv";

    private static final String NAME = "Name";
    private static final String FILE_TYPE = "File Type";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, NAME, FILE_TYPE, EXPECTED_RESULTS, MIN_RESULTS, EXCLUDE_ZERO};
    
    /**
     * @param apiFacade
     */
    public FileTestSuite(ApiFacade apiFacade)
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
        FileSearch fileSearch = (FileSearch)search;
        List<File> fileResults = (List<File>)resultsList;
        int namedResults = 0;
        for (File file : fileResults)
        {
            if (file.getMetadata() != null && file.getMetadata().getName() != null)
                namedResults++;
        }
        if (fileSearch.getExpectedResults() != null)
        {
            
            if (namedResults != fileSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + fileSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, fileSearch, errorMessage);
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
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + fileSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, fileSearch, errorMessage);
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
     * @see caarray.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new FileSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
    	return;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        FileSearch search = (FileSearch)exampleSearch;
        File example = new File();
        example.setMetadata(new FileMetadata());
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.getMetadata().setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(FILE_TYPE) < input.length && !input[headerIndexMap.get(FILE_TYPE)].equals(""))
        {
            String type = input[headerIndexMap.get(FILE_TYPE)].trim();
            FileType fileType = new FileType();
            fileType.setName(type);
            example.getMetadata().setFileType(fileType);
        }
        
        search.setDataFile(example);
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
        if (headerIndexMap.get(EXCLUDE_ZERO) < input.length
                && !input[headerIndexMap.get(EXCLUDE_ZERO)].equals(""))
            search.setExcludeZeros(Boolean
                    .parseBoolean(input[headerIndexMap.get(EXCLUDE_ZERO)].trim()));
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
        return "File";
    }

}
