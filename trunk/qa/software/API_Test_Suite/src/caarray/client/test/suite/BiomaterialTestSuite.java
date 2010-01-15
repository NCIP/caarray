/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.BiomaterialSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * @author vaughng
 * Jul 7, 2009
 */
public class BiomaterialTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "Biomaterial.csv";

    private static final String NAME = "Name";
    private static final String TYPE = "Type";
    private static final String MATCH_MODE = "Match Mode";
    private static final String TERM = "Term";
    private static final String EXPECTED_TYPE = "Expected Type";
    private static final String EXPECTED_EXTERNAL_ID = "Expected External ID";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, TYPE, TERM, MATCH_MODE, PAGES, EXPECTED_RESULTS, MIN_RESULTS, EXPECTED_EXTERNAL_ID, EXPECTED_TYPE};
    
    
    /**
     * @param apiFacade
     */
    public BiomaterialTestSuite(ApiFacade apiFacade)
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
        BiomaterialSearch bioSearch = (BiomaterialSearch)search;
        List<Biomaterial> bioResults = (List<Biomaterial>)resultsList;
        int namedResults = 0;
        for (Biomaterial biomaterial : bioResults)
        {
            if (biomaterial.getName() != null)
                namedResults++;
        }
        if (bioSearch.getExpectedResults() != null)
        {
            
            if (namedResults != bioSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + bioSearch.getExpectedResults()
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
        if (bioSearch.getMinResults() != null)
        {
            
            if (namedResults < bioSearch.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected minimum: "
                        + bioSearch.getMinResults()
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
        if (!bioSearch.getExpectedType().isEmpty())
        {
            for (BiomaterialType type : bioSearch.getExpectedType())
            {
                boolean foundType = false;
                for (Biomaterial biomaterial : bioResults)
                {
                    if (biomaterial != null && biomaterial.getType() != null && biomaterial.getType().equals(type))
                    {
                        foundType = true;
                        break;
                    }
                }
                if (!foundType)
                {
                    testResult.setPassed(false);
                    String detail = "Didn't find expected biomaterial type: " + type.getName();
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected biomaterial type: " + type.getName();
                    testResult.addDetail(detail);
                }
            }
        }
        if (!bioSearch.getExpectedExternalId().isEmpty())
        {
            for (String id : bioSearch.getExpectedExternalId())
            {
                boolean foundId = false;
                for (Biomaterial biomaterial : bioResults)
                {
                    if (biomaterial != null && biomaterial.getExternalId() != null && biomaterial.getExternalId().equals(id))
                    {
                        foundId = true;
                        break;
                    }
                }
                if (!foundId)
                {
                    testResult.setPassed(false);
                    String detail = "Didn't find expected external ID: " + id;
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected external ID: " + id;
                    testResult.addDetail(detail);
                }
            }
        }
        if (bioSearch.getResultsPerLimitOffset() != null)
        {
            boolean passed = true;
            for (Iterator<Integer> iter = bioSearch.getResultsReturnedInPage().iterator(); iter.hasNext();)
            {
                int size = iter.next();
                if (iter.hasNext() && size != bioSearch.getResultsPerLimitOffset())
                {
                    String errorMessage = "Failed with unexpected page size: "
                        + size;
                    setTestResultFailure(testResult, bioSearch, errorMessage);  
                    passed = false;
                }
            }
            if (passed)
            {
                String detail = "Found expected page size: " + bioSearch.getResultsPerLimitOffset();
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
        return new BiomaterialSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        BiomaterialSearch search = (BiomaterialSearch)exampleSearch;
        if (headerIndexMap.get(EXPECTED_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_TYPE)].equals(""))
        {
            BiomaterialType type = BiomaterialType.valueOf(input[headerIndexMap.get(EXPECTED_TYPE)].trim());
            search.addExpectedType(type);
        }
        if (headerIndexMap.get(EXPECTED_EXTERNAL_ID) < input.length
                && !input[headerIndexMap.get(EXPECTED_EXTERNAL_ID)].equals(""))
        {
            search.addExpectedExternalId(input[headerIndexMap.get(EXPECTED_TYPE)].trim());
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        BiomaterialSearch search = (BiomaterialSearch)exampleSearch;
        Biomaterial example = new Biomaterial();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(TYPE) < input.length && !input[headerIndexMap.get(TYPE)].equals(""))
        {
            BiomaterialType type = BiomaterialType.valueOf(input[headerIndexMap.get(TYPE)].trim().toUpperCase());
            example.setType(type);
        }
        if (headerIndexMap.get(TERM) < input.length && !input[headerIndexMap.get(TERM)].equals(""))
        {
            String term = input[headerIndexMap.get(TERM)].trim();
            if (term.startsWith(VAR_START))
                term = getVariableValue(term);
            TermValue value = new TermValue();
            Term t = new Term();
            t.setValue(term);
            value.setTerm(t);
            example.setDiseaseState(value);
        }
        if (headerIndexMap.get(MATCH_MODE) < input.length && !input[headerIndexMap.get(MATCH_MODE)].equals(""))
        {
            MatchMode mode = MatchMode.valueOf(input[headerIndexMap.get(MATCH_MODE)].trim().toUpperCase());
            search.setMatchMode(mode);
        }
        search.setBiomaterial(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(PAGES) < input.length
                && !input[headerIndexMap.get(PAGES)].equals(""))
            search.setResultsPerLimitOffset(Integer.parseInt(input[headerIndexMap.get(PAGES)]
                    .trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer
                    .parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_TYPE)].equals(""))
        {
            BiomaterialType type = BiomaterialType.valueOf(input[headerIndexMap.get(EXPECTED_TYPE)].trim());
            search.addExpectedType(type);
        }
        if (headerIndexMap.get(EXPECTED_EXTERNAL_ID) < input.length
                && !input[headerIndexMap.get(EXPECTED_EXTERNAL_ID)].equals(""))
        {
            search.addExpectedExternalId(input[headerIndexMap.get(EXPECTED_EXTERNAL_ID)].trim());
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
        return "Biomaterial";
    }

}
