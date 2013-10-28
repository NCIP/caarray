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
import com.google.common.collect.Sets;

import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

/**
 * @author wcheng
 *
 */
public class GroupHandlerTest extends AbstractAuditHandlerTest<Group> {
    private static final String GROUP_NAME = "groupName";
    private static final String TEST_GROUP = "TestGroup";

    @Override
    protected void setupHandler() {
        handler = new GroupHandler(processor);
    }

    @Override
    protected void setupEntity() {
        entity = new Group();
        entity.setGroupName(TEST_GROUP);
    }

    @Test
    public void addGroup() {
        User user1 = new User();
        User user2 = new User();
        user1.setLoginName("user1");
        user2.setLoginName("user2");
        entity.setUsers(Sets.newHashSet(user1, user2));
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, GROUP_NAME, null, TEST_GROUP));
        verify(processor).addDetail(record, GROUP_NAME, "Group TestGroup created", null, TEST_GROUP);
        verify(processor).addDetail(record, GROUP_NAME, "User user1 added to group TestGroup", null, user1);
        verify(processor).addDetail(record, GROUP_NAME, "User user2 added to group TestGroup", null, user2);
    }
    
    @Test
    public void updateGroupName() {
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, GROUP_NAME, "oldName", TEST_GROUP));
        verify(processor).addDetail(record, GROUP_NAME, "Group name changed from oldName to TestGroup",
                "oldName", TEST_GROUP);
    }
    
    @Test
    public void updateGroupMembers() {
        User user1 = new User();
        User user2 = new User();
        user1.setLoginName("user1");
        user2.setLoginName("user2");
        AuditLogRecord record = createAuditLogRecord(AuditType.UPDATE);
        assertTrue(logEntity(record, "users", Sets.newHashSet(user1), Sets.newHashSet(user2)));
        verify(processor).addDetail(record, "users", "User user1 removed from group " + TEST_GROUP, user1, null);
        verify(processor).addDetail(record, "users", "User user2 added to group " + TEST_GROUP, null, user2);
    }
}
