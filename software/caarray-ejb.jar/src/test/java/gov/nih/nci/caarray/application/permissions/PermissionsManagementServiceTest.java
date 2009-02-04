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
