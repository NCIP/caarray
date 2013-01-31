//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.List;

/**
 * Search API for caArray, based on various types of criteria.
 *
 * @author Rashmi Srinivasa
 */
public interface CaArraySearchService {

    /**
     * The JNDI name to look up the remote <code>CaArraySearch</code> service.
     */
    String JNDI_NAME = "caarray/CaArraySearchServiceBean/remote";

    /**
     * Performs a query-by-example search based on the entity passed.
     * If the id of the given entity is not null, this query will only match by id,
     * ignoring all other fields.
     *
     * @param <T> object type to search for
     * @param entityExample find entities that match the non-null fields and associations of this example.
     *
     * @return the matching entities.
     */
    <T extends AbstractCaArrayObject> List<T> search(T entityExample);

    /**
     * Searches for entities based on the given CQL query.
     *
     * @param cqlQuery the HQL (Hibernate Query Language) string to use as search criteria.
     *
     * @return the result for the provided query.  May be the list of objects, list of attribute values, or
     * the count, depending upon the query modifiers.
     */
    List<?> search(CQLQuery cqlQuery);
}
