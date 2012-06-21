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
package gov.nih.nci.caarray.services.external.v1_0.impl;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.AbstractExternalService;
import gov.nih.nci.caarray.services.external.BeanMapperLookup;
import gov.nih.nci.caarray.services.external.v1_0.IncorrectEntityTypeException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.google.inject.Inject;

/**
 * Base service for v1_0 external services.
 *
 * @author dkokotov
 */
// CHECKSTYLE:OFF
public class BaseV1_0ExternalService extends AbstractExternalService {
    // CHECKSTYLE:ON

    private static final String UNCHECKED = "unchecked";

    /**
     * registry of generic resolvers.
     */
    private final EntityHandlerRegistry entityHandlerRegistry = new EntityHandlerRegistry();
    private FileTypeRegistry fileTypeRegistry;
    private String mapperVersionKey = BeanMapperLookup.VERSION_1_0;

    /**
     * Constructor.
     */
    public BaseV1_0ExternalService() {
        this.entityHandlerRegistry.addResolver(Organism.class, new PersistentObjectHandler<Organism>(
                edu.georgetown.pir.Organism.class, Order.asc("scientificName")));
        this.entityHandlerRegistry.addResolver(File.class,
                new PersistentObjectHandler<File>(CaArrayFile.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(QuantitationType.class, new PersistentObjectHandler<QuantitationType>(
                gov.nih.nci.caarray.domain.data.QuantitationType.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(Experiment.class, new PersistentObjectHandler<Experiment>(
                gov.nih.nci.caarray.domain.project.Experiment.class, Order.asc("publicIdentifier")));
        this.entityHandlerRegistry.addResolver(Person.class, new PersistentObjectHandler<Person>(
                gov.nih.nci.caarray.domain.contact.Person.class, Order.asc("lastName"), Order.asc("firstName")));
        this.entityHandlerRegistry.addResolver(Hybridization.class, new PersistentObjectHandler<Hybridization>(
                gov.nih.nci.caarray.domain.hybridization.Hybridization.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(Term.class, new PersistentObjectHandler<Term>(
                gov.nih.nci.caarray.domain.vocabulary.Term.class, Order.asc("value")));
        this.entityHandlerRegistry.addResolver(Category.class, new PersistentObjectHandler<Category>(
                gov.nih.nci.caarray.domain.vocabulary.Category.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(TermSource.class, new PersistentObjectHandler<TermSource>(
                gov.nih.nci.caarray.domain.vocabulary.TermSource.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(Factor.class, new PersistentObjectHandler<Factor>(
                gov.nih.nci.caarray.domain.project.Factor.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(ExperimentalContact.class,
                new PersistentObjectHandler<ExperimentalContact>(ExperimentContact.class));
        this.entityHandlerRegistry.addResolver(ArrayDesign.class, new PersistentObjectHandler<ArrayDesign>(
                gov.nih.nci.caarray.domain.array.ArrayDesign.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(Biomaterial.class, new BiomaterialHandler());
        this.entityHandlerRegistry.addResolver(ArrayDataType.class, new PersistentObjectHandler<ArrayDataType>(
                gov.nih.nci.caarray.domain.data.ArrayDataType.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(gov.nih.nci.caarray.external.v1_0.data.FileType.class,
                new FileTypeHandler());
        this.entityHandlerRegistry.addResolver(AssayType.class, new PersistentObjectHandler<AssayType>(
                gov.nih.nci.caarray.domain.project.AssayType.class, Order.asc("name")));
        this.entityHandlerRegistry.addResolver(ArrayProvider.class, new PersistentObjectHandler<ArrayProvider>(
                Organization.class, Order.asc("name")));
    }

    /**
     * @param fileTypeRegistry the file type registry to use
     */
    @Inject
    public void setFileTypeRegistry(FileTypeRegistry fileTypeRegistry) {
        this.fileTypeRegistry = fileTypeRegistry;
    }

    /**
     * Map of BiomaterialType to the corresponding AbstractBiomaterial subclass in the internal model.
     */
    protected static final Map<BiomaterialType, Class<? extends AbstractBioMaterial>> BIOMATERIAL_TYPE_TO_CLASS_MAP =
            new HashMap<BiomaterialType, Class<? extends AbstractBioMaterial>>();
    static {
        BIOMATERIAL_TYPE_TO_CLASS_MAP.put(BiomaterialType.SOURCE, Source.class);
        BIOMATERIAL_TYPE_TO_CLASS_MAP.put(BiomaterialType.SAMPLE, Sample.class);
        BIOMATERIAL_TYPE_TO_CLASS_MAP.put(BiomaterialType.EXTRACT, Extract.class);
        BIOMATERIAL_TYPE_TO_CLASS_MAP.put(BiomaterialType.LABELED_EXTRACT, LabeledExtract.class);
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
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(LimitOffset params,
            SortCriterion<T> sortCriterion, boolean desc) {
        final com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> internalParams =
                new com.fiveamsolutions.nci.commons.data.search.PageSortParams<T>(params.getLimit(),
                        params.getOffset(), sortCriterion, desc);
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
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(LimitOffset params,
            String sortField, boolean desc) {
        final SortCriterion<T> sortCriterion = new AdHocSortCriterion<T>(sortField);
        return toInternalParams(params, sortCriterion, desc);
    }

    /**
     * convert given external paging params instance into internal ones. Use this version to explicitly specify the
     * class of the target entity, when it cannot be deduced by the compiler.
     *
     * @param <T> type being iterated over.
     * @param params the external params
     * @param sortField the field to sort on
     * @param desc whether the sort should be in descending order
     * @param targetClass the class of the element entity of the list being sorted paged.
     * @return the internal params
     */
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(LimitOffset params,
            String sortField, boolean desc, Class<T> targetClass) {
        final SortCriterion<T> sortCriterion = new AdHocSortCriterion<T>(sortField);
        return toInternalParams(params, sortCriterion, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMapperVersionKey() {
        return mapperVersionKey;
    }

    /**
     * Set a new mapper version key.  Used by test code.
     *
     * @param mapperVersionKey new key
     */
    public void setMapperVersionKey(String mapperVersionKey) {
        this.mapperVersionKey = mapperVersionKey;
    }

    /**
     * Retrieve the entity in the internal domain model identified by the given external id. This is expected to be
     * implemented by version-specific subclasses.
     *
     * @param externalId the external id
     * @return the entity {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    protected java.lang.Object getByExternalId(String externalId) {
        final Class<? extends AbstractCaArrayEntity> entityClass =
                (Class<? extends AbstractCaArrayEntity>) getClassFromExternalId(externalId);
        final EntityHandler<? extends AbstractCaArrayEntity> resolver =
                this.entityHandlerRegistry.getResolver(entityClass);
        if (resolver == null) {
            return null;
        }
        return resolver.resolve(getIdFromExternalId(externalId));
    }

    /**
     * Retrieve the entity with given external id expected to exist in the persistent store and have given type.
     *
     * @param <T> the entity type
     * @param externalId the the external id of entity to retrieve
     * @param type the class for the entity type
     * @return the entity
     * @throws InvalidReferenceException if no entity exists with given external id or the entity is not of expected
     *             type.
     */
    protected <T> T getRequiredByExternalId(String externalId, Class<T> type) throws InvalidReferenceException {
        final java.lang.Object o = getByExternalId(externalId);
        if (o == null) {
            throw new NoEntityMatchingReferenceException(new CaArrayEntityReference(externalId));
        }
        try {
            return type.cast(o);
        } catch (final ClassCastException e) {
            throw new IncorrectEntityTypeException(new CaArrayEntityReference(externalId), // NOPMD
                    "expected a reference to a type " + type.getName());
        }
    }

    /**
     * Retrieve the entity for given reference expected to have given type.
     *
     * @param <T> the entity type
     * @param reference the reference to the entity to retrieve
     * @param type the class for the entity type
     * @return the entity, or null if the reference is null
     * @throws InvalidReferenceException if no entity exists for given reference or the entity is not of the expected
     *             type.
     */
    protected <T> T getByReference(CaArrayEntityReference reference, Class<T> type) throws InvalidReferenceException {
        return reference == null ? null : getRequiredByExternalId(reference.getId(), type);
    }

    /**
     * Convert the given list of references to the underlying internal entities pointed to by those references.
     *
     * @param <T> the expected entity type for each reference
     * @param refs the list of references.
     * @param entities the collection in which to store the referenced internal entities.
     * @param type the class for the entity type
     * @return the entities collection (same instance as passed in the <b>entities</b> parameter)
     * @throws InvalidReferenceException if for any reference, no entity exists or is not of the expected type.
     */
    protected <T> Collection<T> mapRequiredReferencesToEntities(Collection<CaArrayEntityReference> refs,
            Collection<T> entities, Class<T> type) throws InvalidReferenceException {
        for (final CaArrayEntityReference ref : refs) {
            entities.add(getRequiredByExternalId(ref.getId(), type));
        }
        return entities;
    }

    /**
     * Convert the given list of references to the underlying internal entities pointed to by those references.
     *
     * @param <T> the expected entity type for each reference
     * @param refs the list of references.
     * @param type the class for the entity type
     * @return the list of referenced internal entities.
     * @throws InvalidReferenceException if for any reference, no entity exists or is not of the expected type.
     */
    protected <T> List<T> mapRequiredReferencesToEntities(Collection<CaArrayEntityReference> refs, Class<T> type)
            throws InvalidReferenceException {
        final List<T> entities = new ArrayList<T>(refs.size());
        mapRequiredReferencesToEntities(refs, entities, type);
        return entities;
    }

    /**
     * @return the entityHandlerRegistry
     */
    protected EntityHandlerRegistry getEntityHandlerRegistry() {
        return this.entityHandlerRegistry;
    }

    /**
     * Class to hold a registry of entity handlers, which can be looked up by class of external entity.
     *
     * @author dkokotov
     */
    protected static class EntityHandlerRegistry {
        private final Map<Class<?>, EntityHandler<? extends AbstractCaArrayEntity>> resolvers =
                new HashMap<Class<?>, EntityHandler<? extends AbstractCaArrayEntity>>();

        private <T extends AbstractCaArrayEntity, S> void
                addResolver(Class<T> externalClass, EntityHandler<T> resolver) {
            this.resolvers.put(externalClass, resolver);
        }

        /**
         * Get the handler for given external entity class.
         *
         * @param <T> type of the external entity class.
         * @param externalClass the class object for the external entity type.
         * @return the handler
         */
        @SuppressWarnings(UNCHECKED)
        public <T extends AbstractCaArrayEntity> EntityHandler<T> getResolver(Class<T> externalClass) {
            return (EntityHandler<T>) this.resolvers.get(externalClass);
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
    protected interface EntityHandler<T extends AbstractCaArrayEntity> {
        /**
         * Retrieve and return the internal instance given an object id.
         *
         * @param objectId the object identifier. the semantics of depend on the external entity type.
         *
         * @return the internal instance for given object id, or null, if one does not exist.
         */
        java.lang.Object resolve(String objectId);

        /**
         * return a list of entities of this handler's type, that match the given example criteria, subject to paging
         * parameters.
         *
         * @param criteria the example criteria
         * @param pagingParams the paging parameters
         * @return the matching entities, subject to paging parameters.
         */
        List<T> queryByExample(ExampleSearchCriteria<T> criteria, LimitOffset pagingParams);
    }

    /**
     * EntityHandler that for external entities that map to internal types extending from PersistentObject.
     *
     * @author dkokotov
     *
     * @param <T> the external entity type for this handler
     */
    private class PersistentObjectHandler<T extends AbstractCaArrayEntity> implements EntityHandler<T> {
        private final Class<? extends PersistentObject> internalClass;
        private final Order[] orders;

        /**
         * @param internalClass the class object for the internal type for this handler
         * @param orders the set of orders to use when doing queries.
         */
        PersistentObjectHandler(Class<? extends PersistentObject> internalClass, Order... orders) {
            this.internalClass = internalClass;
            this.orders = orders;
        }

        /**
         * {@inheritDoc} The objectId is expected to be the database id of the internal object.
         */
        @Override
        public java.lang.Object resolve(String objectId) {
            return ServiceLocatorFactory.getGenericDataService().getPersistentObject(this.internalClass,
                    Long.valueOf(objectId));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings(UNCHECKED)
        public List<T> queryByExample(ExampleSearchCriteria<T> criteria, LimitOffset pagingParams) {
            final List<? extends PersistentObject> results =
                    getDaoFactory().getSearchDao().queryEntityByExample(toInternalCriteria(criteria),
                            pagingParams.getLimit(), pagingParams.getOffset(), this.orders);
            return mapCollection(results, (Class<T>) criteria.getExample().getClass());
        }

        protected gov.nih.nci.caarray.domain.search.ExampleSearchCriteria<? extends PersistentObject>
                toInternalCriteria(ExampleSearchCriteria<T> criteria) {
            final gov.nih.nci.caarray.domain.search.ExampleSearchCriteria<? extends PersistentObject> intCriteria =
                    gov.nih.nci.caarray.domain.search.ExampleSearchCriteria.forEntity(toInternalExample(criteria
                            .getExample()));
            intCriteria.setMatchMode(getHibernateMatchMode(criteria.getMatchMode().name()));
            intCriteria.setExcludeNulls(criteria.isExcludeNulls());
            intCriteria.setExcludeZeroes(criteria.isExcludeZeroes());
            return intCriteria;
        }

        protected PersistentObject toInternalExample(T example) {
            return mapEntity(example, this.internalClass);
        }

        protected Order[] getOrders() {
            return this.orders;
        }
    }

    /**
     * EntityHandler that for external entities that map to internal types extending from AbstractBiomaterial.
     *
     * @author dkokotov
     */
    private final class BiomaterialHandler extends PersistentObjectHandler<Biomaterial> implements
            EntityHandler<Biomaterial> {
        /**
         * @param internalClass the class object for the internal type for this handler
         * @param orders the set of orders to use when doing queries.
         */
        BiomaterialHandler() {
            super(AbstractBioMaterial.class, Order.asc("name"));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings(UNCHECKED)
        public List<Biomaterial> queryByExample(ExampleSearchCriteria<Biomaterial> criteria, LimitOffset pagingParams) {
            final List<AbstractBioMaterial> results = new ArrayList<AbstractBioMaterial>();
            results.addAll((List<AbstractBioMaterial>) getDaoFactory().getSearchDao().queryEntityByExample(
                    toInternalCriteria(criteria), pagingParams.getLimit(), pagingParams.getOffset(), getOrders()));
            return mapCollection(results, Biomaterial.class);
        }

        @Override
        protected PersistentObject toInternalExample(Biomaterial bm) {
            final Class<? extends PersistentObject> klass =
                    bm.getType() == null ? AbstractBioMaterialWrapper.class : BIOMATERIAL_TYPE_TO_CLASS_MAP.get(bm
                            .getType());
            return mapEntity(bm, klass);
        }
    }

    /**
     * EntityHandler for FileTypes.
     *
     * @author dkokotov
     */
    private class FileTypeHandler implements EntityHandler<gov.nih.nci.caarray.external.v1_0.data.FileType> {
        /**
         * {@inheritDoc} The objectId is expected to be the name of a FileType constant.
         */
        @Override
        public java.lang.Object resolve(String objectId) {
            try {
                return BaseV1_0ExternalService.this.fileTypeRegistry.getTypeByName(objectId);
            } catch (final IllegalArgumentException e) {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<gov.nih.nci.caarray.external.v1_0.data.FileType> queryByExample(
                ExampleSearchCriteria<gov.nih.nci.caarray.external.v1_0.data.FileType> criteria,
                LimitOffset pagingParams) {
            final List<gov.nih.nci.caarray.external.v1_0.data.FileType> results =
                    new ArrayList<gov.nih.nci.caarray.external.v1_0.data.FileType>();
            for (final FileType type : BaseV1_0ExternalService.this.fileTypeRegistry.getAllTypes()) {
                if (nameMatches(type, criteria)) {
                    results.add(mapEntity(type, gov.nih.nci.caarray.external.v1_0.data.FileType.class));
                }
            }
            return results;
        }

        private boolean nameMatches(FileType type,
                ExampleSearchCriteria<gov.nih.nci.caarray.external.v1_0.data.FileType> criteria) {
            if (criteria.getExample().getName() == null) {
                // types never have null names
                return criteria.isExcludeNulls();
            }
            return criteria.getMatchMode().matches(criteria.getExample().getName(), type.getName());
        }
    }

    /**
     * Dummy wrapper class to enable one to create an example of AbstractBioMaterial.
     *
     * @author dkokotov
     */
    private static final class AbstractBioMaterialWrapper extends AbstractBioMaterial {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor) {
            // no-op
        }

        @Override
        protected void doAddDirectSuccessor(AbstractExperimentDesignNode successor) {
            // no-op
        }

        @Override
        public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors() {
            return null;
        }

        @Override
        public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors() {
            return null;
        }

        @Override
        public ExperimentDesignNodeType getNodeType() {
            return null;
        }
    }
}
