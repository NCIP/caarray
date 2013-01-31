//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Test case #7959.
 *
 * Requirements: Loaded test data set includes test user and referenced Affymetrix array design.
 */
public class ImportAffymetrixChpTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 1;
    private static final String ORGANISM = "Rattus rattus (ncbitax)";

    @Test
    public void testImportAndRetrieval() throws Exception {
        String title = TestProperties.getAffymetricChpName();
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName(), AFFYMETRIX_PROVIDER, ORGANISM);

        // Create project
        String experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName(), AFFYMETRIX_PROVIDER, ORGANISM);

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        upload(AffymetrixArrayDataFiles.TEST3_CHP);

        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // - Import files
        importData(AUTOCREATE_ANNOTATION_SET);

        reClickForText("One item found", "link=Imported Data", 4, 10000);

        // - validate the status
        checkFileStatus("Imported", SECOND_COLUMN, NUMBER_OF_FILES);

        // make experiment public
        submitExperiment();
        makeExperimentPublic(experimentId);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }

}
