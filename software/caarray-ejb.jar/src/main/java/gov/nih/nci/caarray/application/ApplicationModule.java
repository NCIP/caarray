package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.application.audit.AuditLogServiceBean;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.browse.BrowseServiceBean;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceBean;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementServiceBean;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceBean;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.registration.RegistrationServiceBean;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceBean;

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
        bind(ArrayDataService.class).to(ArrayDataServiceBean.class);
        bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
        bind(AuditLogService.class).to(AuditLogServiceBean.class);
        bind(BrowseService.class).to(BrowseServiceBean.class);
        bind(FileManagementService.class).to(FileManagementServiceBean.class);
        bind(FileAccessService.class).to(FileAccessServiceBean.class);
        bind(GenericDataService.class).to(GenericDataServiceBean.class);
        bind(PermissionsManagementService.class).to(PermissionsManagementServiceBean.class);
        bind(ProjectManagementService.class).to(ProjectManagementServiceBean.class);
        bind(RegistrationService.class).to(RegistrationServiceBean.class);
        bind(VocabularyService.class).to(VocabularyServiceBean.class);
    }

}
