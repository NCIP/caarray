/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.OrganismSearch;

/**
 * Encapsulates a collection of Organism search-by-example tests.
 * 
 * @author vaughng
 * Jul 1, 2009
 */
public class OrganismTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "Organism.csv";

    private static final String COMMON_NAME = "Common Name";
    private static final String EXPECTED_COMMON_NAME = "Expected Common Name";
    private static final String SCIENTIFIC_NAME = "Scientific Name";
    private static final String EXPECTED_SCIENTIFIC_NAME = "Expected Scientific Name";
    

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, ID, COMMON_NAME, MIN_RESULTS,EXPECTED_COMMON_NAME, SCIENTIFIC_NAME, EXPECTED_SCIENTIFIC_NAME, EXPECTED_RESULTS };

    /**
     * @param apiFacade
     */
    public OrganismTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * caarray.client.test.suite.SearchByExampleTestSuite#evaluateResults(java
     * .util.List, caarray.client.test.search.ExampleSearch,
     * caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        OrganismSearch organismSearch = (OrganismSearch)search;
        List<Organism> organismResults = (List<Organism>)resultsList;
        int namedResults = 0;
        for (Organism organism : organismResults)
        {
            if (organism.getScientificName() != null)
                namedResults++;
        }
        if (organismSearch.getExpectedResults() != null)
        {
            
            if (namedResults != organismSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + organismSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, organismSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (organismSearch.getMinResults() != null)
        {
            
            if (namedResults < organismSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + organismSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, organismSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (!organismSearch.getExpectedCommonNames().isEmpty())
        {
            
            for (String expectedName : organismSearch.getExpectedCommonNames())
            {
                boolean foundName = false;
                for (Organism organism : organismResults)
                {
                    if (organism.getCommonName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected common name: " + expectedName;
                    setTestResultFailure(testResult, organismSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected common name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
        if (!organismSearch.getExpectedScientificNames().isEmpty())
        {
            
            for (String expectedName : organismSearch.getExpectedScientificNames())
            {
                boolean foundName = false;
                for (Organism organism : organismResults)
                {
                    if (organism.getScientificName().equalsIgnoreCase(expectedName))
                    {
                        foundName = true;
                        break;
                    }
                }
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected scientific name: " + expectedName;
                    setTestResultFailure(testResult, organismSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected scientific name: " + expectedName;
                    testResult.addDetail(detail);
                }
            }         
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * caarray.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new OrganismSearch();
    }

    /*
     * (non-Javadoc)
     * 
     * @seecaarray.client.test.suite.SearchByExampleTestSuite#
     * populateAdditionalSearchValues(java.lang.String[],
     * caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        OrganismSearch search = (OrganismSearch)exampleSearch;
        
        if (headerIndexMap.get(EXPECTED_COMMON_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_COMMON_NAME)].equals(""))
            search.addExpectedCommonName(input[headerIndexMap.get(EXPECTED_COMMON_NAME)]
                                               .trim());
        
            
        if (headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME)].equals(""))
            search.addExpectedScientificName(input[headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME)]
                    .trim());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java
     * .lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        OrganismSearch search = (OrganismSearch) exampleSearch;
        Organism example = new Organism();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(COMMON_NAME) < input.length
                && !input[headerIndexMap.get(COMMON_NAME)].equals(""))
            example.setCommonName(input[headerIndexMap.get(COMMON_NAME)].trim());
        if (headerIndexMap.get(SCIENTIFIC_NAME) < input.length
                && !input[headerIndexMap.get(SCIENTIFIC_NAME)].equals(""))
            example.setScientificName(input[headerIndexMap.get(SCIENTIFIC_NAME)].trim());
        if (headerIndexMap.get(ID) < input.length
                && !input[headerIndexMap.get(ID)].equals(""))
        {
            String id = input[headerIndexMap.get(ID)].trim();
            if (id.startsWith(VAR_START))
            {
                id = getVariableValue(id);
            }
            example.setId(id);
        }
            

        search.setOrganism(example);
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
        if (headerIndexMap.get(EXPECTED_COMMON_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_COMMON_NAME)].equals(""))
            search.addExpectedCommonName(input[headerIndexMap.get(EXPECTED_COMMON_NAME)]
                    .trim());
        if (headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME) < input.length
                && !input[headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME)].equals(""))
            search.addExpectedScientificName(input[headerIndexMap.get(EXPECTED_SCIENTIFIC_NAME)]
                    .trim());
    }

    /*
     * (non-Javadoc)
     * 
     * @see caarray.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see caarray.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see caarray.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "Organism";
    }

}
