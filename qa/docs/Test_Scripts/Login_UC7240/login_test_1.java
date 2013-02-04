//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package Login_UC7240;



import base.AbstractSeleniumTest;


public class login_test_1 extends AbstractSeleniumTest {
//    private Selenium selenium;

    
    public void testcaarray() throws InterruptedException {
		loginAsPrincipalInvestigator();
		for (int second = 0;; second++) {
			if (second >= 10) fail("timeout");
			try { if (selenium.isTextPresent("Experiment Workspace")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
		
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}
