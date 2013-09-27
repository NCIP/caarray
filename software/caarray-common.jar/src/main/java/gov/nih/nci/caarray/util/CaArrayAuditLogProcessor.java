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
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
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
import com.google.common.collect.Sets;

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
    private static final Map<String, String> EXP_PROPERTIES = new HashMap<String, String>();
    private static final Map<String, String> EXP_COLLECTIONS = new HashMap<String, String>();
    
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
        EXP_PROPERTIES.put("title", "Title");
        EXP_PROPERTIES.put("description", "Description");
        EXP_PROPERTIES.put("assayTypes", "Assay Types");
        EXP_PROPERTIES.put("manufacturer", "Provider");
        EXP_PROPERTIES.put("arrayDesigns", "Array Designs");
        EXP_PROPERTIES.put("organism", "Organism");
        EXP_PROPERTIES.put("experimentDesignTypes", "Experiment Design Types");
        EXP_PROPERTIES.put("designDescription", "Experiment Design Description");
        EXP_PROPERTIES.put("qualityControlTypes", "Quality Control Types");
        EXP_PROPERTIES.put("qualityControlDescription", "Quality Control Description");
        EXP_PROPERTIES.put("replicateTypes", "Replicate Types");
        EXP_PROPERTIES.put("replicateDescription", "Replicate Description");
        EXP_COLLECTIONS.put("experimentContacts", "Contact");
        EXP_COLLECTIONS.put("publications", "Publication");
        EXP_COLLECTIONS.put("factors", "Factor");
        EXP_COLLECTIONS.put("sources", "Source");
        EXP_COLLECTIONS.put("samples", "Sample");
        EXP_COLLECTIONS.put("extracts", "Extract");
        EXP_COLLECTIONS.put("labeledExtracts", "Labeled Extract");
        EXP_COLLECTIONS.put("hybridizations", "Hybridization");
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
        return file.getParent() == null;
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
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logExperiment(AuditLogRecord record, Experiment experiment, String property, 
            String columnName, Object oldVal, Object newVal) {
        Project project = experiment.getProject();
        if (project != null) {
            if (EXP_COLLECTIONS.keySet().contains(property)) {
                addExperimentDetail(record, columnName, project);
                Set<?> addedEntries = Sets.difference(makeSet(newVal), makeSet(oldVal));
                for (Object node : addedEntries) {
                    String msg = String.format(" - %s %s added", EXP_COLLECTIONS.get(property), getObjectName(node));
                    addDetail(record, columnName, msg, null, node);
                }
                Set<?> deletedEntries = Sets.difference(makeSet(oldVal), makeSet(newVal));
                for (Object node : deletedEntries) {
                    String msg = String.format(" - %s %s deleted", EXP_COLLECTIONS.get(property), getObjectName(node));
                    addDetail(record, columnName, msg, node, null);
                }
                addProjectSecurity(record, project, READ_PRIV_ID);
            } else if (EXP_PROPERTIES.keySet().contains(property)) {
                addExperimentDetail(record, columnName, project);
                String msg = String.format(" - %s updated", EXP_PROPERTIES.get(property));
                addDetail(record, columnName, msg, oldVal, newVal);
                addProjectSecurity(record, project, READ_PRIV_ID);
            }
        }
    }
    
    private Set<?> makeSet(Object o) {
        if (o instanceof Collection) {
            Set<?> result = Sets.newHashSet();
            result.addAll((Collection) o);
            return result;
        } else {
            return Sets.newHashSet(o);
        }
    }
    
    private String getObjectName(Object o) {
        if (o instanceof AbstractExperimentDesignNode) {
            return ((AbstractExperimentDesignNode) o).getName();
        } else if (o instanceof Factor) {
            return ((Factor) o).getName();
        } else if (o instanceof ExperimentContact) {
            return ((ExperimentContact) o).getContact().getName();
        } else if (o instanceof Publication) {
            return ((Publication) o).getTitle();
        }
        return "";
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
