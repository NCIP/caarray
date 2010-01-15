package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
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
import caarray.client.test.search.ExperimentKeywordSearch;
import caarray.client.test.search.TestBean;

public class ExperimentKeywordTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "ExperimentKeyword.csv";
    
    private static final String KEYWORD = "Keyword";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,ENUMERATE,
        API, EXPECTED_RESULTS, MIN_RESULTS, KEYWORD, API_UTILS_SEARCH};
    
    public ExperimentKeywordTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    @Override
    protected void evaluateResults(
            Object resultsList,
            TestBean search, TestResult testResult)
    {
        ExperimentKeywordSearch experimentSearch = (ExperimentKeywordSearch) search;
        List<Experiment> experimentResults = (List<Experiment>) resultsList;
        int namedResults = 0;
        for (Experiment experiment : experimentResults)
        {
            if (experiment != null && experiment.getTitle() != null)
                namedResults++;
        }
        if (experimentSearch.getExpectedResults() != null)
        {

            if (namedResults != experimentSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + experimentSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, experimentSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (experimentSearch.getMinResults() != null)
        {

            if (namedResults < experimentSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + experimentSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, experimentSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }}

    @Override
    protected Object executeSearch(
            CriteriaSearch search, TestResult testResult) throws Exception
    {
        ExperimentKeywordSearch criteriaSearch = (ExperimentKeywordSearch)search;
        List<Experiment> resultsList = new ArrayList<Experiment>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.experimentsByKeywordSearchUtils(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateExperimentsByKeyword(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else
            {
                SearchResult<Experiment> results = (SearchResult<Experiment>) apiFacade
                        .searchForExperimentByKeyword(search.getApi(),
                                criteriaSearch.getSearchCriteria(), null);
                resultsList.addAll(results.getResults());
                while (!results.isFullResult())
                {
                    LimitOffset offset = new LimitOffset(results
                            .getMaxAllowedResults(), results.getResults()
                            .size()
                            + results.getFirstResultOffset());
                    results = (SearchResult<Experiment>) apiFacade
                            .searchForExperimentByKeyword(search.getApi(),
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

    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new ExperimentKeywordSearch(); 
    }

    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        //NOOP
    }

    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        ExperimentKeywordSearch search = (ExperimentKeywordSearch) criteriaSearch;
        KeywordSearchCriteria criteria = new KeywordSearchCriteria();
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

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS; 
    }

    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String getType()
    {
        return "Experiment Keyword";
    }

}
