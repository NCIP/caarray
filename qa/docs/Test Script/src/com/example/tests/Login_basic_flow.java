package com.example.tests;



import com.thoughtworks.selenium.*;
import junit.framework.*;
public class Login_basic_flow extends TestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*iexplore", "http://cbvapp-q1001.nci.nih.gov:19280/caarray/protected/Project_list.action");
        //*iehta for internet explorer https
        //*chrome for firefox https
        //*iexplore for internet explore http
        //*firefox for firefox http
        selenium.start();
    }
    
    public void testcaarray2() {
    	selenium.open("/caarray/protected/Project_list.action");
    	selenium.type("j_username", "caarrayadmin");
    	selenium.type("j_password", "f1rebird05");
    	selenium.click("login");
    	selenium.waitForPageToLoad("300000");
   
    }
    
    public void tearDown() {
        //sel.stop();
    }
}

