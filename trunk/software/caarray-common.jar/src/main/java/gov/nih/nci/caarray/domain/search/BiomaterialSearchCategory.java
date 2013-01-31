//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

/**
 * @author mshestopalov
 *
 */
public interface BiomaterialSearchCategory extends ResourceBasedEnum {
    /**
     * @return the resource key that should be used to retrieve a label
     * for this SearchCategory in the UI
     */
    String getResourceKey();

    /**
     * These are the fields to join against in the HQL query.
     * Is null if no join is necessary.
     * @return the fields to join against
     */
    String[] getJoins();

    /**
     * @return the where subclause for this search category. this method assumes that the subclause
     * will be wrapped in parenthesis before being added to the overall where clause of a query.
     */
    String getWhereClause();

}
