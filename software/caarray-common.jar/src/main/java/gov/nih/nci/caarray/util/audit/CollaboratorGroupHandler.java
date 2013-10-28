//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * @author wcheng
 *
 */
public class CollaboratorGroupHandler extends AbstractAuditEntityHandler<CollaboratorGroup> {

    /**
     * @param processor audit log processor
     */
    public CollaboratorGroupHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, CollaboratorGroup entity, String property, 
            String columnName, Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("ownerId".equals(property)) {
            String msg = String.format("Collaborator Group %s owner set to %s",
                    entity.getGroup().getGroupName(), entity.getOwner().getLoginName());
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            addedEntry = true;
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
        return addedEntry;
    }
}
