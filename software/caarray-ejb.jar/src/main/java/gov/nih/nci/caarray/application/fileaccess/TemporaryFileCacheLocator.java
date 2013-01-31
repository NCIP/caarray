//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

/**
 * Provides access to the per-thread cache of uncompressed file data from CaArrayFiles.
 * @author dkokotov
 */
public final class TemporaryFileCacheLocator {
    /**
     * TemporaryFileCacheFactory that returns the default implementation.
     */
    public static final TemporaryFileCacheFactory DEFAULT = new TemporaryFileCacheFactory() {
        public TemporaryFileCache createTempFileCache() {
            return new TemporaryFileCacheImpl();
        }
    };
    private static final ThreadLocal<TemporaryFileCache> CACHE = new ThreadLocal<TemporaryFileCache>();
    private static TemporaryFileCacheFactory tempFileCacheFactory = DEFAULT;

    private TemporaryFileCacheLocator() {
        // empty constructor - utility class
    }

    /**
     * @return an instance of the TemporaryFileCache for the current thread.
     */
    public static TemporaryFileCache getTemporaryFileCache() {
        TemporaryFileCache cache = CACHE.get();
        if (cache == null) {
            cache = tempFileCacheFactory.createTempFileCache();
            CACHE.set(cache);
        }
        return cache;
    }

    /**
     * Cleans up and resets the temporary file cache associated with current thread, such
     * that a new instance of it will be created the next time it is requested.
     */
    public static void resetTemporaryFileCache() {
        TemporaryFileCache cache = CACHE.get();
        if (cache != null) {
            cache.closeFiles();
            CACHE.set(null);
        }
    }

    /**
     * Set the factory that should be used to create the instances of TEmporaryFileCache for each thread.
     * @param factory the factory to use
     */
    public static void setTemporaryFileCacheFactory(TemporaryFileCacheFactory factory) {
        tempFileCacheFactory = factory;
    }
}
