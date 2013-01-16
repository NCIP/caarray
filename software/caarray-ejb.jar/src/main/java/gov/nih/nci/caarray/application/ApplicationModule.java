//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.file.FileManagementModule;
import gov.nih.nci.caarray.application.fileaccess.FileAccessModule;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheImpl;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;

import com.google.inject.AbstractModule;

/**
 * Guice module for the application package.
 * 
 * @author jscott
 */
public class ApplicationModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        install(new FileAccessModule());
        install(new FileManagementModule());

        bind(ArrayDataService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(ArrayDataService.class, ArrayDataService.JNDI_NAME));
        bind(ArrayDesignService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(ArrayDesignService.class, ArrayDesignService.JNDI_NAME));
        bind(AuditLogService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(AuditLogService.class, AuditLogService.JNDI_NAME));
        bind(BrowseService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(BrowseService.class, BrowseService.JNDI_NAME));
        bind(GenericDataService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(GenericDataService.class, GenericDataService.JNDI_NAME));
        bind(PermissionsManagementService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(PermissionsManagementService.class,
                        PermissionsManagementService.JNDI_NAME));
        bind(ProjectManagementService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(ProjectManagementService.class,
                        ProjectManagementService.JNDI_NAME));
        bind(RegistrationService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(RegistrationService.class, RegistrationService.JNDI_NAME));
        bind(VocabularyService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(VocabularyService.class, VocabularyService.JNDI_NAME));

        // a general-purpose instance. it's the responsibility of clients
        // to explicitly call cleanup on it
        bind(TemporaryFileCache.class).to(TemporaryFileCacheImpl.class);
    }

}
