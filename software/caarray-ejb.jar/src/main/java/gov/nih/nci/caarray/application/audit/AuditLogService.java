//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.application.audit;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import gov.nih.nci.caarray.dao.AuditLogDao;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import java.util.List;
import javax.ejb.Local;


/**
 * local EJB interpace for the Dao wrapper {@link AuditLogServiceBean}.
 * @author gax
 */
@Local
public interface AuditLogService extends AuditLogDao {
    /**
     * JNDI name of the session bean.
     */
    String JNDI_NAME = "caarray/AuditLogServiceBean/local";

    /**
     * @param criteria criteria
     * @param sort sort
     * @return matching log records.
     */
    List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria, PageSortParams<AuditLogRecord> sort);
    
    /**
     * @param criteria criteria
     * @return row count.
     */
    int getRecordsCount(AuditLogSearchCriteria criteria);

}
