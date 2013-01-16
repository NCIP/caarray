//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage.fileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.dataStorage.AbstractDataStorageTest;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * @author dkokotov
 */
public class FileSystemDataStorageTest extends AbstractDataStorageTest {
    private File storageDir;
    private static final URI INVALID_SCHEME_URI = CaArrayUtils.makeUriQuietly("foobar:something");
    private static final String DUMMY_DATA = "Fake Data 123";
    private static final int chunkIndex = DUMMY_DATA.length()/2;
    private static final String CHUNK_1 = DUMMY_DATA.substring(0,chunkIndex);
    private static final String CHUNK_2 = DUMMY_DATA.substring(chunkIndex);

    @Before
    public void setupStorageDirectory() {
        this.storageDir = Files.createTempDir();
        this.DS = new FilesystemDataStorage(this.storageDir.getAbsolutePath());
    }

    @After
    public void cleanupStorageDirectory() throws IOException {
        FileUtils.deleteQuietly(this.storageDir);
    }

    private void assertCorrectScheme(String scheme) {
        assertEquals(FilesystemDataStorage.SCHEME, scheme);
    }

    @Override
    protected URI getInvalidUri() {
        return INVALID_SCHEME_URI;
    }

    @Test
    public void testAddUncompressed() throws IOException {
        final StorageMetadata sm = this.DS.add(new ByteArrayInputStream(DUMMY_DATA.getBytes()), false);
        verifyDataStoredForAdd(sm);
    }

    @Test
    public void testAddCompressed() throws IOException {
        final StorageMetadata sm = this.DS
                .add(new ByteArrayInputStream(CaArrayUtils.gzip(DUMMY_DATA.getBytes())), true);
        verifyDataStoredForAdd(sm);
    }

    @Test
    public void testAddChunk() throws IOException {
        StorageMetadata sm = DS.addChunk(null, new ByteArrayInputStream(CHUNK_1.getBytes()));
        File partialFile = new File(storageDir, sm.getHandle().getSchemeSpecificPart() + ".unc");
        assertEquals(CHUNK_1, Files.toString(partialFile, Charset.defaultCharset()));
        assertEquals(sm.getPartialSize(), partialFile.length());

        StorageMetadata sm2 = DS.addChunk(sm.getHandle(), new ByteArrayInputStream(CHUNK_2.getBytes()));
        final File completeFile = new File(storageDir, sm2.getHandle().getSchemeSpecificPart() + ".unc");
        assertEquals(sm.getHandle(), sm2.getHandle());
        assertEquals(DUMMY_DATA, Files.toString(completeFile, Charset.defaultCharset()));
        assertEquals(sm2.getPartialSize(), completeFile.length());
    }
    
    @Test
    public void testFinalizeChunkedFile() throws IOException {
        StorageMetadata sm = DS.addChunk(null, new ByteArrayInputStream(CHUNK_1.getBytes()));
        URI handle = sm.getHandle();
        DS.addChunk(handle, new ByteArrayInputStream(CHUNK_2.getBytes()));
        StorageMetadata smFinal = DS.finalizeChunkedFile(handle);
        verifyDataStoredForAdd(smFinal);

    }

    private void verifyDataStoredForAdd(StorageMetadata sm) throws IOException {
        assertNotNull(sm);
        assertNotNull(sm.getHandle());
        assertCorrectScheme(sm.getHandle().getScheme());

        final File file = new File(this.storageDir, sm.getHandle().getSchemeSpecificPart() + ".unc");
        final File cfile = new File(this.storageDir, sm.getHandle().getSchemeSpecificPart() + ".comp");
        assertTrue(file.exists());
        assertTrue(cfile.exists());
        assertEquals(DUMMY_DATA, Files.toString(file, Charset.defaultCharset()));
        assertEquals(DUMMY_DATA, new String(CaArrayUtils.gunzip(Files.toByteArray(cfile))));
        assertEquals(sm.getUncompressedSize(), file.length());
        assertEquals(sm.getCompressedSize(), cfile.length());
    }

    @Test
    public void testOpenFileCompressed() throws IOException {
        final File tmpFile = new File(this.storageDir, "foo.comp");
        tmpFile.createNewFile();
        Files.write(CaArrayUtils.gzip(DUMMY_DATA.getBytes()), tmpFile);

        final File cfile = this.DS.openFile(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), true);

        assertEquals(DUMMY_DATA, new String(CaArrayUtils.gunzip(Files.toByteArray(cfile))));
    }

    @Test
    public void testOpenInputStreamCompressed() throws IOException {
        final File tmpFile = new File(this.storageDir, "foo.comp");
        tmpFile.createNewFile();
        Files.write(CaArrayUtils.gzip(DUMMY_DATA.getBytes()), tmpFile);

        final InputStream is = this.DS.openInputStream(
                CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), true);

        assertEquals(DUMMY_DATA, new String(CaArrayUtils.gunzip(ByteStreams.toByteArray(is))));
        is.close();
    }

    @Test
    public void testOpenFileUncompressed() throws IOException {
        final File tmpFile = new File(this.storageDir, "foo.unc");
        tmpFile.createNewFile();
        Files.write(DUMMY_DATA, tmpFile, Charset.defaultCharset());

        final File file = this.DS.openFile(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), false);
        assertEquals(DUMMY_DATA, Files.toString(file, Charset.defaultCharset()));
    }

    @Test
    public void testOpenInputStreamUncompressed() throws IOException {
        final File tmpFile = new File(this.storageDir, "foo.unc");
        tmpFile.createNewFile();
        Files.write(DUMMY_DATA, tmpFile, Charset.defaultCharset());

        final InputStream is = this.DS.openInputStream(
                CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), false);
        assertEquals(DUMMY_DATA, new String(ByteStreams.toByteArray(is)));
        is.close();
    }

    @Test
    public void testRemove() throws IOException {
        // given
        final File dataFile = new File(this.storageDir, "foo.unc");
        dataFile.createNewFile();
        final File cdataFile = new File(this.storageDir, "foo.comp");
        cdataFile.createNewFile();
        assertTrue(dataFile.exists());
        assertTrue(cdataFile.exists());

        // when
        this.DS.remove(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"));

        // then
        assertFalse(dataFile.exists());
        assertFalse(cdataFile.exists());
    }

    @Test
    public void testRemoveMultiple() throws IOException {
        // given
        final File dataFile = new File(this.storageDir, "foo.unc");
        dataFile.createNewFile();
        final File cdataFile = new File(this.storageDir, "foo.comp");
        cdataFile.createNewFile();
        assertTrue(dataFile.exists());
        assertTrue(cdataFile.exists());

        // when
        this.DS.remove(Arrays.asList(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"),
                CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "baz")));

        // then
        assertFalse(dataFile.exists());
        assertFalse(cdataFile.exists());
    }

    @Test
    public void testReleaseFileUncompressed() {
        // given

        // when
        this.DS.releaseFile(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), false);

        // then
        // nothing should happen. just verify it didnt throw exception
    }

    @Test
    public void testReleaseFileCompressed() {
        // given

        // when
        this.DS.releaseFile(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), true);

        // then
        // nothing should happen. just verify it didnt throw exception
    }

    @Test
    public void testList() throws IOException {
        // given
        final File dataFile = new File(this.storageDir, "foo.unc");
        dataFile.createNewFile();
        Files.write(DUMMY_DATA, dataFile, Charset.defaultCharset());
        final File cdataFile = new File(this.storageDir, "foo.comp");
        cdataFile.createNewFile();
        Files.write(CaArrayUtils.gzip(DUMMY_DATA.getBytes()), cdataFile);

        // when
        final Set<StorageMetadata> metadatas = Sets.newHashSet(this.DS.list());

        // then
        assertEquals(1, metadatas.size());
        final StorageMetadata sm = metadatas.iterator().next();
        assertEquals(CaArrayUtils.makeUriQuietly(FilesystemDataStorage.SCHEME, "foo"), sm.getHandle());
        assertEquals(new Date(dataFile.lastModified()), sm.getCreationTimestamp());
        assertEquals(dataFile.length(), sm.getUncompressedSize());
        // assertEquals(cdataFile.length(), sm.getCompressedSize());
    }
}
