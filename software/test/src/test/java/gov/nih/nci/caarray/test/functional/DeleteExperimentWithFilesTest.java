//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Test case #7959.
 *
 * Requirements: Loaded test data set includes test user and referenced Affymetrix array design.
 */
public class DeleteExperimentWithFilesTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 29;

    @Test
    public void testDeleteExperimentWithFiles() throws Exception {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        String title = "Delete Experiment" + System.currentTimeMillis();
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        loginAsPrincipalInvestigator();
        importArrayDesign(AffymetrixArrayDesignFiles.HT_HG_U133A_CDF, TestProperties.getAffymetrixU133ADesignName());
        // - Create Experiment
        String experimentId = createExperiment(title, TestProperties.getAffymetrixU133ADesignName());

        // - go to the data tab
        this.selenium.click("link=Data");
        waitForTab();

        upload(MageTabDataFiles.TCGA_BROAD_ZIP, FIFTEEN_MINUTES);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // - Import files
        importData(MAGE_TAB);
        // check to make sure they are imported
        selenium.click("link=Data");
        waitForText("Uncompressed Size");
        selenium.click("link=Imported Data");
        waitForText("Uncompressed Size");
        checkFileStatus("Imported", THIRD_COLUMN, NUMBER_OF_FILES);

        // go back to experiment list screen
        selenium.click("link=My Experiment Workspace");
        waitForText("Public Access");
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        selenium.click("//tr[" + row + "]/td[10]/a/img");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to delete this experiment\\?$"));
        waitForText("Experiment has been deleted.");
        assertFalse(selenium.isTextPresent(experimentId));

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime)/60000f);
        System.out.println("total time = " + totalTime);
    }
}
