//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
/**
 * SortCriterion where the orderBy field must be used with the joins field because the orderBy field
 * depends on an alias established in the joins.
 * @author mshestopalov
 *
 * @param <T> entity type for which this is sort criterion.
 */
public interface JoinableSortCriterion<T> extends SortCriterion<T> {


    /**
     * Get list of join tables for this search order.
     * @return join tables
     */
    String[] getJoins();

}
