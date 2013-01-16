//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.project.Job;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for job queue.
 * @author kkanchinadam
 */
public enum JobSortCriterion implements SortCriterion<Job> {
    /**
     * Position.
     */
    POSITION("jobQueue.position"),

    /**
     * User.
     */
    USER("jobQueue.username"),

    /**
     * Experiment.
     */
    EXPERIMENT("jobQueue.experimentName"),

    /**
     * Job.
     */
    JOB("jobQueue.job"),

    /**
     * Time Requested.
     */
    TIME_REQUESTED("jobQueue.timeRequested"),

    /**
     * Time Started.
     */
    TIME_STARTED("jobQueue.timeStarted"),
    
    /**
     * Status.
     */
    STATUS("jobQueue.status");


    private final String orderField;

    private JobSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getLeftJoinField() {
        // this is to support nci-commons-code 1.0.24, but this aspect of the
        // search is not yet used in caaaray or it is implemented diffrently.
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
