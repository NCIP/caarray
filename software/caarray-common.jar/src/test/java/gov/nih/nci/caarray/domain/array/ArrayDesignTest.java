//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.domain.project.AssayType;

import org.junit.Test;

public class ArrayDesignTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setAssayType(AssayType.EXON.name());
        assertEquals(AssayType.EXON.getValue(), arrayDesign.getAssayType());
        assertEquals(AssayType.EXON, arrayDesign.getAssayTypeEnum());
        arrayDesign.setAssayType(null);
        assertNull(arrayDesign.getAssayTypeEnum());
        assertNull(arrayDesign.getAssayType());
        arrayDesign.setAssayType("illegal value");
    }

    @Test
    public void testSetArrayType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setAssayTypeEnum(AssayType.EXON);
        assertEquals(AssayType.EXON.getValue(), arrayDesign.getAssayType());
        assertEquals(AssayType.EXON, arrayDesign.getAssayTypeEnum());
        arrayDesign.setAssayTypeEnum(null);
        assertNull(arrayDesign.getAssayTypeEnum());
        assertNull(arrayDesign.getAssayType());
    }

}
