//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

/**
 * Sends messages to subscribers interested in events on the job queue.
 * @author jscott
 *
 */
public interface JobMessageSender {

    /**
     * Send a message to all subscribers that a new job has been placed in the queue.
     */
    void sendEnqueueMessage();
}
