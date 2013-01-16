//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * @author jscott
 * Indicates an unexpected exception arose during the parsing of an Aligent file.
 */
class AgilentParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message description of the exception
     * @param cause underlying cause of the exception
     */
    AgilentParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message description of the exception
     */
   AgilentParseException(String message) {
        super(message);
    }
}
