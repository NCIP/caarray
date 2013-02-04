//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author jscott
 *
 */
public interface CaArrayHibernateHelper {
    /**
     * Starts a transaction on the current Hibernate session. Intended for use in
     * unit tests - DAO / Service layer logic should rely on container-managed transactions
     *
     * @return a Hibernate session.
     */
    Transaction beginTransaction();

    /**
     * Break up a list of items into separate in clauses, to avoid limits imposed by databases or by Hibernate bug
     * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2166.
     * @param items list of items to include in the in clause
     * @param columnName name of column to match against the list
     * @param blocks empty Map of HQL param name to param list of values to be set in the HQL query - it will be
     *               populated by the method
     * @return full HQL "in" clause, of the form: " columnName in (:block1) or ... or columnName in (:blockN)"
     */
    String buildInClauses(List<? extends Serializable> items, String columnName,
            Map<String, List<? extends Serializable>> blocks);

    /**
     * Disable security filters on the current session.
     */
    void disableFilters();

    /**
     * Do something in an unfiltered session.
     * @param uc callback class
     * @return the result
     */
    Object doUnfiltered(UnfilteredCallback uc);

    /**
     * @return the configuration
     */
    Configuration getConfiguration();

    /**
     * Returns the current Hibernate session. Note that this returns a special session that can be used only in the
     * context of a transaction. (Assuming that the hibernate properties are set to use a JTA or JDBC transaction
     * factory.)
     *
     * @return a Hibernate session.
     * @see HibernateHelper#getCurrentSession()
     */
    Session getCurrentSession();

    /**
     * @return a new Connection from the data source underlying the hibernate session factory
     * @throws SQLException if an error occurs obtaining the connection
     */
    Connection getNewConnection() throws SQLException;

    /**
     * @return the sessionFactory
     */
    SessionFactory getSessionFactory();
    
    /**
     * Open a hibernate session and bind it as the current session via
     * {@link org.hibernate.context.ManagedSessionContext#bind(org.hibernate.classic.Session)}. The hibernate property
     * "hibernate.current_session_context_class" must be set to "managed" for this to have effect This method should be
     * called from within an Interceptor or Filter type class that is setting up the scope of the Session. This method
     * should then call {@link CaArrayHibernateHelper#unbindAndCleanupSession()} when the scope of the Session is
     * expired.
     * 
     * Clears the SecurityInterceptor.
     *
     * @see HibernateHelper#openAndBindSession()
     */
    void openAndBindSession();

    /**
     * Checks if the transaction is active and then rolls it back.
     *
     * @param tx the Transaction to roll back.
     */
    void rollbackTransaction(Transaction tx);

    /**
     * Set whether security filters should be enabled for the next session returned from getCurrentSession().
     * This should generally only be called via test code.
     * 
     * @param enable whether the filters should be enabled. 
     */
    void setFiltersEnabled(boolean enable);

    /**
     * Sets the named parameters in the given query from the given map.
     * @param params map of parameter name -> value(s) for that named parameter.
     * @param q the query
     */
    void setQueryParams(Map<String, Object> params, Query q);

    /**
     * Close the current session and unbind it via {@link ManagedSessionContext#unbind(SessionFactory)}. The hibernate
     * property "hibernate.current_session_context_class" must be set to "managed" for this to have effect. This method
     * should be called from within an Interceptor or Filter type class that is setting up the scope of the Session,
     * when this scope is about to expire.
     */
    void unbindAndCleanupSession();

    /**
     * If entity is a hibernate proxy, return the actual object it proxies, otherwise return the entity itself.
     *
     * @param entity the object to unwrap (if it is a proxy)
     * @return the unwrapped proxy, or original object.
     */
    Object unwrapProxy(Object entity);
}
