//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.application.audit;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import java.util.List;
import javax.ejb.Stateless;

/**
 * Simple bean to wrap the DAO.
 * @author gax
 */
@Stateless
public class AuditLogServiceBean implements AuditLogService {

    private CaArrayDaoFactory daoLocator = CaArrayDaoFactory.INSTANCE;

    /**
     * @param daoLocator replacement DAO locator (for testing).
     * By default, it is set to {@link CaArrayDaoFactory#INSTANCE}.
     */
    public void setDaoLocator(CaArrayDaoFactory daoLocator) {
        this.daoLocator = daoLocator;
    }

    /**
     * {@inheritDoc}
     */
    public List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria,
            PageSortParams<AuditLogRecord> sort) {
        return daoLocator.getAuditLogDao().getRecords(criteria, sort);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRecordsCount(AuditLogSearchCriteria criteria) {
        return daoLocator.getAuditLogDao().getRecordsCount(criteria);
    }

}
