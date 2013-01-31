//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.dao.stub.CollaboratorGroupDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for service.
 */
@SuppressWarnings("PMD")
public class PermissionsManagementServiceTest extends AbstractCaarrayTest {

    private static final String TEST = "test";
    private PermissionsManagementService permissionsManagementService;
    private final GenericDataServiceStub genericDataServiceStub = new GenericDataServiceStub();
    private final DaoFactoryStub daoFactoryStub = new DaoFactoryStub();

    @Before
    public void setup() {
        PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        bean.setGenericDataService(this.genericDataServiceStub);
        bean.setDaoFactory(this.daoFactoryStub);

        this.permissionsManagementService = bean;
    }

    @Test
    public void testDelete() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.delete(created);
        assertEquals(created, this.genericDataServiceStub.getDeletedObject());
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteException() throws CSTransactionException {
        CollaboratorGroup cg = new CollaboratorGroup(new Group(), new User());
        cg.getOwner().setLoginName("anotheruser");
        this.permissionsManagementService.delete(cg);
    }

    @Test
    public void testGetAll() {
        CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        int count = stub.getNumGetAllCalls();
        this.permissionsManagementService.getCollaboratorGroups();
        assertEquals(count + 1, stub.getNumGetAllCalls());
    }

    @Test
    public void testCreate() throws CSException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        assertEquals(created, stub.getSavedObject());
    }

    @Test(expected = CSException.class)
    public void testCreateException() throws CSException  {
        this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.create(TEST);
    }

    @Test
    public void testAddAndRemoveUsers() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        List<String> toAdd = new ArrayList<String>();
        Long anonId = SecurityUtils.getAnonymousUser().getUserId();
        toAdd.add(anonId.toString());
        toAdd.add("3");
        toAdd.add("4");
        this.permissionsManagementService.addUsers(created, toAdd);
        // gymnastics here due to auth manager being it's own session
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        Group g =  (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        User u1 = (User) HibernateUtil.getCurrentSession().load(User.class, anonId);
        User u2 = (User) HibernateUtil.getCurrentSession().load(User.class, 3L);
        User u3 = (User) HibernateUtil.getCurrentSession().load(User.class, 4L);
        Hibernate.initialize(u1.getGroups());
        assertFalse(u1.getGroups().contains(g));
        assertTrue(u2.getGroups().contains(g));
        assertTrue(u2.getGroups().contains(g));
        tx.commit();

        List<String> toRemove = new ArrayList<String>();
        toRemove.add("3");
        this.permissionsManagementService.removeUsers(created, toRemove);
        // go the other way or remove - make sure groups are set correctly
        tx = HibernateUtil.getCurrentSession().beginTransaction();
        HibernateUtil.getCurrentSession().refresh(u1);
        HibernateUtil.getCurrentSession().refresh(u2);
        HibernateUtil.getCurrentSession().refresh(u3);
        HibernateUtil.getCurrentSession().refresh(g);
        assertFalse(u1.getGroups().contains(g));
        assertFalse(u2.getGroups().contains(g));
        assertTrue(u3.getGroups().contains(g));
        tx.commit();
    }

    @Test
    public void testRename() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        this.permissionsManagementService.rename(created, "test2");
        Group g = (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals("test2", g.getGroupName());
        tx.commit();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddUsersToCollaboratorGroup() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        this.permissionsManagementService.addUsers(created, Arrays.asList("3", "2", "1"));
        Group g = (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals(2, g.getUsers().size());
        for (User u : (Set<User>) g.getUsers()) {
            assertTrue("caarrayuser".equals(u.getLoginName()) || "caarrayadmin".equals(u.getLoginName()));
        }
        assertEquals(TEST, g.getGroupName());
        tx.commit();
    }

    @Test
    public void testAddUsersToAnonymousGroup() throws CSTransactionException, CSObjectNotFoundException {
        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        Predicate anonUserExists = new Predicate() {
            public boolean evaluate(Object o) {
                return ((User) o).getLoginName().equals(SecurityUtils.ANONYMOUS_USERNAME);
            }
         };
         Group g = (Group) HibernateUtil.getCurrentSession().load(Group.class, SecurityUtils.getAnonymousGroup().getGroupId());
         assertTrue(CollectionUtils.exists(g.getUsers(), anonUserExists));
         this.permissionsManagementService.addUsers(SecurityUtils.ANONYMOUS_GROUP, "biostatistician");
         HibernateUtil.getCurrentSession().refresh(g);
         assertTrue(CollectionUtils.exists(g.getUsers(), anonUserExists));
         tx.commit();
    }

    @Test
    public void testGetUsers() {
        List<User> users = this.permissionsManagementService.getUsers(null);
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @After
    public void after() {
        HibernateUtil.enableFilters(false);
        Session s = HibernateUtil.getCurrentSession();
        Transaction tx = s.beginTransaction();
        Iterator<Group> it = s.createQuery("FROM " + Group.class.getName() + " g where g.groupName like '" + TEST + "%'")
                              .list()
                              .iterator();
        if (it.hasNext()) {
            s.delete(it.next());
        }
        tx.commit();
    }
}
