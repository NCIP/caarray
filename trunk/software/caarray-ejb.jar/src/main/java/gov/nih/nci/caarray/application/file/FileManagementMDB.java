//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Singleton MDB that handles file import jobs.
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = FileManagementMDB.QUEUE_JNDI_NAME),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
    }, messageListenerInterface = MessageListener.class)
@Interceptors({ HibernateSessionInterceptor.class, ExceptionLoggingInterceptor.class })
@TransactionManagement(TransactionManagementType.BEAN)
public class FileManagementMDB implements MessageListener {

    private static final Logger LOG = Logger.getLogger(FileManagementMDB.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 3600;

    /**
     * JNDI name for file management handling <code>Queue</code>.
     */
    static final String QUEUE_JNDI_NAME = "queue/caArray/FileManagement";

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
    @Resource private UserTransaction transaction;
    private static ThreadLocal<FileManagementMDB> currentMDB = new ThreadLocal<FileManagementMDB>();

    /**
     * Handles file management job message.
     *
     * @param message the JMS message to handle.
     */
    public void onMessage(Message message) {
        if (!(message instanceof ObjectMessage)) {
            LOG.error("Invalid message type delivered: " + message.getClass().getName());
            return;
        }
        try {
            currentMDB.set(this);
            Serializable contents = ((ObjectMessage) message).getObject();
            if (!(contents instanceof AbstractFileManagementJob)) {
                LOG.error("Invalid message contents: " + contents.getClass().getName());
            } else {
                AbstractFileManagementJob job = (AbstractFileManagementJob) contents;
                job.setDaoFactory(getDaoFactory());
                UsernameHolder.setUser(job.getUsername());
                setInProgressStatus(job);
                performJob(job);
                LOG.info("Successfully completed job");
            }
        } catch (JMSException e) {
            LOG.error("Error handling message", e);
        } finally {
            currentMDB.remove();
        }
    }

    /**
     * Begin the transaction used by the job.
     */
    public void beginTransaction() {
        try {
            this.transaction.setTransactionTimeout(getBackgroundThreadTransactionTimeout());
            this.transaction.begin();
        } catch (NotSupportedException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (SystemException e) {
            LOG.error("Couldn't start transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * Get the background thread timeout.
     * @return the timeout val
     */
    protected int getBackgroundThreadTransactionTimeout() {
        String backgroundThreadTransactionTimeout =
            ConfigurationHelper.getConfiguration().getString(
                    ConfigParamEnum.BACKGROUND_THREAD_TRANSACTION_TIMEOUT.name());
        int timeout = DEFAULT_TIMEOUT_SECONDS;
        if (StringUtils.isNumeric(backgroundThreadTransactionTimeout)) {
            timeout = Integer.parseInt(backgroundThreadTransactionTimeout);
        }
        LOG.debug("Background thread transaction timeout setting: " + timeout);
        return timeout;
    }

    /**
     * Commit the transaction used by the job.
     */
    public void commitTransaction()  {
        try {
            this.transaction.commit();
        } catch (SecurityException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (RollbackException e) {
            LOG.error("Received rollback condition", e);
            rollbackTransaction();
        } catch (HeuristicMixedException e) {
            rollbackTransaction();
        } catch (HeuristicRollbackException e) {
            rollbackTransaction();
        } catch (SystemException e) {
            LOG.error("Couldn't commit transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * Roll back the transaction used by the job.
     */
    public void rollbackTransaction() {
        try {
            this.transaction.rollback();
        } catch (SecurityException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (SystemException e) {
            LOG.error("Error rolling back transaction", e);
            throw new EJBException(e);
        }
    }

    private void performJob(AbstractFileManagementJob job) {
        beginTransaction();
        LOG.info("Starting job of type: " + job.getClass().getSimpleName());
        try {
            job.execute();
        } catch (RuntimeException e) {
            rollbackTransaction();
            handleUnexpectedError(job);
            throw e;
        }
        commitTransaction();
    }

    /**
     * Handles unexpected errors.
     * @param job the job.
     */
    protected void handleUnexpectedError(AbstractFileManagementJob job) {
        job.handleUnexpectedError();
    }

    private void setInProgressStatus(AbstractFileManagementJob job)  {
        beginTransaction();
        job.setInProgressStatus();
        commitTransaction();
    }

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * @return the transaction
     */
    public UserTransaction getTransaction() {
        return this.transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(UserTransaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Get the instance of FileManagementMDB that is processing the current message.
     * @return the current instance
     */
    public static FileManagementMDB getCurrentMDB() {
        return currentMDB.get();
    }

}
