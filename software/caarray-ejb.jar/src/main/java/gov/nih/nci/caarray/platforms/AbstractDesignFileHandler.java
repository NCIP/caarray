//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.Inject;
import com.google.inject.internal.Preconditions;

/**
 * Base class for DesignFileHandler implementations. Provides fields for common dependencies.
 * 
 * @author dkokotov
 */
public abstract class AbstractDesignFileHandler implements DesignFileHandler {
    @Inject
    private SessionTransactionManager sessionTransactionManager;
    @Inject
    private DataStorageFacade dataStorageFacade;
    @Inject
    private ArrayDao arrayDao;
    @Inject
    private SearchDao searchDao;

    /**
     * Empty constructor, needed sometimes when we have to get dependencies injected.
     */
    protected AbstractDesignFileHandler() {
        // empty on purpose
    }

    /**
     * @param sessionTransactionManager the SessionTransactionManager to use
     * @param dataStorageFacade data storage facade to use
     * @param arrayDao the ArrayDao to use
     * @param searchDao the SearchDao to use
     */
    protected AbstractDesignFileHandler(SessionTransactionManager sessionTransactionManager,
            DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
        Preconditions.checkNotNull(sessionTransactionManager);
        Preconditions.checkNotNull(dataStorageFacade);
        Preconditions.checkNotNull(searchDao);
        Preconditions.checkNotNull(arrayDao);

        this.sessionTransactionManager = sessionTransactionManager;
        this.dataStorageFacade = dataStorageFacade;
        this.searchDao = searchDao;
        this.arrayDao = arrayDao;
    }

    /**
     * Convenience method to flush and clear the session using this handler's SessionTransactionManager.
     */
    protected void flushAndClearSession() {
        this.sessionTransactionManager.flushSession();
        this.sessionTransactionManager.clearSession();
    }

    /**
     * @return the SessionTransactionManager
     */
    protected SessionTransactionManager getSessionTransactionManager() {
        return this.sessionTransactionManager;
    }

    /**
     * @return the ArrayDao
     */
    protected ArrayDao getArrayDao() {
        return this.arrayDao;
    }

    /**
     * @return the SearchDao
     */
    protected SearchDao getSearchDao() {
        return this.searchDao;
    }

    /**
     * @return the FileManager
     */
    protected DataStorageFacade getDataStorageFacade() {
        return this.dataStorageFacade;
    }
}
