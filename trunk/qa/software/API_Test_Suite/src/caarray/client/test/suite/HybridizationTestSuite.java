//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ExampleSearch;
import caarray.client.test.search.HybridizationSearch;

/**
 * @author vaughng
 * Jul 10, 2009
 */
public class HybridizationTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "Hybridization.csv";

    private static final String NAME = "Name";
    private static final String ARRAY_DESIGN = "Array Design";
    
    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
        API, NAME, ARRAY_DESIGN,EXPECTED_RESULTS, MIN_RESULTS };
    
    /**
     * @param apiFacade
     */
    public HybridizationTestSuite(ApiFacade apiFacade)
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
        HybridizationSearch hybridizationSearch = (HybridizationSearch)search;
        List<Hybridization> hybridizationList = (List<Hybridization>)resultsList;
        int namedResults = 0;
        for (Hybridization hybridization : hybridizationList)
        {
            if (hybridization.getName() != null)
                namedResults++;
        }
        if (hybridizationSearch.getExpectedResults() != null)
        {           
            if (namedResults != hybridizationSearch.getExpectedResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected: "
                        + hybridizationSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, hybridizationSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (hybridizationSearch.getMinResults() != null)
        {
            if (namedResults < hybridizationSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of results, expected minimum: "
                        + hybridizationSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                setTestResultFailure(testResult, hybridizationSearch, errorMessage);
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
        return new HybridizationSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        // NOOP
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        HybridizationSearch search = (HybridizationSearch)exampleSearch;
        Hybridization example = new Hybridization();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
        {
            example.setName(input[headerIndexMap.get(NAME)].trim());
        }
        if (headerIndexMap.get(ARRAY_DESIGN) < input.length && !input[headerIndexMap.get(ARRAY_DESIGN)].equals(""))
        {
            ArrayDesign design = new ArrayDesign();
            design.setName(input[headerIndexMap.get(ARRAY_DESIGN)].trim());
            ExampleSearchCriteria<ArrayDesign> crit = new ExampleSearchCriteria<ArrayDesign>();
            try
            {
                SearchResult<ArrayDesign> result = apiFacade.searchByExample(search.getApi(), crit, null);
                if (result.getResults() != null)
                {
                    example.setArrayDesign(result.getResults().get(0));
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving array design: " + e.getClass());
                log.error("Exception encountered:",e);
            }
            
        }
            
        
        search.setHybridization(example);
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
            search.setMinResults(Integer.parseInt((input[headerIndexMap.get(MIN_RESULTS)].trim())));
        
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
        return "Hybridization";
    }

}
