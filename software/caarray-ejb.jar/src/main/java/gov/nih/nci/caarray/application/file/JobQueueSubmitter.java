//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.jobqueue.JobQueue;

import com.google.inject.Inject;

/**
 * Submits jobs via the job queue.
 */
public class JobQueueSubmitter implements FileManagementJobSubmitter {    
    private final JobQueue jobQueue;
    
    /**
     * @param jobQueue the Provider&lt;JobQueue&gt; dependency
     */
    @Inject
    public JobQueueSubmitter(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    /**
     * {@inheritDoc}
     */
    public void submitJob(AbstractFileManagementJob job) {
        jobQueue.enqueue(job);
    }
}
