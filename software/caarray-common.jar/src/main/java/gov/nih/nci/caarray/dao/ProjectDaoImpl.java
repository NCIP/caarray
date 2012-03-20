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
@SuppressWarnings("PMD.CyclomaticComplexity")
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
            // ideally, the lastUpdated property should be updated by AutoPropertiesInterceptor,
            // but it's a lot harder to update the parent project when child objects are updated.
            ((Project) persistentObject).setLastUpdated(new Date());
        }
        return super.save(persistentObject);
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
    public List<Project> getProjectsForCurrentUser(PageSortParams pageSortParams) {
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
    public List<AbstractCharacteristic> getCharacteristicsForExperiment(
            Experiment experiment) {
        String queryStr = "SELECT DISTINCT acs from Experiment e left join e.biomaterials abs "
                + "left join abs.characteristics acs "
                + "WHERE e.id = :experiment";
        Query q = getCurrentSession().createQuery(queryStr);
        q.setEntity("experiment", experiment);
        return q.list();
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
