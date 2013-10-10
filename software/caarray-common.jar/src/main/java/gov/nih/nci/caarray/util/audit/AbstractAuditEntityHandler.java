//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * 
 * @author wcheng
 * @param <T> entity type
 */
@SuppressWarnings("PMD.ExcessiveParameterList")
public abstract class AbstractAuditEntityHandler<T> {
    static final Long READ_PRIV_ID = 3L;
    static final Long PERMISSIONS_PRIV_ID = 8L;
    static final Logger LOG = Logger.getLogger(CaArrayAuditLogProcessor.class);

    private final CaArrayAuditLogProcessor processor;
    
    /**
     * @param processor audit log processor
     */
    public AbstractAuditEntityHandler(CaArrayAuditLogProcessor processor) {
        this.processor = processor;
    }
    
    /**
     * @return the processor
     */
    protected CaArrayAuditLogProcessor getProcessor() {
        return processor;
    }

    /**
     * Add an entry to the audit log record for the given object.
     * @param record Audit log record
     * @param entity object being audited
     * @param property property of object being audited
     * @param columnName column name
     * @param oldVal old value
     * @param newVal new value
     * @return true if log entry was added
     */
    public boolean logEntity(AuditLogRecord record, T entity, String property, String columnName,
            Object oldVal, Object newVal) {
        switch (record.getType()) {
            case INSERT:
                return logAdd(record, entity, property, columnName, oldVal, newVal);
            case UPDATE:
                return logUpdate(record, entity, property, columnName, oldVal, newVal);
            case DELETE:
                return logDelete(record, entity, property, columnName, oldVal, newVal);
            default:
                return false;
        }
    }

    /**
     * Log a deletion of an entity.
     * @param record audit log record
     * @param entity entity
     * @param property property
     * @param columnName column
     * @param oldVal old value
     * @param newVal new value
     * @return true if a log entry was added
     */
    protected boolean logDelete(AuditLogRecord record, T entity, String property, String columnName,
            Object oldVal, Object newVal) {
        return false;
    }
    
    /**
     * Log an addition of an entity.
     * @param record audit log record
     * @param entity entity
     * @param property property
     * @param columnName column
     * @param oldVal old value
     * @param newVal new value
     * @return true if a log entry was added
     */
    protected boolean logAdd(AuditLogRecord record, T entity, String property, String columnName,
            Object oldVal, Object newVal) {
        return logAddOrUpdate(record, entity, property, columnName, oldVal, newVal);
    }
    
    /**
     * Log an update to an entity.
     * @param record audit log record
     * @param entity entity
     * @param property property
     * @param columnName column
     * @param oldVal old value
     * @param newVal new value
     * @return true if a log entry was added
     */
    protected boolean logUpdate(AuditLogRecord record, T entity, String property, String columnName,
            Object oldVal, Object newVal) {
        return logAddOrUpdate(record, entity, property, columnName, oldVal, newVal);
    }
    
    /**
     * Log an addition or an update.
     * @param record audit log record
     * @param entity entity
     * @param property property
     * @param columnName column
     * @param oldVal old value
     * @param newVal new value
     * @return true if a log entry was added
     */
    protected boolean logAddOrUpdate(AuditLogRecord record, T entity, String property, String columnName,
            Object oldVal, Object newVal) {
        return false;
    }
}
