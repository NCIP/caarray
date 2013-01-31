//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.security.SecurityInterceptor;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.instancelevel.InstanceLevelSecurityHelper;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * Utility class to create and retrieve Hibernate sessions.  Most methods are pass-throughs to {@link HibernateHelper},
 * except for the methods involving filters.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("unchecked")
public final class HibernateUtil {

    private static final HibernateHelper HIBERNATE_HELPER = new HibernateHelper(SecurityUtils.getAuthorizationManager(),
            new NamingStrategy(), new SecurityInterceptor());

    private static boolean filtersEnabled = true;

    /**
     * A private constructor because this class should not be instantiated. All callable methods are static methods.
     */
    private HibernateUtil() {
    }

    /**
     * Get the hibernate helper.
     * @return the helper.
     */
    public static HibernateHelper getHibernateHelper() {
        return HIBERNATE_HELPER;
    }

    /**
     * Returns the Hibernate configuration.
     *
     * @return a Hibernate configuration.
     * @see HibernateHelper#getConfiguration()
     */
    public static Configuration getConfiguration() {
        return getHibernateHelper().getConfiguration();
    }

    /**
     * Returns the current Hibernate session. Note that this returns a special session that can be used only in the
     * context of a transaction. (Assuming that the hibernate properties are set to use a JTA or JDBC transaction
     * factory.)
     *
     * @return a Hibernate session.
     * @see HibernateHelper#getCurrentSession()
     */
    public static Session getCurrentSession() {
        Session result = getHibernateHelper().getCurrentSession();
        if (filtersEnabled) {
            InstanceLevelSecurityHelper.initializeFilters(UsernameHolder.getUser(), result, SecurityUtils
                    .getAuthorizationManager());
        }
        return result;
    }

    /**
     * Starts a transaction on the current Hibernate session. Intended for use in
     * unit tests - DAO / Service layer logic should rely on container-managed transactions
     *
     * @return a Hibernate session.
     * @see HibernateHelper#beginTransaction()
     */
    public static Transaction beginTransaction() {
        return getHibernateHelper().beginTransaction();
    }

    /**
     * Checks if the transaction is active and then rolls it back.
     *
     * @param tx the Transaction to roll back.
     * @see HibernateHelper#rollbackTransaction(Transaction)
     */
    public static void rollbackTransaction(Transaction tx) {
        getHibernateHelper().rollbackTransaction(tx);
    }

    /**
     * @param enable enabled. This should generally only be called via test code.
     */
    public static void enableFilters(boolean enable) {
        filtersEnabled = enable;
    }

    /**
     * Open a hibernate session and bind it as the current session via
     * {@link org.hibernate.context.ManagedSessionContext#bind(org.hibernate.classic.Session)}. The hibernate property
     * "hibernate.current_session_context_class" must be set to "managed" for this to have effect This method should be
     * called from within an Interceptor or Filter type class that is setting up the scope of the Session. This method
     * should then call {@link HibernateUtil#unbindAndCleanupSession()} when the scope of the Session is expired.
     *
     * @see HibernateHelper#openAndBindSession()
     */
    public static void openAndBindSession() {
        SecurityInterceptor.clear();
        getHibernateHelper().openAndBindSession();
    }

    /**
     * Close the current session and unbind it via
     * {@link org.hibernate.context.ManagedSessionContext#unbind(SessionFactory)}. The hibernate
     * property "hibernate.current_session_context_class" must be set to "managed" for this to have effect. This method
     * should be called from within an Interceptor or Filter type class that is setting up the scope of the Session,
     * when this scope is about to expire.
     * @see HibernateHelper#unbindAndCleanupSession()
     */
    public static void unbindAndCleanupSession() {
        getHibernateHelper().unbindAndCleanupSession();
    }

    /**
     * @return hibernate session factory
     * @see HibernateHelper#getSessionFactory()
     */
    public static SessionFactory getSessionFactory() {
        return getHibernateHelper().getSessionFactory();
    }

    /**
     * Do something in an unfiltered session.
     * @param uc callback class
     * @return the result
     */
    public static Object doUnfiltered(UnfilteredCallback uc) {
        Session session = getCurrentSession();
        disableFilters(session);
        try {
            return uc.doUnfiltered(session);
        } finally {
            enableFilters(session);
        }
    }

    private static void disableFilters(Session session) {
        Set<String> filters = session.getSessionFactory().getDefinedFilterNames();
        for (String filterName : filters) {
            session.disableFilter(filterName);
        }
    }

    private static void enableFilters(Session session) {
        if (filtersEnabled) {
            InstanceLevelSecurityHelper.initializeFilters(UsernameHolder.getUser(), session, SecurityUtils
                    .getAuthorizationManager());
        }
    }

    /**
     * Reinitialize the hibernate filters from the database.
     */
    public static void reinitializeCsmFilters() {
        getHibernateHelper().reinitializeCsmFilters(SecurityUtils.getAuthorizationManager());
    }

}
