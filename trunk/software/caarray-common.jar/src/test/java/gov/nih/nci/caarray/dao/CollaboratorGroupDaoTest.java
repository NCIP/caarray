//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

/**
 * Test cases
 */
public class CollaboratorGroupDaoTest extends AbstractDaoTest {

    private static final CollaboratorGroupDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getCollaboratorGroupDao();

    @Test
    public void testGetAll() {
        Transaction tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();

        assertEquals(0, DAO_OBJECT.getAll().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        User owner = (User) s.load(User.class, 1L);
        Group group = (Group) s.load(Group.class, 1L);
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.getAll().size());
        tx.commit();

        UsernameHolder.setUser("systemadministrator");
        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        // user 8 is "systemadministrator"
        owner = (User) s.load(User.class, 8L);
        group = (Group) s.load(Group.class, 2L);
        cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.getAll().size());
        tx.commit();

        UsernameHolder.setUser(STANDARD_USER);
        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.getAll().size());
        tx.commit();
    }

    @Test
    public void testGetAllForUser() {
        Transaction tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();
        assertEquals(0, DAO_OBJECT.getAll().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        User owner = (User) s.load(User.class, 2L);
        Group group = (Group) s.load(Group.class, 2L);
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        // user 8 is "systemadministrator"
        User otherOwner = (User) s.load(User.class, 8L);
        Group otherGroup = (Group) s.load(Group.class, 8L);
        UsernameHolder.setUser(otherOwner.getLoginName());
        CollaboratorGroup otherCg = new CollaboratorGroup(otherGroup, otherOwner);
        s.save(otherCg);
        tx.commit();

        UsernameHolder.setUser(STANDARD_USER);

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        List<CollaboratorGroup> cgs = DAO_OBJECT.getAllForCurrentUser();
        assertEquals(1, cgs.size());
        assertEquals(cg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        UsernameHolder.setUser(otherOwner.getLoginName());
        cgs = DAO_OBJECT.getAllForCurrentUser();
        assertEquals(1, cgs.size());
        assertEquals(otherCg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        // user 4 is "researchscientist"
        UsernameHolder.setUser(((User) s.load(User.class, 4L)).getLoginName());
        cgs = DAO_OBJECT.getAllForCurrentUser();
        assertTrue(cgs.isEmpty());
        tx.commit();
    }

    @Test
    public void testGetAllForOwner() {
        Transaction tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();
        assertEquals(0, DAO_OBJECT.getAll().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        User owner = (User) s.load(User.class, 2L);
        Group group = (Group) s.load(Group.class, 2L);
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        s.save(cg);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        // turn off filters for sys admin
        HibernateUtil.setFiltersEnabled(false);
        HibernateUtil.disableFilters();
        s = HibernateUtil.getCurrentSession();
        // user 8 is "systemadministrator"
        User otherOwner = (User) s.load(User.class, 8L);
        Group otherGroup = (Group) s.load(Group.class, 8L);
        UsernameHolder.setUser(otherOwner.getLoginName());
        CollaboratorGroup otherCg = new CollaboratorGroup(otherGroup, otherOwner);
        s.save(otherCg);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        List<CollaboratorGroup> cgs = DAO_OBJECT.getAllForUser(owner.getUserId());
        assertEquals(1, cgs.size());
        assertEquals(cg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        cgs = DAO_OBJECT.getAllForUser(otherOwner.getUserId());
        assertEquals(1, cgs.size());
        assertEquals(otherCg.getGroup().getGroupId(), cgs.get(0).getGroup().getGroupId());
        tx.commit();

    }
}
