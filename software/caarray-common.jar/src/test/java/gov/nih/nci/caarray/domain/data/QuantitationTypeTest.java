//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class QuantitationTypeTest {

    @Test
    @SuppressWarnings("PMD")
    public void testSetType() {
        QuantitationType quantitationType = new QuantitationType();
        quantitationType.setTypeClass(String.class);
        assertEquals(String.class, quantitationType.getTypeClass());
        try {
            quantitationType.setTypeClass(QuantitationTypeTest.class);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected;
        }
    }

}
