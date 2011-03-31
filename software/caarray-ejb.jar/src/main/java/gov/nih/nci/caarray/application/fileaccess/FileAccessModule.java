package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.dataStorage.DataStorageModule;

import com.google.inject.AbstractModule;

/**
 * Guice module for the file access subsystem
 * 
 * @author jscott
 */
public class FileAccessModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(FileAccessService.class).to(FileAccessServiceBean.class);

        install(new DataStorageModule("db-multipart", "file-system"));
    }
}
