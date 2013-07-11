//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.audit.AuditLogSecurity;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.fiveamsolutions.nci.commons.audit.DefaultProcessor;

/**
 *
 * @author gax
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CaArrayAuditLogProcessor extends DefaultProcessor {
    private static final Long READ_PRIV_ID = 3L;
    private static final Long PERMISSIONS_PRIV_ID = 8L;
    private static final Long SYSADMIN_GROUP_ID = 7L;
    private final Map<AuditLogRecord, Set<AuditLogSecurity>> securityEntries;
    private static final Map<String, String> EXP_PROP_MAP = new HashMap<String, String>();
    
    private static final Class<?>[] AUDITED_CLASSES = {
        Project.class,
        AccessProfile.class,
        CollaboratorGroup.class,
        Group.class,
        Experiment.class,
        AbstractArrayData.class,
        Organization.class,
        Organism.class
    };

    private static final Logger LOG = Logger.getLogger(CaArrayAuditLogProcessor.class);
    
    static {
        EXP_PROP_MAP.put("title", "Title");
        EXP_PROP_MAP.put("description", "Description");
        EXP_PROP_MAP.put("assayTypes", "Assay Types");
        EXP_PROP_MAP.put("manufacturer", "Provider");
        EXP_PROP_MAP.put("arrayDesigns", "Array Designs");
        EXP_PROP_MAP.put("organism", "Organism");
        EXP_PROP_MAP.put("experimentContacts", "Contacts");
        EXP_PROP_MAP.put("publications", "Publications");
    }
    
    /**
     * default constructor, logs security classes.
     */
    CaArrayAuditLogProcessor() {
        super(AUDITED_CLASSES);
        securityEntries = new HashMap<AuditLogRecord, Set<AuditLogSecurity>>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuditableEntity(Object o) {
        boolean isAuditable = super.isAuditableEntity(o);
        if (!isAuditable && CaArrayFile.class.isAssignableFrom(o.getClass())) {
            return FileStatus.SUPPLEMENTAL.equals(((CaArrayFile) o).getFileStatus());
        }
        return isAuditable;
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
        } else if (entity instanceof Experiment) {
            logExperiment(record, (Experiment) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof AbstractArrayData) {
            logArrayData(record, (AbstractArrayData) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof CaArrayFile) {
            logCaArrayFile(record, (CaArrayFile) entity, property, columnName, oldVal, newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
        addSecurity(AuditLogSecurity.GROUP, SYSADMIN_GROUP_ID, null, record);
    }
    
    /**
     * This method is called for any post processing required after the work from processDetail has been saved.
     * 
     * This method was added for ARRAY-1933, which required access to the session to save some security entries.
     * However, this should be eventually incorporated into DefaultProcessor in nci-commons.  See ARRAY-2496.
     * @param session hibernate session
     * @param records collection of audit records to process
     */
    public void postProcessDetail(Session session, Collection<AuditLogRecord> records) {
        // save audit log security entries
        for (AuditLogRecord record : records) {
            Set<AuditLogSecurity> entriesForRecord = securityEntries.get(record);
            if (entriesForRecord != null) {
                for (AuditLogSecurity entry : entriesForRecord) {
                    session.save(entry);
                }
                securityEntries.remove(record);
            }
        }
    }
    
    /**
     * Returns a mapping of audit records with their associated security entries. The security entries are created in
     * the processDetail method, but not persisted. This map allows us to store the security object created in
     * processDetail and retrieve them later to persist them to the database.
     * @return map of AuditLogRecord to AuditLogSecurity entries
     */
    public Map<AuditLogRecord, Set<AuditLogSecurity>> getSecurityEntries() {
        return securityEntries;
    }
    
    private void addProjectSecurity(AuditLogRecord record, Project project, Long privilegeId) {
        addSecurity(AuditLogSecurity.PROJECT, project.getId(), privilegeId, record);
    }
    
    private void addSecurity(String entityName, Long entityId, Long privilegeId, AuditLogRecord record) {
        Set<AuditLogSecurity> entries = securityEntries.get(record);
        if (entries == null) {
            entries = new HashSet<AuditLogSecurity>();
            securityEntries.put(record, entries);
        }
        for (AuditLogSecurity sec : entries) {
            if (sec.getEntityName().equals(entityName)
                    && sec.getEntityId().equals(entityId)
                    && ((sec.getPrivilegeId() == null && privilegeId == null)
                            || (sec.getPrivilegeId().equals(privilegeId)))) {
                return; // found duplicate
            }
        }
        entries.add(new AuditLogSecurity(entityName, entityId, privilegeId, record));
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked", "PMD.NPathComplexity" })
    private void logAccessProfile(AuditLogRecord record, AccessProfile entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("securityLevelInternal".equals(property)) {
            if (oldVal == null && newVal == SecurityLevel.NONE) {
                return;
            }
            String message = (entity.isPublicProfile() ? "Public" : "Group")
                    + " Access Profile for experiment " + entity.getProject().getExperiment().getTitle()
                    + (entity.isGroupProfile() ? " to group " + entity.getGroup().getGroup().getGroupName() : "")
                    + (oldVal == null ? " set" : " changed from " + oldVal) + " to " + newVal;
            addDetail(record, columnName, message, oldVal, newVal);
            addProjectSecurity(record, entity.getProject(), PERMISSIONS_PRIV_ID);
        } else if ("sampleSecurityLevels".equals(property)) {
            logAccessProfileSamples(record, entity, columnName, (Map<Sample, SampleSecurityLevel>) oldVal,
                    (Map<Sample, SampleSecurityLevel>) newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logProject(AuditLogRecord record, Project project, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("locked".equals(property)) {
            String message = "Experiment " + project.getExperiment().getTitle() + " "
                    + (Boolean.TRUE.equals(newVal) ? "Locked" : "Unlocked")
                    + (record.getType() == AuditType.INSERT ?  " when created" : "");
            addDetail(record, columnName, message, oldVal, newVal);
            addProjectSecurity(record, project, PERMISSIONS_PRIV_ID);
        } else if ("files".equals(property)) {
            Set<Long> newIds = new HashSet<Long>();
            for (CaArrayFile newFile : (Set<CaArrayFile>) newVal) {
                newIds.add(newFile.getId());
            }
            for (CaArrayFile oldFile : (Set<CaArrayFile>) oldVal) {
                if (!newIds.contains(oldFile.getId()) && auditFileDeletion(oldFile)) {
                    addExperimentDetail(record, columnName, project);
                    addDetail(record, columnName, " - File " + oldFile.getName() + " deleted", oldFile, null);
                }
            }
            addProjectSecurity(record, project, READ_PRIV_ID);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }
    
    private boolean auditFileDeletion(CaArrayFile file) {
        FileStatus fileStatus = file.getFileStatus();
        if (FileStatus.SUPPLEMENTAL.equals(fileStatus)) {
            return true;
        } else if (FileStatus.IMPORTED.equals(fileStatus) || FileStatus.IMPORTED_NOT_PARSED.equals(fileStatus)) {
            FileType fileType = file.getFileType();
            FileCategory fileCat = fileType == null ? null : fileType.getCategory();
            return FileCategory.DERIVED_DATA.equals(fileCat) || FileCategory.RAW_DATA.equals(fileCat);
        }
        return false;
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logGroup(AuditLogRecord record, Group entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("groupName".equals(property)) {
            // there is no group.users event on insert so we'll log new users here.
            if (record.getType() == AuditType.INSERT) {
                addDetail(record, columnName, "Group " + newVal + " created", oldVal, newVal);
                if (entity.getUsers() != null) {
                    for (Object u : entity.getUsers()) {
                        String msg = "User " + ((User) u).getLoginName() + " added to group " + entity.getGroupName();
                        addDetail(record, columnName, msg, null, u);
                    }
                }
            } else if (record.getType() == AuditType.UPDATE) {
                String msg = "Group name changed from " + oldVal + " to " + newVal;
                addDetail(record, columnName, msg, oldVal, newVal);
            }
        } else if ("users".equals(property)) {
            Collection<User> tmp = CollectionUtils.subtract((Set<User>) oldVal, (Set<User>) newVal);
            for (User u : tmp) {
                String msg = "User " + u.getLoginName() + " removed from group " + entity.getGroupName();
                addDetail(record, columnName, msg, u, null);
            }
            tmp = CollectionUtils.subtract((Set<User>) newVal, (Set<User>) oldVal);
            for (User u : tmp) {
                String msg = "User " + u.getLoginName() + " added to group " + entity.getGroupName();
                addDetail(record, columnName, msg, null, u);
            }
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logCollaboratorGroup(AuditLogRecord record, CollaboratorGroup entity, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("ownerId".equals(property)) {
            String msg = "Collaborator Group " + entity.getGroup().getGroupName() + " owner set to "
                    + entity.getOwner().getLoginName();
            addDetail(record, columnName, msg, oldVal, newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }
    
    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logExperiment(AuditLogRecord record, Experiment experiment, String property, 
            String columnName, Object oldVal, Object newVal) {
        Project project = experiment.getProject();
        if ("samples".equals(property)) {
            Set<Long> oldIds = new HashSet<Long>();
            Set<Long> newIds = new HashSet<Long>();
            for (Sample s : (Set<Sample>) newVal) {
                newIds.add(s.getId());
            }
            for (Sample s : (Set<Sample>) oldVal) {
                oldIds.add(s.getId());
                if (!newIds.contains(s.getId())) {
                    addExperimentDetail(record, columnName, project);
                    addDetail(record, columnName, " - Sample " + s.getName() + " deleted", s, null);
                }
            }
            for (Sample s : (Set<Sample>) newVal) {
                if (!oldIds.contains(s.getId())) {
                    addExperimentDetail(record, columnName, project);
                    addDetail(record, columnName, " - Sample " + s.getName() + " added", null, s);
                }
            }
            addProjectSecurity(record, project, READ_PRIV_ID);
        } else if (EXP_PROP_MAP.keySet().contains(property)) {
            addExperimentDetail(record, columnName, project);
            addDetail(record, columnName, String.format(" - %s updated", EXP_PROP_MAP.get(property)), oldVal, newVal);
            addProjectSecurity(record, project, READ_PRIV_ID);
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logCaArrayFile(AuditLogRecord record, CaArrayFile file, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("status".equals(property)
                && FileStatus.valueOf((String) newVal) == FileStatus.SUPPLEMENTAL) {
            addExperimentDetail(record, columnName, file.getProject());
            addDetail(record, columnName, " - Supplemental File " + file.getName() + " added", oldVal, newVal);
            addProjectSecurity(record, file.getProject(), READ_PRIV_ID);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logArrayData(AuditLogRecord record, AbstractArrayData ad, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("hybridizations".equals(property)) {
            Set<Hybridization> oldHybs = oldVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) oldVal;
            Set<Hybridization> newHybs = newVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) newVal;
            if (newHybs.size() > oldHybs.size()) {
                CaArrayFile file = ad.getDataFile();
                addExperimentDetail(record, columnName, file.getProject());
                addDetail(record, columnName, " - Data File " + file.getName() + " added", null, newVal);
                addProjectSecurity(record, file.getProject(), READ_PRIV_ID);
            }
        }
    }
    
    private void logAccessProfileSamples(AuditLogRecord record, AccessProfile entity, String columnName,
            Map<Sample, SampleSecurityLevel> o, Map<Sample, SampleSecurityLevel> n) {
        
        // header message.
        String pname = entity.getProject().getExperiment().getTitle();
        StringBuffer sb = new StringBuffer("Access to samples on experiment ");
        sb.append(pname);
        if (entity.isPublicProfile()) {
            sb.append("'s Public Access Profile");
        } else {
            sb.append("'s Access Profile to Group ")
                    .append(entity.getGroup().getGroup().getGroupName());
        }
        AuditLogDetail detail = addDetail(record, columnName, sb.toString());
        boolean added1Sample = false;

        if (o != null) {
            for (Map.Entry<Sample, SampleSecurityLevel> e : o.entrySet()) {
                SampleSecurityLevel v = n.get(e.getKey());
                if (v != null && v != e.getValue()) {
                    StringBuffer m = new StringBuffer(" - Access to sample ");
                    m.append(e.getKey().getName()).append(" changed from ");
                    m.append(e.getValue()).append(" to ").append(v);
                    detail = addDetail(record, columnName, m.toString(), e.getValue(), v);
                    added1Sample = true;
                }
            }
        }

        for (Map.Entry<Sample, SampleSecurityLevel> e : n.entrySet()) {
            // dont log transitions from null to NONE, as they occure when transitioning to a SELECTIVE state.
            if ((o == null || !o.containsKey(e.getKey())) && e.getValue() != SampleSecurityLevel.NONE) {
                String msg = " - Access on sample " + e.getKey().getName() + " set to " + e.getValue();
                detail = addDetail(record, columnName, msg, null, e.getValue());
                added1Sample = true;
            }
        }
        if (added1Sample) {
            addProjectSecurity(record, entity.getProject(), PERMISSIONS_PRIV_ID);
        } else {
            // no need for a header is nothing was added.
            record.getDetails().remove(detail);
        }
    }
    
    private void addExperimentDetail(AuditLogRecord record, String attribute, Project project) {
        if (record.getDetails().size() == 0) {
            addDetail(record, attribute, "Experiment " + project.getExperiment().getTitle() + ":");
            if (!StringUtils.isEmpty(project.getImportDescription())) {
                addDetail(record, attribute, "Import Description: " + project.getImportDescription());
            }
        }
    }
    
    private AuditLogDetail addDetail(AuditLogRecord record, String attribute, String message) {
        return addDetail(record, attribute, message, null, null);
    }
    
    private AuditLogDetail addDetail(AuditLogRecord record, String attribute, String message,
            Object oldVal, Object newVal) {
        AuditLogDetail detail = new AuditLogDetail(record, attribute, null, null);
        if (oldVal != null || newVal != null) {
            super.processDetail(detail, oldVal, newVal);
        }
        detail.setMessage(message);
        record.getDetails().add(detail);
        return detail;
    }
}
