//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Singleton responsible for checking upgrade status and running upgrade if necessary.
 */
public final class UpgradeManager {

    private static final Logger LOG = Logger.getLogger(UpgradeManager.class);
    private static final String MIGRATION_FILE = "/db-migrations.xml";

    /**
     * Transaction timeout (in seconds) for upgrade steps.
     */
    public static final int TIMEOUT_SECONDS = 28800;

    private static UpgradeManager instance = new UpgradeManager();

    private final Map<String, Migration> allMigrations = new HashMap<String, Migration>();
    private final List<Migration> upgradeList = new ArrayList<Migration>();

    private UserTransaction transaction;
    private String currentVersion;

    private UpgradeManager() {
        super();
        initialize();
    }

    private void initialize() {
        loadMigrationSteps();
    }

    private void loadMigrationSteps() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(this.getClass().getResource(MIGRATION_FILE).getPath());
            NodeList nl = dom.getElementsByTagName("migration");
            for (int i = 0; i < nl.getLength(); i++) {
                Element element = (Element) nl.item(i);
                Migration migration = new Migration(element);
                allMigrations.put(migration.getFromVersion(), migration);
            }
        } catch (Exception e) {
            LOG.info("error loading migration file", e);
        }
    }

    /**
     * Returns the singleton instance.
     *
     * @return the instance.
     */
    public static UpgradeManager getInstance() {
        return instance;
    }

    /**
     * Indicates whether an upgrade is required.
     *
     * @return true if an upgrade is required, false otherwise.
     */
    public boolean isUpgradeRequired() {
        return allMigrations.containsKey(getCurrentVersion());
    }

    private String getCurrentVersion() {
        if (currentVersion == null) {
            DataConfiguration config = ConfigurationHelper.getConfiguration();
            currentVersion = config.getString(ConfigParamEnum.SCHEMA_VERSION.name());
        }
        return currentVersion;
    }

    private void computeUpgradeList() {
        String version = getCurrentVersion();
        Migration migration;
        while ((migration = allMigrations.get(version)) != null) {
            upgradeList.add(migration);
            version = migration.getToVersion();
        }
    }

    /**
     * Performs any necessary upgrades.
     */
    public void performUpgrades() {
        computeUpgradeList();

        if (!isUpgradeRequired()) {
            return;
        }
        LOG.info("Starting upgrade");
        HibernateUtil.openAndBindSession();
        bindSessionWithSecurity(false);
        Migration m = null;

        try {
            while (isUpgradeRequired()) {
                startTransaction();
                m = allMigrations.get(getCurrentVersion());
                m.setStatus(MigrationStatus.UPDATING);
                m.execute();
                setCurrentVersion(m.getToVersion());
                commit();
                m.setStatus(MigrationStatus.COMPLETE);
            }
            LOG.info("Upgrade completed successfully");
        } catch (MigrationStepFailedException e) {
            m.setStatus(MigrationStatus.ERROR);
            LOG.error("Failed migration from version " + m.getFromVersion() + " to " + m.getToVersion(), e);
            rollback();
        } finally {
            bindSessionWithSecurity(true);
        }
        HibernateUtil.unbindAndCleanupSession();
    }

    private void startTransaction() {
        transaction = (UserTransaction) ServiceLocatorFactory.getLocator().lookup("java:comp/UserTransaction");
        try {
            transaction.setTransactionTimeout(TIMEOUT_SECONDS);
            transaction.begin();
        } catch (Exception e) {
            LOG.error("Upgrade failed, couldn't start transaction", e);
        }
    }

    private void bindSessionWithSecurity(boolean securityEnabled) {
        HibernateUtil.unbindAndCleanupSession();
        SecurityUtils.setPrivilegedMode(!securityEnabled);
        HibernateUtil.setFiltersEnabled(securityEnabled);
        HibernateUtil.openAndBindSession();
    }

    private void commit() {
        try {
            transaction.commit();
        } catch (Exception e) {
            LOG.error("Couldn't commit transaction", e);
        }
    }

    private void rollback() {
        try {
            transaction.rollback();
        } catch (Exception e) {
            LOG.error("Couldn't rollback transaction", e);
        }
    }

    private void setCurrentVersion(String toVersion) {
        currentVersion = toVersion;
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        config.setProperty(ConfigParamEnum.SCHEMA_VERSION.name(), toVersion);
    }

    /**
     * @return the upgradeList
     */
    public List<Migration> getUpgradeList() {
        return upgradeList;
    }

}
