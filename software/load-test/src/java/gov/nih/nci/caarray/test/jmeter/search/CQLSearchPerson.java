//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.contact.Person;
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
 * A custom JMeter Sampler that acts as a client searching for persons using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchPerson extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String LAST_NAME_PARAM = "lastName";

    private static final String DEFAULT_LAST_NAME = "User";

    private String lastName;
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
        params.addArgument(LAST_NAME_PARAM, DEFAULT_LAST_NAME);
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
        lastName = context.getParameter(LAST_NAME_PARAM, DEFAULT_LAST_NAME);

        CQLQuery cqlQuery = createCqlQuery();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List personList = searchService.search(cqlQuery);
            results.sampleEnd();
            if (isResultOkay(personList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + personList.size() + " persons.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + personList.size()
                        + " persons.");
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
        target.setName("gov.nih.nci.caarray.domain.contact.Person");

        Attribute affiliationAttribute = new Attribute();
        affiliationAttribute.setName("lastName");
        affiliationAttribute.setValue(lastName);
        affiliationAttribute.setPredicate(Predicate.EQUAL_TO);

        target.setAttribute(affiliationAttribute);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(List personList) {
        if (personList.isEmpty()) {
            return true;
        }

        Iterator i = personList.iterator();
        while (i.hasNext()) {
            Person retrievedPerson = (Person) i.next();
            // Check if retrieved person matches requested search criteria.
            if (!lastName.equals(retrievedPerson.getLastName())) {
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
