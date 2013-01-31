//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Scott Miller
 */
public class ProjectActionTest extends AbstractCaarrayTest {

    private static final String WORKSPACE = "workspace";
    ProjectAction action = new ProjectAction();
    private static final ProjectManagementServiceStub projectManagementServiceStub = new LocalProjectManagementServiceStub();

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
    }

    @Before
    public void before() {
        this.action = new ProjectAction() {
            private static final long serialVersionUID = 1L;

            @Override
            protected User getCsmUser() {
                return null;
            }
        };
        projectManagementServiceStub.reset();
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @SuppressWarnings("deprecation")
    private Project getTestProject(Long id) {
        Project p = new Project() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean hasReadPermission(User user) {
                return (this.getId() == 1l);
            }

            @Override
            public boolean hasWritePermission(User user) {
                return (this.getId() == 1l);
            }

        };
        p.setId(id);
        return p;
    }

    @Test
    public void testPrepare() throws Exception {
        this.action.prepare();
        assertEquals(0, projectManagementServiceStub.getProjectByIdCount());
        this.action.setProject(this.getTestProject(null));
        this.action.prepare();
        assertEquals(0, projectManagementServiceStub.getProjectByIdCount());
        this.action.setProject(this.getTestProject(1l));
        this.action.prepare();
        assertEquals(1, projectManagementServiceStub.getProjectByIdCount());
        assertNotNull(this.action.getExperiment());
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(this.action.isEditMode());
        assertEquals(Action.INPUT, this.action.create());
        assertTrue(this.action.isEditMode());
    }

    @Test
    public void testEdit() {
        this.action.setProject(this.getTestProject(null));
        assertEquals(WORKSPACE, this.action.edit());
        this.action.setProject(this.getTestProject((2l)));
        assertEquals("login-details-id", this.action.edit());
        this.action.setProject(this.getTestProject((1l)));
        assertEquals(Action.INPUT, this.action.edit());
        assertTrue(this.action.isEditMode());
    }

    @Test
    public void testDetails() {
        this.action.setProject(this.getTestProject(null));
        assertEquals(WORKSPACE, this.action.details());
        this.action.setProject(this.getTestProject((2l)));
        assertEquals("login-details-id", this.action.details());
        this.action.setProject(this.getTestProject((1l)));
        assertEquals(Action.INPUT, this.action.details());
        assertFalse(this.action.isEditMode());
    }

    @Test
    public void testChangeStatus() {
        this.action.setWorkflowStatus(ProposalStatus.DRAFT);
        this.action.setProject(this.getTestProject(1l));
        assertEquals(WORKSPACE, this.action.changeWorkflowStatus());
        assertEquals(1, projectManagementServiceStub.getChangeWorkflowStatusCount());
        this.action.setProject(this.getTestProject(999l));
        assertEquals(Action.INPUT, this.action.changeWorkflowStatus());
        assertEquals(2, projectManagementServiceStub.getChangeWorkflowStatusCount());
    }

    @Test
    public void testDelete() {
        this.action.setProject(this.getTestProject(1l));
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.deleted"));
    }

    @Test
    public void testDeleteNonDraft() {
        this.action.setProject(this.getTestProject(1l));
        this.action.getProject().setStatus(ProposalStatus.PUBLIC);
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.deleteOnlyDrafts"));
    }

    @Test
    public void testDeleteNotFound() {
        this.action.setProject(new Project());
        UsernameHolder.setUser("someuser");
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.notFound"));
    }

    @Test(expected = PermissionDeniedException.class)
    public void testDeleteNoWritePermission() {
        this.action.setProject(this.getTestProject(2l));
        UsernameHolder.setUser("unauthorizeduser");
        this.action.delete();
    }

    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
        /**
         * {@inheritDoc}
         */
        public void deleteProject(Project project) throws ProposalWorkflowException {
            if (UsernameHolder.getUser().equals("unauthorizeduser")) {
                throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE, UsernameHolder.getUser());
            }
            if (project.getStatus() != ProposalStatus.DRAFT) {
                throw new ProposalWorkflowException("Cannot delete a non-draft project");
            }
            // assume success if the checks pass
        }

    }
}
