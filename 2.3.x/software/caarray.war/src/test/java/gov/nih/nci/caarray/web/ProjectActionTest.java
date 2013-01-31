//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.action.project.ProjectWorkspaceAction;

import java.util.ArrayList;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.opensymphony.xwork2.Action;

/**
 * @author John Hedden
 *
 */
public class ProjectActionTest extends AbstractCaarrayTest {

    private final ProjectWorkspaceAction workspaceAction = new ProjectWorkspaceAction();
    private final LocalProjectManagementServiceStub projectServiceStub = new LocalProjectManagementServiceStub();
    private final LocalFileManagementServiceStub fileManagementStub = new LocalFileManagementServiceStub();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectServiceStub);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementStub);
        loadTestProject();
    }

    private void loadTestProject() {
        final ArrayList<Project> projects = new ArrayList<Project>();
        final Project project = new Project();
        CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setName("file1.ext");
        file1.setFileType(FileType.AFFYMETRIX_CEL);
        CaArrayFile file2 = new CaArrayFile();
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setName("file2.ext");
        file2.setFileType(FileType.AFFYMETRIX_CEL);
        file2.setProject(project);
        CaArrayFile file3 = new CaArrayFile();
        file3.setFileStatus(FileStatus.UPLOADED);
        file3.setName("file3.ext");
        file3.setFileType(FileType.AFFYMETRIX_CEL);
        file3.setProject(project);
        project.getFiles().add(file1);
        project.getFiles().add(file2);
        project.getFiles().add(file3);
        projects.add(project);
        this.workspaceAction.getProjects().setList(projects);
        this.workspaceAction.getProjects().setFullListSize(1);
    }

    @Test
    public void testList() throws Exception {
        MockHttpSession session = new MockHttpSession ();
        MockHttpServletRequest request = new MockHttpServletRequest();
        session.setAttribute("messages", null);
        request.setSession(session);
        ServletActionContext.setRequest(request);
        assertNotNull(this.workspaceAction.getProjects());
        String result = this.workspaceAction.myProjects();
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void testFileUtility() throws Exception {
        MockHttpSession session = new MockHttpSession ();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletActionContext.setRequest(request);
    }

    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
    }

    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
    }
}
