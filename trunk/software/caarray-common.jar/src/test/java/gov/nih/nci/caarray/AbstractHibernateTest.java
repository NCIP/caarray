//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray;

import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for tests that require hibernate backed by an actual database.
 * Handles setting up hibernate and cleaning up the database when done.
 * 
 * @author Steve Lustbader
 */
public abstract class AbstractHibernateTest extends AbstractCaarrayTest {
    private final boolean enableFilters;
    
    protected AbstractHibernateTest(boolean enableFilters) {
        this.enableFilters = enableFilters;
    }
    @Before
    public void baseIntegrationSetUp() {
        UsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
        HibernateUtil.setFiltersEnabled(enableFilters);
        HibernateUtil.openAndBindSession();
    }

    @After
    public void baseIntegrationTearDown() {
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
