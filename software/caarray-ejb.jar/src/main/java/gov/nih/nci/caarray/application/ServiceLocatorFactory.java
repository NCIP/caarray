//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.jobqueue.JobQueueService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.translation.geosoft.GeoSoftExporter;
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import com.google.inject.Provider;

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
     * Allows registration of a <code>ServiceLocator</code> instance; should only be used in test code to replace the
     * actual locator with a test stub.
     * 
     * @param locator the locator to set
     */
    public static void setLocator(ServiceLocator locator) {
        ServiceLocatorFactory.locator = locator;
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static JobQueueService getJobQueueService() {
        return (JobQueueService) getLocator().lookup(JobQueueService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static PermissionsManagementService getPermissionsManagementService() {
        return (PermissionsManagementService) getLocator().lookup(PermissionsManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static FileAccessService getFileAccessService() {
        return (FileAccessService) getLocator().lookup(FileAccessService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static FileManagementService getFileManagementService() {
        return (FileManagementService) getLocator().lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static VocabularyService getVocabularyService() {
        return (VocabularyService) getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static GenericDataService getGenericDataService() {
        return (GenericDataService) getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ArrayDataService getArrayDataService() {
        return (ArrayDataService) getLocator().lookup(ArrayDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static RegistrationService getRegistrationService() {
        return (RegistrationService) getLocator().lookup(RegistrationService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static BrowseService getBrowseService() {
        return (BrowseService) getLocator().lookup(BrowseService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static AuditLogService getAuditLogService() {
        return (AuditLogService) getLocator().lookup(AuditLogService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the MAGE-TAB Exporter singleton service.
     * 
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabExporter getMageTabExporter() {
        return (MageTabExporter) getLocator().lookup(MageTabExporter.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the GEO SOFT Exporter singleton service.
     * 
     * @return the GEO SOFT Exporter service
     */
    public static GeoSoftExporter getGeoSoftExporter() {
        return (GeoSoftExporter) getLocator().lookup(GeoSoftExporter.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the MAGE-TAB Translator singleton service.
     * 
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabTranslator getMageTabTranslator() {
        return (MageTabTranslator) getLocator().lookup(MageTabTranslator.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the DataRetrievalService singleton service.
     * 
     * @return the DataRetrievalService
     */
    public static DataRetrievalService getDataRetrievalService() {
        return (DataRetrievalService) getLocator().lookup(DataRetrievalService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the DataRetrievalService singleton service.
     * 
     * @return the DataRetrievalService
     */
    public static CaArraySearchService getCaArraySearchService() {
        return (CaArraySearchService) getLocator().lookup(CaArraySearchService.JNDI_NAME);
    }

    /**
     * Create a Guice provider that will provide instances of given service by looking them up in this locator using the
     * given JNDI name.
     * 
     * @param <T> service type
     * @param service the service for which a provider will be created
     * @param serviceJndiName the JNDI name by which to lookup the service
     * @return the guice provider
     */
    public static <T> Provider<T> serviceProvider(Class<T> service, final String serviceJndiName) {
        return new Provider<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T get() {
                return (T) getLocator().lookup(serviceJndiName);
            }
        };
    }
}
