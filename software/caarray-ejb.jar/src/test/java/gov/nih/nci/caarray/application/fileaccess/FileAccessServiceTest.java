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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractHibernateTest;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
public class FileAccessServiceTest extends AbstractHibernateTest {
    private FileAccessServiceBean fileAccessService;

    public FileAccessServiceTest() {
        super(true);        
    }
    
    @Before
    public void setUp() {
        this.fileAccessService = new FileAccessServiceBean();
        HibernateUtil.getHibernateHelper().unbindAndCleanupSession();
        HibernateUtil.getHibernateHelper().openAndBindSession();
        
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(TemporaryFileCacheLocator.DEFAULT);
    }

    @After
    public void tearDown() {
        TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
    }

    @Test
    public void testAdd() throws IOException, FileAccessException {
        Transaction tx = HibernateUtil.beginTransaction();
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
        tx.commit();
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.fileaccess.FileAccessService#getFile(gov.nih.nci.caarray.domain.file.CaArrayFile)}.
     * @throws FileAccessException
     */
    @Test
    public void testGetFile() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();        
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
        tx.commit();
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

    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void testRemove() {
        Transaction tx = HibernateUtil.beginTransaction();
        MultiPartBlob.setBlobSize(100);
        File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);

        Project p = new Project();
        p.getExperiment().setTitle("Foo");
        Organism o = new Organism();
        o.setScientificName("baz");
        p.getExperiment().setOrganism(o);
        TermSource ts = new TermSource();
        ts.setName("TS");
        ts.setUrl("http://ts");
        o.setTermSource(ts);
        p.getFiles().add(caArrayFile);
        caArrayFile.setProject(p);
        HibernateUtil.getCurrentSession().save(p);
        HibernateUtil.getCurrentSession().save(caArrayFile);
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        boolean removed = this.fileAccessService.remove(caArrayFile);
        assertTrue(removed);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        caArrayFile.toString();
        tx.commit();
    }

    @Test
    public void testRemoveWithArrayData() throws SQLException {
        Transaction tx = HibernateUtil.beginTransaction();
        MultiPartBlob.setBlobSize(100);
        // SDRF
        File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        // derived data file
        File file2 = MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DATA_FILE;
        CaArrayFile caArrayFile2 = this.fileAccessService.add(file2);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        DerivedArrayData der = new DerivedArrayData();
        der.setDataFile(caArrayFile2);
        Hybridization hyb = new Hybridization();
        hyb.setName("foo");
        hyb.getDerivedDataCollection().add(der);
        der.getHybridizations().add(hyb);
        Sample sample = new Sample();
        sample.setName("sample");
        Extract extract = new Extract();
        extract.setName("extract");
        sample.getExtracts().add(extract);
        LabeledExtract le = new LabeledExtract();
        le.setName("label");
        extract.getLabeledExtracts().add(le);
        le.getHybridizations().add(hyb);
        hyb.getLabeledExtracts().add(le);
        le.getExtracts().add(extract);
        extract.getSamples().add(sample);
        
        Project p = new Project();
        p.getExperiment().setTitle("Foo");
        Organism o = new Organism();
        o.setScientificName("baz");
        p.getExperiment().setOrganism(o);
        TermSource ts = new TermSource();
        ts.setName("TS");
        ts.setUrl("http://ts");
        o.setTermSource(ts);
        p.getFiles().add(caArrayFile);
        p.getFiles().add(caArrayFile2);
        caArrayFile.setProject(p);
        caArrayFile2.setProject(p);
        
        HibernateUtil.getCurrentSession().save(p);
        
        HibernateUtil.getCurrentSession().save(sample);
        HibernateUtil.getCurrentSession().saveOrUpdate(extract);
        HibernateUtil.getCurrentSession().saveOrUpdate(le);
        HibernateUtil.getCurrentSession().saveOrUpdate(sample);
        HibernateUtil.getCurrentSession().saveOrUpdate(hyb);
        HibernateUtil.getCurrentSession().saveOrUpdate(der);

        HibernateUtil.getCurrentSession().save(caArrayFile);
        HibernateUtil.getCurrentSession().save(caArrayFile2);
        HibernateUtil.getCurrentSession().flush();
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        caArrayFile2 = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile2.getId());
        der = (DerivedArrayData) HibernateUtil.getCurrentSession().load(DerivedArrayData.class, der.getId());
        assertEquals(der.getDataFile(), caArrayFile2);
        assertNotNull(caArrayFile.getProject());
        assertNotNull(caArrayFile2.getProject());
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        boolean removed = this.fileAccessService.remove(caArrayFile);
        assertTrue(removed);
        caArrayFile2 = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile2.getId());
        removed = this.fileAccessService.remove(caArrayFile2);
        assertTrue(removed);        

        tx.commit();
        tx = HibernateUtil.beginTransaction();

        try {
            caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
            fail("file " + caArrayFile + " not deleted");
        } catch (org.hibernate.ObjectNotFoundException e) {
        }

        try {
            caArrayFile2 = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile2.getId());
            fail("file " + caArrayFile2 + " not deleted");
        } catch (org.hibernate.ObjectNotFoundException e) {
        }
        try {
            der = (DerivedArrayData) HibernateUtil.getCurrentSession().load(DerivedArrayData.class, der.getId());
            fail("raw array data not deleted " + der);
        } catch (org.hibernate.ObjectNotFoundException e) {
        }
        hyb = (Hybridization) HibernateUtil.getCurrentSession().load(Hybridization.class, hyb.getId());
        assertTrue(hyb.getDerivedDataCollection().isEmpty());
        tx.commit();
    }
}
