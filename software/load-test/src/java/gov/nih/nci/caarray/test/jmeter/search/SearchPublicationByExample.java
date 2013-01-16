//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
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
 * A custom JMeter Sampler that acts as a client searching for publications by example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchPublicationByExample extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String STATUS_PARAM = "status";
    private static final String TYPE_PARAM = "publicationType";

    private static final String DEFAULT_STATUS = "Published";
    private static final String DEFAULT_TYPE = "Journal";

    private String publicationStatus;
    private String publicationType;
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
        params.addArgument(STATUS_PARAM, DEFAULT_STATUS);
        params.addArgument(TYPE_PARAM, DEFAULT_TYPE);
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
        publicationStatus = context.getParameter(STATUS_PARAM, DEFAULT_STATUS);
        publicationType = context.getParameter(TYPE_PARAM, DEFAULT_TYPE);

        Publication examplePublication = createExamplePublication();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List<Publication> publicationList = searchService.search(examplePublication);
            results.sampleEnd();
            if (isResultOkay(publicationList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + publicationList.size() + " publications.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + publicationList.size() + " publications.");
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

    private Publication createExamplePublication() {
        Publication examplePublication = new Publication();

        Term statusTerm = new Term();
        statusTerm.setValue(publicationStatus);
        Category publicationStatusCategory = new Category();
        publicationStatusCategory.setName("PublicationStatus");
        statusTerm.setCategory(publicationStatusCategory);
        examplePublication.setStatus(statusTerm);

        Term typeTerm = new Term();
        typeTerm.setValue(publicationType);
        Category publicationTypeCategory = new Category();
        publicationTypeCategory.setName("PublicationType");
        typeTerm.setCategory(publicationTypeCategory);
        examplePublication.setType(typeTerm);

        return examplePublication;
    }

    private boolean isResultOkay(List<Publication> publicationList) {
        if (publicationList.isEmpty()) {
            return true;
        }

        Iterator<Publication> i = publicationList.iterator();
        while (i.hasNext()) {
            Publication retrievedPublication = i.next();
            // Check if retrieved publication matches requested search criteria.
            if ((!publicationStatus.equals(retrievedPublication.getStatus().getValue())) || (!publicationType.equals(retrievedPublication.getType().getValue()))) {
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
