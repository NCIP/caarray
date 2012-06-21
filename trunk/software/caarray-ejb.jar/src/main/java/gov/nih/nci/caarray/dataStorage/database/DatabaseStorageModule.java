package gov.nih.nci.caarray.dataStorage.database;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Guice module for the database-based data storage plugin.
 * 
 * @author dkokotov
 */
public class DatabaseStorageModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        final MapBinder<String, DataStorage> mapBinder = MapBinder.newMapBinder(binder(), String.class,
                DataStorage.class);
        mapBinder.addBinding(DatabaseMultipartBlobDataStorage.SCHEME).to(DatabaseMultipartBlobDataStorage.class);

        final Multibinder<StorageUnitOfWork> unitBinder = Multibinder.newSetBinder(binder(), StorageUnitOfWork.class);
        bind(DatabaseStorageUnitOfWork.class).in(Singleton.class);
        unitBinder.addBinding().to(DatabaseStorageUnitOfWork.class);

        bind(TemporaryFileCache.class).annotatedWith(Names.named("dbMultipartStorageTempCache")).toProvider(
                DatabaseStorageUnitOfWork.class);
    }
}
