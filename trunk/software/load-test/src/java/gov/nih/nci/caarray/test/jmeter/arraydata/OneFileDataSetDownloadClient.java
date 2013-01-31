//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.arraydata;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;

import java.util.List;
import java.util.Set;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


/**
 * A client downloading a full data set corresponding to one data file through CaArray's Remote Java API.
 */

public class OneFileDataSetDownloadClient extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String EXPERIMENT_NAME_PARAM = "experimentName";
    private static final String DEFAULT_EXPERIMENT_NAME = "Affymetrix Mouse with Data 01";

    private String hostName;
    private int jndiPort;

    /**
     * Sets up the data set download test by initializing the server connection parameters.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void setupTest(JavaSamplerContext context) {
        hostName = context.getParameter(getHostNameParam(), getDefaultHostName());
        jndiPort = Integer.parseInt(context.getParameter(getJndiPortParam(), getDefaultJndiPort()));
    }

    /**
     * Returns the default parameters used by the Sampler if none is specified in the JMeter test being run.
     *
     * @return the <code>Arguments</code> containing default parameters.
     */
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument(EXPERIMENT_NAME_PARAM, DEFAULT_EXPERIMENT_NAME);
        params.addArgument(getHostNameParam(), getDefaultHostName());
        params.addArgument(getJndiPortParam(), getDefaultJndiPort());
        return params;
    }

    /**
     * Runs the data set download test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        String experimentTitle = context.getParameter(EXPERIMENT_NAME_PARAM, DEFAULT_EXPERIMENT_NAME);

        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();

            Experiment experiment = lookupExperiment(searchService, experimentTitle);
            if (experiment != null) {
                Hybridization hybridization = getFirstHybridization(searchService, experiment);
                if (hybridization != null) {
                    DataSet dataSet = getDataSet(searchService, hybridization);
                    if (dataSet != null) {
                        int numValuesRetrieved = 0;
                        // Get each HybridizationData in the DataSet.
                        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
                            HybridizationData populatedHybData = searchService.search(oneHybData).get(0);
                            // Get each column in the HybridizationData.
                            for (AbstractDataColumn column : populatedHybData.getColumns()) {
                                AbstractDataColumn populatedColumn = searchService.search(column).get(0);
                                // Find the type of the column.
                                QuantitationType qType = populatedColumn.getQuantitationType();
                                Class typeClass = qType.getTypeClass();
                                // Retrieve the appropriate data depending on the type of the column.
                                if (typeClass == String.class) {
                                    String[] values = ((StringColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Float.class) {
                                    float[] values = ((FloatColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Short.class) {
                                    short[] values = ((ShortColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Boolean.class) {
                                    boolean[] values = ((BooleanColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Double.class) {
                                    double[] values = ((DoubleColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Integer.class) {
                                    int[] values = ((IntegerColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else if (typeClass == Long.class) {
                                    long[] values = ((LongColumn) populatedColumn).getValues();
                                    numValuesRetrieved += values.length;
                                } else {
                                    // Should never get here.
                                }
                            }
                        }
                        results.sampleEnd();
                        results.setSuccessful(true);
                        results.setResponseCodeOK();
                        results.setResponseMessage("Retrieved " + dataSet.getHybridizationDataList().size() + " hybridization data elements, "
                                + dataSet.getQuantitationTypes().size() + " quantitation types and " + numValuesRetrieved + " values.");
                    } else {
                        results.setSuccessful(false);
                        results.setResponseCode("Error: Retrieved null data set.");
                    }
                } else {
                    results.setSuccessful(false);
                    results.setResponseCode("Error: Retrieved null hybridization.");
                }
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Could not find experiment.");
            }
        } catch (ServerConnectionException e) {
            results.setSuccessful(false);
            StringBuilder trace = buildStackTrace(e);
            results.setResponseCode("Server connection exception: " + e + "\nTrace: " + trace);
        } catch (RuntimeException e) {
            results.setSuccessful(false);
            StringBuilder trace = buildStackTrace(e);
            results.setResponseCode("Runtime exception: " + e + "\nTrace: " + trace);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them in the test output.
            results.setSuccessful(false);
            StringBuilder trace = buildStackTrace(t);
            results.setResponseCode("Throwable: " + t + "\nTrace: " + trace);
        }
        return results;
    }

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        if (experimentList.size() == 0) {
            return null;
        }
        Experiment experiment = experimentList.get(0);
        return experiment;
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private DataSet getDataSet(CaArraySearchService service, Hybridization hybridization) {
        DataSet dataSet = null;
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        for (RawArrayData rawArrayData : rawArrayDataSet) {
            // Return the data set associated with the first raw data.
            RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
            dataSet = populatedArrayData.getDataSet();
            break;
        }
        if (dataSet == null) {
            // If raw data doesn't exist, try to find derived data
            Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
            for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
                // Return the data set associated with the first derived data.
                DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
                dataSet = populatedArrayData.getDataSet();
                break;
            }
        }
        if (dataSet == null) {
            return null;
        } else {
            return service.search(dataSet).get(0);
        }
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
