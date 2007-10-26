package com.example.tests;



import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;
public class submit_experiment_proposal_basic_flow extends SeleneseTestCase {
    private Selenium selenium;
    public void setUp() {
    	selenium = new DefaultSelenium("localhost",
            4444, "*firefox", "http://cbvapp-q1001.nci.nih.gov:19280/caarray/protected/Project_list.action");
        //*iehta for internet explorer https
        //*chrome for firefox https
        //*iexplore for internet explore http
        //*firefox for firefox http
        selenium.start();
    }

	public void testSubmit_experiment_proposal_basic_flow() throws Exception {
		selenium.open("/caarray/protected/Project_list.action");
		selenium.type("j_username", "caarrayadmin");
		selenium.type("j_password", "f1rebird05");
		selenium.click("login");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Propose Project");
		selenium.type("projectForm_project_experiment_title", "qa experiment");
		selenium.select("projectForm_project_experiment_serviceType", "label=Partial");
		selenium.select("projectForm_project_experiment_assayType", "label=Gene Expression");
		selenium.select("projectForm_selectedOrganism", "label=canine");
		selenium.click("//img[@alt='Submit Experiment Proposal']");
		verifyTrue(selenium.isTextPresent("qa experiment"));
	}
}
