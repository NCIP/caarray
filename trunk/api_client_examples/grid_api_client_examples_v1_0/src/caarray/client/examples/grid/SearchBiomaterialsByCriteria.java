//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;
import java.util.List;

/**
 * A client searching for biomaterials using various criteria via the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchBiomaterialsByCriteria {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXTERNAL_ID = "API_TEST_EXTERNAL_ID_01";

    public static void main(String[] args) {
        SearchBiomaterialsByCriteria seeker = new SearchBiomaterialsByCriteria();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Searching for biomaterials with external ID " + EXTERNAL_ID + "...");
            seeker.search();
        } catch (Throwable t) {
            System.out.println("Error during biomaterial search by criteria.");
            t.printStackTrace();
        }
    }

    private void search() throws RemoteException, InvalidInputException {
        BiomaterialSearchCriteria biomaterialSearchCriteria = new BiomaterialSearchCriteria();

        // Set external ID.
        biomaterialSearchCriteria.getExternalIds().add(EXTERNAL_ID);

        // Select tissue site.
        AnnotationCriterion tissueSite = new AnnotationCriterion();
        tissueSite.setValue("Brain");
        CaArrayEntityReference categoryRef = getCategoryReference("OrganismPart");
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

    private CaArrayEntityReference getCategoryReference(String categoryName) throws RemoteException {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        List<Category> categories = (client.searchByExample(criteria, null)).getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
