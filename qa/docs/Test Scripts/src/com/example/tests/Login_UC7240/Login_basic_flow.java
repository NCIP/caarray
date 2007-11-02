package com.example.tests.Login_UC7240;

import com.thoughtworks.selenium.*;
import junit.framework.*;
public class Login_basic_flow extends TestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*chrome", "http://array-qa.nci.nih.gov/caarray/");
    	//*iehta - internet explore experimental browser for https
    	//*chrome - firefox experimental browser for https
    	//*firefox - firefox browser
    	//*iexplore - internet explore browser
        selenium.start();
    }
    
    public void testcaarray() {
		selenium.open("/caarray/protected/Project_list.action");
		selenium.type("j_username", "caarrayadmin");
		selenium.type("j_password", "f1rebird05");
		selenium.click("login");
		selenium.waitForPageToLoad("30000");
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}