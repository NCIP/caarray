//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.audit.AuditLogSecurity;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.audit.AbstractAuditEntityHandler;
import gov.nih.nci.caarray.util.audit.AccessProfileHandler;
import gov.nih.nci.caarray.util.audit.ArrayDataHandler;
import gov.nih.nci.caarray.util.audit.ArrayDesignHandler;
import gov.nih.nci.caarray.util.audit.CaArrayFileHandler;
import gov.nih.nci.caarray.util.audit.CollaboratorGroupHandler;
import gov.nih.nci.caarray.util.audit.ExperimentHandler;
import gov.nih.nci.caarray.util.audit.GroupHandler;
import gov.nih.nci.caarray.util.audit.ProjectHandler;
import gov.nih.nci.caarray.util.audit.ProtocolHandler;
import gov.nih.nci.caarray.util.audit.TermHandler;
import gov.nih.nci.security.authorization.domainobjects.Group;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.DefaultProcessor;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author gax
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveParameterList" })
public class CaArrayAuditLogProcessor extends DefaultProcessor {
    private static final Long SYSADMIN_GROUP_ID = 7L;
    private final Map<AuditLogRecord, Set<AuditLogSecurity>> securityEntries;
    
    private static final Class<?>[] AUDITED_CLASSES = {
        Project.class,
        AccessProfile.class,
        CollaboratorGroup.class,
        Group.class,
        Experiment.class,
        AbstractArrayData.class,
        Organization.class,
        Organism.class,
        Term.class,
        Protocol.class,
        ArrayDesign.class,
        ArrayDesignDetails.class
    };

    private static final Map<Class<?>, Class<? extends AbstractAuditEntityHandler<?>>> PROCESSOR_MAP =
            ImmutableMap.<Class<?>, Class<? extends AbstractAuditEntityHandler<?>>>builder()
            .put(AccessProfile.class, AccessProfileHandler.class)
            .put(AbstractArrayData.class, ArrayDataHandler.class)
            .put(ArrayDesign.class, ArrayDesignHandler.class)
            .put(CaArrayFile.class, CaArrayFileHandler.class)
            .put(CollaboratorGroup.class, CollaboratorGroupHandler.class)
            .put(Experiment.class, ExperimentHandler.class)
            .put(Group.class, GroupHandler.class)
            .put(Project.class, ProjectHandler.class)
            .put(Protocol.class, ProtocolHandler.class)
            .put(Term.class, TermHandler.class)
            .build();

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
    
    private AbstractAuditEntityHandler<?> getHandler(Object entity) {
        try {
            Class<? extends AbstractAuditEntityHandler<?>> handlerClass = PROCESSOR_MAP.get(entity.getClass());
            if (handlerClass == null) {
                for (Class<?> clazz : PROCESSOR_MAP.keySet()) {
                    if (clazz.isAssignableFrom(entity.getClass())) {
                        handlerClass = PROCESSOR_MAP.get(clazz);
                        break;
                    }
                }
            }
            return handlerClass.getDeclaredConstructor(CaArrayAuditLogProcessor.class).newInstance(this);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void processDetail(AuditLogRecord record, Object entity, Serializable key, String property,
            String columnName, Object oldVal, Object newVal) {
        AbstractAuditEntityHandler handler = getHandler(entity);
        if (handler != null && handler.logEntity(record, entity, property, columnName, oldVal, newVal)) {
            addSecurity(AuditLogSecurity.GROUP, SYSADMIN_GROUP_ID, null, record);
        }
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
    
    /**
     * Add project security access for an audit log record.
     * @param record audit log record
     * @param project associated project
     * @param privilegeId privilege
     */
    public void addProjectSecurity(AuditLogRecord record, Project project, Long privilegeId) {
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

    /**
     * Add audit log detail entry for an experiment header.
     * @param record audit log record
     * @param attribute attribute
     * @param project project
     */
    public void addExperimentDetail(AuditLogRecord record, String attribute, Project project) {
        if (record.getDetails().size() == 0) {
            addDetail(record, attribute, "Experiment " + project.getExperiment().getTitle() + ":");
            if (!StringUtils.isEmpty(project.getImportDescription())) {
                addDetail(record, attribute, "Import Description: " + project.getImportDescription());
            }
        }
    }
    
    /**
     * Add audit log detail entry.
     * @param record audit log record
     * @param attribute attribute that changed
     * @param message message
     * @return audit log detail
     */
    public AuditLogDetail addDetail(AuditLogRecord record, String attribute, String message) {
        return addDetail(record, attribute, message, null, null);
    }

    /**
     * Add audit log detail entry.
     * @param record audit log record
     * @param attribute attribute that changed
     * @param message message
     * @param oldVal old value
     * @param newVal new value
     * @return audit log detail
     */
    public AuditLogDetail addDetail(AuditLogRecord record, String attribute, String message,
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
