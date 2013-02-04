//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.jobqueue;


import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobMessageSender;
import gov.nih.nci.caarray.domain.project.JobSnapshot;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Manages a queue of jobs.
 * 
 * @author jscott
 */
@Singleton
public class JobQueueImpl implements JobQueue {
    private final Queue<ExecutableJob> queue = new LinkedList<ExecutableJob>();
    private final JobMessageSender messageSender;
    private final Lock jobQueueLock = new ReentrantLock();
    private final FileDao fileDao;

    /**
     * @param messageSender the MessageSender dependency.
     * @param fileDao the file dao.
     */
    @Inject
    public JobQueueImpl(JobMessageSender messageSender, FileDao fileDao) {
        this.messageSender = messageSender;
        this.fileDao = fileDao;
    }

    /**
     * {@inheritDoc}
     */
    public void enqueue(ExecutableJob job) {
        job.setJobId(UUID.randomUUID());
        job.markAsInQueue();
        jobQueueLock.lock();
        try {
            queue.add(job);
        } finally {
            jobQueueLock.unlock();
        }
        messageSender.sendEnqueueMessage();
    }

    /**
     * {@inheritDoc}
     */
    public int getLength() {
        jobQueueLock.lock();
        try {
            return queue.size();
        } finally {
            jobQueueLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExecutableJob dequeue() {
        ExecutableJob job = null;
        jobQueueLock.lock();
        try {
            if (queue.size() == 0) {
                throw new IllegalStateException("the JobQueue is empty");
            }

            job = queue.remove();
        } finally {
            jobQueueLock.unlock();
        }
        return job;
    }

    /**
     * {@inheritDoc}
     */
    public ExecutableJob peekAtJobQueue() {
        return queue.peek();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Job> getJobsForUser(User user) {
        List<Job> snapshotList = new ArrayList<Job>();
        jobQueueLock.lock();
        try {
            int position = 1;
            for (ExecutableJob originalJob : getJobList()) {
                snapshotList.add(new JobSnapshot(user, originalJob, position++));
            }
        } finally {
            jobQueueLock.unlock();
        }

        return snapshotList;
    }
    
    /**
     * Get all the jobs on the queue as a list.
     * @return the list of jobs
     */
    protected List<ExecutableJob> getJobList() {
        jobQueueLock.lock();
        try {
            return new ArrayList<ExecutableJob>(queue);
        } finally {
            jobQueueLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean cancelJob(String jobId, User user) {
        for (ExecutableJob originalJob : getJobList()) {
            jobQueueLock.lock();
            try {
                if (originalJob.getJobId().equals(UUID.fromString(jobId))) {
                    if (originalJob.isInProgress() 
                            || !originalJob.getOwnerName().equalsIgnoreCase(user.getLoginName())) {
                        return false;
                    }
                    
                    if (queue.remove(originalJob)) {
                        originalJob.markAsCancelled();
                        fileDao.flushSession();
                        fileDao.clearSession();
                        return true;
                    }
                }
            } finally {
                jobQueueLock.unlock();
            }
        }
        return false;
    }
}
