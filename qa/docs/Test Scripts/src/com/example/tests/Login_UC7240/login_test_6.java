package com.example.tests.Login_UC7240;

import com.thoughtworks.selenium.*;
import junit.framework.*;
public class login_test_6 extends TestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*chrome", "http://array-qa.nci.nih.gov/caarray/");
        selenium.start();
    }
    
    public void testGoogle() {
		selenium.open("/caarray/protected/Project_list.action");
		selenium.type("j_username", "caarrayadmin");
		selenium.type("j_password", "");
		selenium.click("login");
		selenium.waitForPageToLoad("30000");
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}
