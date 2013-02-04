//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.ExperimentCriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 2, 2009
 */
public class ExperimentCriteriaTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "ExperimentCriteria.csv";

    private static final String TITLE = "Title";
    private static final String PUBLIC_ID = "Public Identifier";
    private static final String ARRAY_PROVIDER = "Array Provider";
    private static final String ORG_SCI_NAME = "Organism Scientific Name";
    private static final String ORG_COMMON_NAME = "Organism Common Name";
    private static final String ASSAY_TYPE = "Assay Type Name";
    private static final String ANNO_CATEGORY = "Annotation Category";
    private static final String ANNO_VALUE = "Annotation Value";
    private static final String PI_REF = "Principal Investigator Ref";
    private static final String EXPECTED_TITLE = "Expected Title";
    private static final String EXPECTED_ASSAY_TYPE = "Expected Assay Type";
    private static final String EXPECTED_PROVIDER = "Expected Provider";
    private static final String EXPECTED_ORG_SCI_NAME = "Expected Organism Scientific Name";
    

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, TITLE, EXPECTED_TITLE, EXPECTED_RESULTS, MIN_RESULTS, PUBLIC_ID, ARRAY_PROVIDER, ORG_COMMON_NAME, ORG_SCI_NAME,
            ASSAY_TYPE, ANNO_CATEGORY, ANNO_VALUE, PI_REF,EXPECTED_ASSAY_TYPE, EXPECTED_PROVIDER, EXPECTED_ORG_SCI_NAME, API_UTILS_SEARCH,
            ENUMERATE, LOGIN};

    /**
     * @param apiFacade
     */
    public ExperimentCriteriaTestSuite(ApiFacade apiFacade)
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
        ExperimentCriteriaSearch experimentSearch = (ExperimentCriteriaSearch)search;
        List<Experiment> experimentResults = (List<Experiment>)resultsList;
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
        }
        if (!experimentSearch.getExpectedTitle().isEmpty())
        {
            
            for (String expectedName : experimentSearch.getExpectedTitle())
            {
                boolean foundName = false;
                for (Experiment experiment : experimentResults)
                {
                    if (experiment != null && experiment.getTitle().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected title: " + expectedName;
                    setTestResultFailure(testResult, experimentSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected title: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!experimentSearch.getExpectedAssayType().isEmpty())
        {
            
            for (String expectedAssay : experimentSearch.getExpectedAssayType())
            {
                boolean foundName = false;
                for (Experiment experiment : experimentResults)
                {
                    if (experiment != null)
                    {
                        for (AssayType assay : experiment.getAssayTypes())
                        {
                            if (assay != null && assay.getName().equalsIgnoreCase(expectedAssay))
                            {
                                foundName = true;
                                break;
                            }
                        }
                    }
                    
                    
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected assay: " + expectedAssay;
                    setTestResultFailure(testResult, experimentSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected assay: " + expectedAssay;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!experimentSearch.getExpectedProvider().isEmpty())
        {
            
            for (String expectedProvider : experimentSearch.getExpectedProvider())
            {
                boolean foundName = false;
                for (Experiment experiment : experimentResults)
                {
                    if (experiment != null && experiment.getArrayProvider() != null && experiment.getArrayProvider().getName().equalsIgnoreCase(expectedProvider))
                    {
                        foundName = true;
                        break;
                    }

                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected provider: " + expectedProvider;
                    setTestResultFailure(testResult, experimentSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected provider: " + expectedProvider;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!experimentSearch.getExpectedOrganismScientificName().isEmpty())
        {
            
            for (String expectedName : experimentSearch.getExpectedOrganismScientificName())
            {
                boolean foundName = false;
                for (Experiment experiment : experimentResults)
                {
                    if (experiment != null && experiment.getOrganism() != null && experiment.getOrganism().getScientificName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected organism name: " + expectedName;
                    setTestResultFailure(testResult, experimentSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected organism name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new ExperimentCriteriaSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        ExperimentCriteriaSearch search = (ExperimentCriteriaSearch)criteriaSearch;
        addAnnotationCriterion(search, search.getExperimentSearchCriteria(), input);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch) throws Exception
    {
        ExperimentCriteriaSearch search = (ExperimentCriteriaSearch)criteriaSearch;
        ExperimentSearchCriteria criteria = new ExperimentSearchCriteria();
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
        if (headerIndexMap.get(LOGIN) < input.length
                && !input[headerIndexMap.get(LOGIN)].equals(""))
            search.setLogin(Boolean.parseBoolean(input[headerIndexMap
                    .get(LOGIN)].trim()));
        if (headerIndexMap.get(EXPECTED_TITLE) < input.length
                && !input[headerIndexMap.get(EXPECTED_TITLE)].equals(""))
            search.addExpectedTitle(input[headerIndexMap.get(EXPECTED_TITLE)].trim());
        if (headerIndexMap.get(EXPECTED_ASSAY_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_ASSAY_TYPE)].equals(""))
        {
            search.addExpectedAssayType(input[headerIndexMap.get(EXPECTED_ASSAY_TYPE)].trim());
        }
            
        if (headerIndexMap.get(EXPECTED_PROVIDER) < input.length
                && !input[headerIndexMap.get(EXPECTED_PROVIDER)].equals(""))
        {
            search.addExpectedProvider(input[headerIndexMap.get(EXPECTED_PROVIDER)].trim());
        }
            
        if (headerIndexMap.get(EXPECTED_ORG_SCI_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_ORG_SCI_NAME)].equals(""))
        {
            search.addExpectedOrganismScientificName(input[headerIndexMap.get(EXPECTED_ORG_SCI_NAME)].trim());
        }
            
        
        if (headerIndexMap.get(TITLE) < input.length && !input[headerIndexMap.get(TITLE)].equals(""))
        {
            String title = input[headerIndexMap.get(TITLE)].trim();
            if (title.startsWith(VAR_START))
                title = getVariableValue(title);
                    
            criteria.setTitle(title);
        }
            
        if (headerIndexMap.get(PUBLIC_ID) < input.length && !input[headerIndexMap.get(PUBLIC_ID)].equals(""))
        {
            String name = input[headerIndexMap.get(PUBLIC_ID)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            criteria.setPublicIdentifier(name);
        }
            
        if (headerIndexMap.get(ARRAY_PROVIDER) < input.length && !input[headerIndexMap.get(ARRAY_PROVIDER)].equals(""))
        {
            String name = input[headerIndexMap.get(ARRAY_PROVIDER)].trim();
            CaArrayEntityReference ref = null;
            if (name.startsWith(VAR_START))
            {
                name = getVariableValue(name);
                ref = new CaArrayEntityReference(name);
            }
            else
            {
                ArrayProvider provider = apiFacade.getArrayProvider(search.getApi(), name);
                if (provider != null)
                {
                    criteria.setArrayProvider(provider.getReference());
                }
                else
                {
                    ref = new CaArrayEntityReference(name);
                }
            }
            criteria.setArrayProvider(ref);
            
            
        }
        Organism organism = null;
        String scientificName = null;
        String commonName = null;
        if ((headerIndexMap.get(ORG_SCI_NAME) < input.length && !input[headerIndexMap.get(ORG_SCI_NAME)].equals("")))
        {
            scientificName = input[headerIndexMap.get(ORG_SCI_NAME)];
            
            if (scientificName.startsWith(VAR_START))
                scientificName = getVariableValue(scientificName);
                
        }
        if (headerIndexMap.get(ORG_COMMON_NAME) < input.length && !input[headerIndexMap.get(ORG_COMMON_NAME)].equals(""))
        {
            commonName = input[headerIndexMap.get(ORG_COMMON_NAME)];
            if (commonName.startsWith(VAR_START))
                commonName = getVariableValue(commonName);
        }
        if (scientificName != null || commonName != null)
        {      
            if (scientificName != null && scientificName.equals(""))
            {
                criteria.setOrganism(new CaArrayEntityReference(""));
            }
            else if (commonName != null && commonName.equals(""))
            {
                criteria.setOrganism(new CaArrayEntityReference(""));
            }
            else
            {
                organism = apiFacade.getOrganism(search.getApi(), scientificName, commonName);
                if (organism != null)
                {
                    criteria.setOrganism(organism.getReference());
                }
                else
                {
                    //likely a purposeful invalid reference, so this should work the same way
                    criteria.setOrganism(new CaArrayEntityReference(scientificName != null ? scientificName : commonName));
                }  
            }
                     
        }
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(ASSAY_TYPE)];
            CaArrayEntityReference ref = null;
            //Variables will indicate actual values for the ref (e.g. empty string) rather than search values)
            if (name.startsWith(VAR_START))
            {
                name = getVariableValue(name);
                ref = new CaArrayEntityReference(name);
            }
            else
            {
                AssayType assay = apiFacade.getAssayType(search.getApi(), name);
                if (assay != null)
                {
                    ref = assay.getReference();
                    criteria.setAssayType(assay.getReference());
                }
                else
                {
                    ref = new CaArrayEntityReference(name);
                }
            }
            criteria.setAssayType(ref);
        }
        addAnnotationCriterion(search, criteria, input);
        if (headerIndexMap.get(PI_REF) < input.length && !input[headerIndexMap.get(PI_REF)].equals(""))
        {
            String name = input[headerIndexMap.get(PI_REF)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            criteria.getPrincipalInvestigators().add(new CaArrayEntityReference(name));
            
        }
        
        search.setExperimentSearchCriteria(criteria);    
    }

    private void addAnnotationCriterion(ExperimentCriteriaSearch search, ExperimentSearchCriteria criteria, String[] input) throws Exception
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
        return "Experiment by Criteria";
    }

    @Override
    protected Object executeSearch(
            CriteriaSearch search, TestResult testResult) throws Exception
    {
        ExperimentCriteriaSearch criteriaSearch = (ExperimentCriteriaSearch)search;
        List<Experiment> resultsList = new ArrayList<Experiment>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.experimentsByCriteriaSearchUtils(search.getApi(), criteriaSearch.getExperimentSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateExperiments(search.getApi(), criteriaSearch.getExperimentSearchCriteria()));
            }
            else if (search.isLogin())
            {
                List<Experiment> noLoginResults = getSearchResults(criteriaSearch, false);
                resultsList.addAll(getSearchResults(criteriaSearch, true));
                if (resultsList.size() <= noLoginResults.size())
                {
                    String errorMessage = "Login search did not return more results than non-login search. Login: " + resultsList.size() +
                    ", no login: " + noLoginResults.size();
                    setTestResultFailure(testResult, criteriaSearch, errorMessage);
                }
                else
                {
                    String detail = "Login search produced more results than non-login search.Login: " + resultsList.size() +
                    ", no login: " + noLoginResults.size();
                    testResult.addDetail(detail);
                }
            }
            else
            {
                resultsList.addAll(getSearchResults(criteriaSearch, false));
            }
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered executing search: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            testResult.addDetail("Exception encountered executing search: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        
        
        
        return resultsList;
    }
    
    private List<Experiment> getSearchResults(ExperimentCriteriaSearch criteriaSearch, boolean login) throws Exception
    {
        List<Experiment> resultsList = new ArrayList<Experiment>();
        SearchResult<Experiment> results = (SearchResult<Experiment>) apiFacade
                .searchForExperiments(criteriaSearch.getApi(), criteriaSearch
                        .getExperimentSearchCriteria(), null, login);
        resultsList.addAll(results.getResults());
        while (!results.isFullResult())
        {
            LimitOffset offset = new LimitOffset(
                    results.getMaxAllowedResults(), results.getResults().size()
                            + results.getFirstResultOffset());
            results = (SearchResult<Experiment>) apiFacade
                    .searchForExperiments(criteriaSearch.getApi(),
                            criteriaSearch.getExperimentSearchCriteria(),
                            offset, login);
            resultsList.addAll(results.getResults());
        }
        return resultsList;
}

}
