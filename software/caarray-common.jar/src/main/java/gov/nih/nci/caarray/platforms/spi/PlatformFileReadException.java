//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms.spi;

import java.io.File;

/**
 * Exception that indicates a platform design or data file couldn't be successfully read.
 * 
 * @author dkokotov
 */
public final class PlatformFileReadException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final File file;

    /**
     * Create a new PlatformFileReadException for given file.
     * 
     * @param file the file that could not be read
     * @param message more details about the error
     */
    public PlatformFileReadException(File file, String message) {
        super(message);
        this.file = file;
    }

    /**
     * Create a new PlatformFileReadException for given file.
     * 
     * @param file the file that could not be read
     * @param message more details about the error
     * @param cause lower-level exception that led to the error
     */
    public PlatformFileReadException(File file, String message, Throwable cause) {
        super(message, cause);
        this.file = file;
    }

    /**
     * @return the file which couldn't be read
     */
    public File getFile() {
        return file;
    }
}
