//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.FileType;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.FileTypeSearch;

/**
 * Encapsulates a collection of FileType search-by-example tests.
 * 
 * @author vaughng
 *
 */
public class FileTypeTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "FileType.csv";

    private static final String NAME = "Name";
    private static final String EXPECTED_NAME = "Expected Name";
    private static final String CATEGORY = "Category";
    private static final String EXPECTED_CATEGORY = "Expected Category";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, NAME, CATEGORY, EXPECTED_RESULTS, MIN_RESULTS, EXPECTED_NAME, EXPECTED_CATEGORY };
    
    /**
     * @param apiFacade
     */
    public FileTypeTestSuite(ApiFacade apiFacade)
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
        FileTypeSearch fileTypeSearch = (FileTypeSearch)search;
        List<FileType> fileResults = (List<FileType>)resultsList;
        int namedResults = 0;
        for (FileType fileType : fileResults)
        {
            if (fileType.getName() != null && fileType.getId() != null)
                namedResults++;
        }
        if (fileTypeSearch.getExpectedResults() != null)
        {
            
            if (namedResults != fileTypeSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + fileTypeSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, fileTypeSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (fileTypeSearch.getMinResults() != null)
        {
            
            if (namedResults < fileTypeSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + fileTypeSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, fileTypeSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (testResult.isPassed() && !fileTypeSearch.getExpectedNames().isEmpty())
        {
            
            for (String expectedName : fileTypeSearch.getExpectedNames())
            {
                boolean foundName = false;
                for (FileType fileType : fileResults)
                {
                    if (fileType.getName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected name: " + expectedName;
                    setTestResultFailure(testResult, fileTypeSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (testResult.isPassed() && !fileTypeSearch.getExpectedCategories().isEmpty())
        {
            
            for (String expectedCategory : fileTypeSearch.getExpectedCategories())
            {
                boolean foundName = false;
                for (FileType fileType : fileResults)
                {
                    //TODO: not sure how this works now
                    /*if (fileType.getCategory() != null && fileType.getCategory().getName().equalsIgnoreCase(expectedCategory))
                    {
                        foundName = true;
                        break;
                    }*/
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected category: " + expectedCategory;
                    setTestResultFailure(testResult, fileTypeSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected category: " + expectedCategory;
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
        return new FileTypeSearch();
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
            FileTypeSearch search = (FileTypeSearch)exampleSearch;
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)]);
        }
        if (headerIndexMap.get(EXPECTED_CATEGORY) < input.length
                && !input[headerIndexMap.get(EXPECTED_CATEGORY)].equals(""))
        {
            FileTypeSearch search = (FileTypeSearch)exampleSearch;
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_CATEGORY)]);
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        FileTypeSearch search = (FileTypeSearch)exampleSearch;
        FileType example = new FileType();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
            
        
        search.setFileType(example);
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
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)].trim());
        if (headerIndexMap.get(EXPECTED_CATEGORY) < input.length
                && !input[headerIndexMap.get(EXPECTED_CATEGORY)].equals(""))
            search.addExpectedCategory(input[headerIndexMap.get(EXPECTED_CATEGORY)].trim());
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
        return "FileType";
    }

}
