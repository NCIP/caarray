//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter;

/**
 * InvalidOptionsException indicates incorrect options specified.
 * @author dharley
 *
 */
public class InvalidOptionsException extends Exception {
    
    private static final String DEFAULT_MESSAGE = "Invalid options specified.";
    
    /**
     * Creates new InvalidOptionsException.
     * @param cause the cause
     */
    public InvalidOptionsException(final Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates new InvalidOptionsException.
     * @param message the message
     * @param cause the cause
     */
    public InvalidOptionsException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates new InvalidOptionsException.
     * @param message the message
     */
    public InvalidOptionsException(final String message) {
        super(message);
    }
    
}
