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
public class ImportSimpleMageTabSetTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 10;

    @Test
    public void testImportAndRetrieval() throws Exception {
        String title = TestProperties.getAffymetricSpecificationName();
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());

        // Create project
        String experimentId = createExperiment(title,TestProperties.getAffymetrixSpecificationDesignName());

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();
        
        // Upload the sepecification zip file:
        upload(MageTabDataFiles.SPECIFICATION_ZIP);
        
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // - Import files
        importData(MAGE_TAB);

        reClickForText("15 items found", "link=Imported Data", 10, 30000);
        // - validate the status
        checkFileStatus("Imported", THIRD_COLUMN, NUMBER_OF_FILES);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime)/60000f);
        System.out.println("total time = " + totalTime);

        // make experiment public
        submitExperiment();
        makeExperimentPublic(experimentId);
    }
}
