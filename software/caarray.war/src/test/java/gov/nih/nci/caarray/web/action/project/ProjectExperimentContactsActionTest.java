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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng, Jevon Gill
 *
 */
@SuppressWarnings("PMD")
public class ProjectExperimentContactsActionTest extends AbstractBaseStrutsTest {

    private final ProjectExperimentContactsAction action = new ProjectExperimentContactsAction();
    private static ExperimentContact DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
    private static int NUM_EXPERIMENT_CONTACTS = 2;
    private static final LocalProjectManagementServiceStub projectManagementServiceStub = new LocalProjectManagementServiceStub();
    private static User STANDARD_USER;

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {

         ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
         locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
         locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
         locatorStub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
         DUMMY_EXPERIMENT_CONTACT.setId(1L);

         CaArrayUsernameHolder.setUser("caarrayadmin");
         STANDARD_USER = CaArrayUsernameHolder.getCsmUser();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() {
        // no current experiment contact id
        action.prepare();
        assertNull(action.getCurrentExperimentContact().getId());

        // valid current experiment id
        ExperimentContact currentExperimentContact = new ExperimentContact();
        currentExperimentContact.setId(1L);
        action.setCurrentExperimentContact(currentExperimentContact);
        action.prepare();
        assertEquals(DUMMY_EXPERIMENT_CONTACT, action.getCurrentExperimentContact());

        // invalid current experiment id
        currentExperimentContact = new ExperimentContact();
        currentExperimentContact.setId(2L);
        action.setCurrentExperimentContact(currentExperimentContact);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test(expected=NotImplementedException.class)
    public void testCopy() {
        action.copy();
    }

    @Test
    public void testLoad() {
        Project p = new Project();
        Experiment e = new Experiment();
        p.setExperiment(e);
        for (int i=0; i<NUM_EXPERIMENT_CONTACTS; i++) {
            e.getExperimentContacts().add(new ExperimentContact());
        }
        action.setProject(p);
        assertEquals("list", action.load());
        assertEquals(NUM_EXPERIMENT_CONTACTS, action.getPagedItems().getFullListSize());
    }

    @Test
    public void testSave() {
        assertEquals(1,action.getProject().getOwners().size());
        ProjectExperimentContactsAction action = new ProjectExperimentContactsAction();
        action.setCurrentExperimentContact(new ExperimentContact());
        assertNull(action.getExperiment().getPrimaryInvestigator());
        action.save();

        assertEquals(0, action.getExperiment().getExperimentContacts().size());
        Term piRole = VocabularyUtils.getMOTerm(ExperimentContact.PI_ROLE);
        DUMMY_EXPERIMENT_CONTACT.getRoles().add(piRole);
        Person person = new Person(STANDARD_USER);
        action.getCurrentExperimentContact().setContact(person);
        action.setCurrentExperimentContact(DUMMY_EXPERIMENT_CONTACT);

        action.save();
        assertEquals(1, action.getExperiment().getExperimentContacts().size());
        String roles = action.getExperiment().getExperimentContacts().get(0).getRoleNames();
        assertTrue("submitter, investigator".equals(roles) || "investigator, submitter".equals(roles));
        assertEquals(1,action.getExperiment().getPrimaryInvestigatorCount());
        assertNotNull(action.getExperiment().getPrimaryInvestigator());
        assertEquals("Administrator, caArray", action.getExperiment().getExperimentContacts().get(0).getContact().getName());
    }

    @Test
    public void testEdit() {
        assertEquals(Action.INPUT, action.edit());
   }

    @Test
    public void testDelete() {

        Project p = new Project();

        Term piRole = VocabularyUtils.getMOTerm(ExperimentContact.PI_ROLE);
        DUMMY_EXPERIMENT_CONTACT.getRoles().add(piRole);
        p.getExperiment().getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
        ExperimentContact ec = new ExperimentContact();
        p.getExperiment().getExperimentContacts().add(ec);
        action.setProject(p);

        action.setCurrentExperimentContact(ec);

        assertEquals(2, action.getExperiment().getExperimentContacts().size());
        assertEquals("list",action.delete());
        assertEquals(1, action.getExperiment().getExperimentContacts().size());
   }

    @Test
    public void testDeleteOnlyPi() {

        Project p = new Project();

        Term piRole = VocabularyUtils.getMOTerm(ExperimentContact.PI_ROLE);
        DUMMY_EXPERIMENT_CONTACT.getRoles().add(piRole);
        p.getExperiment().getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
        action.setProject(p);

        action.setCurrentExperimentContact(DUMMY_EXPERIMENT_CONTACT);

        assertEquals(1, action.getExperiment().getExperimentContacts().size());
        assertEquals("list",action.delete());
        assertEquals(1, action.getExperiment().getExperimentContacts().size());

   }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(ExperimentContact.class) && entityId.equals(1L)) {
                return (T)DUMMY_EXPERIMENT_CONTACT;
            }
            return null;
        }

        @Override
        public int collectionSize(Collection<? extends PersistentObject> collection) {
            return collection.size();
        }
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
