//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter;

/**
 * ConversionException thrown in event of copy number to MAGE-TAB data matrix format conversion failure.
 * @author dharley
 *
 */
public class ConversionException extends Exception {

    /**
     * Creates new ConversionException.
     * @param cause the cause
     */
    public ConversionException(final Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates new ConversionException.
     * @param message the message
     * @param cause the cause
     */
    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates new ConversionException.
     * @param message the message
     */
    public ConversionException(final String message) {
        super(message);
    }
    
}
