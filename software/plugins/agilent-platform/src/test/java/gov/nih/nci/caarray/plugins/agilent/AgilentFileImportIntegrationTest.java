//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedArrayDesignFileHandler;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedDataHandler;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file import of agilent data files
 * 
 * @author dkokotov, jscott
 */
public class AgilentFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new AgilentModule());
    }

    @Test
    public void testReimportProjectFiles() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_MIRNA_1_XML_SMALL,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);

        final Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(AgilentArrayDataFiles.MIRNA, UnparsedDataHandler.FILE_TYPE_ILLUMINA_RAW_TXT);

        CaArrayFileSet fileSet = uploadFiles(files);
        importFiles(fileSet);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());

        assertEquals(1, project.getImportedFiles().size());
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, project.getImportedFiles().iterator().next().getFileStatus());
        final CaArrayFile f = project.getImportedFiles().iterator().next();
        f.setFileType(AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().save(f);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        fileSet = new CaArrayFileSet();
        fileSet.addAll(project.getImportedFiles());
        getFileManagementService().reimportAndParseProjectFiles(project, fileSet);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(1, project.getImportedFiles().size());
        assertEquals(1, project.getExperiment().getHybridizations().size());
        assertEquals(1, project.getExperiment().getHybridizations().iterator().next().getRawDataCollection().size());
        assertEquals("Agilent Raw Text", project.getExperiment().getHybridizations().iterator().next()
                .getRawDataCollection().iterator().next().getType().getName());
        final CaArrayFile imported = project.getImportedFiles().iterator().next();
        assertEquals(FileStatus.IMPORTED, imported.getFileStatus());
        tx.commit();
    }

    @Test
    public void testReimport() throws Exception {
        ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_REDUCED_XML,
                        UnparsedArrayDesignFileHandler.AGILENT_CSV);
        assertNull(design.getDesignDetails());

        Transaction tx = this.hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(design);
        getFileManagementService().reimportAndParseArrayDesign(design.getId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(5, design.getNumberOfFeatures().intValue());
        assertEquals(5, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }

    @Test
    public void testReimportWithReferencingExperiment() throws Exception {
        ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_REDUCED_XML,
                        UnparsedArrayDesignFileHandler.AGILENT_CSV);
        assertNull(design.getDesignDetails());
        addDesignToExperiment(design);

        Transaction tx = this.hibernateHelper.beginTransaction();
        design.getFirstDesignFile().setFileType(AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(design);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().evict(design);
        getFileManagementService().reimportAndParseArrayDesign(design.getId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        assertNotNull(design.getDesignDetails());
        assertEquals(5, design.getNumberOfFeatures().intValue());
        assertEquals(5, design.getDesignDetails().getFeatures().size());
        tx.commit();
    }
}
