//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.security.SecurityPolicyMode;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 */
public class EntityConfiguringInterceptorTest extends AbstractCaarrayTest {
    private Injector injector;
    private CaArrayHibernateHelper hibernateHelper;
    private FileAccessServiceStub fasStub;

    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @Before
    public void init() {
        this.fasStub = new FileAccessServiceStub();
        this.injector = createInjector();
        this.hibernateHelper = this.injector.getInstance(CaArrayHibernateHelper.class);
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(DataStorageFacade.class).toInstance(
                        EntityConfiguringInterceptorTest.this.fasStub.createStorageFacade());
                requestStaticInjection(gov.nih.nci.caarray.services.EntityPruner.class);
                requestStaticInjection(gov.nih.nci.caarray.services.EntityConfiguringInterceptor.class);
            }
        });
    }

    @Test
    public void testPrepareReturnValue() throws Exception {
        this.hibernateHelper.beginTransaction();

        final EntityConfiguringInterceptor interceptor = new EntityConfiguringInterceptor();
        final TestInvocationContext testContext = new TestInvocationContext();
        final TestEntity entity = new TestEntity();
        assertNotNull(entity.a.getFoo());
        testContext.returnValue = entity;
        interceptor.prepareReturnValue(testContext);
        checkEntity(entity);
        final Set<TestEntity> entitySet = new HashSet<TestEntity>();
        entitySet.add(new TestEntity());
        entitySet.add(new TestEntity());
        testContext.returnValue = entitySet;
        interceptor.prepareReturnValue(testContext);
        for (final TestEntity nextEntity : entitySet) {
            checkEntity(nextEntity);
        }
    }

    private void checkEntity(TestEntity entity) {
        assertNotNull(entity.a.getId());
        assertNull(entity.a.getFoo());
        assertNull(entity.a.getB());
    }

    private static class TestInvocationContext implements InvocationContext {

        private Object returnValue;

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
            return this.returnValue;
        }

        @Override
        public void setParameters(Object[] arg0) {
            // empty on purpose
        }

    }

    public static class TestEntity {

        A a = new A();

        public A getA() {
            return this.a;
        }

        public void setA(A a) {
            this.a = a;
        }

    }

    public static class A extends AbstractCaArrayObject {

        private static final long serialVersionUID = 1L;
        private B b = new B();
        private Long id = 1L;
        private String foo = "Harrumph";

        @Override
        public Long getId() {
            return this.id;
        }

        /**
         * @return the foo
         */
        @AttributePolicy(deny = "TestPolicy")
        public String getFoo() {
            return this.foo;
        }

        /**
         * @param foo the foo to set
         */
        public void setFoo(String foo) {
            this.foo = foo;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        public B getB() {
            return this.b;
        }

        public void setB(B b) {
            this.b = b;
        }

        @Override
        public Set<SecurityPolicy> getRemoteApiSecurityPolicies(User currentUser) {
            return Collections.singleton(new SecurityPolicy("TestPolicy", SecurityPolicyMode.BLACKLIST));
        }
    }

    public static class B implements PersistentObject {

        private static final long serialVersionUID = 1L;

        @Override
        public Long getId() {
            return null;
        }
    }

}
