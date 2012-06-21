/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
 * Your) shall mean a person or an entity, and all other entities that control, 
 * are controlled by, or are under common control with the entity. Control for 
 * purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares, 
 * or (iii) beneficial ownership of such entity. 
 *
 * This License is granted provided that You agree to the conditions described 
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up, 
 * no-charge, irrevocable, transferable and royalty-free right and license in 
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
 * modifications and derivative works thereof; and (iii) sublicense the 
 * foregoing rights set out in (i) and (ii) to third parties, including the 
 * right to license such rights to further third parties. For sake of clarity, 
 * and not by way of limitation, NCI shall have no right of accounting or right 
 * of payment from You or Your sub-licensees for the rights granted under this 
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the 
 * above copyright notice, this list of conditions and the disclaimer and 
 * limitation of liability of Article 6, below. Your redistributions in object 
 * code form must reproduce the above copyright notice, this list of conditions 
 * and the disclaimer of Article 6 in the documentation and/or other materials 
 * provided with the distribution, if any. 
 *
 * Your end-user documentation included with the redistribution, if any, must 
 * include the following acknowledgment: This product includes software 
 * developed by 5AM and the National Cancer Institute. If You do not include 
 * such end-user documentation, You shall include this acknowledgment in the 
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM" 
 * to endorse or promote products derived from this Software. This License does 
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the 
 * terms of this License. 
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this 
 * Software into Your proprietary programs and into any third party proprietary 
 * programs. However, if You incorporate the Software into third party 
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software 
 * into such third party proprietary programs and for informing Your 
 * sub-licensees, including without limitation Your end-users, of their 
 * obligation to secure any required permissions from such third parties before 
 * incorporating the Software into such third party proprietary software 
 * programs. In the event that You fail to obtain such permissions, You agree 
 * to indemnify NCI for any claims against NCI by such third parties, except to 
 * the extent prohibited by law, resulting from Your failure to obtain such 
 * permissions. 
 *
 * For sake of clarity, and not by way of limitation, You may add Your own 
 * copyright statement to Your modifications and to the derivative works, and 
 * You may provide additional or different license terms and conditions in Your 
 * sublicenses of modifications of the Software, or any derivative works of the 
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR 
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
