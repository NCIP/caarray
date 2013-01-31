//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Runs the necessary list of upgrade scripts and utilities necessary to upgrade caArray
 * from its previously registered version to the current version.
 */
public class ApplicationUpgradeListener implements ServletContextListener {

    /**
     * {@inheritDoc}
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void contextInitialized(ServletContextEvent arg0) {
        (new Thread() {
            public void run() {
                UpgradeManager.getInstance().performUpgrades();
            }
        }).start();
    }

}
