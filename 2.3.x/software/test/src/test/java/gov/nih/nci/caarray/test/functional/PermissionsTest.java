//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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

    @Test
    public void testCollaborators() throws Exception {
        String groupName = "fellows " + System.currentTimeMillis();
        // - Login
        loginAsPrincipalInvestigator();

        // - Create a Collaboration Group
        createCollaborationGroup(groupName);

        // - logout
        selenium.click("link=Logout");
        waitForText("Browse caArray");

    }

    /**
     * @param string
     */
    private void createCollaborationGroup(String groupName) {
        selenium.click("link=Manage Collaboration Groups");
        waitForText("Group Members");
        selenium.click("link=Add a New Collaboration Group");
        waitForText("Choose a name for the group.");
        selenium.type("newGroupForm_groupName", groupName);
        selenium.click("link=Save");
        selenium.waitForPageToLoad("30000");
        // -  find the added group and click the edit icon
        int row = getExperimentRow(groupName, ZERO_COLUMN);
        // edit icon to add members
        selenium.click("//tr[" + row + "]/td[3]/a/img");
        waitForText("Remove");
        selenium.click("link=Add a New Group Member");
        waitForText("Search for users by choosing filter criteria");
        // filter - shows all users
        clickAndWait(FILTER_BUTON);
        // add all the users
        addUsers();

    }

    private void addUsers() {
        for (int i = 0;; i++) {
            clickAndWait(ADD_USER_BUTTON);
            selenium.waitForPageToLoad("30000");
            if ("Nothing found to display.".equalsIgnoreCase(selenium.getTable("row.1.0"))) {
                return;
            }
            // safety catch
            if (i > 50) {
                fail("attempted to add more than 50 users");
            }
        }
    }


}
