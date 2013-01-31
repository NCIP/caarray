//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

/**
 * Service locator class responsible for creating TemporaryFileCache instances. Clients should obtain
 * TemporaryFileCaches from this class by calling either getTemporaryFileCache() or newTemporaryFileCache(), depending
 * on desired cache lifecycle.
 * 
 * This class maintains a per-thread registry of TemporaryFileCache instances. When needing a TemporaryFileCache whose
 * lifecycle is tied to a request or API method call, clients should obtain it from this class by calling
 * getTemporaryFileCache(), ensuring that the same instance will be returned throughout servicing a single request,
 * avoiding having to recache the file multiple times. Such instances are also monitored by Servlet Filters and EJB
 * interceptors, ensuring that resources are cleaned up once the request or API method call is finished.
 * 
 * If a TemporaryFileCache with a different lifecycle is required, it should be obtained by calling
 * newTemporaryFileCache(). In this case, the client is responsible for ensuring that the cache's resources are cleaned
 * up as promptly as possible once it's finished using it.
 * 
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
     * @return a new instance of TemporaryFileCache not tied to the current thread.
     */
    public static TemporaryFileCache newTemporaryFileCache() {
        return tempFileCacheFactory.createTempFileCache();
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
