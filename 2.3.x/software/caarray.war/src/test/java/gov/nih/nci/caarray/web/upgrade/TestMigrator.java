//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

/**
 * @author Winston Cheng
 *
 */
public class TestMigrator implements Migrator {
    private static boolean migrated;

    public void migrate() throws MigrationStepFailedException {
        migrated = true;
    }

    /**
     * @return the migrated
     */
    public static boolean isMigrated() {
        return migrated;
    }

    /**
     * @param migrated the migrated to set
     */
    public static void setMigrated(boolean migrated) {
        TestMigrator.migrated = migrated;
    }
}
