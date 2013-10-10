//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

/**
 * @author wcheng
 *
 */
public class ProtocolHandler extends AbstractAuditEntityHandler<Protocol> {

    /**
     * @param processor audit log processor
     */
    public ProtocolHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, Protocol entity, String property, String columnName,
            Object oldVal, Object newVal) {
        String addUpdate = record.getType() == AuditType.INSERT ? "added" : "updated";
        if (record.getDetails().size() == 0) {
            String msg = String.format("%s '%s' %s", "Protocol", entity.getName(), addUpdate);
            getProcessor().addDetail(record, "Protocol", msg);
        }
        String msg = String.format(" - %s %s", property, addUpdate);
        getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
        return true;
    }
}
