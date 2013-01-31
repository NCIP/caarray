//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

/**
 * Test cases
 */
public class ContactDaoTest extends AbstractDaoTest {

    private static final ContactDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getContactDao();

    @Test
    public void testGetAllProviders() {
        Transaction tx = HibernateUtil.beginTransaction();
        assertEquals(0, DAO_OBJECT.getAllProviders().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();
        Organization o = new Organization();
        o.setName("Foo");
        o.setEmail("foo@bar.org");
        o.setProvider(true);
        s.save(o);
        tx.commit();


        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.getAllProviders().size());
        assertEquals("Foo", DAO_OBJECT.getAllProviders().get(0).getName());
        tx.commit();
    }    
}
