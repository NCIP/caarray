//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
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
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test cases for service.
 */
public class PermissionsManagementServiceTest extends AbstractServiceTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    private static final String TEST = "test";
    private PermissionsManagementService permissionsManagementService;
    private final GenericDataServiceStub genericDataServiceStub = new GenericDataServiceStub();
    private final DaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private Transaction tx;

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
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                requestStaticInjection(gov.nih.nci.caarray.security.AuthorizationManagerExtensions.class);
                requestStaticInjection(gov.nih.nci.caarray.security.SecurityUtils.class);
                requestStaticInjection(gov.nih.nci.caarray.domain.permissions.CollaboratorGroup.class);
            }
        });
    }

    @Before
    public void setup() {
        final PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        bean.setHibernateHelper(hibernateHelper);
        bean.setCollaboratorGroupDao(this.daoFactoryStub.getCollaboratorGroupDao());
        bean.setSearchDao(this.daoFactoryStub.getSearchDao());
        bean.setGenericDataService(this.genericDataServiceStub);

        this.permissionsManagementService = bean;

        this.tx = hibernateHelper.beginTransaction();
    }

    @After
    public void after() {
        try {
            this.tx.commit();
        } catch (final Exception e) {
            this.tx.rollback();
        }
        this.tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession()
                .createQuery("delete FROM " + Group.class.getName() + " g where g.groupName like '" + TEST + "%'")
                .executeUpdate();
        this.tx.commit();
    }

    @Test
    public void testDelete() throws CSTransactionException, CSObjectNotFoundException {
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.delete(created);
        assertEquals(created, this.genericDataServiceStub.getDeletedObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteException() throws CSTransactionException {
        final CollaboratorGroup cg = new CollaboratorGroup(new Group(), new User());
        cg.getOwner().setLoginName("anotheruser");
        // only the owner of the group can delete it, so attempting to delete after changing the owner should
        // cause an exception
        this.permissionsManagementService.delete(cg);
    }

    @Test
    public void testGetAll() {
        final CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        final int count = stub.getNumGetAllCalls();
        this.permissionsManagementService.getCollaboratorGroups();
        assertEquals(count + 1, stub.getNumGetAllCalls());
    }

    @Test
    public void testGetAllForCurrentUser() {
        final CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        final int count = stub.getNumGetAllForUserCalls();
        this.permissionsManagementService.getCollaboratorGroupsForCurrentUser();
        assertEquals(count + 1, stub.getNumGetAllForUserCalls());
    }

    @Test
    public void testCreate() throws CSException {
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        final CollaboratorGroupDaoStub stub = (CollaboratorGroupDaoStub) this.daoFactoryStub.getCollaboratorGroupDao();
        assertEquals(created, stub.getSavedObject());
    }

    @Test(expected = CSException.class)
    public void testCreateException() throws CSException {
        this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.create(TEST);
    }

    @Test
    public void testAddAndRemoveUsers() throws CSTransactionException, CSObjectNotFoundException {
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        final List<Long> toAdd = new ArrayList<Long>();
        final Long anonId = SecurityUtils.getAnonymousUser().getUserId();
        toAdd.add(anonId);
        toAdd.add(3L);
        toAdd.add(4L);
        this.permissionsManagementService.addUsers(created, toAdd);
        hibernateHelper.getCurrentSession().flush();

        final Group g = (Group) hibernateHelper.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        final User anonUser = (User) hibernateHelper.getCurrentSession().load(User.class, anonId);
        final User user3 = (User) hibernateHelper.getCurrentSession().load(User.class, 3L);
        final User user4 = (User) hibernateHelper.getCurrentSession().load(User.class, 4L);
        Hibernate.initialize(anonUser.getGroups());
        assertFalse(anonUser.getGroups().contains(g));
        assertTrue(user3.getGroups().contains(g));
        assertTrue(user4.getGroups().contains(g));

        final List<Long> toRemove = new ArrayList<Long>();
        toRemove.add(3L);
        this.permissionsManagementService.removeUsers(created, toRemove);
        hibernateHelper.getCurrentSession().flush();

        hibernateHelper.getCurrentSession().refresh(anonUser);
        hibernateHelper.getCurrentSession().refresh(user3);
        hibernateHelper.getCurrentSession().refresh(user4);
        hibernateHelper.getCurrentSession().refresh(g);
        assertFalse(anonUser.getGroups().contains(g));
        assertFalse(user3.getGroups().contains(g));
        assertTrue(user4.getGroups().contains(g));
    }

    @Test
    public void testRename() throws CSTransactionException, CSObjectNotFoundException {
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.rename(created, "test2");
        final Group g = (Group) hibernateHelper.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals("test2", g.getGroupName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddUsersToCollaboratorGroup() throws CSTransactionException, CSObjectNotFoundException {
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        this.permissionsManagementService.addUsers(created, Arrays.asList(3L, 2L, 1L));
        final Group g = (Group) hibernateHelper.getCurrentSession().load(Group.class, created.getGroup().getGroupId());
        assertEquals(2, g.getUsers().size());
        for (final User u : (Set<User>) g.getUsers()) {
            assertTrue("caarrayuser".equals(u.getLoginName()) || "caarrayadmin".equals(u.getLoginName()));
        }
        assertEquals(TEST, g.getGroupName());
    }

    /**
     * @see http://gforge.nci.nih.gov/tracker/index.php?func=detail&aid=12306
     */
    @Test
    public void testAddUserKeepsAnonUserInAnonGroup() throws CSTransactionException, CSObjectNotFoundException {
        final Predicate anonUserExists = new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((User) o).getLoginName().equals(SecurityUtils.ANONYMOUS_USERNAME);
            }
        };
        final Group g =
                (Group) hibernateHelper.getCurrentSession().load(Group.class,
                        SecurityUtils.findGroupByName(SecurityUtils.ANONYMOUS_GROUP).getGroupId());
        assertTrue(CollectionUtils.exists(g.getUsers(), anonUserExists));
        this.permissionsManagementService.addUsers(SecurityUtils.ANONYMOUS_GROUP, "biostatistician");
        hibernateHelper.getCurrentSession().refresh(g);
        assertTrue(CollectionUtils.exists(g.getUsers(), anonUserExists));
    }

    @Test
    public void testGetUsers() {
        final Number count =
                (Number) hibernateHelper.getCurrentSession().createCriteria(User.class)
                        .setProjection(Projections.rowCount()).uniqueResult();
        final List<User> users = this.permissionsManagementService.getUsers(null);
        assertNotNull(users);
        assertEquals(count.intValue(), users.size());
    }

    @Test
    public void testGetCollaboratorGroupsForOwner() throws Exception {
        final User u = CaArrayUsernameHolder.getCsmUser();
        final List<CollaboratorGroup> l =
                this.permissionsManagementService.getCollaboratorGroupsForOwner(u.getUserId().longValue());
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
        private Long nextId = 1L;

        @SuppressWarnings("deprecation")
        @Override
        public Long save(PersistentObject caArrayObject) {
            if (caArrayObject instanceof CollaboratorGroup) {
                ((CollaboratorGroup) caArrayObject).setId(this.nextId++);
            }
            super.save(caArrayObject);
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
            return caArrayObject.getId();
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.savedObjects.remove(caArrayEntity.getId());
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
