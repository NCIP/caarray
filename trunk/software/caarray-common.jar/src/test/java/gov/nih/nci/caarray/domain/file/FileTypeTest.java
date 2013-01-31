//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileTypeTest {

    @Test
    public final void testIsArrayDesign() {
        assertTrue(FileType.AFFYMETRIX_CDF.isArrayDesign());
        assertTrue(FileType.ILLUMINA_DESIGN_CSV.isArrayDesign());
        assertTrue(FileType.GENEPIX_GAL.isArrayDesign());
        assertTrue(FileType.GEO_GPL.isArrayDesign());
        assertFalse(FileType.AFFYMETRIX_CEL.isArrayDesign());
        assertFalse(FileType.GEO_GSM.isArrayDesign());
        assertFalse(FileType.GEO_SOFT.isArrayDesign());
        assertFalse(FileType.SCANARRAY_CSV.isArrayDesign());
    }

    @Test
    public final void testIsDerivedArrayData() {
        assertTrue(FileType.AFFYMETRIX_CHP.isDerivedArrayData());
        assertTrue(FileType.ILLUMINA_DATA_CSV.isDerivedArrayData());
        assertTrue(FileType.GENEPIX_GPR.isDerivedArrayData());
        assertFalse(FileType.AFFYMETRIX_CEL.isDerivedArrayData());
        assertFalse(FileType.GEO_GSM.isDerivedArrayData());
        assertFalse(FileType.GEO_SOFT.isDerivedArrayData());
        assertFalse(FileType.SCANARRAY_CSV.isDerivedArrayData());
    }

    @Test
    public final void testIsRawArrayData() {
        assertTrue(FileType.AFFYMETRIX_CEL.isRawArrayData());
        assertFalse(FileType.AFFYMETRIX_CHP.isRawArrayData());
        assertTrue(FileType.GEO_GSM.isRawArrayData());
        assertTrue(FileType.GEO_SOFT.isRawArrayData());
        assertTrue(FileType.SCANARRAY_CSV.isRawArrayData());
    }

    @Test
    public final void testIsArrayData() {
        assertFalse(FileType.AFFYMETRIX_CDF.isArrayData());
        assertTrue(FileType.AFFYMETRIX_CEL.isArrayData());
        assertTrue(FileType.AFFYMETRIX_CHP.isArrayData());
        assertTrue(FileType.GEO_GSM.isArrayData());
        assertTrue(FileType.GEO_SOFT.isArrayData());
        assertTrue(FileType.SCANARRAY_CSV.isArrayData());
    }

}
