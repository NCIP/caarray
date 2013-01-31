//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.project.FileProcessingResult;

/**
 * Exception used to report a problem during processing a file for upload or unpacking.
 * @author Winston Cheng
 */
public class InvalidFileException extends Exception {
    private final String file;
    private final FileProcessingResult result;
    private final String resourceKey;

    /**
     * Constructs an invalid file exception with a resource key.
     * @param file the name of the file which caused the issue.
     * @param message Error message
     * @param key resource key for message to display on UI
     */
    public InvalidFileException(String file, String key, String message) {
        this (file, key, null, message, null);
    }

    /**
     * Constructs an invalid file exception with a resource key.
     * @param file the name of the file which caused the issue.
     * @param message Error message
     * @param key resource key for message to display on UI
     * @param result the partial result of processing the file until the error occurred
     */
    public InvalidFileException(String file, String key, FileProcessingResult result, String message) {
        this(file, key, result, message, null);
    }

    /**
     * Constructs an invalid file exception with a resource key.
     * @param file the name of the file which caused the issue.
     * @param message Error message
     * @param key resource key for message to display on UI
     * @param cause underlying exception that caused the error
     */
    public InvalidFileException(String file, String key, String message, Throwable cause) {
        this(file, key, null, message, cause);
    }

    /**
     * Constructs an invalid file exception with a resource key.
     * @param file the name of the file which caused the issue.
     * @param message Error message
     * @param key resource key for message to display on UI
     * @param cause underlying exception that caused the error
     * @param result the partial result of processing the file until the error occurred
     */
    public InvalidFileException(String file, String key, FileProcessingResult result, String message, 
            Throwable cause) {
        super(message, cause);
        this.file = file;
        this.resourceKey = key;
        this.result = result;
    }

    /**
     * @return the resourceKey
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @return the result
     */
    public FileProcessingResult getResult() {
        return result;
    }        
}
