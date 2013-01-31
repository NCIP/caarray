//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.publication.Publication;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for publication.
 * @author dkokotov
 */
public enum PublicationSortCriterion implements SortCriterion<Publication> {
    /**
     * name.
     */
    TITLE("title"),

    /**
     * description.
     */
    AUTHORS("authors"),

    /**
     * URI.
     */
    URI("uri");

    private final String orderField;

    private PublicationSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
