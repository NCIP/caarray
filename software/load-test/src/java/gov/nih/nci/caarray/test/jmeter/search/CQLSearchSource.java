//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.sample.Source;
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
 * A custom JMeter Sampler that acts as a client searching for Sources using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchSource extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String NAME_PARAM = "sourceName";
    private static final String MATERIAL_TYPE_PARAM = "materialType";

    private static final String DEFAULT_NAME = null;
    private static final String DEFAULT_MATERIAL_TYPE = null;

    private String sourceName;
    private String materialType;
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
        params.addArgument(NAME_PARAM, DEFAULT_NAME);
        params.addArgument(MATERIAL_TYPE_PARAM, DEFAULT_MATERIAL_TYPE);
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
        sourceName = context.getParameter(NAME_PARAM, DEFAULT_NAME);
        materialType = context.getParameter(MATERIAL_TYPE_PARAM, DEFAULT_MATERIAL_TYPE);

        CQLQuery cqlQuery = createCqlQuery();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List sourceList = searchService.search(cqlQuery);
            results.sampleEnd();
            if (isResultOkay(sourceList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + sourceList.size() + " sources.");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + sourceList.size() + " sources.");
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
        target.setName("gov.nih.nci.caarray.domain.sample.Source");

        Attribute sourceNameAttribute = new Attribute();
        sourceNameAttribute.setName("name");
        sourceNameAttribute.setValue(sourceName);
        sourceNameAttribute.setPredicate(Predicate.EQUAL_TO);

        Association materialTypeAssociation = new Association();
        materialTypeAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute materialTypeAttribute = new Attribute();
        materialTypeAttribute.setName("value");
        materialTypeAttribute.setValue(materialType);
        materialTypeAttribute.setPredicate(Predicate.EQUAL_TO);
        materialTypeAssociation.setAttribute(materialTypeAttribute);
        materialTypeAssociation.setRoleName("materialType");

        Group associations = new Group();
        associations.setAttribute(new Attribute[] {sourceNameAttribute});
        associations.setAssociation(new Association[] {materialTypeAssociation});
        associations.setLogicRelation(LogicalOperator.AND);
        target.setGroup(associations);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(List sourceList) {
        if (sourceList.isEmpty()) {
            return true;
        }

        Iterator i = sourceList.iterator();
        while (i.hasNext()) {
            Source retrievedSource = (Source) i.next();
            // Check if retrieved Source matches requested search criteria.
            if ((!sourceName.equals(retrievedSource.getName())) || (!materialType.equals(retrievedSource.getMaterialType().getValue()))) {
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
