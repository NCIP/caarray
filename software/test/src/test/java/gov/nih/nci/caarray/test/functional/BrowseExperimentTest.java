//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;


import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import org.junit.Test;

/**
 *
 * Tests browsing experiments and deleting an experiment with cel files.
 *
 */
public class BrowseExperimentTest extends AbstractSeleniumTest {

    @Test
    public void testBrowsing() throws Exception {
        String title = "browsable " + System.currentTimeMillis();
        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());

        // - Create an Experiment
        String experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // - Submit Experiment Proposal
        lockExperimentFromEdits();
        makeExperimentPublic(experimentId);

        // - logout
        selenium.click("link=Logout");
        waitForText("Browse caArray");

        // - Click on Experiments link on the login page
        selenium.click("link=Experiments");
        waitForText("found");
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Organisms
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Organisms");
        waitForText("found");
        // Click on the Homo sapiens tab incase there is more than one tab
        selenium.click("link=Homo sapiens (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Array Providers
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Array Providers");
        waitForText("found");
        // Click on the Affymetrix tab incase there is more than one tab
        selenium.click("link=Affymetrix (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Unique Array Designs
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Unique Array Designs");
        waitForText("found");
        // Click on the Affymetrix tab incase there is more than one tab
        selenium.click("link=Test3 (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();
    }

    @Test
    public void testDeleteProject() throws Exception {
        String title = "delete " + System.currentTimeMillis();
        loginAsPrincipalInvestigator();
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        String experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // file upload based on ImportAffymetrixChpTest.testImportAndRetrieval()
        // - go to the data tab
        pause(1000);
        selenium.click("link=Data");
        waitForTab();
        upload(AffymetrixArrayDataFiles.TEST3_SPECIFICATION_ZIP);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, 1);
        // - Import files
        importData(MAGE_TAB);
        
        selenium.click("link=My Experiment Workspace");
        waitForText("Public Access");

        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        if (row == -1){
            fail("Did not find experiment with id = " + experimentId);
        }
        pause(1000);
        assertTrue("Delete icon is missing at row "+row+" column 10", selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[10]/a/img"));
        selenium.chooseCancelOnNextConfirmation();
        selenium.click("//tr[" + row + "]/td[10]/a/img");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to delete this experiment\\?$"));
        pause(1000);
        assertEquals(row, getExperimentRow(experimentId, ZERO_COLUMN));
        selenium.click("//tr[" + row + "]/td[10]/a/img");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to delete this experiment\\?$"));
        waitForText("Experiment has been deleted.");
        assertFalse(selenium.isTextPresent(experimentId));

        // testing the delete icon is not present when an experiment is in the 'locked' state.
        experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());
        lockExperimentFromEdits();
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // ensure the delete icon is not present
        assertFalse(selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[8]/a/img"));
    }

    private void assertColumnHeaders() {
        verifyEquals("Experiment ID", selenium.getText("link=Experiment ID"));
        verifyEquals("Experiment Title", selenium.getText("link=Experiment Title"));
        assertTrue("Primary Contact header not found", selenium.isTextPresent("Primary Contact"));
        verifyEquals("Organism", selenium.getText("link=Organism"));
        assertTrue("Condition/Disease State header not found", selenium.isTextPresent("Condition/Disease State"));
        assertTrue("Samples header not found", selenium.isTextPresent("Samples"));
        verifyEquals("Updated", selenium.getText("link=Updated"));
    }

}
