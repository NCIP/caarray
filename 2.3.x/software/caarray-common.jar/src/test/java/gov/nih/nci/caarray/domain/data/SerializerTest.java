//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;

public class SerializerTest {
    
    private static final int[] TEST_ARRAY = new int[] {1, 2, 3};

    @Test
    public void testGetValue() {
        Serializer serializer = new Serializer();
        int[] values = (int[]) serializer.getValue();
        assertNull(values);
        serializer.setValue(TEST_ARRAY);
        checkValues(serializer.getValue());
        serializer.getSerializedValues();
        checkValues(serializer.getValue());
    }

    private void checkValues(Serializable value) {
        int[] values = (int[]) value;
        assertEquals(values.length, TEST_ARRAY.length);
        for (int i = 0; i < TEST_ARRAY.length; i++) {
            assertEquals(TEST_ARRAY[i], values[i]);
        }
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.domain.data.Serializer#getSerializedValues()}.
     */
    @Test
    public void testGetSerializedValue() {
        Serializer serializer = new Serializer();
        serializer.setValue(TEST_ARRAY);
        assertNotNull(serializer.getSerializedValues());
    }

}
