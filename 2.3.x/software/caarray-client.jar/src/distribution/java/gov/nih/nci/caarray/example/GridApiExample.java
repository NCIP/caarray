//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.example;

import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;

/**
 * A simple class that connects to the remote Java API of a caArray server and retrieves and
 * prints a list of<code>QuantitationTypes</code>.
 */
@SuppressWarnings("PMD")
public class GridApiExample {

    private static final String DEFAULT_SERVER = "array.nci.nih.gov";
    private static final int DEFAULT_GRID_SERVICE_PORT = 80;

    private String hostname = DEFAULT_SERVER;
    private int port = DEFAULT_GRID_SERVICE_PORT;
    private String url;

    public static void main(String[] args) {
        GridApiExample client = new GridApiExample();
        if (args.length == 2) {
            client.hostname = args[0];
            client.port = Integer.parseInt(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage ApiClientTest [hostname port]");
            System.exit(1);
        }
        client.url = "http://" + client.hostname + ":" + client.port + "/wsrf/services/cagrid/CaArraySvc";
        System.out.println("Using URL: " + client.url);
        client.runTest();
    }

    /**
     * Downloads data using the caArray Remote Java API.
     */
    public void runTest() {
        CaArraySvcClient client;
        try {
            client = new CaArraySvcClient(url);
            CQLQuery cqlQuery = new CQLQuery();
            Object target = new Object();
            cqlQuery.setTarget(target);
            target.setName(QuantitationType.class.getName());
            CQLQueryResults results = client.query(cqlQuery);
            CQLQueryResultsIterator iterator = new CQLQueryResultsIterator(results,
                    GridApiExample.class.getResourceAsStream("/client-config.wsdd"));
            while (iterator.hasNext()) {
                QuantitationType type = (QuantitationType) iterator.next();
                System.out.println(type);
            }
            System.out.println("Successfully ran query");
        } catch (MalformedURIException e) {
            System.err.println("Received MalformedURIException");
            e.printStackTrace(System.err);
            System.exit(1);
        } catch (RemoteException e) {
            System.err.println("Received RemoteException");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
