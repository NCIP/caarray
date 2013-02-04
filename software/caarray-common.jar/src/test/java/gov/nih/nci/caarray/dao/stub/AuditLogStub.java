//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao.stub;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import gov.nih.nci.caarray.dao.AuditLogDao;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gax
 */
public class AuditLogStub implements AuditLogDao {

    public List<AuditLogRecord> getRecords(AuditLogSearchCriteria criteria, PageSortParams<AuditLogRecord> sort) {
        return Collections.emptyList();
    }

    public int getRecordsCount(AuditLogSearchCriteria criteria) {
        return 0;
    }

}
