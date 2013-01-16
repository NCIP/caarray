//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import gov.nih.nci.caarray.application.file.AbstractFileValidationIntegrationTest;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file validation of nimblegen data files
 * 
 * @author dkokotov, jscott
 */
public class NimblegenFileValidationIntegrationTest extends AbstractFileValidationIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new NimblegenModule());
    }

    @Test
    public void testInvalidProbeNamesForNiblegenPairData() throws Exception {
        final FileFileTypeWrapper[] dataFiles = new FileFileTypeWrapper[1];
        dataFiles[0] =
                new FileFileTypeWrapper(NimblegenArrayDataFiles.BAD_SHORT_HUMAN_EXPRESSION,
                        PairDataHandler.NORMALIZED_PAIR_FILE_TYPE);
        final FileFileTypeWrapper design =
                new FileFileTypeWrapper(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN, NdfHandler.NDF_FILE_TYPE);
        final List<String[]> expectedErrorsList = new ArrayList<String[]>();
        final String[] expectedSdrfErrors =
                new String[] {"Probe with name 'BLOCK1|NM_001932|foo' was not found in array design '2006-08-03_HG18_60mer_expr-short' version '2.0'." };
        expectedErrorsList.add(expectedSdrfErrors);
        doValidation(dataFiles, design, new FileType[] {PairDataHandler.NORMALIZED_PAIR_FILE_TYPE }, expectedErrorsList);
    }
}
