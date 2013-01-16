//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class DesignElementTypeTest {

    @Test
    public final void testGetByValue() {
        assertEquals(DesignElementType.FEATURE, DesignElementType.getByValue(DesignElementType.FEATURE.getValue()));
        assertEquals(null, DesignElementType.getByValue(null));
        assertEquals(DesignElementType.LOGICAL_PROBE, DesignElementType.getByValue(DesignElementType.LOGICAL_PROBE.getValue()));
        assertEquals(DesignElementType.PHYSICAL_PROBE, DesignElementType.getByValue(DesignElementType.PHYSICAL_PROBE.getValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGetByValueIllegalArgument() {
        DesignElementType.getByValue("no such value");
    }

}
