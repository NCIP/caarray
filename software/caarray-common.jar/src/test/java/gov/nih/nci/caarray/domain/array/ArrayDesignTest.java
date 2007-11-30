package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.domain.project.AssayType;

import org.junit.Test;

public class ArrayDesignTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setType(AssayType.EXON.name());
        assertEquals(AssayType.EXON.name(), arrayDesign.getType());
        assertEquals(AssayType.EXON, arrayDesign.getAssayType());
        arrayDesign.setType(null);
        assertNull(arrayDesign.getAssayType());
        assertNull(arrayDesign.getType());
        arrayDesign.setType("illegal value");
    }

    @Test
    public void testSetArrayType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setAssayType(AssayType.EXON);
        assertEquals(AssayType.EXON.name(), arrayDesign.getType());
        assertEquals(AssayType.EXON, arrayDesign.getAssayType());
        arrayDesign.setAssayType(null);
        assertNull(arrayDesign.getAssayType());
        assertNull(arrayDesign.getType());
    }

}
