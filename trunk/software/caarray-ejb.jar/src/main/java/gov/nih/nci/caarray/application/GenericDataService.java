//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Generic service for handling data.
 * @author Scott Miller
 */
public interface GenericDataService extends com.fiveamsolutions.nci.commons.service.GenericDataService {

    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/GenericDataServiceBean/local";

    /**
     * Retrieves all instances of the given class.
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param orders the order by clauses
     * @return the list of t's
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders);

    /**
     * Retrieves the given number of instances of the given class, starting with the given index, out of all
     * the instances in the persistent store.
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param maxResults number of entities to retrieve. A negative or 0 value would indicate no limit.
     * @param firstResult 0-based index of first entity to retrieve, given the ordering specified.
     * @param orders the order by clauses. these may not be specified, but in that case the ordering used to 
     * figure out which entity to start with is undefined. 
     * @return the list of instances. If maxResults was greater than 0, the list will have at most 
     * maxResults elements, but may have less.
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders);

    /**
     * Retrieves all instances of the given class with given ids.
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param ids the ids of entities to retrieve
     * @return the list of entities with given ids
     */
    <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids);

    /**
     * Deletes an object from the database.  May throw exceptions if the object is referenced
     * by other objects in the system.
     *
     * @param object the object to delete
     */
    void delete(PersistentObject object);

    /**
     * Save an object.
     * @param object the object to save.
     */
    void save(PersistentObject object);

    /**
     * Generate a name suitable for assignment to a copy of an entity with the given name.
     * The idea is to add a numeric prefix to the end of the name, so that if the current
     * entity name is "Baz", the copy becomes "Baz2", then "Baz3", and so forth.
     *
     * More formally, the copy name is derived from the given name as follows:
     * <ol>
     * <li>Calculate the non-numerical prefix in the given name. Thus "Baz"->"Baz", "Baz2" -> "Baz",
     * and "Baz2n" -> "Baz2n"
     * <li>Look at all instances of the same entity whose names consist of the same prefix plus any number
     * of numeric suffixes
     * <li>Determine the maximum numeric suffix of this set of names, and add one
     * to this number. The minimum for this value is 2.
     * <li>The copy name is the prefix from step 1 concatenated with the number calculated in step 3.
     * </ol>
     * As an example, given "Baz2" as the input name, and "Baz", "Baz5", "Baz7n" and "Boo" as the names
     * of all the entities of that class, the copy name will be "Baz6"
     *
     * @param entityClass the entity class for which to calculate the copy name
     * @param fieldName the name of the property which holds the name of the entity (will generally be "name")
     * @param name the given name
     * @return the copy name, as calculated according to the above algorithm
     */
    String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name);

    /**
     * Filters the given collection where the given property = the given value.
     * @param <T> the class of objects to expext in return.
     * @param collection the collection to filter.
     * @param property the property to filter on.
     * @param value the value of the property.
     * @return the list of objects representing the filtered set.
     */
    <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property, String value);

    /**
     * Retrieves a specific subset of items from a given collection based on given sorting and paging params. The
     * intention is that this collection is proxied, and the implementations of this will be able to retrieve the
     * requested subset without needing to initialize the entire collection
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
      * Optionally filters the given collection based on given property and values and returns a subset of the results 
      * based on given sorting and paging params. It is intended that the collection may be proxied and implementations
      * will be able to query the persistent store without initializing the whole collection.
      *
      * Note that this method currently only supports SortCriterions that are either simple properties of the target
      * class or required single-valued associations from it. If a non-required association is used in the sort
      * criterion, then any instances for which that association is null will not be included in the results 
      * (because an inner join is used)
      *
      * @param <T> the class of objects to expect in return.
      * @param collection the collection from which to retrieve the subset
      * @param property the property of the objects in the target collection to filter on
      * @param values the expected values for the property. An in comparison is used. If values is null or empty, then
      * no filtering will be done
      * @param pageSortParams parameters specifying how the collection is to be sorted and which page is to be retrieved
      * @return the list of objects representing the requested subset of the collection.
      */
     <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
             List<? extends Serializable> values, PageSortParams<T> pageSortParams);

     /**
      * Returns the size of a given collection. The intention is that this collection is proxied, and the
      * implementations of this will be able to calculate the size without initializing it.
      * @param collection the collection whose size to calculate
      * @return the size of the collection
      */
      int collectionSize(Collection<? extends PersistentObject> collection);
}
