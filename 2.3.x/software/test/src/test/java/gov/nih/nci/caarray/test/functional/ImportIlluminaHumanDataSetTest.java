//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;

import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Test case #7959.
 *
 * Requirements: Loaded test data set includes test user and referenced Affymetrix array design.
 */
public class ImportIlluminaHumanDataSetTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 1;
    private static final String ORGANISM = "Rattus rattus (ncbitax)";
    private static final String PROVIDER = "Illumina";

    @Test
    public void testImportAndRetrieval() throws Exception {
        String title = TestProperties.getIlluminaRatName();
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV, TestProperties.getIlluminaDesignName(),PROVIDER, ORGANISM);

        // Create project
        createExperiment(title, TestProperties.getIlluminaDesignName(), PROVIDER, ORGANISM);
        String experimentId = selenium.getText("//tr[4]/td[2]");

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        // Upload the following files:
        upload(IlluminaArrayDataFiles.HUMAN_WG6);
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // change the file type to "Illumina CSV Data File".
        selenium.click("selectFilesForm_selectedFileIds");
        selenium.click("link=Change File Type");
        waitForText("Required fields are marked");
        selenium.select("projectForm_changeToFileType", "label=Illumina Data CSV");
        selenium.click("link=Save");
        waitForText("1 file(s) updated", FIFTEEN_MINUTES);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);
        waitForAction();

        // - Import files
        importData(AUTOCREATE_ANNOTATION_SET);

        // - click on the Imported data tab and re-click until data
        // - can be found
        reClickForText("One item found", "link=Imported Data", 4, 60000);

        // - validate the status
        checkFileStatus("Imported", THIRD_COLUMN, NUMBER_OF_FILES);

        // make experiment public
        submitExperiment();
        makeExperimentPublic(experimentId);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }

}
