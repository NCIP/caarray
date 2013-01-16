//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.dataStorage.database.DatabaseStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;

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
    /**
     * key under which the name of the storage engine to use for storing new file data is found.
     */
    public static final String FILE_DATA_ENGINE = "dataStorage.fileDataEngine";
    /**
     * key under which the name of the storage engine to use for storing new parsed data is found.
     */
    public static final String PARSED_DATA_ENGINE = "dataStorage.parsedDataEngine";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        install(new FileSystemStorageModule());
        install(new DatabaseStorageModule());

        bind(StorageUnitOfWork.class).to(AggregateStorageUnitOfWork.class);
        bind(DataStorageFacade.class);

        requireBinding(Key.get(String.class, Names.named(FILE_DATA_ENGINE)));
        requireBinding(Key.get(String.class, Names.named(PARSED_DATA_ENGINE)));
    }
}
