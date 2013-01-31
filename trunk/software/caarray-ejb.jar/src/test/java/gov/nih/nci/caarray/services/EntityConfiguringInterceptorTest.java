//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.security.SecurityPolicyMode;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 *
 */
public class EntityConfiguringInterceptorTest extends AbstractCaarrayTest {

    @Test
    public void testPrepareReturnValue() throws Exception {
        HibernateUtil.beginTransaction();

        EntityConfiguringInterceptor interceptor = new EntityConfiguringInterceptor();
        TestInvocationContext testContext = new TestInvocationContext();
        TestEntity entity = new TestEntity();
        assertNotNull(entity.a.getFoo());
        testContext.returnValue = entity;
        interceptor.prepareReturnValue(testContext);
        checkEntity(entity);
        Set<TestEntity> entitySet = new HashSet<TestEntity>();
        entitySet.add(new TestEntity());
        entitySet.add(new TestEntity());
        testContext.returnValue = entitySet;
        interceptor.prepareReturnValue(testContext);
        for (TestEntity nextEntity : entitySet) {
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
            return this.returnValue;
        }

        public void setParameters(Object[] arg0) {
            // empty on purpose
        }

    }

    public static class TestEntity {

        A a = new A();

        public A getA() {
            return a;
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


        public Long getId() {
            return id;
        }

        /**
         * @return the foo
         */
        @AttributePolicy(deny = "TestPolicy")
        public String getFoo() {
            return foo;
        }

        /**
         * @param foo the foo to set
         */
        public void setFoo(String foo) {
            this.foo = foo;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public B getB() {
            return b;
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

        public Long getId() {
            return null;
        }
    }

}
