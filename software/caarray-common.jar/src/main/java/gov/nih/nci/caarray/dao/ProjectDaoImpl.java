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
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

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
    private static final String TERM_FOR_EXPERIMENT_HSQL = "select distinct s.{0} from " + Experiment.class.getName()
        + " e," +  Source.class.getName() + " s where e.id = :" + EXP_ID_PARAM + " and s in elements(e.sources) "
        + "and s.{0} is not null";

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
                new StringBuilder(selectClause).append(" from ").append(Project.class.getName()).append(" p ")
                        .append(" where p.statusInternal ").append(showPublic ? " = " : " != ").append(
                                " :status and (p.id in ").append(ownerSubqueryStr).append(" or p.id in ").append(
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
            sb.append(i++ == 0 ? " WHERE (" : " OR (");
            String[] fields = category.getSearchFields();
            int j = 0;
            for (String field : fields) {
                if (j++ > 0) {
                    sb.append(" OR ");
                }
                sb.append(field).append(" LIKE :keyword");
            }
            sb.append(')');
        }
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "cellType");
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "diseaseState");
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        String hsql = MessageFormat.format(TERM_FOR_EXPERIMENT_HSQL, "tissueSite");
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        String hsql = "select distinct b.materialType from " + Experiment.class.getName() + " e,"
            + AbstractBioMaterial.class.getName() +  " b where e.id = :expId and "
            + "(b in elements(e.sources) or b in elements(e.samples) or b in elements(e.extracts) "
            + "or b in elements(e.labeledExtracts)) and b.materialType is not null";
        return HibernateUtil.getCurrentSession().createQuery(hsql).setLong(EXP_ID_PARAM, experiment.getId()).list();
    }
}
