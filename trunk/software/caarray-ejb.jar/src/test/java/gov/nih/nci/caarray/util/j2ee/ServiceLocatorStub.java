//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.j2ee;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceBean;
import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.browse.BrowseServiceBean;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceBean;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceBean;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.registration.RegistrationServiceBean;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorBean;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple stub implementation of locator -- allows clients to seed a lookup table with other stubs by JNDI name.
 */
public final class ServiceLocatorStub implements ServiceLocator {

    private static final long serialVersionUID = 4520519885611921043L;

    private final Map<String, Object> lookupMap = new HashMap<String, Object>();

    /**
     * Prevents direct construction; use the static register methods.
     */
    private ServiceLocatorStub() {
        // no-op
    }

    public Object lookup(String jndiName) {
        return lookupMap.get(jndiName);
    }

    public void addLookup(String jndiName, Object object) {
        lookupMap.put(jndiName, object);
    }

    public static ServiceLocatorStub registerEmptyLocator() {
        ServiceLocatorStub locatorStub = new ServiceLocatorStub();
        ServiceLocatorFactory.setLocator(locatorStub);
        return locatorStub;
    }

    public static ServiceLocatorStub registerActualImplementations() {
        ServiceLocatorStub locatorStub = new ServiceLocatorStub();
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, new ArrayDataServiceBean());
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, new ArrayDesignServiceBean());
        locatorStub.addLookup(FileAccessService.JNDI_NAME, new FileAccessServiceBean());
        locatorStub.addLookup(FileManagementService.JNDI_NAME, new FileManagementServiceBean());
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new GenericDataServiceBean());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceBean());
        locatorStub.addLookup(RegistrationService.JNDI_NAME, new RegistrationServiceBean());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceBean());
        locatorStub.addLookup(BrowseService.JNDI_NAME, new BrowseServiceBean());
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, new MageTabTranslatorBean());
        ServiceLocatorFactory.setLocator(locatorStub);
        return locatorStub;
    }
}
