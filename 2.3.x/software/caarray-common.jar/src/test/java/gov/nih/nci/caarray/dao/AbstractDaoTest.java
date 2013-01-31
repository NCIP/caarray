//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

/**
 * Helper methods for the dao classes
 */
@SuppressWarnings("PMD")
public abstract class AbstractDaoTest extends AbstractCaarrayTest {
    @Before
    public void abstractSetup() {
        UsernameHolder.setUser(STANDARD_USER);
        HibernateUtil.enableFilters(true);
        HibernateUtil.openAndBindSession();
   }


    @After
    public void tearDown() {
        try {
            Transaction tx = HibernateUtil.getCurrentSession().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (HibernateException e) {
            // ok - there was no active transaction
        }
        HibernateUtil.unbindAndCleanupSession();
        HibernateIntegrationTestCleanUpUtility.cleanUp();
    }

}
