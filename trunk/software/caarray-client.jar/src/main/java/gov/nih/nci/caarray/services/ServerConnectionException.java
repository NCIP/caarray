//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

/**
 * Indicates a problem connecting to a caArray server (or a dropped connection).
 */
public class ServerConnectionException extends Exception {

    private static final long serialVersionUID = 4865588860956168307L;

    /**
     *
     * @param message the message of the exception
     * @param cause the cause of the exception
     */
    public ServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
