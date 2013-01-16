//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ArrayDesignSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of ArrayDesign search-by-example tests.
 * 
 * @author vaughng
 * Jun 27, 2009
 */
public class ArrayDesignTestSuite extends SearchByExampleTestSuite 

{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "ArrayDesign.csv";

    private static final String NAME = "Name";
    private static final String LSID = "LSID";
    private static final String PROVIDER = "Associated Provider";
    private static final String ASSAY_TYPE = "Assay Type";
    private static final String EXPECTED_PROVIDER = "Expected Provider";
    private static final String EXPECTED_ORGANISM = "Expected Organism";
    private static final String NULL = "NULL";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, LSID, PROVIDER, ASSAY_TYPE, EXPECTED_PROVIDER, MIN_RESULTS, EXPECTED_RESULTS,
            EXPECTED_ORGANISM, NULL };
    

    /**
     * @param gridClient
     * @param javaSearchService
     */
    public ArrayDesignTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        ArrayDesignSearch search = (ArrayDesignSearch)exampleSearch;
        ArrayDesign example = new ArrayDesign();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(LSID) < input.length && !input[headerIndexMap.get(LSID)].equals(""))
            example.setLsid(input[headerIndexMap.get(LSID)].trim());
        
        if (headerIndexMap.get(PROVIDER) < input.length && !input[headerIndexMap.get(PROVIDER)].equals(""))
        {
            /*ArrayProvider provider = new ArrayProvider();
            provider.setName(input[headerIndexMap.get(PROVIDER)]);
            example.setArrayProvider(provider);*/
            ArrayProvider provider = null;
            try
            {
                provider = apiFacade.getArrayProvider(search.getApi(), input[headerIndexMap.get(PROVIDER)]);   
            }
            catch (Exception e)
            {
                log.error("Error retrieving ArrayProvider for ArrayDesign search", e);
                provider = new ArrayProvider();
                provider.setName(input[headerIndexMap.get(PROVIDER)]);
            }
            
            if (provider != null)
            {
                example.setArrayProvider(provider);
            }
            
        }
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            example.getAssayTypes().add(new AssayType(input[headerIndexMap.get(ASSAY_TYPE)]));
        }
        if (headerIndexMap.get(NULL) < input.length && !input[headerIndexMap.get(NULL)].equals(""))
        {
            example = null;
        }
        search.setArrayDesign(example);
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
        if (headerIndexMap.get(EXPECTED_PROVIDER) < input.length
                && !input[headerIndexMap.get(EXPECTED_PROVIDER)].equals(""))
            search.setExpectedProvider(input[headerIndexMap.get(EXPECTED_PROVIDER)].trim());
        if (headerIndexMap.get(EXPECTED_ORGANISM) < input.length
                && !input[headerIndexMap.get(EXPECTED_ORGANISM)].equals(""))
            search.setExpectedOrganism(input[headerIndexMap.get(EXPECTED_ORGANISM)]);
    }
    
    
    
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        ArrayDesignSearch search = (ArrayDesignSearch)exampleSearch;
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            search.getArrayDesign().getAssayTypes().add(new AssayType(input[headerIndexMap.get(ASSAY_TYPE)]));
        }
    }

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String getType()
    {
        return "ArrayDesign";
    }

    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        ArrayDesignSearch adSearch = (ArrayDesignSearch)search;
        List<ArrayDesign> adResults = (List<ArrayDesign>)resultsList;
        if (adSearch.getExpectedResults() != null || adSearch.getMinResults() != null)
        {
            int namedResults = 0;
            for (ArrayDesign arrayDesign : adResults)
            {
                if (arrayDesign.getName() != null)
                    namedResults++;
            }
            if (adSearch.getExpectedResults() != null && namedResults != adSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, ArrayDesign:"
                        + adSearch.getArrayDesign().getName()
                        + "- expected number of results: "
                        + adSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
            if (adSearch.getMinResults() != null && namedResults < adSearch.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected minimum number of results, ArrayDesign:"
                        + adSearch.getArrayDesign().getName()
                        + "- expected number of results: "
                        + adSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (adSearch.getExample() == null)
        {
            if (adSearch.getExceptionClass() != null)
            {
                if (!adSearch.getExceptionClass().equals(InvalidInputException.class.toString())
                        && !adSearch.getExceptionClass().equals(InvalidInputFault.class.toString()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected exception class: " + adSearch.getExceptionClass();
                    testResult.addDetail(detail);
                }
                else
                {
                    testResult.addDetail("Found expected exception " + adSearch.getExceptionClass());
                }
            }
        }
        for (ArrayDesign arrayDesign : adResults)
        {
            if (adSearch.getExpectedProvider() != null)
            {
                if (arrayDesign.getArrayProvider() == null
                        || !arrayDesign.getArrayProvider().getName().equals(
                                adSearch.getExpectedProvider()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected array provider: "
                            + (arrayDesign.getArrayProvider() == null ? "null"
                                    : arrayDesign.getArrayProvider().getName())
                            + ", expected provider: "
                            + adSearch.getExpectedProvider();
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected provider: "
                            + adSearch.getExpectedProvider();
                    testResult.addDetail(detail);
                }
            }
            if (adSearch.getExpectedOrganism() != null)
            {
                if (arrayDesign.getOrganism() == null || 
                        !arrayDesign.getOrganism().getCommonName().equalsIgnoreCase(adSearch.getExpectedOrganism()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected organism common name: " +
                        (arrayDesign.getOrganism() == null ? "null" : arrayDesign.getOrganism().getCommonName()) +
                        ", expected name: " + adSearch.getExpectedOrganism();
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected organism: " + adSearch.getExpectedOrganism();
                    testResult.addDetail(detail);
                }
            }
        }
        
    }

    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ArrayDesignSearch();
    }
    
    

}
