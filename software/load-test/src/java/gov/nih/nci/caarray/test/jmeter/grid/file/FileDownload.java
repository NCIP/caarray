//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.grid.file;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.test.jmeter.base.CaArrayJmeterSampler;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


/**
 * A client downloading an array data file through the CaArray Grid Service.
 */

public class FileDownload extends CaArrayJmeterSampler implements JavaSamplerClient {
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String DEFAULT_FILE_NAME = "H_TK6 MDR1 replicate 1.cel";

    private static final String TEST_SERVICE_URL = "test.serviceUrl";

    private String hostName;
    private int gridServicePort;

    /**
     * Sets up the file download test by initializing the server connection parameters.
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
        params.addArgument(FILE_NAME_PARAM, DEFAULT_FILE_NAME);
        params.addArgument(getHostNameParam(), getDefaultHostName());
        params.addArgument(getGridServicePortParam(), getDefaultGridServicePort());
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
        String fileName = context.getParameter(FILE_NAME_PARAM, DEFAULT_FILE_NAME);

        try {
            CaArraySvcClient client = new CaArraySvcClient(System.getProperty(TEST_SERVICE_URL));

            CaArrayFile caArrayFile = lookupFile(client, fileName);
            if (caArrayFile != null) {
                results.sampleStart();
                byte[] byteArray = client.readFile(caArrayFile);
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

    private CaArrayFile lookupFile(CaArraySvcClient client, String fileName) throws Exception {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.file.CaArrayFile");
        Attribute fileNameAttribute = new Attribute();
        fileNameAttribute.setName("name");
        fileNameAttribute.setValue(fileName);
        fileNameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(fileNameAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        return (CaArrayFile) iter.next();
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
