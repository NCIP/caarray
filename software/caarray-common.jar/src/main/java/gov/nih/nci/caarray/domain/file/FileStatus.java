//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.file;

import java.util.EnumSet;
import java.util.Set;

/**
 * Status of a <code>CaArrayFile</code>.
 */
public enum FileStatus {

    /**
     * Partially uploaded.
     */
    UPLOADING("Uploading"),

    /**
     * Uploaded, but not yet validated.
     */
    UPLOADED("Uploaded"),

    /**
     * In the queue awaiting validation or import.
     */
    IN_QUEUE("In Queue"),

    /**
     * In the process of validating.
     */
    VALIDATING("Validating"),

    /**
     * Included non-parsed data format in validation.
     */
    VALIDATED_NOT_PARSED("Validated Not Parsed"),

    /**
     * Successfully validated.
     */
    VALIDATED("Validated"),

    /**
     * Validation uncovered errors.
     */
    VALIDATION_ERRORS("Validation Errors"),

    /**
     * In the process of importing.
     */
    IMPORTING("Importing"),

    /**
     * Successfully imported.
     */
    IMPORTED("Imported"),

    /**
     * Successfully imported but data not parsed.
     */
    IMPORTED_NOT_PARSED("Imported Not Parsed"),

    /**
     * Failed Import.
     */
    IMPORT_FAILED("Import Failed"),

    /**
     * Arbitrary files associated directly with a project.
     */
    SUPPLEMENTAL("Supplemental");
    
    private final String displayName;

    FileStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * @return File status display name.
     */
    public String getDisplayValue() {
        return displayName;
    }

    /**
     * The set of FileStatuses in which a file can be deleted.
     */    
    public static final Set<FileStatus> DELETABLE_FILE_STATUSES = EnumSet.of(UPLOADING, UPLOADED, IMPORTED,
            IMPORT_FAILED, IMPORTED_NOT_PARSED, VALIDATED, VALIDATED_NOT_PARSED, VALIDATION_ERRORS, SUPPLEMENTAL);

    /**
     * The set of FileStatuses in which a file can be imported.
     */    
    public static final Set<FileStatus> IMPORTABLE_FILE_STATUSES = EnumSet.of(UPLOADED, IMPORT_FAILED, VALIDATED,
            VALIDATED_NOT_PARSED, VALIDATION_ERRORS, IN_QUEUE);

    /**
     * The set of FileStatuses in which a file can be validated.
     */    
    public static final Set<FileStatus> VALIDATABLE_FILE_STATUSES = EnumSet.of(UPLOADED, IMPORT_FAILED, VALIDATED,
            VALIDATED_NOT_PARSED, VALIDATION_ERRORS, IN_QUEUE);

    /**
     * @return the deletable
     */
    public boolean isDeletable() {
        return DELETABLE_FILE_STATUSES.contains(this);
    }

    /**
     * @return the validatable
     */
    public boolean isValidatable() {
        return VALIDATABLE_FILE_STATUSES.contains(this);
    }

    /**
     * @return the importable
     */
    public boolean isImportable() {
        return IMPORTABLE_FILE_STATUSES.contains(this);
    }
}
