//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.sample.Source;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for sources.
 * @author dkokotov
 */
public enum SourceSortCriterion implements SortCriterion<Source> {
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
    TISSUESITE("tissuesite"),

    /**
     * diseasestate.
     */
    DISEASESTATE("diseasestate"),

    /**
     * material type.
     */
    MATERIALTYPE("materialType"),

    /**
     * cell type.
     */
    CELLTYPE("cellType"),

    /**
     * provider name.
     */
    PROVIDER_NAME("provider.name"),


    /**
     * description.
     */
    DESCRIPTION("description");

    private final String orderField;

    SourceSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
