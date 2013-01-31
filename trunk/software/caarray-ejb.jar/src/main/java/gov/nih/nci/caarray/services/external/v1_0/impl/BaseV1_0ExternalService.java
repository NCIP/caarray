//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.impl;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
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
    
    /**
     * Constructor.
     */
    public BaseV1_0ExternalService() {
        entityHandlerRegistry.addResolver(Organism.class, new PersistentObjectHandler<Organism>(
                edu.georgetown.pir.Organism.class, Order.asc("scientificName")));
        entityHandlerRegistry.addResolver(File.class, new PersistentObjectHandler<File>(CaArrayFile.class,
                Order.asc("name")));
        entityHandlerRegistry.addResolver(QuantitationType.class, new PersistentObjectHandler<QuantitationType>(
                gov.nih.nci.caarray.domain.data.QuantitationType.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(Experiment.class, new PersistentObjectHandler<Experiment>(
                gov.nih.nci.caarray.domain.project.Experiment.class, Order.asc("publicIdentifier")));
        entityHandlerRegistry.addResolver(Person.class, new PersistentObjectHandler<Person>(
                gov.nih.nci.caarray.domain.contact.Person.class, Order.asc("lastName"), Order.asc("firstName")));
        entityHandlerRegistry.addResolver(Hybridization.class, new PersistentObjectHandler<Hybridization>(
                gov.nih.nci.caarray.domain.hybridization.Hybridization.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(Term.class, new PersistentObjectHandler<Term>(
                gov.nih.nci.caarray.domain.vocabulary.Term.class, Order.asc("value")));
        entityHandlerRegistry.addResolver(Category.class, new PersistentObjectHandler<Category>(
                gov.nih.nci.caarray.domain.vocabulary.Category.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(TermSource.class, new PersistentObjectHandler<TermSource>(
                gov.nih.nci.caarray.domain.vocabulary.TermSource.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(Factor.class, new PersistentObjectHandler<Factor>(
                gov.nih.nci.caarray.domain.project.Factor.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(ExperimentalContact.class, new PersistentObjectHandler<ExperimentalContact>(
                ExperimentContact.class));
        entityHandlerRegistry.addResolver(ArrayDesign.class, new PersistentObjectHandler<ArrayDesign>(
                gov.nih.nci.caarray.domain.array.ArrayDesign.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(Biomaterial.class, new BiomaterialHandler());
        entityHandlerRegistry.addResolver(ArrayDataType.class, new PersistentObjectHandler<ArrayDataType>(
                gov.nih.nci.caarray.domain.data.ArrayDataType.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(gov.nih.nci.caarray.external.v1_0.data.FileType.class, new FileTypeHandler());
        entityHandlerRegistry.addResolver(AssayType.class, new PersistentObjectHandler<AssayType>(
                gov.nih.nci.caarray.domain.project.AssayType.class, Order.asc("name")));
        entityHandlerRegistry.addResolver(ArrayProvider.class, new PersistentObjectHandler<ArrayProvider>(
                Organization.class, Order.asc("name")));
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
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> internalParams = 
            new com.fiveamsolutions.nci.commons.data.search.PageSortParams<T>(
                params.getLimit(), params.getOffset(), sortCriterion, desc);
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
    protected <T> com.fiveamsolutions.nci.commons.data.search.PageSortParams<T> toInternalParams(LimitOffset params,
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
     * Retrieve the entity in the internal domain model identified by the given external id. This is
     * expected to be implemented by version-specific subclasses.
     * 
     * @param externalId the external id     
     * @return the entity
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    protected java.lang.Object getByExternalId(String externalId) {        
        Class<? extends AbstractCaArrayEntity> entityClass = 
            (Class<? extends AbstractCaArrayEntity>) getClassFromExternalId(externalId);
        EntityHandler<? extends AbstractCaArrayEntity> resolver = entityHandlerRegistry.getResolver(entityClass); 
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
     * @throws InvalidReferenceException if no entity exists with given external id or the entity is not of the expected
     *             type.
     */
    protected <T> T getRequiredByExternalId(String externalId, Class<T> type) throws InvalidReferenceException {
        java.lang.Object o = getByExternalId(externalId);
        if (o == null) {
            throw new NoEntityMatchingReferenceException(new CaArrayEntityReference(externalId));
        }
        try {
            return type.cast(o);                    
        } catch (ClassCastException e) {
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
        for (CaArrayEntityReference ref : refs) {
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
        List<T> entities = new ArrayList<T>(refs.size());
        mapRequiredReferencesToEntities(refs, entities, type);
        return entities;
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
         * return a list of entities of this handler's type, that match the given example criteria, subject to paging
         * parameters.
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
         * {@inheritDoc}
         * The objectId is expected to be the database id of the internal object.
         */
        public java.lang.Object resolve(String objectId) {
            return ServiceLocatorFactory.getGenericDataService().getPersistentObject(internalClass,
                    Long.valueOf(objectId));
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings(UNCHECKED)
        public List<T> queryByExample(ExampleSearchCriteria<T> criteria, LimitOffset pagingParams) {
            List<? extends PersistentObject> results = getDaoFactory().getSearchDao().queryEntityByExample(
                    toInternalCriteria(criteria), pagingParams.getLimit(), pagingParams.getOffset(),
                    this.orders);
            return mapCollection(results, (Class<T>) criteria.getExample().getClass());
        }
        
        protected gov.nih.nci.caarray.domain.search.ExampleSearchCriteria<? extends PersistentObject> 
        toInternalCriteria(ExampleSearchCriteria<T> criteria) {
            gov.nih.nci.caarray.domain.search.ExampleSearchCriteria<? extends PersistentObject> intCriteria = 
                gov.nih.nci.caarray.domain.search.ExampleSearchCriteria
                    .forEntity(toInternalExample(criteria.getExample()));
            intCriteria.setMatchMode(getHibernateMatchMode(criteria.getMatchMode().name()));
            intCriteria.setExcludeNulls(criteria.isExcludeNulls());
            intCriteria.setExcludeZeroes(criteria.isExcludeZeroes());
            return intCriteria;
        }
        
        protected PersistentObject toInternalExample(T example) {
            return mapEntity(example, internalClass);
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
        @SuppressWarnings(UNCHECKED)
        public List<Biomaterial> queryByExample(ExampleSearchCriteria<Biomaterial> criteria, 
                LimitOffset pagingParams) {
            List<AbstractBioMaterial> results = new ArrayList<AbstractBioMaterial>();
            results.addAll((List<AbstractBioMaterial>) getDaoFactory().getSearchDao().queryEntityByExample(
                    toInternalCriteria(criteria), pagingParams.getLimit(), pagingParams.getOffset(), getOrders()));
            return mapCollection(results, Biomaterial.class);
        }
        
        @Override
        protected PersistentObject toInternalExample(Biomaterial bm) {
            Class<? extends PersistentObject> klass = bm.getType() == null ? AbstractBioMaterialWrapper.class
                    : BIOMATERIAL_TYPE_TO_CLASS_MAP.get(bm.getType()); 
            return mapEntity(bm, klass);
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
                return FileType.valueOf(objectId);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        public List<gov.nih.nci.caarray.external.v1_0.data.FileType> queryByExample(
                ExampleSearchCriteria<gov.nih.nci.caarray.external.v1_0.data.FileType> criteria,
                LimitOffset pagingParams) {
            List<gov.nih.nci.caarray.external.v1_0.data.FileType> results = 
                new ArrayList<gov.nih.nci.caarray.external.v1_0.data.FileType>();
            for (FileType type : FileType.values()) {
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
            return criteria.getMatchMode().matches(criteria.getExample().getName(), type.name());
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
