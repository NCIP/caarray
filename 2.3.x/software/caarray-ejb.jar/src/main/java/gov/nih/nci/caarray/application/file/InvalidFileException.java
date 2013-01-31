//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

/**
 * @author Winston Cheng
 *
 */
public class InvalidFileException extends Exception {
    private String resourceKey;

    /**
     * Constructs an invalid file exception with a resource key.
     * @param message Error message
     * @param key resource key for message to display on UI
     */
    public InvalidFileException(String message, String key) {
        super(message);
        this.resourceKey = key;
    }

    /**
     * @return the resourceKey
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * @param resourceKey the resourceKey to set
     */
    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }
}
