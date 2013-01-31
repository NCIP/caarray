//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.validation.ValidationResult;

/**
 * Indicates that the MAGE-TAB set can't be succesfully parsed because the content is invalid.
 */
public final class InvalidMageTabException extends Exception {

    private static final long serialVersionUID = 107348410227852110L;

    private final ValidationResult validationResult;

    InvalidMageTabException(ValidationResult validationResult) {
        super();
        this.validationResult = validationResult;
    }

    /**
     * @return the validationResult
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

}
