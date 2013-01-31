//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceTest extends AbstractCaarrayTest {

    private FileAccessServiceBean fileAccessService;
    private Transaction transaction;

    @Before
    public void setUp() {
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
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
