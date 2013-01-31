//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.junit.Test;

@SuppressWarnings("PMD")
public class CaArrayFileTest {

    @Test
    public void testSetFileStatus() {
        CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        assertEquals("IMPORTED", file.getStatus());
        assertEquals(true, file.isDeletable());

        file.setFileStatus(FileStatus.UPLOADED);
        assertEquals(FileStatus.UPLOADED, file.getFileStatus());
        assertEquals("UPLOADED", file.getStatus());
        assertEquals(true, file.isValidatable());

        file.setFileStatus(FileStatus.VALIDATED);
        assertEquals(FileStatus.VALIDATED, file.getFileStatus());
        assertEquals("VALIDATED", file.getStatus());
        assertEquals(true, file.isImportable());

        file.setFileStatus(null);
        assertEquals(null, file.getFileStatus());
        assertEquals(null, file.getStatus());
    }

    @Test
    public void testSetStatus() {
        CaArrayFile file = new CaArrayFile();
        file.setStatus("IMPORTED");
        assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        assertEquals("IMPORTED", file.getStatus());
        file.setStatus(null);
        assertEquals(null, file.getFileStatus());
        assertEquals(null, file.getStatus());
        try {
            file.setStatus("ILLEGAL STATUS");
            fail("Shouldn't be able to set status not in FileStatus");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIsMatch() {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        assertTrue(caArrayFile.isMatch(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        assertFalse(caArrayFile.isMatch(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
    }

}
