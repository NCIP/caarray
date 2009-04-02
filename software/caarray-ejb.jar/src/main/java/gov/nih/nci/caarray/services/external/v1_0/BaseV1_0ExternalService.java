/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.query.PagingParams;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.AbstractExternalService;
import gov.nih.nci.caarray.services.external.BeanMapperLookup;
import gov.nih.nci.cagrid.cqlquery.Object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * Base service for v1_0 external services.
 * 
 * @author dkokotov
 */
// CHECKSTYLE:OFF
public class BaseV1_0ExternalService extends AbstractExternalService {
// CHECKSTYLE:ON    
    
    private static final String CQL_QUERY_ERROR = "Could not execute CQL Query";
    private static final String UNCHECKED = "unchecked";
    
    /**
     * registry of generic resolvers.
     */
    private final EntityHandlerRegistry entityHandlerRegistry = new EntityHandlerRegistry(); 
    
    /**
     * Constructor.
     */
    public BaseV1_0ExternalService() {
        entityHandlerRegistry
                .addResolver(Organism.class, new PersistentObjectHandler<Organism>(edu.georgetown.pir.Organism.class));
        entityHandlerRegistry.addResolver(DataFile.class, new PersistentObjectHandler<DataFile>(CaArrayFile.class));
        entityHandlerRegistry.addResolver(QuantitationType.class, new PersistentObjectHandler<QuantitationType>(
                gov.nih.nci.caarray.domain.data.QuantitationType.class));
        entityHandlerRegistry.addResolver(Experiment.class, new PersistentObjectHandler<Experiment>(
                gov.nih.nci.caarray.domain.project.Experiment.class));
        entityHandlerRegistry.addResolver(Person.class, new PersistentObjectHandler<Person>(
                gov.nih.nci.caarray.domain.contact.Person.class));
        entityHandlerRegistry.addResolver(Hybridization.class, new PersistentObjectHandler<Hybridization>(
                gov.nih.nci.caarray.domain.hybridization.Hybridization.class));
        entityHandlerRegistry.addResolver(Term.class, new PersistentObjectHandler<Term>(
                gov.nih.nci.caarray.domain.vocabulary.Term.class));
        entityHandlerRegistry.addResolver(Category.class, new PersistentObjectHandler<Category>(
                gov.nih.nci.caarray.domain.vocabulary.Category.class));
        entityHandlerRegistry.addResolver(TermSource.class, new PersistentObjectHandler<TermSource>(
                gov.nih.nci.caarray.domain.vocabulary.TermSource.class));
        entityHandlerRegistry.addResolver(Factor.class, new PersistentObjectHandler<Factor>(
                gov.nih.nci.caarray.domain.project.Factor.class));
        entityHandlerRegistry.addResolver(ExperimentalContact.class, new PersistentObjectHandler<ExperimentalContact>(
                ExperimentContact.class));
        entityHandlerRegistry.addResolver(ArrayDesign.class, new PersistentObjectHandler<ArrayDesign>(
                gov.nih.nci.caarray.domain.array.ArrayDesign.class));
        entityHandlerRegistry.addResolver(Biomaterial.class, new PersistentObjectHandler<Biomaterial>(
                AbstractBioMaterial.class));
        entityHandlerRegistry.addResolver(ArrayDataType.class, new PersistentObjectHandler<ArrayDataType>(
                gov.nih.nci.caarray.domain.data.ArrayDataType.class));
        entityHandlerRegistry.addResolver(gov.nih.nci.caarray.external.v1_0.data.FileType.class, new FileTypeHandler());
    }
    
    /**
     * convert given external paging params instance into internal paging params.
     * 
     * @param <T> type being iterated over.
     * @param params the external params
     * @param sortCriterion the sort criterion to use
     * @param desc whether the sort should be in descending order
     * @return the internal params
     */
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(PagingParams params,
            SortCriterion<T> sortCriterion, boolean desc) {
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> internalParams = 
            new com.fiveamsolutions.nci.commons.data.search.PageSortParams<T>(
                params.getMaxResults(), params.getFirstResult(), sortCriterion, desc);
        return internalParams;
    }

    /**
     * convert given external paging params instance into internal paging params.
     * 
     * @param <T> type being iterated over.
     * @param params the external params
     * @param sortField the field to sort on
     * @param desc whether the sort should be in descending order
     * @return the internal params
     */
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(PagingParams params,
            String sortField, boolean desc) {
        SortCriterion<T> sortCriterion = new AdHocSortCriterion<T>(sortField);
        return toInternalParams(params, sortCriterion, desc);
    }

    /**
     * convert given external paging params instance into internal paging params. Use this version to explicitly
     * specify the class of the target entity, when it cannot be deduced by the compiler.
     * 
     * @param <T> type being iterated over.
     * @param params the external params
     * @param sortField the field to sort on
     * @param desc whether the sort should be in descending order
     * @param targetClass the class of the element entity of the list being sorted paged.
     * @return the internal params
     */
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(PagingParams params,
            String sortField, boolean desc, Class<T> targetClass) {
        SortCriterion<T> sortCriterion = new AdHocSortCriterion<T>(sortField);
        return toInternalParams(params, sortCriterion, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMapperVersionKey() {
        return BeanMapperLookup.VERSION_1_0;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    protected java.lang.Object getByLsid(String lsid) {        
        Class<? extends AbstractCaArrayEntity> entityClass = 
            (Class<? extends AbstractCaArrayEntity>) getClassFromLsid(lsid);
        EntityHandler<? extends AbstractCaArrayEntity> resolver = entityHandlerRegistry.getResolver(entityClass); 
        String objectId = new LSID(lsid).getObjectId();
        return resolver.resolve(objectId);
    }

    /**
     * Retrieve the entity with given lsid expected to exist in the persistent store and have given type.
     * @param <T> the entity type
     * @param lsid the lsid of entity to retrieve
     * @param type the class for the entity type
     * @return the entity
     * @throws InvalidReferenceException if no entity exists with given lsid or the entity is not of the expected type.
     */
    protected <T> T getRequiredByLsid(String lsid, Class<T> type) throws InvalidReferenceException {
        java.lang.Object o = getByLsid(lsid);
        if (o == null) {
            throw new NoEntityMatchingReferenceException(new CaArrayEntityReference(lsid));
        }
        try {
            return type.cast(o);                    
        } catch (ClassCastException e) {
            throw new IncorrectEntityTypeException(e, new CaArrayEntityReference(lsid));            
        }
    }
            
    /**
     * @return the entityHandlerRegistry
     */
    protected EntityHandlerRegistry getEntityHandlerRegistry() {
        return entityHandlerRegistry;
    }

    /**
     * Class to hold a registry of entity handlers, which can be looked up by class of external entity.
     * 
     * @author dkokotov
     */
    protected static class EntityHandlerRegistry {
        private final Map<Class<?>, EntityHandler<? extends AbstractCaArrayEntity>> resolvers = 
            new HashMap<Class<?>, EntityHandler<? extends AbstractCaArrayEntity>>();

        private <T extends AbstractCaArrayEntity, S> void addResolver(Class<T> externalClass, 
                EntityHandler<T> resolver) {
            resolvers.put(externalClass, resolver);
        }

        /**
         * Get the handler for given external entity class.
         * @param <T> type of the external entity class.
         * @param externalClass the class object for the external entity type.
         * @return the handler
         */
        @SuppressWarnings(UNCHECKED)
        public <T extends AbstractCaArrayEntity> EntityHandler<T> getResolver(Class<T> externalClass) {
            return (EntityHandler<T>) resolvers.get(externalClass);
        }
    }

    /**
     * An external entity handler encapsulates the logic to perform generic operations for an external entity type.
     * These include retrieving an instance of it by id, and performing CQL and by-example queries.
     * 
     * @author dkokotov
     *
     * @param <T> the type of the external entity that this EntityHandler handles.
     */
    protected static interface EntityHandler<T extends AbstractCaArrayEntity> {
        /**
         * Retrieve and return the internal instance given an object id.
         * @param objectId the object identifier. the semantics of depend on the external entity type.
         * 
         * @return the internal instance for given object id, or null, if one does not exist.
         */
        java.lang.Object resolve(String objectId);
        
        /**
         * return a list of entities of this handler's type, that match the given example entity, subject to paging
         * parameters.
         * @param example the example entity
         * @param pagingParams the paging parameters
         * @return the matching entities, subject to paging parameters.
         */
        List<T> queryByExample(T example, PagingParams pagingParams);

        /**
         * return a list o entities of this handler's type that match the given CQL target object, subject to paging
         * parameters.
         * @param queryTarget the CQL target object expressing constraints entities should satisfy. The type of this
         * target object must match the type of this handler.
         * @param pagingParams the paging parameters.
         * @return the matching entities, subject to paging parameters.
         */
        List<T> queryByCQL(gov.nih.nci.cagrid.cqlquery.Object queryTarget, PagingParams pagingParams);
        
        /**
         * return a count of entities of this handler's type that match the given CQL target object.
         * @param queryTarget
         * @param queryTarget the CQL target object expressing constraints entities should satisfy. The type of this
         * target object must match the type of this handler.
         * @return the count matching entities.
         */
        int countQueryByCQL(gov.nih.nci.cagrid.cqlquery.Object queryTarget);
    }    

    /**
     * EntityHandler that for external entities that map to internal types extending from PersistentObject.
     * 
     * @author dkokotov
     *
     * @param <T> the external entity type for this handler
     */
    private final class PersistentObjectHandler<T extends AbstractCaArrayEntity> implements EntityHandler<T> {
        private final Class<? extends PersistentObject> internalClass;
        private final Order[] orders;
                
        /**
         * @param internalClass the class object for the internal type for this handler
         * @param orders the set of orders to use when doing queries.
         */
        private PersistentObjectHandler(Class<? extends PersistentObject> internalClass, Order... orders) {
            this.internalClass = internalClass;
            this.orders = orders;
        }

        /**
         * {@inheritDoc}
         * The objectId is expected to be the database id of the internal object.
         */
        public java.lang.Object resolve(String objectId) {
            return getDataService().getPersistentObject(internalClass, Long.valueOf(objectId));
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings(UNCHECKED)
        public List<T> queryByExample(T example, PagingParams pagingParams) {
            PersistentObject internalExample = mapEntity(example, internalClass);        
            List<PersistentObject> results = getDaoFactory().getSearchDao().queryEntityByExample(internalExample,
                    MatchMode.EXACT, true, StringUtils.EMPTY_STRING_ARRAY, pagingParams.getMaxResults(),
                    pagingParams.getFirstResult(), this.orders);
            return mapCollection(results, (Class<T>) example.getClass());
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings(UNCHECKED)
        public List<T> queryByCQL(Object queryTarget, PagingParams pagingParams) {
            try {
                Class<T> entityClass = (Class<T>) Class.forName(queryTarget.getName());
                List<? extends PersistentObject> results = getDataService().retrieveAll(this.internalClass,
                        pagingParams.getMaxResults(), pagingParams.getFirstResult(), orders);            
                return mapCollection(results, entityClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(CQL_QUERY_ERROR, e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(CQL_QUERY_ERROR, e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(CQL_QUERY_ERROR, e);
            }            
        }
        
        /**
         * {@inheritDoc}
         */
        public int countQueryByCQL(Object queryTarget) {
            try {
                return getDataService().retrieveAll(this.internalClass).size();
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(CQL_QUERY_ERROR, e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(CQL_QUERY_ERROR, e);
            }
        }
    }

    /**
     * EntityHandler for FileTypes.
     * @author dkokotov
     */
    private class FileTypeHandler implements EntityHandler<gov.nih.nci.caarray.external.v1_0.data.FileType> {
        /**
         * {@inheritDoc}
         * The objectId is expected to be the name of a FileType constant.
         */
        public java.lang.Object resolve(String objectId) {
            try {
                return mapEntity(FileType.valueOf(objectId), gov.nih.nci.caarray.external.v1_0.data.FileType.class);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        public List<gov.nih.nci.caarray.external.v1_0.data.FileType> queryByExample(
                gov.nih.nci.caarray.external.v1_0.data.FileType example, PagingParams pagingParams) {
            List<gov.nih.nci.caarray.external.v1_0.data.FileType> results = 
                new ArrayList<gov.nih.nci.caarray.external.v1_0.data.FileType>();
            for (FileType type : FileType.values()) {
                boolean nameMatches = example.getName() == null || example.getName().equals(type.name());
                if (nameMatches) {
                    results.add(mapEntity(type, gov.nih.nci.caarray.external.v1_0.data.FileType.class));
                }
            }
            return results;
        }

        /**
         * {@inheritDoc}
         */
        public List<gov.nih.nci.caarray.external.v1_0.data.FileType> queryByCQL(Object queryTarget,
                PagingParams pagingParams) {
            List<FileType> results = Arrays.asList(FileType.values()).subList(pagingParams.getFirstResult(),
                    pagingParams.getFirstResult() + pagingParams.getMaxResults());
            return mapCollection(results, gov.nih.nci.caarray.external.v1_0.data.FileType.class);
        }

        /**
         * {@inheritDoc}
         */
        public int countQueryByCQL(Object queryTarget) {
            return FileType.values().length;
        }
    }
}
