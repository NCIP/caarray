//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.fiveamsolutions.nci.commons.audit.DefaultProcessor;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;

/**
 *
 * @author gax
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CaArrayAuditLogProcessor extends DefaultProcessor {
    
    private static final Class<?>[] AUDITED_CLASSES = {
        Project.class,
        AccessProfile.class,
        CollaboratorGroup.class,
        Group.class
    };

    private static final Logger LOG = Logger.getLogger(CaArrayAuditLogProcessor.class);
    /**
     * default ctor, logs security classes.
     */
    CaArrayAuditLogProcessor() {
        super(AUDITED_CLASSES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void processDetail(AuditLogRecord record, Object entity, Serializable key, String property,
            String columnName, Object oldVal, Object newVal) {
        
        if (entity instanceof AccessProfile) {
            logAccessProfile(record, (AccessProfile) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof Project) {
            logProject(record, (Project) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof Group) {
            logGroup(record, (Group) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof CollaboratorGroup) {
            logCollaboratorGroup(record, (CollaboratorGroup) entity, property, columnName, oldVal, newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked", "PMD.NPathComplexity" })
    private void logAccessProfile(AuditLogRecord record, AccessProfile entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("securityLevelInternal".equals(property)) {
            if (oldVal == null && newVal == SecurityLevel.NONE) {
                return;
            }
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            String type = entity.isPublicProfile() ? "Public" : "Group";
            detail.setMessage(type + " Access Profile for experiment " + entity.getProject().getExperiment().getTitle()
                    + (entity.isGroupProfile() ? " to group " + entity.getGroup().getGroup().getGroupName() : "")
                    + (oldVal == null ? " set" : " changed from " + oldVal) + " to " + newVal);
            record.getDetails().add(detail);

        } else if ("sampleSecurityLevels".equals(property)) {
            logAccessProfileSamples(record, entity, columnName, (Map<Sample, SampleSecurityLevel>) oldVal,
                    (Map<Sample, SampleSecurityLevel>) newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logProject(AuditLogRecord record, Project entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("locked".equals(property)) {
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            String newS = Boolean.TRUE.equals(newVal) ? "Locked" : "Unlocked";
            detail.setMessage("Experiment " + entity.getExperiment().getTitle() + " " + newS
                    + (record.getType() == AuditType.INSERT ?  " when created" : ""));
            record.getDetails().add(detail);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logGroup(AuditLogRecord record, Group entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("groupName".equals(property)) {
            // there is no group.users event on insert so we'll log new users here.
            if (record.getType() == AuditType.INSERT) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, oldVal, newVal);
                detail.setMessage("Group " + newVal + " created");
                record.getDetails().add(detail);
                if (entity.getUsers() != null) {
                    for (Object u : entity.getUsers()) {
                        detail = new AuditLogDetail(record, columnName, null, null);
                        super.processDetail(detail, null, u);
                        detail.setMessage("User " + ((User) u).getLoginName() + " added to group "
                                + entity.getGroupName());
                        record.getDetails().add(detail);
                    }
                }
            } else if (record.getType() == AuditType.UPDATE) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, oldVal, newVal);
                detail.setMessage("Group name changed from " + oldVal + " to " + newVal);
                record.getDetails().add(detail);
            }
        } else if ("users".equals(property)) {
            Collection<User> tmp = CollectionUtils.subtract((Set) oldVal, (Set) newVal);
            for (User u : tmp) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, u, null);
                detail.setMessage("User " + u.getLoginName() + " removed from group " + entity.getGroupName());
                record.getDetails().add(detail);
            }
            tmp = CollectionUtils.subtract((Set) newVal, (Set) oldVal);
            for (User u : tmp) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, null, u);
                detail.setMessage("User " + u.getLoginName() + " added to group " + entity.getGroupName());
                record.getDetails().add(detail);
            }
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logCollaboratorGroup(AuditLogRecord record, CollaboratorGroup entity, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("ownerId".equals(property)) {
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            detail.setMessage("Collaborator Group " + entity.getGroup().getGroupName() + " owner set to "
                    + entity.getOwner().getLoginName());
            record.getDetails().add(detail);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    private void logAccessProfileSamples(AuditLogRecord record, AccessProfile entity, String columnName,
            Map<Sample, SampleSecurityLevel> o, Map<Sample, SampleSecurityLevel> n) {
        
        // header message.
        String pname = entity.getProject().getExperiment().getTitle();
        AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
        StringBuffer sb = new StringBuffer("Access to samples on experiment ");
        sb.append(pname);
        if (entity.isPublicProfile()) {
            sb.append("'s Public Access Profile");
        } else {
            sb.append("'s Access Profile to Group ")
                    .append(entity.getGroup().getGroup().getGroupName());
        }        
        detail.setMessage(sb.toString());
        record.getDetails().add(detail);
        boolean added1Sample = false;

        if (o != null) {
            for (Map.Entry<Sample, SampleSecurityLevel> e : o.entrySet()) {
                SampleSecurityLevel v = n.get(e.getKey());
                if (v != null && v != e.getValue()) {
                    StringBuffer m = new StringBuffer(" - Access to sample ").append(e.getKey().getName());
                    m.append(" changed from ").append(e.getValue()).append(" to ").append(v);                
                    detail = new AuditLogDetail(record, columnName, null, null);
                    super.processDetail(detail, e.getValue(), v);
                    detail.setMessage(m.toString());
                    record.getDetails().add(detail);
                    added1Sample = true;
                }
            }
        }

        for (Map.Entry<Sample, SampleSecurityLevel> e : n.entrySet()) {
            // dont log transitions from null to NONE, as they occure when transitioning to a SELECTIVE state.
            if ((o == null || !o.containsKey(e.getKey())) && e.getValue() != SampleSecurityLevel.NONE) {
                detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, null, e.getValue());
                detail.setMessage(" - Access on sample " + e.getKey().getName() + " set to "
                        + e.getValue());
                record.getDetails().add(detail);
                added1Sample = true;
            }
        }
        if (!added1Sample) {
            // no need for a header is nothing was added.
            record.getDetails().remove(detail);
        }
    }
}
