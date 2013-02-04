//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.Iterator;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * A custom JMeter Sampler that acts as a client searching for publications using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchPublication extends CaArrayJmeterSampler implements JavaSamplerClient {
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
     * Runs the CQL search test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        publicationStatus = context.getParameter(STATUS_PARAM, DEFAULT_STATUS);
        publicationType = context.getParameter(TYPE_PARAM, DEFAULT_TYPE);

        CQLQuery cqlQuery = createCqlQuery();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List publicationList = searchService.search(cqlQuery);
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

    private CQLQuery createCqlQuery() {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.publication.Publication");

        Association statusAssociation = new Association();
        statusAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute statusAttribute = new Attribute();
        statusAttribute.setName("value");
        statusAttribute.setValue(publicationStatus);
        statusAttribute.setPredicate(Predicate.EQUAL_TO);
        statusAssociation.setAttribute(statusAttribute);
        statusAssociation.setRoleName("status");

        Association typeAssociation = new Association();
        typeAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute typeAttribute = new Attribute();
        typeAttribute.setName("value");
        typeAttribute.setValue(publicationType);
        typeAttribute.setPredicate(Predicate.EQUAL_TO);
        typeAssociation.setAttribute(typeAttribute);
        typeAssociation.setRoleName("type");

        Group associations = new Group();
        associations.setAssociation(new Association[] {statusAssociation, typeAssociation});
        associations.setLogicRelation(LogicalOperator.AND);
        target.setGroup(associations);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(List publicationList) {
        if (publicationList.isEmpty()) {
            return true;
        }

        Iterator i = publicationList.iterator();
        while (i.hasNext()) {
            Publication retrievedPublication = (Publication) i.next();
            // Check if retrieved publication matches requested search criteria.
            if ((!publicationStatus.equals(retrievedPublication.getStatus().getValue()))
                    || (!publicationType.equals(retrievedPublication.getType().getValue()))) {
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
