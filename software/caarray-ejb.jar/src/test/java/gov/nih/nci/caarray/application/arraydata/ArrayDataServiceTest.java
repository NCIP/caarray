//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import org.junit.Test;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest extends AbstractArrayDataServiceTest {
    @Test
    public void testInitialize() {
        this.arrayDataService.initialize();
        assertTrue(this.daoFactoryStub.dataTypeMap.containsKey(TestArrayDescriptor.INSTANCE));
        assertTrue(this.daoFactoryStub.quantitationTypeMap.keySet().contains(TestQuantitationDescriptor.INSTANCE));
    }

    @Test
    public void testUnsupportedDataFile() throws InvalidDataFileException {
        final CaArrayFile expFile = getDataCaArrayFile(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP, new FileType(
                "AFFYMETRIX_EXP", FileCategory.RAW_DATA, false));
        this.arrayDataService.importData(expFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, expFile.getFileStatus());
    }
}
