//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
