//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author jscott
 *
 */
public interface BaseJob {
    /**
     * @return the job id
     */
    UUID getJobId();

    /**
     * @return the name of the user who owns the job
     */
    String getOwnerName();

    /**
     * @return the name of the experiment or array desing related to the job.
     */
    String getJobEntityName();

    /**
     * @return the id of the experiment or array design related to the job.
     */
    long getJobEntityId();

    /**
     * @return the jobType
     */
    JobType getJobType();

    /**
     * The time requested is never null.
     * @return the timeRequested
     */
    Date getTimeRequested();

    /**
     * @return the timeStarted
     */
    Date getTimeStarted();


    /**
     * @return the job status
     */
    JobStatus getJobStatus();

    /**
     * @return true if the job is in progress
     */
    boolean isInProgress();

    /**
     * @return parent of this job or null if this is top-level job
     */
    ParentJob getParent();

    /**
     * Return an empty list if there are no children.
     * @return children of this job
     */
    List<BaseJob> getChildren();
}
