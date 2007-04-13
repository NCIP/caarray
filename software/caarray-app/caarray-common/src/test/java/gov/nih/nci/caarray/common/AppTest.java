package gov.nih.nci.caarray.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public final class AppTest extends TestCase {

    /**
     * Create the test case.
     *
     * @param testName name of the test case
     */
    public AppTest(final String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Tests getting a message.
     */
    public void testGetMessage() {
        final App theApp = new App();
        assertNotNull(theApp.getMessage());
    }
}
