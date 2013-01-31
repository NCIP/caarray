//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Base interface for all caArray domain DAOs.
 * It provides methods to save, update, remove and query entities by example.
 *
 * @author ETavela
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface CaArrayDao {

    /**
     * Saves the entity to persistent storage, updating or inserting
     * as necessary.
     *
     * @param persistentObject the entity to save
     */
    void save(PersistentObject persistentObject);

    /**
     * Saves the collection of entities to persistent storage, updating or inserting
     * as necessary.
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
     * Calls queryEntityByExample(entityToMatch, MatchMode.EXACT, order).
     *
     * @param <T> entity type
     * @param entityToMatch get objects matching this entity
     * @param order list or order by clauses to add
     * @return the List of objects, or an empty List.
     */
    <T> List<T> queryEntityByExample(T entityToMatch, Order... order);

    /**
     * Calls queryEntityByExample(entityToMatch, MatchMode.EXACT, true, new String[] { }, order).
     *
     * @param <T> entity type
     * @param mode string comparison mode
     * @param entityToMatch get objects matching this entity
     * @param order list or order by clauses to add
     * @return the List of objects, or an empty List.
     */
    <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, Order... order);

    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity,
     * or null if none exists.
     *
     * @param <T> entity type
     * @param entityToMatch get objects matching this entity
     * @param mode string comparison mode
     * @param excludeNulls whether to exclude null properties from the query (ie not consider them at all, rather
     * than including an is null check for them)
     * @param excludeProperties a list of additional properties to exclude from the query.
     * @param order list or order by clauses to add
     * @return the List of objects, or an empty List.
     */
    <T> List<T> queryEntityByExample(T entityToMatch, MatchMode mode, boolean excludeNulls, String[] excludeProperties,
            Order... order);

    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity
     * and its associations, or null if none exists.  This method ignores collection associations
     * (ie, one-to-many), but handles non-collection associations such as many-to-one or
     * one-to-one.
     *
     * @param <T> entity type
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this entity
     * @param order list or order by clauses to add
     * @return the List of <code>AbstractCaArrayEntity</code> objects, or an empty List.
     */
    <T extends PersistentObject> List<T> queryEntityAndAssociationsByExample(T entityToMatch, Order... order);

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
     * @param object object to merge
     * @return merged object
     * @see Session#merge(Object)
     */
    Object mergeObject(Object object);

    /**
     * Evicts a persistent object from the current Hibernate <code>Session</code>.
     * @param object object to evict
     */
    void evictObject(Object object);

}
