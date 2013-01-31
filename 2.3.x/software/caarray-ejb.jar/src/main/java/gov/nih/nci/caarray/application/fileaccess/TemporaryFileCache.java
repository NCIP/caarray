//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;

/**
 * Provides a per-thread cache of uncompressed file data from CaArrayFiles in temporary files on the filesystem.
 * Each thread of execution should take care to call TemporaryFileCache.getInstance().closeFiles() prior to
 * termination to ensure timely reclamation of the temporary files. In the web tier this is probably best accomplished
 * with a Filter; other threads should take appropriate measures.
 * @author dkokotov
 */
public interface TemporaryFileCache {
    /**
     * Returns a file <code>java.io.File</code> which will hold the uncompressed data for the
     * <code>CaArrayFile</code> object provided. The client should eventually call closeFile() for this
     * <code>CaArrayFile</code> (or closeFiles()) to allow the temporary file to be cleaned up.
     *
     * @param caArrayFile logical file whose contents are needed
     * @return the <code>java.io.File</code> pointing to the temporary file on the filesystem which will
     * hold the uncompressed contents of the given logical file.
     */
    File getFile(CaArrayFile caArrayFile);

    /**
     * Creates a temporary <code>File</code>. The client should eventually call deleteFile() to clean up.
     *
     * @param fileName the name of the file to create.
     * @return the temporary <code>File</code> that was created.
     */
    File createFile(String fileName);

    /**
     * Closes all temporary files opened in the current session and deletes the temporary directory used to store them.
     * This method should always be called at the conclusion of a session of working with file data.
     */
    void closeFiles();

    /**
     * Closes the file corresponding to the given logical file opened in the current session. Note that at the end
     * of the session of working with file data, you should still call closeFiles() to perform final cleanup
     * even if all files had been previously closed via calls to this method
     * @param caarrayFile the logical file to close the filesystem file for
     */
    void closeFile(CaArrayFile caarrayFile);

    /**
     * Deletes the temporary file.
     *
     * @param file the temporary <code>File</code> to delete.
     */
    void delete(File file);
}
