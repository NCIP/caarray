//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;

public class FileUploadUtilsTest {

    @Mock
    private ProjectManagementServiceStub mockProjectManagementService;
    @Mock
    protected FileAccessServiceStub mockFileAccessService;
    @Mock
    private DataStorageFacade mockStorageFacade;

    private static final String NON_ZIP_EXTENSION = ".idf";

    private FileUploadUtils uploadUtils = new FileUploadUtils(null);
    CaArrayFile existingFile = new CaArrayFile();

    Project project = new Project();
    List<FileWrapper> fileWrappers = Lists.newArrayList();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        final ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, this.mockProjectManagementService);
        stub.addLookup(FileAccessServiceStub.JNDI_NAME, this.mockFileAccessService);

        uploadUtils = new FileUploadUtils(mockStorageFacade);
        project.getExperiment().setPublicIdentifier("publicId");
        existingFile.setFileStatus(FileStatus.UPLOADED);
        existingFile.setName("testfile1.cel");
        existingFile.setProject(project);
        project.getFiles().add(existingFile);
    }

    @Test
    public void testUploadFiles_NonZip() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFileUpload(file, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(1, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().contains(file.getName()));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    private void addFileUpload(File file, boolean unpack) {
        FileWrapper fileWrapper = new FileWrapper();
        fileWrapper.setFile(file);
        fileWrapper.setFileName(file.getName());
        fileWrapper.setCompressed(unpack);
        fileWrapper.setTotalFileSize(file.length());
        fileWrappers.add(fileWrapper);
    }

    @Test
    public void testUploadFiles_NonZipUnpack() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFileUpload(file, true);

        try {
            uploadUtils.uploadFiles(project, fileWrappers);
            fail("An InvalidFileException should have been thrown!");
        } catch (InvalidFileException e) {
            assertEquals(FileUploadUtils.UNPACKING_ERROR_KEY, e.getResourceKey());
            assertEquals(FileUploadUtils.INVALID_ZIP_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testUploadFiles_ZipNoUnpack() throws Exception {
        File file = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        addFileUpload(file, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(1, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().contains(file.getName()));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_ZipUnpack() throws Exception {
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile, contentFile);

        addFileUpload(zipFile, true);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(1, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().contains(contentFile.getName()));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    private void addFilesToZip(File zipFile, File... files) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(zipFile);

        try {
            for (File file : files) {
                ZipEntry entry = null;
                if (file.isDirectory()) {
                    entry = new ZipEntry(file.getPath() + System.getProperty("file.separator"));
                } else {
                    entry = new ZipEntry(file.getName());
                }
                zipOut.putNextEntry(entry);
            }
        } finally {
            IOUtils.closeQuietly(zipOut);
        }

    }

    @Test
    public void testUploadFiles_MultiZipNoUnpack() throws Exception {
        File file = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        addFileUpload(file, false);
        addFileUpload(file2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(2, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(file.getName(), file2.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MultiZipUnpack() throws Exception {
        File zipFile1 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File contentFile2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile1, contentFile, contentFile2);
        File zipFile2 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile3 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile2, contentFile3);
        addFileUpload(zipFile1, true);
        addFileUpload(zipFile2, true);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(3, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(contentFile.getName(), contentFile2.getName(), contentFile3.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MultiZipUnpackConflicts() throws Exception {
        File zipFile1 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File contentFile2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile1, contentFile, contentFile2);
        File zipFile2 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile3 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile2, contentFile3);
        addFileUpload(zipFile1, true);
        addFileUpload(zipFile2, true);

        existingFile.setName(contentFile.getName());

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(2, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(contentFile2.getName(), contentFile3.getName())));
        assertTrue(result.getConflictingFiles().contains(contentFile.getName()));
    }

    @Test
    public void testUploadFiles_MultiZipMixedUnpack() throws Exception {
        File zipFile1 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File contentFile2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile1, contentFile, contentFile2);
        File zipFile2 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile3 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File zipFile3 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        addFilesToZip(zipFile2, contentFile3);
        addFileUpload(zipFile1, true);
        addFileUpload(zipFile2, true);
        addFileUpload(zipFile3, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(4, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(contentFile.getName(), contentFile2.getName(), contentFile3.getName(),
                        zipFile3.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MultiNonZip() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFileUpload(file, false);
        addFileUpload(file2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(2, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(file.getName(), file2.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MultiNonZipConflicts() throws Exception {
        File file1 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFileUpload(file1, false);
        addFileUpload(file2, false);

        existingFile.setName(file1.getName());

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(1, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(Lists.newArrayList(file2.getName())));
        assertTrue(result.getConflictingFiles().contains(file1.getName()));
    }

    @Test
    public void testUploadFiles_MixedZipNonZipNoUnpack() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFileUpload(file, false);
        addFileUpload(zipFile, false);
        addFileUpload(file2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(3, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(file.getName(), file2.getName(), zipFile.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MixedZipNonZipUnpack() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File contentFile2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile, contentFile, contentFile2);
        addFileUpload(file, false);
        addFileUpload(zipFile, true);
        addFileUpload(file2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(4, result.getCount());
        assertTrue(result.getSuccessfullyProcessedFiles().containsAll(
                Lists.newArrayList(file.getName(), file2.getName(), contentFile.getName(), contentFile2.getName())));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MixedZipNonZipMixedUnpack() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File contentFile2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile, contentFile, contentFile2);
        File zipFile2 = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        addFilesToZip(zipFile2, File.createTempFile("tmp", NON_ZIP_EXTENSION),
                File.createTempFile("tmp", NON_ZIP_EXTENSION));
        addFileUpload(file, false);
        addFileUpload(zipFile, true);
        addFileUpload(file2, false);
        addFileUpload(zipFile2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        List<String> expectedUploadedFiles = Lists.newArrayList(file.getName(), file2.getName(), contentFile.getName(),
                contentFile2.getName(), zipFile2.getName());
        assertEquals(expectedUploadedFiles.size(), result.getCount());
        assertTrue("Expected: " + expectedUploadedFiles + " But found :"
                + result.getSuccessfullyProcessedFiles().toString(), result.getSuccessfullyProcessedFiles()
                .containsAll(expectedUploadedFiles));
        assertTrue(result.getConflictingFiles().isEmpty());
    }

    @Test
    public void testUploadFiles_MixedZipNonZipUnpackDuplicateInZip() throws Exception {
        doMixedZipNonZipUnpackDuplicateTest(false);
    }

    private void doMixedZipNonZipUnpackDuplicateTest(boolean zipFirst) throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File file2 = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile, contentFile, file);
        if (zipFirst) {
            addFileUpload(zipFile, true);
            addFileUpload(file, false);
        } else {
            addFileUpload(file, false);
            addFileUpload(zipFile, true);
        }
        addFileUpload(file2, false);

        FileProcessingResult result = uploadUtils.uploadFiles(project, fileWrappers);
        List<String> expectedUploadedFiles = Lists.newArrayList(file.getName(), file2.getName(), contentFile.getName());
        assertEquals(expectedUploadedFiles.size(), result.getCount());
        assertTrue("Expected: " + expectedUploadedFiles + " But found :"
                + result.getSuccessfullyProcessedFiles().toString(), result.getSuccessfullyProcessedFiles()
                .containsAll(expectedUploadedFiles));
        assertTrue(result.getConflictingFiles().contains(file.getName()));
    }

    @Test
    public void testUploadFiles_MixedZipNonZipUnpackDuplicateOutOfZip() throws Exception {
        doMixedZipNonZipUnpackDuplicateTest(true);
    }

    @Test
    public void testUploadFiles_ManagementServiceError() throws Exception {
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        addFilesToZip(zipFile, contentFile);
        addFileUpload(zipFile, true);

        when(mockProjectManagementService.addFile(eq(project), any(InputStream.class), anyString())).thenThrow(
                new IllegalStateException("Hooray for failures!"));

        try {
            uploadUtils.uploadFiles(project, fileWrappers);
            fail("An InvalidFileException should have been thrown!");
        } catch (InvalidFileException e) {
            assertTrue(e.getCause() instanceof InvalidFileException);
            InvalidFileException cause = (InvalidFileException) e.getCause();
            assertEquals(FileUploadUtils.ADDING_FILE_ERROR_KEY, cause.getResourceKey());
            assertEquals(cause.getMessage(), FileUploadUtils.ADDING_FILE_ERROR_MESSAGE, cause.getMessage());
        }
    }

    @Test
    public void testUploadFiles_DirectoryInZip() throws Exception {
        File zipFile = File.createTempFile("tmp", FileUploadUtils.VALID_ZIP_EXTENSION);
        File contentFile = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        File tempDir = new File(contentFile.getParent());
        addFilesToZip(zipFile, contentFile);
        addFilesToZip(zipFile, tempDir);
        addFileUpload(zipFile, true);

        try {
            uploadUtils.uploadFiles(project, fileWrappers);
            fail("An InvalidFileException should have been thrown!");
        } catch (InvalidFileException e) {
            assertTrue(e.getCause() instanceof InvalidFileException);
            InvalidFileException cause = (InvalidFileException) e.getCause();
            assertEquals(FileUploadUtils.DIRECTORIES_NOT_SUPPORTED_KEY, cause.getResourceKey());
            assertEquals(cause.getMessage(), FileUploadUtils.DIRECTORIES_NOT_SUPPORTED_MESSAGE, cause.getMessage());
        }
    }

    @Test
    public void testUnpackFiles_SingleZip() throws Exception {
        CaArrayFile mockFile = mock(CaArrayFile.class);
        when(mockStorageFacade.openInputStream(any(URI.class), eq(false))).thenReturn(
                FileUtils.openInputStream(File.createTempFile("aaa", "bbb")));

        uploadUtils.unpackFiles(project, Lists.newArrayList(mockFile));
        verify(mockStorageFacade).openInputStream(any(URI.class), eq(false));
    }

    @Test
    public void testUnpackFiles_MultipleZips() throws Exception {
        CaArrayFile mockFile = mock(CaArrayFile.class);
        CaArrayFile mockFile2 = mock(CaArrayFile.class);
        when(mockStorageFacade.openInputStream(any(URI.class), eq(false))).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return FileUtils.openInputStream(File.createTempFile("aaa", "bbb"));
            }

        });

        uploadUtils.unpackFiles(project, Lists.newArrayList(mockFile, mockFile2));
        verify(mockStorageFacade, times(2)).openInputStream(any(URI.class), eq(false));
    }

    @Test
    public void testUnpackFiles_IOException() throws Exception {
        CaArrayFile mockFile = mock(CaArrayFile.class);
        InputStream input = FileUtils.openInputStream(File.createTempFile("aaa", "bbb"));
        IOUtils.closeQuietly(input);
        when(mockStorageFacade.openInputStream(any(URI.class), eq(false))).thenReturn(input);

        try {
            uploadUtils.unpackFiles(project, Lists.newArrayList(mockFile));
            fail("An InvalidFileException should have been thrown!");
        } catch (InvalidFileException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
}
