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
 * Throws UnsupportedOperationException for all CaArrayHibernateHelper functions.
 * 
 * Inherit from this class if you need to implement CaArrayHibernateHelper in a limited context,
 * such as a test, where not all functions are needed.  Override only the functions
 * needed and rely on an exception being thrown if any unimplemented function is called
 * unexpectedly.
 * 
 * @author jscott
 */
public class CaArrayHibernateHelperUnsupportedOperationImpl implements CaArrayHibernateHelper {

    /**
     * {@inheritDoc}
     */
    public Transaction beginTransaction() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String buildInClauses(List<? extends Serializable> items, String columnName,
            Map<String, List<? extends Serializable>> blocks) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void disableFilters() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object doUnfiltered(UnfilteredCallback uc) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Session getCurrentSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Connection getNewConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public SessionFactory getSessionFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void openAndBindSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void rollbackTransaction(Transaction tx) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void setFiltersEnabled(boolean enable) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void setQueryParams(Map<String, Object> params, Query q) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void unbindAndCleanupSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object unwrapProxy(Object entity) {
        throw new UnsupportedOperationException();
    }

}
