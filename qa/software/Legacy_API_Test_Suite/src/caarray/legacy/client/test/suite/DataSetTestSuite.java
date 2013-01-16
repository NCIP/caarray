//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.suite;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;
import caarray.legacy.client.test.search.DataSetSearch;

/**
 * @author vaughng
 * Aug 14, 2009
 */
public class DataSetTestSuite extends ConfigurableTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + java.io.File.separator + "DataSet.csv";

    
    private static final String QUANT_TYPE = "Quantitation Type";
    private static final String HYB = "Hybridization";

    private static final String EXPECTED_PROBE = "Expected Probe Ids";
    private static final String EXPECTED_DATA_TYPE = "Expected Data Type";
    private static final String EXPECTED_DATA_RESULTS = "Expected Data Results";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API,  QUANT_TYPE, HYB, EXPECTED_PROBE, EXPECTED_DATA_TYPE,
            EXPECTED_DATA_RESULTS };
    
    private List<DataSetSearch> dataSearches = new ArrayList<DataSetSearch>();
    
    /**
     * @param apiFacade
     */
    public DataSetTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#constructSearches(java.util.List)
     */
    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        DataSetSearch search = null;
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = new DataSetSearch();
                boolean skip = false;
                if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
                {
                    search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                                                              .trim()));
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                }
                if (!skip)
                {
                    
                    populateSearch(input, search);  
                } 
                if (search != null)
                    dataSearches.add(search);
            }
            else
            {
                boolean skip = false;
                if (search == null)
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
                if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!skip)
                {
                    populateAdditionalValues(input,search);
                }
                
            }
    
            
    
            index++;
            if (index < spreadsheetRows.size())
            {
                row = spreadsheetRows.get(index);
            }
            else
            {
                row = null;
            }
        }
        filterSearches();
    }
    
    private void populateSearch(String[] input, DataSetSearch search)
    {
        DataRetrievalRequest request = new DataRetrievalRequest();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
       
        
        if (headerIndexMap.get(EXPECTED_PROBE) < input.length
                && !input[headerIndexMap.get(EXPECTED_PROBE)].equals(""))
            search.setExpectedProbeIds(Integer.parseInt(input[headerIndexMap.get(EXPECTED_PROBE)].trim()));
        
        if (headerIndexMap.get(EXPECTED_DATA_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_TYPE)].equals(""))
        {
            String type = input[headerIndexMap.get(EXPECTED_DATA_TYPE)];
            search.setExpectedDataType(DataType.valueOf(type));
        }
        if (headerIndexMap.get(EXPECTED_DATA_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_RESULTS)].equals(""))
        {
            String res = input[headerIndexMap.get(EXPECTED_DATA_RESULTS)];
            search.setExpectedDataResults(Integer.parseInt(res));
        }
        
            
        
            
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)].trim();
            
            try
            {
                Hybridization hyb = getHybridization(name, search.getApi());
                if (hyb != null)
                {
                    request.addHybridization(hyb);
                }
                else
                {
                    System.out.println("No Hyb found for: " + name);
                }
                
                
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving hyb:" + name);
            }
             
        }
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(QUANT_TYPE)].trim();
            try
            {
                QuantitationType quant = getQuantitation(name, search.getApi());
                if (quant != null)
                {
                    request.addQuantitationType(quant);
                }
                else
                {
                    System.out.println("No QuantitationType found for: " + name);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving QuantitationType:" + name);
            }
        }
         
        
        search.setRequest(request);    
    }
    
    private void populateAdditionalValues(String[] input, DataSetSearch search)
    {
        DataRetrievalRequest request = search.getRequest();
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)].trim();
            
            try
            {
                Hybridization hyb = getHybridization(name, search.getApi());
                if (hyb != null)
                {
                    request.addHybridization(hyb);
                }
                else
                {
                    System.out.println("No Hyb found for: " + name);
                }
                
                
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving hyb:" + name);
            }
             
        }
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(QUANT_TYPE)].trim();
            try
            {
                QuantitationType quant = getQuantitation(name, search.getApi());
                if (quant != null)
                {
                    request.addQuantitationType(quant);
                }
                else
                {
                    System.out.println("No QuantitationType found for: " + name);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception retrieving QuantitationType:" + name);
            }
        }
         
    }
    
    private Hybridization getHybridization(String name, String api) throws Exception
    {
        if (api.equalsIgnoreCase("java"))
        {
            Hybridization hyb = new Hybridization();
            hyb.setName(name);
            List<Hybridization> hybList = apiFacade.searchByExample(api, hyb, false);
            if (!hybList.isEmpty())
                return hybList.get(0);
            return null;
        }
        else
        {
            CQLQueryResults results = (CQLQueryResults)apiFacade.query(api, getHybridizationQuery(name));
            if (results.getObjectResult() != null && results.getObjectResult().length > 0)
            {
                Iterator iter = new CQLQueryResultsIterator(results, CaArraySvcClient.class
                        .getResourceAsStream("client-config.wsdd"));
                if (iter.hasNext()) {
                    Hybridization hyb = (Hybridization) (iter.next());
                    return hyb;
                }
                
                   
            }
        }
        return null;
    }
    
    private QuantitationType getQuantitation(String name, String api) throws Exception
    {
        if (api.equalsIgnoreCase("java"))
        {
            QuantitationType quant = new QuantitationType();
            quant.setName(name);
            List<QuantitationType> qList = apiFacade.searchByExample(api, quant, false);
            if (!qList.isEmpty())
                return qList.get(0);
            return null;
        }
        else
        {
            CQLQueryResults results = (CQLQueryResults)apiFacade.query(api, getQuantitationQuery(name));
            if (results.getObjectResult() != null && results.getObjectResult().length > 0)
            {
                Iterator iter = new CQLQueryResultsIterator(results, CaArraySvcClient.class
                        .getResourceAsStream("client-config.wsdd"));
                if (iter.hasNext()) {
                    QuantitationType hyb = (QuantitationType) (iter.next());
                    return hyb;
                }
                
                   
            }
        }
        return null;
    }
    
    private void filterSearches()
    {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL))
        {
            List<DataSetSearch> filteredSearches = new ArrayList<DataSetSearch>();
            for (DataSetSearch search : dataSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            dataSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<DataSetSearch> filteredSearches = new ArrayList<DataSetSearch>();
            for (DataSetSearch search : dataSearches)
            {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            dataSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<DataSetSearch> filteredSearches = new ArrayList<DataSetSearch>();
            for (DataSetSearch search : dataSearches)
            {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float)Math.floor(search.getTestCase())))
                    filteredSearches.add(search);
            }
            dataSearches = filteredSearches; 
        }
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.legacy.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        for (DataSetSearch search : dataSearches)
        {
            TestResult testResult = new TestResult();
            try
            {
                if (search.getApi() == null)
                {
                    setTestResultFailure(testResult, search,
                            "No API indicated for test case: "
                                    + search.getTestCase());
                    resultReport.addTestResult(testResult);
                    continue;
                }

                
                DataSet results = null;
                long startTime = System.currentTimeMillis();
                try
                {
                    results = apiFacade.getDataSet(search.getApi(), search.getRequest());
                }
                catch (Throwable t)
                {
                    String detail = "An exception occured executing a DataSet search: "
                    + t.getClass() + " " + t.getLocalizedMessage();
                    testResult.addDetail(detail);
                    t.printStackTrace();
                }
                long elapsedTime = System.currentTimeMillis() - startTime;

                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null)
                    testResult.setTestCase(search.getTestCase());

                evaluateResults(results, search, testResult);
            }
            catch (Throwable t)
            {

                setTestResultFailure(testResult, search,
                        "An exception occured executing a DataSet search: "
                                + t.getClass() + " " + t.getLocalizedMessage());
                t.printStackTrace();
            }

            resultReport.addTestResult(testResult);
        }
        
        System.out.println(getType() + " tests complete ...");
    }
    
    protected void evaluateResults(DataSet dataSet, DataSetSearch dataSearch,
            TestResult testResult)
    {
        int namedResults = 0;
        
        if (dataSet != null && dataSet.getDesignElementList() != null)
        {
            String designElementType = dataSet.getDesignElementList().getDesignElementType();
            if (DesignElementType.LOGICAL_PROBE.getValue().equals(designElementType)) {
                for (AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements())
                {
                    LogicalProbe probe = (LogicalProbe)element;
                    if (probe != null && probe.getId()!= null)
                        namedResults++;
                }
            }
            
        }
        
        if (dataSearch.getExpectedProbeIds() != null)
        {

            if (namedResults != dataSearch.getExpectedProbeIds())
            {
                String errorMessage = "Failed with unexpected number of probe ids, expected: "
                        + dataSearch.getExpectedProbeIds()
                        + ", actual number of probe ids: " + namedResults;
                setTestResultFailure(testResult, dataSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of probe ids: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        
        if (dataSearch.getExpectedDataType() != null)
        {
            DataType expectedType = dataSearch.getExpectedDataType();
            int expectedResults = dataSearch.getExpectedDataResults();
            boolean foundType = false;
            
                if (dataSet != null)
                {
                    
                    for (HybridizationData hyb : dataSet.getHybridizationDataList())
                    {

                    int valuesFound = 0;
                    for (AbstractDataColumn column : hyb.getColumns())
                    {
                        // Find the type of the column.
                        QuantitationType quantType = column
                                .getQuantitationType();

                        if (quantType.getDataType().equals(expectedType))
                        {
                            foundType = true;
                            DataType dataType = quantType.getDataType();

                            int valuesLen = 0;
                            switch (dataType)
                            {
                            case BOOLEAN:
                                boolean[] bval = ((BooleanColumn) column)
                                        .getValues();
                                valuesLen = (bval != null ? bval.length : 0);
                                break;
                            case DOUBLE:
                                double[] dval = ((DoubleColumn) column)
                                        .getValues();
                                valuesLen = (dval != null ? dval.length : 0);
                                break;
                            case FLOAT:
                                float[] fval = ((FloatColumn) column)
                                        .getValues();
                                valuesLen = (fval != null ? fval.length : 0);
                                break;
                            case INTEGER:
                                int[] ival = ((IntegerColumn) column)
                                        .getValues();
                                valuesLen = (ival != null ? ival.length : 0);
                                break;
                            case LONG:
                                long[] lval = ((LongColumn) column).getValues();
                                valuesLen = (lval != null ? lval.length : 0);
                                break;
                            case SHORT:
                                short[] sval = ((ShortColumn) column)
                                        .getValues();
                                valuesLen = (sval != null ? sval.length : 0);
                                break;
                            case STRING:
                                String[] stval = ((StringColumn) column)
                                        .getValues();
                                valuesLen = (stval != null ? stval.length : 0);
                                break;
                            default:
                                break;
                            }

                            valuesFound = valuesLen;
                            if (valuesFound == expectedResults)
                                break;

                        }
                    }
                    if (valuesFound != expectedResults)
                    {
                        String detail = "Didn't find expected number of "
                                + expectedType.getName() + " values for hyb " + hyb.getHybridization().getName() + ", "
                                + "expected: "
                                + dataSearch.getExpectedDataResults()
                                + ", actual " + "results: " + valuesFound;
                        setTestResultFailure(testResult, dataSearch, detail);
                    }
                    else
                    {
                        String detail = "Found expected number of "
                                + expectedType.getName() + " values for hyb " + hyb.getHybridization().getName() +": "
                                + valuesFound;
                        testResult.addDetail(detail);
                    }
                }
                                    
                                
                    
                
                
                if (!foundType)
                {
                    String errorMessage = "Didn't find expected data type: "
                            + dataSearch.getExpectedDataType();
                    setTestResultFailure(testResult, dataSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected data type: "
                            + dataSearch.getExpectedDataType();
                    testResult.addDetail(detail);
                }
            }

        }

    }
    protected void setTestResultFailure(TestResult testResult,
            DataSetSearch search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
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
        return "DataSet";
    }

    private CQLQuery getHybridizationQuery(String name)
    {
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.hybridization.Hybridization");
        Attribute att = new Attribute();
        att.setName("name");
        att.setValue(name);
        att.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(att);
        CQLQuery query = new CQLQuery();
        query.setTarget(target);
        return query;
    }
    
    private CQLQuery getQuantitationQuery(String name)
    {
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.data.QuantitationType");
        Attribute att = new Attribute();
        att.setName("name");
        att.setValue(name);
        att.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(att);
        CQLQuery query = new CQLQuery();
        query.setTarget(target);
        return query;
    }
}
