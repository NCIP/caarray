//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage.database;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheImpl;
import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * A StorageUnitOfWork which handles initializing and cleaning up the temporary file cache used to cache data from
 * database to disk.
 * 
 * @author dkokotov
 */
@Singleton
public class DatabaseStorageUnitOfWork implements StorageUnitOfWork, Provider<TemporaryFileCache> {
    private static final Logger LOG = Logger.getLogger(DatabaseStorageUnitOfWork.class);
    private final ThreadLocal<TemporaryFileCache> tempCache = new ThreadLocal<TemporaryFileCache>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin() {
        LOG.debug("beginning unit of work");
        Preconditions.checkState(null == this.tempCache.get(),
                "Work already begun on this thread. Looks like you have called UnitOfWork.begin() twice"
                        + " without a balancing call to end() in between.");

        this.tempCache.set(new TemporaryFileCacheImpl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        LOG.debug("ending unit of work unit of work");
        final TemporaryFileCache fc = this.tempCache.get();
        if (fc == null) {
            return; // ignore multiple closures
        }

        try {
            fc.deleteFiles();
        } finally {
            this.tempCache.set(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemporaryFileCache get() {
        final TemporaryFileCache fc = this.tempCache.get();
        Preconditions.checkState(fc != null, "Requested TemporaryFileCache outside work unit. "
                + "Try calling UnitOfWork.begin() first, or use a PersistFilter if you "
                + "are inside a servlet environment.");
        return fc;
    }
}
