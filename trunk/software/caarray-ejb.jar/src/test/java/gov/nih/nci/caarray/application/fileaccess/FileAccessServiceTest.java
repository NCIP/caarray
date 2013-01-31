//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import java.sql.SQLException;
import static org.junit.Assert.*;
import gov.nih.nci.caarray.application.AbstractServiceTest;
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

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceTest extends AbstractServiceTest {

    private FileAccessServiceBean fileAccessService;
    private Transaction transaction;

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
        this.transaction = HibernateUtil.beginTransaction();
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
        transaction.rollback();
        transaction = null;
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
        this.transaction = HibernateUtil.beginTransaction();
        MultiPartBlob.setBlobSize(100);
        File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertTrue(caArrayFile.isDeletable());
        HibernateUtil.getCurrentSession().save(caArrayFile);
        HibernateUtil.getCurrentSession().flush();

        Project p = new Project();
        p.getFiles().add(caArrayFile);
        caArrayFile.setProject(p);

        this.fileAccessService.remove(caArrayFile);

        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        caArrayFile.toString();
        transaction.rollback();
        transaction = null;
    }

    @Test
    public void testRemoveWithArrayData() throws SQLException {
        MultiPartBlob.setBlobSize(100);
        this.transaction = HibernateUtil.beginTransaction();
        // SDRF
        File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertTrue(caArrayFile.isDeletable());
        HibernateUtil.getCurrentSession().save(caArrayFile);
        // derived data file
        File file2 = MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DATA_FILE;
        CaArrayFile caArrayFile2 = this.fileAccessService.add(file2);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertTrue(caArrayFile2.isDeletable());
        HibernateUtil.getCurrentSession().save(caArrayFile2);
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
        HibernateUtil.getCurrentSession().save(sample);
        HibernateUtil.getCurrentSession().saveOrUpdate(extract);
        HibernateUtil.getCurrentSession().saveOrUpdate(le);
        HibernateUtil.getCurrentSession().saveOrUpdate(sample);
        HibernateUtil.getCurrentSession().saveOrUpdate(hyb);
        HibernateUtil.getCurrentSession().saveOrUpdate(der);
        HibernateUtil.getCurrentSession().saveOrUpdate(caArrayFile2);
        HibernateUtil.getCurrentSession().flush();

        this.transaction.commit();
        this.transaction = HibernateUtil.beginTransaction();
        caArrayFile = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile.getId());
        caArrayFile2 = (CaArrayFile) HibernateUtil.getCurrentSession().load(CaArrayFile.class, caArrayFile2.getId());
        der = (DerivedArrayData) HibernateUtil.getCurrentSession().load(DerivedArrayData.class, der.getId());
        assertEquals(der.getDataFile(), caArrayFile2);
        der = CaArrayDaoFactory.INSTANCE.getArrayDao().getDerivedArrayData(caArrayFile2);
        assertEquals(der.getDataFile(), caArrayFile2);
        
        Project p = new Project();
        p.getFiles().add(caArrayFile);
        p.getFiles().add(caArrayFile2);
        caArrayFile.setProject(p);
        caArrayFile2.setProject(p);

        this.fileAccessService.remove(caArrayFile);
        this.fileAccessService.remove(caArrayFile2);

        this.transaction.commit();
        this.transaction = HibernateUtil.beginTransaction();

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
    }
}
