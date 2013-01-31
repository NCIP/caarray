//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.search.impl;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.ExternalBiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AbstractExperimentGraphNode;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationColumn;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationValueSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Characteristic;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.impl.BaseV1_0ExternalService;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author dkokotov
 */
@Stateless(name = "SearchServicev1_0")
@RemoteBinding(jndiBinding = SearchService.JNDI_NAME)
@PermitAll
@Interceptors({ AuthorizationInterceptor.class, HibernateSessionInterceptor.class })
@TransactionTimeout(SearchServiceBean.TIMEOUT_SECONDS)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SearchServiceBean extends BaseV1_0ExternalService implements SearchService {
    static final int TIMEOUT_SECONDS = 1800;

    static final int MAX_EXPERIMENT_RESULTS = 50;
    static final int MAX_BIOMATERIAL_RESULTS = 200;
    static final int MAX_HYBRIDIZATION_RESULTS = 200;
    static final int MAX_FILE_RESULTS = 200;        
    static final int MAX_EXAMPLE_RESULTS = 50;
    
    /**
     * {@inheritDoc}
     */
    public SearchResult<Experiment> searchForExperiments(ExperimentSearchCriteria criteria, LimitOffset pagingParams)
            throws InvalidReferenceException, UnsupportedCategoryException {
        List<Experiment> externalExperiments = new ArrayList<Experiment>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.project.Experiment> 
            actualParams = toInternalParams(defaultIfNull(pagingParams, MAX_EXPERIMENT_RESULTS), "title", false);
        gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria internalCriteria = toInternalCriteria(criteria);
        List<gov.nih.nci.caarray.domain.project.Experiment> experiments = ServiceLocatorFactory
                .getProjectManagementService().searchByCriteria(actualParams, internalCriteria);
        applySecurityPolicies(experiments);
        mapCollection(experiments, externalExperiments, Experiment.class);
        HibernateUtil.getCurrentSession().clear();
        return new SearchResult<Experiment>(externalExperiments, MAX_EXPERIMENT_RESULTS, actualParams.getIndex());
    }

    private gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria toInternalCriteria(
            ExperimentSearchCriteria criteria) throws InvalidReferenceException, UnsupportedCategoryException {
        gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria();
        intCriteria.setTitle(criteria.getTitle());
        intCriteria.setPublicIdentifier(criteria.getPublicIdentifier());
        intCriteria.setOrganism(getByReference(criteria.getOrganism(), edu.georgetown.pir.Organism.class));
        mapRequiredReferencesToEntities(criteria.getPrincipalInvestigators(),
                intCriteria.getPrincipalInvestigators(), gov.nih.nci.caarray.domain.contact.Person.class);
        intCriteria.setArrayProvider(getByReference(criteria.getArrayProvider(),
                gov.nih.nci.caarray.domain.contact.Organization.class));
        intCriteria.setAssayType(getByReference(criteria.getAssayType(),
                gov.nih.nci.caarray.domain.project.AssayType.class));
        for (AnnotationCriterion ac : criteria.getAnnotationCriterions()) {
            intCriteria.getAnnotationCriterions().add(toInternalCriterion(ac));
        }
        return intCriteria;
    }
    
    /**
     * {@inheritDoc}
     */
    public SearchResult<Experiment> searchForExperimentsByKeyword(KeywordSearchCriteria criteria,
            LimitOffset pagingParams) {
        List<Experiment> externalExperiments = new ArrayList<Experiment>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.project.Project> 
            actualParams = toInternalParams(defaultIfNull(pagingParams, MAX_EXPERIMENT_RESULTS), 
                    ProjectSortCriterion.TITLE, false);
        String keyword = criteria.getKeyword();
        List<Project> projects = ServiceLocatorFactory.getProjectManagementService().searchByCategory(actualParams,
                keyword, SearchCategory.values());
        for (Project p : projects) {
            gov.nih.nci.caarray.domain.project.Experiment exp = p.getExperiment();
            applySecurityPolicies(exp);
            externalExperiments.add(mapEntity(exp, Experiment.class));
        }
        HibernateUtil.getCurrentSession().clear();
        return new SearchResult<Experiment>(externalExperiments, MAX_EXPERIMENT_RESULTS, actualParams.getIndex());
    }
    
    /**
     * {@inheritDoc}
     */
    public SearchResult<Biomaterial> searchForBiomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria,
            LimitOffset pagingParams) {
        Set<BiomaterialType> types = criteria.getTypes();
        if (types.isEmpty()) {
            types = EnumSet.allOf(BiomaterialType.class);
        }
        Set<Class<? extends AbstractBioMaterial>> bmClasses = new HashSet<Class<? extends AbstractBioMaterial>>();
        for (BiomaterialType type : criteria.getTypes()) {
            bmClasses.add(BIOMATERIAL_TYPE_TO_CLASS_MAP.get(type));
        }

        LimitOffset actualParams = defaultIfNull(pagingParams, MAX_BIOMATERIAL_RESULTS);

        List<Biomaterial> externalBms = new ArrayList<Biomaterial>();        
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<AbstractBioMaterial> bmParams = toInternalParams(
                actualParams, new AdHocSortCriterion<AbstractBioMaterial>("this.name"), false);
        List<AbstractBioMaterial> bms = getDaoFactory().getSampleDao().searchByCategory(bmParams,
                criteria.getKeyword(), bmClasses, ExternalBiomaterialSearchCategory.values());
        applySecurityPolicies(bms);
        mapCollection(bms, externalBms, Biomaterial.class);
        return new SearchResult<Biomaterial>(externalBms, MAX_BIOMATERIAL_RESULTS, actualParams.getOffset());
    }

    /**
     * {@inheritDoc}
     */
    public List<Person> getAllPrincipalInvestigators() {
        List<Person> externalPersons = new ArrayList<Person>();
        List<gov.nih.nci.caarray.domain.contact.Person> persons = getDaoFactory().getContactDao()
                .getAllPrincipalInvestigators();
        mapCollection(persons, externalPersons, Person.class);
        return externalPersons;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getAllCharacteristicCategories(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException {
        List<Category> externalCategories = new ArrayList<Category>();
        gov.nih.nci.caarray.domain.project.Experiment experiment = getByReference(experimentRef,
                gov.nih.nci.caarray.domain.project.Experiment.class);
        List<gov.nih.nci.caarray.domain.vocabulary.Category> categories = getDaoFactory().getVocabularyDao()
                .searchForCharacteristicCategory(experiment, AbstractCharacteristic.class, null);
        // add in the standard categories
        for (ExperimentOntologyCategory cat : EnumSet.of(ExperimentOntologyCategory.ORGANISM_PART,
                ExperimentOntologyCategory.DISEASE_STATE, ExperimentOntologyCategory.CELL_TYPE,
                ExperimentOntologyCategory.MATERIAL_TYPE, ExperimentOntologyCategory.LABEL_COMPOUND,
                ExperimentOntologyCategory.EXTERNAL_ID)) {
            categories.add(VocabularyUtils.getCategory(cat)); 
        }
        mapCollection(categories, externalCategories, Category.class);
        return externalCategories;
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTermsForCategory(CaArrayEntityReference categoryRef, String valuePrefix)
            throws InvalidReferenceException {
        if (categoryRef == null) {
            throw new InvalidReferenceException(null);
        }
        gov.nih.nci.caarray.domain.vocabulary.Category category = getRequiredByExternalId(categoryRef.getId(),
                gov.nih.nci.caarray.domain.vocabulary.Category.class);
        Set<gov.nih.nci.caarray.domain.vocabulary.Term> terms = getDaoFactory().getVocabularyDao().getTermsRecursive(
                category, valuePrefix);
        List<Term> externalTerms = new ArrayList<Term>(terms.size());
        mapCollection(terms, externalTerms, Term.class);
        return externalTerms;
    }

    /**
     * {@inheritDoc}
     */
    public SearchResult<File> searchForFiles(FileSearchCriteria criteria, LimitOffset pagingParams)
            throws InvalidReferenceException {
        List<File> externalFiles = new ArrayList<File>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<CaArrayFile> actualParams = 
            toInternalParams(defaultIfNull(pagingParams, MAX_FILE_RESULTS), "name", false);
        gov.nih.nci.caarray.domain.search.FileSearchCriteria internalCriteria = toInternalCriteria(criteria);
        List<CaArrayFile> files = getDaoFactory().getFileDao().searchFiles(actualParams,
                internalCriteria);
        mapCollection(files, externalFiles, File.class);
        HibernateUtil.getCurrentSession().clear();
        return new SearchResult<File>(externalFiles, MAX_FILE_RESULTS, actualParams.getIndex());
    };

    private gov.nih.nci.caarray.domain.search.FileSearchCriteria toInternalCriteria(FileSearchCriteria criteria)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.search.FileSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.FileSearchCriteria();
        intCriteria.setExtension(criteria.getExtension());
        for (FileCategory category : criteria.getCategories()) {
            intCriteria.getCategories().add(gov.nih.nci.caarray.domain.file.FileCategory.valueOf(category.name()));
        }
        intCriteria.setExperiment(getByReference(criteria.getExperiment(),
                gov.nih.nci.caarray.domain.project.Experiment.class));
        mapRequiredReferencesToEntities(criteria.getTypes(), intCriteria.getTypes(),
                FileType.class);
        mapRequiredReferencesToEntities(criteria.getExperimentGraphNodes(), intCriteria.getExperimentNodes(),
                AbstractExperimentDesignNode.class);
        return intCriteria;
    }

    private LimitOffset defaultIfNull(LimitOffset params, int maxServiceResults) {
        int firstResult = params != null ? params.getOffset() : 0;
        int maxResults = params != null ? params.getLimit() : -1;
        
        // if either of the two numbers is < 0 (meaning no limit), then taking the max will give us the more 
        // restrictive limit, otherwise taking the min will
        if (maxServiceResults < 0 || maxResults < 0) {
            maxResults = Math.max(maxServiceResults, maxResults);
        } else {
            maxResults = Math.min(maxServiceResults, maxResults);
        }
        return new LimitOffset(maxResults, firstResult);
    }

    /**
     * {@inheritDoc}
     */    
    @SuppressWarnings("unchecked")
    public <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(ExampleSearchCriteria<T> criteria,
            LimitOffset pagingParams) throws InvalidInputException {
        if (criteria == null) {
            throw new InvalidInputException("criteria cannot be null");
        }
        T example = criteria.getExample();
        if (example == null) {
            throw new InvalidInputException("example cannot be null");
        }
        LimitOffset actualParams = defaultIfNull(pagingParams, MAX_EXAMPLE_RESULTS);
        EntityHandler<T> resolver = getEntityHandlerRegistry().getResolver((Class<T>) example.getClass());
        List<T> results = resolver.queryByExample(criteria, actualParams);
        return new SearchResult<T>(results, MAX_EXAMPLE_RESULTS, actualParams.getOffset());
    }
    
    /**
     * {@inheritDoc}
     */
    public SearchResult<Hybridization> searchForHybridizations(HybridizationSearchCriteria criteria,
            LimitOffset pagingParams)
            throws InvalidReferenceException {
        List<Hybridization> externalHybs = new ArrayList<Hybridization>();
        PageSortParams<gov.nih.nci.caarray.domain.hybridization.Hybridization> actualParams = 
            toInternalParams(defaultIfNull(pagingParams, MAX_HYBRIDIZATION_RESULTS), "name", false);
        List<gov.nih.nci.caarray.domain.hybridization.Hybridization> hybs = getDaoFactory().getHybridizationDao()
                .searchByCriteria(actualParams, toInternalCriteria(criteria));
        applySecurityPolicies(hybs);
        mapCollection(hybs, externalHybs, Hybridization.class);
        return new SearchResult<Hybridization>(externalHybs, MAX_HYBRIDIZATION_RESULTS, actualParams.getIndex());
    }
    
    private gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria toInternalCriteria(
            HybridizationSearchCriteria criteria)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria();
        intCriteria.setExperiment(getByReference(criteria.getExperiment(),
                gov.nih.nci.caarray.domain.project.Experiment.class));
        mapRequiredReferencesToEntities(criteria.getBiomaterials(), intCriteria.getBiomaterials(),
                AbstractBioMaterial.class);
        intCriteria.getNames().addAll(criteria.getNames());
        return intCriteria;
    }

    /**
     * {@inheritDoc}
     */
    public SearchResult<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, 
            LimitOffset pagingParams) throws InvalidReferenceException, UnsupportedCategoryException {
        LimitOffset actualParams = defaultIfNull(pagingParams, MAX_BIOMATERIAL_RESULTS);
        List<Biomaterial> externalSamples = new ArrayList<Biomaterial>();
        gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria intCriteria = toInternalCriteria(criteria);
        
        List<? extends AbstractBioMaterial> bms = getDaoFactory().getSampleDao().searchByCriteria(
                toInternalParams(actualParams, "name", false, AbstractBioMaterial.class), intCriteria);

        mapCollection(bms, externalSamples, Biomaterial.class);                                   

        return new SearchResult<Biomaterial>(externalSamples, MAX_BIOMATERIAL_RESULTS, actualParams.getOffset());
    }
    
    private gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria toInternalCriteria(
            BiomaterialSearchCriteria criteria) throws InvalidReferenceException, UnsupportedCategoryException {
        gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria();
        intCriteria.setExperiment(getByReference(criteria.getExperiment(),
                gov.nih.nci.caarray.domain.project.Experiment.class));
        intCriteria.getNames().addAll(criteria.getNames());
        intCriteria.getExternalIds().addAll(criteria.getExternalIds());
        for (AnnotationCriterion ac : criteria.getAnnotationCriterions()) {
            intCriteria.getAnnotationCriterions().add(toInternalCriterion(ac));
        }
        for (BiomaterialType type : criteria.getTypes()) {
            intCriteria.getBiomaterialClasses().add(BIOMATERIAL_TYPE_TO_CLASS_MAP.get(type));
        }
        return intCriteria;
    }
    
    private gov.nih.nci.caarray.domain.search.AnnotationCriterion toInternalCriterion(AnnotationCriterion ac)
            throws InvalidReferenceException, UnsupportedCategoryException {
        gov.nih.nci.caarray.domain.search.AnnotationCriterion internalCrit = 
            new gov.nih.nci.caarray.domain.search.AnnotationCriterion();
        internalCrit
                .setCategory(getByReference(ac.getCategory(), gov.nih.nci.caarray.domain.vocabulary.Category.class));
        if (!isSupportedCategory(internalCrit.getCategory())) {
            throw new UnsupportedCategoryException(ac.getCategory());
        }
        internalCrit.setValue(ac.getValue());
        return internalCrit;
    }
    
    private boolean isSupportedCategory(gov.nih.nci.caarray.domain.vocabulary.Category category) {
        return Arrays.asList(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName(),
                ExperimentOntologyCategory.CELL_TYPE.getCategoryName(),
                ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName(),
                ExperimentOntologyCategory.ORGANISM_PART.getCategoryName()).contains(category.getName());
    }

    /**
     * {@inheritDoc}
     */
    public List<QuantitationType> searchForQuantitationTypes(QuantitationTypeSearchCriteria criteria)
            throws InvalidInputException {
        List<QuantitationType> externalTypes = new ArrayList<QuantitationType>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.data.QuantitationType> 
            actualParams = toInternalParams(defaultIfNull(null, -1), "name", false);
        gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria internalCriteria = 
            toInternalCriteria(criteria);
        List<gov.nih.nci.caarray.domain.data.QuantitationType> types = getDaoFactory().getArrayDao()
                .searchForQuantitationTypes(actualParams, internalCriteria);
        mapCollection(types, externalTypes, QuantitationType.class);
        HibernateUtil.getCurrentSession().clear();
        return externalTypes;  
    }

    private gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria toInternalCriteria(
            QuantitationTypeSearchCriteria criteria) throws InvalidInputException {
        if (criteria.getHybridization() == null) {
            throw new InvalidInputException("hybridization must be set");
        }
        gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria();
        
        intCriteria.setHybridization((gov.nih.nci.caarray.domain.hybridization.Hybridization) getByExternalId(criteria
                .getHybridization().getId()));
        
        for (CaArrayEntityReference arrayDataTypeRef : criteria.getArrayDataTypes()) {
            gov.nih.nci.caarray.domain.data.ArrayDataType type =
                    (gov.nih.nci.caarray.domain.data.ArrayDataType) getByExternalId(arrayDataTypeRef.getId());
            if (type == null) {
                throw new NoEntityMatchingReferenceException(arrayDataTypeRef);
            }
            intCriteria.getArrayDataTypes().add(type);
        }
        
        for (CaArrayEntityReference fileTypeRef : criteria.getFileTypes()) {
            String fileTypeName = new LSID(fileTypeRef.getId()).getObjectId();            
            intCriteria.getFileTypes().add(gov.nih.nci.caarray.domain.file.FileType.valueOf(fileTypeName));
        }
        
        for (FileCategory category : criteria.getFileCategories()) {
            intCriteria.getFileCategories().add(gov.nih.nci.caarray.domain.file.FileCategory.valueOf(category.name()));
        }
        
        return intCriteria;
    }
 
    /**
     * {@inheritDoc}
     */
    public AnnotationSet getAnnotationSet(AnnotationSetRequest request) throws InvalidReferenceException {
        AnnotationSet set = new AnnotationSet();
        
        List<gov.nih.nci.caarray.domain.vocabulary.Category> categories = mapRequiredReferencesToEntities(request
                .getCategories(), gov.nih.nci.caarray.domain.vocabulary.Category.class);
        List<AbstractExperimentDesignNode> nodes = mapRequiredReferencesToEntities(request.getExperimentGraphNodes(),
                AbstractExperimentDesignNode.class);

        mapCollection(categories, set.getCategories(), Category.class);
        for (AbstractExperimentDesignNode node : nodes) {
            set.getColumns().add(getAnnotations(node, categories));
        }

        return set;        
    }
    
    private <T extends AbstractExperimentGraphNode> AnnotationColumn getAnnotations(AbstractExperimentDesignNode node,
            List<gov.nih.nci.caarray.domain.vocabulary.Category> categories) {
        AnnotationColumn ac = new AnnotationColumn();
        Class<? extends AbstractExperimentGraphNode> externalClass = 
            node instanceof gov.nih.nci.caarray.domain.hybridization.Hybridization ? Hybridization.class
                : Biomaterial.class;
        ac.setNode(mapEntity(node, externalClass));
        for (gov.nih.nci.caarray.domain.vocabulary.Category cat : categories) {
            Set<AbstractCharacteristic> chars = node.getCharacteristicsRecursively(cat);
            AnnotationValueSet valueSet = new AnnotationValueSet();
            valueSet.setCategory(mapEntity(cat, Category.class));
            for (AbstractCharacteristic characteristic : chars) {
                Characteristic externalChar = mapEntity(characteristic, Characteristic.class);
                valueSet.getValues().add(externalChar.getValue());
            }
            ac.getValueSets().add(valueSet);
        }
        return ac;
    }
}
