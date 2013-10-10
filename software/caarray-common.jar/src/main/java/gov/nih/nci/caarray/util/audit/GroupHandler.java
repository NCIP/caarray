//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * @author wcheng
 *
 */
public class GroupHandler extends AbstractAuditEntityHandler<Group> {

    /**
     * @param processor audit log processor
     */
    public GroupHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAdd(AuditLogRecord record, Group entity, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("groupName".equals(property)) {
            // there is no group.users event on insert so we'll log new users here.
            getProcessor().addDetail(record, columnName, "Group " + newVal + " created", oldVal, newVal);
            if (entity.getUsers() != null) {
                for (Object u : entity.getUsers()) {
                    String msg = "User " + ((User) u).getLoginName() + " added to group " + entity.getGroupName();
                    getProcessor().addDetail(record, columnName, msg, null, u);
                }
            }
            addedEntry = true;
        }
        return addedEntry;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    protected boolean logUpdate(AuditLogRecord record, Group entity, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean updatedEntry = false;
        if ("groupName".equals(property)) {
            String msg = "Group name changed from " + oldVal + " to " + newVal;
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            updatedEntry = true;
        } else if ("users".equals(property)) {
            Collection<User> tmp = CollectionUtils.subtract((Set<User>) oldVal, (Set<User>) newVal);
            for (User u : tmp) {
                String msg = "User " + u.getLoginName() + " removed from group " + entity.getGroupName();
                getProcessor().addDetail(record, columnName, msg, u, null);
                updatedEntry = true;
            }
            tmp = CollectionUtils.subtract((Set<User>) newVal, (Set<User>) oldVal);
            for (User u : tmp) {
                String msg = "User " + u.getLoginName() + " added to group " + entity.getGroupName();
                getProcessor().addDetail(record, columnName, msg, null, u);
                updatedEntry = true;
            }
        }
        return updatedEntry;
    }
}
