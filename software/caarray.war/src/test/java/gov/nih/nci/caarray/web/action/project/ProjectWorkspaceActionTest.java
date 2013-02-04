//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.util.ArrayList;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 * 
 */
public class ProjectWorkspaceActionTest extends AbstractBaseStrutsTest {
    private final ProjectWorkspaceAction action = new ProjectWorkspaceAction();

    private static final int WORK_QUEUE_COUNT = 5;

    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
        locatorStub.addLookup(FileManagementService.JNDI_NAME, new FileManagementServiceStub());
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataServiceStub());
    }

    @Test
    public void testWorkspace() {
        assertEquals(Action.SUCCESS, this.action.workspace());
        assertEquals(WORK_QUEUE_COUNT, this.action.getProjects().getFullListSize());
    }

    @Before
    public void before() {
        loadTestProject();
    }

    private void loadTestProject() {
        final ArrayList<Project> projects = new ArrayList<Project>();
        final Project project = new Project();
        final CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setName("file1.ext");
        final CaArrayFile file2 = new CaArrayFile();
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setName("file2.ext");
        file2.setProject(project);
        final CaArrayFile file3 = new CaArrayFile();
        file3.setFileStatus(FileStatus.UPLOADED);
        file3.setName("file3.ext");
        file3.setProject(project);
        project.getFiles().add(file1);
        project.getFiles().add(file2);
        project.getFiles().add(file3);
        projects.add(project);
        this.action.getProjects().setList(projects);
        this.action.getProjects().setFullListSize(1);
    }

    @Test
    public void testList() throws Exception {
        ServletActionContext.getRequest().getSession().setAttribute("messages", null);
        assertNotNull(this.action.getProjects());
        final String result = this.action.workspace();
        assertEquals(Action.SUCCESS, result);
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public int getMyProjectCount() {
            return WORK_QUEUE_COUNT;
        }
    }

    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (Project.class.equals(entityClass)) {
                final Project project = new Project();
                project.setId(entityId);
                return (T) project;
            } else {
                return super.getPersistentObject(entityClass, entityId);
            }
        }
    }
}
