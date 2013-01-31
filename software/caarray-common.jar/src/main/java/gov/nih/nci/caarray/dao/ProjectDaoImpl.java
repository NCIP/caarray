//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UnfilteredCallback;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.project</code> package.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class ProjectDaoImpl extends AbstractCaArrayDaoImpl implements ProjectDao {
    private static final Logger LOG = Logger.getLogger(ProjectDaoImpl.class);
    private static final String UNCHECKED = "unchecked";
    private static final String EXP_ID_PARAM = "expId";
    private static final String SOURCES = "sources";
    private static final String[] BIOMATERIALS = {"sources", "samples", "extracts", "labeledExtracts" };
    private static final String TERM_FOR_EXPERIMENT_HSQL = "select distinct s.{0} from " + Experiment.class.getName()
        + " e left join e.{1} s where e.id = :" + EXP_ID_PARAM + " and s.{0} is not null";

    /**
     * Saves a project by first updating the lastUpdated field, and then saves the entity to persistent storage,
     * updating or inserting as necessary.
     *
     * @param persistentObject the entity to save
     */
    @Override
    public void save(PersistentObject persistentObject) {
        if (persistentObject instanceof Project) {
            ((Project) persistentObject).setLastUpdated(new Date());
        }
        super.save(persistentObject);
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * {@inheritDoc}
     */
    public Project getProjectByPublicId(String publicId) {
        Criteria c = HibernateUtil.getCurrentSession().createCriteria(Project.class);
        c.createCriteria("experiment").add(Restrictions.eq("publicIdentifier", publicId).ignoreCase());
        return (Project) c.uniqueResult();
    }

    @SuppressWarnings(UNCHECKED)
    public List<Project> getProjectsForCurrentUser(boolean showPublic, PageSortParams pageSortParams) {
        Query q = getProjectsForUserQuery(showPublic, false, pageSortParams);
        q.setFirstResult(pageSortParams.getIndex());
        q.setMaxResults(pageSortParams.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int getProjectCountForCurrentUser(boolean showPublic) {
        Query q = getProjectsForUserQuery(showPublic, true, null);
        return ((Number) q.uniqueResult()).intValue();
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private Query getProjectsForUserQuery(boolean showPublic, boolean count,
            PageSortParams<Project> pageSortParams) {
        User user  = UsernameHolder.getCsmUser();
        SortCriterion<Project> sortCrit = pageSortParams != null ? pageSortParams.getSortCriterion() : null;
        String ownerSubqueryStr =
                "(select pe.value from " + ProtectionElement.class.getName()
                        + " pe where pe.objectId = :objectId and pe.attribute = :attribute and "
                        + " pe.application = :application and :user in elements(pe.owners)) ";
        String collabSubqueryStr =
                "(select ap.projectForGroupProfile.id from "
                        + AccessProfile.class.getName()
                        + " ap join ap.group cg, Group g "
                        + " where ap.securityLevelInternal != :noneSecLevel and cg.groupId = g.groupId "
                        + " and :user in elements(g.users))";
        String selectClause = count ? "SELECT COUNT(DISTINCT p)" : "SELECT DISTINCT p";
        StringBuilder queryStr =
                new StringBuilder(selectClause).append(" from ").append(Project.class.getName()).append(" p ");
        if (!count) {
            queryStr.append(" left join fetch p.experiment e");
        }
        queryStr.append(" where p.statusInternal ").append(showPublic ? " = " : " != ")
                .append(" :status and (p.id in ").append(ownerSubqueryStr).append(" or p.id in ").append(
                        collabSubqueryStr).append(")");
        if (!count && sortCrit != null) {
            queryStr.append(" ORDER BY p.").append(sortCrit.getOrderField());
            if (pageSortParams.isDesc()) {
                queryStr.append(" desc");
            }
        }

        Query query = getCurrentSession().createQuery(queryStr.toString());
        query.setParameter("status", ProposalStatus.PUBLIC);
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
    @SuppressWarnings(UNCHECKED)
    public List<Project> searchByCategory(PageSortParams<Project> params, String keyword,
            SearchCategory... categories) {
        Query q = getSearchQuery(false, params, keyword, categories);
        q.setFirstResult(params.getIndex());
        q.setMaxResults(params.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, SearchCategory... categories) {
        Query q = getSearchQuery(true, null, keyword, categories);
        return ((Number) q.uniqueResult()).intValue();
    }



    private Query getSearchQuery(boolean count, PageSortParams<Project> params, String keyword,
            SearchCategory... categories) {
        SortCriterion<Project> sortCrit = params != null ? params.getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
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
        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString("keyword", "%" + keyword + "%");
        return q;
    }

    private static String getJoinClause(SearchCategory... categories) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>();
        for (SearchCategory category : categories) {
            joins.addAll(Arrays.asList(category.getJoins()));
        }
        StringBuffer sb = new StringBuffer();
        for (String table : joins) {
            sb.append(" LEFT JOIN ").append(table);
        }
        return sb.toString();
    }

    private String getWhereClause(SearchCategory... categories) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (SearchCategory category : categories) {
            sb.append(i++ == 0 ? " WHERE (" : " OR (").append(category.getWhereClause()).append(')');
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "cellType", SOURCES);
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "diseaseState", SOURCES);
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "tissueSite", SOURCES);
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        // DEVELOPER NOTE: it is faster to use separate queries to retrieve the material types from each
        // biomaterial type. doing it in one query requires either multiple subselects or a cartesian product
        // join, both of which are very slow
        Set<Term> types = new HashSet<Term>();
        for (String biomaterial : BIOMATERIALS) {
            String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "materialType", biomaterial);
            types.addAll(HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId())
                    .list());
        }
        return new ArrayList<Term>(types);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public Set<Sample> getUnfilteredSamplesForProject(final Project project) {
        UnfilteredCallback unfilteredCallback = new UnfilteredCallback() {
            public Object doUnfiltered(Session s) {
                Project p = (Project) s.get(Project.class, project.getId());
                return p.getExperiment().getSamples();
            }
        };
        return (Set<Sample>) HibernateUtil.doUnfiltered(unfilteredCallback);
    }

    /**
     * {@inheritDoc}
     */
    public Source getSourceForExperiment(Experiment experiment, String sourceName) {
        return getBioMaterialForExperiment(experiment, sourceName, Source.class);
    }

    /**
     * {@inheritDoc}
     */
    public Sample getSampleForExperiment(Experiment experiment, String sampleName) {
        return getBioMaterialForExperiment(experiment, sampleName, Sample.class);
    }

    /**
     * {@inheritDoc}
     */
    public Extract getExtractForExperiment(Experiment experiment, String extractName) {
        return getBioMaterialForExperiment(experiment, extractName, Extract.class);
    }

    /**
     * {@inheritDoc}
     */
    public LabeledExtract getLabeledExtractForExperiment(Experiment experiment, String labeledExtractName) {
        return getBioMaterialForExperiment(experiment, labeledExtractName, LabeledExtract.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractBioMaterial> T getBioMaterialForExperiment(Experiment experiment, String bioMaterialName,
            Class<T> bioMaterialClass) {
        if (experiment == null) {
            return null;
        }
        String queryString = "from " + bioMaterialClass.getName()
                + " b where b.experiment = :experiment and b.name = :name";
        Query query = HibernateUtil.getCurrentSession().createQuery(queryString);
        query.setParameter("experiment", experiment);
        query.setString("name", bioMaterialName);
        return (T) query.uniqueResult();
    }

}
