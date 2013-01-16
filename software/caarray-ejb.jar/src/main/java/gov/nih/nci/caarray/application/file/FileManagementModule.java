//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Guice module for the application package.
 * 
 * @author jscott
 */
public class FileManagementModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(FileManagementService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(FileManagementService.class, FileManagementService.JNDI_NAME));
        // bind(MageTabImporter.class);
    }

    /**
     * @return a MageTabTranslator instance
     */
    @Provides
    public MageTabTranslator getMageTabTranslator() {
        return ServiceLocatorFactory.getMageTabTranslator();
    }
}
