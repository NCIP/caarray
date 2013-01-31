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
import caarray.client.examples.BaseProperties;

import java.util.List;

/**
 * A client searching for experiments using search-by-example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiLoginAndSearch {
    private static final String USER_NAME = "caarrayGuest";
    private static final String PASSWD = "caarrayGuest";
    private static final String PROVIDER_NAME = "Affymetrix";
    private static final String ORGANISM_NAME = "house mouse";

    public static void main(String[] args) {
        ApiLoginAndSearch client = new ApiLoginAndSearch();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            CaArraySearchService searchService = null;
            System.out.println("Searching for " + ORGANISM_NAME + " experiments run on the " + PROVIDER_NAME
                    + " platform.");

            System.out.println("Searching as an anonymous user...");
            server.connect();
            searchService = server.getSearchService();
            client.searchExperiments(searchService, PROVIDER_NAME, ORGANISM_NAME);

            System.out.println("Searching as credentialled user " + USER_NAME + "...");
            server.connect(USER_NAME, PASSWD);
            searchService = server.getSearchService();
            client.searchExperiments(searchService, PROVIDER_NAME, ORGANISM_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void searchExperiments(CaArraySearchService searchService, String providerName, String organismName) {
        Experiment exampleExperiment = createExampleExperiment(providerName, organismName);
        List<Experiment> experimentList = searchService.search(exampleExperiment);
        System.out.println("Retrieved " + experimentList.size() + " experiments.");
    }

    private Experiment createExampleExperiment(String providerName, String organismName) {
        Experiment exampleExperiment = new Experiment();

        Organization organization = new Organization();
        organization.setName(providerName);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organismName);
        exampleExperiment.setOrganism(organismCriterion);

        return exampleExperiment;
    }
}
