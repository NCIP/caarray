//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceTest extends AbstractServiceTest {
    private static final String DUMMY_FILE_NAME = "file";
    private static final long DUMMY_SIZE = 100L;
    private static final long DUMMY_COMPRESSED_SIZE = 10L;
    private static final long DUMMY_CHUNK_SIZE = 20L;
    
    private final StorageMetadata TEST_METADATA = getStorageMetadata(
            DUMMY_COMPRESSED_SIZE, DUMMY_SIZE, 0, CaArrayUtils.makeUriQuietly("test:1"), new Date());

    @Mock
    private FileDao fileDao;
    @Mock
    private ArrayDao arrayDao;
    @Mock
    private DataStorageFacade dataStorageFacade;

    private FileAccessServiceBean fileAccessService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.dataStorageFacade.addFile(any(InputStream.class), anyBoolean())).thenReturn(TEST_METADATA);

        this.fileAccessService = new FileAccessServiceBean();
        final FileTypeRegistry typeRegistry =
                new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                        Collections.<DesignFileHandler> emptySet());
        this.fileAccessService.setFileDao(this.fileDao);
        this.fileAccessService.setArrayDao(this.arrayDao);
        this.fileAccessService.setDataStorageFacade(this.dataStorageFacade);
        this.fileAccessService.setTypeRegistry(typeRegistry);
    }

    @Test
    public void testAddUnknownType() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", ".ext");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file);
        doAsserts(file, caArrayFile, null, null, null, 0);
    }

    @Test
    public void testAddKnownType() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", ".idf");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file);
        doAsserts(file, caArrayFile, FileTypeRegistry.MAGE_TAB_IDF, null, null, 0);
    }

    @Test
    public void testAddKnownTypeWithParent() throws IOException, FileAccessException {
        Project project = new Project();
        project.setId(1L);

        final File parentFile = File.createTempFile("parent", ".idf");
        parentFile.deleteOnExit();
        final CaArrayFile caArrayFileParent = this.fileAccessService.add(parentFile);
        caArrayFileParent.setProject(project);
        doAsserts(parentFile, caArrayFileParent, FileTypeRegistry.MAGE_TAB_IDF, null, null, 0);

        final File childFile = File.createTempFile("child", ".idf");
        childFile.deleteOnExit();
        final CaArrayFile caArrayFileChild = this.fileAccessService.add(childFile, caArrayFileParent);
        doAsserts(childFile, caArrayFileChild, FileTypeRegistry.MAGE_TAB_IDF, 
                caArrayFileParent.getName(), caArrayFileParent, 1);
    }

    @Test
    public void testAddKnownTypeWithName() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", "ext");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file, "testfile1.idf");
        doAsserts(file, caArrayFile, FileTypeRegistry.MAGE_TAB_IDF, "testfile1.idf", null, 0);
    }

    @Test
    public void testAddKnownTypeWithInputStream() throws IOException, FileAccessException {
        final String contents = "test";
        final CaArrayFile caArrayFile =
                this.fileAccessService.add(new ByteArrayInputStream(contents.getBytes()), "testfile1.idf");
        doAsserts(null, caArrayFile, FileTypeRegistry.MAGE_TAB_IDF, "testfile1.idf", null, 0);
    }

    private void doAsserts(File file, CaArrayFile caArrayFile, FileType expectedFileType,
            String expectedFilename, CaArrayFile parentFile, int expectedNumberOfChildren) {
        if (file != null && expectedFilename == null) {
            assertEquals(file.getName(), caArrayFile.getName());
        } else {
            assertEquals(expectedFilename, caArrayFile.getName());
        }
        if (expectedFileType == null) {
            assertNull(caArrayFile.getFileType());
        } else {
            assertEquals(expectedFileType, caArrayFile.getFileType());
        }

        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        assertEquals(TEST_METADATA.getUncompressedSize(), caArrayFile.getUncompressedSize());
        assertEquals(TEST_METADATA.getCompressedSize(), caArrayFile.getCompressedSize());
        assertEquals(TEST_METADATA.getHandle(), caArrayFile.getDataHandle());

        if (parentFile != null) {
            assertEquals(caArrayFile.getParent().getId(), parentFile.getId());
            assertEquals(caArrayFile.getParent().getName(), parentFile.getName());
            assertEquals(caArrayFile.getParent(), parentFile);
            assertEquals(caArrayFile.getProject().getId(), parentFile.getProject().getId());
            if (expectedNumberOfChildren <= 0) {
                assertFalse(caArrayFile.getParent().hasChildren());
                assertFalse(parentFile.hasChildren());
            } else {
                assertTrue(caArrayFile.getParent().hasChildren());
                assertTrue(parentFile.hasChildren());

                assertEquals(expectedNumberOfChildren, caArrayFile.getParent().getChildren().size());
                assertEquals(expectedNumberOfChildren, parentFile.getChildren().size());
            }
        }
    }

    @Test
    public void testAddFirstChunk() throws IOException {
        File firstChunk = File.createTempFile("chunk", ".first");
        File lastChunk = File.createTempFile("chunk", ".last");
        firstChunk.deleteOnExit();
        lastChunk.deleteOnExit();

        URI handle = CaArrayUtils.makeUriQuietly("chunk:1");
        StorageMetadata firstMetadata = getStorageMetadata(0, 0, DUMMY_CHUNK_SIZE, handle, null);
        StorageMetadata lastMetadata = getStorageMetadata(0, 0, DUMMY_SIZE, handle, null);
        StorageMetadata finalizedMetadata = getStorageMetadata(DUMMY_COMPRESSED_SIZE, DUMMY_SIZE, 0, handle, null);
        when(dataStorageFacade.addFileChunk((URI)isNull(), any(InputStream.class))).thenReturn(firstMetadata);
        when(dataStorageFacade.addFileChunk(eq(handle), any(InputStream.class))).thenReturn(lastMetadata);
        when(dataStorageFacade.finalizeChunkedFile(handle)).thenReturn(finalizedMetadata);

        CaArrayFile partialFile = fileAccessService.addChunk(firstChunk, DUMMY_FILE_NAME, DUMMY_SIZE, null);
        assertNotNull(partialFile);
        assertEquals(DUMMY_FILE_NAME, partialFile.getName());
        assertEquals(DUMMY_CHUNK_SIZE, partialFile.getPartialSize());
        assertEquals(FileStatus.UPLOADING, partialFile.getFileStatus());
        verify(dataStorageFacade).addFileChunk((URI)isNull(), any(InputStream.class));

        CaArrayFile completeFile = fileAccessService.addChunk(lastChunk, DUMMY_FILE_NAME, DUMMY_SIZE, partialFile);
        assertEquals(DUMMY_SIZE, completeFile.getUncompressedSize());
        assertEquals(DUMMY_COMPRESSED_SIZE, completeFile.getCompressedSize());
        assertEquals(FileStatus.UPLOADED, completeFile.getFileStatus());
        verify(dataStorageFacade).addFileChunk(eq(handle), any(InputStream.class));
        verify(dataStorageFacade).finalizeChunkedFile(handle);
    }
    
    @Test
    public void testSynchronizeWithStorage() {
        final List<URI> fileRefs = Lists.newArrayList(CaArrayUtils.makeUriQuietly("foo:bar"));
        final List<URI> parsedRefs = Lists.newArrayList(CaArrayUtils.makeUriQuietly("bar:foo"));

        final Set<URI> allRefs = Sets.newHashSet(fileRefs);
        allRefs.addAll(parsedRefs);

        when(this.fileDao.getAllFileHandles()).thenReturn(fileRefs);
        when(this.arrayDao.getAllParsedDataHandles()).thenReturn(parsedRefs);

        this.fileAccessService.synchronizeDataStorage();

        verify(this.dataStorageFacade).removeUnreferencedData(allRefs,
                FileAccessServiceBean.MIN_UNREFERENCABLE_DATA_AGE);
    }
    
    @Test
    public void testSynchronizeWithNoData() {
        when(this.fileDao.getAllFileHandles()).thenReturn(new ArrayList<URI>());
        when(this.arrayDao.getAllParsedDataHandles()).thenReturn(new ArrayList<URI>());

        this.fileAccessService.synchronizeDataStorage();

        verify(this.dataStorageFacade, never()).removeUnreferencedData(anySet(), anyLong());
    }

    @Test
    public void testRemoveNotDeletable() {
        final CaArrayFile f = new CaArrayFile();
        f.setId(1L);
        final Project p = new Project();
        p.setId(1L);
        p.getFiles().add(f);
        f.setProject(p);

        when(this.fileDao.getDeletableFiles(1L)).thenReturn(Lists.<CaArrayFile> newArrayList());

        final boolean removed = this.fileAccessService.remove(f);
        assertFalse(removed);
    }

    @Test
    public void testRemoveNoArrayData() {
        final CaArrayFile f = new CaArrayFile();
        f.setId(1L);
        final Project p = new Project();
        p.setId(1L);
        p.getFiles().add(f);
        f.setProject(p);

        when(this.fileDao.getDeletableFiles(1L)).thenReturn(Lists.newArrayList(f));
        when(this.arrayDao.getArrayData(anyLong())).thenReturn(null);

        final boolean removed = this.fileAccessService.remove(f);
        assertTrue(removed);
        assertTrue(p.getFiles().isEmpty());
        verify(this.fileDao).remove(f);
    }

    @Test
    public void testRemoveWithArrayData() {
        final CaArrayFile f = new CaArrayFile();
        f.setId(1L);
        final Project p = new Project();
        p.setId(1L);
        p.getFiles().add(f);
        f.setProject(p);

        final RawArrayData ad = new RawArrayData();
        ad.setId(1L);
        final Hybridization h = new Hybridization();
        h.setId(1L);
        final LabeledExtract le = new LabeledExtract();
        ad.addHybridization(h);
        h.getRawDataCollection().add(ad);
        le.getHybridizations().add(h);
        h.getLabeledExtracts().add(le);
        Date lastModifiedDate = new Date();
        le.setLastModifiedDataTime(lastModifiedDate);
        p.getExperiment().setLastDataModificationDate(lastModifiedDate);

        when(this.fileDao.getDeletableFiles(1L)).thenReturn(Lists.newArrayList(f));
        when(this.arrayDao.getArrayData(1L)).thenReturn(ad);

        final boolean removed = this.fileAccessService.remove(f);
        assertTrue(removed);
        assertTrue(p.getFiles().isEmpty());
        assertTrue(h.getRawDataCollection().isEmpty());
        assertNotSame(lastModifiedDate, p.getExperiment().getLastDataModificationDate());
        assertNotSame(lastModifiedDate, le.getLastModifiedDataTime());
        verify(this.fileDao).remove(f);
        verify(this.arrayDao).remove(ad);
    }

    @Test
    public void testRemoveParentFile() {
         testRemoveParentOrChildFile(true);
    }

    @Test
    public void testRemoveChildFile() {
        testRemoveParentOrChildFile(false);
    }
    
    private void testRemoveParentOrChildFile(boolean removeParentFile) {
        final Project p = new Project();
        p.setId(1L);

        final CaArrayFile parentFile = getCaArrayFile(p, "Parent File", 1L, null);
        final CaArrayFile childFile1 = getCaArrayFile(p, "Child File 1", 2L, parentFile);
        final CaArrayFile childFile2 = getCaArrayFile(p, "Child File 2", 3L, parentFile);

        when(this.fileDao.getDeletableFiles(1L)).thenReturn(Lists.newArrayList(parentFile, childFile1, childFile2));
        when(this.arrayDao.getArrayData(anyLong())).thenReturn(null);

        CaArrayFile fileToRemove = removeParentFile ? parentFile : childFile1;
        final boolean removed = this.fileAccessService.remove(fileToRemove);
        assertTrue(removed);
        
        InOrder inOrder = inOrder(fileDao);
        inOrder.verify(fileDao).flushSession();
        inOrder.verify(fileDao).getDeletableFiles(any(Long.class));

        if (removeParentFile) {
            assertTrue(p.getFiles().isEmpty());
            verify(this.fileDao).remove(parentFile);
            verify(this.fileDao).remove(childFile1);
            verify(this.fileDao).remove(childFile2);
        } else {
            assertFalse(p.getFiles().isEmpty());
            assertEquals(2, p.getFiles().size());

            assertFalse(p.getFiles().contains(childFile1));
            assertTrue(p.getFiles().contains(parentFile));
            assertTrue(p.getFiles().contains(childFile2));

            assertTrue(parentFile.hasChildren());
            assertEquals(1, parentFile.getChildren().size());
            assertFalse(parentFile.getChildren().contains(childFile1));
            assertTrue(parentFile.getChildren().contains(childFile2));

            verify(this.fileDao).remove(childFile1);
            verify(this.fileDao, never()).remove(parentFile);
            verify(this.fileDao, never()).remove(childFile2);
        }
    }

    private CaArrayFile getCaArrayFile(Project p, String filename, Long id, CaArrayFile parentFile) {
        final CaArrayFile caArrayFile = parentFile == null ? new CaArrayFile() : new CaArrayFile(parentFile);
        caArrayFile.setName(filename);
        caArrayFile.setId(id);
        p.getFiles().add(caArrayFile);
        caArrayFile.setProject(p);
        if (parentFile != null) {
            parentFile.addChild(caArrayFile);
        }

        return caArrayFile;
    }
    
    private StorageMetadata getStorageMetadata(long compressedSize, long uncompressedSize, long partialSize, URI handle, Date date) {
        StorageMetadata metadata = new StorageMetadata();
        metadata.setCompressedSize(compressedSize);
        metadata.setUncompressedSize(uncompressedSize);
        metadata.setPartialSize(partialSize);
        metadata.setHandle(handle);
        metadata.setCreationTimestamp(date);
        return metadata;
    }
}
