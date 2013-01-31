//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.sample.Sample;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for samples.
 * @author dkokotov
 */
public enum SampleSortCriterion implements SortCriterion<Sample> {
    /**
     * name.
     */
    NAME("name"),

    /**
     * Experiment title.
     */
    TITLE("experiment.title"),

    /**
     * Organism.
     */
    ORGANISM("experiment.organism.scientificName"),

    /**
     * tissuesite.
     */
    TISSUESITE("tissueSite.value"),

    /**
     * diseasestate.
     */
    DISEASESTATE("diseaseState.value"),



    /**
     * description.
     */
    DESCRIPTION("description");

    private final String orderField;

    private SampleSortCriterion(String orderField, String... joins) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
