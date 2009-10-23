/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.FileCriteriaSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class FileCriteriaTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + java.io.File.separator + "FileCriteria.csv";
    
    private static final String EXPERIMENT_ID = "Experiment Id";
    private static final String EXPERIMENT_TITLE = "Experiment Name";
    private static final String EXPERIMENT_REF = "Experiment Reference";
    private static final String EXTENSION = "Extension";
    private static final String TYPE_REF = "Type Reference";
    private static final String CATEGORY = "Category Name";
    private static final String SAMPLE = "Sample";
    private static final String SAMPLE_REF = "Sample Reference";
    private static final String HYB = "Hybridization";
    private static final String HYB_REF = "Hybridization Reference";
    private static final String SOURCE = "Source";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,ENUMERATE,
            API, EXPERIMENT_ID, EXPECTED_RESULTS, MIN_RESULTS,EXPERIMENT_TITLE, EXPERIMENT_REF,
            EXTENSION, TYPE_REF, CATEGORY, SAMPLE, SAMPLE_REF, HYB, HYB_REF, SOURCE,API_UTILS_SEARCH };

    /**
     * @param apiFacade
     */
    public FileCriteriaTestSuite(ApiFacade apiFacade)
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
        FileCriteriaSearch fileSearch = (FileCriteriaSearch)search;
        List<File> fileResults = (List<File>)resultsList;
        int namedResults = 0;
        for (File file : fileResults)
        {
            if (file != null && file.getMetadata().getName() != null)
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
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(CriteriaSearch search, TestResult testResult)
            throws Exception
    {
        FileCriteriaSearch criteriaSearch = (FileCriteriaSearch)search;
        List<File> resultsList = new ArrayList<File>();
        try
        {
            if (search.isApiUtilsSearch())
            {
                resultsList.addAll(apiFacade.filesByCriteriaSearchUtils(search.getApi(), criteriaSearch.getFileSearchCriteria()));
            }
            else if (search.isEnumerate())
            {
                resultsList.addAll(apiFacade.enumerateFiles(search.getApi(), criteriaSearch.getFileSearchCriteria()));
            }
            else
            {
                SearchResult<File> results = (SearchResult<File>) apiFacade
                        .searchForFiles(criteriaSearch.getApi(),
                                criteriaSearch.getFileSearchCriteria(), null);
                resultsList.addAll(results.getResults());
                while (!results.isFullResult())
                {
                    LimitOffset offset = new LimitOffset(results
                            .getMaxAllowedResults(), results.getResults()
                            .size()
                            + results.getFirstResultOffset());
                    results = (SearchResult<File>) apiFacade
                            .searchForFiles(criteriaSearch.getApi(),
                                    criteriaSearch.getFileSearchCriteria(), offset);
                    resultsList.addAll(results.getResults());
                }
            }
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered executing search: " + e.getMessage());
            testResult.addDetail("Exception encountered executing search: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error(e);
        } 
        
        
        
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new FileCriteriaSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        FileCriteriaSearch search = (FileCriteriaSearch)criteriaSearch;
        FileSearchCriteria criteria = search.getFileSearchCriteria();
        if (headerIndexMap.get(TYPE_REF) < input.length && !input[headerIndexMap.get(TYPE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(TYPE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getTypes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(CATEGORY) < input.length && !input[headerIndexMap.get(CATEGORY)].equals(""))
        {
            String name = input[headerIndexMap.get(CATEGORY)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            criteria.getCategories().add(FileCategory.valueOf(name.toUpperCase()));
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
            criteria.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(SAMPLE_REF) < input.length && !input[headerIndexMap.get(SAMPLE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(SAMPLE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getExperimentGraphNodes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (hyb != null)
            {
                ref = hyb.getReference();
            }
            else
            {
                ref.setId(name);
            }
            criteria.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(HYB_REF) < input.length && !input[headerIndexMap.get(HYB_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(HYB_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getExperimentGraphNodes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(SOURCE) < input.length && !input[headerIndexMap.get(SOURCE)].equals(""))
        {
            String name = input[headerIndexMap.get(SOURCE)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Biomaterial bio = apiFacade.getBiomaterial(search.getApi(), name);
            if (bio != null)
                criteria.getExperimentGraphNodes().add(bio.getReference());
        }

    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        FileCriteriaSearch search = (FileCriteriaSearch)criteriaSearch;
        FileSearchCriteria criteria = new FileSearchCriteria();
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
        
        if (headerIndexMap.get(EXTENSION) < input.length && !input[headerIndexMap.get(EXTENSION)].equals(""))
        {
            String extension = input[headerIndexMap.get(EXTENSION)].trim();
            if (extension.startsWith(VAR_START))
                extension = getVariableValue(extension);
                    
            criteria.setExtension(extension);
        }
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
        
        if (headerIndexMap.get(TYPE_REF) < input.length && !input[headerIndexMap.get(TYPE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(TYPE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getTypes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(CATEGORY) < input.length && !input[headerIndexMap.get(CATEGORY)].equals(""))
        {
            String name = input[headerIndexMap.get(CATEGORY)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            criteria.getCategories().add(FileCategory.valueOf(name.toUpperCase()));
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
            criteria.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(SAMPLE_REF) < input.length && !input[headerIndexMap.get(SAMPLE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(SAMPLE_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getExperimentGraphNodes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
            CaArrayEntityReference ref = new CaArrayEntityReference();
            if (hyb != null)
            {
                ref = hyb.getReference();
            }
            else
            {
                ref.setId(name);
            }
            criteria.getExperimentGraphNodes().add(ref);
        }
        if (headerIndexMap.get(HYB_REF) < input.length && !input[headerIndexMap.get(HYB_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(HYB_REF)];
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            criteria.getExperimentGraphNodes().add(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(SOURCE) < input.length && !input[headerIndexMap.get(SOURCE)].equals(""))
        {
            String name = input[headerIndexMap.get(SOURCE)];
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            Biomaterial bio = apiFacade.getBiomaterial(search.getApi(), name);
            if (bio != null)
                criteria.getExperimentGraphNodes().add(bio.getReference());
        }
        
        search.setFileSearchCriteria(criteria);    
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
        return "File Criteria";
    }

}
