//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.project.Experiment;
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
 * A custom JMeter Sampler that acts as a client searching for experiments using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchClient extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String MANUFACTURER_PARAM = "manufacturerName";
    private static final String ORGANISM_PARAM = "organismName";

    private static final String DEFAULT_MANUFACTURER = "Affymetrix";
    private static final String DEFAULT_ORGANISM = "Mouse";

    private String manufacturer;
    private String organism;
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
        params.addArgument(MANUFACTURER_PARAM, DEFAULT_MANUFACTURER);
        params.addArgument(ORGANISM_PARAM, DEFAULT_ORGANISM);
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
        manufacturer = context.getParameter(MANUFACTURER_PARAM, DEFAULT_MANUFACTURER);
        organism = context.getParameter(ORGANISM_PARAM, DEFAULT_ORGANISM);

        CQLQuery cqlQuery = createCqlQuery();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List experimentList = searchService.search(cqlQuery);
            results.sampleEnd();
            if (isResultOkay(experimentList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + experimentList.size() + " experiments.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request.");
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
        gov.nih.nci.cagrid.cqlquery.Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association manufacturerAssociation = new Association();
        manufacturerAssociation.setName("gov.nih.nci.caarray.domain.contact.Organization");
        Attribute manufacturerName = new Attribute();
        manufacturerName.setName("name");
        manufacturerName.setValue(manufacturer);
        manufacturerName.setPredicate(Predicate.EQUAL_TO);
        manufacturerAssociation.setAttribute(manufacturerName);
        manufacturerAssociation.setRoleName("manufacturer");

        Association organismAssociation = new Association();
        organismAssociation.setName("edu.georgetown.pir.Organism");
        Attribute organismName = new Attribute();
        organismName.setName("commonName");
        organismName.setValue(organism);
        organismName.setPredicate(Predicate.EQUAL_TO);
        organismAssociation.setAttribute(organismName);
        organismAssociation.setRoleName("organism");

        Group associations = new Group();
        associations.setAssociation(new Association[] {manufacturerAssociation, organismAssociation});
        associations.setLogicRelation(LogicalOperator.AND);
        target.setGroup(associations);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(List experimentList) {
        if (experimentList.isEmpty()) {
            return false;
        }

        Iterator i = experimentList.iterator();
        while (i.hasNext()) {
            Experiment retrievedExperiment = (Experiment) i.next();
            if ((!manufacturer.equals(retrievedExperiment.getManufacturer().getName()))
                    || (!organism.equals(retrievedExperiment.getOrganism().getCommonName()))) {
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
