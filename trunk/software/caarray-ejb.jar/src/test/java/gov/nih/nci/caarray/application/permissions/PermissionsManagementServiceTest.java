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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.dao.CollaboratorGroupDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.CollaboratorGroupDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Test cases for service.
 */
@SuppressWarnings("PMD")
public class PermissionsManagementServiceTest extends AbstractServiceTest {

    private static final String TEST = "test";
    private PermissionsManagementService permissionsManagementService;
    private final GenericDataServiceStub genericDataServiceStub = new GenericDataServiceStub();
    private final DaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private Transaction tx;

    @Before
    public void setup() {
        PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        bean.setGenericDataService(this.genericDataServiceStub);
        bean.setDaoFactory(this.daoFactoryStub);

        this.permissionsManagementService = bean;

        tx = HibernateUtil.beginTransaction();
    }

    @SuppressWarnings("unchecked")
    @After
    public void after() {
        try {
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().createQuery("delete FROM " + Group.class.getName() + " g where g.groupName like '" + TEST + "%'").executeUpdate();
        tx.commit();
    }

    @Test
    public void testDelete() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.delete(created);
        assertEquals(created, this.genericDataServiceStub.getDeletedObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws CSTransactionException {
        CollaboratorGroup cg = new CollaboratorGroup(new Group(), new User());
        cg.getOwner().setLoginName("anotheruser");
        // only the owner of the group can delete it, so attempting to delete after changing the owner should
        // cause an exception
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
    public void testGetAllForCurrentUser() {
        CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        int count = stub.getNumGetAllForUserCalls();
        this.permissionsManagementService.getCollaboratorGroupsForCurrentUser();
        assertEquals(count + 1, stub.getNumGetAllForUserCalls());
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
        List<Long> toAdd = new ArrayList<Long>();
        Long anonId = SecurityUtils.getAnonymousUser().getUserId();
        toAdd.add(anonId);
        toAdd.add(3L);
        toAdd.add(4L);
        this.permissionsManagementService.addUsers(created, toAdd);
        HibernateUtil.getCurrentSession().flush();
        
        Group g =  (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        User anonUser = (User) HibernateUtil.getCurrentSession().load(User.class, anonId);
        User user3 = (User) HibernateUtil.getCurrentSession().load(User.class, 3L);
        User user4 = (User) HibernateUtil.getCurrentSession().load(User.class, 4L);
        Hibernate.initialize(anonUser.getGroups());
        assertFalse(anonUser.getGroups().contains(g));
        assertTrue(user3.getGroups().contains(g));
        assertTrue(user4.getGroups().contains(g));


        List<Long> toRemove = new ArrayList<Long>();
        toRemove.add(3L);
        this.permissionsManagementService.removeUsers(created, toRemove);
        HibernateUtil.getCurrentSession().flush();

        HibernateUtil.getCurrentSession().refresh(anonUser);
        HibernateUtil.getCurrentSession().refresh(user3);
        HibernateUtil.getCurrentSession().refresh(user4);
        HibernateUtil.getCurrentSession().refresh(g);
        assertFalse(anonUser.getGroups().contains(g));
        assertFalse(user3.getGroups().contains(g));
        assertTrue(user4.getGroups().contains(g));
    }

    @Test
    public void testRename() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
//        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        this.permissionsManagementService.rename(created, "test2");
        Group g = (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals("test2", g.getGroupName());
//        tx.commit();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddUsersToCollaboratorGroup() throws CSTransactionException, CSObjectNotFoundException {
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
//        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
        this.permissionsManagementService.addUsers(created, Arrays.asList(3L, 2L, 1L));
        Group g = (Group) HibernateUtil.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals(2, g.getUsers().size());
        for (User u : (Set<User>) g.getUsers()) {
            assertTrue("caarrayuser".equals(u.getLoginName()) || "caarrayadmin".equals(u.getLoginName()));
        }
        assertEquals(TEST, g.getGroupName());
//        tx.commit();
    }

    @Test
    public void testAddUsersToAnonymousGroup() throws CSTransactionException, CSObjectNotFoundException {
//        Transaction tx = HibernateUtil.getCurrentSession().beginTransaction();
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
//         tx.commit();
    }

    @Test
    public void testGetUsers() {
        Number count = (Number) HibernateUtil.getCurrentSession().createCriteria(User.class).setProjection(Projections.rowCount()).uniqueResult();
        List<User> users = this.permissionsManagementService.getUsers(null);
        assertNotNull(users);
        assertEquals(count.intValue(), users.size());
    }

    @Test
    public void testGetCollaboratorGroupsForOwner() throws Exception {
        User u = UsernameHolder.getCsmUser();
        List<CollaboratorGroup> l = this.permissionsManagementService.getCollaboratorGroupsForOwner(u.getUserId().longValue());
        assertSame(Collections.EMPTY_LIST, l);
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalCollaboratorGroupDaoStub collaboratorGroupDao = new LocalCollaboratorGroupDaoStub();

        @Override
        public CollaboratorGroupDao getCollaboratorGroupDao() {
            return this.collaboratorGroupDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new LocalSearchDaoStub(this.collaboratorGroupDao);
        }
    }

    private static class LocalCollaboratorGroupDaoStub extends CollaboratorGroupDaoStub {

        final HashMap<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();
        PersistentObject lastSaved;
        PersistentObject lastDeleted;
        private Long nextId = 1L;

        @SuppressWarnings("deprecation")
        @Override
        public void save(PersistentObject caArrayObject) {
            if (caArrayObject instanceof CollaboratorGroup) {
                ((CollaboratorGroup) caArrayObject).setId(nextId++);
            }
            super.save(caArrayObject);
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.lastDeleted = caArrayEntity;
            this.savedObjects.remove(caArrayEntity.getId());
        }

        public PersistentObject getLastDeleted() {
            return this.lastDeleted;
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        private final LocalCollaboratorGroupDaoStub collaboratorGroupDao;

        public LocalSearchDaoStub(LocalCollaboratorGroupDaoStub projectDao) {
            this.collaboratorGroupDao = projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            return (T) this.collaboratorGroupDao.savedObjects.get(entityId);

        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
            return (T) this.collaboratorGroupDao.savedObjects.get(entityId);
        }

    }

}
