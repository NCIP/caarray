//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.listener;

import gov.nih.nci.caarray.util.HibernateUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Base class for ServletContextListeners that perform operations which interact with Hibernate
 * and thus require a current Hibernate Session. Takes care of setting up such a Session and cleaning
 * up after the listener is done. Subclasses should implement doContextInitialized and do their work there.
 * 
 * @author Dan Kokotov
 */
public abstract class AbstractHibernateSessionScopeListener implements ServletContextListener {

    /**
     * {@inheritDoc}
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing - subclasses can override if needed
    }

    /**
     * {@inheritDoc}
     */
    public final void contextInitialized(ServletContextEvent event) {
        HibernateUtil.openAndBindSession();
        doContextInitialized(event);
        HibernateUtil.unbindAndCleanupSession();
    }
    
    /**
     * Subclasses should implement this method and do their work there. 
     * @param event the servlet context event
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public abstract void doContextInitialized(ServletContextEvent event);
}
