//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.search;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 *
 * @author gax
 */
public enum AuditLogSortCriterion implements SortCriterion<AuditLogRecord> {
    /**
     * sort my username.
     */
    USERNAME("username"),
    /**
     * sort by event date.
     */
    DATE("created_date");
    
    private final String orderField;
    
    private AuditLogSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getLeftJoinField() {
        // this is to support nci-commons-code 1.0.24, but this aspect of the
        // search is not yet used in caaaray or it is implemented diffrently.
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
