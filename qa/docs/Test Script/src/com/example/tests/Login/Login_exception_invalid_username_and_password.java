package com.example.tests.Login;



import com.thoughtworks.selenium.*;
import junit.framework.*;
public class Login_exception_invalid_username_and_password extends TestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*chrome", "http://cbvapp-q1001.nci.nih.gov:19280/caarray/protected/Project_list.action");
        //*iehta for internet explorer https
        //*chrome for firefox https
        //*iexplore for internet explore http
        //*firefox for firefox http
        selenium.start();
    }
    
    public void testcaarray2() {
    	selenium.open("/caarray/protected/Project_list.action");
    	selenium.type("j_username", "abcd");
    	selenium.type("j_password", "abcd");
    	selenium.click("login");
    	selenium.waitForPageToLoad("300000");
   
    }
    
    public void tearDown() {
        //selenium.stop();
    }
}

