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
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
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
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.PagingParams;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.BaseV1_0ExternalService;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
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
    private static final Logger LOG = Logger.getLogger(SearchServiceBean.class);
    
    static final int TIMEOUT_SECONDS = 1800;
        
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
            LOG.error("Could not retrieve array designs", e);
            throw new IllegalStateException("Could not retrieve array designs", e);
        } catch (InstantiationException e) {
            LOG.error("Could not retrieve array designs", e);
            throw new IllegalStateException("Could not retrieve array designs", e);
        }
        return externalDesigns;
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDataType> getAllArrayDataTypes(PagingParams pagingParams) {
        List<ArrayDataType> externalTypes = new ArrayList<ArrayDataType>();
        try {
            PagingParams actualParams = defaultIfNull(pagingParams);
            List<gov.nih.nci.caarray.domain.data.ArrayDataType> types = getDataService().retrieveAll(
                    gov.nih.nci.caarray.domain.data.ArrayDataType.class, actualParams.getMaxResults(),
                    actualParams.getFirstResult(), Order.asc("name"));
            mapCollection(types, externalTypes, ArrayDataType.class);
        } catch (IllegalAccessException e) {
            LOG.error("Could not retrieve array data types", e);
            throw new IllegalStateException("Could not retrieve array data types", e);
        } catch (InstantiationException e) {
            LOG.error("Could not retrieve array data types", e);
            throw new IllegalStateException("Could not retrieve array data types", e);
        }
        return externalTypes;
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
            LOG.error("Could not retrieve organisms", e);
            throw new IllegalStateException("Could not retrieve organisms", e);
        } catch (InstantiationException e) {
            LOG.error("Could not retrieve organisms", e);
            throw new IllegalStateException("Could not retrieve organisms", e);
        }
        return externalOrganisms;
    }

    /**
     * {@inheritDoc}
     */
    public List<Experiment> searchForExperiments(ExperimentSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
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
            ExperimentSearchCriteria criteria) throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria();
        intCriteria.setTitle(criteria.getTitle());
        intCriteria.setPublicIdentifier(criteria.getPublicIdentifier());
        if (criteria.getOrganism() != null) {
            intCriteria
                    .setOrganism(getRequiredByLsid(criteria.getOrganism().getId(), edu.georgetown.pir.Organism.class));
        }
        if (criteria.getPrincipalInvestigator() != null) {
            intCriteria.setPrincipalInvestigator(getRequiredByLsid(criteria.getPrincipalInvestigator().getId(),
                    gov.nih.nci.caarray.domain.contact.Person.class));
        }
        if (criteria.getArrayProvider() != null) {
            Organization example = new Organization();
            example.setName(criteria.getArrayProvider().getName());
            Organization provider = CaArrayUtils.uniqueResult(getDaoFactory().getSearchDao().query(example));
            intCriteria.setArrayProvider(provider);
        }
        if (criteria.getAssayType() != null) {
            AssayType example = new AssayType(criteria.getAssayType().getName());
            AssayType assayType = CaArrayUtils.uniqueResult(getDaoFactory().getSearchDao().query(example));
            intCriteria.setAssayType(assayType);
        }
        // need to handle the rest of the criteria
        return intCriteria;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Experiment> searchForExperimentsByKeyword(KeywordSearchCriteria criteria, PagingParams pagingParams) {
        List<Experiment> externalExperiments = new ArrayList<Experiment>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.project.Project> 
            actualParams = toInternalParams(defaultIfNull(pagingParams), ProjectSortCriterion.TITLE, false);
        String keyword = criteria.getKeyword();
        List<Project> projects = getProjectManagementService().searchByCategory(actualParams, keyword,
                SearchCategory.values());
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
        
        if (types.contains(BiomaterialType.SAMPLE)) {
            List<gov.nih.nci.caarray.domain.sample.Sample> samples = getProjectManagementService().searchByCategory(
                    sampleParams, keyword, SearchSampleCategory.values());
            mapCollection(samples, externalSamples, Biomaterial.class);            
        }
        if (types.contains(BiomaterialType.SOURCE)) {
            List<gov.nih.nci.caarray.domain.sample.Source> sources = getProjectManagementService().searchByCategory(
                    sourceParams, keyword, SearchSourceCategory.values());
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
    public AbstractCaArrayEntity getByReference(CaArrayEntityReference reference)
            throws NoEntityMatchingReferenceException {
        Class<? extends AbstractCaArrayEntity> entityClass = 
            (Class<? extends AbstractCaArrayEntity>) getClassFromLsid(reference.getId());
        java.lang.Object entity = getByLsid(reference.getId());
        if (entity == null) {
            throw new NoEntityMatchingReferenceException(reference);
        }
        return mapEntity(entity, entityClass);
    }

    /**
     * {@inheritDoc}
     */
    public List<AbstractCaArrayEntity> getByReferences(List<CaArrayEntityReference> references)
            throws NoEntityMatchingReferenceException {
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
    public List<DataFile> searchForFiles(FileSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
        List<DataFile> externalFiles = new ArrayList<DataFile>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<CaArrayFile> actualParams = 
            toInternalParams(defaultIfNull(pagingParams), "name", false);
        gov.nih.nci.caarray.domain.search.FileSearchCriteria internalCriteria = toInternalCriteria(criteria);
        List<CaArrayFile> files = getProjectManagementService().searchFiles(actualParams, internalCriteria);
        mapCollection(files, externalFiles, DataFile.class);
        HibernateUtil.getCurrentSession().clear();
        return externalFiles;
    };

    private gov.nih.nci.caarray.domain.search.FileSearchCriteria toInternalCriteria(FileSearchCriteria criteria)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.search.FileSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.FileSearchCriteria();
        intCriteria.setExtension(criteria.getExtension());
        Set<FileTypeCategory> categories = new HashSet<FileTypeCategory>(criteria.getCategories());
        if (categories.isEmpty()) {
            categories.addAll(EnumSet.allOf(FileTypeCategory.class));
        }
        intCriteria.setIncludeRaw(categories.contains(FileTypeCategory.RAW));
        intCriteria.setIncludeDerived(categories.contains(FileTypeCategory.DERIVED));
        intCriteria.setIncludeSupplemental(categories.contains(FileTypeCategory.SUPPLEMENTAL));
        if (criteria.getExperiment() != null) {
            intCriteria.setExperiment(getRequiredByLsid(criteria.getExperiment().getId(),
                    gov.nih.nci.caarray.domain.project.Experiment.class));
        }
        for (CaArrayEntityReference typeRef : criteria.getTypes()) {
            intCriteria.getTypes().add(
                    getRequiredByLsid(typeRef.getId(), gov.nih.nci.caarray.domain.file.FileType.class));
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
    public java.util.List<?> searchByCQL(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery, PagingParams params) {
        Object o = cqlQuery.getTarget();
        QueryModifier qm = cqlQuery.getQueryModifier();
        try {
            Class<? extends AbstractCaArrayEntity> entityClass = (Class<? extends AbstractCaArrayEntity>) Class
                    .forName(o.getName());
            EntityHandler<? extends AbstractCaArrayEntity> resolver = getEntityHandlerRegistry().getResolver(
                    entityClass);
            if (qm != null && qm.isCountOnly()) {
                int count = resolver.countQueryByCQL(o);
                return Collections.singletonList(count);
            } else {
                List<? extends AbstractCaArrayEntity> results = resolver.queryByCQL(o, defaultIfNull(params));
                if (qm != null) {
                    if (!ArrayUtils.isEmpty(qm.getAttributeNames())) {                        
                        return toAttributeResults(results, qm.getAttributeNames());
                    } else if (qm.getDistinctAttribute() != null) {
                        return toDistinctAttributeResults(results, qm.getDistinctAttribute());
                    }
                }
                return results;
            }
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        } catch (ClassNotFoundException e) {
            return Collections.emptyList();
        } catch (NoSuchMethodException e) {
            return Collections.emptyList();
        } catch (InvocationTargetException e) {
            return Collections.emptyList();
        }
    }
    
    private List<java.lang.Object[]> toAttributeResults(List<? extends AbstractCaArrayEntity> results,
            String[] attrNames) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<java.lang.Object[]> attributeResults = new ArrayList<java.lang.Object[]>();
        for (AbstractCaArrayEntity externalResult : results) {
            java.lang.Object[] attributeResult = new java.lang.Object[attrNames.length];
            for (int i = 0; i < attrNames.length; i++) {
                attributeResult[i] = PropertyUtils.getProperty(externalResult, attrNames[i]);
            }
            attributeResults.add(attributeResult);
        }
        return attributeResults;
    }
    
    private List<java.lang.Object> toDistinctAttributeResults(List<? extends AbstractCaArrayEntity> results,
            String attrName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Set<java.lang.Object> attributeResults = new HashSet<java.lang.Object>();
        for (AbstractCaArrayEntity externalResult : results) {
            java.lang.Object attributeResult = PropertyUtils.getProperty(externalResult, attrName);
            if (!attributeResults.contains(attributeResult)) {
                attributeResults.add(attributeResult);                                
            }
        }
        return new ArrayList<java.lang.Object>(attributeResults);                                
    }

    /**
     * {@inheritDoc}
     */    
    @SuppressWarnings("unchecked")
    public <T extends AbstractCaArrayEntity> List<T> searchByExample(T example, PagingParams pagingParams) {
        EntityHandler<T> resolver = getEntityHandlerRegistry().getResolver((Class<T>) example.getClass());
        return resolver.queryByExample(example, defaultIfNull(pagingParams));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Hybridization> searchForHybridizations(HybridizationSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.project.Experiment e = getRequiredByLsid(criteria.getExperiment().getId(),
                gov.nih.nci.caarray.domain.project.Experiment.class);
        List<Hybridization> externalHybs = new ArrayList<Hybridization>();
        PageSortParams<gov.nih.nci.caarray.domain.hybridization.Hybridization> actualParams = 
            toInternalParams(defaultIfNull(pagingParams), "name", false);
        List<gov.nih.nci.caarray.domain.hybridization.Hybridization> hybs = getDataService().pageAndFilterCollection(
                e.getHybridizations(), "name", new LinkedList<String>(criteria.getNames()), actualParams);
        mapCollection(hybs, externalHybs, Hybridization.class);
        return externalHybs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException {
        gov.nih.nci.caarray.domain.project.Experiment e = getRequiredByLsid(criteria.getExperiment().getId(),
                gov.nih.nci.caarray.domain.project.Experiment.class);
        List<Biomaterial> externalSamples = new ArrayList<Biomaterial>();
        Set<BiomaterialType> types = criteria.getTypes();
        if (types.isEmpty()) {
            types = EnumSet.allOf(BiomaterialType.class);
        }
        
        if (types.contains(BiomaterialType.SOURCE)) {
            List<gov.nih.nci.caarray.domain.sample.Source> sources = pageAndFilterBiomaterials(e.getSources(), criteria
                    .getNames(), pagingParams, Source.class);
            mapCollection(sources, externalSamples, Biomaterial.class);            
        }
        if (types.contains(BiomaterialType.SAMPLE)) {
            List<gov.nih.nci.caarray.domain.sample.Sample> samples = pageAndFilterBiomaterials(e.getSamples(), criteria
                    .getNames(), pagingParams, Sample.class);
            mapCollection(samples, externalSamples, Biomaterial.class);
        }
        if (types.contains(BiomaterialType.EXTRACT)) {
            List<gov.nih.nci.caarray.domain.sample.Extract> extracts = pageAndFilterBiomaterials(e.getExtracts(),
                    criteria.getNames(), pagingParams, Extract.class);
            mapCollection(extracts, externalSamples, Biomaterial.class);
        }
        if (types.contains(BiomaterialType.LABELED_EXTRACT)) {
            List<gov.nih.nci.caarray.domain.sample.LabeledExtract> labeledExtracts = pageAndFilterBiomaterials(e
                    .getLabeledExtracts(), criteria.getNames(), pagingParams, LabeledExtract.class);
            mapCollection(labeledExtracts, externalSamples, Biomaterial.class);
        }
        return externalSamples;
    }
    
    private <T extends AbstractBioMaterial> List<T> pageAndFilterBiomaterials(Collection<T> biomaterials,
            Set<String> names, PagingParams pagingParams, Class<T> biomaterialClass) {
        return getDataService().pageAndFilterCollection(biomaterials, "name", new LinkedList<String>(names),
                toInternalParams(defaultIfNull(pagingParams), "name", false, biomaterialClass));
    }

    /**
     * {@inheritDoc}
     */
    public List<QuantitationType> searchForQuantitationTypes(QuantitationTypeSearchCriteria criteria,
            PagingParams pagingParams) {
        List<QuantitationType> externalTypes = new ArrayList<QuantitationType>();
        com.fiveamsolutions.nci.commons.data.search.PageSortParams<gov.nih.nci.caarray.domain.data.QuantitationType> 
            actualParams = toInternalParams(defaultIfNull(pagingParams), "name", false);
        gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria internalCriteria = 
            toInternalCriteria(criteria);
        List<gov.nih.nci.caarray.domain.data.QuantitationType> types = getDaoFactory().getArrayDao()
                .searchForQuantitationTypes(actualParams, internalCriteria);
        mapCollection(types, externalTypes, QuantitationType.class);
        HibernateUtil.getCurrentSession().clear();
        return externalTypes;  
    }

    private gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria toInternalCriteria(
            QuantitationTypeSearchCriteria criteria) {
        gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria intCriteria = 
            new gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria();
        
        intCriteria.setHybridization((gov.nih.nci.caarray.domain.hybridization.Hybridization) getByLsid(criteria
                .getHybridization().getId()));
        
        for (CaArrayEntityReference arrayDataTypeRef : criteria.getArrayDataTypes()) {
            intCriteria.getArrayDataTypes().add(
                    (gov.nih.nci.caarray.domain.data.ArrayDataType) getByLsid(arrayDataTypeRef.getId()));
        }
        
        for (CaArrayEntityReference fileTypeRef : criteria.getFileTypes()) {
            String fileTypeName = new LSID(fileTypeRef.getId()).getObjectId();            
            intCriteria.getFileTypes().add(gov.nih.nci.caarray.domain.file.FileType.valueOf(fileTypeName));
        }
        
        Set<FileTypeCategory> categories = new HashSet<FileTypeCategory>(criteria.getFileTypeCategories());
        if (categories.isEmpty()) {
            categories.addAll(EnumSet.allOf(FileTypeCategory.class));
        }
        intCriteria.setIncludeRaw(categories.contains(FileTypeCategory.RAW));
        intCriteria.setIncludeDerived(categories.contains(FileTypeCategory.DERIVED));
        
        return intCriteria;
    }
}
