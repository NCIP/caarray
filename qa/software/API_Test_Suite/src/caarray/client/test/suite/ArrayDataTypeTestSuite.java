//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ArrayDataTypeSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of ArrayDataType search-by-example tests.
 * 
 * @author vaughng 
 * Jun 26, 2009
 */
public class ArrayDataTypeTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "ArrayDataType.csv";

    private static final String NAME = "Name";
    private static final String QUANT_TYPE = "Quantitation Type";
    private static final String EXPECTED_QUANT = "Expected Quantitations";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, QUANT_TYPE, EXPECTED_QUANT, EXPECTED_RESULTS };

    
    public ArrayDataTypeTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        ArrayDataTypeSearch search = (ArrayDataTypeSearch)exampleSearch;
        ArrayDataType example = new ArrayDataType();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            QuantitationType qType = new QuantitationType();
            qType.setName(input[headerIndexMap.get(QUANT_TYPE)].trim());
            example.getQuantitationTypes().add(qType);
        }
        search.setArrayDataType(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_QUANT) < input.length
                && !input[headerIndexMap.get(EXPECTED_QUANT)].equals(""))
            search.setExpectedQuantitations(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_QUANT)].trim()));
    }
    
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        ArrayDataTypeSearch search = (ArrayDataTypeSearch)exampleSearch;
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            QuantitationType qType = new QuantitationType();
            qType.setName(input[headerIndexMap.get(QUANT_TYPE)]);
            search.getArrayDataType().getQuantitationTypes().add(qType);
        }
    }

    
    protected void evaluateResults(List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        ArrayDataTypeSearch adtSearch = (ArrayDataTypeSearch)search;
        List<ArrayDataType> adtResults = (List<ArrayDataType>)resultsList;
        if (adtSearch.getExpectedResults() != null)
        {
            int namedResults = 0;
            for (ArrayDataType arrayDataType : adtResults)
            {
                if (arrayDataType.getName() != null)
                    namedResults++;
            }
            if (namedResults != adtSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + adtSearch.getExpectedResults()
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
        if (testResult.isPassed() && adtSearch.getExpectedQuantitations() != null)
        {

            Set<QuantitationType> qtypeSet = new HashSet<QuantitationType>();
            int namedQuantitations = 0;
            for (ArrayDataType arrayDataType : adtResults)
            {
                
                for (QuantitationType qType : arrayDataType
                        .getQuantitationTypes())
                {
                    if (qType.getName() != null)
                        qtypeSet.add(qType);
                        //namedQuantitations++;
                }
            }
                namedQuantitations = qtypeSet.size();
                if (namedQuantitations != adtSearch.getExpectedQuantitations())
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of named quantitations, expected: "
                            + adtSearch.getExpectedQuantitations()
                            + ", actual number: " + namedQuantitations;
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected quantitations: "
                            + namedQuantitations;
                    testResult.addDetail(detail);
                }
            
        }
    }

    @Override
    protected String getType()
    {
        return "ArrayDataType";
    }

    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }
    
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ArrayDataTypeSearch();
    }

}
