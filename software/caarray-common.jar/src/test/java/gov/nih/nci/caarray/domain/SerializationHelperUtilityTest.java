//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.util.CaArrayUtils;

import org.junit.Test;

public class SerializationHelperUtilityTest {

    @Test
    public void testSerialize() {
        String[] stringValues = new String[]{"string1", "string2", "string3" };
        byte[] bytes = CaArrayUtils.serialize(stringValues);
        String[] retrievedStringValues = (String[]) CaArrayUtils.deserialize(bytes);
        assertEquals("string1", retrievedStringValues[0]);
        assertEquals("string2", retrievedStringValues[1]);
        assertEquals("string3", retrievedStringValues[2]);
        stringValues = null;
        bytes = CaArrayUtils.serialize(stringValues);
        retrievedStringValues = (String[]) CaArrayUtils.deserialize(bytes);
        assertNull(retrievedStringValues);
    }

}
