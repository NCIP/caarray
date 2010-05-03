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
package gov.nih.nci.caarray.web.action.vocabulary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

/**
 * @author Scott Miller
 */
public class VocabularyActionTest extends AbstractBaseStrutsTest {
    private static final String START_WITH_EDIT = "startWithEdit";
    private static final String RETURN_PROJECT_ID = "returnProjectId";
    private static final String RETURN_INITIAL_TAB1 = "returnInitialTab1";
    private static final String RETURN_INITIAL_TAB2 = "returnInitialTab2";
    private static final String RETURN_INITIAL_TAB2_URL = "returnInitialTab2Url";

    private static VocabularyService vocabularyService;
    VocabularyAction action;

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        vocabularyService = new VocabularyServiceStub() {

            /**
             * {@inheritDoc}
             */
            @Override
            public Term getTerm(Long id) {
                if (id.equals(1L))  {
                    Term t = super.getTerm(id);
                    t.setValue("test term");
                    return t;
                } else if (id.equals(2L)) {
                    return null;
                }
                return super.getTerm(id);
            }

        };
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(VocabularyService.JNDI_NAME, vocabularyService);
    }

    @Before
    public void before() {
        this.action = new VocabularyAction();
    }

    @Test(expected = PermissionDeniedException.class)
    @SuppressWarnings("deprecation")
    public void testPrepare() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.prepare();
        assertEquals(null, this.action.getCurrentTerm());
        this.action.setCurrentTerm(new Term());
        this.action.getCurrentTerm().setId(1l);
        this.action.prepare();
        assertEquals("test term", this.action.getCurrentTerm().getValue());
        this.action.getCurrentTerm().setId(2l);
        this.action.prepare();
    }

    @Test
    public void testList() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        assertEquals(Action.SUCCESS, this.action.list());

        HttpSession session = ServletActionContext.getRequest().getSession();
        session.setAttribute(START_WITH_EDIT, "true");
        session.setAttribute(RETURN_PROJECT_ID, "1");
        session.setAttribute(RETURN_INITIAL_TAB1, RETURN_INITIAL_TAB1);
        session.setAttribute(RETURN_INITIAL_TAB2, RETURN_INITIAL_TAB2);
        session.setAttribute(RETURN_INITIAL_TAB2_URL, RETURN_INITIAL_TAB2_URL);

        assertEquals(Action.INPUT, this.action.list());
        assertTrue(this.action.isEditMode());
        assertEquals(1l, this.action.getReturnProjectId().longValue());
        assertEquals(RETURN_INITIAL_TAB1, this.action.getReturnInitialTab1());
        assertEquals(RETURN_INITIAL_TAB2, this.action.getReturnInitialTab2());
        assertEquals(RETURN_INITIAL_TAB2_URL, this.action.getReturnInitialTab2Url());
        assertNull(session.getAttribute(START_WITH_EDIT));
        assertNull(session.getAttribute(RETURN_PROJECT_ID));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB1));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB2));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB2_URL));
        assertTrue(this.action.isReturnToProjectOnCompletion());
    }

    @Test
    public void testSearchForTerms() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.setCurrentTerm(new Term());
        assertEquals("termAutoCompleterValues", this.action.searchForTerms());
    }

    @Test
    public void testDetails() {
        assertEquals(Action.INPUT, this.action.details());
        assertFalse(this.action.isEditMode());
    }

    @Test
    public void testProjectEdit() {
        assertEquals("projectEdit", this.action.projectEdit());
    }

    @Test
    public void testSave() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.setCurrentTerm(new Term());
        assertEquals(Action.SUCCESS, this.action.save());

        this.action.setCreateNewSource(true);
        this.action.setReturnToProjectOnCompletion(true);
        assertEquals("projectEdit", this.action.save());
    }
}
