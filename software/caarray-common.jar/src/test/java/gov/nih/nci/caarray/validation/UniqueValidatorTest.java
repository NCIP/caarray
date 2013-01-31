//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.Test;

/**
 * @author dkokotov
 */
@SuppressWarnings("PMD")
public class UniqueValidatorTest extends AbstractDaoTest {
    private static final VocabularyDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    @Test
    public void testUniqueValidator() {
        TermSource t1 = new TermSource();
        t1.setName("name1");
        t1.setUrl("url1");
        t1.setVersion("version1");

        TermSource t2 = new TermSource();
        t2.setName("name1");
        t2.setUrl("url2");

        TermSource t3 = new TermSource();
        t3.setName("name2");
        t3.setVersion("version1");

        TermSource t4 = new TermSource();
        t4.setName("name1");
        t4.setUrl("foobar");
        t4.setVersion("version1");

        TermSource t5 = new TermSource();
        t5.setName("name1");

        TermSource t6= new TermSource();
        t6.setName("name3");
        t6.setUrl("url2");
        t6.setVersion("version3");


        UniqueConstraintsValidator ucv = new UniqueConstraintsValidator();
        ucv.initialize(t1.getClass().getAnnotation(UniqueConstraints.class));

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(t1);

            assertTrue(ucv.isValid(t1));
            assertTrue(ucv.isValid(t2));
            assertTrue(ucv.isValid(t3));
            assertFalse(ucv.isValid(t4));
            assertTrue(ucv.isValid(t5));
            assertTrue(ucv.isValid(t6));

            DAO_OBJECT.save(t2);
            DAO_OBJECT.save(t3);

            assertTrue(ucv.isValid(t1));
            assertTrue(ucv.isValid(t2));
            assertTrue(ucv.isValid(t3));
            assertFalse(ucv.isValid(t4));
            assertFalse(ucv.isValid(t5));
            assertTrue(ucv.isValid(t6));

            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol: " + e.getMessage());
        }
    }
}

