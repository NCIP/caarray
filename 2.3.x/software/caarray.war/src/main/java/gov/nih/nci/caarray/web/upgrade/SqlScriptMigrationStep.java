//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.SessionFactoryImplementor;
import org.w3c.dom.Element;

/**
 * Executes a SQL script as part of an application upgrade.
 */
final class SqlScriptMigrationStep extends AbstractMigrationStep {

    private static final Logger LOG = Logger.getLogger(SqlScriptMigrationStep.class);

    private final String script;
    private final boolean ignoreErrors;
    private final String delimiter;

    SqlScriptMigrationStep(Element element) {
        this.script = getContent(element);
        ignoreErrors = BooleanUtils.toBoolean(element.getAttribute("ignoreErrors"));
        delimiter = StringUtils.defaultIfEmpty(element.getAttribute("delimiter"), ";");
    }

    @Override
    void execute() throws MigrationStepFailedException {
        InputStream instream = this.getClass().getResourceAsStream("/" + script);
        BufferedReader in = new BufferedReader(new InputStreamReader(instream));
        Connection connection = null;
        try {
            connection =
                ((SessionFactoryImplementor) HibernateUtil.getSessionFactory()).getConnectionProvider().getConnection();
            String line = null;
            StringBuffer sqlStatement = new StringBuffer();
            Statement s = connection.createStatement();
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--")) {
                    continue;
                }
                if (line.endsWith(delimiter)) {
                    sqlStatement.append(line.substring(0, line.length() - delimiter.length()));
                    LOG.info("Executing SQL statement: " + sqlStatement.toString());
                    executeUpdate(sqlStatement, s);
                    sqlStatement = new StringBuffer();
                } else {
                    sqlStatement.append(line).append(' ');
                }
            }
        } catch (SQLException e) {
            throw new MigrationStepFailedException(e);
        } catch (IOException e) {
            throw new MigrationStepFailedException(e);
        } finally {
            close(connection);
            close(instream);
        }
    }

    private void executeUpdate(StringBuffer sqlStatement, Statement s) throws MigrationStepFailedException {
        try {
            s.executeUpdate(sqlStatement.toString());
        } catch (SQLException e) {
            if (ignoreErrors) {
                LOG.info("Ignoring SQLException because ignoreErrors is true", e);
            } else {
                throw new MigrationStepFailedException(e);
            }
        }
    }

    private void close(InputStream instream) {
        try {
            if (instream != null) {
                instream.close();
            }
        } catch (IOException e) {
            LOG.error("Couldn't close InputStream", e);
        }
    }

    private void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOG.error("Couldn't close connection", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SQL migration script " + script;
    }

}
