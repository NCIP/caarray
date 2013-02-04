//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.api.external.v1_0.grid;

import static org.junit.Assert.fail;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.rmi.RemoteException;

import org.junit.BeforeClass;

/**
 * @author dkokotov
 *
 */
public class AbstractExternalGridApiTest extends AbstractApiTest {
    private static final String SERVICE_NAME = "CaArraySvc_v1_0";
    
    protected static CaArraySvc_v1_0Client gridClient;
    
    @BeforeClass
    public static void connectToServer() {
        try {
        	String serviceUrl = TestProperties.getBaseGridServiceUrl() + SERVICE_NAME;
        	System.out.println("serviceUrl = " + serviceUrl + "=");
            gridClient = new CaArraySvc_v1_0Client(serviceUrl);
        } catch (RemoteException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Remote exception: " + e + "\nTrace: " + trace);
            fail("Remote exception: " + e);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            fail("Throwable: " + t);
        } 
    }
}
