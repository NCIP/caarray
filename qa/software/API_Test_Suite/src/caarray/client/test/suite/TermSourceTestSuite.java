/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.TermSourceSearch;

/**
 *  Encapsulates a collection of TermSource search-by-example tests.
 *  
 * @author vaughng
 * Jul 2, 2009
 */
public class TermSourceTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "TermSource.csv";

    private static final String NAME = "Name";
    private static final String EXPECTED_NAME = "Expected Name";
    private static final String URL = "Url";
    private static final String EXPECTED_URL = "Expected Url";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, MIN_RESULTS, EXPECTED_NAME, URL,
            EXPECTED_URL, EXPECTED_RESULTS };
    /**
     * @param apiFacade
     */
    public TermSourceTestSuite(ApiFacade apiFacade)
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
        TermSourceSearch termSearch = (TermSourceSearch)search;
        List<TermSource> termSourceResults = (List<TermSource>)resultsList;
        int namedResults = 0;
        for (TermSource termSource : termSourceResults)
        {
            if (termSource.getName() != null)
                namedResults++;
        }
        if (termSearch.getExpectedResults() != null)
        {
            
            if (namedResults != termSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + termSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, termSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (termSearch.getMinResults() != null)
        {
            
            if (namedResults < termSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + termSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, termSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (!termSearch.getExpectedNames().isEmpty())
        {
            
            for (String expectedName : termSearch.getExpectedNames())
            {
                boolean foundName = false;
                for (TermSource termSource : termSourceResults)
                {
                    if (termSource.getName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected name: " + expectedName;
                    setTestResultFailure(testResult, termSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!termSearch.getExpectedUrls().isEmpty())
        {
            
            for (String expectedUrl : termSearch.getExpectedUrls())
            {
                boolean foundName = false;
                for (TermSource termSource : termSourceResults)
                {
                    if (termSource.getUrl().equalsIgnoreCase(expectedUrl))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected url: " + expectedUrl;
                    setTestResultFailure(testResult, termSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected url: " + expectedUrl;
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
       return new TermSourceSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        TermSourceSearch search = (TermSourceSearch)exampleSearch;
        
        if (headerIndexMap.get(EXPECTED_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_NAME)].equals(""))
            search.addExpectedName(input[headerIndexMap.get(EXPECTED_NAME)]
                                               .trim());
        
            
        if (headerIndexMap.get(EXPECTED_URL) < input.length
                && !input[headerIndexMap.get(EXPECTED_URL)].equals(""))
            search.addExpectedUrl(input[headerIndexMap.get(EXPECTED_URL)]
                    .trim());
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        TermSourceSearch search = (TermSourceSearch) exampleSearch;
        TermSource example = new TermSource();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length
                && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(URL) < input.length
                && !input[headerIndexMap.get(URL)].equals(""))
            example.setUrl(input[headerIndexMap.get(URL)].trim());

        search.setTermSource(example);
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
        if (headerIndexMap.get(EXPECTED_URL) < input.length
                && !input[headerIndexMap.get(EXPECTED_URL)].equals(""))
            search.addExpectedUrl(input[headerIndexMap.get(EXPECTED_URL)]
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
        return "TermSource";
    }

}
