//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Responsible for upgrading a current version of the application schema to a target version.
 */
public final class Migration {

    private static final Logger LOG = Logger.getLogger(Migration.class);

    private final List<AbstractMigrationStep> steps = new ArrayList<AbstractMigrationStep>();
    private final String fromVersion;
    private final String toVersion;
    private MigrationStatus status;

    Migration(Element element) {
        fromVersion = element.getAttribute("fromVersion");
        toVersion = element.getAttribute("toVersion");
        loadSteps(element);
        status = MigrationStatus.PENDING;
    }

    private void loadSteps(Element element) {
        NodeList stepNodes = element.getElementsByTagName("*");
        for (int i = 0; i < stepNodes.getLength(); i++) {
            steps.add(createMigrationStep((Element) stepNodes.item(i)));
        }
    }

    private AbstractMigrationStep createMigrationStep(Element element) {
        String elementName = element.getNodeName();
        if ("sql-script".equals(elementName)) {
            return new SqlScriptMigrationStep(element);
        } else if ("migrator-class".equals(elementName)) {
            return new ClassBasedMigrationStep(element);
        } else {
            throw new IllegalArgumentException("Invalid migration step element name: " + elementName);
        }
    }

    void execute() throws MigrationStepFailedException {
        LOG.info("Executing data migration from version " + fromVersion + " to version " + toVersion);
        for (AbstractMigrationStep step : steps) {
            LOG.info("Executing " + step.toString());
            step.execute();
        }
    }

    /**
     * @return the version to migrate from
     */
    public String getFromVersion() {
        return fromVersion;
    }

    /**
     * @return the version to migrate to
     */
    public String getToVersion() {
        return toVersion;
    }

    /**
     * @return the status
     */
    public MigrationStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(MigrationStatus status) {
        this.status = status;
    }
}
