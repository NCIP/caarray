//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
* Stub for ExecutableJob.
* @author kkanchinadam
*/
public class ExecutableJobStub extends AbstractCaArrayEntity implements ExecutableJob {
    private static final long serialVersionUID = 1234567890L;

    private UUID jobId;
    private String ownerName;
    private String jobEntityName;
    private long jobEntityId;
    private JobType jobType;
    private Date timeRequested;
    private Date timeStarted;
    private JobStatus jobStatus;
    private boolean readWriteAccess;
    private ParentJob parent;
    private List<BaseJob> children;

    public void setReadWriteAccess(boolean readWriteAccess) {
        this.readWriteAccess = readWriteAccess;
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
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * {@inheritDoc}
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
        return jobStatus.equals(JobStatus.RUNNING);
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
    }

    /**
     * {@inheritDoc}
     */
    public void markAsInQueue() {
    }

    /**
     * {@inheritDoc}
     */
    public void markAsCancelled() {
    }

    /**
     * {@inheritDoc}
     */
    public void markAsInProgress() {
    }

    /**
     * {@inheritDoc}
     */
    public void markAsProcessed() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasReadAccess(User user) {
        return readWriteAccess;
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasWriteAccess(User user) {
        return readWriteAccess;
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement getUnexpectedErrorPreparedStatement(
            Connection connection) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFileSet getFileSet() {
        return new CaArrayFileSet();
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
