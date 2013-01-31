//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.nih.nci.caarray.domain.project.AssayType;

import org.junit.Test;

public class ArrayDesignTest {
    private static AssayType DUMMY_ASSAY_TYPE1 = new AssayType("Gene Expression");

    @Test
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        arrayDesign.setAssayTypes(assayTypes);
        assertEquals(DUMMY_ASSAY_TYPE1.getName(), arrayDesign.getAssayTypes().first().getName());
        assertEquals(DUMMY_ASSAY_TYPE1, arrayDesign.getAssayTypes().first());
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(new AssayType("illegal value"));
        arrayDesign.setAssayTypes(assayTypes);
    }

    @Test
    public void testSetArrayType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        arrayDesign.setAssayTypes(assayTypes);
        assertEquals(DUMMY_ASSAY_TYPE1.getName(), arrayDesign.getAssayTypes().first().getName());
        assertEquals(DUMMY_ASSAY_TYPE1, arrayDesign.getAssayTypes().first());
    }
}
