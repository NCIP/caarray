/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
