//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import org.w3c.dom.Element;

/**
 * Interface to be implemented by any migration utility classes.
 */
interface Migrator {

    /**
     * Perform the migration step.
     * @throws MigrationStepFailedException if the migration failed.
     */
    void migrate() throws MigrationStepFailedException;

    /**
     * Set the XML element used to define this Migrator.
     * @param element the XML element to set
     */
    void setElement(Element element);
}
