//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import org.w3c.dom.Element;

/**
 * Base class for all migration steps.
 */
abstract class AbstractMigrationStep {

    AbstractMigrationStep() {
        super();
    }

    abstract void execute() throws MigrationStepFailedException;

    final String getContent(Element element) {
        return element.getFirstChild().getNodeValue();
    }

}
