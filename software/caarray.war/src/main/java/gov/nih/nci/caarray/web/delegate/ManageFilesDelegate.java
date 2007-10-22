package gov.nih.nci.caarray.web.delegate;

import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

/**
 * ManageFilesDelegate Delegate.
 * @author John Hedden
 *
 */
public class ManageFilesDelegate extends BaseDelegate {

    private ServiceLocator locator = ServiceLocator.INSTANCE;

    /**
     * Get FileManagementService.
     * @return fileManagementService
     */
    public FileManagementService getFileManagementService() {
        return (FileManagementService) locator.lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * Get ProjectManagementService.
     * @return projectManagementService
     */
    public ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) locator.lookup(ProjectManagementService.JNDI_NAME);
    }

    /**
     * Get FileAccessService.
     * @return fileAccessService
     */
    public FileAccessService getFileAccessService() {
        return (FileAccessService) locator.lookup(FileAccessService.JNDI_NAME);
    }

    /**
     * get locator for junit.
     * @return ServiceLocator ServiceLocator
     */
    public ServiceLocator getLocator() {
        return locator;
    }

    /**
     * For use by unit tests.
     * @param locator locator
     */
    public void setLocator(ServiceLocator locator) {
        this.locator = locator;
    }
}
