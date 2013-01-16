//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.listener;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.fileaccess.DataStorageCleanupThread;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileCleanupThread;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.plugins.CaArrayPluginsFacade;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.staticinjection.CaArrayWarStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.inject.Injector;

/**
 * Performs initialization operations required at startup of the caArray application.
 */
public class StartupListener implements ServletContextListener {
    private static final int TIMER_INTERVAL_FIFTEEN_MINS = 900000;
    private static final int TIMER_INTERVAL_ONE_MIN = 60000;

    /**
     * Creates connection to DataService as well as sets configuration in application scope. Initiates scheduled task to
     * cleanup files every 15 mins
     *
     * @param event ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        final Injector injector =
                InjectorFactory.getInjector().createChildInjector(new CaArrayWarStaticInjectionModule());

        final CaArrayHibernateHelper hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        hibernateHelper.openAndBindSession();
        final ArrayDataService arrayDataService =
                (ArrayDataService) ServiceLocatorFactory.getLocator().lookup(ArrayDataService.JNDI_NAME);
        arrayDataService.initialize();
        SecurityUtils.init();
        boolean privilevedMode = SecurityUtils.isPrivilegedMode();
        try {
            SecurityUtils.setPrivilegedMode(true);
            final FileAccessService fileAccessService =
                    (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
            fileAccessService.cleanupUnreferencedChildren();
        } finally {
            SecurityUtils.setPrivilegedMode(privilevedMode);
        }
        hibernateHelper.unbindAndCleanupSession();

        final Timer fileCleanupTimer = new Timer(true);
        fileCleanupTimer.schedule(FileCleanupThread.getInstance(), TIMER_INTERVAL_FIFTEEN_MINS,
                TIMER_INTERVAL_FIFTEEN_MINS);

        final Timer dataStorageCleanupTimer = new Timer(true);
        final DataStorageCleanupThread dataStorageCleanupThread = injector.getInstance(DataStorageCleanupThread.class);
        dataStorageCleanupTimer.schedule(dataStorageCleanupThread, TIMER_INTERVAL_ONE_MIN, TIMER_INTERVAL_FIFTEEN_MINS);

        initAtlassianPlugins(event.getServletContext());
    }

    private void initAtlassianPlugins(ServletContext servletContext) {
        final ApplicationContext appContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        final CaArrayPluginsFacade plugins = (CaArrayPluginsFacade) appContext.getBean("pluginsFacade");
        plugins.start();
        CaArrayPluginsFacade.setInstance(plugins);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing - subclasses can override if needed
    }
}
