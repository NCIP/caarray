//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.file.AbstractFileValidationIntegrationTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 *
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class IlluminaFileValidationIntegrationTest extends AbstractFileValidationIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new IlluminaModule());
    }

    @Test
    public void testValidateDefect18625Hybes() throws Exception {
        final ArrayDesign design =
                importArrayDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_REDUCED, IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
        addDesignToExperiment(design);

        final Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(IlluminaArrayDataFiles.DEFECT_18652_IDF_REDUCED, FileTypeRegistry.MAGE_TAB_IDF);
        files.put(IlluminaArrayDataFiles.DEFECT_18652_SDRF_REDUCED, FileTypeRegistry.MAGE_TAB_SDRF);
        files.put(IlluminaArrayDataFiles.HUMAN_WG6_REDUCED, CsvDataHandler.DATA_CSV_FILE_TYPE);

        uploadAndValidateFiles(files);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        for (final CaArrayFile file : project.getFiles()) {
            if (!file.getFileType().equals(FileTypeRegistry.MAGE_TAB_SDRF)) {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.VALIDATION_ERRORS, file.getFileStatus());
                assertEquals(1, file.getValidationResult().getMessages().size());
                assertTrue(file.getValidationResult().getMessages().get(0).getMessage().contains("WRONG"));
            }
        }
        tx.commit();
    }

    @Test
    public void testInvalidHybridizationsInSDRF() throws Exception {
        final FileFileTypeWrapper[] dataFiles = new FileFileTypeWrapper[3];
        dataFiles[0] = new FileFileTypeWrapper(IlluminaArrayDataFiles.DEFECT_18652_IDF_REDUCED, FileTypeRegistry.MAGE_TAB_IDF);
        dataFiles[1] =
                new FileFileTypeWrapper(IlluminaArrayDataFiles.DEFECT_18652_SDRF_REDUCED, FileTypeRegistry.MAGE_TAB_SDRF);
        dataFiles[2] =
                new FileFileTypeWrapper(IlluminaArrayDataFiles.HUMAN_WG6_REDUCED, CsvDataHandler.DATA_CSV_FILE_TYPE);
        final FileFileTypeWrapper design =
                new FileFileTypeWrapper(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_REDUCED,
                        IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedSdrfErrors =
                new String[] {"Hybridization(s) [WRONG] were not found in data files provided." };
        expectedErrorsList.add(expectedSdrfErrors);
        doValidation(dataFiles, design, new FileType[] {FileTypeRegistry.MAGE_TAB_SDRF }, expectedErrorsList);
    }

    @Test
    public void testInvalidProbeNamesForIlluminaGenotypingProcessedMatrixData() throws Exception {
        final FileFileTypeWrapper[] dataFiles = new FileFileTypeWrapper[1];
        dataFiles[0] =
                new FileFileTypeWrapper(IlluminaArrayDataFiles.BAD_ILLUMINA_DERIVED_1_HYB,
                        GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE);
        final FileFileTypeWrapper design =
                new FileFileTypeWrapper(IlluminaArrayDesignFiles.ILLUMINA_SMALL_BGX_TXT, BgxDesignHandler.BGX_FILE_TYPE);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedErrors =
                new String[] {"Probe with name 'ILMN_0000000' was not found in array design 'illumina-small.bgx' version '2.0'." };
        expectedErrorsList.add(expectedErrors);
        doValidation(dataFiles, design, new FileType[] {GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE },
                expectedErrorsList);
    }
}
