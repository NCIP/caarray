//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package submit_experiment_proposal_UC7218;



	import base.AbstractSeleniumTest;

import com.thoughtworks.selenium.DefaultSelenium;


	public class submit_experiment_proposal_test_2 extends AbstractSeleniumTest {
//	    private Selenium selenium;
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
	    	loginAsPrincipalInvestigator();

	    	selenium.click("link=Create/Propose Experiment");
			Thread.sleep(2000);
			for (int second = 0;; second++) {
				if (second >= 60) fail("timeout");
				try { if (selenium.isTextPresent("New Experiment")) break; } catch (Exception e) {}
				Thread.sleep(1000);
			}

			selenium.type("projectForm_project_experiment_title", "qa experiment");
			selenium.select("projectForm_project_experiment_serviceType", "label=Full");
			selenium.select("projectForm_project_experiment_assayType", "label=Gene Expression");
			selenium.select("projectForm_project_experiment_manufacturer", "label=Affymetrix");
			selenium.select("projectForm_project_experiment_organism", "label=Bovine");

			selenium.click("//li/a/span/span");
			Thread.sleep(2000);
			for (int second = 0;; second++) {
				if (second >= 60) fail("timeout");
				try { if ("Project with title qa experiment has been successfully saved.".equals(selenium.getText("successMessages"))) break; } catch (Exception e) {}
				Thread.sleep(1000);
			}

			selenium.click("link=My Experiment Workspace");
			selenium.open("/caarray/protected/project/workspace.action");
			selenium.click("link=Create/Propose Experiment");
			selenium.waitForPageToLoad("30000");
		}
	
	   
	    
	    public void tearDown() {
	        //selenium.stop();
	    }
	}

