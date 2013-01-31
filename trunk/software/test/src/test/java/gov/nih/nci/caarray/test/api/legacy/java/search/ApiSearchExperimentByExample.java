//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.java.search;

import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * A client searching for experiments using search-by-example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiSearchExperimentByExample extends AbstractApiTest {
    private static final String[] MANUFACTURER_NAMES = {
        "Affymetrix",
        "Illumina"
    };
    private static final String[] ORGANISM_NAMES = {
        "human",
        "black rat"
    };

    @Test
    public void testSearchExperiment() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Searching by Example for Experiments...");
            int i = 0;
            for (String manufacturerName : MANUFACTURER_NAMES) {
                String organismName = ORGANISM_NAMES[i++];
                boolean resultIsOkay = searchExperiments(searchService, manufacturerName, organismName);
                assertTrue("Error: Response did not match request.", resultIsOkay);
            }
        } catch (ServerConnectionException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Server connection exception: " + e + "\nTrace: " + trace);
            assertTrue("Server connection exception: " + e, false);
        } catch (RuntimeException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Runtime exception: " + e + "\nTrace: " + trace);
            assertTrue("Runtime exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private boolean searchExperiments(CaArraySearchService searchService, String manufacturerName, String organismName) {
        Experiment exampleExperiment = createExampleExperiment(manufacturerName, organismName);
        List<Experiment> experimentList = searchService.search(exampleExperiment);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
        boolean resultIsOkay = isResultOkay(experimentList, manufacturerName, organismName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + experimentList.size()
                    + " experiments with array provider " + manufacturerName + " and organism "
                    + organismName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request. Retrieved " + experimentList.size()
                    + " experiments.");
        }
        for (Experiment experiment : experimentList) {
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getTitle(): " + experiment.getTitle());
        }
        return resultIsOkay;
    }

    private Experiment createExampleExperiment(String manufacturerName, String organismName) {
        Experiment exampleExperiment = new Experiment();

        Organization organization = new Organization();
        organization.setName(manufacturerName);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organismName);
        exampleExperiment.setOrganism(organismCriterion);

        return exampleExperiment;
    }

    private boolean isResultOkay(List<Experiment> experimentList, String manufacturerName, String organismName) {
        if (experimentList.isEmpty()) {
            return false;
        }

        Iterator<Experiment> i = experimentList.iterator();
        while (i.hasNext()) {
            Experiment retrievedExperiment = i.next();
            // Check if retrieved experiment matches requested search criteria.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getManufacturer().getName(): "
                    + retrievedExperiment.getManufacturer().getName());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getOrganism().getCommonName(): "
                    + retrievedExperiment.getOrganism().getCommonName());
            if ((!manufacturerName.equals(retrievedExperiment.getManufacturer().getName()))
                    || (!organismName.equals(retrievedExperiment.getOrganism().getCommonName()))) {
                return false;
            }
            // Check if retrieved experiment has mandatory fields.
            if (retrievedExperiment.getTitle() == null || retrievedExperiment.getAssayTypes() == null
                    && retrievedExperiment.getManufacturer() == null) {
                return false;
            }
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getAssayTypes(): "
                    + retrievedExperiment.getAssayTypes());
        }
        return true;
    }
}
