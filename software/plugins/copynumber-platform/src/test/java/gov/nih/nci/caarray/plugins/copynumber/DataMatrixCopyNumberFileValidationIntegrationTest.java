//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.copynumber;

import gov.nih.nci.caarray.application.file.AbstractFileValidationIntegrationTest;
import gov.nih.nci.caarray.application.util.MessageTemplates;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.plugins.agilent.AgilentModule;
import gov.nih.nci.caarray.plugins.agilent.AgilentXmlDesignFileHandler;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file validation of nimblegen data files
 * 
 * @author dkokotov, jscott
 */
public class DataMatrixCopyNumberFileValidationIntegrationTest extends AbstractFileValidationIntegrationTest {
    
    private FileFileTypeWrapper design;
    
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new DataMatrixCopyNumberModule());
        InjectorFactory.addPlatform(new AgilentModule());
    }
    
    @Before
    public void setup() {
        design = new FileFileTypeWrapper(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML,
                AgilentXmlDesignFileHandler.XML_FILE_TYPE);
    }

    @Test
    public void testInvalidProbeNamesForDataMatrixCopyNumberData() throws Exception {
        final FileFileTypeWrapper[] dataFiles = getDataFiles(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_IDF, 
                MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_SDRF,
                MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_DATA);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedErrors =
                new String[] {"Probe with name 'foo' was not found in array design '022522_D_F_20090107.short' version '2.0'." };
        expectedErrorsList.add(expectedErrors);
        doValidation(dataFiles, design, new FileType[] {DataMatrixCopyNumberHandler.COPY_NUMBER_FILE_TYPE },
                expectedErrorsList);
    }

    @Test
    public void testInvalidArrayDesignNameInSdrf() throws Exception {
        final FileFileTypeWrapper[] dataFiles = getDataFiles(MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_BAD_SDRF_IDF,
                MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_BAD_SDRF_SDRF,
                MageTabDataFiles.BAD_DATA_MATRIX_COPY_NUMER_DATA);
        final String arrayDesignName = "Agilent.com:PhysicalArrayDesign:022522_D_F_20090107";
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedErrors =
                new String[] {
                        String.format(MessageTemplates.NON_EXISTING_ARRAY_DESIGN_ERROR_MESSAGE_TEMPLATE,
                                arrayDesignName),
                        String.format(
                                MessageTemplates.ARRAY_DESIGN_NOT_ASSOCIATED_WITH_EXPERIMENT_ERROR_MESSAGE_TEMPLATE,
                                arrayDesignName) };
        expectedErrorsList.add(expectedErrors);
        doValidation(dataFiles, design, new FileType[] {FileTypeRegistry.MAGE_TAB_SDRF }, expectedErrorsList);
    }
    
    @Test
    public void splitSdrf() throws Exception {
        final FileFileTypeWrapper[] dataFiles = getDataFiles(MageTabDataFiles.DATA_MATRIX_COPY_NUMER_HYB_SUBSET_IDF,
                MageTabDataFiles.DATA_MATRIX_COPY_NUMER_HYB_SUBSET_SDRF,
                MageTabDataFiles.GOOD_DATA_MATRIX_COPY_NUMER_DATA);
        
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedErrors = new String[] {};
        expectedErrorsList.add(expectedErrors);
        
        final List<String[]> expectedWarningsList = new ArrayList<String[]>();
        final String[] expectedWarnings = new String[] { DataMatrixCopyNumberHandler.PARTIAL_SDRF_WARNING };
        expectedWarningsList.add(expectedWarnings);
        
        doValidation(dataFiles, design, new FileType[] {DataMatrixCopyNumberHandler.COPY_NUMBER_FILE_TYPE}, 
                expectedErrorsList, expectedWarningsList);
    }
    
    private FileFileTypeWrapper[] getDataFiles(File idf, File sdrf, File dataMatrix) {
        final FileFileTypeWrapper[] result = new FileFileTypeWrapper[3];
        result[0] = new FileFileTypeWrapper(idf, FileTypeRegistry.MAGE_TAB_IDF);
        result[1] = new FileFileTypeWrapper(sdrf, FileTypeRegistry.MAGE_TAB_SDRF);
        result[2] = new FileFileTypeWrapper(dataMatrix, DataMatrixCopyNumberHandler.COPY_NUMBER_FILE_TYPE);
        return result;
    }
}
