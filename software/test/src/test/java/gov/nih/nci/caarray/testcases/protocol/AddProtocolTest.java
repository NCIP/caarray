package gov.nih.nci.caarray.testcases.protocol;
import org.junit.Test;

import gov.nih.nci.caarray.testcases.base.AbstractSeleniumTest;

/**
 * Test script for Add Protocol.
 *
 * @author ETavela
 */
public class AddProtocolTest extends AbstractSeleniumTest {

    @Test
    public void testAddProtocol() {
        selenium.open("/caarray/");
        selenium.click("protocolMenu:addProtocol");
        selenium.waitForPageToLoad("30000");
        selenium.type("protocolForm:protocolName", "name");
        selenium.type("protocolForm:protocolTitle", "title");
        selenium.type("protocolForm:protocolText", "some text");
        selenium.type("protocolForm:protocolUrl", "http://www.5amsolutions.com");
        selenium.select("protocolForm:protocolType", "label=acclimatization");
        selenium.click("protocolForm:saveButton");
        selenium.waitForPageToLoad("30000");
    }

}
