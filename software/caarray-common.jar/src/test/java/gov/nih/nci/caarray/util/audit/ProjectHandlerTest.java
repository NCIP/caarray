//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Set;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.google.common.collect.Sets;

/**
 * @author wcheng
 *
 */
public class ProjectHandlerTest extends AbstractAuditHandlerTest<Project> {
    private static final String LOCKED = "locked";
    
    @Override
    protected void setupHandler() {
        handler = new ProjectHandler(processor);
    }
    
    @Override
    protected void setupEntity() {
        entity = new Project();
        entity.getExperiment().setTitle("TestExp");
    }

    @Test
    public void testNewProject() {
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, LOCKED, null, false));
        verify(processor).addDetail(record, LOCKED, "Experiment TestExp Unlocked when created", null, false);
    }
    
    @Test
    public void testLockProject() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, LOCKED, false, true));
        verify(processor).addDetail(record, LOCKED, "Experiment TestExp Locked", false, true);
    }
    
    @Test
    public void testDeleteFiles() {
        CaArrayFile file1 = createDummyFile(1);
        CaArrayFile file2 = createDummyFile(2);
        CaArrayFile file3 = createDummyFile(3);
        Set<CaArrayFile> oldFiles = Sets.newHashSet(file1, file2);
        Set<CaArrayFile> newFiles = Sets.newHashSet(file2, file3);
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "files", oldFiles, newFiles));
        verify(processor).addExperimentDetail(record, "files", entity);
        verify(processor).addDetail(record, "files", " - File file1 deleted", file1, null);
    }
    
    @Test
    public void testInvalidProperty() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertFalse(logEntity(record, "invalid"));
    }
    
    @Test
    public void testDeleteProject() {
        AuditLogRecord record = createAuditLogRecord(AuditType.DELETE);
        assertTrue(logEntity(record, "experiment"));
        verify(processor).addDetail(record, "experiment", "Experiment 'TestExp' deleted", OLD_VALUE, NEW_VALUE);
    }
    
    @SuppressWarnings("deprecation")
    private CaArrayFile createDummyFile(int id) {
        CaArrayFile file = new CaArrayFile();
        file.setName("file" + id);
        file.setId(Long.valueOf(id));
        return file;
    }
}
