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
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleJoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SourceJoinableSortCriterion;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchField;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchField;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.PagingParams;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.BaseV1_0ExternalService;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.Order;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
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
@SuppressWarnings("PMD") // temporary, as this is experimental
public class SearchServiceBean extends BaseV1_0ExternalService implements SearchService {
    static final int TIMEOUT_SECONDS = 1800;
    
    private static final Map<String, SearchCategory> EXPERIMENT_SEARCH_CATEGORY_MAP = 
        new HashMap<String, SearchCategory>();
    static {
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.ARRAY_DESIGN.getName(), SearchCategory.ARRAY_DESIGN);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.ARRAY_PROVIDER.getName(),
                SearchCategory.ARRAY_PROVIDER);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.DISEASE_STATE.getName(), SearchCategory.DISEASE_STATE);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.ORGANISM.getName(), SearchCategory.ORGANISM);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.PUBLIC_ID.getName(), SearchCategory.EXPERIMENT_ID);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.SAMPLE_NAME.getName(), SearchCategory.SAMPLE);
        EXPERIMENT_SEARCH_CATEGORY_MAP.put(ExperimentSearchField.TITLE.getName(), SearchCategory.EXPERIMENT_TITLE);
    }
    
    private static final Map<String, SearchSampleCategory> SAMPLE_SEARCH_CATEGORY_MAP = 
        new HashMap<String, SearchSampleCategory>();
    static {
        SAMPLE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.NAME.getName(), SearchSampleCategory.SAMPLE_NAME);
        SAMPLE_SEARCH_CATEGORY_MAP
                .put(BiomaterialSearchField.EXTERNAL_ID.getName(), SearchSampleCategory.SAMPLE_EXTERNAL_ID);
        SAMPLE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.DISEASE_STATE.getName(),
                SearchSampleCategory.SAMPLE_DISEASE_STATE);
        SAMPLE_SEARCH_CATEGORY_MAP
                .put(BiomaterialSearchField.TISSUE_SITE.getName(), SearchSampleCategory.SAMPLE_TISSUE_SITE);
        SAMPLE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.ORGANISM.getName(), SearchSampleCategory.SAMPLE_ORGANISM);
    }

    private static final Map<String, SearchSourceCategory> SOURCE_SEARCH_CATEGORY_MAP = 
        new HashMap<String, SearchSourceCategory>();
    static {
        SOURCE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.NAME.getName(), null);
        SOURCE_SEARCH_CATEGORY_MAP
                .put(BiomaterialSearchField.EXTERNAL_ID.getName(), null);
        SOURCE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.DISEASE_STATE.getName(),
                SearchSourceCategory.SAMPLE_DISEASE_STATE);
        SOURCE_SEARCH_CATEGORY_MAP
                .put(BiomaterialSearchField.TISSUE_SITE.getName(), SearchSourceCategory.SAMPLE_TISSUE_SITE);
        SOURCE_SEARCH_CATEGORY_MAP.put(BiomaterialSearchField.ORGANISM.getName(), SearchSourceCategory.SAMPLE_ORGANISM);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getAllArrayDesigns(PagingParams pagingParams) {
        List<ArrayDesign> externalDesigns = new ArrayList<ArrayDesign>();
        try {
            PagingParams actualParams = defaultIfNull(pagingParams);
            List<gov.nih.nci.caarray.domain.array.ArrayDesign> designs = getDataService().retrieveAll(
                    gov.nih.nci.caarray.domain.array.ArrayDesign.class, actualParams.getMaxResults(),
                    actualParams.getFirstResult(), Order.asc("name"));
            mapCollection(designs, externalDesigns, ArrayDesign.class);
        } catch (IllegalAccessException e) {
            // log it
        } catch (InstantiationException e) {
            // log
        }
        return externalDesigns;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> getAllOrganisms(PagingParams pagingParams) {
        List<Organism> externalOrganisms = new ArrayList<Organism>();
        try {
            PagingParams actualParams = defaultIfNull(pagingParams);
            List<edu.georgetown.pir.Organism> organisms = getDataService().retrieveAll(
                    edu.georgetown.pir.Organism.class, actualParams.getMaxResults(), actualParams.getFirstResult(),
                    Order.asc("scientificName"));
            mapCollection(organisms, externalOrganisms, Organism.class);
        } catch (IllegalAccessException e) {
            // log it
        } catch (InstantiationException e) {
            // log
        }
        return externalOrganisms;
    }

    /**
     * {@inheritDoc}
     */
    public List<Experiment> searchForExperiments(ExperimentSearchCriteria criteria, PagingParams pagingParams) {
        List<Experiment> externalExperiments = new ArrayList<Experiment>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.project.Experiment> 
            actualParams = toInternalParams(defaultIfNull(pagingParams), "title", false);
        gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria internalCriteria = toInternalCriteria(criteria);
        List<gov.nih.nci.caarray.domain.project.Experiment> experiments = getProjectManagementService()
                .searchByCriteria(actualParams, internalCriteria);
        applySecurityPolicies(experiments);
        mapCollection(experiments, externalExperiments, Experiment.class);
        HibernateUtil.getCurrentSession().clear();
        return externalExperiments;
    }

    private gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria toInternalCriteria(
            ExperimentSearchCriteria criteria) {
        gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria();
        intCriteria.setAnd(criteria.isAnd());
        intCriteria.setTitle(criteria.getTitle());
        if (criteria.getOrganisms() != null) {
            for (CaArrayEntityReference orgRef : criteria.getOrganisms()) {
                intCriteria.getOrganisms().add((edu.georgetown.pir.Organism) getByLsid(orgRef.getLsid()));
            }
        }
        if (criteria.getPrincipalInvestigator() != null) {
            intCriteria.setPrincipalInvestigator((gov.nih.nci.caarray.domain.contact.Person) getByLsid(criteria
                    .getPrincipalInvestigator().getLsid()));
        }
        if (criteria.getArrayProvider() != null) {
            Organization example = new Organization();
            example.setName(criteria.getArrayProvider().getName());
            Organization provider = CaArrayUtils.uniqueResult(getDaoFactory().getSearchDao().query(example));
            intCriteria.setArrayProvider(provider);
        }
        if (criteria.getAssayType() != null) {
            AssayType at = AssayType.getByValue(criteria.getAssayType().getName());
            intCriteria.setAssayType(at);
        }
        // need to handle the rest of the criteria
        return intCriteria;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Experiment> searchForExperimentsByKeyword(ExperimentKeywordSearchCriteria criteria, 
            PagingParams pagingParams) {
        List<Experiment> externalExperiments = new ArrayList<Experiment>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.project.Project> 
            actualParams = toInternalParams(defaultIfNull(pagingParams), ProjectSortCriterion.TITLE, false);
        String keyword = criteria.getKeyword();
        List<SearchCategory> categories = new ArrayList<SearchCategory>();
        for (ExperimentSearchField field : criteria.getFields()) {
            categories.add(EXPERIMENT_SEARCH_CATEGORY_MAP.get(field.getName()));
        }
        List<Project> projects = getProjectManagementService()
                .searchByCategory(actualParams, keyword, categories.toArray(new SearchCategory[categories.size()]));
        for (Project p : projects) {
            gov.nih.nci.caarray.domain.project.Experiment exp = p.getExperiment();
            applySecurityPolicies(exp);
            externalExperiments.add(mapEntity(exp, Experiment.class));
        }
        HibernateUtil.getCurrentSession().clear();
        return externalExperiments;        
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Biomaterial> searchForBiomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria, 
            PagingParams pagingParams) {
        List<Biomaterial> externalSamples = new ArrayList<Biomaterial>();
        
        Set<BiomaterialType> types = criteria.getTypes();
        if (types.isEmpty()) {
            types = EnumSet.allOf(BiomaterialType.class);
        }
        
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.sample.Sample> 
            sampleParams = toInternalParams(defaultIfNull(pagingParams), SampleJoinableSortCriterion.NAME, false);
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.sample.Source> 
            sourceParams = toInternalParams(defaultIfNull(pagingParams), SourceJoinableSortCriterion.NAME, false);
        String keyword = criteria.getKeyword();
        
        List<SearchSampleCategory> sampleCategories = new ArrayList<SearchSampleCategory>();
        List<SearchSourceCategory> sourceCategories = new ArrayList<SearchSourceCategory>();
        for (BiomaterialSearchField field : criteria.getFields()) {
            if (SAMPLE_SEARCH_CATEGORY_MAP.get(field.getName()) != null) {
                sampleCategories.add(SAMPLE_SEARCH_CATEGORY_MAP.get(field.getName()));                
            }
            if (SOURCE_SEARCH_CATEGORY_MAP.get(field.getName()) != null) {
                sourceCategories.add(SOURCE_SEARCH_CATEGORY_MAP.get(field.getName()));
            }
        }

        if (!sampleCategories.isEmpty() && types.contains(BiomaterialType.SAMPLE)) {
            List<gov.nih.nci.caarray.domain.sample.Sample> samples = getProjectManagementService().searchByCategory(
                    sampleParams, keyword, sampleCategories.toArray(new SearchSampleCategory[sampleCategories.size()]));
            mapCollection(samples, externalSamples, Biomaterial.class);            
        }
        if (!sourceCategories.isEmpty() && types.contains(BiomaterialType.SOURCE)) {
            List<gov.nih.nci.caarray.domain.sample.Source> sources = getProjectManagementService().searchByCategory(
                    sourceParams, keyword, sourceCategories.toArray(new SearchSourceCategory[sourceCategories.size()]));
            mapCollection(sources, externalSamples, Biomaterial.class);            
        }
        return externalSamples;                
    }


    /**
     * {@inheritDoc}
     */
    public List<FileType> getAllFileTypes(PagingParams pagingParams) {
        List<FileType> externalTypes = new ArrayList<FileType>();
        mapCollection(Arrays.asList(gov.nih.nci.caarray.domain.file.FileType.values()), externalTypes, FileType.class);
        return externalTypes;
    }

    /**
     * {@inheritDoc}
     */
    public List<Person> getAllPrincipalInvestigators(PagingParams pagingParams) {
        List<Person> externalPersons = new ArrayList<Person>();
        List<gov.nih.nci.caarray.domain.contact.Person> persons = getProjectManagementService()
                .getAllPrincipalInvestigators();
        mapCollection(persons, externalPersons, Person.class);
        return externalPersons;
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayProvider> getAllProviders(PagingParams pagingParams) {
        List<ArrayProvider> externalProviders = new ArrayList<ArrayProvider>();
        List<Organization> providers = getArrayDesignService().getAllProviders();
        mapCollection(providers, externalProviders, ArrayProvider.class);
        return externalProviders;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public AbstractCaArrayEntity getByReference(CaArrayEntityReference reference) {
        Class<? extends AbstractCaArrayEntity> entityClass = 
            (Class<? extends AbstractCaArrayEntity>) getClassFromLsid(reference.getLsid());
        PersistentObject entity = getByLsid(reference.getLsid());
        AbstractCaArrayEntity externalEntity = mapEntity(entity, entityClass);
        return externalEntity;
    }

    /**
     * {@inheritDoc}
     */
    public List<AbstractCaArrayEntity> getByReferences(List<CaArrayEntityReference> references) {
        List<AbstractCaArrayEntity> results = new ArrayList<AbstractCaArrayEntity>();
        for (CaArrayEntityReference reference : references) {
            AbstractCaArrayEntity entity = getByReference(reference);
            results.add(entity);
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    public List<DataFile> searchForFiles(FileSearchCriteria criteria, PagingParams pagingParams) {
        List<DataFile> externalFiles = new ArrayList<DataFile>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<CaArrayFile> actualParams = 
            toInternalParams(defaultIfNull(pagingParams), "name", false);
        gov.nih.nci.caarray.domain.search.FileSearchCriteria internalCriteria = toInternalCriteria(criteria);
        List<CaArrayFile> files = getProjectManagementService().searchFiles(actualParams, internalCriteria);
        mapCollection(files, externalFiles, DataFile.class);
        HibernateUtil.getCurrentSession().clear();
        return externalFiles;
    };

    private gov.nih.nci.caarray.domain.search.FileSearchCriteria toInternalCriteria(FileSearchCriteria criteria) {
        gov.nih.nci.caarray.domain.search.FileSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.FileSearchCriteria();
        intCriteria.setAnd(criteria.isAnd());
        intCriteria.setExtension(criteria.getExtension());
        Set<FileTypeCategory> categories = new HashSet<FileTypeCategory>(criteria.getCategories());
        if (categories.isEmpty()) {
            categories.addAll(EnumSet.allOf(FileTypeCategory.class));
        }
        intCriteria.setIncludeRaw(categories.contains(FileTypeCategory.RAW));
        intCriteria.setIncludeDerived(categories.contains(FileTypeCategory.DERIVED));
        intCriteria.setIncludeSupplemental(categories.contains(FileTypeCategory.SUPPLEMENTAL));
        if (criteria.getExperiment() != null) {
            intCriteria.setExperiment((gov.nih.nci.caarray.domain.project.Experiment) getByLsid(criteria
                    .getExperiment().getLsid()));
        }
        for (CaArrayEntityReference typeRef : criteria.getTypes()) {
            String typeName = new LSID(typeRef.getLsid()).getObjectId();            
            intCriteria.getTypes().add(gov.nih.nci.caarray.domain.file.FileType.valueOf(typeName));
        }
        return intCriteria;
    }

    private PagingParams defaultIfNull(PagingParams params) {
        return params != null ? params : new PagingParams(-1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public java.util.List<?> search(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery, PagingParams params) {
        Object o = cqlQuery.getTarget();
        QueryModifier qm = cqlQuery.getQueryModifier();
        try {
            Class<? extends AbstractCaArrayEntity> entityClass = (Class<? extends AbstractCaArrayEntity>) Class
                    .forName(o.getName());
            Class<? extends PersistentObject> internalEntityClass = CLASS_MAP.get(entityClass);
            if (qm != null && qm.isCountOnly()) {
                List<? extends PersistentObject> results = getDataService().retrieveAll(internalEntityClass);
                return Collections.singletonList(Integer.valueOf(results.size()));
            } else {
                PagingParams actualParams = defaultIfNull(params);
                List<? extends PersistentObject> results = getDataService().retrieveAll(internalEntityClass,
                        actualParams.getMaxResults(), actualParams.getFirstResult(), Order.asc("id"));
                List<AbstractCaArrayEntity> externalResults = new ArrayList<AbstractCaArrayEntity>();
                mapCollection(results, externalResults, entityClass);
                if (qm != null) {
                    if (!ArrayUtils.isEmpty(qm.getAttributeNames())) {                        
                        String[] attrNames = qm.getAttributeNames();
                        List<java.lang.Object[]> attributeResults = new ArrayList<java.lang.Object[]>();
                        for (AbstractCaArrayEntity externalResult : externalResults) {
                            java.lang.Object[] attributeResult = new java.lang.Object[attrNames.length];
                            for (int i = 0; i < attrNames.length; i++) {
                                attributeResult[i] = PropertyUtils.getProperty(externalResult, attrNames[i]);
                            }
                            attributeResults.add(attributeResult);
                        }
                        return attributeResults;
                    } else if (qm.getDistinctAttribute() != null) {
                        List<java.lang.Object> attributeResults = new ArrayList<java.lang.Object>();
                        for (AbstractCaArrayEntity externalResult : externalResults) {
                            java.lang.Object attributeResult = PropertyUtils.getProperty(externalResult, qm
                                    .getDistinctAttribute());
                            if (!attributeResults.contains(attributeResult)) {
                                attributeResults.add(attributeResult);                                
                            }
                        }
                        return attributeResults;                        
                    }
                }
                return externalResults;
            }
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        } catch (InstantiationException e) {
            return Collections.emptyList();
        } catch (NoSuchMethodException e) {
            return Collections.emptyList();
        } catch (InvocationTargetException e) {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Hybridization> searchForHybridizations(HybridizationSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.project.Experiment e = getByLsid(criteria.getExperiment().getLsid(),
                gov.nih.nci.caarray.domain.project.Experiment.class);
        List<Hybridization> externalHybs = new ArrayList<Hybridization>();
        PageSortParams<gov.nih.nci.caarray.domain.hybridization.Hybridization> actualParams = 
            toInternalParams(defaultIfNull(pagingParams), "name", false);
        List<gov.nih.nci.caarray.domain.hybridization.Hybridization> hybs = getDataService().pageCollection(
                e.getHybridizations(), actualParams);
        mapCollection(hybs, externalHybs, Hybridization.class);
        return externalHybs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.project.Experiment e = getByLsid(criteria.getExperiment().getLsid(),
                gov.nih.nci.caarray.domain.project.Experiment.class);
        List<Biomaterial> externalSamples = new ArrayList<Biomaterial>();
        Set<BiomaterialType> types = criteria.getTypes();
        if (types.isEmpty()) {
            types = EnumSet.allOf(BiomaterialType.class);
        }
        if (types.contains(BiomaterialType.SOURCE)) {
            List<gov.nih.nci.caarray.domain.sample.Source> sources = getDataService().pageCollection(
                    e.getSources(), toInternalParams(defaultIfNull(pagingParams), "name", false, Source.class));
            mapCollection(sources, externalSamples, Biomaterial.class);            
        }
        if (types.contains(BiomaterialType.SAMPLE)) {
            List<gov.nih.nci.caarray.domain.sample.Sample> samples = getDataService().pageCollection(e.getSamples(),
                    toInternalParams(defaultIfNull(pagingParams), "name", false, Sample.class));
            mapCollection(samples, externalSamples, Biomaterial.class);
        }
        if (types.contains(BiomaterialType.EXTRACT)) {
            List<gov.nih.nci.caarray.domain.sample.Extract> extracts = getDataService().pageCollection(e.getExtracts(),
                    toInternalParams(defaultIfNull(pagingParams), "name", false, Extract.class));
            mapCollection(extracts, externalSamples, Biomaterial.class);
        }
        if (types.contains(BiomaterialType.LABELED_EXTRACT)) {
            List<gov.nih.nci.caarray.domain.sample.LabeledExtract> labeledExtracts = getDataService().pageCollection(
                    e.getLabeledExtracts(),
                    toInternalParams(defaultIfNull(pagingParams), "name", false, LabeledExtract.class));
            mapCollection(labeledExtracts, externalSamples, Biomaterial.class);
        }
        return externalSamples;
    }
}
