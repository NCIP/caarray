//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;

/**
 * @author wcheng
 *
 */
public class CaArrayFileHandlerTest extends AbstractAuditHandlerTest<CaArrayFile> {

    @Override
    protected void setupHandler() {
        handler = new CaArrayFileHandler(processor);
    }

    @Override
    protected void setupEntity() {
        Project project = new Project();
        project.getExperiment().setTitle("TestExp");

        entity = new CaArrayFile();
        entity.setName("TestFile");
        entity.setProject(project);
    }

    @Test
    public void addSupplementalFile() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "status", FileStatus.UPLOADED.toString(), FileStatus.SUPPLEMENTAL.toString()));
        verify(processor).addExperimentDetail(record, "status", entity.getProject());
        verify(processor).addDetail(record, "status", " - Supplemental File TestFile added",
                FileStatus.UPLOADED.toString(), FileStatus.SUPPLEMENTAL.toString());

    }
}
