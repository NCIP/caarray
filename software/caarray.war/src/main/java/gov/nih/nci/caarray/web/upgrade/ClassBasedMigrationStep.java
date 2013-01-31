//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import org.w3c.dom.Element;

/**
 * Upgrades the caArray application by invoking the upgrade method of a supplied migration
 * utility class.
 */
class ClassBasedMigrationStep extends AbstractMigrationStep {

    private final String className;
    private final Element element;

    ClassBasedMigrationStep(Element element) {
        this.element = element;
        this.className = getContent(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    void execute() throws MigrationStepFailedException {
        try {
            Class migratorClass = Class.forName(className);
            Object migratorObject = migratorClass.newInstance();
            if (migratorObject instanceof Migrator) {
                Migrator migrator = (Migrator) migratorObject;
                migrator.setElement(element);
                migrator.migrate();
            } else {
                throw new MigrationStepFailedException("Configured migration class "
                        + className + " is not an instance of Migrator");
            }
        } catch (ClassNotFoundException e) {
            throw new MigrationStepFailedException(e);
        } catch (InstantiationException e) {
            throw new MigrationStepFailedException(e);
        } catch (IllegalAccessException e) {
            throw new MigrationStepFailedException(e);
        } catch (RuntimeException e) {
            throw new MigrationStepFailedException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Migration class " + className;
    }

}
