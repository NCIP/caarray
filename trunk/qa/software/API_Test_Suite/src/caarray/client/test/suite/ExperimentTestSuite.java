//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;

import java.io.File;
import java.util.Iterator;
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
    private static final String ARRAY_DESIGN_REF = "Array Design Ref";
    private static final String FACTOR_REF = "Factor Ref";
    private static final String ASSAY_TYPE = "Assay Type";
    private static final String MATCH_MODE = "Match Mode";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE, ENUMERATE,
            API, TITLE, ORG_SCI_NAME, ARRAY_PROVIDER, ARRAY_DESIGN_REF,ASSAY_TYPE,FACTOR_REF,EXPECTED_RESULTS, MIN_RESULTS, PAGES, API_UTILS_SEARCH, MATCH_MODE, MAX_RESULTS };


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
        if (experimentSearch.getResultsPerLimitOffset() != null)
        {
            boolean passed = true;
            for (Iterator<Integer> iter = experimentSearch.getResultsReturnedInPage().iterator(); iter.hasNext();)
            {
                int size = iter.next();
                // If it's not the last page, and the expected number of results were not returned, fail
                if (iter.hasNext() && size != experimentSearch.getResultsPerLimitOffset())
                {
                    String errorMessage = "Failed with unexpected page size: "
                        + size;
                    setTestResultFailure(testResult, experimentSearch, errorMessage);  
                    passed = false;
                }
            }
            if (passed)
            {
                String detail = "Found expected page size: " + experimentSearch.getResultsPerLimitOffset();
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
                log.error("Exception encountered:",t);
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
        if (headerIndexMap.get(PAGES) < input.length
                && !input[headerIndexMap.get(PAGES)].equals(""))
            search.setResultsPerLimitOffset(Integer.parseInt(input[headerIndexMap.get(PAGES)]
                    .trim()));
        if (headerIndexMap.get(API_UTILS_SEARCH) < input.length
                && !input[headerIndexMap.get(API_UTILS_SEARCH)].equals(""))
            search.setApiUtil(Boolean.parseBoolean(input[headerIndexMap.get(API_UTILS_SEARCH)].trim()));
        
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
                log.error("Exception encountered:",t);
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
                log.error("Exception encountered:",t);
                }
            
        }
        if (headerIndexMap.get(ARRAY_DESIGN_REF) < input.length && !input[headerIndexMap.get(ARRAY_DESIGN_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(ARRAY_DESIGN_REF)].trim();
            if (ref.startsWith(VAR_START))
            {
                ref = getVariableValue(ref);
            }
            ArrayDesign ad = new ArrayDesign();
            ad.setId(ref);
            example.getArrayDesigns().add(ad);         
        }
        if (headerIndexMap.get(FACTOR_REF) < input.length && !input[headerIndexMap.get(FACTOR_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(FACTOR_REF)].trim();
            if (ref.startsWith(VAR_START))
            {
                ref = getVariableValue(ref);
            }
            Factor factor = new Factor();
            factor.setId(ref);
            example.getFactors().add(factor);         
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
                log.error("Exception encountered:",t);
            }
            
        }
        
        if (headerIndexMap.get(MATCH_MODE) < input.length && !input[headerIndexMap.get(MATCH_MODE)].equals(""))
        {
            String mm = input[headerIndexMap.get(MATCH_MODE)];
            search.setMatchMode(Enum.valueOf(MatchMode.class, mm));
        }

        search.setExperiment(example);

        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer.parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        if (headerIndexMap.get(MAX_RESULTS) < input.length
                && !input[headerIndexMap.get(MAX_RESULTS)].equals(""))
            search.setStopResults(Integer.parseInt(input[headerIndexMap.get(MAX_RESULTS)].trim()));
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
