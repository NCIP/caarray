/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-app
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-app Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-app Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-app Software; (ii) distribute and
 * have distributed to and by third parties the caarray-app Software and any
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

import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.google.inject.Inject;

/**
 *
 * @author gax
 */
public class AuditLogDaoImpl extends AbstractCaArrayDaoImpl implements AuditLogDao {
    private static final String FROM_CLAUSE = " from audit_log_record r, audit_log_security s, audit_log_detail d";
    private static final String WHERE_CLAUSE = " where r.id = s.record and r.id = d.record";
    private static final String SECURITY_CLAUSE = " and ((s.entity_name = 'project' and "
            + "(select count(pig.group_id) from csm_project_id_group pig where pig.group_id in (:GROUP_NAMES)"
            + " and pig.privilege_id = s.privilege_id and pig.attribute_value = s.entity_id) > 0) "
            + "or (s.entity_name = 'group' and s.entity_id in (:GROUP_NAMES)))";
    private static final String USERNAME_CLAUSE = " and r.username = :username";
    private static final String MESSAGE_CLAUSE = " and lower(d.message) like :message";
    
    /**
     * 
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject 
    public AuditLogDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    public List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria,
            PageSortParams<AuditLogRecord> sort) {
        StringBuffer sb = new StringBuffer();
        buildSql(criteria, sb, "distinct r.*");
        sb.append(" order by ");
        if (sort.getSortCriteria().isEmpty()) {
            sb.append("created_date asc, r.id desc");
        } else {
            String direction = sort.isDesc() ? " desc" : " asc";
            String comma = "";
            for (SortCriterion<AuditLogRecord> s : sort.getSortCriteria()) {
                sb.append(comma);
                sb.append("r.").append(s.getOrderField()).append(direction);
                comma = ", ";
            }
            sb.append(comma).append("r.id").append(direction);
        }

        SQLQuery q = buildQuery(criteria, sb);
        q.addEntity("r", AuditLogRecord.class);
        q.setMaxResults(sort.getPageSize());
        q.setFirstResult(sort.getIndex());
        @SuppressWarnings("unchecked")
        List<AuditLogRecord> records = q.list();
        return records;
    }

    /**
     * {@inheritDoc}
     */
    public int getRecordsCount(AuditLogSearchCriteria criteria) {
        StringBuffer sb = new StringBuffer();
        buildSql(criteria, sb, "count(distinct r.id)");
        Query q = buildQuery(criteria, sb);
        return ((Number) q.uniqueResult()).intValue();
    }

    private void buildSql(AuditLogSearchCriteria criteria, StringBuffer sql, String selectClause) {
        sql.append("select ").append(selectClause).append(FROM_CLAUSE).append(WHERE_CLAUSE).append(SECURITY_CLAUSE);
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            sql.append(USERNAME_CLAUSE);
        }
        if (StringUtils.isNotBlank(criteria.getMessage())) {
            sql.append(MESSAGE_CLAUSE);
        }
    }

    private SQLQuery buildQuery(AuditLogSearchCriteria criteria, StringBuffer sb) {
        SQLQuery q = getCurrentSession().createSQLQuery(sb.toString());
        q.setParameterList("GROUP_NAMES", getGroupNames());
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            q.setParameter("username", criteria.getUsername());
        }
        if (StringUtils.isNotBlank(criteria.getMessage())) {
            q.setParameter("message", "%" + criteria.getMessage().toLowerCase() + "%");
        }        
        return q;
    }
    
    @SuppressWarnings("unchecked")
    private String[] getGroupNames() {
        User user = CaArrayUsernameHolder.getCsmUser();
        try {
            Set<Group> groups = SecurityUtils.getAuthorizationManager().getGroups(user.getUserId().toString());
            String[] groupNames = new String[groups.size()];
            int i = 0;
            for (Group g : groups) {
                groupNames[i++] = String.valueOf(g.getGroupId());
            }
            return groupNames;
        } catch (CSObjectNotFoundException e) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }
}
