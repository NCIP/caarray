//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;


/**
 * Represents a job with children.
 *
 * @author wcheng
 */
public interface ParentJob extends BaseJob {
    /**
     * Indicate a child job has been cancelled to the parent.
     */
    void handleChildCancelled();

    /**
     * Indicate a child job has been processed to the parent.
     * This should handle any required updates to the parent job as a result of the completion of the child job.
     */
    void handleChildProcessed();
}
