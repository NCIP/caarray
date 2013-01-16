//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.upgrade;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

/**
 * @author jscott
 *
 */
public class HibernateSingleConnectionProvider implements ConnectionProvider {
    private static Connection connection;

    /**
     * {@inheritDoc}
     */
    public void close() throws HibernateException {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void closeConnection(Connection conn) throws SQLException {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void configure(Properties props) throws HibernateException {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() throws SQLException {
         return connection;
    }

    /**
     * {@inheritDoc}
      */
    public boolean supportsAggressiveRelease() {
        return false;
    }

    /**
     * @param connection the single connection to serve to Hibernate 
     */
    public static void setConnection(Connection connection) {
        HibernateSingleConnectionProvider.connection = connection;
    }
}
