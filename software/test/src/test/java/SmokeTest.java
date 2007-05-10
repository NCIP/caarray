import com.thoughtworks.selenium.*;

public class SmokeTest extends SeleneseTestCase {

    public void setUp() throws Exception {
        System.setProperty("selenium.port", "8081");
        super.setUp("http://localhost:8080", "*iexplore");
    }

    public void testSmoke() throws Exception {
        selenium.open("/caarray/");
        selenium.click("_idJsp0:_idJsp1");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.isElementPresent("protocolForm"));
    }

}
