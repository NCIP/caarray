package com.example.tests.Login_UC7240;

import com.thoughtworks.selenium.*;
import junit.framework.*;
public class Login_basic_flow extends TestCase {
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
    
    public void testcaarray() {
		selenium.open("/caarray/protected/project/workspace.action");
		selenium.type("j_username", "caarrayadmin");
		selenium.type("j_password", "caArray2!");
		selenium.click("//span/span");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Experiment Workspace"));
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}