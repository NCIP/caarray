//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

/**
 * Thrown to indicate there was a problem storing or retrieving data from the data storage.
 */
public class DataStoreException extends RuntimeException {

    private static final long serialVersionUID = 3582622697786140397L;

    /**
     * Constructor with message and cause.
     * 
     * @param message error message
     * @param cause the underlying exception which caused the error
     */
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with message.
     * 
     * @param message error message
     */
    public DataStoreException(String message) {
        super(message);
    }
}
