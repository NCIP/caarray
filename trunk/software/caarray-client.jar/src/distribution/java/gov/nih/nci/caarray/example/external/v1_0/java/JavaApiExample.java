//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.example.external.v1_0.java;

import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.AbstractSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.List;

/**
 * A simple class that connects to the remote Java API of a caArray server and retrieves and
 * prints a list of<code>QuantitationTypes</code>.
 */
@SuppressWarnings("PMD")
public class JavaApiExample {

    private static final String DEFAULT_SERVER = "array.nci.nih.gov";
    private static final int DEFAULT_JNDI_PORT = 8080;

    private String hostname = DEFAULT_SERVER;
    private int port = DEFAULT_JNDI_PORT;
    private CaArrayServer server;
    private SearchService searchService;
    private AbstractSearchApiUtils searchUtils;
    private DataService dataService;
    private DataApiUtils dataUtils;
    
    public static void main(String[] args) {
        JavaApiExample client = new JavaApiExample();
        if (args.length == 2) {
            client.hostname = args[0];
            client.port = Integer.parseInt(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage ApiClientTest [hostname port]");
            System.exit(1);
        }
        System.out.println("Using values: " + client.hostname + ":" + client.port);
        client.connectToServer();
        client.runExample();
    }

    public void connectToServer() {
        server = new CaArrayServer(hostname, port);
        try {
            server.connect();
            System.out.println("Successfully connected to server");
            searchService = server.getSearchService();
            searchUtils = new JavaSearchApiUtils(searchService);
            dataService = server.getDataService();
            dataUtils = new JavaDataApiUtils(dataService);
        } catch (ServerConnectionException e) {
            System.out.println("Couldn't connect to server: likely JNDI problem");
            e.printStackTrace(System.err);
            System.exit(1);
        }        
    }
    
    /**
     * Executes example Grid API client code.
     */
    public void runExample() {
        try {
            List<FileType> fileTypes = searchUtils.byExample(new ExampleSearchCriteria<FileType>(new FileType()))
                    .list();
            System.out.println("File Types: " + fileTypes);
        } catch (Throwable t) {
            System.out.println("Received exception: " + t);
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
