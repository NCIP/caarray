//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases
 */
public class CollaboratorGroupDaoTest extends AbstractDaoTest {
    private CollaboratorGroupDao daoObject;

    @Before
    public void setup() {
        this.daoObject = new CollaboratorGroupDaoImpl(this.hibernateHelper);
    }

    @Test
    public void testGetAll() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        Session s = this.hibernateHelper.getCurrentSession();

        assertEquals(0, this.daoObject.getAll().size());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        User owner = (User) s.load(User.class, 1L);
        Group group = (Group) s.load(Group.class, 1L);
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        assertEquals(1, this.daoObject.getAll().size());
        tx.commit();

        CaArrayUsernameHolder.setUser("systemadministrator");
        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        // user 8 is "systemadministrator"
        owner = (User) s.load(User.class, 8L);
        group = (Group) s.load(Group.class, 2L);
        cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        assertEquals(2, this.daoObject.getAll().size());
        tx.commit();

        CaArrayUsernameHolder.setUser(STANDARD_USER);
        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        assertEquals(1, this.daoObject.getAll().size());
        tx.commit();
    }

    @Test
    public void testGetAllForUser() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        Session s = this.hibernateHelper.getCurrentSession();
        assertEquals(0, this.daoObject.getAll().size());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        final User owner = (User) s.load(User.class, 2L);
        final Group group = (Group) s.load(Group.class, 2L);
        final CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        // user 8 is "systemadministrator"
        final User otherOwner = (User) s.load(User.class, 8L);
        final Group otherGroup = (Group) s.load(Group.class, 8L);
        CaArrayUsernameHolder.setUser(otherOwner.getLoginName());
        final CollaboratorGroup otherCg = new CollaboratorGroup(otherGroup, otherOwner);
        s.save(otherCg);
        tx.commit();

        CaArrayUsernameHolder.setUser(STANDARD_USER);

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        List<CollaboratorGroup> cgs = this.daoObject.getAllForCurrentUser();
        assertEquals(1, cgs.size());
        assertEquals(cg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        CaArrayUsernameHolder.setUser(otherOwner.getLoginName());

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        cgs = this.daoObject.getAllForCurrentUser();
        assertEquals(1, cgs.size());
        assertEquals(otherCg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        // user 4 is "researchscientist"
        CaArrayUsernameHolder.setUser(((User) s.load(User.class, 4L)).getLoginName());
        cgs = this.daoObject.getAllForCurrentUser();
        assertTrue(cgs.isEmpty());
        tx.commit();
    }

    @Test
    public void testGetAllForOwner() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        Session s = this.hibernateHelper.getCurrentSession();
        assertEquals(0, this.daoObject.getAll().size());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        final User owner = (User) s.load(User.class, 2L);
        final Group group = (Group) s.load(Group.class, 2L);
        final CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        // turn off filters for sys admin
        this.hibernateHelper.setFiltersEnabled(false);
        this.hibernateHelper.disableFilters();
        s = this.hibernateHelper.getCurrentSession();
        // user 8 is "systemadministrator"
        final User otherOwner = (User) s.load(User.class, 8L);
        final Group otherGroup = (Group) s.load(Group.class, 8L);
        CaArrayUsernameHolder.setUser(otherOwner.getLoginName());
        final CollaboratorGroup otherCg = new CollaboratorGroup(otherGroup, otherOwner);
        s.save(otherCg);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        List<CollaboratorGroup> cgs = this.daoObject.getAllForUser(owner.getUserId());
        assertEquals(1, cgs.size());
        assertEquals(cg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        cgs = this.daoObject.getAllForUser(otherOwner.getUserId());
        assertEquals(1, cgs.size());
        assertEquals(otherCg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

    }
}
