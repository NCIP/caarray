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
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectContactsActionTest {
    private final ProjectContactsAction action = new ProjectContactsAction();
    private static final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    private static final LocalProjectManagementServiceStub projectManagementServiceStub = new LocalProjectManagementServiceStub();

    private static User STANDARD_USER;
    private static Person DUMMY_PERSON_1 = new Person();
    private static Person DUMMY_PERSON_2 = new Person();
    private static Term PI_ROLE;
    private static Term MAIN_POC_ROLE;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        Project project = new Project();
        action.setProject(project);
        PI_ROLE = vocabularyServiceStub.getTerm(null, ExperimentContact.PI_ROLE);
        MAIN_POC_ROLE = vocabularyServiceStub.getTerm(null, ExperimentContact.MAIN_POC_ROLE);
        UsernameHolder.setUser("caarrayadmin");
        STANDARD_USER = UsernameHolder.getCsmUser();
    }

    @Test
    public void testLoad() {
        assertEquals(Action.INPUT, action.load());
    }

    @Test
    public void testSetup() {
        // no pi, no poc
        action.setup();
        assertEquals(STANDARD_USER.getLastName(), action.getPrimaryInvestigator().getLastName());
        assertTrue(action.isPiIsMainPoc());
        assertNull(action.getMainPointOfContact().getLastName());

        // has pi, no poc
        ExperimentContact piContact = new ExperimentContact(getExperiment(), DUMMY_PERSON_1, PI_ROLE);
        getExperimentContacts().add(piContact);
        action.setup();
        assertEquals(DUMMY_PERSON_1.getLastName(), action.getPrimaryInvestigator().getLastName());
        assertFalse(action.isPiIsMainPoc());
        assertNull(action.getMainPointOfContact().getLastName());

        // has pi, separate poc
        ExperimentContact pocContact = new ExperimentContact(getExperiment(), DUMMY_PERSON_2, MAIN_POC_ROLE);
        getExperimentContacts().add(pocContact);
        action.setup();
        assertEquals(DUMMY_PERSON_1.getLastName(), action.getPrimaryInvestigator().getLastName());
        assertFalse(action.isPiIsMainPoc());
        assertEquals(DUMMY_PERSON_2.getLastName(), action.getMainPointOfContact().getLastName());

        // pi is poc
        getExperiment().removeSeparateMainPointOfContact();
        piContact.getRoles().add(MAIN_POC_ROLE);
        action.setup();
        assertEquals(DUMMY_PERSON_1.getLastName(), action.getPrimaryInvestigator().getLastName());
        assertTrue(action.isPiIsMainPoc());
        assertNull(action.getMainPointOfContact().getLastName());
    }

    @Test
    public void testSave() {
        ServletActionContext.setRequest(new MockHttpServletRequest());
        // save new project
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());

        // save existing project
        this.action.setProject(new Project());
        this.action.getProject().setId(1L);
        assertEquals(Action.SUCCESS, this.action.save());

        // save project with PI
        this.action.setProject(new Project());
        this.action.setPrimaryInvestigator(DUMMY_PERSON_2);
        this.action.setPiIsMainPoc(true);
        ExperimentContact piContact = new ExperimentContact(getExperiment(), DUMMY_PERSON_1, PI_ROLE);
        getExperimentContacts().add(piContact);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());

        this.action.setProject(new Project());
        this.action.setPrimaryInvestigator(DUMMY_PERSON_2);
        this.action.setPiIsMainPoc(false);
        piContact = new ExperimentContact(getExperiment(), DUMMY_PERSON_1, PI_ROLE);
        getExperimentContacts().add(piContact);
        piContact.getRoles().add(MAIN_POC_ROLE);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, this.action.save());

        // ProposalWorkflowException
        Project p = new Project();
        p.setId(2L);
        p.setExperiment(new Experiment());
        p.getExperiment().setTitle("title");
        this.action.setProject(p);
        assertEquals(Action.INPUT, this.action.save());
        assertTrue(ActionHelper.getMessages().contains("project.saveProblem"));

        // InconsistentProjectStateException
        p = new Project();
        p.setId(3L);
        p.setExperiment(new Experiment());
        p.getExperiment().setTitle("title");
        this.action.setProject(p);
        assertEquals(Action.INPUT, this.action.save());
        assertTrue(action.getActionErrors().contains("project.inconsistentState.importing_files"));
    }

    private Experiment getExperiment() {
        return action.getProject().getExperiment();
    }
    private List<ExperimentContact> getExperimentContacts() {
        return getExperiment().getExperimentContacts();
    }

    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
        @Override
        public void saveProject(Project project, PersistentObject... orphans) throws ProposalWorkflowException,
            InconsistentProjectStateException {
            Long id = project.getId();
            if (id == null) {
                return;
            } else if (id.equals(2L)) {
                throw new ProposalWorkflowException();
            } else if (id.equals(3L)) {
                throw new InconsistentProjectStateException(Reason.IMPORTING_FILES, new Object[] {});
            }
        }

    }
}
