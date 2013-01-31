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

    private static final int WORK_QUEUE_COUNT = 5;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
    }

    @Test
    public void testWorkspace() {
        assertEquals(Action.SUCCESS, action.workspace());
        assertEquals(WORK_QUEUE_COUNT, action.getProjects().getFullListSize());
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public int getMyProjectCount() {
            return WORK_QUEUE_COUNT;
        }
    }
}
