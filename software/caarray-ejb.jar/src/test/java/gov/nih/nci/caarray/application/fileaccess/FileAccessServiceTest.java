/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
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
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceTest extends AbstractServiceTest {
    private static final StorageMetadata TEST_METADATA = new StorageMetadata(10, 100,
            CaArrayUtils.makeUriQuietly("test:1"), new Date());

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
        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                Collections.<DesignFileHandler> emptySet());
        this.fileAccessService.setDependencies(this.fileDao, this.arrayDao, this.dataStorageFacade, typeRegistry);

    }

    @Test
    public void testAddUnknownType() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", ".ext");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file);
        assertEquals(file.getName(), caArrayFile.getName());
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        assertNull(caArrayFile.getFileType());
        assertEquals(TEST_METADATA.getUncompressedSize(), caArrayFile.getUncompressedSize());
        assertEquals(TEST_METADATA.getCompressedSize(), caArrayFile.getCompressedSize());
        assertEquals(TEST_METADATA.getHandle(), caArrayFile.getDataHandle());
    }

    @Test
    public void testAddKnownType() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", ".idf");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file);
        assertEquals(file.getName(), caArrayFile.getName());
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        assertEquals(FileTypeRegistry.MAGE_TAB_IDF, caArrayFile.getFileType());
        assertEquals(TEST_METADATA.getUncompressedSize(), caArrayFile.getUncompressedSize());
        assertEquals(TEST_METADATA.getCompressedSize(), caArrayFile.getCompressedSize());
        assertEquals(TEST_METADATA.getHandle(), caArrayFile.getDataHandle());
    }

    @Test
    public void testAddKnownTypeWithName() throws IOException, FileAccessException {
        final File file = File.createTempFile("pre", "ext");
        file.deleteOnExit();
        final CaArrayFile caArrayFile = this.fileAccessService.add(file, "testfile1.idf");
        assertEquals("testfile1.idf", caArrayFile.getName());
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        assertEquals(FileTypeRegistry.MAGE_TAB_IDF, caArrayFile.getFileType());
        assertEquals(TEST_METADATA.getUncompressedSize(), caArrayFile.getUncompressedSize());
        assertEquals(TEST_METADATA.getCompressedSize(), caArrayFile.getCompressedSize());
        assertEquals(TEST_METADATA.getHandle(), caArrayFile.getDataHandle());
    }

    @Test
    public void testAddKnownTypeWithInputStream() throws IOException, FileAccessException {
        final String contents = "test";
        final CaArrayFile caArrayFile = this.fileAccessService.add(new ByteArrayInputStream(contents.getBytes()),
                "testfile1.idf");
        assertEquals("testfile1.idf", caArrayFile.getName());
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        assertEquals(FileTypeRegistry.MAGE_TAB_IDF, caArrayFile.getFileType());
        assertEquals(TEST_METADATA.getUncompressedSize(), caArrayFile.getUncompressedSize());
        assertEquals(TEST_METADATA.getCompressedSize(), caArrayFile.getCompressedSize());
        assertEquals(TEST_METADATA.getHandle(), caArrayFile.getDataHandle());
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
        ad.addHybridization(h);
        h.getRawDataCollection().add(ad);

        when(this.fileDao.getDeletableFiles(1L)).thenReturn(Lists.newArrayList(f));
        when(this.arrayDao.getArrayData(1L)).thenReturn(ad);

        final boolean removed = this.fileAccessService.remove(f);
        assertTrue(removed);
        assertTrue(p.getFiles().isEmpty());
        assertTrue(h.getRawDataCollection().isEmpty());
        verify(this.fileDao).remove(f);
        verify(this.arrayDao).remove(ad);
    }
}
