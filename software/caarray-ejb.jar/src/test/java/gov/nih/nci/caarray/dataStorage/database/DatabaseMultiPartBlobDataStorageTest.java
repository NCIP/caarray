//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.AbstractDataStorageTest;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Provider;

/**
 * @author dkokotov
 */
public class DatabaseMultiPartBlobDataStorageTest extends AbstractDataStorageTest {
    private static final URI INVALID_SCHEME_URI = CaArrayUtils.makeUriQuietly("foobar:something");
    private static final String DUMMY_DATA = "Fake Data 123";
    private static final int CHUNK_INDEX = DUMMY_DATA.length()/2;
    private static final String CHUNK_1 = DUMMY_DATA.substring(0,CHUNK_INDEX);
    private static final String CHUNK_2 = DUMMY_DATA.substring(CHUNK_INDEX);

    @Mock
    private MultipartBlobDao blobDao;
    @Mock
    private SearchDao searchDao;
    @Mock
    private TemporaryFileCache temporaryFileCache;

    private static long COUNTER = 1L;
    private File tempDir;

    @Before
    public void setupStorage() {
        this.tempDir = Files.createTempDir();
        MockitoAnnotations.initMocks(this);
        this.DS = new DatabaseMultipartBlobDataStorage(this.blobDao, this.searchDao,
                new Provider<TemporaryFileCache>() {
                    @Override
                    public TemporaryFileCache get() {
                        return DatabaseMultiPartBlobDataStorageTest.this.temporaryFileCache;
                    }
                });
        when(blobDao.save(any(MultiPartBlob.class))).thenAnswer(new Answer<Long>() {
            public Long answer(InvocationOnMock invocation) throws Throwable {
                MultiPartBlob blob = (MultiPartBlob) invocation.getArguments()[0];
                if (blob.getId() == null) {
                    blob.setId(COUNTER++);
                }
                return blob.getId();
            }
        });
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.deleteQuietly(this.tempDir);
    }

    @Test
    public void testAddUncompressed() throws IOException {
        testAdd(false, DUMMY_DATA.getBytes());
    }

    @Test
    public void testAddCompressed() throws IOException {
        testAdd(true, DUMMY_DATA.getBytes());
    }

    private void testAdd(boolean compressed, byte[] data) throws IOException {
        // given

        // when
        final StorageMetadata sm = this.DS.add(new ByteArrayInputStream(compressed ? CaArrayUtils.gzip(data) : data),
                compressed);

        // then
        assertNotNull(sm);
        assertNotNull(sm.getHandle());
        assertCorrectScheme(sm.getHandle().getScheme());

        // check the correct data was given put in the multipartblob
        final ArgumentCaptor<MultiPartBlob> argument = ArgumentCaptor.forClass(MultiPartBlob.class);
        verify(this.blobDao).save(argument.capture());
        final InputStream in = argument.getValue().readUncompressedContents();
        assertTrue(Arrays.equals(data, ByteStreams.toByteArray(in)));
    }

    @Test
    public void testAddChunk() throws IOException {
        StorageMetadata sm = DS.addChunk(null, new ByteArrayInputStream(CHUNK_1.getBytes()));

        ArgumentCaptor<MultiPartBlob> argument = ArgumentCaptor.forClass(MultiPartBlob.class);
        verify(blobDao).save(argument.capture());
        MultiPartBlob blob = argument.getValue();
        byte[] partialFile = ByteStreams.toByteArray(blob.readCompressedContents());
        assertNotNull(sm.getHandle());
        assertEquals(sm.getPartialSize(), partialFile.length);
        assertTrue(Arrays.equals(CHUNK_1.getBytes(), partialFile));

        when(searchDao.retrieve(MultiPartBlob.class, blob.getId())).thenReturn(blob);
        StorageMetadata sm2 = DS.addChunk(sm.getHandle(), new ByteArrayInputStream(CHUNK_2.getBytes()));

        byte[] completeFile = ByteStreams.toByteArray(blob.readCompressedContents());
        assertNotNull(sm2.getHandle());
        assertEquals(sm2.getPartialSize(), completeFile.length);
        assertTrue(Arrays.equals(DUMMY_DATA.getBytes(), completeFile));
    }
    
    @Test
    public void testFinalizeChunkedFile() throws IOException {
        StorageMetadata sm = DS.addChunk(null, new ByteArrayInputStream(CHUNK_1.getBytes()));

        ArgumentCaptor<MultiPartBlob> argument = ArgumentCaptor.forClass(MultiPartBlob.class);
        verify(blobDao).save(argument.capture());
        MultiPartBlob blob = argument.getValue();
        when(searchDao.retrieve(MultiPartBlob.class, blob.getId())).thenReturn(blob);

        DS.addChunk(sm.getHandle(), new ByteArrayInputStream(CHUNK_2.getBytes()));
        StorageMetadata smFinal = DS.finalizeChunkedFile(sm.getHandle());

        ArgumentCaptor<MultiPartBlob> argument2 = ArgumentCaptor.forClass(MultiPartBlob.class);
        verify(blobDao, times(3)).save(argument2.capture());
        MultiPartBlob finalBlob = argument2.getAllValues().get(2);
        byte[] compressedFile = ByteStreams.toByteArray(finalBlob.readCompressedContents());
        byte[] uncompressedFile = ByteStreams.toByteArray(finalBlob.readUncompressedContents());
        assertEquals(smFinal.getCompressedSize(), compressedFile.length);
        assertEquals(smFinal.getUncompressedSize(), uncompressedFile.length);
        assertTrue(Arrays.equals(DUMMY_DATA.getBytes(), uncompressedFile));
    }

    
    @Test
    public void testOpenFileUncompressed() throws IOException {
        // given
        final MultiPartBlob test = new MultiPartBlob();
        test.writeData(new ByteArrayInputStream(DUMMY_DATA.getBytes()), true, 1000);
        final File tmpFile = new File(this.tempDir, "foo");
        tmpFile.createNewFile();
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.temporaryFileCache.getFile("1.unc")).willReturn(null);
        given(this.temporaryFileCache.createFile("1.unc")).willReturn(tmpFile);

        // when
        final File f = this.DS.openFile(
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)), false);

        // then
        assertEquals(tmpFile, f);
        assertEquals(DUMMY_DATA, Files.toString(f, Charset.defaultCharset()));
    }

    @Test
    public void testOpenInputStreamUncompressed() throws IOException {
        // given
        final MultiPartBlob test = new MultiPartBlob();
        test.writeData(new ByteArrayInputStream(DUMMY_DATA.getBytes()), true, 1000);
        final File tmpFile = new File(this.tempDir, "foo");
        tmpFile.createNewFile();
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.temporaryFileCache.getFile("1.unc")).willReturn(null);
        given(this.temporaryFileCache.createFile("1.unc")).willReturn(tmpFile);

        // when
        final InputStream is = this.DS.openInputStream(
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)), false);

        // then
        assertEquals(DUMMY_DATA, new String(ByteStreams.toByteArray(is)));
        is.close();
    }

    @Test
    public void testOpenFileCompressed() throws IOException {
        // given
        final MultiPartBlob test = new MultiPartBlob();
        test.writeData(new ByteArrayInputStream(DUMMY_DATA.getBytes()), true, 1000);
        final File tmpFile = new File(this.tempDir, "foo");
        tmpFile.createNewFile();
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.temporaryFileCache.getFile("1.comp")).willReturn(null);
        given(this.temporaryFileCache.createFile("1.comp")).willReturn(tmpFile);

        // when
        final File f = this.DS.openFile(
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)), true);

        // then
        assertEquals(DUMMY_DATA, new String(CaArrayUtils.gunzip(Files.toByteArray(f))));
    }

    @Test
    public void testOpenInputStreamCompressed() throws IOException {
        // given
        final MultiPartBlob test = new MultiPartBlob();
        test.writeData(new ByteArrayInputStream(DUMMY_DATA.getBytes()), true, 1000);
        final File tmpFile = new File(this.tempDir, "foo");
        tmpFile.createNewFile();
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.searchDao.retrieve(MultiPartBlob.class, 1L)).willReturn(test);
        given(this.temporaryFileCache.getFile("1.comp")).willReturn(null);
        given(this.temporaryFileCache.createFile("1.comp")).willReturn(tmpFile);

        // when
        final InputStream is = this.DS.openInputStream(
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)), true);

        // then
        assertEquals(DUMMY_DATA, new String(CaArrayUtils.gunzip(ByteStreams.toByteArray(is))));
        is.close();
    }

    @Test
    public void testList() throws IOException {
        // given
        final MultiPartBlob test = new MultiPartBlob();
        test.setId(1L);
        test.setCreationTimestamp(new Date());
        test.writeData(new ByteArrayInputStream(DUMMY_DATA.getBytes()), true, 1000);
        given(this.searchDao.retrieveAll(MultiPartBlob.class)).willReturn(Arrays.asList(test));

        // when
        final Set<StorageMetadata> metadatas = Sets.newHashSet(this.DS.list());

        // then
        assertEquals(1, metadatas.size());
        final StorageMetadata sm = metadatas.iterator().next();
        assertEquals(CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)),
                sm.getHandle());
        assertEquals(test.getCreationTimestamp(), sm.getCreationTimestamp());
        assertEquals(test.getUncompressedSize(), sm.getUncompressedSize());
        assertEquals(test.getCompressedSize(), sm.getCompressedSize());
    }

    @Test
    public void testRemove() {
        // given

        // when
        this.DS.remove(CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)));

        // then
        verify(this.blobDao).deleteByIds(argThat(Matchers.hasItem(1L)));
    }

    @Test
    public void testRemoveMultiple() {
        // given

        // when
        this.DS.remove(Arrays.asList(
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)),
                CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(2L))));

        // then
        verify(this.blobDao).deleteByIds(argThat(Matchers.hasItems(1L, 2L)));
    }

    @Test
    public void testReleaseFileUncompressed() {
        // given

        // when
        this.DS.releaseFile(CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)),
                false);

        // then
        verify(this.temporaryFileCache).delete("1.unc");
    }

    @Test
    public void testReleaseFileCompressed() {
        // given

        // when
        this.DS.releaseFile(CaArrayUtils.makeUriQuietly(DatabaseMultipartBlobDataStorage.SCHEME, String.valueOf(1L)),
                true);

        // then
        verify(this.temporaryFileCache).delete("1.comp");
    }

    private void assertCorrectScheme(String scheme) {
        assertEquals(DatabaseMultipartBlobDataStorage.SCHEME, scheme);
    }

    @Override
    protected URI getInvalidUri() {
        return INVALID_SCHEME_URI;
    }
}
