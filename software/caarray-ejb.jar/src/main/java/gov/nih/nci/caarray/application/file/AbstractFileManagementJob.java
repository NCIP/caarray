//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * Base class for file handling jobs.
 */
abstract class AbstractFileManagementJob implements Serializable {

    private static final Logger LOG = Logger.getLogger(AbstractFileManagementJob.class);

    private static final long serialVersionUID = 1L;
    private final String username;
    private CaArrayDaoFactory daoFactory;

    AbstractFileManagementJob(String username) {
        this.username = username;
    }

    String getUsername() {
        return this.username;
    }

    abstract void execute();

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
    }

    ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    ArrayDesignImporter getArrayDesignImporter() {
        return new ArrayDesignImporter(getArrayDesignService());
    }

    abstract void setInProgressStatus();

    abstract PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException;

    void handleUnexpectedError() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ((SessionFactoryImplementor) HibernateUtil.getSessionFactory()).
                getConnectionProvider().getConnection();
            con.setAutoCommit(false);
            ps = getUnexpectedErrorPreparedStatement(con);
            ps.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOG.error("Error while attempting to handle an unexpected error.", e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                LOG.error("Error while attempting close the connection after handling an unexpected error.", e);
            }
        }
    }
}
