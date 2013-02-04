//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
     * Workflow status.
     */
    STATUS("locked"),

    /**
     * Last updated.
     */
    LAST_UPDATED("lastUpdated"),

    /**
     * Public Access profile.
     */
    PUBLIC_ACCESS("publicProfile.securityLevelInternal");

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

    /**
     * {@inheritDoc}
     */
    public String getLeftJoinField() {
        // this is to support nci-commons-code 1.0.24, but this aspect of the
        // search is not yet used in caaaray or it is implemented diffrently.
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
