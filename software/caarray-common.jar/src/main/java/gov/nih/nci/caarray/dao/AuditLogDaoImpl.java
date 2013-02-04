//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
