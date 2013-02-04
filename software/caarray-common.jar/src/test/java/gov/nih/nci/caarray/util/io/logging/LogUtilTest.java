//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.io.logging;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Test the <code>LogUtil</code> class.
 */
public class LogUtilTest extends AbstractCaarrayTest {

    private final TestLog testLog = new TestLog() {
        @Override
        public boolean isDebugEnabled() {return true;}
    };

    @Test
    public void testLogSubsystemEntry() {
        LogUtil.logSubsystemEntry(testLog, 1, "two", null);
        String expectedMessage = "[SUBSYSTEM ENTRY] testLogSubsystemEntry\n\t"
            + "argument[Integer] = 1\n\targument[String] = two\n\targument[unknown type] = null";
        assertEquals("DEBUG", testLog.getLogLevel());
        assertEquals(expectedMessage, testLog.getMessage());
    }

    @Test
    public void testLogSubsystemExit() {
        LogUtil.logSubsystemExit(testLog);
        String expectedMessage = "[SUBSYSTEM EXIT] testLogSubsystemExit";
        assertEquals("DEBUG", testLog.getLogLevel());
        assertEquals(expectedMessage, testLog.getMessage());
    }

    /**
     * Logging stub for test.
     */
    private static class TestLog extends Logger {

        private Object loggedMessage;
        private String logLevel;

        public TestLog() {
            super("TestLog");
        }

        String getLogLevel() {
            return logLevel;
        }

        Object getMessage() {
            return loggedMessage;
        }

        @Override
        public void debug(Object message) {
            logLevel = "DEBUG";
            this.loggedMessage = message;
        }

        @Override
        public void debug(Object message, Throwable t) {
            this.loggedMessage = message;
        }

        @Override
        public void error(Object message) {
            this.loggedMessage = message;
        }

        @Override
        public void error(Object message, Throwable t) {
            this.loggedMessage = message;
        }

        @Override
        public void fatal(Object message) {
            this.loggedMessage = message;
        }

        @Override
        public void fatal(Object message, Throwable t) {
            this.loggedMessage = message;
        }

        @Override
        public void info(Object message) {
            this.loggedMessage = message;
        }

        @Override
        public void info(Object message, Throwable t) {
            this.loggedMessage = message;
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        public boolean isErrorEnabled() {
            return false;
        }

        public boolean isFatalEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void trace(Object message) {
            logLevel = "TRACE";
            this.loggedMessage = message;
        }

        @Override
        public void trace(Object message, Throwable t) {
            this.loggedMessage = message;
        }

        @Override
        public void warn(Object message) {
            this.loggedMessage = message;
        }

        @Override
        public void warn(Object message, Throwable t) {
            this.loggedMessage = message;
        }

    }

}
