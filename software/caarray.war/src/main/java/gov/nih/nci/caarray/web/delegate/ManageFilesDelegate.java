package gov.nih.nci.caarray.web.delegate;

import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

/**
 * ManageFilesDelegate Delegate.
 * @author John Hedden
 *
 */
public class ManageFilesDelegate extends BaseDelegate {

    private ServiceLocator locator = ServiceLocator.INSTANCE;
    private FileManagementService fileManagementService;

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
