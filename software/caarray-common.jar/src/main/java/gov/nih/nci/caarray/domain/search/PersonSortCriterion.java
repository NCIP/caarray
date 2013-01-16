//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.contact.Person;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Enum of possible sort criterions for Person objects.
 * @author Jevon Gill
 */
public enum PersonSortCriterion implements SortCriterion<Person> {

    /**
     * Person first name.
     */
    FIRST_NAME("contact.firstName"),

    /**
     * Person last name.
     */
    LAST_NAME("contact.lastName"),

    /**
     * Person email.
     */
    EMAIL("contact.email"),

    /**
     * Person phone.
     */
    PHONE("contact.phone");

    private final String orderField;

    private PersonSortCriterion(String orderField) {
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
