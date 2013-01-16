//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.search.java;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.util.Iterator;
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
//        List<Experiment> experimentList = searchService.search(exampleExperiment);
//        System.out.println("Retrieved " + experimentList.size() + " experiments.");
//        for (Experiment experiment : experimentList) {
//            System.out.println("Experiment title: " + experiment.getTitle());
//        }
        
        CQLQuery cqlQuery = createCqlQueryArrayDesign("Test3");
        List<Experiment> experimentList = (List<Experiment>) searchService.search(cqlQuery);
        System.out.println("Retrieved " + experimentList.size() + " experiments.");
        for (Experiment experiment : experimentList) {
            System.out.println("Experiment title: " + experiment.getTitle());
        }

        cqlQuery = createCqlQueryTerm("disease_state_design");
        experimentList = (List<Experiment>) searchService.search(cqlQuery);
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
    
    private CQLQuery createCqlQueryArrayDesign(String arrayDesignName) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association arrayDesignAssociation = new Association();
        arrayDesignAssociation.setName("gov.nih.nci.caarray.domain.array.ArrayDesign");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("name");
        nameAttribute.setValue(arrayDesignName);
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        arrayDesignAssociation.setAttribute(nameAttribute);
        arrayDesignAssociation.setRoleName("arrayDesigns");

        target.setAssociation(arrayDesignAssociation);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private CQLQuery createCqlQueryTerm(String ds) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association arrayDesignAssociation = new Association();
        arrayDesignAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("value");
        nameAttribute.setValue(ds);
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        arrayDesignAssociation.setAttribute(nameAttribute);
        arrayDesignAssociation.setRoleName("experimentDesignTypes");

        target.setAssociation(arrayDesignAssociation);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    // Deserialize the results and retrieve the matching experiments.
    private void parseResults(CQLQueryResults cqlResults) {
        if (cqlResults.getObjectResult() == null) {
            System.out.println("Result was null.");
            return;
        }
        System.out.println("Retrieved " + cqlResults.getObjectResult().length + " experiments.");
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            Experiment retrievedExperiment = (Experiment) (iter.next());
            System.out.println("Experiment title: " + retrievedExperiment.getTitle());
        }
    }
}
