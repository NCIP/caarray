//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package register_for_an_account_UC7244;



import junit.framework.TestCase;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


public class register_test_1 extends TestCase {
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
		selenium.click("link=Register");
		selenium.waitForPageToLoad("30000");
		selenium.type("regForm_registrationRequest_firstName", "qa first");
		selenium.type("regForm_registrationRequest_lastName", "qa last");
		selenium.type("regForm_registrationRequest_phone", "1111111111");
		selenium.type("regForm_registrationRequest_organization", "qa organization");
		selenium.type("regForm_registrationRequest_address1", "1234 qa street");
		selenium.type("regForm_registrationRequest_city", "qa");
		selenium.type("regForm_registrationRequest_zip", "12345");
		selenium.type("regForm_registrationRequest_email", "qa@nih.gov");
		Thread.sleep(3000);
		selenium.click("regForm_0");
		selenium.waitForPageToLoad("30000");	
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}
