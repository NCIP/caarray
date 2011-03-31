package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.dataStorage.database.DatabaseStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;

import com.google.inject.AbstractModule;

/**
 * Guice module that configures the data storage subsystem. This installs all the data storage engine implementation
 * modules. After implementing DataStorage interface for a new data storage engine, create a Guice module to add its
 * bindings and install it in this module.
 * 
 * A data storage implementation module should add bindings for its DataStorage implementation, optionally a
 * StorageUnitOfWork implementation, and any other dependencies. You must pick a key for your storage engine distinct
 * from any keys used by other storage engines. See one of the existing implementation modules for an example.
 * 
 * @author dkokotov
 */
public class DataStorageModule extends AbstractModule {
    private final String fileStorageEngine;
    private final String parsedStorageEngine;

    public DataStorageModule(String fileStorageEngine, String parsedStorageEngine) {
        this.fileStorageEngine = fileStorageEngine;
        this.parsedStorageEngine = parsedStorageEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        install(new FileSystemStorageModule());
        install(new DatabaseStorageModule());

        bind(StorageUnitOfWork.class).to(AggregateStorageUnitOfWork.class);
        bind(DataStorageFacade.class);

        bindConstant().annotatedWith(FileData.class).to(this.fileStorageEngine);
        bindConstant().annotatedWith(ParsedData.class).to(this.parsedStorageEngine);
    }
}
