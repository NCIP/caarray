//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package Login_UC7240;



import junit.framework.TestCase;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


public class login_test_4 extends TestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*iexplore", "http://array-qa.nci.nih.gov/caarray/");
    	//*iehta - internet explore experimental browser for https
    	//*chrome - firefox experimental browser for https
    	//*firefox - firefox browser
    	//*iexplore - internet explore browser
        selenium.start();
    }
    
    public void testcaarray() throws InterruptedException {
		selenium.open("/caarray/protected/project/workspace.action");
		selenium.type("j_username", "CAARRAYADMIN");
		selenium.type("j_password", "caArray2!");
		Thread.sleep(5000);
		selenium.click("//span/span");
		selenium.waitForPageToLoad("30000");
		for (int second = 0;; second++) {
			if (second >= 10) fail("timeout");
			try { if (selenium.isTextPresent("Invalid username and/or password, please try again.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}
