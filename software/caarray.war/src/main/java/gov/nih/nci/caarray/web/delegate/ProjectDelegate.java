package gov.nih.nci.caarray.web.delegate;

import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

/**
 * ProjectDelegate.
 * @author John Hedden
 *
 */
public class ProjectDelegate extends BaseDelegate {

    private ServiceLocator locator = ServiceLocator.INSTANCE;
    private ProjectManagementService projectManagementService;
    private FileManagementService fileManagementService;

    /**
     * Get ProjectManagementService.
     * @return projectManagementService
     */
    public ProjectManagementService getProjectManagementService() {
        if (projectManagementService == null) {
            projectManagementService =
                (ProjectManagementService) locator.lookup(ProjectManagementService.JNDI_NAME);
        }
        return projectManagementService;
    }

    /**
     * Get FileManagementService.
     * @return fileManagementService
     */
    public FileManagementService getFileManagementService() {
        if (fileManagementService == null) {
            fileManagementService = (FileManagementService) locator.lookup(FileManagementService.JNDI_NAME);
        }
        return fileManagementService;
    }

}
