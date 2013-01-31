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
        assertFalse(FileType.AFFYMETRIX_CEL.isArrayDesign());
    }

    @Test
    public final void testIsDerivedArrayData() {
        assertTrue(FileType.AFFYMETRIX_CHP.isDerivedArrayData());
        assertTrue(FileType.ILLUMINA_DATA_CSV.isDerivedArrayData());
        assertTrue(FileType.GENEPIX_GPR.isDerivedArrayData());
        assertFalse(FileType.AFFYMETRIX_CEL.isDerivedArrayData());
    }

    @Test
    public final void testIsRawArrayData() {
        assertTrue(FileType.AFFYMETRIX_CEL.isRawArrayData());
        assertFalse(FileType.AFFYMETRIX_CHP.isRawArrayData());
    }

    @Test
    public final void testIsArrayData() {
        assertFalse(FileType.AFFYMETRIX_CDF.isArrayData());
        assertTrue(FileType.AFFYMETRIX_CEL.isArrayData());
        assertTrue(FileType.AFFYMETRIX_CHP.isArrayData());
    }

}
