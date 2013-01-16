//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import org.hibernate.dialect.MySQLInnoDBDialect;

/**
 * A hack: hibernate has a bug in parsing the Filter clauses with subselects where it unnecessarily prepends
 * table qualifications to identifiers. To get around this, we need to use a special known value as the identifier
 * and then tell hibernate that it's a keyword, thereby preventing it from qualifying it with the table name.
 * @author dkokotov
 */
public class CaarrayInnoDBDialect extends MySQLInnoDBDialect {
    /** The well known value used as the identifier in the nested subqueries for CSM filters. */
    public static final String FILTER_ALIAS = "__caarray_filter_alias__";

    /**
     * Create a new dialect.
     */
    public CaarrayInnoDBDialect() {
        super();
        registerKeyword(FILTER_ALIAS);
    }
}
