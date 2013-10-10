//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public class TermHandlerTest extends AbstractAuditHandlerTest<Term> {
    
    @Override
    protected void setupHandler() {
        handler = new TermHandler(processor);
    }
    
    @Override
    protected void setupEntity() {
        entity = new Term();
        entity.setValue("TestTerm");
    }

    @Test
    public void testAddTerm() {
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, "description"));
        verify(processor).addDetail(record, "Term", "Term 'TestTerm' added");
        verify(processor).addDetail(record, "description", " - description added", OLD_VALUE, NEW_VALUE);
    }
    
    @Test
    public void testUpdateTerm() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "url"));
        verify(processor).addDetail(record, "Term", "Term 'TestTerm' updated");
        verify(processor).addDetail(record, "url", " - url updated", OLD_VALUE, NEW_VALUE);
    }
}
