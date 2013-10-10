//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import java.util.Date;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public abstract class AbstractAuditHandlerTest<T> {
    static final String OLD_VALUE = "oldValue";
    static final String NEW_VALUE = "newValue";
    
    
    @Mock
    CaArrayAuditLogProcessor processor;
    AbstractAuditEntityHandler<T> handler;
    T entity;
    
    @Before
    public void setupProcessor() {
        MockitoAnnotations.initMocks(this);
        setupHandler();
        setupEntity();
    }
    
    protected abstract void setupHandler();

    protected abstract void setupEntity();
    
    protected AuditLogRecord createAuditLogRecord(AuditType type) {
        return new AuditLogRecord(type, "entity", 1L, "user", new Date());
    }
    
    protected boolean logEntity(AuditLogRecord record, String property) {
        return logEntity(record, property, OLD_VALUE, NEW_VALUE);
    }

    protected boolean logEntity(AuditLogRecord record, String property, Object oldVal, Object newVal) {
        return handler.logEntity(record, entity, property, property, oldVal, newVal);
    }
}
