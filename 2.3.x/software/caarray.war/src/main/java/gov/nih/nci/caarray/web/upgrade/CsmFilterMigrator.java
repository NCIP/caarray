//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

/**
 * If the CSM filters have been updated by a previous migration step, this Migrator reinitializes the filters to make
 * sure the changes take effect without restarting the application.
 *
 * @author Steve Lustbader
 */
public final class CsmFilterMigrator extends AbstractMigrator implements Migrator {
    private static final Logger LOG = Logger.getLogger(CsmFilterMigrator.class);

    /**
     * {@inheritDoc}
     */
    public void migrate() throws MigrationStepFailedException {
        LOG.info("Reinitializing CSM filters");
        UserTransaction transaction =
            ((UserTransaction) ServiceLocatorFactory.getLocator().lookup("java:comp/UserTransaction"));
        try {
            transaction.commit();
            HibernateUtil.unbindAndCleanupSession();
            HibernateUtil.reinitializeCsmFilters();
            SecurityUtils.setPrivilegedMode(true);
            HibernateUtil.enableFilters(false);
            HibernateUtil.openAndBindSession();
            transaction.setTransactionTimeout(UpgradeManager.TIMEOUT_SECONDS);
            transaction.begin();
        } catch (Exception e) {
            throw new MigrationStepFailedException("Could not manage transaction when updating CSM filters", e);
        }
    }

}
