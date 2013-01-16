//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.web.struts2.validator.HibernateValidator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

/**
 * Note: the actual HibernateValidator class has been moved to nci-commons, but the test remains here because hibernate,
 * etc isn't available there for testing.
 * 
 * 
 * @author Scott Miller
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class HibernateValidatorTest extends AbstractBaseStrutsTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    private TestAction action;
    private Transaction tx;

    private ValidatorContext validatorContext;
    private HibernateValidator validator;

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
        return Guice.createInjector(new CaArrayHibernateHelperModule());
    }

    @Before
    public void onSetUp() {
        this.action = new TestAction();
        this.validatorContext = new DelegatingValidatorContext(this.action);
        validator = new HibernateValidator();
        validator.setValidatorContext(validatorContext);

        CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
        tx = hibernateHelper.beginTransaction();

    }

    public void teardown() {
        hibernateHelper.rollbackTransaction(tx);
    }

    @Test
    public void testHibernateValidatorWithNullObject() throws Exception {
        final ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.set("source", null);
        this.validator.setValueStack(valueStack);
        this.validator.setFieldName("source");
        this.validator.validate(null);
        assertFalse(this.validatorContext.hasActionErrors());
        assertFalse(this.validatorContext.hasFieldErrors());
    }

    @Test
    public void testHibernateValidatorWithInvalidObject() throws Exception {
        Source source = new Source();

        ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.set("source", source);
        this.validator.setValueStack(valueStack);
        this.validator.setFieldName("source");
        this.validator.validate(null);
        assertFalse(this.validatorContext.hasActionErrors());
        assertTrue(this.validatorContext.hasFieldErrors());
        assertEquals(1, this.validatorContext.getFieldErrors().size());
        final Map.Entry<String, List<String>> error = this.validatorContext.getFieldErrors().entrySet().iterator()
                .next();
        assertEquals("source.name", error.getKey());
        assertEquals(1, error.getValue().size());
        assertEquals("name must be set", error.getValue().get(0));
    }

    @Test
    public void testHibernateValidatorWithValidObject() throws Exception {
        final Source source = new Source();
        source.setName("test name 1");

        ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.set("source", source);
        this.validator.setValueStack(valueStack);
        this.validator.setFieldName("source");
        this.validator.validate(null);
        assertFalse(this.validatorContext.hasActionErrors());
    }

    @Test
    public void testHibernateValidatorListProcessing() throws Exception {
        final List<Source> sources = new ArrayList<Source>();
        Source s = new Source();
        s.setName("test name 3");
        sources.add(s);

        s = new Source();
        sources.add(s);

        sources.add(null);

        s = new Source();
        s.setName("test name 4");
        sources.add(s);

        final ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.set("sourceList", sources);
        this.validator.setValueStack(valueStack);
        this.validator.setFieldName("sourceList");
        this.validator.validate(null);

        assertFalse(this.validatorContext.hasActionErrors());
        assertTrue(this.validatorContext.hasFieldErrors());
        assertEquals(1, this.validatorContext.getFieldErrors().size());
        final Map.Entry<String, List<String>> error = this.validatorContext.getFieldErrors().entrySet().iterator()
                .next();
        assertEquals("sourceList[1].name", error.getKey());
        assertEquals(1, error.getValue().size());
        assertEquals("name must be set", error.getValue().get(0));
    }

    @Test
    public void testHibernateValidatorArrayProcessing() throws Exception {
        final Source s1 = new Source();
        s1.setName("test name 3");
        final Source s2 = new Source();
        s2.setName("test name 4");
        final Source[] sources = { s1, new Source(), null, s2 };

        final ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.set("sourceList", sources);
        this.validator.setValueStack(valueStack);
        this.validator.setFieldName("sourceList");
        this.validator.validate(null);

        assertFalse(this.validatorContext.hasActionErrors());
        assertTrue(this.validatorContext.hasFieldErrors());
        assertEquals(1, this.validatorContext.getFieldErrors().size());
        final Map.Entry<String, List<String>> error = this.validatorContext.getFieldErrors().entrySet().iterator()
                .next();
        assertEquals("sourceList[1].name", error.getKey());
        assertEquals(1, error.getValue().size());
        assertEquals("name must be set", error.getValue().get(0));
    }

    /**
     * The action for this test case.
     */
    private class TestAction extends ActionSupport {
    }
}
