//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;


/**
 * Class for creating an ad-hoc sort criterion, ie when there is no enumeration of sort fields
 * for an entity.
 * @param <T> the type of the entity to which the sort is to be applied
 * @author dkokotov
 */
public class AdHocSortCriterion<T> implements JoinableSortCriterion<T> {
    private final String orderField;

    /**
     * Create a new criterion using given sort field.
     * @param orderField the field to sort on
     */
    public AdHocSortCriterion(String orderField) {
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
    public String[] getJoins() {
        return null;
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
