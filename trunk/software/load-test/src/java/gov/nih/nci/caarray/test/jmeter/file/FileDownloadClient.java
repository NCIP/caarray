//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.file;

import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;

import java.util.List;
import java.util.Set;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


/**
 * A client downloading an array data file through CaArray's Remote Java API.
 */

public class FileDownloadClient extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String EXPERIMENT_NAME_PARAM = "experimentName";
    private static final String DEFAULT_EXPERIMENT_NAME = "Affymetrix Mouse with Data 01";

    private String hostName;
    private int jndiPort;

    /**
     * Sets up the file download test by initializing the server connection parameters.
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
     * Runs the file download test and returns the result.
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
                    CaArrayFile dataFile = getDataFile(searchService, hybridization);
                    if (dataFile != null) {
                        FileRetrievalService fileRetrievalService = server.getFileRetrievalService();
                        results.sampleStart();
                        byte[] byteArray = fileRetrievalService.readFile(dataFile);
                        if (byteArray != null) {
                            results.setSuccessful(true);
                            results.setResponseCodeOK();
                            results.setResponseMessage("Retrieved " + byteArray.length + " bytes.");
                        } else {
                            results.setSuccessful(false);
                           results.setResponseCode("Error: Retrieved null byte array.");
                        }
                        results.sampleEnd();
                    } else {
                        results.setSuccessful(false);
                        results.setResponseCode("Error: Retrieved null data file.");
                    }
                } else {
                    results.setSuccessful(false);
                    results.setResponseCode("Error: Retrieved null hybridization for experiment with title " + experimentTitle);
                }
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Could not find experiment." + experimentTitle);
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
        return experimentList.get(0);
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

    private CaArrayFile getDataFile(CaArraySearchService service, Hybridization hybridization) {
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        for (RawArrayData rawArrayData : rawArrayDataSet) {
            // Return the file associated with the first raw data.
            RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
            return populatedArrayData.getDataFile();
        }
        // If raw data doesn't exist, try to find derived data
        Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
        for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
            // Return the file associated with the first derived data.
            DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
            return populatedArrayData.getDataFile();
        }
        return null;
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
