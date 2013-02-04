//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.grid.search;

import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * A custom JMeter Sampler that acts as a client searching for sources using CQL through the CaArray Grid Service.
 *
 * @author Rashmi Srinivasa
 */
public class CQLSearchSource extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String NAME_PARAM = "sourceName";
    private static final String MATERIAL_TYPE_PARAM = "materialType";

    private static final String DEFAULT_NAME = null;
    private static final String DEFAULT_MATERIAL_TYPE = null;

    private static final String TEST_SERVICE_URL = "test.serviceUrl";

    private String sourceName;
    private String materialType;
    private String hostName;
    private int gridServicePort;

    /**
     * Sets up the search-by-example test by initializing the connection parameters to use.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void setupTest(JavaSamplerContext context) {
        hostName = context.getParameter(getHostNameParam(), getDefaultHostName());
        gridServicePort = Integer.parseInt(context.getParameter(getGridServicePortParam(), getDefaultGridServicePort()));
        System.setProperty(TEST_SERVICE_URL, "http://" + hostName + ":" + gridServicePort + "/wsrf/services/cagrid/CaArraySvc");
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
        params.addArgument(getGridServicePortParam(), getDefaultGridServicePort());
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
            CaArraySvcClient client = new CaArraySvcClient(System.getProperty(TEST_SERVICE_URL));
            results.sampleStart();
            CQLQueryResults cqlResults = client.query(cqlQuery);
            results.sampleEnd();
            results.setSuccessful(true);
            results.setResponseCodeOK();
            results.setResponseMessage("Retrieved " + cqlResults.getObjectResult().length + " sources.");
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

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
