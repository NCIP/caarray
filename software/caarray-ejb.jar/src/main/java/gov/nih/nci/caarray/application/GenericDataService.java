/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.application;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Generic service for handling data.
 * 
 * @author Scott Miller
 */
public interface GenericDataService extends com.fiveamsolutions.nci.commons.service.GenericDataService {

    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/GenericDataServiceBean/local";

    /**
     * Retrieves all instances of the given class.
     * 
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param orders the order by clauses
     * @return the list of t's
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders);

    /**
     * Retrieves the given number of instances of the given class, starting with the given index, out of all the
     * instances in the persistent store.
     * 
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param maxResults number of entities to retrieve. A negative or 0 value would indicate no limit.
     * @param firstResult 0-based index of first entity to retrieve, given the ordering specified.
     * @param orders the order by clauses. these may not be specified, but in that case the ordering used to figure out
     *            which entity to start with is undefined.
     * @return the list of instances. If maxResults was greater than 0, the list will have at most maxResults elements,
     *         but may have less.
     */
    <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders);

    /**
     * Retrieves all instances of the given class with given ids.
     * 
     * @param <T> the type of the entity to retrieve
     * @param entityClass the class of the entity to retrieve
     * @param ids the ids of entities to retrieve
     * @return the list of entities with given ids
     */
    <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids);

    /**
     * Deletes an object from the database. May throw exceptions if the object is referenced by other objects in the
     * system.
     * 
     * @param object the object to delete
     */
    void delete(PersistentObject object);

    /**
     * Save an object.
     * 
     * @param object the object to save.
     */
    void save(PersistentObject object);

    /**
     * Generate a name suitable for assignment to a copy of an entity with the given name. The idea is to add a numeric
     * prefix to the end of the name, so that if the current entity name is "Baz", the copy becomes "Baz2", then "Baz3",
     * and so forth.
     * 
     * More formally, the copy name is derived from the given name as follows:
     * <ol>
     * <li>Calculate the non-numerical prefix in the given name. Thus "Baz"->"Baz", "Baz2" -> "Baz", and "Baz2n" ->
     * "Baz2n"
     * <li>Look at all instances of the same entity whose names consist of the same prefix plus any number of numeric
     * suffixes
     * <li>Determine the maximum numeric suffix of this set of names, and add one to this number. The minimum for this
     * value is 2.
     * <li>The copy name is the prefix from step 1 concatenated with the number calculated in step 3.
     * </ol>
     * As an example, given "Baz2" as the input name, and "Baz", "Baz5", "Baz7n" and "Boo" as the names of all the
     * entities of that class, the copy name will be "Baz6"
     * 
     * @param entityClass the entity class for which to calculate the copy name
     * @param fieldName the name of the property which holds the name of the entity (will generally be "name")
     * @param name the given name
     * @return the copy name, as calculated according to the above algorithm
     */
    String getIncrementingCopyName(Class<?> entityClass, String fieldName, String name);

    /**
     * Filters the given collection where the given property = the given value.
     * 
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
     * criterion, then any instances for which that association is null will not be included in the results (because an
     * inner join is used)
     * 
     * @param <T> the class of objects to expect in return.
     * @param collection the collection from which to retrieve the subset
     * @param property the property of the objects in the target collection to filter on
     * @param values the expected values for the property. An in comparison is used. If values is null or empty, then no
     *            filtering will be done
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
     * Refresh the state of the given persistent object from the data store.
     * 
     * @param object the object to refresh.
     */
    void refresh(PersistentObject object);
}
