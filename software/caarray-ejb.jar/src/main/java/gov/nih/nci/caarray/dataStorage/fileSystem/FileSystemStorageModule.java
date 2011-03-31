package gov.nih.nci.caarray.dataStorage.fileSystem;

import gov.nih.nci.caarray.dataStorage.spi.DataStorage;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * Guice module for the file-based data storage plugin.
 * 
 * @author dkokotov
 */
public class FileSystemStorageModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        final MapBinder<String, DataStorage> mapBinder = MapBinder.newMapBinder(binder(), String.class,
                DataStorage.class);
        mapBinder.addBinding(FilesystemDataStorage.SCHEME).to(FilesystemDataStorage.class);

        // TODO: extract to externally configured
        bind(String.class).annotatedWith(Names.named("dataStorage.fileSystem.baseDir")).toInstance(
                "/Users/dkokotov/dev/tmp/caarray-fs-storage");
    }
}
