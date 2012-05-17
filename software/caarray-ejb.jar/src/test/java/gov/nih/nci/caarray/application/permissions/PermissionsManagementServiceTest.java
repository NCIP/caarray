/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
