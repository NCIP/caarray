//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage.fileSystem;

import gov.nih.nci.caarray.dataStorage.spi.DataStorage;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * Guice module for the file-based data storage plugin.
 * 
 * @author dkokotov
 */
public class FileSystemStorageModule extends AbstractModule {
    /**
     * configuration constant name for the directory where files should be stored.
     */
    public static final String BASE_DIR_KEY = "dataStorage.fileSystem.baseDir";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        final MapBinder<String, DataStorage> mapBinder =
                MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
        mapBinder.addBinding(FilesystemDataStorage.SCHEME).to(FilesystemDataStorage.class);

        requireBinding(Key.get(String.class, Names.named(BASE_DIR_KEY)));
    }
}
