//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

/**
 * Thrown by a DataStorage implementation to indicate it was given a handle with a scheme it does not support.
 */
public class UnsupportedSchemeException extends DataStoreException {

    private static final long serialVersionUID = 3582622697786140397L;

    /**
     * Constructor with message and cause.
     * 
     * @param message error message
     * @param cause the underlying exception which caused the error
     */
    public UnsupportedSchemeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with message.
     * 
     * @param message error message
     */
    public UnsupportedSchemeException(String message) {
        super(message);
    }
}
