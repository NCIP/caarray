

package com.example.tests.Login_UC7240;
import com.thoughtworks.selenium.*;
import junit.framework.*;
public class Login_basic_flow extends TestCase {
    private Selenium selenium;
    public Login_basic_flow(StringBuilder verificationErrors)
    {
    	selenium = new DefaultSelenium("localhost", 4444, "*firefox", "https://caarraydb-qa.nci.nih.gov/caarray/");
    	selenium.start();
    verificationErrors = new StringBuilder();
    }
    
    public Login_basic_flow() {
    	selenium.open("https://caarraydb-qa.nci.nih.gov/caarray/");
		assertEquals("caArrayDB Home", selenium.getTitle());
		selenium.type("userName", "caarraytestuser1");
		selenium.type("password", "Pass#1234");
		selenium.click("submit");
		selenium.waitForPageToLoad("30000");
		assertEquals("caArrayDB Home", selenium.getTitle());
		selenium.click("contactsLink");
		assertEquals("Search People", selenium.getTitle());

    }
    
    public void tearDown() {
    	selenium.stop();
    }
}