//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;

import org.junit.Test;

/**
 * Test case #7959.
 *
 * Requirements: Loaded test data set includes test user and referenced Affymetrix array design.
 */
public class ImportGenePixSetTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 6;
    private static final String ORGANISM = "Bos taurus (ncbitax)";
    private static final String PROVIDER = "GenePix";
    private static final String SAMPLE_IN_SDRF = "new";
    private static final String SAMPLE_NOT_IN_SDRF = "123";

    @Test
    public void testImportAndRetrieval() throws Exception {
        String title = TestProperties.getGenepixCowName();

        // - Login
        loginAsPrincipalInvestigator();
        // - Add the array design
        importArrayDesign(GenepixArrayDataFiles.JOE_DERISI_FIX, TestProperties.getGenepixDesignName(), PROVIDER,
                ORGANISM);
        // Create project
        String experimentId = createExperiment(title, TestProperties.getGenepixDesignName(), PROVIDER, ORGANISM);

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        // Upload the following GenePix files:
        upload(GenepixArrayDataFiles.GPR_3_0_6);
        upload(GenepixArrayDataFiles.GPR_3_0_6_mod);
        upload(GenepixArrayDataFiles.GPR_4_0_1);
        upload(GenepixArrayDataFiles.GPR_4_1_1);
        upload(GenepixArrayDataFiles.EXPORTED_IDF);
        upload(GenepixArrayDataFiles.EXPORTED_SDRF);

        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, NUMBER_OF_FILES);

        // - Import files
        importData(MAGE_TAB);

        // - click on the Imported data tab and re-click until data can be found
        reClickForText(GenepixArrayDataFiles.GPR_3_0_6_mod.getName(), "link=Imported Data", 4, 60000);

        // - validate the status
        checkFileStatus("Imported", THIRD_COLUMN, NUMBER_OF_FILES);

        // - check sample names
        checkSampleNames();

        // make experiment public
        submitExperiment();
        makeExperimentPublic(experimentId);
    }

    private void checkSampleNames() {
        selenium.click("link=Annotations");
        waitForTab();
        selenium.click("link=Samples");
        waitForText("Sample name");
        assertTrue(selenium.isTextPresent(SAMPLE_IN_SDRF));
        assertFalse(selenium.isTextPresent(SAMPLE_NOT_IN_SDRF));
    }
}
