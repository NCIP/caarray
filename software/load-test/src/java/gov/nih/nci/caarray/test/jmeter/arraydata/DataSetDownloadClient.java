//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.arraydata;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
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
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


/**
 * A custom JMeter Sampler that acts as a client downloading an array data set through CaArray's Remote Java API.
 * The DataSet can contain data from multiple experiments, hybridizations and a subset of quantitation types.
 *
 * @author Rashmi Srinivasa
 */
public class DataSetDownloadClient extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String EXPERIMENT_NAMES_PARAM = "experimentNamesCsv";
    private static final String QUANTITATION_TYPES_PARAM = "quantitationTypesCsv";

    private static final String DEFAULT_EXPERIMENT_NAME = "Affymetrix Mouse with Data 01";
    private static final String DEFAULT_QUANTITATION_TYPE = "CELintensity";

    private String experimentTitlesCsv;
    private String quantitationTypesCsv;
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
        params.addArgument(EXPERIMENT_NAMES_PARAM, DEFAULT_EXPERIMENT_NAME);
        params.addArgument(QUANTITATION_TYPES_PARAM, DEFAULT_QUANTITATION_TYPE);
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
        experimentTitlesCsv = context.getParameter(EXPERIMENT_NAMES_PARAM, DEFAULT_EXPERIMENT_NAME);
        quantitationTypesCsv = context.getParameter(QUANTITATION_TYPES_PARAM, DEFAULT_QUANTITATION_TYPE);

        DataRetrievalRequest request = new DataRetrievalRequest();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();

            lookupExperiments(searchService, request);
            lookupQuantitationTypes(searchService, request);
            DataRetrievalService dataService = server.getDataRetrievalService();
            results.sampleStart();
            DataSet dataSet = dataService.getDataSet(request);
            int numValuesRetrieved = 0;

            // Check if the retrieved hybridizations and quantitation types are as requested.
            if (dataSet != null) {
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
                results.sampleEnd();
                results.setSuccessful(false);
                results.setResponseCode("Retrieved null DataSet.");
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

    private void lookupExperiments(CaArraySearchService service, DataRetrievalRequest request) {
        String[] experimentTitles = experimentTitlesCsv.split(",");
        if (experimentTitles == null) {
            return;
        }

        // Locate each experiment, and add its hybridizations to the request.
        Experiment exampleExperiment = new Experiment();
        for (int i = 0; i < experimentTitles.length; i++) {
            String experimentTitle = experimentTitles[i];
            exampleExperiment.setTitle(experimentTitle);
            List<Experiment> experimentList = service.search(exampleExperiment);
            Set<Hybridization> allHybs = getAllHybridizations(experimentList);
            request.getHybridizations().addAll(allHybs);
        }
    }

    private void lookupQuantitationTypes(CaArraySearchService service, DataRetrievalRequest request) {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        QuantitationType exampleQuantitationType = new QuantitationType();
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            exampleQuantitationType.setName(quantitationTypeName);
            List<QuantitationType> quantitationTypeList = service.search(exampleQuantitationType);
            request.getQuantitationTypes().addAll(quantitationTypeList);
        }
    }

    private Set<Hybridization> getAllHybridizations(List<Experiment> experimentList) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        for (Experiment experiment : experimentList) {
            hybridizations.addAll(getAllHybridizations(experiment));
        }
        return hybridizations;
    }

    private Set<Hybridization> getAllHybridizations(Experiment experiment) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        hybridizations.addAll(experiment.getHybridizations());
        return hybridizations;
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}



