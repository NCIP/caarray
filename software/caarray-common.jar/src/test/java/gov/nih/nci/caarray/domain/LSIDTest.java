//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class LSIDTest {

    /**
     * Test method for {@link gov.nih.nci.caarray.domain.LSID#toString()}.
     */
    @Test
    public void testLsid() {
        String authority = "5amsolutions.com";
        String namespace = "testnamespace";
        String objectId = "1234";
        LSID lsid = new LSID(authority, namespace, objectId);
        assertEquals(authority, lsid.getAuthority());
        assertEquals(namespace, lsid.getNamespace());
        assertEquals(objectId, lsid.getObjectId());
        assertEquals("URN:LSID:5amsolutions.com:testnamespace:1234", lsid.toString());
    }

}
