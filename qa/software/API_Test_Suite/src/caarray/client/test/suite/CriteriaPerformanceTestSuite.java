//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.PerformanceSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 20, 2009
 */
public class CriteriaPerformanceTestSuite extends SearchByCriteriaTestSuite
{
    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "Performance.csv";
    
    private static final String TYPE = "Type";
    private static final String PAGES = "Pages";
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, MAX_TIME, TYPE, PAGES};
    
    public enum SearchType
    {
        EXPERIMENT, EXPERIMENT_KEYWORD, FILE_CONTENT, DATASET, BIOMATERIAL, BIOMATERIAL_KEYWORD
    }

    /**
     * @param apiFacade
     */
    public CriteriaPerformanceTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#evaluateResults(java.lang.Object, caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(Object resultsList, TestBean search,
            TestResult testResult)
    {
        CriteriaSearch performanceSearch = (CriteriaSearch)search;
        if (performanceSearch.getMaxTime() != null)
        {
            
            if (testResult.getElapsedTime() > performanceSearch.getMaxTime())
            {
                String errorMessage = "Search did not complete in expected time, expected: "
                        + performanceSearch.getMaxTime()
                        + ", actual time: " + testResult.getElapsedTime();
                setTestResultFailure(testResult, performanceSearch, errorMessage);
            }
            else
            {
                String detail = "Search completed in expected time: "
                        + testResult.getElapsedTime();
                testResult.addDetail(detail);
            }
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(CriteriaSearch search, TestResult testResult)
            throws Exception
    {
        PerformanceSearch performanceSearch = (PerformanceSearch) search;
        Object result = null;
        try
        {
            SearchType type = performanceSearch.getSearchType();
            String api = search.getApi();
            LimitOffset offset = null;
            if (performanceSearch.getPages() != null)
            {
                offset = new LimitOffset();
                offset.setLimit(performanceSearch.getPages()*100);
            } 
            switch (type)
            {
            case EXPERIMENT:
                if (offset != null)
                {
                    result = apiFacade.searchForExperiments(api, new ExperimentSearchCriteria(), offset, false);
                }
                else
                {
                    result = apiFacade.experimentsByCriteriaSearchUtils(api,
                            new ExperimentSearchCriteria());
                }
                
                break;
            case EXPERIMENT_KEYWORD:
                if (offset != null)
                {
                    result = apiFacade.searchForExperimentByKeyword(api, new KeywordSearchCriteria(), offset);
                }
                else
                {
                    result = apiFacade.experimentsByKeywordSearchUtils(api,
                            new KeywordSearchCriteria());
                }
                
                break;
            case BIOMATERIAL:
                if (offset != null)
                {
                    result = apiFacade.searchForBiomaterials(api, new BiomaterialSearchCriteria(), offset);
                }
                else
                {
                    result = apiFacade.biomaterialsByCriteriaSearchUtils(api,
                            new BiomaterialSearchCriteria());
                }
                
                break;
            case BIOMATERIAL_KEYWORD:
                if (offset != null)
                {
                    result = apiFacade.searchForBiomaterialByKeyword(api, new BiomaterialKeywordSearchCriteria(), offset);
                }
                else
                {
                    result = apiFacade.biomaterialsByKeywordSearchUtils(api,
                            new BiomaterialKeywordSearchCriteria());
                }                
                break;
            case DATASET:
                result = apiFacade.getDataSet(api, new DataSetRequest());
                break;
            case FILE_CONTENT:
                result = apiFacade.copyFileContentsUtils(api,
                        new ArrayList<CaArrayEntityReference>(), false);
                break;
            default:
                break;
            }
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered executing search: "
                    + e.getClass()
                    + (e.getMessage() != null ? e.getMessage() : ""));
            testResult.addDetail("Exception encountered executing search: "
                    + e.getClass()
                    + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        }
        if (isResultsEmpty(result))
        {
            setTestResultFailure(testResult, performanceSearch, "No results were returned for the search");
        }
        return result;
    }
    
    private boolean isResultsEmpty(Object result)
    {
        if (result == null)
            return true;
        
        if (result instanceof SearchResult)
        {
            return ((SearchResult)result).getResults().isEmpty();
        }
        if (result instanceof List)
        {
            return ((List)result).isEmpty();
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new PerformanceSearch();
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
        PerformanceSearch search = (PerformanceSearch)criteriaSearch;
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(MAX_TIME) < input.length
                && !input[headerIndexMap.get(MAX_TIME)].equals(""))
            search.setMaxTime(Long
                    .parseLong(input[headerIndexMap.get(MAX_TIME)].trim()));
        if (headerIndexMap.get(TYPE) < input.length
                && !input[headerIndexMap.get(TYPE)].equals(""))
        {
            SearchType type = SearchType.valueOf(input[headerIndexMap.get(TYPE)].trim().toUpperCase());
            search.setSearchType(type);
        }
        if (headerIndexMap.get(PAGES) < input.length
                && !input[headerIndexMap.get(PAGES)].equals(""))
            search.setPages(Integer.parseInt(input[headerIndexMap.get(PAGES)]
                    .trim()));
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
        return "Performance";
    }

}
