package submit_experiment_proposal_UC7218;



	import junit.framework.TestCase;

	import com.thoughtworks.selenium.DefaultSelenium;
	import com.thoughtworks.selenium.Selenium;


	public class submit_experiment_proposal_test_1 extends TestCase {
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
	    
	    public void testcaarray() throws InterruptedException {
			selenium.open("/caarray/protected/project/workspace.action");
			selenium.type("j_username", "caarrayadmin");
			selenium.type("j_password", "caArray2!");
			selenium.click("//span/span");
			selenium.waitForPageToLoad("30000");
			selenium.click("link=Create/Propose Experiment");
			Thread.sleep(5000);
			selenium.type("projectForm_project_experiment_title", "qa experiment");
			selenium.click("//li/a/span/span");
			for (int second = 0;; second++) {
				if (second >= 10) fail("timeout");
				try { if (selenium.isTextPresent("Project with title qa experiment has been successfully saved.")) break; } catch (Exception e) {}
				Thread.sleep(1000);
			}

			selenium.click("link=My Experiment Workspace");
			selenium.waitForPageToLoad("30000");
			for (int second = 0;; second++) {
				if (second >= 10) fail("timeout");
				try { if (selenium.isTextPresent("qa experiment")) break; } catch (Exception e) {}
				Thread.sleep(1000);
			}
			
	    }
	    
	    public void tearDown() {
	        //selenium.stop();
	    }
	}

