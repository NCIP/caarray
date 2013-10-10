//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public class TermHandler extends AbstractAuditEntityHandler<Term> {

    /**
     * @param processor audit log processor
     */
    public TermHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, Term entity, String property, String columnName,
            Object oldVal, Object newVal) {
        String addUpdate = record.getType() == AuditType.INSERT ? "added" : "updated";
        if (record.getDetails().size() == 0) {
            String msg = String.format("%s '%s' %s", "Term", entity.getValue(), addUpdate);
            getProcessor().addDetail(record, "Term", msg);
        }
        String msg = String.format(" - %s %s", property, addUpdate);
        getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
        return true;
    }

}
