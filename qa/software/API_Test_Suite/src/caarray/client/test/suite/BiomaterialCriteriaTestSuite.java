//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.BiomaterialCriteriaSearch;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 9, 2009
 */
public class BiomaterialCriteriaTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "BiomaterialCriteria.csv";

    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String EXPERIMENT_TITLE = "Experiment Title";
    private static final String EXPERIMENT_REF = "Experiment Ref";
    private static final String EXTERNAL_ID = "External Id";
    private static final String ANNO_CATEGORY = "Annotation Category";
    private static final String ANNO_VALUE = "Annotation Value";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,ENUMERATE,
            API, NAME, EXPECTED_RESULTS, MIN_RESULTS, TYPE, EXPERIMENT_TITLE, EXPERIMENT_REF,
            EXTERNAL_ID,ANNO_CATEGORY, ANNO_VALUE, API_UTILS_SEARCH};

    /**
     * @param apiFacade
     */
    public BiomaterialCriteriaTestSuite(ApiFacade apiFacade)
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
        BiomaterialCriteriaSearch bioSearch = (BiomaterialCriteriaSearch)search;
        List<Biomaterial> bioResults = (List<Biomaterial>)resultsList;
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
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(
            CriteriaSearch search, TestResult testResult) throws Exception
    {
        BiomaterialCriteriaSearch criteriaSearch = (BiomaterialCriteriaSearch)search;
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.biomaterialsByCriteriaSearchUtils(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateBiomaterials(search.getApi(), criteriaSearch.getSearchCriteria()));
            }
            else
            {
                SearchResult<Biomaterial> results = (SearchResult<Biomaterial>) apiFacade
                        .searchForBiomaterials(criteriaSearch.getApi(),
                                criteriaSearch.getSearchCriteria(), null);
                resultsList.addAll(results.getResults());
                while (!results.isFullResult())
                {
                    LimitOffset offset = new LimitOffset(results
                            .getMaxAllowedResults(), results.getResults()
                            .size()
                            + results.getFirstResultOffset());
                    results = (SearchResult<Biomaterial>) apiFacade
                            .searchForBiomaterials(criteriaSearch.getApi(),
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
        return new BiomaterialCriteriaSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        BiomaterialCriteriaSearch search = (BiomaterialCriteriaSearch)criteriaSearch;
        BiomaterialSearchCriteria criteria = search.getSearchCriteria();
        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
        {
            String name = input[headerIndexMap.get(NAME)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
                    
            criteria.getNames().add(name);
        }
        if (headerIndexMap.get(EXTERNAL_ID) < input.length && !input[headerIndexMap.get(EXTERNAL_ID)].equals(""))
        {
            String id = input[headerIndexMap.get(EXTERNAL_ID)].trim();
            if (id.startsWith(VAR_START))
                id = getVariableValue(id);
                    
            criteria.getExternalIds().add(id);
        }   
        if (headerIndexMap.get(TYPE) < input.length && !input[headerIndexMap.get(TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(TYPE)].toUpperCase().trim();
            BiomaterialType type = BiomaterialType.valueOf(name);
            criteria.getTypes().add(type);
        }
        addAnnotationCriterion(search, criteria, input);

    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        BiomaterialCriteriaSearch search = (BiomaterialCriteriaSearch)criteriaSearch;
        BiomaterialSearchCriteria criteria = new BiomaterialSearchCriteria();
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
        
        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
        {
            String name = input[headerIndexMap.get(NAME)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
                    
            criteria.getNames().add(name);
        }
        if (headerIndexMap.get(EXTERNAL_ID) < input.length && !input[headerIndexMap.get(EXTERNAL_ID)].equals(""))
        {
            String id = input[headerIndexMap.get(EXTERNAL_ID)].trim();
            if (id.startsWith(VAR_START))
                id = getVariableValue(id);
                    
            criteria.getExternalIds().add(id);
        }   
        if (headerIndexMap.get(TYPE) < input.length && !input[headerIndexMap.get(TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(TYPE)].toUpperCase().trim();
            BiomaterialType type = BiomaterialType.valueOf(name);
            criteria.getTypes().add(type);
        }
        
        addAnnotationCriterion(search, criteria, input);
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
        if (headerIndexMap.get(EXPERIMENT_REF) < input.length && !input[headerIndexMap.get(EXPERIMENT_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(EXPERIMENT_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.setExperiment(new CaArrayEntityReference(ref));
        }
        
        search.setSearchCriteria(criteria);    
    }
    
    private void addAnnotationCriterion(BiomaterialCriteriaSearch search, BiomaterialSearchCriteria criteria, String[] input) throws Exception
    {
        AnnotationCriterion annoCriterion = null;
        if (headerIndexMap.get(ANNO_CATEGORY) < input.length && !input[headerIndexMap.get(ANNO_CATEGORY)].equals(""))
        {
            annoCriterion = new AnnotationCriterion();
            String name = input[headerIndexMap.get(ANNO_CATEGORY)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            CaArrayEntityReference cat = apiFacade.getCategoryReference(search.getApi(), name);
            if (cat != null)
            {
                annoCriterion.setCategory(cat);
            }
            else
            {
                annoCriterion.setCategory(new CaArrayEntityReference(name));
            }
            
        }
        if (headerIndexMap.get(ANNO_VALUE) < input.length && !input[headerIndexMap.get(ANNO_VALUE)].equals(""))
        {
            if (annoCriterion == null)
                annoCriterion = new AnnotationCriterion();
            
            annoCriterion.setValue(input[headerIndexMap.get(ANNO_VALUE)]);
            
        }
        if (annoCriterion != null)
        {
            criteria.getAnnotationCriterions().add(annoCriterion);
        }
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
        return "Biomaterial by Criteria";
    }

}
