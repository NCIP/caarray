//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.AdditionalOrganismName;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

/**
 * @author Scott Miller
 *
 */
public class OrganismDaoTest extends AbstractDaoTest {

    private static final OrganismDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getOrganismDao();

    @Test
    public void testGetAll() {
        Transaction tx = HibernateUtil.beginTransaction();
        assertEquals(0, DAO_OBJECT.getAllOrganisms().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();
        Organism o = new Organism();
        o.setScientificName("testscientificname");
        TermSource t = new TermSource();
        t.setName("testtermsource");
        o.setTermSource(t);

        AdditionalOrganismName additionalName = new AdditionalOrganismName();
        additionalName.setValue("additional name");
        o.getAdditionalOrganismNameCollection().add(additionalName);

        s.save(o);
        tx.commit();


        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.getAllOrganisms().size());
        Organism retrieved = DAO_OBJECT.getOrganism(o.getId());
        assertEquals("testscientificname", retrieved.getScientificName());
        assertEquals(1, retrieved.getAdditionalOrganismNameCollection().size());
        assertEquals("additional name", retrieved.getAdditionalOrganismNameCollection().iterator().next().getValue());
        tx.commit();
    }

    @Test
    public void testGetMatchingNames() {
        Transaction tx = HibernateUtil.beginTransaction();
        assertEquals(0, DAO_OBJECT.getAllOrganisms().size());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getCurrentSession();
        Organism o = new Organism();
        o.setScientificName("searchscientificname");
        TermSource t = new TermSource();
        t.setName("testtermsource");
        o.setTermSource(t);

        s.save(o);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        s = HibernateUtil.getCurrentSession();
        assertEquals(1, DAO_OBJECT.searchForOrganismNames("search").size());
        tx.commit();
    }
}
