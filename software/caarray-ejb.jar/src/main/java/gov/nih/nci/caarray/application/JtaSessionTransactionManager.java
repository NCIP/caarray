//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Implementation of SessionTransactionManager that forwards to the current Hibernate session and JTA transaction
 * manager (via FileManagementMDB).
 *
 * @author dkokotov
 */
public class JtaSessionTransactionManager implements SessionTransactionManager {
    private static final Logger LOG = Logger.getLogger(JtaSessionTransactionManager.class);
    private SearchDao searchDao;
    private UserTransaction transaction;

    /**
     * {@inheritDoc}
     */
    public void beginTransaction() {
        try {
            getTransaction().setTransactionTimeout(Utils.getBackgroundThreadTransactionTimeout());
            getTransaction().begin();
        } catch (final NotSupportedException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (final SystemException e) {
            LOG.error("Couldn't start transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearSession() {
        this.searchDao.clearSession();
    }

    /**
     * {@inheritDoc}
     */
    public void commitTransaction() {
        try {
            getTransaction().commit();
        } catch (final SecurityException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (final RollbackException e) {
            LOG.error("Received rollback condition", e);
            rollbackTransaction();
        } catch (final HeuristicMixedException e) {
            rollbackTransaction();
        } catch (final HeuristicRollbackException e) {
            rollbackTransaction();
        } catch (final SystemException e) {
            LOG.error("Couldn't commit transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flushSession() {
        this.searchDao.flushSession();
    }

    /**
     * {@inheritDoc}
     */
    public void rollbackTransaction() {
        try {
            getTransaction().rollback();
        } catch (final SecurityException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (final SystemException e) {
            LOG.error("Error rolling back transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @return the transaction
     */
    private UserTransaction getTransaction() throws IllegalStateException {
        if (transaction == null) {
            try {
                InitialContext ic = new InitialContext();
                transaction = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            } catch (NamingException e) {
                LOG.error("Couldn't get InitialContext", e);
                throw new IllegalStateException(e);
            }
        }

        return transaction;
    }

}
