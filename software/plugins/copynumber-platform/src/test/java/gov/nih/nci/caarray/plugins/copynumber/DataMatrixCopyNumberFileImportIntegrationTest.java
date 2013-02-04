//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.copynumber;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.plugins.agilent.AgilentModule;
import gov.nih.nci.caarray.plugins.agilent.AgilentXmlDesignFileHandler;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file import of nimblegen data files
 * 
 * @author dkokotov, jscott
 */
public class DataMatrixCopyNumberFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new DataMatrixCopyNumberModule());
        InjectorFactory.addPlatform(new AgilentModule());
    }

    @Test
    public void importSuccess() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        importFiles(fileSet, DataMatrixCopyNumberHandler.COPY_NUMBER_FILE_TYPE);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        tx.commit();
    }

    @Test
    public void partialSdrfHybCount() throws Exception {
        final ArrayDesign design =
                importArrayDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        addDesignToExperiment(design);
        final MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.DATA_MATRIX_COPY_NUMER_HYB_SUBSET_IDF));
        fileSet.addSdrf(new JavaIOFileRef(MageTabDataFiles.DATA_MATRIX_COPY_NUMER_HYB_SUBSET_SDRF));
        fileSet.addDataMatrix(new JavaIOFileRef(MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA));
        importFiles(fileSet, DataMatrixCopyNumberHandler.COPY_NUMBER_FILE_TYPE);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        tx.commit();
    }
}
