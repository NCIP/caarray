//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

/**
 * This is the exception thrown by all DAO class methods.
 * It encapsulates an error message, and optionally, a causing exception.
 *
 * @author Rashmi Srinivasa
 */
public class DAOException extends RuntimeException {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    /**
     * Constructs a DAO exception object with an error message.
     *
     * @param message An error message describing the exception.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructs a DAO exception object with an error message and a causing exception.
     *
     * @param message An error message describing the exception.
     * @param cause The underlying exception that caused this DAO exception.
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
