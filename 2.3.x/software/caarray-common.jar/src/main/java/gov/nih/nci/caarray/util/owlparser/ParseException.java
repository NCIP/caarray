//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.owlparser;

/**
 * Exception thrown when there is an error parsing the owl.
 * @author dkokotov
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Default.
     */
    public ParseException() {
        super();
    }

    /**
     * PArse error with given message.
     * @param message the message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * PArse error with given cause.
     * @param cause the cause
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * PArse error with given message and cause.
     * @param message the message
     * @param cause the cause
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
