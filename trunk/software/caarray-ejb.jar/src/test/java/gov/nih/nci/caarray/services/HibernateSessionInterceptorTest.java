//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

public class HibernateSessionInterceptorTest extends AbstractCaarrayTest {

    @Before
    public void setUp() {
        HibernateUtil.setFiltersEnabled(false);
    }

    @Test
    public void testManageHibernateSession() throws Exception {
        TestInvocationContext testContext = new TestInvocationContext();
        HibernateSessionInterceptor interceptor = new HibernateSessionInterceptor();
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        interceptor.manageHibernateSession(testContext);
        tx.commit();
        assertTrue(testContext.getHibernateSession() != null);
        assertTrue(testContext.hibernateSessionOpenInCall);
    }

    private static class TestInvocationContext implements InvocationContext {

        Session hibernateSession;
        boolean hibernateSessionOpenInCall;

        public Map<String, Object> getContextData() {
            return null;
        }

        public Method getMethod() {
            return null;
        }

        public Object[] getParameters() {
            return new Object[]{};
        }

        public Object getTarget() {
            return null;
        }

        public Object proceed() {
            setHibernateSession();
            return null;
        }

        public void setParameters(Object[] arg0) {
            // empty on purpose
        }

        public Session getHibernateSession() {
            return this.hibernateSession;
        }

        public void setHibernateSession() {
            this.hibernateSession = HibernateUtil.getCurrentSession();
            this.hibernateSessionOpenInCall = this.hibernateSession.isOpen();
        }
    }

}
