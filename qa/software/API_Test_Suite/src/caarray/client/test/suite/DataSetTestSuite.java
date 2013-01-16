//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.BooleanColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.DesignElement;
import gov.nih.nci.caarray.external.v1_0.data.DoubleColumn;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FloatColumn;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.IntegerColumn;
import gov.nih.nci.caarray.external.v1_0.data.LongColumn;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.data.ShortColumn;
import gov.nih.nci.caarray.external.v1_0.data.StringColumn;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.DataSetSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 13, 2009
 */
public class DataSetTestSuite extends SearchByCriteriaTestSuite
{
    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + java.io.File.separator + "DataSet.csv";

    private static final String FILE = "File";
    private static final String FILE_REF = "File Reference";
    private static final String FILE_EXPERIMENT = "File Experiment";
    private static final String FILE_EXPERIMENT_ID = "File Experiment Id";
    private static final String QUANT_TYPE = "Quantitation Type";
    private static final String HYB = "Hybridization";
    private static final String HYB_REF = "Hybridization Reference";
    
    private static final String EXPECTED_PROBE = "Expected Probe Ids";
    private static final String EXPECTED_QUANT_TYPE = "Expected Quantitation Type";
    private static final String EXPECTED_DATA_TYPE = "Expected Data Type";
    private static final String EXPECTED_DATA_RESULTS = "Expected Data Results";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, FILE_REF, FILE, EXPECTED_RESULTS, MIN_RESULTS, FILE_EXPERIMENT, FILE_EXPERIMENT_ID,QUANT_TYPE, HYB, HYB_REF,EXPECTED_QUANT_TYPE,
            EXPECTED_PROBE, EXPECTED_DATA_TYPE, EXPECTED_DATA_RESULTS, MAX_TIME};
    /**
     * @param apiFacade
     */
    public DataSetTestSuite(ApiFacade apiFacade)
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
        DataSetSearch dataSearch = (DataSetSearch) search;
        DataSet dataResults = (DataSet) resultsList;
        int namedResults = 0;

        if (dataResults != null && dataResults.getDesignElements() != null)
        {
            for (DesignElement probe : dataResults.getDesignElements())
            {
                if (probe != null && probe.getName() != null)
                    namedResults++;
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
        if (dataSearch.getMinResults() != null)
        {

            if (namedResults < dataSearch.getMinResults())
            {
                String errorMessage = "Failed with unexpected number of probe ids, expected minimum: "
                        + dataSearch.getMinResults()
                        + ", actual number of probe ids: " + namedResults;
                setTestResultFailure(testResult, dataSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (dataSearch.getMaxTime() != null)
        {

            if (testResult.getElapsedTime() < dataSearch.getMaxTime())
            {
                String errorMessage = "Search did not complete in expected time, expected: "
                        + dataSearch.getMaxTime()
                        + ", actual time: " + testResult.getElapsedTime();
                setTestResultFailure(testResult, dataSearch, errorMessage);
            }
            else
            {
                String detail = "Search completed in expected time: "
                        + testResult.getElapsedTime();
                testResult.addDetail(detail);
            }
        }
        if (!dataSearch.getExpectedQuantitationTypes().isEmpty())
        {

            for (String expectedName : dataSearch
                    .getExpectedQuantitationTypes())
            {
                boolean foundName = false;
                if (dataResults != null)
                {
                    for (QuantitationType type : dataResults.getQuantitationTypes())
                    {
                        if (type != null
                                && type.getName().equalsIgnoreCase(expectedName))
                        {
                            foundName = true;
                            break;
                        }
                    }
                }
                
                if (!foundName)
                {
                    String errorMessage = "Didn't find expected quantitation type: "
                            + expectedName;
                    setTestResultFailure(testResult, dataSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected quantitation type: "
                            + expectedName;
                    testResult.addDetail(detail);
                }
            }
        }
        if (!dataSearch.getExpectedDataType().isEmpty())
        {
            for (String typeKey : dataSearch.getExpectedDataType().keySet())
            {
                boolean foundType = false;
                if (dataResults != null)
                {
                    for (QuantitationType quantType : dataResults
                            .getQuantitationTypes())
                    {
                        if (quantType.getName().equalsIgnoreCase(typeKey))
                        {
                            DataType expectedType = dataSearch.getExpectedDataType().get(typeKey);
                            if (quantType.getDataType().equals(
                                    expectedType))
                            {
                                foundType = true;
                                DataType dataType = quantType.getDataType();
                                if (dataSearch.getExpectedDataResults()
                                        .containsKey(typeKey))
                                {
                                    int valuesFound = 0;
                                    int expectedValues = dataSearch.getExpectedDataResults().get(typeKey);
                                    List<HybridizationData> hybData = dataResults
                                            .getDatas();
                                    for (HybridizationData data : hybData)
                                    {
                                        for (AbstractDataColumn column : data
                                                .getDataColumns())
                                        {
                                            if (column.getQuantitationType() != null
                                                    && column.getQuantitationType().getName()
                                                            .equalsIgnoreCase(typeKey))
                                            {
                                                int valuesLen = 0;
                                                switch (dataType)
                                                {
                                                case BOOLEAN:
                                                    boolean[] bval = ((BooleanColumn) column)
                                                            .getValues();
                                                    valuesLen = (bval != null ? bval.length
                                                            : 0);
                                                    break;
                                                case DOUBLE:
                                                    double[] dval = ((DoubleColumn) column)
                                                            .getValues();
                                                    valuesLen = (dval != null ? dval.length
                                                            : 0);
                                                    break;
                                                case FLOAT:
                                                    float[] fval = ((FloatColumn) column)
                                                            .getValues();
                                                    valuesLen = (fval != null ? fval.length
                                                            : 0);
                                                    break;
                                                case INTEGER:
                                                    int[] ival = ((IntegerColumn) column)
                                                            .getValues();
                                                    valuesLen = (ival != null ? ival.length
                                                            : 0);
                                                    break;
                                                case LONG:
                                                    long[] lval = ((LongColumn) column)
                                                            .getValues();
                                                    valuesLen = (lval != null ? lval.length
                                                            : 0);
                                                    break;
                                                case SHORT:
                                                    short[] sval = ((ShortColumn) column)
                                                            .getValues();
                                                    valuesLen = (sval != null ? sval.length
                                                            : 0);
                                                    break;
                                                case STRING:
                                                    String[] stval = ((StringColumn) column)
                                                            .getValues();
                                                    valuesLen = (stval != null ? stval.length
                                                            : 0);
                                                    break;
                                                default:
                                                    break;
                                                }
                                                
                                                    valuesFound = valuesLen;
                                                    if (valuesFound == expectedValues)
                                                        break;
                                                 
                                            }
                                        }
                                    }
                                    if (valuesFound != expectedValues)
                                    {
                                        String detail = "Didn't find expected number of "
                                            + dataType.getName()
                                            + " values, "
                                            + "expected: "
                                            + dataSearch
                                                    .getExpectedDataResults()
                                                    .get(typeKey)
                                            + ", actual "
                                            + "results: "
                                            + valuesFound;
                                    setTestResultFailure(
                                            testResult, dataSearch,
                                            detail);
                                    }
                                    else
                                    {
                                        String detail = "Found expected number of "
                                            + dataType.getName()
                                            + " values: "
                                            + valuesFound;
                                    testResult.addDetail(detail);
                                    }
                                }
                                break;
                            }

                        }

                    }
                }
                
                if (!foundType)
                {
                    String errorMessage = "Didn't find expected data type: "
                            + dataSearch.getExpectedDataType().get(typeKey);
                    setTestResultFailure(testResult, dataSearch, errorMessage);
                }
                else
                {
                    String detail = "Found expected data type: "
                            + dataSearch.getExpectedDataType().get(typeKey);
                    testResult.addDetail(detail);
                }
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
        DataSetSearch criteriaSearch = (DataSetSearch)search;
        DataSet result = null;
        try
        {
            result = apiFacade.getDataSet(search.getApi(), criteriaSearch.getDataSetRequest());
            
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered retrieving data set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            testResult.addDetail("Exception encountered retrieving data set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        
        
        
        return result;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new DataSetSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        DataSetSearch search = (DataSetSearch)criteriaSearch;
        DataSetRequest criteria = search.getDataSetRequest();
        if (headerIndexMap.get(EXPECTED_QUANT_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].equals(""))
            search.addExpectedQuantitationType(input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim());
        if (headerIndexMap.get(EXPECTED_DATA_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_TYPE)].equals(""))
        {
            String type = input[headerIndexMap.get(EXPECTED_DATA_TYPE)].toUpperCase();
            String quantType = input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim();
            search.getExpectedDataType().put(quantType, DataType.valueOf(type));
        }
        if (headerIndexMap.get(EXPECTED_DATA_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_RESULTS)].equals(""))
        {
            String res = input[headerIndexMap.get(EXPECTED_DATA_RESULTS)];
            String quantType = input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim();
            search.getExpectedDataResults().put(quantType, Integer.parseInt(res));
        }
        if (headerIndexMap.get(FILE) < input.length && !input[headerIndexMap.get(FILE)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            List<String> fileNames = new ArrayList<String>();
            fileNames.add(name);
            try
            {
                List<File> files = apiFacade.getFilesByName(search.getApi(), fileNames, search.getExperimentName());
                for (File file : files)
                {
                    criteria.getDataFiles().add(file.getReference());
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception occured retrieving files for DataSet test: " + search.getTestCase());
                log.error("Exception encountered:",e);
            }
            
            
            
        }
        if (headerIndexMap.get(HYB_REF) < input.length && !input[headerIndexMap.get(HYB_REF)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB_REF)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = new CaArrayEntityReference(name);
                     
            criteria.getHybridizations().add(ref);
        }  
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = null;
            try
            {
                Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
                ref = (hyb != null ? hyb.getReference() : new CaArrayEntityReference(name));
            }
            catch (Exception e)
            {
                ref = new CaArrayEntityReference(name);
                log.error("Exception encountered:",e);
            }
                     
            criteria.getHybridizations().add(ref);
        }
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(QUANT_TYPE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            CaArrayEntityReference ref = null;
            try
            {
                QuantitationType quant = apiFacade.getQuantitationType(search.getApi(), name);
                ref = (quant != null ? quant.getReference() : new CaArrayEntityReference(name));
            }
            catch (Exception e)
            {
                ref = new CaArrayEntityReference(name);
                log.error("Exception encountered:",e);
            }
                     
            criteria.getQuantitationTypes().add(ref);
        }
         
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        DataSetSearch search = (DataSetSearch)criteriaSearch;
        DataSetRequest criteria = new DataSetRequest();
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
        if (headerIndexMap.get(MAX_TIME) < input.length
                && !input[headerIndexMap.get(MAX_TIME)].equals(""))
            search.setMaxTime(Long
                    .parseLong(input[headerIndexMap.get(MAX_TIME)].trim()));
        
        if (headerIndexMap.get(EXPECTED_PROBE) < input.length
                && !input[headerIndexMap.get(EXPECTED_PROBE)].equals(""))
            search.setExpectedProbeIds(Integer.parseInt(input[headerIndexMap.get(EXPECTED_PROBE)].trim()));
        if (headerIndexMap.get(EXPECTED_QUANT_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].equals(""))
            search.addExpectedQuantitationType(input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim());
        if (headerIndexMap.get(EXPECTED_DATA_TYPE) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_TYPE)].equals(""))
        {
            String type = input[headerIndexMap.get(EXPECTED_DATA_TYPE)];
            String quantType = input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim();
            search.getExpectedDataType().put(quantType, DataType.valueOf(type));
        }
        if (headerIndexMap.get(EXPECTED_DATA_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_DATA_RESULTS)].equals(""))
        {
            String res = input[headerIndexMap.get(EXPECTED_DATA_RESULTS)];
            String quantType = input[headerIndexMap.get(EXPECTED_QUANT_TYPE)].trim();
            search.getExpectedDataResults().put(quantType, Integer.parseInt(res));
        }
        
            
        if (headerIndexMap.get(FILE_REF) < input.length && !input[headerIndexMap.get(FILE_REF)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE_REF)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = new CaArrayEntityReference(name);
            criteria.getDataFiles().add(ref);
        }
        
        if (headerIndexMap.get(FILE) < input.length && !input[headerIndexMap.get(FILE)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            List<String> fileNames = new ArrayList<String>();
            fileNames.add(name);
            if (headerIndexMap.get(FILE_EXPERIMENT) < input.length && !input[headerIndexMap.get(FILE_EXPERIMENT)].equals(""))
            {
                String experiment = input[headerIndexMap.get(FILE_EXPERIMENT)].trim();
                search.setExperimentName(experiment);
                List<File> files = apiFacade.getFilesByName(search.getApi(), fileNames, experiment);
                
                    for (File file : files)
                    {
                        criteria.getDataFiles().add(file.getReference());
                    }
                
                
            }
            else if (headerIndexMap.get(FILE_EXPERIMENT_ID) < input.length && !input[headerIndexMap.get(FILE_EXPERIMENT_ID)].equals(""))
            {
                String experimentId = input[headerIndexMap.get(FILE_EXPERIMENT_ID)].trim();
                ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
                crit.setPublicIdentifier(experimentId);
                SearchResult<Experiment> result = (SearchResult<Experiment>)apiFacade.searchForExperiments(search.getApi(), crit, null, false);
                if (!result.getResults().isEmpty())
                {
                    Experiment exp = result.getResults().get(0);
                    search.setExperimentName(exp.getTitle());
                    List<File> files = apiFacade.getFilesByName(search.getApi(), fileNames, exp.getTitle());
                    for (File file : files)
                    {
                        criteria.getDataFiles().add(file.getReference());
                    }
                }
            }
            
        }
            
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            String name = input[headerIndexMap.get(HYB)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            CaArrayEntityReference ref = null;
            try
            {
                Hybridization hyb = apiFacade.getHybridization(search.getApi(), name);
                ref = (hyb != null ? hyb.getReference() : new CaArrayEntityReference(name));
            }
            catch (Exception e)
            {
                ref = new CaArrayEntityReference(name);
                log.error("Exception encountered:",e);
            }
                     
            criteria.getHybridizations().add(ref);
        }
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            String name = input[headerIndexMap.get(QUANT_TYPE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            
            CaArrayEntityReference ref = null;
            try
            {
                QuantitationType quant = apiFacade.getQuantitationType(search.getApi(), name);
                ref = (quant != null ? quant.getReference() : new CaArrayEntityReference(name));
            }
            catch (Exception e)
            {
                ref = new CaArrayEntityReference(name);
                log.error("Exception encountered:",e);
            }
                     
            criteria.getQuantitationTypes().add(ref);
        }
         
        
        search.setDataSetRequest(criteria);    
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
        return "DataSet";
    }

}
