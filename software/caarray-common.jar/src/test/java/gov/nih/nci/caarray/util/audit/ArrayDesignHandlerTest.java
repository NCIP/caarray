//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import gov.nih.nci.caarray.domain.array.ArrayDesign;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public class ArrayDesignHandlerTest extends AbstractAuditHandlerTest<ArrayDesign> {
    
    @Override
    protected void setupHandler() {
        handler = new ArrayDesignHandler(processor);
    }
    
    @Override
    protected void setupEntity() {
        entity = new ArrayDesign();
        entity.setName("TestDesign");
    }

    @Test
    public void testAddArrayDesign() {
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, "description"));
        verify(processor).addDetail(record, "Array Design", "Array Design 'TestDesign' added");
        verify(processor).addDetail(record, "description", " - description added", OLD_VALUE, NEW_VALUE);
    }
    
    @Test
    public void testEditArrayDesign() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "version"));
        verify(processor).addDetail(record, "Array Design", "Array Design 'TestDesign' updated");
        verify(processor).addDetail(record, "version", " - version updated", OLD_VALUE, NEW_VALUE);
    }
    
    @Test
    public void testImportArrayDesign() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        // numberOfFeatures is only updated on import
        assertTrue(logEntity(record, "numberOfFeatures"));
        verify(processor).addDetail(record, "numberOfFeatures", "Array Design 'TestDesign' imported", OLD_VALUE, NEW_VALUE);
    }

    @Test
    public void testDeleteArrayDesign() {
        AuditLogRecord record = createAuditLogRecord(AuditType.DELETE);
        assertTrue(logEntity(record, "name"));
        verify(processor).addDetail(record, "name", "Array Design 'TestDesign' deleted", OLD_VALUE, NEW_VALUE);
    }
}
