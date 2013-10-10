//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.security.authorization.domainobjects.Group;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * @author wcheng
 *
 */
public class CaArrayAuditLogProcessorTest {
    private final CaArrayAuditLogProcessor processor = new CaArrayAuditLogProcessor();
    private static final String DESCRIPTION = "description";
    private static final String VALUE = "value";
    private static final String NAME = "name";
    
    @Test
    public void auditableEntities() {
        assertTrue(processor.isAuditableEntity(new Project()));
        assertTrue(processor.isAuditableEntity(new AccessProfile()));
        assertTrue(processor.isAuditableEntity(new CollaboratorGroup()));
        assertTrue(processor.isAuditableEntity(new Group()));
        assertTrue(processor.isAuditableEntity(new Experiment()));
        assertTrue(processor.isAuditableEntity(new RawArrayData()));
        assertTrue(processor.isAuditableEntity(new Organization()));
        assertTrue(processor.isAuditableEntity(new Organism()));
        assertTrue(processor.isAuditableEntity(new Term()));
        assertTrue(processor.isAuditableEntity(new Protocol("protocol", null, null)));
        CaArrayFile file = new CaArrayFile();
        assertFalse(processor.isAuditableEntity(file));
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        assertTrue(processor.isAuditableEntity(file));
    }
    
    @Test
    public void processTerm() {
        AuditLogRecord addRecord = createRecord(AuditType.INSERT);
        Term tissueSite = new Term();
        tissueSite.setValue("Brain");
        tissueSite.setDescription("desc");
        processor.processDetail(addRecord, tissueSite, null, VALUE, VALUE, null, "Brain");
        processor.processDetail(addRecord, tissueSite, null, DESCRIPTION, DESCRIPTION, null, "desc");
        Set<String> messages = getMessages(addRecord);
        assertEquals(3, messages.size());
        assertTrue(messages.contains("Term 'Brain' added"));
        assertTrue(messages.contains(" - value added"));
        assertTrue(messages.contains(" - description added"));
        
        AuditLogRecord updateRecord = createRecord(AuditType.UPDATE);
        tissueSite.setDescription("desc2");
        processor.processDetail(updateRecord, tissueSite, null, DESCRIPTION, DESCRIPTION, "desc", "desc2");
        messages = getMessages(updateRecord);
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Term 'Brain' updated"));
        assertTrue(messages.contains(" - description updated"));
    }

    @Test
    public void processProtocol() {
        AuditLogRecord addRecord = createRecord(AuditType.INSERT);
        Protocol tissueSite = new Protocol("Test Protocol", null, null);
        tissueSite.setDescription("blah");
        processor.processDetail(addRecord, tissueSite, null, NAME, NAME, null, "Brain");
        processor.processDetail(addRecord, tissueSite, null, DESCRIPTION, DESCRIPTION, null, "blah");
        Set<String> messages = getMessages(addRecord);
        assertEquals(3, messages.size());
        assertTrue(messages.contains("Protocol 'Test Protocol' added"));
        assertTrue(messages.contains(" - name added"));
        assertTrue(messages.contains(" - description added"));
        
        AuditLogRecord updateRecord = createRecord(AuditType.UPDATE);
        tissueSite.setDescription("updated");
        processor.processDetail(updateRecord, tissueSite, null, DESCRIPTION, DESCRIPTION, "blah", "updated");
        messages = getMessages(updateRecord);
        assertEquals(2, messages.size());
        assertTrue(messages.contains("Protocol 'Test Protocol' updated"));
        assertTrue(messages.contains(" - description updated"));
    }

    private AuditLogRecord createRecord(AuditType type) {
        return new AuditLogRecord(type, "test", 1L, "caarrayuser", new Date());
    }
    
    private Set<String> getMessages(AuditLogRecord record) {
        Set<String> messages = new HashSet<String>();
        for (AuditLogDetail detail : record.getDetails()) {
            messages.add(detail.getMessage());
        }
        return messages;
    }
}
