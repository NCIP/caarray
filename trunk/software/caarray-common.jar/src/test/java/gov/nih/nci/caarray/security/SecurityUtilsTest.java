//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class SecurityUtilsTest {

    List<Object> garbage = new ArrayList<Object>();
    private Transaction tx;

    @BeforeClass
    public static void beforeClass() {
        SecurityUtils.init();
    }

    @Before
    public void before() {
        tx = HibernateUtil.beginTransaction();
    }

    @SuppressWarnings("unchecked")
    @After
    public void after() {
        try {
            if (tx.isActive()) {
                tx.commit();
            }
        } catch (Exception e) {
            tx.rollback();
        }
        tx = HibernateUtil.beginTransaction();
        for (Object o : garbage) {
            HibernateUtil.getCurrentSession().delete(o);
        }
//        HibernateUtil.getCurrentSession().createQuery("delete FROM " + Group.class.getName() + " g where g.groupName like 'test%'").executeUpdate();
        tx.commit();
    }

    /**
     * Test of createGroup method, of class SecurityUtils.
     */
    @Test
    public void testCreateGroup() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.createGroup(group);
        garbage.add(group);

        tx.commit();

        Group g = SecurityUtils.getAuthorizationManager().getGroupById(group.getGroupId().toString());
        assertEquals(group.getGroupName(), g.getGroupName());
        assertEquals(group.getGroupDesc(), g.getGroupDesc());
    }

    @Test
    public void testAssignUsersToGroup() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.createGroup(group);
        garbage.add(group);

        User u = new User();
        u.setLastName("ll");
        u.setFirstName("ff");
        u.setLoginName("lll");
        SecurityUtils.getAuthorizationManager().createUser(u);
        garbage.add(u);

        Long groupId = group.getGroupId();
        Set<Long> userIds = Collections.singleton(u.getUserId());
        SecurityUtils.assignUsersToGroup(groupId, userIds);

        tx.commit();

        Set<User> members = SecurityUtils.getAuthorizationManager().getUsers(group.getGroupId().toString());
        assertEquals(1L, members.size());
        assertEquals(u.getUserId(), members.iterator().next().getUserId());
    }

    /**
     * Test of removeUserFromGroup method, of class SecurityUtils.
     */
    @Test
    public void testRemoveUserFromGroup() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.getAuthorizationManager().createGroup(group);
        garbage.add(group);

        User u = new User();
        u.setLastName("ll");
        u.setFirstName("ff");
        u.setLoginName("lll");
        SecurityUtils.getAuthorizationManager().createUser(u);
        garbage.add(u);

        SecurityUtils.getAuthorizationManager().assignUsersToGroup(group.getGroupId().toString(), new String[]{u.getUserId().toString()});
        Set<User> members = SecurityUtils.getAuthorizationManager().getUsers(group.getGroupId().toString());
        assertEquals(1L, members.size());
        
        SecurityUtils.removeUserFromGroup(group.getGroupId(), u.getUserId());
        tx.commit();
        
        members = SecurityUtils.getAuthorizationManager().getUsers(group.getGroupId().toString());
        assertTrue(members.isEmpty());
    }

    /**
     * Test of removeGroup method, of class SecurityUtils.
     */
    @Test
    public void testRemoveGroup() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.getAuthorizationManager().createGroup(group);
        SecurityUtils.removeGroup(group.getGroupId());
        tx.commit();
        try {
            SecurityUtils.getAuthorizationManager().getGroupById(group.getGroupId().toString());
            fail("group should be deleted");
        } catch (CSObjectNotFoundException e) {
        }
    }

    /**
     * Test of getUsers method, of class SecurityUtils.
     */
    @Test
    public void testGetUsers() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.getAuthorizationManager().createGroup(group);
        garbage.add(group);

        User u = new User();
        u.setLastName("ll");
        u.setFirstName("ff");
        u.setLoginName("lll");
        SecurityUtils.getAuthorizationManager().createUser(u);
        garbage.add(u);

        SecurityUtils.getAuthorizationManager().assignUsersToGroup(group.getGroupId().toString(), new String[]{u.getUserId().toString()});
        

        Set<User> s = SecurityUtils.getUsers(group.getGroupId());
        assertEquals(1L, s.size());
        assertEquals(u.getUserId(), s.iterator().next().getUserId());
    }

    /**
     * Test of findGroupByName method, of class SecurityUtils.
     */
    @Test
    public void testFindGroupByName() throws Exception {
        Group group = new Group();
        group.setGroupName("testGroup");
        group.setGroupDesc("some desc");
        SecurityUtils.getAuthorizationManager().createGroup(group);
        garbage.add(group);

        Group g = SecurityUtils.findGroupByName(group.getGroupName());
        assertEquals(group.getGroupId(), g.getGroupId());
    }
}
