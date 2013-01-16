//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.application.file.AbstractFileValidationIntegrationTest;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for the file validation of agilent files.
 * 
 * @author jscott, dkokotov
 */
public class AgilentFileValidationIntegrationTest extends AbstractFileValidationIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new AgilentModule());
    }

    @Test
    public void testInvalidProbeNamesForAgilentRawTextData() throws Exception {
        final FileFileTypeWrapper[] dataFiles = new FileFileTypeWrapper[3];
        dataFiles[0] =
                new FileFileTypeWrapper(AgilentArrayDataFiles.TINY_RAW_TEXT,
                        AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE);
        dataFiles[1] = new FileFileTypeWrapper(AgilentArrayDataFiles.TINY_IDF, FileTypeRegistry.MAGE_TAB_IDF);
        dataFiles[2] = new FileFileTypeWrapper(AgilentArrayDataFiles.TINY_SDRF, FileTypeRegistry.MAGE_TAB_SDRF);
        File designFile = AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML_ERRORS;
        final FileFileTypeWrapper design =
                new FileFileTypeWrapper(designFile,
                        AgilentXmlDesignFileHandler.XML_FILE_TYPE);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        String designName = designFile.getName().substring(0, designFile.getName().lastIndexOf('.'));
        final String[] expectedErrors = new String[] {
        		"Probe with aliases 'Agilent_Tiny_2','chr10:19811167-19811226' was not found in array design '"
        		+ designName + "' version '2.0'.",
        		"Probe with aliases 'Agilent_Tiny_1','chr6:62964904-62964963' was not found in array design '" 
        		+ designName + "' version '2.0'."
        		};
        expectedErrorsList.add(expectedErrors);
        doValidation(dataFiles, design, new FileType[] {AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE },
                expectedErrorsList);
    }
}
