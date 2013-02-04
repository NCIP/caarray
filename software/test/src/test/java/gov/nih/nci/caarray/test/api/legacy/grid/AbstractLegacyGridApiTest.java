//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.api.legacy.grid;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;

import java.rmi.RemoteException;

import org.junit.BeforeClass;

/**
 * @author dkokotov
 *
 */
public class AbstractLegacyGridApiTest extends AbstractApiTest {
    private static final String SERVICE_NAME = "CaArraySvc";
    
    protected static CaArraySvcClient gridClient;
    
    @BeforeClass
    public static void connectToServer() {
        try {
            gridClient = new CaArraySvcClient(TestProperties.getBaseGridServiceUrl() + SERVICE_NAME);
        } catch (RemoteException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Remote exception: " + e + "\nTrace: " + trace);
            assertTrue("Remote exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        } 
    }
}
