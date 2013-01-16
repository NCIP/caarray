//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base interface for all caArray domain DAOs. It provides methods to save, update, remove and query entities by
 * example.
 * 
 * @author ETavela
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface CaArrayDao {

    /**
     * Saves the entity to persistent storage, updating or inserting as necessary.
     * 
     * @param persistentObject the entity to save
     * @return the id of the persistent object
     */
    Long save(PersistentObject persistentObject);

    /**
     * Saves the collection of entities to persistent storage, updating or inserting as necessary.
     * 
     * @param persistentObjects the entity collection to save
     */
    void save(Collection<? extends PersistentObject> persistentObjects);

    /**
     * Deletes the entity from persistent storage.
     * 
     * @param persistentObject the entity to be deleted.
     */
    void remove(PersistentObject persistentObject);

    /**
     * Query by example using the given example entity, excluding zero or null properties, and using exact matching for
     * string properties. Non-null single valued associations will be included in the query.
     * 
     * @param <T> entity type
     * @param entityToMatch get objects matching this entity
     * @param order list or order by clauses to add
     * @return the List of objects matching the example, or an empty list if no matches.
     */
    <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... order);

    /**
     * Query by example using the given criteria. Non-null single valued associations will be included in the query.
     * 
     * @param <T> entity type
     * @param criteria the criteria for searching, which defines the example entity and search semantics.
     * @param orders list or order by clauses to add
     * @return the List of objects matching the example, or an empty list if no matches.
     */
    <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, Order... orders);

    /**
     * Query by example using the given criteria, subject to the given paging constraints. Non-null single valued
     * associations will be included in the query.
     * 
     * @param <T> entity type
     * @param criteria the criteria for searching, which defines the example entity and search semantics.
     * @param maxResults number of entities to retrieve. A negative or 0 value would indicate no limit.
     * @param firstResult 0-based index of first entity to retrieve, given the ordering specified.
     * @param orders list or order by clauses to add
     * @return the List of objects matching the example, or an empty list if no matches.
     */
    <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria, int maxResults,
            int firstResult, Order... orders);

    /**
     * Flushes the current Hibernate <code>Session</code>.
     */
    void flushSession();

    /**
     * Clears the current Hibernate <code>Session</code>.
     */
    void clearSession();

    /**
     * Merges a persistent object into the current Hibernate <code>Session</code>.
     * 
     * @param object object to merge
     * @return merged object
     * @see Session#merge(Object)
     */
    Object mergeObject(Object object);

    /**
     * Evicts a persistent object from the current Hibernate <code>Session</code>.
     * 
     * @param object object to evict
     */
    void evictObject(Object object);
}
