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
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceTest {

    private FileAccessServiceBean fileAccessService;
    private Transaction transaction;

    @Before
    public void setUp() {
        this.fileAccessService = new FileAccessServiceBean();
        HibernateUtil.enableFilters(false);
        this.transaction = HibernateUtil.beginTransaction();
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(TemporaryFileCacheLocator.DEFAULT);
    }

    @After
    public void tearDown() {
        TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
        if (this.transaction != null) {
            this.transaction.rollback();
        }
    }

    @Test
    public void testAdd() throws IOException, FileAccessException {
        File file = File.createTempFile("pre", ".ext");
        file.deleteOnExit();
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        assertEquals(file.getName(), caArrayFile.getName());
        assertNull(caArrayFile.getFileType());

        file = File.createTempFile("pre", ".cdf");
        file.deleteOnExit();
        caArrayFile = this.fileAccessService.add(file);
        assertEquals(FileType.AFFYMETRIX_CDF, caArrayFile.getFileType());

        caArrayFile = this.fileAccessService.add(GenepixArrayDataFiles.GPR_3_0_6);
        assertEquals(FileType.GENEPIX_GPR, caArrayFile.getFileType());
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#getFile(gov.nih.nci.caarray.domain.file.CaArrayFile)}.
     * @throws FileAccessException
     */
    @Test
    public void testGetFile() throws Exception {
        MultiPartBlob.setBlobSize(100);
        File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        HibernateUtil.getCurrentSession().save(caArrayFile);
        HibernateUtil.getCurrentSession().flush();
        File retrievedFile = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
        assertEquals(file.getName(), retrievedFile.getName());
        assertEquals(file.length(), retrievedFile.length());
        assertTrue(file.exists());
        assertTrue(retrievedFile.exists());

        InputStream originalIs = new FileInputStream(file);
        byte[] originalBytes = IOUtils.toByteArray(originalIs);
        IOUtils.closeQuietly(originalIs);

        InputStream retrievedIs = new FileInputStream(retrievedFile);
        byte[] retrievedBytes = IOUtils.toByteArray(retrievedIs);
        IOUtils.closeQuietly(retrievedIs);
        assertEquals(originalBytes.length, retrievedBytes.length);
        for (int i = 0; i < originalBytes.length; i++) {
            assertEquals(new Byte(originalBytes[i]), new Byte(retrievedBytes[i])); // NOPMD
        }

        TemporaryFileCacheLocator.getTemporaryFileCache().closeFile(caArrayFile);
        assertFalse(retrievedFile.exists());
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#unzipFiles(java.util.List, java.util.List)}.
     * @throws FileAccessException
     */
    @Test
    public void testUnzipFilesSingle() throws FileAccessException {
        File file1 = MageTabDataFiles.SPECIFICATION_ZIP;

        List<File> uploadFiles = new ArrayList<File>();
        uploadFiles.add(file1);

        List<String> uploadFileNames = new ArrayList<String>();
        uploadFileNames.add(MageTabDataFiles.SPECIFICATION_ZIP.getName());

        assertEquals(1, uploadFiles.size());
        this.fileAccessService.unzipFiles(uploadFiles, uploadFileNames);
        assertEquals(16, uploadFiles.size());
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#unzipFiles(java.util.List, java.util.List)}.
     */
    @Test
    public void testUnzipFilesMultiple() {
        File file1 = MageTabDataFiles.SPECIFICATION_ZIP;
        File file2 = MageTabDataFiles.EBI_TEMPLATE_IDF;

        List<File> uploadFiles = new ArrayList<File>();
        uploadFiles.add(file1);
        uploadFiles.add(file2);

        List<String> uploadFileNames = new ArrayList<String>();
        uploadFileNames.add(MageTabDataFiles.SPECIFICATION_ZIP.getName());

        assertEquals(2, uploadFiles.size());
        this.fileAccessService.unzipFiles(uploadFiles, uploadFileNames);
        assertEquals(17, uploadFiles.size());
    }
}
