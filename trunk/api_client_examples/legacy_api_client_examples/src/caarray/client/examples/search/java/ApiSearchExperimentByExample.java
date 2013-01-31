//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.search.java;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import java.util.List;

import caarray.client.examples.BaseProperties;

/**
 * A client searching for experiments using search-by-example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiSearchExperimentByExample {
    private static final String PROVIDER_NAME = "Affymetrix";
    private static final String ORGANISM_NAME = "human";

    public static void main(String[] args) {
        ApiSearchExperimentByExample client = new ApiSearchExperimentByExample();
        try {
            // Connect to the server.
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();

            // Get the search service.
            CaArraySearchService searchService = server.getSearchService();

            // Search for experiments.
            System.out.println("Searching for " + ORGANISM_NAME + " experiments run on the " + PROVIDER_NAME
                    + " platform...");
            client.searchExperiments(searchService, PROVIDER_NAME, ORGANISM_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    // Search for experiments based on array provider and organism.
    private void searchExperiments(CaArraySearchService searchService, String providerName, String organismName) {
        // Create an example experiment with the desired array provider and organism.
        Experiment exampleExperiment = createExampleExperiment(providerName, organismName);

        // Call the search service to obtain all experiments similar to the example experiment.
        List<Experiment> experimentList = searchService.search(exampleExperiment);
        System.out.println("Retrieved " + experimentList.size() + " experiments.");
        for (Experiment experiment : experimentList) {
            System.out.println("Experiment title: " + experiment.getTitle());
        }
    }

    // Create an example experiment with the desired array provider and organism.
    private Experiment createExampleExperiment(String providerName, String organismName) {
        Experiment exampleExperiment = new Experiment();

        // Set the array provider.
        Organization organization = new Organization();
        organization.setName(providerName);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        // Set the organism.
        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organismName);
        exampleExperiment.setOrganism(organismCriterion);

        // Set the assay type.
        // exampleExperiment.setAssayTypeEnum(AssayType.SNP);

        return exampleExperiment;
    }
}
