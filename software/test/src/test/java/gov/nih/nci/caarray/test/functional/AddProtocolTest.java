package gov.nih.nci.caarray.test.functional;
import org.junit.Test;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;

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
        selenium.type("protocolForm:protocolName", "new protocol");
        selenium.type("protocolForm:protocolDescription", "new protocol description");
        selenium.type("protocolForm:protocolContact", "new protocol contact");
        selenium.type("protocolForm:protocolHardware", "new protocol hardware");
        selenium.type("protocolForm:protocolSoftware", "new protocol software");
        selenium.type("protocolForm:protocolUrl", "http://www.5amsolutions.com");
        selenium.click("protocolForm:saveButton");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("new protocol"));
        assertEquals("View Protocol", selenium.getTitle());
        verifyTrue(selenium.isTextPresent("new protocol description"));
    }

    @Test
    public void testSearchForProtocols() {
        testAddProtocol();
        selenium.click("protocolForm:modifyButton");
        selenium.waitForPageToLoad("30000");
        selenium.type("protocolForm:protocolName", "updated protocol");
        selenium.type("protocolForm:protocolDescription", "updated protocol description");
        selenium.type("protocolForm:protocolUrl", "http://updated.5amsolutions.com");
        selenium.select("protocolForm:protocolType", "label=fractionate");
        selenium.click("protocolForm:saveButton");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("updated protocol"));
        verifyTrue(selenium.isTextPresent("updated protocol description"));
        verifyTrue(selenium.isTextPresent("http://updated.5amsolutions.com"));
        verifyTrue(selenium.isTextPresent("fractionate"));
        selenium.click("protocolMenu:searchProtocol");
        selenium.waitForPageToLoad("30000");
        selenium.type("searchProtocolForm:protocolName", "updated protocol");
        selenium.select("searchProtocolForm:protocolType", "label=fractionate");
        selenium.click("searchProtocolForm:searchButton");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("updated protocol"));
    }

}
