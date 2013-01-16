//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.List;

/**
 * A client searching for biomaterials using various criteria via the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchBiomaterialsByCriteria {
    private static SearchService searchService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXTERNAL_ID = "API_TEST_EXTERNAL_ID_01";
    private static final String TISSUE_SITE_CATEGORY = "OrganismPart";
    private static final String TISSUE_SITE_VALUE = "Brain";

    public static void main(String[] args) {
        SearchBiomaterialsByCriteria seeker = new SearchBiomaterialsByCriteria();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            System.out.println("Searching for biomaterials with external ID " + EXTERNAL_ID + "...");
            seeker.search();
        } catch (Throwable t) {
            System.out.println("Error during biomaterial search by criteria.");
            t.printStackTrace();
        }
    }

    private void search() throws InvalidInputException {
        BiomaterialSearchCriteria biomaterialSearchCriteria = new BiomaterialSearchCriteria();

        // Set external ID.
        biomaterialSearchCriteria.getExternalIds().add(EXTERNAL_ID);

        // Select tissue site.
        AnnotationCriterion tissueSite = new AnnotationCriterion();
        tissueSite.setValue(TISSUE_SITE_VALUE);
        CaArrayEntityReference categoryRef = getCategoryReference(TISSUE_SITE_CATEGORY);
        tissueSite.setCategory(categoryRef);
        biomaterialSearchCriteria.getAnnotationCriterions().add(tissueSite);

        // Search for biomaterials that satisfy all of the above criteria.
        long startTime = System.currentTimeMillis();
        List<Biomaterial> biomaterials = (searchServiceHelper.biomaterialsByCriteria(biomaterialSearchCriteria)).list();
        long totalTime = System.currentTimeMillis() - startTime;
        if (biomaterials == null || biomaterials.size() <= 0) {
            System.out.println("No biomaterials found matching the requested criteria.");
        } else {
            System.out.println("Retrieved " + biomaterials.size() + " biomaterials in " + totalTime + " ms.");
        }
    }

    private CaArrayEntityReference getCategoryReference(String categoryName) throws InvalidInputException {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        SearchResult<Category> results = searchService.searchByExample(criteria, null);
        List<Category> categories = results.getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
