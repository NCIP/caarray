package submit_experiment_proposal_UC7218;



	import junit.framework.TestCase;

	import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


	public class submit_experiment_proposal_test_2 extends TestCase {
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
			selenium.select("projectForm_project_experiment_serviceType", "label=Full");
			selenium.select("projectForm_project_experiment_assayType", "label=Gene Expression");
			selenium.select("projectForm_project_experiment_organism", "label=Bovine");
			selenium.click("//li/a/span/span");
			Thread.sleep(5000);
			selenium.click("link=Contacts");
			Thread.sleep(5000);
			selenium.type("projectForm_primaryInvestigator_email", "keeneron@mail.nig.gov");
			selenium.type("projectForm_primaryInvestigator_phone", "2222222222");
			selenium.click("//li/a/span/span");
			Thread.sleep(5000);
			selenium.click("link=Annotations");
			Thread.sleep(5000);
			selenium.select("projectForm_project_experiment_experimentDesignType", "label=genetic_modification_design");
			selenium.click("//li/a/span/span");
			Thread.sleep(5000);
			selenium.click("link=Experimental Factors");
			Thread.sleep(5000);
			selenium.click("//div/a/span/span");
			Thread.sleep(5000);
			selenium.type("projectForm_currentFactor_name", "qa factor");
			selenium.click("//li[2]/a/span/span");
			Thread.sleep(5000);
			selenium.click("link=Samples");
			Thread.sleep(5000);
			selenium.click("//div/a/span/span");
			Thread.sleep(5000);
			selenium.type("projectForm_currentSample_name", "qa sample");
			selenium.click("//li[2]/a/span/span");
			Thread.sleep(5000);
			selenium.click("link=Publications");
			Thread.sleep(5000);
			selenium.click("//div/a/span/span");
			Thread.sleep(5000);
			selenium.type("projectForm_currentPublication_title", "qa publication");
			selenium.click("//li[2]/a/span/span");
			selenium.click("//span/span");
		}
			
	   
	    
	    public void tearDown() {
	        //selenium.stop();
	    }
	}

