//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;
import gov.nih.nci.caarray.util.CaArrayUtils;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;

/**
 * Guice module for the file access subsystem.
 * 
 * @author jscott
 */
public class FileAccessModule extends AbstractModule {
    private static final Logger LOG = Logger.getLogger(FileAccessModule.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(FileAccessService.class).toProvider(
                ServiceLocatorFactory.serviceProvider(FileAccessService.class, FileAccessService.JNDI_NAME));

        CaArrayUtils.bindPropertiesAsNamed("/dataStorage.properties", binder());
        install(new DataStorageModule());
    }
}
