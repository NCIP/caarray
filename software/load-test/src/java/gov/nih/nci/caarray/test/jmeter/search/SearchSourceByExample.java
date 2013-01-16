//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import gov.nih.nci.caarray.domain.sample.Source;
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
 * A custom JMeter Sampler that acts as a client searching for sources by example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchSourceByExample extends CaArrayJmeterSampler implements JavaSamplerClient {
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
     * Runs the search-by-example test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        sourceName = context.getParameter(NAME_PARAM, DEFAULT_NAME);
        materialType = context.getParameter(MATERIAL_TYPE_PARAM, DEFAULT_MATERIAL_TYPE);

        Source exampleSource = createExampleSource();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List<Source> sourceList = searchService.search(exampleSource);
            results.sampleEnd();
            if (isResultOkay(sourceList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + sourceList.size() + " sources of name "
                        + sourceName + " and material type " + materialType);
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

    private Source createExampleSource() {
        Source exampleSource = new Source();

        exampleSource.setName(sourceName);

        Term materialTypeTerm = new Term();
        materialTypeTerm.setValue(materialType);
        Category materialTypeCategory = new Category();
        materialTypeCategory.setName("MaterialType");
        materialTypeTerm.setCategory(materialTypeCategory);
        exampleSource.setMaterialType(materialTypeTerm);

        return exampleSource;
    }

    private boolean isResultOkay(List<Source> sourceList) {
        if (sourceList.isEmpty()) {
            return true;
        }

        Iterator<Source> i = sourceList.iterator();
        while (i.hasNext()) {
            Source retrievedSource = i.next();
            // Check if retrieved source matches requested search criteria.
            if ((sourceName != null) && (!sourceName.equals(retrievedSource.getName()))) {
                return false;
            }
            if (materialType != null) {
                if ((retrievedSource.getMaterialType() == null)
                        || (!materialType.equals(retrievedSource.getMaterialType().getValue()))) {
                    return false;
                }
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
