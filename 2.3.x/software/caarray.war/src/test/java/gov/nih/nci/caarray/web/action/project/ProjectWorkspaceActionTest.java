//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectWorkspaceActionTest extends AbstractCaarrayTest {
    private final ProjectWorkspaceAction action = new ProjectWorkspaceAction();

    private static final int PUBLIC_COUNT = 3;
    private static final int WORK_QUEUE_COUNT = 5;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
    }

    @Test
    public void testWorkspace() {
        assertEquals(Action.SUCCESS, action.workspace());
        assertEquals(PUBLIC_COUNT, action.getPublicCount());
        assertEquals(WORK_QUEUE_COUNT, action.getWorkQueueCount());
    }

    @Test
    public void testPublicProjects() {
        assertEquals(Action.SUCCESS, action.publicProjects());
        assertEquals(PUBLIC_COUNT, action.getProjects().getFullListSize());
    }

    @Test
    public void testMyProjects() {
        assertEquals(Action.SUCCESS, action.myProjects());
        assertEquals(WORK_QUEUE_COUNT, action.getProjects().getFullListSize());
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public int getMyProjectCount(boolean showPublic) {
            return showPublic ? PUBLIC_COUNT : WORK_QUEUE_COUNT;
        }
    }
}
