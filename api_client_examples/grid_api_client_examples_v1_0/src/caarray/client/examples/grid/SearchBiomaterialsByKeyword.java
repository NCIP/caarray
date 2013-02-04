//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;
import java.util.List;

/**
 * A client searching for biomaterials by keyword using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchBiomaterialsByKeyword {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String KEYPHRASE = "TCGA";

    public static void main(String[] args) {
        SearchBiomaterialsByKeyword seeker = new SearchBiomaterialsByKeyword();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Searching for sources and samples by keyword " + KEYPHRASE + "...");
            seeker.search();
        } catch (Throwable t) {
            System.out.println("Error during biomaterial search by keyword.");
            t.printStackTrace();
        }
    }

    private void search() throws RemoteException, InvalidInputException {
        BiomaterialKeywordSearchCriteria criteria = new BiomaterialKeywordSearchCriteria();
        criteria.setKeyword(KEYPHRASE);
        criteria.getTypes().add(BiomaterialType.SAMPLE);
        criteria.getTypes().add(BiomaterialType.SOURCE);
        long startTime = System.currentTimeMillis();
        List<Biomaterial> biomaterials = (searchServiceHelper.biomaterialsByKeyword(criteria)).list();
        long totalTime = System.currentTimeMillis() - startTime;
        if (biomaterials == null || biomaterials.size() <= 0) {
            System.err.println("No biomaterials found.");
            return;
        }
        System.out.println("Found " + biomaterials.size() + " biomaterials in " + totalTime + " ms.");
        System.out.println("Name\tType\tTissue Site\tDisease State");
        for (Biomaterial biomaterial : biomaterials) {
            printBiomaterialDetails(biomaterial);
        }
    }

    private void printBiomaterialDetails(Biomaterial biomaterial) throws RemoteException {
        // Print basic biomaterial attributes.
        System.out.print(biomaterial.getName() + "\t");
        System.out.print(biomaterial.getType() + "\t");
        Term term = biomaterial.getTissueSite() == null ? null : biomaterial.getTissueSite().getTerm();
        String termVal = term == null ? null : term.getValue();
        System.out.print(termVal + "\t");
        term = biomaterial.getDiseaseState() == null ? null : biomaterial.getDiseaseState().getTerm();
        termVal = term == null ? null : term.getValue();
        System.out.println(termVal);
    }
}
