//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.Iterator;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * A custom JMeter Sampler that acts as a client searching for quantitation types using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchQuantitationType extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String TYPE_PARAM = "quantitationTypeType";

    private static final String DEFAULT_TYPE = "java.lang.Float";

    private String quantitationTypeType;
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
        params.addArgument(TYPE_PARAM, DEFAULT_TYPE);
        params.addArgument(getHostNameParam(), getDefaultHostName());
        params.addArgument(getJndiPortParam(), getDefaultJndiPort());
        return params;
    }

    /**
     * Runs the CQL search test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        quantitationTypeType = context.getParameter(TYPE_PARAM, DEFAULT_TYPE);

        CQLQuery cqlQuery = createCqlQuery();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List quantitationTypeList = searchService.search(cqlQuery);
            results.sampleEnd();
            if (isResultOkay(quantitationTypeList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + quantitationTypeList.size() + " quantitation types.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + quantitationTypeList.size() + " quantitation types.");
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

    private CQLQuery createCqlQuery() {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.data.QuantitationType");

        Attribute typeAttribute = new Attribute();
        typeAttribute.setName("type");
        typeAttribute.setValue(quantitationTypeType);
        typeAttribute.setPredicate(Predicate.EQUAL_TO);

        target.setAttribute(typeAttribute);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(List quantitationTypeList) {
        if (quantitationTypeList.isEmpty()) {
            return true;
        }

        Iterator i = quantitationTypeList.iterator();
        while (i.hasNext()) {
            QuantitationType retrievedQuantitationType = (QuantitationType) i.next();
            // Check if retrieved quantitation type matches requested search criteria.
            if (!quantitationTypeType.equals(retrievedQuantitationType.getType())) {
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
