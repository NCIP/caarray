//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import gov.nih.nci.caarray.domain.protocol.Protocol;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public class ProtocolHandlerTest extends AbstractAuditHandlerTest<Protocol> {

    @Override
    protected void setupHandler() {
        handler = new ProtocolHandler(processor);
    }
    
    @Override
    protected void setupEntity() {
        entity = new Protocol("TestProtocol", null, null);
    }
    
    @Test
    public void testAddProtocol() {
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, "description"));
        verify(processor).addDetail(record, "Protocol", "Protocol 'TestProtocol' added");
        verify(processor).addDetail(record, "description", " - description added", OLD_VALUE, NEW_VALUE);
    }
    
    @Test
    public void testUpdateProtocol() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "url"));
        verify(processor).addDetail(record, "Protocol", "Protocol 'TestProtocol' updated");
        verify(processor).addDetail(record, "url", " - url updated", OLD_VALUE, NEW_VALUE);
    }
}
