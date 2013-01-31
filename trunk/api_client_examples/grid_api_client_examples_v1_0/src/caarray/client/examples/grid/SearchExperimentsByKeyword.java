//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;
import java.util.List;

/**
 * An LSDBrowser-like client searching for experiments by keyword using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchExperimentsByKeyword {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String KEYPHRASE = "Glioblastoma";

    public static void main(String[] args) {
        SearchExperimentsByKeyword seeker = new SearchExperimentsByKeyword();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Searching for experiments by keyword " + KEYPHRASE + "...");
            seeker.seek();
        } catch (Throwable t) {
            System.out.println("Error during experiment search by keyword.");
            t.printStackTrace();
        }
    }

    private void seek() throws RemoteException, InvalidInputException {
        KeywordSearchCriteria criteria = new KeywordSearchCriteria();
        criteria.setKeyword(KEYPHRASE);
        long startTime = System.currentTimeMillis();
        List<Experiment> experiments = (searchServiceHelper.experimentsByKeyword(criteria)).list();
        long totalTime = System.currentTimeMillis() - startTime;
        if (experiments == null || experiments.size() <= 0) {
            System.err.println("No experiments found.");
            return;
        }
        System.out.println("Found " + experiments.size() + " experiments in " + totalTime + " ms.");
        System.out.println("Public Identifier\tTitle\tAssay Type\tOrganism\tNumber of Samples\tDisease States");
        for (Experiment experiment : experiments) {
            printExperimentDetails(experiment);
        }
    }

    private void printExperimentDetails(Experiment experiment) throws RemoteException {
        // Print basic experiment attributes.
        System.out.print(experiment.getPublicIdentifier() + "\t");
        System.out.print(experiment.getTitle() + "\t");
        System.out.print(experiment.getAssayTypes().iterator().next().getName() + "\t");
        System.out.print(experiment.getOrganism().getScientificName() + "\t");

        // Retrieve and print number of samples.
        BiomaterialSearchCriteria criteria = new BiomaterialSearchCriteria();
        criteria.setExperiment(experiment.getReference());
        criteria.getTypes().add(BiomaterialType.SAMPLE);
        List<Biomaterial> biomaterials = client.searchForBiomaterials(criteria, null).getResults();
        int numSamples = biomaterials == null ? 0 : biomaterials.size();
        System.out.print(numSamples + "\t");

        // Retrieve and print disease states.
//        Category diseaseStateCategory = new Category();
//        diseaseStateCategory.setName("DiseaseState");
//        Set<Term> diseaseStates = client.getAnnotationValues(experimentRef, diseaseStateCategory);
//        for (Term diseaseState : diseaseStates) {
//            System.out.print(diseaseState.getValue() + " ");
//        }
        System.out.println();
    }
}
