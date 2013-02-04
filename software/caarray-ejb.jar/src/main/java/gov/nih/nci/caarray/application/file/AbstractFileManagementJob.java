//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.ParentJob;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;

/**
 * Base class for file handling jobs.
 */
public abstract class AbstractFileManagementJob implements Serializable, ExecutableJob, ParentJob {

    private static final long serialVersionUID = 1L;

    private String ownerName;
    private Date timeRequested;
    private Date timeStarted;
    private UUID jobId;
    private JobStatus jobStatus;
    private boolean childrenCancelled;
    private FileAccessService fileAccessService;

    private final ParentJob parent;
    private final List<BaseJob> children = new ArrayList<BaseJob>();

    @Inject
    AbstractFileManagementJob(String username, FileAccessService fileAccessService) {
        this(username, null, fileAccessService);
    }

    @Inject
    AbstractFileManagementJob(String username, ParentJob parent, FileAccessService fileAccessService) {
        this.parent = parent;
        this.fileAccessService = fileAccessService;
        init(username);
    }

    final void init(String username) {
        setOwnerName(username);
        this.timeRequested = new Date();
    }

    /**
     * Sets the ownerName.
     *
     * @param ownerName new owner name
     */
    final void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * Perform the job.
     */
    protected abstract void doExecute();

    /**
     * Perform the job by delegating to subclasses.
     */
    @Override
    public void execute() {
        timeStarted = new Date();
        doExecute();
    }

    /**
     * @return the file set this job is operating on
     */
    @Override
    public abstract CaArrayFileSet getFileSet();

    /**
     * @return the FileStatus used to indicate that this job is in progress
     */
    protected abstract FileStatus getInProgressStatus();

    /**
     * Set the appropriate fileset and job status value indicating that the job has been cancelled.
     */
    @Override
    public void markAsCancelled() {
        if (getParent() != null) {
            getParent().handleChildCancelled();
        }
        setFilesetStatus(FileStatus.UPLOADED);
        setJobStatus(JobStatus.CANCELLED);
    }

    /**
     * Set the appropriate fileset and job status value indicating that the job is in progress.
     */
    @Override
    public void markAsInProgress() {
        setFilesetStatus(getInProgressStatus());
        setJobStatus(JobStatus.RUNNING);
    }

    /**
     * Set the job status value indicating that the job has been processed.
     */
    @Override
    public void markAsProcessed() {
        if (getParent() != null) {
            getParent().handleChildProcessed();
        }
        setJobStatus(JobStatus.PROCESSED);
    }

    /**
     * @return the FileStatus used to indicate that this job is in the queue
     */
    protected FileStatus getInQueueStatus() {
        return FileStatus.IN_QUEUE;
    }

   /**
     * Set the appropriate fileset and job status value indicating that the job is in the queue.
     */
    @Override
    public void markAsInQueue() {
        setFilesetStatus(getInQueueStatus());
        setJobStatus(JobStatus.IN_QUEUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleChildCancelled() {
        childrenCancelled = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleChildProcessed() {
        if (childrenCancelled) {
            for (CaArrayFile file : getFileSet().getFiles()) {
                if (isNonImportedArrayData(file) || isNotArrayData(file)) {
                    file.setFileStatus(FileStatus.UPLOADED);
                }
            }
        }
    }

    private boolean isNotArrayData(CaArrayFile file) {
        return !file.getFileType().isArrayData();
    }

    private boolean isNonImportedArrayData(CaArrayFile file) {
        return file.getFileType().isArrayData() 
                && !(file.getFileStatus().equals(FileStatus.IMPORTED) 
                        || file.getFileStatus().equals(FileStatus.IMPORTED_NOT_PARSED));
    }

    private void setFilesetStatus(FileStatus fileStatus) {
        getFileSet().updateStatus(fileStatus);
    }

    /**
     * @return true if the job is in progress
     */
    @Override
    public boolean isInProgress() {
        return jobStatus.equals(JobStatus.RUNNING);
    }

    /**
     * set the appropriate job status value.
     */
    private void setJobStatus(JobStatus status) {
        jobStatus = status;
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
    public abstract PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getJobEntityName();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract long getJobEntityId();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract JobType getJobType();

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
    public void setJobId(UUID jobId) {
        this.jobId = jobId;
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
    public ParentJob getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BaseJob> getChildren() {
        return children;
    }

    /**
     * @return the fileAccessService
     */
    protected FileAccessService getFileAccessService() {
        return fileAccessService;
    }
}
