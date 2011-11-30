/**
 * NOTICE: This software  source code and any of  its derivatives are the
 * confidential  and  proprietary   information  of  Vecna  Technologies,
 * Inc. (such source  and its derivatives are hereinafter  referred to as
 * "Confidential Information"). The  Confidential Information is intended
 * to be  used exclusively by  individuals or entities that  have entered
 * into either  a non-disclosure agreement or license  agreement (or both
 * of  these agreements,  if  applicable) with  Vecna Technologies,  Inc.
 * ("Vecna")   regarding  the  use   of  the   Confidential  Information.
 * Furthermore,  the  Confidential  Information  shall be  used  only  in
 * accordance  with   the  terms   of  such  license   or  non-disclosure
 * agreements.   All  parties using  the  Confidential Information  shall
 * verify that their  intended use of the Confidential  Information is in
 * compliance  with and  not in  violation of  any applicable  license or
 * non-disclosure  agreements.  Unless expressly  authorized by  Vecna in
 * writing, the Confidential Information  shall not be printed, retained,
 * copied, or  otherwise disseminated,  in part or  whole.  Additionally,
 * any party using the Confidential  Information shall be held liable for
 * any and  all damages incurred  by Vecna due  to any disclosure  of the
 * Confidential  Information (including  accidental disclosure).   In the
 * event that  the applicable  non-disclosure or license  agreements with
 * Vecna  have  expired, or  if  none  currently  exists, all  copies  of
 * Confidential Information in your  possession, whether in electronic or
 * printed  form, shall be  destroyed or  returned to  Vecna immediately.
 * Vecna  makes no  representations  or warranties  hereby regarding  the
 * suitability  of  the   Confidential  Information,  either  express  or
 * implied,  including  but not  limited  to  the  implied warranties  of
 * merchantability,    fitness    for    a   particular    purpose,    or
 * non-infringement. Vecna  shall not be liable for  any damages suffered
 * by  licensee as  a result  of  using, modifying  or distributing  this
 * Confidential Information.  Please email [info@vecnatech.com]  with any
 * questions regarding the use of the Confidential Information.
 */

package gov.nih.nci.caarray.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkokotov
 */
@SuppressWarnings("PMD")
public class UniqueValidatorTest extends AbstractDaoTest {
    private VocabularyDao DAO_OBJECT;

    @Before
    public void setUp() {
        this.DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
    }

    @Test
    public void testUniqueValidator() {
        final TermSource t1 = new TermSource();
        t1.setName("name1");
        t1.setUrl("url1");
        t1.setVersion("version1");

        final TermSource t2 = new TermSource();
        t2.setName("name1");
        t2.setUrl("url2");

        final TermSource t3 = new TermSource();
        t3.setName("name2");
        t3.setVersion("version1");

        final TermSource t4 = new TermSource();
        t4.setName("name1");
        t4.setUrl("foobar");
        t4.setVersion("version1");

        final TermSource t5 = new TermSource();
        t5.setName("name1");

        final TermSource t6 = new TermSource();
        t6.setName("name3");
        t6.setUrl("url2");
        t6.setVersion("version3");

        final UniqueConstraintsValidator ucv = new UniqueConstraintsValidator();
        ucv.initialize(t1.getClass().getAnnotation(UniqueConstraints.class));

        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.DAO_OBJECT.save(t1);

            assertTrue(ucv.isValid(t1));
            assertTrue(ucv.isValid(t2));
            assertTrue(ucv.isValid(t3));
            assertFalse(ucv.isValid(t4));
            assertTrue(ucv.isValid(t5));
            assertTrue(ucv.isValid(t6));

            this.DAO_OBJECT.save(t2);
            this.DAO_OBJECT.save(t3);

            assertTrue(ucv.isValid(t1));
            assertTrue(ucv.isValid(t2));
            assertTrue(ucv.isValid(t3));
            assertFalse(ucv.isValid(t4));
            assertFalse(ucv.isValid(t5));
            assertTrue(ucv.isValid(t6));

            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol: " + e.getMessage());
        }
    }
}
