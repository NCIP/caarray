//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.example.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.DeserializationException;

/**
 * A simple class that connects to the remote Java API of a caArray server and retrieves and
 * prints a list of<code>QuantitationTypes</code>.
 */
@SuppressWarnings("PMD")
public class GridApiEnumerateExperiments {

    private static final String DEFAULT_SERVER = "array.nci.nih.gov";
    private static final int DEFAULT_GRID_SERVICE_PORT = 80;

    private String hostname = DEFAULT_SERVER;
    private int port = DEFAULT_GRID_SERVICE_PORT;
    private String url;

    public static void main(String[] args) {
    	GridApiEnumerateExperiments client = new GridApiEnumerateExperiments();
        if (args.length == 2) {
            client.hostname = args[0];
            client.port = Integer.parseInt(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage ApiClientTest [hostname port]");
            System.exit(1);
        }
        client.url = "http://" + client.hostname + ":" + client.port + "/wsrf/services/cagrid/CaArraySvc_v1_0";
        System.out.println("\n" + "client.url = " + client.url+ "\n");
        client.runTest();
    }

    /**
     * Downloads data using the caArray Remote Java API.
     */
    public void runTest() {
        CaArraySvc_v1_0Client client;
        try {
            client = new CaArraySvc_v1_0Client(url);
            EnumerationResponseContainer orgEnum = client.enumerateOrganisms();
  
            
            ClientEnumIterator iter = 
            	EnumerationResponseHelper.createClientIterator
            	(orgEnum,CaArraySvc_v1_0Client.class.getResourceAsStream("client-config.wsdd"));
            IterationConstraints ic = new IterationConstraints(10, -1, null);
            iter.setIterationConstraints(ic);
            int i = 0;
            while (iter.hasNext()) {
            	try {
                    
            		SOAPElement elem = (SOAPElement) iter.next();
                    if (elem != null) {
                    	i = i + 1;
                    	java.lang.Object o = ObjectDeserializer.toObject(elem, Organism.class);
                    	Organism myOrganism = (Organism) o;
                        System.out.println("i = " + i + " Organism CommonName = " + myOrganism.getCommonName());
                        System.out.println("i = " + i + " Organism ScientificName = " + myOrganism.getScientificName());
                        System.out.println("i = " + i + " Organism Accession = " + myOrganism.getAccession());
                        System.out.println("i = " + i + " Organism Url = " + myOrganism.getUrl());
                        System.out.println("i = " + i + " Organism Value = " + myOrganism.getValue());
                        System.out.println("i = " + i + " Organism ID = " + myOrganism.getId());
                        
                        TermSource myTermSource = myOrganism.getTermSource();
                        System.out.println("i = " + i + " Organism TermSource Name = " + myTermSource.getName());
                        System.out.println("i = " + i + " Organism TermSource Url = " + myTermSource.getUrl());
                        System.out.println("i = " + i + " Organism TermSource ID = " + myTermSource.getId());
                        System.out.println("\n");
                        
                        }
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            
        } catch (MalformedURIException e) {
            System.err.println("Received MalformedURIException");
            e.printStackTrace(System.err);
            System.exit(1);
        } catch (RemoteException e) {
            System.err.println("Received RemoteException");
            e.printStackTrace(System.err);
            System.exit(1);
        } catch (DeserializationException e) {
            System.err.println("Received DeserializationException");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
