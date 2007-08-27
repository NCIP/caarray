package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.domain.file.ArrayType;

import org.junit.Test;

public class ArrayDesignTest {

    @Test
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setType(ArrayType.EXON.name());
        assertEquals(ArrayType.EXON.name(), arrayDesign.getType());
        assertEquals(ArrayType.EXON, arrayDesign.getArrayType());
        arrayDesign.setType(null);
        assertNull(arrayDesign.getArrayType());
        assertNull(arrayDesign.getType());
        try {
            arrayDesign.setType("illegal value");
            fail("Illegal values shouldn't be allowed");
        } catch (IllegalArgumentException e) { // NOPMD
            // expected
        }
    }

    @Test
    public void testSetArrayType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setArrayType(ArrayType.EXON);
        assertEquals(ArrayType.EXON.name(), arrayDesign.getType());
        assertEquals(ArrayType.EXON, arrayDesign.getArrayType());
        arrayDesign.setArrayType(null);
        assertNull(arrayDesign.getArrayType());
        assertNull(arrayDesign.getType());
    }

}
