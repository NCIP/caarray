//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobSnapshot;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collections;
import java.util.List;

public class JobDaoSingleJobStub implements JobQueue {
    private ExecutableJob job;
    
    public void enqueue(ExecutableJob job) {
        this.job = job;
    }

    /**
     * {@inheritDoc}
     */
    public ExecutableJob peekAtJobQueue() {
        return job;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setJobInProgress(ExecutableJob job) {
        job.markAsInProgress();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return null != job ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    public ExecutableJob dequeue() {
        return job;
    }

    /**
     * {@inheritDoc}
     */
    public List<Job> getJobsForUser(User user) {
        if (null != job) {
            final int position = 1;
            JobSnapshot jobSnapshot = new JobSnapshot(user, job, position);
            return Collections.singletonList((Job) jobSnapshot);
        } else {
            return Collections.emptyList();
        }
    }

    public boolean cancelJob(String jobId, User user) {
        return true;
    }
}
