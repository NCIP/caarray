//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package base;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * Base class for all functional tests that use Selenium Remote Control. Provides proper set up in order to be called by
 * caArray's ant script.
 *
 * @author tavelae
 */

public abstract class AbstractSeleniumTest extends SeleneseTestCase {

    private static final int PAGE_TIMEOUT_SECONDS = 180;

	private static final int PAGE_SIZE = 20;

	protected static int RECORD_TIMEOUT_SECONDS = 240;
    @Override
    public void setUp() throws Exception {
    	selenium = new DefaultSelenium("localhost",
                4444, "*iexplore", "http://array-qa.nci.nih.gov/caarray/");
        	//*iehta - internet explore experimental browser for https
        	//*chrome - firefox experimental browser for https
        	//*firefox - firefox browser
        	//*iexplore - internet explore browser
            selenium.start();
    }
    private String toMillisecondsString(int seconds) {
        return String.valueOf(seconds * 1000);
    }


    protected void waitForText(String theText){
	for (int second = 0;; second++) {
		if (second >= 10) fail("timeout");
		try { if (theText.equals(selenium.getText(theText))) break; } catch (Exception e) {}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    }

    protected void waitForPageToLoad() {
        selenium.waitForPageToLoad(toMillisecondsString(PAGE_TIMEOUT_SECONDS));
    }


    protected void clickAndWait(String linkOrButton) {
        selenium.click(linkOrButton);
        waitForPageToLoad();
    }

    protected void loginAsPrincipalInvestigator() {
    	selenium.open("/caarray/protected/project/workspace.action");

        selenium.type("j_username", "caarrayadmin");
        selenium.type("j_password", "caArray2!");
        clickAndWait("//span/span");

    }

    protected void  upload(File file) throws IOException, InterruptedException {
        upload(file, RECORD_TIMEOUT_SECONDS);
    }

    protected void upload(File file, int timeoutSeconds) throws IOException, InterruptedException {
        String filePath = file.getCanonicalPath().replace('/', File.separatorChar);
        selenium.type("upload", filePath);
        selenium.click("link=Upload");
        waitForAction(timeoutSeconds);
        assertTrue(selenium.isTextPresent(file.getName()));
    }

    protected void waitForElementWithId(String id, int timeoutSeconds) {
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementById('" + id
                + "') != null", toMillisecondsString(timeoutSeconds));
    }

    protected void waitForElementWithId(String id) {
        waitForElementWithId(id, PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForAction() {
        waitForAction(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForAction(int timeoutSeconds) {
        waitForDiv("submittingText", timeoutSeconds);
    }

    protected void waitForTab() {
        waitForTab(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForTab(int timeoutSeconds) {
        waitForDiv("loadingText", timeoutSeconds);
    }

    protected void waitForDiv(String divId) {
        waitForDiv(divId, PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForDiv(String divId, int timeoutSeconds) {
        selenium.waitForCondition("element = selenium.browserbot.getCurrentWindow().document.getElementById('" +
                divId + "'); element != null && element.style.display == 'none';", toMillisecondsString(timeoutSeconds));
    }

    protected void waitForSecondLevelTab() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitForSecondLevelTab(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForSecondLevelTab(int timeoutSeconds) {
        waitForElementWithId("tabboxlevel2wrapper", timeoutSeconds);
    }



protected void findTextOnPages(String theText) {
    int row = 3;
    for (int i = 1; i < PAGE_SIZE; i++) {
        if (i % PAGE_SIZE == 20) {
            // - switch to next page
            selenium.click("link=Next");
            waitForAction();
            row = 1;
        }
        assertEquals(theText, selenium.getTable("row."+(row++)+".1"));
//      assertEquals("qa experiment", selenium.getTable("row.9.1"));
    }
}
}
//}
