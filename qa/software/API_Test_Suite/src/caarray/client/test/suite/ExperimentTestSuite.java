/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.ExperimentSearch;

/**
 * @author vaughng
 * Jul 9, 2009
 */
public class ExperimentTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "Experiment.csv";

    private static final String TITLE = "Title";
    private static final String ORG_SCI_NAME = "Organism Scientific Name";
    private static final String ARRAY_PROVIDER = "Array Provider";
    private static final String ASSAY_TYPE = "Assay Type";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE, ENUMERATE,
            API, TITLE, ORG_SCI_NAME, ARRAY_PROVIDER, ASSAY_TYPE,EXPECTED_RESULTS, MIN_RESULTS };


    /**
     * @param apiFacade
     */
    public ExperimentTestSuite(ApiFacade apiFacade)
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
        ExperimentSearch experimentSearch = (ExperimentSearch)search;
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
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ExperimentSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        ExperimentSearch search = (ExperimentSearch)exampleSearch;
        Experiment example = search.getExperiment();
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            try
            {
                AssayType type = apiFacade.getAssayType(search.getApi(), input[headerIndexMap.get(ASSAY_TYPE)].trim());
                example.getAssayTypes().add(type);
            }
            catch (Throwable t)
            {
                System.out.println("AssayType for Experiement test case: " + search.getTestCase() + " could not be found.");
            }
            
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        ExperimentSearch search = (ExperimentSearch)exampleSearch;
        Experiment example = new Experiment();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
    
        if (headerIndexMap.get(TITLE) < input.length && !input[headerIndexMap.get(TITLE)].equals(""))
            example.setTitle(input[headerIndexMap.get(TITLE)].trim());
        
        if (headerIndexMap.get(ORG_SCI_NAME) < input.length && !input[headerIndexMap.get(ORG_SCI_NAME)].equals(""))
        {
            try
            {
                Organism organism = apiFacade.getOrganism(search.getApi(), input[headerIndexMap.get(ORG_SCI_NAME)].trim(), null);
                example.setOrganism(organism);
            }
            catch (Throwable t)
            {
                System.out.println("Organism for Experiement test case: " + search.getTestCase() + " could not be found.");
            }
            
        }
        if (headerIndexMap.get(ARRAY_PROVIDER) < input.length && !input[headerIndexMap.get(ARRAY_PROVIDER)].equals(""))
        {
            try
            {
                ArrayProvider arrayProvider = apiFacade.getArrayProvider(search.getApi(), input[headerIndexMap.get(ARRAY_PROVIDER)].trim());
                example.setArrayProvider(arrayProvider);
            }
            catch (Throwable t)
            {
                System.out.println("ArrayProvider for Experiement test case: " + search.getTestCase() + " could not be found.");
            }
            
        }
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            try
            {
                AssayType type = apiFacade.getAssayType(search.getApi(), input[headerIndexMap.get(ASSAY_TYPE)].trim());
                example.getAssayTypes().add(type);
            }
            catch (Throwable t)
            {
                System.out.println("AssayType for Experiement test case: " + search.getTestCase() + " could not be found.");
            }
            
        }
        
        search.setExperiment(example);
        
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer.parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        if (headerIndexMap.get(ENUMERATE) < input.length
                && !input[headerIndexMap.get(ENUMERATE)].equals(""))
            search.setEnumerate(Boolean.parseBoolean(input[headerIndexMap
                    .get(ENUMERATE)].trim()));
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
        return "Experiment";
    }

}
