//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

/**
 * Status of a <code>CaArrayFile</code>.
 */
public enum FileStatus {

    /**
     * Uploaded, but not yet validated.
     */
    UPLOADED(true, true, true),

    /**
     * In the queue awaiting validation or import.
     */
    IN_QUEUE(false, false, false),

    /**
     * In the process of validating.
     */
    VALIDATING(false, false, false),

    /**
     * Included non-parsed data format in validation.
     */
    VALIDATED_NOT_PARSED(true, true, true),

    /**
     * Successfully validated.
     */
    VALIDATED(true, true, true),

    /**
     * Validation uncovered errors.
     */
    VALIDATION_ERRORS(true, true, true),

    /**
     * In the process of importing.
     */
    IMPORTING(false, false, false),

    /**
     * Successfully imported.
     */
    IMPORTED(true, false, false),

    /**
     * Successfully imported but data not parsed.
     */
    IMPORTED_NOT_PARSED(true, false, false),

    /**
     * Failed Import.
     */
    IMPORT_FAILED(true, true, true),

    /**
     * Arbitrary files associated directly with a project.
     */
    SUPPLEMENTAL(true, false, false);

    private boolean deletable = true;
    private boolean validatable = true;
    private boolean importable = true;

    FileStatus(boolean deletable, boolean validatable, boolean importable) {
        this.deletable = deletable;
        this.validatable = validatable;
        this.importable = importable;
    }

    /**
     * @return the deletable
     */
    public boolean isDeletable() {
        return this.deletable;
    }

    /**
     * @return the validatable
     */
    public boolean isValidatable() {
        return this.validatable;
    }

    /**
     * @return the importable
     */
    public boolean isImportable() {
        return this.importable;
    }
}
