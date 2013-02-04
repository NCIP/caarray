//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.ParentJob;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class JobStub extends AbstractCaArrayEntity implements Job {
    private static final long serialVersionUID = 1234567890L;

    private int position;
    private String ownerName;
    private String jobEntityName;
    private long jobEntityId;
    private JobType jobType;
    private Date timeRequested;
    private Date timeStarted;
    private JobStatus jobStatus;
    private boolean userHasReadAccess;
    private boolean userHasWriteAccess;
    private boolean inProgress;
    private UUID jobId;

    private boolean userHasOwnership;

    private ParentJob parent;
    private List<BaseJob> children;

    /**
     * {@inheritDoc}
     */
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * {@inheritDoc}
     */
    public void setUsername(String username) {
        this.ownerName = username;
    }

    /**
     * {@inheritDoc}
     */
    public String getJobEntityName() {
        return jobEntityName;
    }

    /**
     * {@inheritDoc}
     */
    public void setJobEntityName(String jobEntityName) {
        this.jobEntityName = jobEntityName;
    }

    /**
     * {@inheritDoc}
     */
    public long getJobEntityId() {
        return jobEntityId;
    }

    /**
     * {@inheritDoc}
     */
    public void setJobEntityId(long jobEntityId) {
        this.jobEntityId = jobEntityId;
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
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
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
    public void setTimeRequested(Date timeRequested) {
        this.timeRequested = timeRequested;
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
    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
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
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInProgress() {
        return this.inProgress;
    }

    /**
     * @param inProgress
     *            true if the job is in progress
     */
    public void setIsInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasReadAccess() {
        return userHasReadAccess;
    }

    /**
     * @param value
     *            true if the user should have read access
     */
    public void setUserReadAccess(boolean value) {
        userHasReadAccess = value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasWriteAccess() {
        return userHasWriteAccess;
    }

    /**
     * @param value
     *            true if the user should have write access
     */
    public void setUserWriteAccess(boolean value) {
        userHasWriteAccess = value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUserHasOwnership() {
        return this.userHasOwnership;
    }

    /**
     * @param value
     *            true if the user is the owner of the job
     */
    public void setuserHasOwnership(boolean value) {
        userHasOwnership = value;
    }

    /**
     * {@inheritDoc}
     */
    public UUID getJobId() {
        return jobId;
    }

    /**
     * {@inheritDoc}
     */
    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    /**
     * {@inheritDoc}
     */
	public ParentJob getParent() {
		return parent;
	}

    /**
     * {@inheritDoc}
     */
	public void setParent(ParentJob parent) {
		this.parent = parent;
	}

    /**
     * {@inheritDoc}
     */
	public List<BaseJob> getChildren() {
		return children;
	}

    /**
     * {@inheritDoc}
     */
	public void setChildren(List<BaseJob> children) {
		this.children = children;
	}

}
