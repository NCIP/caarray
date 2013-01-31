//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.listener;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.fileaccess.FileCleanupThread;
import gov.nih.nci.caarray.security.SecurityUtils;

import java.util.Timer;

import javax.servlet.ServletContextEvent;

/**
 * Performs initialization operations required at startup of the caArray application.
 */
public class StartupListener extends AbstractHibernateSessionScopeListener {

    private static final int TIMER_INTERVAL_FIFTEEN_MINS = 900000;
    /**
     * Creates connection to DataService as well as sets configuration in
     * application scope. Initiates scheduled task to cleanup files every 15 mins
     * @param event ServletContextEvent
     */
    @Override
    public void doContextInitialized(ServletContextEvent event) {
        ArrayDataService arrayDataService =
            (ArrayDataService) ServiceLocatorFactory.getLocator().lookup(ArrayDataService.JNDI_NAME);
        arrayDataService.initialize();

        SecurityUtils.init();
        Timer timer = new Timer();
        timer.schedule(FileCleanupThread.getInstance(), TIMER_INTERVAL_FIFTEEN_MINS, TIMER_INTERVAL_FIFTEEN_MINS);
    }
}
