//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;

import org.junit.Test;

/**
 * 
 * Use Case UC#7231. Test Case #10320, #10321, #10324, #10325 Requirements: Browse by Experiments, Organism, Array
 * Providers, and Unique Array Designs
 * 
 */
public class PermissionsTest extends AbstractSeleniumTest {
    private final String ADD_USER_BUTTON = "//img[@alt='Add']";
    private final String FILTER_BUTON = "//li[2]/a/span/span";
    private final String EDIT_ACCESS_CONTROL_BUTTON = "//form[@id='collaborators_form']/a/span/span";
    private final String SAVE_BUTTON = "//ul[@id='btnrow']/li[2]/a/span/span";
    @Test
    public void testPermissions() throws Exception {
        String groupName = "fellows " + System.currentTimeMillis();
        String experimentName = "Permission Test"+ System.currentTimeMillis();
        
        // - Login and create an experiment
        loginAsPrincipalInvestigator();
        String experimentId = createExperiment(experimentName);
        
        logout();
        loginAs("caarrayuser");

        // makes sure the experiment cannot be viewed by other users
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
       // String value = selenium.is
        assertTrue("Experiment " + experimentId +" was found",row == -1);
        
        logout();
        // log back in as PI and submit the experiment and set permissions
        loginAsPrincipalInvestigator();
        
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // click the edit icon
        selenium.click("//table[@id='row']/tbody/tr[" + row + "]/td[9]/a/img");
        waitForText("Overall Experiment Characteristics");
        lockExperimentFromEdits();
        
        createCollaborationGroup(groupName);
        addUsers();
        
        // set the new group on the experiment
        selenium.click("link=My Experiment Workspace");
        waitForText("Assay Type");
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // click the permission icon
        selenium.click("//table[@id='row']/tbody/tr["+row+"]/td[7]/a/img");
        waitForText("Who May Access this Experiment");
        // select the collaboration group for the experiment
        selenium.select("collaborators_form_collaboratorGroup_id", "label=" + groupName);
        
        // set the permission
        selenium.click(EDIT_ACCESS_CONTROL_BUTTON);
        waitForText("Read/Write Selective");
        
        // Experiment Access ddw
        selenium.select("profileForm_accessProfile_securityLevel", "label=Read"); 
        selenium.click(SAVE_BUTTON);
        waitForText("The permissions have been updated");
    
        logout();
        loginAs("caarrayuser");
        
        // makes sure the experiment can be viewed by other users
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        
        // experiment was found
        assertTrue("Experiment " + experimentId +" was found",row != -1);
        
        // the permission is read only so ensure the edit icon is not avaliable
        assertFalse(selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[9]/a/img"));
        logout();
    }


    private void logout() {
        selenium.click("link=Logout");
        waitForText("Browse caArray");    }


    private void addUsers() {
        selenium.type("targetUserLastName", "user");
        selenium.type("targetUserFirstName", "caarray");
        clickAndWait(FILTER_BUTON);
        clickAndWait(ADD_USER_BUTTON);
        waitForText("caArray User (caarrayuser) successfully added to group");
    }

}
