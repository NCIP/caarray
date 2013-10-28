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

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

/**
 * @author wcheng
 *
 */
public class CollaboratorGroupHandlerTest extends AbstractAuditHandlerTest<CollaboratorGroup> {

    @Override
    protected void setupHandler() {
        handler = new CollaboratorGroupHandler(processor);
    }

    @Override
    protected void setupEntity() {
        Group group = new Group();
        group.setGroupName("TestGroup");
        User owner = new User();
        owner.setLoginName("testUser");
        entity = new CollaboratorGroup(group, owner);
    }

    @Test
    public void changeOwner() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "ownerId"));
        verify(processor).addDetail(record, "ownerId", "Collaborator Group TestGroup owner set to testUser",
                OLD_VALUE, NEW_VALUE);

    }
}
