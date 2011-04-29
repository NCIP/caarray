package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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

        final Properties storageProps = new Properties();
        try {
            storageProps.load(FileAccessModule.class.getResourceAsStream("/dataStorage.properties"));
        } catch (final IOException e) {
            LOG.warn("Could not load properties from dataStorage.properties");
        }
        Names.bindProperties(binder(), storageProps);
        install(new DataStorageModule());
    }
}
