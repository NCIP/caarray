//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package submit_experiment_proposal_UC7218;

import base.AbstractSeleniumTest;



public class submit_experiment_proposal_test_1 extends AbstractSeleniumTest {
	

//	public void setUp() {
//		selenium = new DefaultSelenium("localhost", 4444, "*firefox",
//				"http://array-qa.nci.nih.gov/caarray/");
//		// *iehta - internet explore experimental browser for https
//		// *chrome - firefox experimental browser for https
//		// *firefox - firefox browser
//		// *iexplore - internet explore browser
//		selenium.start();
//	}

	public void testcaarray() throws InterruptedException {
		loginAsPrincipalInvestigator();
		Thread.sleep(2000);

		
		selenium.click("link=Create/Propose Experiment");
		Thread.sleep(2000);
		selenium.type("projectForm_project_experiment_title", "qa experiment");
		selenium.click("//li/a/span/span");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Service Type must be set")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Assay Type must be set")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Provider must be set")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("Organism must be set")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("You must select at least one tissue site.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isTextPresent("You must select at least one tissue type.")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

	}

	
	
	public void tearDown() {
		// selenium.stop();
	}
}
