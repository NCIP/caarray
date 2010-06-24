package gov.nih.nci.caarray.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.staticinjection.CaArrayCommonStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author gax
 */
public class SecurityUtilsTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper; 

    List<Object> garbage = new ArrayList<Object>();
    private Transaction tx;

    @BeforeClass
    public static void beforeClass() {
        SecurityUtils.init();
    }
    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies. 
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
    }
    
    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayCommonStaticInjectionModule(), new CaArrayHibernateHelperModule());
    }

    @Before
    public void before() {
        tx = hibernateHelper.beginTransaction();
    }

    @After
    public void after() {
        try {
            if (tx.isActive()) {
                tx.commit();
            }
        } catch (Exception e) {
            tx.rollback();
        }
        tx = hibernateHelper.beginTransaction();
        for (Object o : garbage) {
            hibernateHelper.getCurrentSession().delete(o);
        }
//        hibernateHelper.getCurrentSession().createQuery("delete FROM " + Group.class.getName() + " g where g.groupName like 'test%'").executeUpdate();
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

    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
