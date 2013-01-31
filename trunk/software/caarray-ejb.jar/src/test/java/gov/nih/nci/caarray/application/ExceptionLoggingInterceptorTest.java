//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.junit.Test;

public class ExceptionLoggingInterceptorTest extends AbstractCaarrayTest {

    @Test
    @SuppressWarnings("PMD")
    public final void testLogException() throws Exception {
        ExceptionLoggingInterceptor interceptor = new ExceptionLoggingInterceptor();
        TestInvocationContext testContext = new TestInvocationContext();
        testContext.target = this;
        testContext.exception = new IllegalArgumentException("test IllegalArgumentException");
        try {
            interceptor.logException(testContext);
            fail ("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        testContext.exception = null;
        try {
            interceptor.logException(testContext);
        } catch (Exception e) {
            fail("Exception not expected");
        }
    }

    public void aMethod() {
        // no-op
    }

    private static class TestInvocationContext implements InvocationContext {

        private Exception exception;
        private Object target;

        public Map<String, Object> getContextData() {
            return null;
        }

        public Method getMethod() {
            try {
                return ExceptionLoggingInterceptorTest.class.getMethod("aMethod", new Class[] {});
            } catch (SecurityException e) {
                fail("Unexpected exception");
                return null;
            } catch (NoSuchMethodException e) {
                fail("Unexpected exception");
                return null;
            }
        }

        public Object[] getParameters() {
            return new Object[]{};
        }

        public Object getTarget() {
            return target;
        }

        @SuppressWarnings("PMD")
        public Object proceed() throws Exception {
            if (exception != null) {
                throw exception;
            } else {
                return null;
            }
        }

        public void setParameters(Object[] arg0) {
            // no-op
        }

    }

}
