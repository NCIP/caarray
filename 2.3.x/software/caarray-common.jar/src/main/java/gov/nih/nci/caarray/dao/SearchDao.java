//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO to search for entities using different criteria. Supports searching by example, CQL, HQL (Hibernate Query
 * Language) string and Hibernate Detached Criteria.
 *
 * @author Rashmi Srinivasa
 */
public interface SearchDao extends CaArrayDao {
    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity and its associations, or null
     * if none exists. If the id of the given entity is not null, this query will only match by id, ignoring all other
     * fields.
     *
     * @param <T> the type of entities to retrieve
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this entity
     * @return the List of <code>AbstractCaArrayEntity</code> objects, or an empty List.
     */
    <T extends AbstractCaArrayObject> List<T> query(T entityToMatch);

    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> retrieved based on the given CQL query.
     *
     * @param cqlQuery CQL query to use as search criteria.
     * @return the List of objects, values, or the count, depending on query modifier.
     */
    List<?> query(CQLQuery cqlQuery);

    /**
     * Retrieves the object from the database.
     *
     * @param <T> the class to retrieve
     * @param entityClass the class of the object to retrieve
     * @param entityId the id of the entity to retrieve
     * @return the entity.
     */
    <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId);

    /**
     * Retrieves the object from the database using given lock mode.
     *
     * @param <T> the class to retrieve
     * @param entityClass the class of the object to retrieve
     * @param entityId the id of the entity to retrieve
     * @param lockMode the lock mode to use
     * @return the entity.
     */
    <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode);

    /**
     * Retrieves the object from the database, bypassing security filters.
     *
     * @param <T> the class to retrieve
     * @param entityClass the class of the object to retrieve
     * @param entityId the id of the entity to retrieve
     * @return the entity.
     */
    <T extends PersistentObject> T retrieveUnsecured(Class<T> entityClass, Long entityId);

    /**
     * Return the instances of given class with given ids from the database.
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of entity to retrieve. must not be null
     * @param ids the ids of entities to retrieve.
     * @return the entities with given ids
     */
    <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids);

    /**
     * Refreshes the object's state from the database.
     *
     * @param o the object whose state needs to be refreshed. This must be a persistent or detached object (ie its id
     *            must be non-null)
     */
    void refresh(PersistentObject o);

    /**
     * Method to get all the entities of a given type.
     *
     * @param <T> the type
     * @param entityClass the class
     * @param orders the order by clauses
     * @return the list of objects
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders);

    /**
     * Filters the given collection where the given property = the given value.
     *
     * @param <T> the class of objects to expect in return.
     * @param collection the collection to filter.
     * @param property the property to filter on.
     * @param value the value of the property.
     * @return the list of objects representing the filtered set.
     */
    <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property, String value);

    /**
     * Retrieves a specific subset of items from a given collection based on given sorting and paging params. The
     * intention is that this collection is proxied, and the implementations of this will be able to retrieve the
     * requested subset without needing to initialize the entire collection.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     *
     * @param <T> the class of objects to expect in return.
     * @param collection the collection from which to retrieve the subset
     * @param pageSortParams parameters specifying how the collection is to be sorted and which page is to be retrieved
     * @return the list of objects representing the requested subset
     */
    <T extends PersistentObject> List<T> pageCollection(Collection<T> collection, PageSortParams<T> pageSortParams);

    /**
     * Returns the size of a given collection. The intention is that this collection is proxied, and the implementations
     * of this will be able to calculate the size without initializing it.
     *
     * @param collection the collection whose size to calculate
     * @return the size of the collection
     */
    int collectionSize(Collection<? extends PersistentObject> collection);

    /**
     * Retrieve the list of values of the given field of the given entity that start with the given prefix.
     *
     *
     * @param entityClass the entity class
     * @param fieldName the field name. This must be a String-valued field
     * @param prefix the string that the values should begin with
     * @return the List of values of the given field of the given entity that start with the given value
     */
    List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String prefix);
}
