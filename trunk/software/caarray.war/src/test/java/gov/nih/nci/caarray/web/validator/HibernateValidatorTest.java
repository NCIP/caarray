/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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