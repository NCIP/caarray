package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.domain.file.ArrayType;

import org.junit.Test;

public class ArrayDesignTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setType(ArrayType.EXON.name());
        assertEquals(ArrayType.EXON.name(), arrayDesign.getType());
        assertEquals(ArrayType.EXON, arrayDesign.getArrayType());
        arrayDesign.setType(null);
        assertNull(arrayDesign.getArrayType());
        assertNull(arrayDesign.getType());
        arrayDesign.setType("illegal value");
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
