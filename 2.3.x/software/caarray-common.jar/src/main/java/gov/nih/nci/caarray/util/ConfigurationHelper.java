//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import javax.sql.DataSource;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Helper class for retrieving system configuration.
 * @author dkokotov
 */
public final class ConfigurationHelper {
    private static final String DATASOURCE_JNDI_LOC = "java:jdbc/CaArrayDataSource";
    private static final String TABLE_NAME = "config_parameter";
    private static final String PARAM_NAME_COLUMN = "param";
    private static final String PARAM_VALUE_COLUMN = "raw_value";

    private ConfigurationHelper() {
        // empty constructor
    }

    /**
     * @return the system configuration.
     */
    public static DataConfiguration getConfiguration() {
        DataSource ds = null;
        try {
            ds = (DataSource) ServiceLocatorFactory.getLocator().lookup(DATASOURCE_JNDI_LOC);
            if (ds == null) {
                ds = getAdhocDataSource();
            }
        } catch (IllegalStateException e) {
            ds = getAdhocDataSource();
        }
        DatabaseConfiguration config = new DatabaseConfiguration(ds, TABLE_NAME, PARAM_NAME_COLUMN, PARAM_VALUE_COLUMN);
        config.setDelimiterParsingDisabled(true);
        return new DataConfiguration(config);
    }

    /**
     * @return whether this is a development deployment, or a production deployment
     */
    public static boolean isDev() {
        return getConfiguration().getBoolean(ConfigParamEnum.DEVELOPMENT_MODE.name(), false);
    }
    
    private static DataSource getAdhocDataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty(Environment.URL));
        ds.setUser(config.getProperty(Environment.USER));
        ds.setPassword(config.getProperty(Environment.PASS));
        return ds;
    }

}
