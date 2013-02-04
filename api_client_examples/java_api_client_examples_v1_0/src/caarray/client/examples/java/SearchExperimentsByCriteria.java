//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.List;

/**
 * A client searching for experiments using various criteria via the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchExperimentsByCriteria {
    private static SearchService searchService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String PROVIDER_NAME = "Affymetrix";
    private static final String ORGANISM_NAME = "human";
    private static final String TISSUE_SITE_CATEGORY = "OrganismPart";
    private static final String TISSUE_SITE_VALUE = "Brain";
    private static final String PI_NAME = "Golub";
    private static final String ASSAY_TYPE = "Gene Expression";

    public static void main(String[] args) {
        SearchExperimentsByCriteria seeker = new SearchExperimentsByCriteria();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            seeker.doSearch(server);
        } catch (Throwable t) {
            System.out.println("Error during experiment search by criteria.");
            t.printStackTrace();
        }
    }

    public void doSearch( CaArrayServer server ) {
        try {
            searchService = server.getSearchService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            System.out.println("Searching for " + ORGANISM_NAME + " " + TISSUE_SITE_VALUE + " " + PROVIDER_NAME + " "
                    + ASSAY_TYPE + " experiments by " + PI_NAME + "...");
            search();
            System.out.println("");
        } catch (Throwable t) {
            System.out.println("Error during experiment search by criteria.");
            t.printStackTrace();
        }
    }

    private void search() throws InvalidInputException {
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();

        // Select array provider. (See LookUpEntities example client to see how to get list of all array providers.)
        ExampleSearchCriteria<ArrayProvider> providerCriteria = new ExampleSearchCriteria<ArrayProvider>();
        ArrayProvider exampleProvider = new ArrayProvider();
        exampleProvider.setName(PROVIDER_NAME);
        providerCriteria.setExample(exampleProvider);
        List<ArrayProvider> arrayProviders = searchService.searchByExample(providerCriteria, null).getResults();
        if (arrayProviders == null || arrayProviders.size() <= 0) {
            System.err.println("Could not find array provider called " + PROVIDER_NAME);
            return;
        }
        CaArrayEntityReference providerRef = arrayProviders.get(0).getReference();
        experimentSearchCriteria.setArrayProvider(providerRef);

        // Select organism. (See LookUpEntities example client to see how to get list of all organisms.)
        ExampleSearchCriteria<Organism> organismCriteria = new ExampleSearchCriteria<Organism>();
        Organism exampleOrganism = new Organism();
        exampleOrganism.setCommonName(ORGANISM_NAME);
        organismCriteria.setExample(exampleOrganism);
        List<Organism> organisms = searchService.searchByExample(organismCriteria, null).getResults();
        if (organisms == null || organisms.size() <= 0) {
            System.err.println("Could not find organism with common name = " + ORGANISM_NAME);
            return;
        }
        CaArrayEntityReference organismRef = organisms.get(0).getReference();
        experimentSearchCriteria.setOrganism(organismRef);

        // Select assay type. (See LookUpEntities example client to see how to get list of all assay types.)
        ExampleSearchCriteria<AssayType> assayTypeCriteria = new ExampleSearchCriteria<AssayType>();
        AssayType exampleAssayType = new AssayType();
        exampleAssayType.setName(ASSAY_TYPE);
        assayTypeCriteria.setExample(exampleAssayType);
        List<AssayType> assayTypes = searchService.searchByExample(assayTypeCriteria, null).getResults();
        if (assayTypes == null || assayTypes.size() <= 0) {
            System.err.println("Could not find assay type " + ASSAY_TYPE);
            return;
        }
        CaArrayEntityReference assayTypeRef = assayTypes.get(0).getReference();
        experimentSearchCriteria.setAssayType(assayTypeRef);

        // Select principal investigator.
        // Typically, the client application will search for all principal investigators, display the list of
        // PIs to the user and let the user pick one. But in this sample code, we are picking one.
        List<Person> investigators = searchService.getAllPrincipalInvestigators();
        for (Person investigator : investigators) {
            if (PI_NAME.equalsIgnoreCase(investigator.getLastName())) {
                experimentSearchCriteria.getPrincipalInvestigators().add(investigator.getReference());
                break;
            }
        }

        // Select tissue site.
        // Typically, the client application might search for all annotation values in the tissue site ("OrganismPart")
        // category, display the list of values to the user and let the user pick one. But in this sample code,
        // we are picking one ("Brain").
        AnnotationCriterion tissueSite = new AnnotationCriterion();
        tissueSite.setValue(TISSUE_SITE_VALUE);
        CaArrayEntityReference categoryRef = getCategoryReference(TISSUE_SITE_CATEGORY);
        tissueSite.setCategory(categoryRef);
        experimentSearchCriteria.getAnnotationCriterions().add(tissueSite);

        // Search for experiments that satisfy all of the above criteria.
        long startTime = System.currentTimeMillis();
        List<Experiment> experiments = (searchServiceHelper.experimentsByCriteria(experimentSearchCriteria)).list();
        long totalTime = System.currentTimeMillis() - startTime;
        if (experiments == null || experiments.size() <= 0) {
            System.out.println("No experiments found matching the requested criteria.");
        } else {
            System.out.println("Retrieved " + experiments.size() + " experiments in " + totalTime + " ms.");
        }
    }

    private CaArrayEntityReference getCategoryReference(String categoryName) throws InvalidInputException {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        List<Category> categories = searchService.searchByExample(criteria, null).getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
