//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.genepix;

import gov.nih.nci.caarray.application.file.AbstractFileValidationIntegrationTest;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file validation of genepix data files
 * 
 * @author dkokotov, jscott
 */
public class GenepixFileValidationIntegrationTest extends AbstractFileValidationIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new GenepixModule());
    }

    @Test
    public void testInvalidProbeNamesForGenepixGprData() throws Exception {
        final FileFileTypeWrapper[] dataFiles = new FileFileTypeWrapper[3];
        dataFiles[0] = new FileFileTypeWrapper(GenepixArrayDataFiles.BAD_SMALL_IDF, FileTypeRegistry.MAGE_TAB_IDF);
        dataFiles[1] = new FileFileTypeWrapper(GenepixArrayDataFiles.BAD_SMALL_SDRF, FileTypeRegistry.MAGE_TAB_SDRF);
        dataFiles[2] = new FileFileTypeWrapper(GenepixArrayDataFiles.BAD_GPR_4_1_1, GprHandler.GPR_FILE_TYPE);
        final FileFileTypeWrapper design =
                new FileFileTypeWrapper(GenepixArrayDataFiles.JOE_DERISI_FIX, GalDesignHandler.GAL_FILE_TYPE);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedErrors =
                new String[] {"Probe with name 'FOO' was not found in array design 'JoeDeRisi-fix' version '2.0'." };
        expectedErrorsList.add(expectedErrors);
        doValidation(dataFiles, design, new FileType[] {GprHandler.GPR_FILE_TYPE }, expectedErrorsList);
    }
}
