//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class HibernateSessionInterceptorTest extends AbstractCaarrayTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                requestStaticInjection(gov.nih.nci.caarray.services.HibernateSessionInterceptor.class);
            }
        });
    }

    @Before
    public void setUp() {
        hibernateHelper.setFiltersEnabled(false);
    }

    @Test
    public void testManageHibernateSession() throws Exception {
        final TestInvocationContext testContext = new TestInvocationContext();
        final HibernateSessionInterceptor interceptor = new HibernateSessionInterceptor();
        final Transaction tx = hibernateHelper.getCurrentSession().beginTransaction();
        interceptor.manageHibernateSession(testContext);
        tx.commit();
        assertTrue(testContext.getHibernateSession() != null);
        assertTrue(testContext.hibernateSessionOpenInCall);
    }

    private static class TestInvocationContext implements InvocationContext {

        Session hibernateSession;
        boolean hibernateSessionOpenInCall;

        @Override
        public Map<String, Object> getContextData() {
            return null;
        }

        @Override
        public Method getMethod() {
            return null;
        }

        @Override
        public Object[] getParameters() {
            return new Object[] {};
        }

        @Override
        public Object getTarget() {
            return null;
        }

        @Override
        public Object proceed() {
            setHibernateSession();
            return null;
        }

        @Override
        public void setParameters(Object[] arg0) {
            // empty on purpose
        }

        public Session getHibernateSession() {
            return this.hibernateSession;
        }

        public void setHibernateSession() {
            this.hibernateSession = hibernateHelper.getCurrentSession();
            this.hibernateSessionOpenInCall = this.hibernateSession.isOpen();
        }
    }

}
