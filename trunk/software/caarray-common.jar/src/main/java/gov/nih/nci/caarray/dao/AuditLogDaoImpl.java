//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.util.HibernateUtil;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

/**
 *
 * @author gax
 */
public class AuditLogDaoImpl implements AuditLogDao {

    /**
     * {@inheritDoc}
     */
    public List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria,
            PageSortParams<AuditLogRecord> sort) {
        StringBuffer sb = new StringBuffer();
        buildHql(criteria, sb, "distinct r");
        sb.append(" order by ");
        if (sort.getSortCriteria().isEmpty()) {
            sb.append("createdDate asc, r.id desc");
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

        Query q = buildQuery(criteria, sb);
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
        buildHql(criteria, sb, "count(distinct r)");
        Query q = buildQuery(criteria, sb);
        return ((Number) q.uniqueResult()).intValue();
    }

    private void buildHql(AuditLogSearchCriteria criteria, StringBuffer hql, String selectClause) {
        hql.append("select ").append(selectClause).append(" from ").append(AuditLogRecord.class.getName())
                .append(" r inner join r.details d");

        String where = " where";
        String and = "";
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            hql.append(where);
            hql.append(" r.username = :username");
            and = " and";
            where = "";
        }
        if (StringUtils.isNotBlank(criteria.getMessage())) {
            hql.append(and);
            hql.append(where);
            hql.append(" lower(d.message) like :message");
        }
    }

    private Query buildQuery(AuditLogSearchCriteria criteria, StringBuffer sb) {
        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        if (StringUtils.isNotBlank(criteria.getUsername())) {
            q.setParameter("username", criteria.getUsername());
        }
        if (StringUtils.isNotBlank(criteria.getMessage())) {
            q.setParameter("message", "%" + criteria.getMessage().toLowerCase() + "%");
        }        
        
        return q;
    }
}
