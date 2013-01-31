//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.example.external.v1_0.grid;


import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.util.List;

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
        client.url = "http://" + client.hostname + ":" + client.port + "/wsrf/services/cagrid/CaArraySvc_v1_0";
        System.out.println("Using URL: " + client.url);
        client.runExample();
    }

    /**
     * Does some examples of API invocation
     */
    public void runExample() {
        CaArraySvc_v1_0Client client;
        try {
            client = new CaArraySvc_v1_0Client(url);
            DataApiUtils dataUtils = new GridDataApiUtils(client);
            SearchApiUtils searchUtils = new GridSearchApiUtils(client);

            Organism exampleOrg = new Organism();
            exampleOrg.setCommonName("house");
            List<Organism> mouseOrgs = searchUtils.byExample(
                    new ExampleSearchCriteria<Organism>(exampleOrg, MatchMode.ANYWHERE)).list();
            System.out.println("Mouse organisms: " + mouseOrgs);
        } catch (Exception e) {
            System.err.println("Received Exception " + e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
