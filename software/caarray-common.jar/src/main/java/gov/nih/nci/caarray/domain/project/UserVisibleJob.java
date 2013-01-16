//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This object is a synthetic "job" used to represent the aggregate of a set of split jobs. Since job splitting should
 * be transparent to the user, this is the object represented on the UI instead of the individual split jobs
 * @author wcheng
 */
public class UserVisibleJob implements Job {
    private final UUID jobId;
    private final ParentJob parent;
    private final String ownerName;
    private final String jobEntityName;
    private final long jobEntityId;
    private final JobType jobType;
    private Date timeRequested;
    private Date timeStarted;
    private JobStatus jobStatus;
    private final boolean doesUserHaveReadAccess;
    private final boolean doesUserHaveWriteAccess;
    private final boolean doesUserHaveOwnership;
    private final int position;

    private final Map<JobStatus, Integer> statusCounts = new HashMap<JobStatus, Integer>();

    /**
     * Create an object representing the given job and all its siblings.
     * @param childJob one of the sibling jobs this object is an aggregate of
     * @param position the position of the job in the job queue at the time this snapshot is created
     */
    public UserVisibleJob(Job childJob, int position) {
        jobId = childJob.getJobId();
        parent = childJob.getParent();
        ownerName = childJob.getOwnerName();
        jobEntityName = childJob.getJobEntityName();
        jobEntityId = childJob.getJobEntityId();
        jobType = childJob.getJobType();
        doesUserHaveReadAccess = childJob.getUserHasReadAccess();
        doesUserHaveWriteAccess = childJob.getUserHasWriteAccess();
        doesUserHaveOwnership = childJob.getUserHasOwnership();
        timeRequested = childJob.getTimeRequested();
        timeStarted = childJob.getTimeStarted();
        jobStatus = childJob.getJobStatus();
        this.position = position;

        if (parent != null) {
            setJobStatusFromChildren();
            setTimeRequestedFromChildren();
            setTimeStartedFromChildren();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParentJob getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BaseJob> getChildren() {
        return parent.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getJobId() {
        return jobId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobEntityName() {
        return jobEntityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getJobEntityId() {
        return jobEntityId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobType getJobType() {
        return jobType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimeRequested() {
        return timeRequested;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimeStarted() {
        return timeStarted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInProgress() {
        return jobStatus.equals(JobStatus.RUNNING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasReadAccess() {
        return doesUserHaveReadAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasWriteAccess() {
        return doesUserHaveWriteAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasOwnership() {
        return doesUserHaveOwnership;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * A user can only cancel a job which is in_queue, or one of its children is in_queue.
     * @return true if the user can cancel this job
     */
    public boolean getUserCanCancelJob() {
        if (!getUserHasOwnership()) { return false; }
        if (getParent() == null) {
            return JobStatus.IN_QUEUE.equals(getJobStatus());
        } else {
            boolean canCancel = false;
            for (BaseJob job : getChildren()) {
                if (JobStatus.IN_QUEUE.equals(job.getJobStatus())) {
                    canCancel = true;
                    break;
                }
            }
            return canCancel;
        }
    }

    /**
     * @return a map of the number of jobs by status
     */
    public Map<JobStatus, Integer> getStatusCounts() {
        return statusCounts;
    }

    /**
     * @return the number of jobs processed
     */
    public int getJobsProcessed() {
        Integer count = statusCounts.get(JobStatus.PROCESSED);
        return count == null ? 0 : count;
    }

    /**
     * Sets the job status based on the statuses of the children.
     */
    private void setJobStatusFromChildren() {
        for (JobStatus cStatus : JobStatus.values()) {
            statusCounts.put(cStatus, 0);
        }
        for (BaseJob job : parent.getChildren()) {
            JobStatus cStatus = job.getJobStatus();
            statusCounts.put(cStatus, statusCounts.get(cStatus) + 1);
        }
        if (statusCounts.get(JobStatus.CANCELLED) > 0) {
            jobStatus = JobStatus.CANCELLED;
        } else if (statusCounts.get(JobStatus.RUNNING) > 0) {
            jobStatus = JobStatus.RUNNING;
        } else if (statusCounts.get(JobStatus.IN_QUEUE) > 0) {
            jobStatus = JobStatus.IN_QUEUE;
        } else {
            jobStatus = JobStatus.PROCESSED;
        }
    }

    /**
     * Sets time requested to the earliest child.
     */
    private void setTimeRequestedFromChildren() {
        for (BaseJob job : parent.getChildren()) {
            Date cTimeRequested = job.getTimeRequested();
            if (cTimeRequested.before(timeRequested)) {
                timeRequested = cTimeRequested;
            }
        }
    }

    /**
     * Sets time started to the earliest child.
     */
    private void setTimeStartedFromChildren() {
        for (BaseJob job : parent.getChildren()) {
            Date cTimeStarted = job.getTimeStarted();
            if (cTimeStarted != null && cTimeStarted.before(timeStarted)) {
                timeStarted = cTimeStarted;
            }
        }
    }
}
