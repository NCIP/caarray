//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.jobqueue;

import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

/**
 * Provides access and management functionality to the Job Queue.
 * @author Krishna Kanchinadam
 */

public interface JobQueueService {
    /**
     * The default JNDI name to use to lookup <code>JobQueueService</code>.
     */
    String JNDI_NAME = "caarray/JobQueueServiceBean/local";

    /**
     * Gets a list of the jobs in the system that the user has access to.
     *  For a user with non-system administrator role, the following jobs will be retrieved:
     *      1. Jobs in the queue that correspond to an experiment for which the user does not have full read
     *         and/or read-write privileges.
     *      2. Jobs corresponding to experiments owned by the user.
     *      3. Jobs corresponding to experiments owned by a different user, but for which the user has full
     *         read privileges.
     *  For a user with system administrator role, the following jobs will be retrieved:
     *      1. All jobs in the queue.
     *
     *  @param user user for which the jobs in the queue are to be retrieved.
     *  @return all jobs for the user.
     */
    List<Job> getJobsForUser(User user);

    /**
     * Gets the count of jobs.
     *
     * @param user the user the jobs are related to
     * @return the count of all jobs directly related to the given user.
     */
    int getJobCount(User user);

    /**
     * Cancels the given job. If it is a split job, then all siblings are also canceled.
     *
     * @param jobId string representation of the job id to cancel.
     * @param user the logged in user.
     * @return a boolean value that indicates if the job was canceled or not. true implies that the job was
     * canceled successfully, whereas a false value indicates that the job could not be canceled. In the case
     * of jobs with siblings, return true if at least one job was canceled.
     */
    boolean cancelJob(String jobId, User user);
}
