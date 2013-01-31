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

public class CaArrayFileSetTest {

    @Test
    public void testGetFile() {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF.getName());
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        fileSet.add(caArrayFile2);
        fileSet.add(new CaArrayFile());
        assertEquals(caArrayFile, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        assertEquals(caArrayFile2, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        assertNull(fileSet.getFile(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
    }

    @Test
    public void testGetStatus() {
        CaArrayFile caArrayFile = new CaArrayFile();
        CaArrayFile caArrayFile2 = new CaArrayFile();
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        fileSet.add(caArrayFile2);

        caArrayFile.setFileStatus(FileStatus.IMPORTED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertEquals(FileStatus.IMPORTED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTING);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTING, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATING);
        assertEquals(FileStatus.VALIDATING, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED);
        assertEquals(FileStatus.VALIDATED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        assertEquals(FileStatus.VALIDATED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        assertEquals(FileStatus.VALIDATED_NOT_PARSED, fileSet.getStatus());
    }

}
