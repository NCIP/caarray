//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;


/**
 * Indicates that a single file can't be handled because the content is invalid.
 */
public final class InvalidDataFileException extends Exception {

    private static final long serialVersionUID = 107348410227852110L;
    
    private final FileValidationResult fileValidationResult;

    /**
     * Creates a new instance that wraps a validation result.
     * 
     * @param fileValidationResult contains validation messages, including the error that caused this exception.
     */
    public InvalidDataFileException(FileValidationResult fileValidationResult) {
        super();
        this.fileValidationResult = fileValidationResult;
    }

    /**
     * @return the fileValidationResult
     */
    public FileValidationResult getFileValidationResult() {
        return fileValidationResult;
    }
    
}
