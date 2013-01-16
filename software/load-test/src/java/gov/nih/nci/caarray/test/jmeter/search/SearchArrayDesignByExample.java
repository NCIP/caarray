//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;

import java.util.Iterator;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * A custom JMeter Sampler that acts as a client searching for array designs by example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchArrayDesignByExample extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String PROVIDER_PARAM = "provider";

    private static final String DEFAULT_PROVIDER = "Affymetrix";

    private String provider;
    private String hostName;
    private int jndiPort;

    /**
     * Sets up the search-by-example test by initializing the connection parameters to use.
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
        params.addArgument(PROVIDER_PARAM, DEFAULT_PROVIDER);
        params.addArgument(getHostNameParam(), getDefaultHostName());
        params.addArgument(getJndiPortParam(), getDefaultJndiPort());
        return params;
    }

    /**
     * Runs the search-by-example test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        provider = context.getParameter(PROVIDER_PARAM, DEFAULT_PROVIDER);

        ArrayDesign exampleArrayDesign = createExampleArrayDesign();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List<ArrayDesign> arrayDesignList = searchService.search(exampleArrayDesign);
            results.sampleEnd();
            if (isResultOkay(arrayDesignList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + arrayDesignList.size() + " array designs.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + arrayDesignList.size() + " array designs.");
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

    private ArrayDesign createExampleArrayDesign() {
        ArrayDesign exampleArrayDesign = new ArrayDesign();

        Organization organization = new Organization();
        organization.setName(provider);
        organization.setProvider(true);
        exampleArrayDesign.setProvider(organization);

        return exampleArrayDesign;
    }

    private boolean isResultOkay(List<ArrayDesign> arrayDesignList) {
        if (arrayDesignList.isEmpty()) {
            return true;
        }

        Iterator<ArrayDesign> i = arrayDesignList.iterator();
        while (i.hasNext()) {
            ArrayDesign retrievedArrayDesign = i.next();
            // Check if retrieved array design matches requested search criteria.
            if (!provider.equals(retrievedArrayDesign.getProvider().getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
