//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.action.project.ProjectFilesAction;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author John Hedden
 *
 */
public class ImportTest extends AbstractCaarrayTest {

    private final ProjectFilesAction action = new ProjectFilesAction();
    private final LocalProjectManagementServiceStub projectServiceStub = new LocalProjectManagementServiceStub();
    private final LocalFileManagementServiceStub fileManagementStub = new LocalFileManagementServiceStub();

    /**
     * setup.
     */
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectServiceStub);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementStub);
        loadTestProject();
    }

    @SuppressWarnings("deprecation")
    private void loadTestProject() {
        final Project project = new Project();;
        CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setName("file1.ext");
        file1.setFileType(FileType.AFFYMETRIX_CEL);
        file1.setId(Long.valueOf(1));
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

        this.action.setProject(project);

        MockHttpSession session = new MockHttpSession ();
        session.setAttribute("myProject", project);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletActionContext.setRequest(request);

    }

    /**
     * test import file.
     * @throws Exception Exception
     */
    @Test
    public void testImportFile() throws Exception {
//        MockHttpSession session = new MockHttpSession ();
//        MockHttpServletRequest request = new MockHttpServletRequest();
//
//        Project myProject = action.getProject();
//        assertNotNull(myProject);
//
//        session.setAttribute("myProject", myProject);
//
//        request.setSession(session);
//        request.setParameter("files:1:selected", "files:1:selected");
//        request.setParameter("files:1:notselected", "files:1:notselected");
//        request.setParameter("notfiles:1:selected", "notfiles:1:selected");
//        ServletActionContext.setRequest(request);
//
//        action.importFile();
    }

    /**
     * local stub
     */
    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
    }

    /**
     * local stub.
     */
    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
        boolean calledImportFiles;

        @Override
        public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions options) {
            super.importFiles(targetProject, fileSet, options);
            this.calledImportFiles = true;
        }
    }

}
