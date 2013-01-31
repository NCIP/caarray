//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;

import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * A custom JMeter Sampler that acts as a client downloading array design details through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ArrayDesignDownloadClient extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String ARRAY_DESIGN_NAME_PARAM = "arrayDesignName";
    private static final String DEFAULT_ARRAY_DESIGN_NAME = "Test3";

    private String arrayDesignName;
    private String hostName;
    private int jndiPort;

    /**
     * Sets up the array design download test by initializing the server connection parameters.
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
        params.addArgument(ARRAY_DESIGN_NAME_PARAM, DEFAULT_ARRAY_DESIGN_NAME);
        params.addArgument(getHostNameParam(), getDefaultHostName());
        params.addArgument(getJndiPortParam(), getDefaultJndiPort());
        return params;
    }

    /**
     * Runs the array design download test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        arrayDesignName = context.getParameter(ARRAY_DESIGN_NAME_PARAM, DEFAULT_ARRAY_DESIGN_NAME);

        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();

            ArrayDesign arrayDesign = lookupArrayDesign(searchService, arrayDesignName);
            if (arrayDesign != null) {
                ArrayDesignDetailsService arrayDesignDetailsService = server.getArrayDesignDetailsService();
                results.sampleStart();
                ArrayDesignDetails details = arrayDesignDetailsService.getDesignDetails(arrayDesign);
                results.sampleEnd();
                if (details != null) {
                    results.setSuccessful(true);
                    results.setResponseCodeOK();
                    results.setResponseMessage("Retrieved " + details.getFeatures().size() + " features, "
                            + details.getProbeGroups().size() + " probe groups, " + details.getProbes().size()
                            + " probes and " + details.getLogicalProbes().size() + " logical probes.");
                } else {
                    results.setSuccessful(false);
                    results.setResponseCode("Error: Array Design Details was null.");
                }
            } else {
                results.sampleEnd();
                results.setSuccessful(false);
                results.setResponseCode("Error: Array Design was null.");
            }
        } catch (ServerConnectionException e) {
            results.setSuccessful(false);
            StringBuilder trace = buildStackTrace(e);
            results.setResponseCode("Could not connect to server at host " + hostName + "; port " + jndiPort
                    + "; exception: " + e + "\nTrace: " + trace);
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

    private ArrayDesign lookupArrayDesign(CaArraySearchService service, String arrayDesignName) {
        ArrayDesign exampleArrayDesign = new ArrayDesign();
        exampleArrayDesign.setName(arrayDesignName);

        List<ArrayDesign> arrayDesignList = service.search(exampleArrayDesign);
        int numArrayDesignsFound = arrayDesignList.size();
        if (numArrayDesignsFound == 0) {
            return null;
        }
        ArrayDesign arrayDesign = arrayDesignList.get(0);
        return arrayDesign;
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
