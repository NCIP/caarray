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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.proxy.HibernateProxy;

import com.fiveamsolutions.nci.commons.audit.AuditLogInterceptor;
import com.fiveamsolutions.nci.commons.util.CompositeInterceptor;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * Utility class to create and retrieve Hibernate sessions.  Most methods are pass-throughs to {@link HibernateHelper},
 * except for the methods involving filters.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("unchecked")
public final class HibernateUtil {

    private static final AuditLogInterceptor AUDIT_LOG_INTERCEPTOR = new AuditLogInterceptor();
    private static final CaArrayAuditLogProcessor AUDIT_LOG_PROCESSOR = new CaArrayAuditLogProcessor();
    private static final HibernateHelper HIBERNATE_HELPER = new HibernateHelper(SecurityUtils.getAuthorizationManager(),
            new NamingStrategy(), new CompositeInterceptor(new SecurityInterceptor(), AUDIT_LOG_INTERCEPTOR));
    static {
        AUDIT_LOG_INTERCEPTOR.setHibernateHelper(HIBERNATE_HELPER);
        AUDIT_LOG_INTERCEPTOR.setProcessor(AUDIT_LOG_PROCESSOR);
    }

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
     * Get the audit log processor.
     * @return AUDIT_LOG_PROCESSOR.
     */
    public static CaArrayAuditLogProcessor getAuditLogProcessor() {
        return AUDIT_LOG_PROCESSOR;
    }

    /**
     * Get the audit logger interceptor.
     * @return AUDIT_LOG_INTERCEPTOR.
     */
    public static AuditLogInterceptor getAuditLogInterceptor() {
        return AUDIT_LOG_INTERCEPTOR;
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
        result.enableFilter("BiomaterialFilter");
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
     * Set whether security filters should be enabled for the next session returned from getCurrentSession().
     * This should generally only be called via test code.
     * 
     * @param enable whether the filters should be enabled. 
     */
    public static void setFiltersEnabled(boolean enable) {
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
     * @return a new Connection from the data source underlying the hibernate session factory
     * @throws SQLException if an error occurs obtaining the connection
     */
    public static Connection getNewConnection() throws SQLException {
        return ((SessionFactoryImplementor) HibernateUtil.getSessionFactory()).getConnectionProvider().getConnection();
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

    /**
     * Disable security filters on the current session.
     */
    public static void disableFilters() {
        disableFilters(getCurrentSession());
    }

    private static void disableFilters(Session session) {
        Set<String> filters = session.getSessionFactory().getDefinedFilterNames();
        for (String filterName : filters) {
            // we only want to disable the security filters. assume security filters are ones
            // with USER_NAME and APPLICATION_ID parameters
            FilterDefinition fd = session.getSessionFactory().getFilterDefinition(filterName);
            if (fd.getParameterNames().contains("USER_NAME") && fd.getParameterNames().contains("APPLICATION_ID")) {
                session.disableFilter(filterName);                
            }
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
    
    /**
     * Sets the named parameters in the given query from the given map.
     * @param params map of parameter name -> value(s) for that named parameter.
     * @param q the query
     */
    public static void setQueryParams(final Map<String, Object> params, Query q) {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof Collection<?>) {
                q.setParameterList(key, (Collection<?>) value);
            } else {
                q.setParameter(key, value);
            }
        }
    }

    /**
     * Break up a list of items into separate in clauses, to avoid limits imposed by databases or by Hibernate bug
     * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2166.
     * @param items list of items to include in the in clause
     * @param columnName name of column to match against the list
     * @param blocks empty Map of HQL param name to param list of values to be set in the HQL query - it will be
     *               populated by the method
     * @return full HQL "in" clause, of the form: " columnName in (:block1) or ... or columnName in (:blockN)"
     */
    public static String buildInClause(List<? extends Serializable> items, String columnName,
            Map<String, List<? extends Serializable>> blocks) {
        StringBuffer inClause = new StringBuffer();
        int startIndex = blocks.size();
        for (int i = 0; i < items.size(); i += HibernateHelper.MAX_IN_CLAUSE_LENGTH) {
            List<? extends Serializable> block = items.subList(i, Math.min(items.size(), i
                    + HibernateHelper.MAX_IN_CLAUSE_LENGTH));
            int paramNameIndex = startIndex + (i / HibernateHelper.MAX_IN_CLAUSE_LENGTH);
            String paramName = "block" + paramNameIndex;
            if (inClause.length() > 0) {
                inClause.append(" or");
            }
            inClause.append(" " + columnName + " in (:" + paramName + ")");
            blocks.put(paramName, block);
        }
        return inClause.toString();
    }
    
    /**
     * If entity is a hibernate proxy, return the actual object it proxies, otherwise return the entity itself.
     *
     * @param entity the object to unwrap (if it is a proxy)
     * @return the unwrapped proxy, or original object.
     */
    public static Object unwrapProxy(Object entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        } else {
            return entity;
        }
    }
}
