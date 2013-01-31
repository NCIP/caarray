//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.sample.LabeledExtract;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for labeled extracts.
 * @author dkokotov
 */
public enum LabeledExtractSortCriterion implements SortCriterion<LabeledExtract> {
    /**
     * name.
     */
    NAME("name"),

    /**
     * description.
     */
    DESCRIPTION ("description");

    private final String orderField;

    private LabeledExtractSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
