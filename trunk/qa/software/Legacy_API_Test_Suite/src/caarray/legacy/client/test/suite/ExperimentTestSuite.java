/**
 * 
 */
package caarray.legacy.client.test.suite;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;

import java.io.File;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.search.ExampleSearch;
import caarray.legacy.client.test.search.ExperimentSearch;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class ExperimentTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "Experiment.csv";

    private static final String ORG_SCI_NAME = "Organism Scientific Name";
    private static final String ARRAY_PROVIDER = "Array Provider";
    private static final String LOGIN = "Login";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, ORG_SCI_NAME, ARRAY_PROVIDER, EXPECTED_RESULTS, MIN_RESULTS, LOGIN };


    /**
     * @param apiFacade
     */
    public ExperimentTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.legacy.client.test.search.ExampleSearch, caarray.legacy.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayObject> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        ExperimentSearch experimentSearch = (ExperimentSearch)search;
        List<Experiment> experimentResults = (List<Experiment>)resultsList;
        int namedResults = 0;
        for (Experiment experiment : experimentResults)
        {
            if (experiment.getTitle() != null)
                namedResults++;
        }
        if (experimentSearch.getExpectedResults() != null)
        {
            
            if (namedResults != experimentSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + experimentSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
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
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected minimum: "
                        + experimentSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
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
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ExperimentSearch();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        //N/A
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
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
        if (headerIndexMap.get(LOGIN) < input.length
                && !input[headerIndexMap.get(LOGIN)].equals(""))
            search.setLogin(Boolean.parseBoolean(input[headerIndexMap.get(LOGIN)]
                    .trim()));
        
        Organism organism = null;
        if (headerIndexMap.get(ORG_SCI_NAME) < input.length && !input[headerIndexMap.get(ORG_SCI_NAME)].equals(""))
        {
           organism = new Organism();
           organism.setScientificName(input[headerIndexMap.get(ORG_SCI_NAME)].trim());         
        }
        if (organism != null)
        {
            example.setOrganism(organism);
        }
        Organization organization = null;
        if (headerIndexMap.get(ARRAY_PROVIDER) < input.length && !input[headerIndexMap.get(ARRAY_PROVIDER)].equals(""))
        {
            organization = new Organization();
            organization.setName(input[headerIndexMap.get(ARRAY_PROVIDER)].trim());
            organization.setProvider(true);
            
        }
        if (organization != null)
        {
            example.setManufacturer(organization);
        }
        
        search.setExperiment(example);

        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer.parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "Experiment";
    }

}
