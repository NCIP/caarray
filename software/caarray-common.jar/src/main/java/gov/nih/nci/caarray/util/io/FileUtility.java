//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import gov.nih.nci.caarray.magetab.io.FileRef;
import java.io.File;

/**
 * Utility methods for file handling.
 */
public final class FileUtility {

    /**
     * Prevent instantiation.
     */
    private FileUtility() {
        super();
    }
    
    /**
     * Simple file argument checker to check that a file is not null and exists.
     *
     * @param file the file to check.
     */
    public static void checkFileExists(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument was null.");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " does not exist.");
        }
    }

    /**
     * Simple file argument checker to check that a file is not null and exists.
     *
     * @param file the file to check.
     * @since 2.4.0
     */
    public static void checkFileExists(FileRef file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument was null.");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("File " + file + " does not exist.");
        }
    }
    
}
