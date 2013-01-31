//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class SerializationHelperUtilityTest {

    @Test
    public void testSerialize() {
        String[] stringValues = new String[]{"string1", "string2", "string3" };
        byte[] bytes = SerializationHelperUtility.serialize(stringValues);
        String[] retrievedStringValues = (String[]) SerializationHelperUtility.deserialize(bytes);
        assertEquals("string1", retrievedStringValues[0]);
        assertEquals("string2", retrievedStringValues[1]);
        assertEquals("string3", retrievedStringValues[2]);
        stringValues = null;
        bytes = SerializationHelperUtility.serialize(stringValues);
        retrievedStringValues = (String[]) SerializationHelperUtility.deserialize(bytes);
        assertNull(retrievedStringValues);
    }

}
