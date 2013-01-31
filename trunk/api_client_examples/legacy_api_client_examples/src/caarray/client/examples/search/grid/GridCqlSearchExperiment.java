//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.search.grid;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import caarray.client.examples.BaseProperties;

import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * A client searching for experiments using CQL through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridCqlSearchExperiment {
    private static final String ARRAY_DESIGN_NAME = "Test3";

    public static void main(String[] args) {
        GridCqlSearchExperiment gridClient = new GridCqlSearchExperiment();
        try {
            CaArraySvcClient client = new CaArraySvcClient(BaseProperties.getGridServiceUrl());
            System.out
                    .println("Grid-CQL-Searching for experiments using the array design " + ARRAY_DESIGN_NAME + "...");
            gridClient.searchExperiments(client, ARRAY_DESIGN_NAME);
        } catch (RemoteException e) {
            System.out.println("Remote server threw an exception.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void searchExperiments(CaArraySvcClient client, String arrayDesignName) throws RemoteException {
        CQLQuery cqlQuery = createCqlQuery(arrayDesignName);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        parseResults(cqlResults);
    }

    private CQLQuery createCqlQuery(String arrayDesignName) {
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
