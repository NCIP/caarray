//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessUtilsTest extends AbstractServiceTest {
    /**
     * Test method for
     * {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#unzipFiles(java.util.List, java.util.List)}.
     * 
     * @throws FileAccessException
     */
    @Test
    public void testUnzipFilesSingle() throws FileAccessException {
        final DataStorageFacade dataStorageFacade = mock(DataStorageFacade.class);
        final FileAccessUtils fileAccessUtils = new FileAccessUtils(dataStorageFacade);

        final File file1 = MageTabDataFiles.SPECIFICATION_ZIP;

        final List<File> uploadFiles = new ArrayList<File>();
        uploadFiles.add(file1);

        final List<String> uploadFileNames = new ArrayList<String>();
        uploadFileNames.add(MageTabDataFiles.SPECIFICATION_ZIP.getName());

        assertEquals(1, uploadFiles.size());

        fileAccessUtils.unzipFiles(uploadFiles, uploadFileNames);
        assertEquals(16, uploadFiles.size());
    }

    /**
     * Test method for
     * {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#unzipFiles(java.util.List, java.util.List)}.
     */
    @Test
    public void testUnzipFilesMultiple() {
        final DataStorageFacade dataStorageFacade = mock(DataStorageFacade.class);
        final FileAccessUtils fileAccessUtils = new FileAccessUtils(dataStorageFacade);

        final File file1 = MageTabDataFiles.SPECIFICATION_ZIP;
        final File file2 = MageTabDataFiles.EBI_TEMPLATE_IDF;

        final List<File> uploadFiles = new ArrayList<File>();
        uploadFiles.add(file1);
        uploadFiles.add(file2);

        final List<String> uploadFileNames = new ArrayList<String>();
        uploadFileNames.add(MageTabDataFiles.SPECIFICATION_ZIP.getName());

        assertEquals(2, uploadFiles.size());
        fileAccessUtils.unzipFiles(uploadFiles, uploadFileNames);
        assertEquals(17, uploadFiles.size());
    }
}
