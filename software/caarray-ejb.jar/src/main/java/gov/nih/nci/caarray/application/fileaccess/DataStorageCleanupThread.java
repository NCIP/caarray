//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.logging.api.logger.util.ThreadVariable;
import gov.nih.nci.logging.api.user.UserInfo;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Thread to cleanup any items held by data storage engines which are no longer references by any caArray objects.
 * 
 * @author dkokotov
 */
public final class DataStorageCleanupThread extends TimerTask {
    private static final Logger LOG = Logger.getLogger(DataStorageCleanupThread.class);

    private final CaArrayHibernateHelper hibernateHelper;

    /**
     * Constructor.
     * 
     * @param hibernateHelper hibernate helper to use
     */
    @Inject
    public DataStorageCleanupThread(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // hack needed for CLM
        ThreadVariable.set(new UserInfo());

        LOG.debug("Beginning storage cleanup run");
        this.hibernateHelper.openAndBindSession();

        ServiceLocatorFactory.getFileAccessService().synchronizeDataStorage();

        this.hibernateHelper.unbindAndCleanupSession();
        LOG.debug("Finished storage cleanup run");
    }
}
