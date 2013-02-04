//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.project.DefaultJobMessageSenderImpl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.log4j.Logger;

/**
 * Sends messages to subscribers interested in events on the job queue.
 * @author jscott
 *
 */
public class JobMessageSenderImpl extends DefaultJobMessageSenderImpl {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_QUEUE_CONN_FACTORY = "java:/JmsXA";
    private static final Logger LOG = Logger.getLogger(JmsJobSubmitter.class);

    /**
     * {@inheritDoc}
     */
    public void sendEnqueueMessage() {
        ServiceLocator locator = ServiceLocatorFactory.getLocator();
        final TopicConnectionFactory factory = (TopicConnectionFactory) locator.lookup(DEFAULT_QUEUE_CONN_FACTORY);
        final Topic topic = (Topic) locator.lookup(FileManagementMDB.QUEUE_JNDI_NAME);
        TopicConnection connection = null;
        TopicSession session = null;
        TopicPublisher publisher = null;
        try {
            connection = factory.createTopicConnection();
            session = connection.createTopicSession(true, 0);
            publisher = session.createPublisher(topic);
            
            final Message message = session.createTextMessage("enqueue");
            publisher.send(message);
        } catch (JMSException e) {
            LOG.error("Couldn't submit job to JMS", e);
        } finally {
            close(publisher);
            close(session);
            close(connection);
        }
    }

    private void close(TopicPublisher publisher) {
        if (publisher != null) {
            try {
                publisher.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close TopicPublisher", e);
            }
        }
    }

    private void close(TopicSession session) {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close TopicSession", e);
            }
        }
    }

    private void close(TopicConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOG.error("Couldn't close TopicConnection", e);
            }
        }
    }   
}
