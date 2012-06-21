/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.BiomaterialKeywordSearch;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 9, 2009
 */
public class BiomaterialKeywordTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "BiomaterialKeyword.csv";
    
    private static final String KEYWORD = "Keyword";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,ENUMERATE,
        API, EXPECTED_RESULTS, MIN_RESULTS, KEYWORD, API_UTILS_SEARCH};
    
    /**
     * @param apiFacade
     */
    public BiomaterialKeywordTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#evaluateResults(java.util.List, caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            Object resultsList,
            TestBean search, TestResult testResult)
    {
        BiomaterialKeywordSearch bioSearch = (BiomaterialKeywordSearch) search;
        List<Biomaterial> bioResults = (List<Biomaterial>) resultsList;
        int namedResults = 0;
        for (Biomaterial biomaterial : bioResults)
        {
            if (biomaterial != null && biomaterial.getName() != null)
                namedResults++;
        }
        if (bioSearch.getExpectedResults() != null)
        {

            if (namedResults != bioSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + bioSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, bioSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (bioSearch.getMinResults() != null)
        {

            if (namedResults < bioSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + bioSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, bioSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }}

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(
            CriteriaSearch search, TestResult testResult) throws Exception
    {
        BiomaterialKeywordSearch criteriaSearch = (BiomaterialKeywordSearch)search;
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.biomaterialsByKeywordSearchUtils(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateBiomaterialsByKeyword(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else
            {
                SearchResult<Biomaterial> results = (SearchResult<Biomaterial>) apiFacade
                        .searchForBiomaterialByKeyword(search.getApi(),
                                criteriaSearch.getSearchCriteria(), null);
                resultsList.addAll(results.getResults());
                while (!results.isFullResult())
                {
                    LimitOffset offset = new LimitOffset(results
                            .getMaxAllowedResults(), results.getResults()
                            .size()
                            + results.getFirstResultOffset());
                    results = (SearchResult<Biomaterial>) apiFacade
                            .searchForBiomaterialByKeyword(search.getApi(),
                                    criteriaSearch.getSearchCriteria(), offset);
                    resultsList.addAll(results.getResults());
                }
            }
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered executing search: " + e.getMessage());
            testResult.addDetail("Exception encountered executing search: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        
        
        
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new BiomaterialKeywordSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        //NOOP
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        BiomaterialKeywordSearch search = (BiomaterialKeywordSearch) criteriaSearch;
        BiomaterialKeywordSearchCriteria criteria = new BiomaterialKeywordSearchCriteria();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
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
        if (headerIndexMap.get(API_UTILS_SEARCH) < input.length
                && !input[headerIndexMap.get(API_UTILS_SEARCH)].equals(""))
            search.setApiUtilsSearch(Boolean.parseBoolean(input[headerIndexMap
                    .get(API_UTILS_SEARCH)].trim()));
        if (headerIndexMap.get(ENUMERATE) < input.length
                && !input[headerIndexMap.get(ENUMERATE)].equals(""))
            search.setEnumerate(Boolean.parseBoolean(input[headerIndexMap
                    .get(ENUMERATE)].trim()));
        
        if (headerIndexMap.get(KEYWORD) < input.length
                && !input[headerIndexMap.get(KEYWORD)].equals(""))
        {
            String keyword = input[headerIndexMap.get(KEYWORD)].trim();
            if (keyword.startsWith(VAR_START))
                keyword = getVariableValue(keyword);
            
            criteria.setKeyword(keyword);
        }
        
        search.setSearchCriteria(criteria);
        
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
        return "Biomaterial Keyword";
    }

}
