//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.project.Project;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for projects.
 * @author dkokotov
 */
public enum ProjectSortCriterion implements SortCriterion<Project> {
    /**
     * Experiment ID.
     */
    PUBLIC_ID("experiment.publicIdentifier"),

    /**
     * Experiment title.
     */
    TITLE("experiment.title"),

    /**
     * Organism.
     */
    ORGANISM("experiment.organism.scientificName"),

    /**
     * Assay Type.
     */
    ASSAY_TYPE("experiment.assayType"),

    /**
     * Workflow status.
     */
    STATUS("statusInternal"),

    /**
     * Workflow status.
     */
    LAST_UPDATED("lastUpdated");

    private final String orderField;

    private ProjectSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
