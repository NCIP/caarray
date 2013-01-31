//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

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
    }
}
