//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.example.legacy.java;

import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

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
        client.runExample();
    }

    /**
     * Executes example Java API client code.
     */
    public void runExample() {
        CaArrayServer server = new CaArrayServer(hostname, port);
        try {
            server.connect();
            System.out.println("Successfully connected to server");
        } catch (ServerConnectionException e) {
            System.out.println("Couldn't connect to server: likely JNDI problem");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        try {
            CaArraySearchService searchService = server.getSearchService();

            CQLQuery cqlQuery = new CQLQuery();
            Object target = new Object();
            cqlQuery.setTarget(target);
            target.setName(AbstractFactorValue.class.getName());
            List<AbstractFactorValue> vals = (List<AbstractFactorValue>) searchService.search(cqlQuery);
            for (AbstractFactorValue val : vals) {
                System.out.println("Abstract Factor Value " + ToStringBuilder.reflectionToString(val));
            }
        } catch (Throwable t) {
            System.out.println("Got exception: " + t);
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
