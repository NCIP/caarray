//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.download_array_design.grid;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import caarray.client.examples.BaseProperties;

import java.rmi.RemoteException;

/**
 * A client downloading array design details through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridArrayDesignDownload {
    private static final String ARRAY_DESIGN_NAME = BaseProperties.AFFYMETRIX_DESIGN;

    public static void main(String[] args) {
        GridArrayDesignDownload gridClient = new GridArrayDesignDownload();
        try {
            CaArraySvcClient client = new CaArraySvcClient(BaseProperties.getGridServiceUrl());
            System.out.println("Grid-Downloading array design details from " + ARRAY_DESIGN_NAME + "...");
            gridClient.downloadDetails(client, ARRAY_DESIGN_NAME);
        } catch (RemoteException e) {
            System.out.println("Remote server threw an exception.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void downloadDetails(CaArraySvcClient client, String arrayDesignName) throws RemoteException {
        ArrayDesign arrayDesign = lookupArrayDesign(client, arrayDesignName);
        if (arrayDesign == null) {
            System.out.println("Error: Could not find array design " + arrayDesignName);
            return;
        }
        ArrayDesignDetails details = client.getDesignDetails(arrayDesign);
        if (details != null) {
            int numFeatures = details.getFeatures().size();
            int numProbeGroups = details.getProbeGroups().size();
            int numProbes = details.getProbes().size();
            int numLogicalProbes = details.getLogicalProbes().size();
            System.out.println("Retrieved " + numFeatures + " features, " + numProbeGroups + " probe group(s), "
                    + numProbes + " physical probes and " + numLogicalProbes + " logical probes.");
        } else {
            System.out.println("Error: Retrieved null array design details.");
        }
    }

    private ArrayDesign lookupArrayDesign(CaArraySvcClient client, String arrayDesignName) throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.array.ArrayDesign");
        Attribute arrayDesignNameAttribute = new Attribute();
        arrayDesignNameAttribute.setName("name");
        arrayDesignNameAttribute.setValue(arrayDesignName);
        arrayDesignNameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(arrayDesignNameAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return null;
        }
        return (ArrayDesign) iter.next();
    }
}
