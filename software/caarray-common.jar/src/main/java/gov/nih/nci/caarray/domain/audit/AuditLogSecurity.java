//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.audit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * This object maps access to an audit log record based on project permissions.
 * @author wcheng
 */
@Entity
@Table(name = "audit_log_security")
public class AuditLogSecurity {
    /**
     * Security based on project permissions.
     */
    public static final String PROJECT = "Project";
    /**
     * Security based on group membership.
     */
    public static final String GROUP = "Group";
    
    private Long id;
    private String entityName;
    private Long entityId;
    private Long privilegeId;
    private AuditLogRecord record;

    /**
     * For UI / Hibernate Usage only.
     */
    public AuditLogSecurity() {
        // intentionally empty
    }
    
    /**
     * Constructor.
     * @param entityName the type of entity this security entry is based on
     * @param entityId the id of the entity
     * @param privilegeId the privilege that restricts access to the record
     * @param record the audit log record this applies to
     */
    public AuditLogSecurity(String entityName, Long entityId, Long privilegeId, AuditLogRecord record) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.privilegeId = privilegeId;
        this.record = record;
    }
    
    /**
     * Returns the id.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @deprecated should only be used by castor and hibernate
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the privilegeId
     */
    public Long getPrivilegeId() {
        return privilegeId;
    }

    /**
     * @param privilegeId the privilegeId to set
     */
    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * @return the audit log record to which this security object applies.
     */
    @NotNull
    @OneToOne
    @JoinColumn(name = "record")
    @ForeignKey(name = "audit_security_record_fk")
    public AuditLogRecord getRecord() {
        return record;
    }

    /**
     * @param record the audit log record to set
     */
    public void setRecord(AuditLogRecord record) {
        this.record = record;
    }
}
