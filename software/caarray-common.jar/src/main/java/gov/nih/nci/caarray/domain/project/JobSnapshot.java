//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Represents the read-only state of another job at the time an instance of this
 * class is created.
 *
 * @author jscott
 *
 */
public class JobSnapshot implements Job {
    private final BaseJob originalJob;
    private final UUID jobId;
    private final String ownerName;
    private final String jobEntityName;
    private final long jobEntityId;
    private final JobType jobType;
    private final Date timeRequested;
    private final Date timeStarted;
    private final JobStatus jobStatus;
    private final boolean doesUserHaveReadAccess;
    private final boolean doesUserHaveWriteAccess;
    private final boolean doesUserHaveOwnership;
    private final boolean canUserCancelJob;
    private final boolean inProgress;
    private final int position;

    /**
     * @param user the current user
     * @param originalJob the job this object is a snapshot of
     * @param position the position of the job in the job queue at the time this snapshot is created
     */
    public JobSnapshot(User user, ExecutableJob originalJob, int position) {
        this.originalJob = originalJob;
        this.position = position;

        this.jobId = originalJob.getJobId();
        ownerName = originalJob.getOwnerName();
        jobEntityName = originalJob.getJobEntityName();
        jobEntityId = originalJob.getJobEntityId();
        jobType = originalJob.getJobType();
        timeRequested = originalJob.getTimeRequested();
        timeStarted = originalJob.getTimeStarted();
        jobStatus = originalJob.getJobStatus();
        doesUserHaveReadAccess = originalJob.userHasReadAccess(user);
        doesUserHaveWriteAccess = originalJob.userHasWriteAccess(user);
        inProgress = originalJob.isInProgress();
        doesUserHaveOwnership = originalJob.getOwnerName().equalsIgnoreCase(user.getLoginName());
        // Currently, only job owners can cancel a job. Refer to ARRAY-1953 for more information.
        canUserCancelJob = doesUserHaveOwnership && !inProgress;
    }

    /**
     * {@inheritDoc}
     */
    public UUID getJobId() {
        return this.jobId;
    }

    /**
     * {@inheritDoc}
     */
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * {@inheritDoc}
     */
    public String getJobEntityName() {
        return this.jobEntityName;
    }

    /**
     * {@inheritDoc}
     */
    public long getJobEntityId() {
        return this.jobEntityId;
    }

    /**
     * {@inheritDoc}
     */
    public JobType getJobType() {
        return jobType;
    }

    /**
     * {@inheritDoc}
     */
    public Date getTimeRequested() {
        return timeRequested;
    }

    /**
     * {@inheritDoc}
     */
    public Date getTimeStarted() {
        return timeStarted;
    }

    /**
     * {@inheritDoc}
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasReadAccess() {
        return doesUserHaveReadAccess;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasWriteAccess() {
        return doesUserHaveWriteAccess;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasOwnership() {
        return doesUserHaveOwnership;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserCanCancelJob() {
        return canUserCancelJob;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * {@inheritDoc}
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the job represented by this snapshot
     */
    public BaseJob getOriginalJob() {
        return originalJob;
    }

    /**
     * {@inheritDoc}
     */
    public ParentJob getParent() {
        return originalJob.getParent();
    }

    /**
     * {@inheritDoc}
     */
    public List<BaseJob> getChildren() {
        return originalJob.getChildren();
    }
}
