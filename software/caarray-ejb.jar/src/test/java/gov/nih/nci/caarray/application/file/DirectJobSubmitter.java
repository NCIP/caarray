//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.jobqueue.JobQueue;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.lang.UnhandledException;

/**
 * Submitter that circumvents JMS
 */
class DirectJobSubmitter implements FileManagementJobSubmitter {

    private final FileManagementMDB fileManagementMDB;
    private final JobQueue jobQueue;

    DirectJobSubmitter(FileManagementMDB fileManagementMDB, JobQueue jobQueue) {
        this.jobQueue = jobQueue;
        this.fileManagementMDB = fileManagementMDB;
    }

    public void submitJob(AbstractFileManagementJob job) {
        jobQueue.enqueue(job);
        TextMessage message = getMessageStub("enqueue");
        fileManagementMDB.onMessage(message);
    }

    private TextMessage getMessageStub(String messageText) {
        TextMessage message = mock(TextMessage.class);
        try {
            when(message.getText()).thenReturn(messageText);
        } catch (JMSException e) {
            throw new UnhandledException(e);
        }
        return message;
    }
}
