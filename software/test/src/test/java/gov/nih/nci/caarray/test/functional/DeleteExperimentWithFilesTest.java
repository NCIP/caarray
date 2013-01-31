//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
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

        upload(MageTabDataFiles.TCGA_BROAD_ZIP);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // - Import files
        importData(MAGE_TAB);

        // - click on the Imported data tab and re-click until data
        // - can be found
        reClickForText("29 items found", "link=Imported Data", 10, 10000);


        // go back to experiment list screen
        selenium.click("link=My Experiment Workspace");
        waitForText("Work Queue");
        pause(2000);
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        // - Click on the image to enter the delete mode again
        selenium.click("//tr[" + row + "]/td[8]/a/img");

        // delete experiment
        waitForText("Experiment has been deleted", FIFTEEN_MINUTES);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime)/60000f);
        System.out.println("total time = " + totalTime);
    }
}
