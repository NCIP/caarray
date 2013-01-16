//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

/**
 * Indicates the status of a<code>Job</code>.
 * @author kkanchinadam
 *
 */
public enum JobStatus {
    /**
     * Indicates the job is running or currently being processed.
     */
    RUNNING("Running"),
    /**
     * Indicates the job is in queue.
     */
    IN_QUEUE("In Queue"),
    /**
     * Indicates the job has been cancelled.
     */
    CANCELLED("Cancelled"),
    /**
     * Indicates the job has been processed.
     */
    PROCESSED("Processed");

    private final String displayName;

    JobStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return File status display name.
     */
    public String getDisplayValue() {
        return displayName;
    }
}
