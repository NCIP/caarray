//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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

    /**
     * {@inheritDoc}
     */
    public String getLeftJoinField() {
        // this is to support nci-commons-code 1.0.24, but this aspect of the
        // search is not yet used in caaaray or it is implemented diffrently.
        // https://jira.5amsolutions.com/browse/NCIC-60
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
