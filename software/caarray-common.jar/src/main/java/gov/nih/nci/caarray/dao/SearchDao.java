/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
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
     * Returns the list of <code>AbstractCaArrayEntity</code> retrieved based on the given CQL query.
     *
     * @param cqlQuery CQL query to use as search criteria.
     * @return the List of objects, values, or the count, depending on query modifier.
     */
    List<?> query(CQLQuery cqlQuery);
    
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
     * Retrieves a caarray entity by its lsid.
     * @param <T> the type of entity
     * @param entityClass the class of entity to retrieve
     * @param lsid the lsid
     * @return the entity, or null if no entity with that id exists
     */
    <T extends AbstractCaArrayEntity> T getEntityByLsid(Class<T> entityClass, LSID lsid);


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
     * Method to get all the entities of a given type, subject to given paging constraints.
     *
     * @param <T> the type
     * @param entityClass the class
     * @param maxResults number of entities to retrieve. A negative or 0 value would indicate no limit.
     * @param firstResult 0-based index of first entity to retrieve, given the ordering specified.
     * @param orders the order by clauses
     * @return the list of objects
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders);

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
