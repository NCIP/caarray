import com.thoughtworks.selenium.*;
import junit.framework.*;
public class googletest extends TestCase {
    private Selenium sel;
    public googletest(StringBuilder verificationErrors)
    {
    sel = new DefaultSelenium("localhost", 4444, "*iehta", "https://caarraydb-qa.nci.nih.gov/caarray/");
    sel.start();
    verificationErrors = new StringBuilder();
    }
    
    public googletest() {
    	sel.open("https://caarraydb-qa.nci.nih.gov/caarray/");
		assertEquals("caArrayDB Home", sel.getTitle());
		sel.type("userName", "caarraytestuser1");
		sel.type("password", "Pass#1234");
		sel.click("submit");
		sel.waitForPageToLoad("30000");
		assertEquals("caArrayDB Home", sel.getTitle());
		sel.click("contactsLink");
		assertEquals("Search People", sel.getTitle());
		sel.click("//input[@value='Search']");
		sel.waitForPageToLoad("30000");
		assertEquals("Search People", sel.getTitle());
    }
    
    public void tearDown() {
        //sel.stop();
    }
}
