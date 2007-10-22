package gov.nih.nci.caarray.web.delegate;

import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

/**
 * ProjectDelegate.
 * @author John Hedden
 *
 */
public class ProjectDelegate extends BaseDelegate {

    private ServiceLocator locator = ServiceLocator.INSTANCE;

    /**
     * Get ProjectManagementService.
     * @return projectManagementService
     */
    public ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) locator.lookup(ProjectManagementService.JNDI_NAME);
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
