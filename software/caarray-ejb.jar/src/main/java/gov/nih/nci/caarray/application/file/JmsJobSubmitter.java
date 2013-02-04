//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.log4j.Logger;

/**
 * Submits jobs via JMS queue.
 */
class JmsJobSubmitter implements Serializable, FileManagementJobSubmitter {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_QUEUE_CONN_FACTORY = "java:/JmsXA";
    private static final Logger LOG = Logger.getLogger(JmsJobSubmitter.class);

    /**
     * {@inheritDoc}
     */
    public void submitJob(AbstractFileManagementJob job) {
        ServiceLocator locator = ServiceLocatorFactory.getLocator();
        final QueueConnectionFactory factory = (QueueConnectionFactory) locator.lookup(DEFAULT_QUEUE_CONN_FACTORY);
        final Queue queue = (Queue) locator.lookup(FileManagementMDB.QUEUE_JNDI_NAME);
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;
        QueueSender queueSender = null;
        try {
            queueConnection = factory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(true, 0);
            queueSender = queueSession.createSender(queue);
            final ObjectMessage message = queueSession.createObjectMessage(job);
            queueSender.send(message);
        } catch (JMSException e) {
            LOG.error("Couldn't submit job to JMS", e);
        } finally {
            close(queueSender);
            close(queueSession);
            close(queueConnection);
        }
    }

    private void close(QueueSender queueSender) {
        if (queueSender != null) {
            try {
                queueSender.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close QueueSender", e);
            }
        }
    }

    private void close(QueueSession queueSession) {
        if (queueSession != null) {
            try {
                queueSession.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close QueueSession", e);
            }
        }
    }

    private void close(QueueConnection queueConnection) {
        if (queueConnection != null) {
            try {
                queueConnection.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close QueueConnection", e);
            }
        }
    }

}
