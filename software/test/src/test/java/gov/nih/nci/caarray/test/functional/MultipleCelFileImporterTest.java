//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;

import org.junit.Test;

/**
 * Imports the largest Affy CEL files (HG-U133
 */
public class MultipleCelFileImporterTest extends AbstractSeleniumTest {

    private static final int NUM_SETS_OF_TEN = 1;

    private static final int NUMBER_OF_FILES = 10;

    @Test
    public void testUploadFiles() throws Exception {
        loginAsPrincipalInvestigator();
        importArrayDesign(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF, TestProperties.AFFYMETRIX_HUMAN_DESIGN);
        for (int i = 0; i < NUM_SETS_OF_TEN; i++) {
            importTenFiles();
        }
    }

    public void importTenFiles() throws Exception {
        String title = TestProperties.getAffymetricHumanName();
        // Create experiment
        String experimentId = createExperiment(title, TestProperties.AFFYMETRIX_HUMAN_DESIGN);
        // go to the data tab
        selenium.click("link=Data");
        waitForTab();

        upload(MageTabDataFiles.PERFORMANCE_10_IDF);
        upload(MageTabDataFiles.PERFORMANCE_10_SDRF);
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file1.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file2.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file3.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file4.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file5.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file6.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file7.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file8.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file9.CEL"));
        upload(new File(MageTabDataFiles.PERFORMANCE_DIRECTORY, "file10.CEL"));
        checkFileStatus("Uploaded", THIRD_COLUMN);

        // Import the files.
        importData(MAGE_TAB);
        lockExperimentFromEdits();
        makeExperimentPublic(experimentId);
    }

    private void checkFileStatus(String status, int column) {
        for (int row = 1; row < NUMBER_OF_FILES; row++) {
            assertEquals(status, selenium.getTable("row." + row + "." + column));
        }
    }
}
