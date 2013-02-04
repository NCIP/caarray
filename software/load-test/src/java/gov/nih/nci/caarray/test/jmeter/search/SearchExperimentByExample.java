//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.search;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
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
 * A custom JMeter Sampler that acts as a client searching for experiments by example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchExperimentByExample extends CaArrayJmeterSampler implements JavaSamplerClient {
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
     * Runs the search-by-example test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();
        manufacturer = context.getParameter(MANUFACTURER_PARAM, DEFAULT_MANUFACTURER);
        organism = context.getParameter(ORGANISM_PARAM, DEFAULT_ORGANISM);

        Experiment exampleExperiment = createExampleExperiment();
        try {
            CaArrayServer server = new CaArrayServer(hostName, jndiPort);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            results.sampleStart();
            List<Experiment> experimentList = searchService.search(exampleExperiment);
            results.sampleEnd();
            if (isResultOkay(experimentList)) {
                results.setSuccessful(true);
                results.setResponseCodeOK();
                results.setResponseMessage("Retrieved " + experimentList.size() + " experiments with array provider = "
                        + manufacturer + " and organism = " + organism + ".");
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request. Retrieved " + experimentList.size()
                        + " experiments with array provider = " + manufacturer + " and organism = " + organism + ".");
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

    private Experiment createExampleExperiment() {
        Experiment exampleExperiment = new Experiment();

        Organization organization = new Organization();
        organization.setName(manufacturer);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organism);
        exampleExperiment.setOrganism(organismCriterion);

        return exampleExperiment;
    }

    private boolean isResultOkay(List<Experiment> experimentList) {
        if (experimentList.isEmpty()) {
            return true;
        }

        Iterator<Experiment> i = experimentList.iterator();
        while (i.hasNext()) {
            Experiment retrievedExperiment = i.next();
            // Check if retrieved experiment matches requested search criteria.
            if ((!manufacturer.equals(retrievedExperiment.getManufacturer().getName()))
                    || (!organism.equals(retrievedExperiment.getOrganism().getCommonName()))) {
                return false;
            }
            // Check if retrieved experiment has mandatory fields.
            if ((retrievedExperiment.getTitle() == null)
                    || (retrievedExperiment.getAssayTypes() == null && retrievedExperiment.getManufacturer() == null)) {
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
