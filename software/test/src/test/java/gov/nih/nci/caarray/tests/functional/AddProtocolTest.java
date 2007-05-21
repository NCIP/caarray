package gov.nih.nci.caarray.tests.functional;
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
        selenium.type("protocolForm:protocolName", "new protocol");
        selenium.type("protocolForm:protocolTitle", "new protocol title");
        selenium.type("protocolForm:protocolText", "new protocol text");
        selenium.type("protocolForm:protocolUrl", "http://www.5amsolutions.com");
        selenium.click("protocolForm:saveButton");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("new protocol"));
        assertEquals("View Protocol", selenium.getTitle());
        verifyTrue(selenium.isTextPresent("new protocol text"));
    }

    @Test
    public void testSearchForProtocols() {
        testAddProtocol();
        selenium.click("protocolForm:modifyButton");
        selenium.waitForPageToLoad("30000");
        selenium.type("protocolForm:protocolName", "updated protocol");
        selenium.type("protocolForm:protocolText", "updated protocol text");
        selenium.type("protocolForm:protocolUrl", "http://updated.5amsolutions.com");
        selenium.select("protocolForm:protocolType", "label=fractionate");
        selenium.click("protocolForm:saveButton");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("updated protocol"));
        verifyTrue(selenium.isTextPresent("updated protocol text"));
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
