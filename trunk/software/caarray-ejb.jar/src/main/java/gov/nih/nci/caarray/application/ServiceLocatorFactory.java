//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.translation.geosoft.GeoSoftExporter;
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;

/**
 * Provides access to a <code>ServiceLocator</code>.
 */
public final class ServiceLocatorFactory {

    private static ServiceLocator locator = new ServiceLocatorImplementation();

    private ServiceLocatorFactory() {

    }

    /**
     * Returns a <code>ServiceLocator</code> instance.
     *
     * @return the locator
     */
    public static ServiceLocator getLocator() {
        return locator;
    }

    /**
     * Allows registration of a <code>ServiceLocator</code> instance; should only
     * be used in test code to replace the actual locator with a test stub.
     *
     * @param locator the locator to set
     */
    public static void setLocator(ServiceLocator locator) {
        ServiceLocatorFactory.locator = locator;
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static PermissionsManagementService getPermissionsManagementService() {
        return (PermissionsManagementService) getLocator().lookup(PermissionsManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static FileAccessService getFileAccessService() {
        return (FileAccessService) getLocator().lookup(FileAccessService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static FileManagementService getFileManagementService() {
        return (FileManagementService) getLocator().lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static VocabularyService getVocabularyService() {
        return (VocabularyService) getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static GenericDataService getGenericDataService() {
        return (GenericDataService) getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static ArrayDataService getArrayDataService() {
        return (ArrayDataService) getLocator().lookup(ArrayDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static RegistrationService getRegistrationService() {
        return (RegistrationService) getLocator().lookup(RegistrationService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static BrowseService getBrowseService() {
        return (BrowseService) getLocator().lookup(BrowseService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. 
     * @return the service
     */
    public static AuditLogService getAuditLogService() {
        return (AuditLogService) getLocator().lookup(AuditLogService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the MAGE-TAB Exporter singleton service.
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabExporter getMageTabExporter() {
        return (MageTabExporter) getLocator().lookup(MageTabExporter.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the GEO SOFT Exporter singleton service.
     * @return the GEO SOFT Exporter service
     */
    public static GeoSoftExporter getGeoSoftExporter() {
        return (GeoSoftExporter) getLocator().lookup(GeoSoftExporter.JNDI_NAME);
    }

}
