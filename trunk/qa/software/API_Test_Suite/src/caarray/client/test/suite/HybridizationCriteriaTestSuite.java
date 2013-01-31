//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.HybridizationCriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class HybridizationCriteriaTestSuite extends SearchByCriteriaTestSuite
{


    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "HybridizationCriteria.csv";

    private static final String EXPERIMENT_ID = "Experiment Id";
    private static final String EXPERIMENT_TITLE = "Experiment Name";
    private static final String EXPERIMENT_REF = "Experiment Reference";
    private static final String SAMPLE = "Sample";
    private static final String SAMPLE_REF = "Sample Reference";
    private static final String NAME = "Name";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,ENUMERATE,
            API, EXPERIMENT_ID, EXPECTED_RESULTS, MIN_RESULTS,EXPERIMENT_TITLE, EXPERIMENT_REF,
            SAMPLE, SAMPLE_REF, NAME, API_UTILS_SEARCH };

    
    /**
     * @param apiFacade
     */
    public HybridizationCriteriaTestSuite(ApiFacade apiFacade)
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
        HybridizationCriteriaSearch hybSearch = (HybridizationCriteriaSearch)search;
        List<Hybridization> hybResults = (List<Hybridization>)resultsList;
        int namedResults = 0;
        for (Hybridization hyb : hybResults)
        {
            if (hyb != null && hyb.getName() != null)
                namedResults++;
        }
        if (hybSearch.getExpectedResults() != null)
        {
            
            if (namedResults != hybSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + hybSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, hybSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (hybSearch.getMinResults() != null)
        {
            
            if (namedResults < hybSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + hybSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, hybSearch, errorMessage);
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
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(CriteriaSearch search, TestResult testResult)
            throws Exception
    {
        HybridizationCriteriaSearch criteriaSearch = (HybridizationCriteriaSearch)search;
        List<Hybridization> resultsList = new ArrayList<Hybridization>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.hybridizationsByCriteriaSearchUtils(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateHybridizations(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else
            {
                SearchResult<Hybridization> results = (SearchResult<Hybridization>) apiFacade
                        .searchForHybridizations(criteriaSearch.getApi(),
                                criteriaSearch.getSearchCriteria(), null);
                resultsList.addAll(results.getResults());
                while (!results.isFullResult())
                {
                    LimitOffset offset = new LimitOffset(results
                            .getMaxAllowedResults(), results.getResults()
                            .size()
                            + results.getFirstResultOffset());
                    results = (SearchResult<Hybridization>) apiFacade
                            .searchForHybridizations(criteriaSearch.getApi(),
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
        return new HybridizationCriteriaSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        HybridizationCriteriaSearch search = (HybridizationCriteriaSearch)criteriaSearch;
        HybridizationSearchCriteria criteria = search.getSearchCriteria();
        
        if (headerIndexMap.get(SAMPLE) < input.length && !input[headerIndexMap.get(SAMPLE)].equals(""))
        {
            String name = input[headerIndexMap.get(SAMPLE)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Biomaterial biomaterial = apiFacade.getBiomaterial(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (biomaterial != null)
            {
                ref = biomaterial.getReference();
            }
            else
            {
                ref.setId(name);
            }
            criteria.getBiomaterials().add(ref);
        }
        if (headerIndexMap.get(SAMPLE_REF) < input.length && !input[headerIndexMap.get(SAMPLE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(SAMPLE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getBiomaterials().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
        {
            String name = input[headerIndexMap.get(NAME)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            criteria.getNames().add(name);
        }

    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        HybridizationCriteriaSearch search = (HybridizationCriteriaSearch)criteriaSearch;
        HybridizationSearchCriteria criteria = new HybridizationSearchCriteria();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
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
        if (headerIndexMap.get(API_UTILS_SEARCH) < input.length
                && !input[headerIndexMap.get(API_UTILS_SEARCH)].equals(""))
            search.setApiUtilsSearch(Boolean.parseBoolean(input[headerIndexMap
                    .get(API_UTILS_SEARCH)].trim()));
        if (headerIndexMap.get(ENUMERATE) < input.length
                && !input[headerIndexMap.get(ENUMERATE)].equals(""))
            search.setEnumerate(Boolean.parseBoolean(input[headerIndexMap
                    .get(ENUMERATE)].trim()));
        
        if (headerIndexMap.get(EXPERIMENT_TITLE) < input.length && !input[headerIndexMap.get(EXPERIMENT_TITLE)].equals(""))
        {
            String title = input[headerIndexMap.get(EXPERIMENT_TITLE)];
            if (title.startsWith(VAR_START))
                title = getVariableValue(title);
            Experiment experiment = apiFacade.getExperiment(search.getApi(), title);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (experiment != null)
            {
                ref = experiment.getReference();
            }
            else
            {
                ref.setId(title);
            }
            criteria.setExperiment(ref);
        }
        if (headerIndexMap.get(EXPERIMENT_ID) < input.length && !input[headerIndexMap.get(EXPERIMENT_ID)].equals(""))
        {
            String id = input[headerIndexMap.get(EXPERIMENT_ID)];
            if (id.startsWith(VAR_START))
                id = getVariableValue(id);
            ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
            crit.setPublicIdentifier(id);
            List<Experiment> experiments = (List<Experiment>)apiFacade.searchForExperiments(search.getApi(), crit, null, false).getResults();
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (!experiments.isEmpty())
            {
                ref = experiments.get(0).getReference();
            }
            else
            {
                ref.setId(id);
            }
            criteria.setExperiment(ref);
        }
        if (headerIndexMap.get(EXPERIMENT_REF) < input.length && !input[headerIndexMap.get(EXPERIMENT_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(EXPERIMENT_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.setExperiment(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(SAMPLE) < input.length && !input[headerIndexMap.get(SAMPLE)].equals(""))
        {
            String name = input[headerIndexMap.get(SAMPLE)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Biomaterial biomaterial = apiFacade.getBiomaterial(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (biomaterial != null)
            {
                ref = biomaterial.getReference();
            }
            else
            {
                ref.setId(name);
            }
            criteria.getBiomaterials().add(ref);
        }
        if (headerIndexMap.get(SAMPLE_REF) < input.length && !input[headerIndexMap.get(SAMPLE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(SAMPLE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getBiomaterials().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
        {
            String name = input[headerIndexMap.get(NAME)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            criteria.getNames().add(name);
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
        return "Hybridization Criteria";
    }

}
