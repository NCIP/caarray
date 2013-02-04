//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.dao;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import java.util.List;

/**
 * 
 * @author gax
 */
public interface AuditLogDao {

    /**
     * @param criteria filter/search criteria
     * @param sort sort parameters
     * @return matching log records.
     */
    List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria, PageSortParams<AuditLogRecord> sort);

    /**
     * @param criteria filter/search criteria criteria
     * @return row count.
     */
    int getRecordsCount(AuditLogSearchCriteria criteria);

}
