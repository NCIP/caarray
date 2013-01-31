//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

/**
 * Exception that indicates an Affymetrix array design file couldn't be successfully read.
 */
public final class AffymetrixArrayDesignReadException extends Exception {

    private static final long serialVersionUID = 1L;

    AffymetrixArrayDesignReadException(String message) {
        super(message);
    }

    /**
     * Create a new exception.
     * @param message error message
     * @param cause underlying cause
     */
    AffymetrixArrayDesignReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
