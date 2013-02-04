//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.api.external.v1_0.java;

import org.junit.BeforeClass;

import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

/**
 * @author dkokotov
 *
 */
public class AbstractExternalJavaApiTest extends AbstractApiTest {
    protected static CaArrayServer caArrayServer;
    
    @BeforeClass
    public static void connectToServer() throws ServerConnectionException {
        caArrayServer = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                .getServerJndiPort());
        caArrayServer.connect();
    }
}
