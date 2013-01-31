//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;


/**
 * Indicates that a files can't be handled because the content is invalid.
 */
public final class InvalidDataException extends Exception {

    private static final long serialVersionUID = 107348410227852110L;
    
    private final ValidationResult validationResult;

    /**
     * Creates a new instance that wraps a validation result.
     * 
     * @param validationResult contains validation messages, including the error that caused this exception.
     */
    public InvalidDataException(ValidationResult validationResult) {
        super(validationResult.toString());
        this.validationResult = validationResult;
    }

    /**
     * @return the validationResult
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

}
