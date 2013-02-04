//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydesign;

/**
 * Exception that indicates an Array Design couldn't be successfully deleted.
 */
public final class ArrayDesignDeleteException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception.
     * @param message error message
     */
    public ArrayDesignDeleteException(String message) {
        super(message);
    }

    /**
     * Create a new exception.
     * @param message error message
     * @param cause underlying cause
     */
    ArrayDesignDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

}
