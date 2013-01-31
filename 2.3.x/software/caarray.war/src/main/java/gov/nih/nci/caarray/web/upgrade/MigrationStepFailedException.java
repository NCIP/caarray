//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;


/**
 * Indicates that a step failed in execution.
 */
class MigrationStepFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    MigrationStepFailedException(Throwable t) {
        super(t);
    }

    MigrationStepFailedException(String message) {
        super(message);
    }

    MigrationStepFailedException(String message, Throwable t) {
        super(message, t);
    }

}
