//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

/**
 * Indicates a problem storing or retrieving files.
 */
public class FileAccessException extends RuntimeException {

    private static final long serialVersionUID = 3582622697786140397L;

    FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
