//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.ActionValidatorManagerFactory;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Note: the actual HibernateValidator class has been moved to nci-commons, but the test remains here because
 * hibernate, etc isn't available there for testing.
 * @author Scott Miller
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class HibernateValidatorTest extends AbstractBaseStrutsTest {

    private TestAction action;
    private Transaction tx;

    @Before
    public void onSetUp() {
        this.action = new TestAction();
        Source s1 = new Source();
        s1.setName("test name 1");

        Source s2 = new Source();
        s2.setName("test name 2");

        this.action.setSource(s1);
        this.action.setSource2(s2);
        tx = HibernateUtil.beginTransaction();
    }
    
    public void teardown() {
        HibernateUtil.rollbackTransaction(tx);        
    }

    @Test
    public void testHibernateValidatorWithNullObject() throws Exception {
        this.action.setSource(null);
        ActionValidatorManager avm = ActionValidatorManagerFactory.getInstance();
        avm.validate(this.action, null);
        Map<?, ?> fieldErrors = this.action.getFieldErrors();

        assertFalse(this.action.hasErrors());
        assertEquals(0, fieldErrors.size());
    }

    @Test
    public void testHibernateValidatorWithInvalidObject() throws Exception {
        this.action.setSource(new Source());
        ActionValidatorManager avm = ActionValidatorManagerFactory.getInstance();
        avm.validate(this.action, null);
        Map<?, ?> fieldErrors = this.action.getFieldErrors();

        assertTrue(this.action.hasErrors());
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("source.name"));
    }

    @Test
    public void testHibernateValidatorWithValidObject() throws Exception {
        ActionValidatorManager avm = ActionValidatorManagerFactory.getInstance();
        avm.validate(this.action, null);
        Map<?, ?> fieldErrors = this.action.getFieldErrors();

        assertFalse(this.action.hasErrors());
        assertEquals(0, fieldErrors.size());
    }

    @Test
    public void testHibernateValidatorListProcessing() throws Exception {
        Source s = new Source();
        s.setName("test name 3");
        this.action.getSourceList().add(s);

        s = new Source();
        this.action.getSourceList().add(s);

        this.action.getSourceList().add(null);

        s = new Source();
        s.setName("test name 4");
        this.action.getSourceList().add(s);

        ActionValidatorManager avm = ActionValidatorManagerFactory.getInstance();
        avm.validate(this.action, null);
        Map<?, ?> fieldErrors = this.action.getFieldErrors();

        assertTrue(this.action.hasErrors());
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("sourceList[1].name"));
    }

    @Test
    public void testHibernateValidatorArrayProcessing() throws Exception {
        Source s1 = new Source();
        s1.setName("test name 3");
        Source s2 = new Source();
        s2.setName("test name 4");
        this.action.setSourceArray(new Source[] { s1, new Source(), null, s2 });

        ActionValidatorManager avm = ActionValidatorManagerFactory.getInstance();
        avm.validate(this.action, null);
        Map<?, ?> fieldErrors = this.action.getFieldErrors();

        assertTrue(this.action.hasErrors());
        assertEquals(1, fieldErrors.size());
        assertTrue(fieldErrors.containsKey("sourceArray[1].name"));
    }

    /**
     * The action for this test case.
     */
    private class TestAction extends ActionSupport {
        private static final long serialVersionUID = 1L;

        private Source source = new Source();
        private Source source2 = new Source();
        private Source[] sourceArray = null;
        private List<Source> sourceList = new ArrayList<Source>();

        /**
         * Test action does nothing.
         *
         * @return the directive for the next action / page to be directed to
         */
        @Override
        public String execute() {
            return Action.SUCCESS;
        }

        @CustomValidator(type = "hibernate", parameters = { @ValidationParameter(name = "resourceKeyBase", value = "experiment.sources") })
        public Source getSource() {
            return this.source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        @CustomValidator(type = "hibernate", parameters = { @ValidationParameter(name = "resourceKeyBase", value = "experiment.sources"), @ValidationParameter(name = "appendPrefix", value = "false") })
        public Source getSource2() {
            return this.source2;
        }

        public void setSource2(Source source2) {
            this.source2 = source2;
        }

        @SuppressWarnings("PMD.MethodReturnsInternalArray")
        @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase", value = "experiment.sources"))
        public Source[] getSourceArray() {
            return this.sourceArray;
        }

        @SuppressWarnings("PMD.ArrayIsStoredDirectly")
        public void setSourceArray(Source[] sourceArray) {
            this.sourceArray = sourceArray;
        }

        /**
         * @return the protocolList
         */
        @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase", value = "experiment.sources"))
        public List<Source> getSourceList() {
            return this.sourceList;
        }

        public void setSourceList(List<Source> sourceList) {
            this.sourceList = sourceList;
        }
    }
}
