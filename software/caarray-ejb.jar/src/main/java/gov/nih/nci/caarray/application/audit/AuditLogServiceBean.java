//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.application.audit;

import gov.nih.nci.caarray.dao.AuditLogDao;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.injection.InjectionInterceptor;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;

/**
 * Simple bean to wrap the DAO.
 * 
 * @author gax
 */
@Stateless
@Interceptors(InjectionInterceptor.class)
public class AuditLogServiceBean implements AuditLogService {
    private AuditLogDao auditLogDao;

    /**
     * 
     * @param auditLogDao the AuditLogDao dependency
     */
    @Inject
    public void setAuditLogDao(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria, PageSortParams<AuditLogRecord> sort) {
        return this.auditLogDao.getRecords(criteria, sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecordsCount(AuditLogSearchCriteria criteria) {
        return this.auditLogDao.getRecordsCount(criteria);
    }

}
