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

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UnfilteredCallback;
import gov.nih.nci.caarray.util.UsernameHolder;
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
import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.project</code> package.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class ProjectDaoImpl extends AbstractCaArrayDaoImpl implements ProjectDao {
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

    /**
     * {@inheritDoc}
     */
    public Project getProjectByPublicId(String publicId) {
        Criteria c = HibernateUtil.getCurrentSession().createCriteria(Project.class);
        c.createCriteria("experiment").add(Restrictions.eq("publicIdentifier", publicId).ignoreCase());
        return (Project) c.uniqueResult();
    }

    @SuppressWarnings(UNCHECKED)
    public List<Project> getProjectsForCurrentUser(PageSortParams pageSortParams) {
        Query q = getProjectsForUserQuery(false, pageSortParams);
        q.setFirstResult(pageSortParams.getIndex());
        q.setMaxResults(pageSortParams.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    
    public List<Project> getProjectsForOwner(User user) {
        final String q = "SELECT DISTINCT p FROM " + Project.class.getName()
                + " p left join fetch p.experiment e where p.id in"
                + "(select pe.value from " + ProtectionElement.class.getName()
                        + " pe where pe.objectId = :objectId and pe.attribute = :attribute and "
                        + " pe.application = :application and :user in elements(pe.owners)) ";

        Query query = getCurrentSession().createQuery(q);
        query.setString("objectId", Project.class.getName());
        query.setString("attribute", "id");
        query.setEntity("application", SecurityUtils.getApplication());
        query.setEntity("user", user);

        @SuppressWarnings("unchecked")
        List<Project> projects = query.list(); 
        return projects; // NOPMD
    }

    /**
     * {@inheritDoc}
     */
    public int getProjectCountForCurrentUser() {
        Query q = getProjectsForUserQuery(true, null);
        return ((Number) q.uniqueResult()).intValue();
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private Query getProjectsForUserQuery(boolean count,
            PageSortParams<Project> pageSortParams) {
        User user  = UsernameHolder.getCsmUser();
        @SuppressWarnings("deprecation")
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
        queryStr.append(" where (p.id in ").append(ownerSubqueryStr).append(" or p.id in ").append(
                        collabSubqueryStr).append(")");
        if (!count && sortCrit != null) {
            queryStr.append(" ORDER BY p.").append(sortCrit.getOrderField());
            if (pageSortParams.isDesc()) {
                queryStr.append(" desc");
            }
        }

        Query query = getCurrentSession().createQuery(queryStr.toString());
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
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
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
        @SuppressWarnings("deprecation")
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
        q.setString("keyword", "%" + StringUtils.defaultString(keyword) + "%");
        return q;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({UNCHECKED, "PMD" })
    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        StringBuilder from = new StringBuilder("SELECT distinct e FROM ").append(Experiment.class.getName()).append(
                " e");
        StringBuilder where = new StringBuilder("WHERE (1=1)");
        Map<String, Object> queryParams = new HashMap<String, Object>();
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
            from.append(" LEFT JOIN e.assayTypes at");
            where.append(" AND at.id = :assay_type_id");
            queryParams.put("assay_type_id", criteria.getAssayType().getId());            
        }
        if (criteria.getArrayProvider() != null) {
            from.append(" LEFT JOIN e.manufacturer m");
            where.append(" AND m.id = :array_provider_id");
            queryParams.put("array_provider_id", criteria.getArrayProvider().getId());            
        }
        if (criteria.getPrincipalInvestigator() != null) {
            from.append(" LEFT JOIN e.experimentContacts ec LEFT JOIN ec.roles r LEFT JOIN ec.contact p");
            where.append(" AND r.value = :pi_role AND p.id = :pi_id");
            queryParams.put("pi_role", ExperimentContact.PI_ROLE);            
            queryParams.put("pi_id", criteria.getPrincipalInvestigator().getId());            
        }
        if (!criteria.getAnnotationCriterions().isEmpty()) {
            List<String> diseaseStates = new LinkedList<String>();
            List<String> tissueSites = new LinkedList<String>();
            List<String> cellTypes = new LinkedList<String>();
            List<String> materialTypes = new LinkedList<String>();
            for (AnnotationCriterion ac : criteria.getAnnotationCriterions()) {
                if (ac.getCategory().getName().equals(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName())) {
                    diseaseStates.add(ac.getValue());
                } else if (ac.getCategory().getName().equals(ExperimentOntologyCategory.CELL_TYPE.getCategoryName())) {
                    cellTypes.add(ac.getValue());
                } else if (ac.getCategory().getName()
                        .equals(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName())) {
                    materialTypes.add(ac.getValue());
                } else if (ac.getCategory().getName().equals(
                        ExperimentOntologyCategory.TISSUE_ANATOMIC_SITE.getCategoryName())) {
                    tissueSites.add(ac.getValue());
                }
            }
              
            if (!diseaseStates.isEmpty() || !tissueSites.isEmpty() || !cellTypes.isEmpty() 
                    || !materialTypes.isEmpty()) {
                from.append(" LEFT JOIN e.sources so LEFT JOIN e.samples sa ");
                where.append(" AND ( (0=1) ");
                Map<String, List<? extends Serializable>> blocks = new HashMap<String, List<? extends Serializable>>();
                addAnnotationCriterionValues(where, from, diseaseStates, "diseaseState", "ds", blocks);
                addAnnotationCriterionValues(where, from, tissueSites, "tissueSite", "ts", blocks);
                addAnnotationCriterionValues(where, from, materialTypes, "materialType", "mt", blocks);
                addAnnotationCriterionValues(where, from, cellTypes, "cellType", "ct", blocks);
                where.append(" )");
                queryParams.putAll(blocks);
            }
        }
        
        Query q = HibernateUtil.getCurrentSession().createQuery(
                from.append(" ").append(where).append(" ").append(toHqlOrder(params)).toString());
        HibernateUtil.setQueryParams(queryParams, q);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void addAnnotationCriterionValues(StringBuilder where, StringBuilder from, List<String> values,
            String assocPath, String alias, Map<String, List<? extends Serializable>> blocks) {
        if (!values.isEmpty()) {
            String sourceAlias = alias + "_so";
            String sampleAlias = alias + "_sa";
            
            from.append(" LEFT JOIN so.").append(assocPath).append(" ").append(sourceAlias).append(" LEFT JOIN sa.")
                    .append(assocPath).append(" ").append(sampleAlias);
            where.append(" OR ").append(HibernateHelper.buildInClause(values, sourceAlias + ".value", blocks));
            where.append(" OR ").append(HibernateHelper.buildInClause(values, sampleAlias + ".value", blocks));
        }
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
    public List<AssayType> getAssayTypes() {
        String queryString = "from " + AssayType.class.getName() + " c order by c.name asc";
        return HibernateUtil.getCurrentSession().createQuery(queryString).setCacheable(true).list();
    }

    /**
     * {@inheritDoc}
     */
    public Set<AbstractBioMaterial> getUnfilteredBiomaterialsForProject(final Long projectId) {
        UnfilteredCallback unfilteredCallback = new UnfilteredCallback() {
            public Object doUnfiltered(Session s) {
                Query q = s.createQuery("FROM " + AbstractBioMaterial.class.getName()
                        + " bm WHERE bm.experiment.project.id = ?");
                q.setParameter(0, projectId);                    
                return q.list();
            }
        };
        @SuppressWarnings(UNCHECKED)        
        List<AbstractBioMaterial> bms = (List<AbstractBioMaterial>) HibernateUtil.doUnfiltered(unfilteredCallback);
        return new HashSet<AbstractBioMaterial>(bms);
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
