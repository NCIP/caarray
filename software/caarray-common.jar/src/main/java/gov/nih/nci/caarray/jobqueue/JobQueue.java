//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.jobqueue;


import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

/**
 * Manages a queue of jobs.
 * @author jscott
 *
 */
public interface JobQueue {

    /**
     * Adds a job to the end of the queue.
     * @param job the job to add
     */
    void enqueue(ExecutableJob job);

    /**
     * Peek and return the job at the head of the queue, but do not remove it from the queue. 
     * @return the job at the head of the queue or null if the queue is empty.
     */
    ExecutableJob peekAtJobQueue();
    
    /**
     * @return the number of jobs in the queue.
     */
    int getLength();

    /**
     * Remove the job at the head of the queue and return it. 
     * Throws an exception if either the job queue is empty, or if the job is in progress. 
     * @return the job at the head of the queue.
     */
    ExecutableJob dequeue();
    
    /**
     * Gets all jobs on the queue as a list. The reqd and write access properties are set
     * according to the privileges of the user with the given user name.
     * @param user the username of the owner
     * @return all jobs accessible to the user with the given user name
     */
    List<Job> getJobsForUser(User user);

    /**
     * Cancels the given job.
     * 
     * @param jobId string representation of the job id to cancel.
     * @param user the logged in user.
     * @return a boolean value that indicates if the job was canceled or not. true implies that the job was
     * canceled successfully, whereas a false value indicates that the job could not be canceled. 
     */
    boolean cancelJob(String jobId, User user);
}
