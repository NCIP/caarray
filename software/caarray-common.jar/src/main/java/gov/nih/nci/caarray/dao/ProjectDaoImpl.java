//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.UnfilteredCallback;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.project</code> package.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
class ProjectDaoImpl extends AbstractCaArrayDaoImpl implements ProjectDao {
    private static final String UNCHECKED = "unchecked";
    private static final String EXP_ID_PARAM = "expId";
    private static final String SOURCES = "sources";
    private static final String[] BIOMATERIALS = {"sources", "samples", "extracts", "labeledExtracts" };
    private static final String TERM_FOR_EXPERIMENT_HSQL = "select distinct s.{0} from " + Experiment.class.getName()
            + " e left join e.{1} s where e.id = :" + EXP_ID_PARAM + " and s.{0} is not null";

    private final FileTypeRegistry typeRegistry;

    /**
     *
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     * @return
     */
    @Inject
    public ProjectDaoImpl(CaArrayHibernateHelper hibernateHelper, FileTypeRegistry typeRegistry) {
        super(hibernateHelper);
        this.typeRegistry = typeRegistry;
    }

    /**
     * Saves a project by first updating the lastUpdated field, and then saves the entity to persistent storage,
     * updating or inserting as necessary.
     *
     * @param persistentObject the entity to save
     */
    @Override
    public Long save(PersistentObject persistentObject) {
        if (persistentObject instanceof Project) {
            Project project = (Project) persistentObject;
            // ideally, the lastUpdated property should be updated by AutoPropertiesInterceptor,
            // but it's a lot harder to update the parent project when child objects are updated.
            project.setLastUpdated(new Date());
            manageLastDataModificationDate(project);
        }
        return super.save(persistentObject);
    }

    private void manageLastDataModificationDate(Project project) {
        if (isExperimentDataModified(project)) {
            project.getExperiment().setLastDataModificationDate(new Date());
        }
    }
    
    private boolean isExperimentDataModified(Project project) {
        if (project.getId() != null) {
            Session tempSession = getHibernateHelper().getSessionFactory().openSession();
            try {
                tempSession.enableFilter(Experiment.BIOMATERIAL_FILTER_NAME);
                Project prePersistPrj = (Project) tempSession.load(Project.class, project.getId());
                return samplesChanged(prePersistPrj, project);
            } finally {
                tempSession.close();
            }
        } else {
            return true;
        }
    }
    
    private boolean samplesChanged(Project proj1, Project proj2) {
        return proj1.getExperiment().getSamples().size() != proj2.getExperiment().getSamples().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Project getProjectByPublicId(String publicId) {
        final Criteria c = getCurrentSession().createCriteria(Project.class);
        c.createCriteria("experiment").add(Restrictions.eq("publicIdentifier", publicId).ignoreCase());
        return (Project) c.uniqueResult();
    }

    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Project> getProjectsForCurrentUser(PageSortParams<Project> pageSortParams) {
        final Query q = getProjectsForUserQuery(false, pageSortParams);
        q.setFirstResult(pageSortParams.getIndex());
        q.setMaxResults(pageSortParams.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getProjectsForOwner(User user) {
        final String q =
                "SELECT DISTINCT p FROM " + Project.class.getName() + " p left join fetch p.experiment e where p.id in"
                        + "(select pe.value from " + ProtectionElement.class.getName()
                        + " pe where pe.objectId = :objectId and pe.attribute = :attribute and "
                        + " pe.application = :application and :user in elements(pe.owners)) ";

        final Query query = getCurrentSession().createQuery(q);
        query.setString("objectId", Project.class.getName());
        query.setString("attribute", "id");
        query.setEntity("application", SecurityUtils.getApplication());
        query.setEntity("user", user);

        @SuppressWarnings("unchecked")
        final List<Project> projects = query.list();
        return projects; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getProjectCountForCurrentUser() {
        final Query q = getProjectsForUserQuery(true, null);
        return ((Number) q.uniqueResult()).intValue();
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private Query getProjectsForUserQuery(boolean count, PageSortParams<Project> pageSortParams) {
        final User user = CaArrayUsernameHolder.getCsmUser();
        @SuppressWarnings("deprecation")
        final SortCriterion<Project> sortCrit = pageSortParams != null ? pageSortParams.getSortCriterion() : null;
        final String ownerSubqueryStr =
                "(select pe.value from " + ProtectionElement.class.getName()
                        + " pe where pe.objectId = :objectId and pe.attribute = :attribute and "
                        + " pe.application = :application and :user in elements(pe.owners)) ";
        final String collabSubqueryStr =
                "(select ap.projectForGroupProfile.id from " + AccessProfile.class.getName()
                        + " ap join ap.group cg, Group g "
                        + " where ap.securityLevelInternal != :noneSecLevel and cg.groupId = g.groupId "
                        + " and :user in elements(g.users))";
        final String selectClause = count ? "SELECT COUNT(DISTINCT p)" : "SELECT DISTINCT p";
        final StringBuilder queryStr =
                new StringBuilder(selectClause).append(" from ").append(Project.class.getName()).append(" p ");
        if (!count) {
            queryStr.append(" left join fetch p.experiment e");
        }
        queryStr.append(" where (p.id in ").append(ownerSubqueryStr).append(" or p.id in ").append(collabSubqueryStr)
                .append(")");
        if (!count && sortCrit != null) {
            queryStr.append(" ORDER BY p.").append(sortCrit.getOrderField());
            if (pageSortParams.isDesc()) {
                queryStr.append(" desc");
            }
        }

        final Query query = getCurrentSession().createQuery(queryStr.toString());
        query.setString("objectId", Project.class.getName());
        query.setString("attribute", "id");
        query.setEntity("application", SecurityUtils.getApplication());
        query.setEntity("user", user);
        query.setParameter("noneSecLevel", SecurityLevel.NONE);
        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Project> searchByCategory(PageSortParams<Project> params, String keyword, SearchCategory... cats) {
        final Query q = getSearchQuery(false, params, keyword, cats);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchCount(String keyword, SearchCategory... categories) {
        final Query q = getSearchQuery(true, null, keyword, categories);
        return ((Number) q.uniqueResult()).intValue();
    }

    private Query getSearchQuery(boolean count, PageSortParams<Project> params, String keyword,
            SearchCategory... categories) {
        @SuppressWarnings("deprecation")
        final SortCriterion<Project> sortCrit = params != null ? params.getSortCriterion() : null;
        final StringBuffer sb = new StringBuffer();
        if (count) {
            sb.append("SELECT COUNT(DISTINCT p)");
        } else {
            sb.append("SELECT DISTINCT p");
        }
        sb.append(" FROM ").append(Project.class.getName()).append(" p");
        if (!count) {
            sb.append(" left join fetch p.experiment e left join fetch e.organism");
        }
        sb.append(getJoinClause(categories));
        sb.append(getWhereClause(categories));
        if (!count && sortCrit != null) {
            sb.append(" ORDER BY p.").append(sortCrit.getOrderField());
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }
        final Query q = getCurrentSession().createQuery(sb.toString());
        q.setString("keyword", "%" + StringUtils.defaultString(keyword) + "%");
        return q;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({UNCHECKED, "PMD" })
    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        final StringBuilder from =
                new StringBuilder("SELECT distinct e FROM ").append(Experiment.class.getName()).append(" e");
        final StringBuilder where = new StringBuilder("WHERE (1=1)");
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        if (criteria.getTitle() != null) {
            where.append(" AND upper(e.title) = upper(:title)");
            queryParams.put("title", criteria.getTitle());
        }
        if (criteria.getPublicIdentifier() != null) {
            where.append(" AND upper(e.publicIdentifier) = upper(:pid)");
            queryParams.put("pid", criteria.getPublicIdentifier());
        }
        if (criteria.getOrganism() != null) {
            from.append(" INNER JOIN e.organism o");
            where.append(" AND o.id = :organism_id");
            queryParams.put("organism_id", criteria.getOrganism().getId());
        }
        if (criteria.getAssayType() != null) {
            from.append(" INNER JOIN e.assayTypes at");
            where.append(" AND at.id = :assay_type_id");
            queryParams.put("assay_type_id", criteria.getAssayType().getId());
        }
        if (criteria.getArrayProvider() != null) {
            from.append(" INNER JOIN e.manufacturer m");
            where.append(" AND m.id = :array_provider_id");
            queryParams.put("array_provider_id", criteria.getArrayProvider().getId());
        }
        if (!criteria.getPrincipalInvestigators().isEmpty()) {
            from.append(" INNER JOIN e.experimentContacts ec INNER JOIN ec.roles r INNER JOIN ec.contact p");
            where.append(" AND r.value = :pi_role AND p in (:pis)");
            queryParams.put("pi_role", ExperimentContact.PI_ROLE);
            queryParams.put("pis", criteria.getPrincipalInvestigators());
        }
        if (!criteria.getAnnotationCriterions().isEmpty()) {
            addAnnotationCriteria(criteria, where, from, queryParams);
        }

        final Query q =
                getCurrentSession().createQuery(
                        from.append(" ").append(where).append(" ").append(toHqlOrder(params)).toString());
        getHibernateHelper().setQueryParams(queryParams, q);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    private void addAnnotationCriteria(ExperimentSearchCriteria criteria, StringBuilder where, StringBuilder from,
            Map<String, Object> queryParams) {
        final List<String> diseaseStates = new LinkedList<String>();
        final List<String> tissueSites = new LinkedList<String>();
        final List<String> cellTypes = new LinkedList<String>();
        final List<String> materialTypes = new LinkedList<String>();
        for (final AnnotationCriterion ac : criteria.getAnnotationCriterions()) {
            if (ac.getCategory().getName().equals(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName())) {
                diseaseStates.add(ac.getValue());
            } else if (ac.getCategory().getName().equals(ExperimentOntologyCategory.CELL_TYPE.getCategoryName())) {
                cellTypes.add(ac.getValue());
            } else if (ac.getCategory().getName().equals(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName())) {
                materialTypes.add(ac.getValue());
            } else if (ac.getCategory().getName().equals(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName())) {
                tissueSites.add(ac.getValue());
            }
        }

        if (!diseaseStates.isEmpty() || !tissueSites.isEmpty() || !cellTypes.isEmpty() || !materialTypes.isEmpty()) {
            final Map<String, List<? extends Serializable>> blocks =
                    new HashMap<String, List<? extends Serializable>>();
            addAnnotationCriterionValues(where, from, diseaseStates, "diseaseState", "ds", blocks);
            addAnnotationCriterionValues(where, from, tissueSites, "tissueSite", "ts", blocks);
            addAnnotationCriterionValues(where, from, materialTypes, "materialType", "mt", blocks);
            addAnnotationCriterionValues(where, from, cellTypes, "cellType", "ct", blocks);
            where.append(" )");
            queryParams.putAll(blocks);
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void addAnnotationCriterionValues(StringBuilder where, StringBuilder from, List<String> values,
            String assocPath, String alias, Map<String, List<? extends Serializable>> blocks) {
        final String bmAlias = "b_" + alias;
        if (!values.isEmpty()) {
            from.append(" INNER JOIN e.biomaterials ").append(bmAlias);
            from.append(" INNER JOIN ").append(bmAlias).append(".").append(assocPath).append(" ").append(alias);
            where.append(" AND (").append(getHibernateHelper().buildInClauses(values, alias + ".value", blocks))
                    .append(")");
        }
    }

    private static String getJoinClause(SearchCategory... categories) {
        final LinkedHashSet<String> joins = new LinkedHashSet<String>();
        for (final SearchCategory category : categories) {
            joins.addAll(Arrays.asList(category.getJoins()));
        }
        final StringBuffer sb = new StringBuffer();
        for (final String table : joins) {
            sb.append(" LEFT JOIN ").append(table);
        }
        return sb.toString();
    }

    private String getWhereClause(SearchCategory... categories) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        for (final SearchCategory category : categories) {
            sb.append(i++ == 0 ? " WHERE (" : " OR (").append(category.getWhereClause()).append(')');
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        final String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "cellType", SOURCES);
        return getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        final String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "diseaseState", SOURCES);
        return getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<AbstractCharacteristic> getArbitraryCharacteristicsForExperimentSamples(
            Experiment experiment) {
        Query q = generateArbitraryCharacteristicsCategoriesPreparedQuery(experiment, "acs");
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Category> getArbitraryCharacteristicsCategoriesForExperimentSamples(
            Experiment experiment) {
        Query q = generateArbitraryCharacteristicsCategoriesPreparedQuery(experiment, "acs.category");
        return q.list();
    }

    private Query generateArbitraryCharacteristicsCategoriesPreparedQuery(Experiment experiment, String entityToken) {
        String sQuery = 
                "select distinct @ENTITY@ "
            + "from @EXPERIMENT@ e "
            + "     left join e.biomaterials abs "
            + "     left join abs.characteristics acs "
            + "where (abs.class = :sample_class or abs.class = :source_class) "
            + "      and e.id = :exp";
        sQuery = sQuery
                .replaceAll("@ENTITY@", entityToken)
                .replaceAll("@EXPERIMENT@", Experiment.class.getName());
        Query q = getCurrentSession().createQuery(sQuery);
        q.setString("sample_class", Sample.DISCRIMINATOR);
        q.setString("source_class", Source.DISCRIMINATOR);
        q.setEntity("exp", experiment);
        return q;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        // DEVELOPER NOTE: it is faster to use separate queries to retrieve the material types from each
        // biomaterial type. doing it in one query requires either multiple subselects or a cartesian product
        // join, both of which are very slow
        final Set<Term> types = new HashSet<Term>();
        for (final String biomaterial : BIOMATERIALS) {
            final String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "materialType", biomaterial);
            types.addAll(getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list());
        }
        return new ArrayList<Term>(types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        final String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "tissueSite", SOURCES);
        return getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    public List<AssayType> getAssayTypes() {
        final String queryString = "from " + AssayType.class.getName() + " c order by c.name asc";
        return getCurrentSession().createQuery(queryString).setCacheable(true).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AbstractBioMaterial> getUnfilteredBiomaterialsForProject(final Long projectId) {
        final UnfilteredCallback unfilteredCallback = new UnfilteredCallback() {
            @Override
            public Object doUnfiltered(Session s) {
                final Query q =
                        s.createQuery("FROM " + AbstractBioMaterial.class.getName()
                                + " bm WHERE bm.experiment.project.id = ?");
                q.setParameter(0, projectId);
                return q.list();
            }
        };
        @SuppressWarnings(UNCHECKED)
        final List<AbstractBioMaterial> bms =
                (List<AbstractBioMaterial>) getHibernateHelper().doUnfiltered(unfilteredCallback);
        return new HashSet<AbstractBioMaterial>(bms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Source getSourceForExperiment(Experiment experiment, String sourceName) {
        return getBioMaterialForExperiment(experiment, sourceName, Source.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample getSampleForExperiment(Experiment experiment, String sampleName) {
        return getBioMaterialForExperiment(experiment, sampleName, Sample.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Extract getExtractForExperiment(Experiment experiment, String extractName) {
        return getBioMaterialForExperiment(experiment, extractName, Extract.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LabeledExtract getLabeledExtractForExperiment(Experiment experiment, String labeledExtractName) {
        return getBioMaterialForExperiment(experiment, labeledExtractName, LabeledExtract.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractBioMaterial> T getBioMaterialForExperiment(Experiment experiment,
            String bioMaterialName, Class<T> bioMaterialClass) {
        if (experiment == null) {
            return null;
        }
        final String queryString =
                "from " + bioMaterialClass.getName() + " b where b.experiment = :experiment and b.name = :name";
        final Query query = getCurrentSession().createQuery(queryString);
        query.setParameter("experiment", experiment);
        query.setString("name", bioMaterialName);
        return (T) query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsWithReImportable() {
        final String q =
                "select distinct p from " + Project.class.getName()
                        + " p left join p.files f where f.status = :status and f.type in (:types) order by p.id";
        final Query query = getCurrentSession().createQuery(q);
        query.setParameter("status", FileStatus.IMPORTED_NOT_PARSED.name());
        query.setParameterList("types",
                Sets.newHashSet(FileTypeRegistryImpl.namesForTypes(this.typeRegistry.getParseableArrayDataTypes())));
        return query.list();
    }
}
