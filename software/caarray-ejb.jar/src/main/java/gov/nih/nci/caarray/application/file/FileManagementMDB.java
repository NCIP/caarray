//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.StorageInterceptor;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
import javax.jms.TextMessage;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Singleton MDB that handles file import jobs.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = FileManagementMDB.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1") },
    messageListenerInterface = MessageListener.class)
@Interceptors({HibernateSessionInterceptor.class, ExceptionLoggingInterceptor.class, InjectionInterceptor.class,
        StorageInterceptor.class })
@TransactionManagement(TransactionManagementType.BEAN)
public class FileManagementMDB implements MessageListener {

    private static final Logger LOG = Logger.getLogger(FileManagementMDB.class);

    /**
     * JNDI name for file management handling Topic.
     */
    static final String QUEUE_JNDI_NAME = "topic/caArray/FileManagement";
    private static ThreadLocal<FileManagementMDB> currentMDB = new ThreadLocal<FileManagementMDB>();

    @Resource
    private UserTransaction transaction;

    private CaArrayHibernateHelper hibernateHelper;
    private JobQueue jobQueue;
    private Provider<UsernameHolder> userHolderProvider;

    /**
     * Default no-arg ctor is required by app container.
     */
    public FileManagementMDB() {
        //no-op default ctor. Dependencies should be added through the setters.
    }

    /**
     * STARTING WITH caArray 2.5.0 THIS VERSION OF THE CTOR IS A CONVENIENCE FOR TESTING USE ONLY.
     *
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     * @param jobQueue the JobQueue dependency
     * @param userHolderProvider provides userHolder objects. Using a provider here to enable a possible
     *        future enhancement where thread specific userHolders can be provided.
     */
     FileManagementMDB(CaArrayHibernateHelper hibernateHelper, JobQueue jobQueue,
            Provider<UsernameHolder> userHolderProvider) {
        this.hibernateHelper = hibernateHelper;
        this.jobQueue = jobQueue;
        this.userHolderProvider = userHolderProvider;
    }

    /**
     * Handles file management job message.
     *
     * @param message the JMS message to handle.
     */
    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage)) {
            LOG.error("Invalid message type delivered: " + message.getClass().getName());
            return;
        }
        try {
            currentMDB.set(this);
            final String messageText = ((TextMessage) message).getText();
            if ("enqueue".equals(messageText)) {
                final UsernameHolder usernameHolder = this.userHolderProvider.get();
                final ExecutableJob job = jobQueue.peekAtJobQueue();
                if (null != job) {
                    final String previousUser = usernameHolder.getUser();
                    usernameHolder.setUser(job.getOwnerName());
                    try {
                        setJobInProgress(job);
                        performJob(job);
                        LOG.info("Successfully completed job");
                    } finally {
                        // remove the job from the queue if there is an exception or successfully completed.
                        jobQueue.dequeue();
                        usernameHolder.setUser(previousUser);
                    }
                }
            } else {
                LOG.error("Invalid message text: \"" + messageText + "\"");
            }
        } catch (final JMSException e) {
            LOG.error("Error handling message", e);
        } finally {
            currentMDB.remove();
        }
    }

    private void setJobInProgress(ExecutableJob job) {
        beginTransaction();
        job.markAsInProgress();
        commitTransaction();
    }

    /**
     * Begin the transaction used by the job.
     */
    public void beginTransaction() {
        try {
            this.transaction.setTransactionTimeout(getBackgroundThreadTransactionTimeout());
            this.transaction.begin();
        } catch (final NotSupportedException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (final SystemException e) {
            LOG.error("Couldn't start transaction", e);
            throw new EJBException(e);
        }
    }

    /**
     * Get the background thread timeout.
     * @return the timeout val
     */
    protected int getBackgroundThreadTransactionTimeout() {
        return Utils.getBackgroundThreadTransactionTimeout();
    }

    /**
     * Commit the transaction used by the job.
     */
    public void commitTransaction() {
        try {
            this.transaction.commit();
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
     * Roll back the transaction used by the job.
     */
    public void rollbackTransaction() {
        try {
            this.transaction.rollback();
        } catch (final SecurityException e) {
            LOG.error("Unexpected throwable -- transaction is supported", e);
            throw new IllegalStateException(e);
        } catch (final SystemException e) {
            LOG.error("Error rolling back transaction", e);
            throw new EJBException(e);
        }
    }

    private void performJob(ExecutableJob job) {
        beginTransaction();
        LOG.info("Starting job of type: " + job.getClass().getSimpleName());
        try {
            job.execute();
            job.markAsProcessed();
        } catch (final RuntimeException e) {
            rollbackTransaction();
            handleUnexpectedError(job);
            throw e;
        }
        commitTransaction();
    }

    /**
     * Handles unexpected errors.
     *
     * @param job the job.
     */
    protected void handleUnexpectedError(ExecutableJob job) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.hibernateHelper.getNewConnection();
            con.setAutoCommit(false);
            ps = job.getUnexpectedErrorPreparedStatement(con);
            ps.executeUpdate();
            con.commit();
        } catch (final SQLException e) {
            LOG.error("Error while attempting to handle an unexpected error.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (final SQLException e) {
                LOG.error("Error while attempting close the connection after handling an unexpected error.", e);
            }
        }
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
     *
     * @return the current instance
     */
    public static FileManagementMDB getCurrentMDB() {
        return currentMDB.get();
    }

    /**
     * @param hibernateHelper the hibernateHelper to set
     */
    @Inject
    public void setHibernateHelper(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * @param jobQueue the jobQueue to set
     */
    @Inject
    public void setJobQueue(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    /**
     * @param userHolderProvider the userHolderProvider to set
     */
    @Inject
    public void setUserHolderProvider(Provider<UsernameHolder> userHolderProvider) {
        this.userHolderProvider = userHolderProvider;
    }

}
