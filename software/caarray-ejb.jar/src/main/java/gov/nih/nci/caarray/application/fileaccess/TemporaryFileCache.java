//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import java.io.File;

/**
 * Manages a cache of temporary files on the filesystem. Ensures that, for a given name, only one temporary file exists.
 * Implementations of this should be used in preference to File.createTemporaryFile.
 * 
 * The typical lifecycle of a cache would be per request or API method call; however, other lifecycles are possible. In
 * all cases, at the end of the lifecycle the client code should take care to call deleteFiles() to ensure timely
 * reclamation of the temporary files. In the web tier this is probably best accomplished with a Filter; other clients
 * should take appropriate measures.
 * 
 * @author dkokotov
 */
public interface TemporaryFileCache {
    /**
     * Creates a temporary <code>File</code>. The client should eventually call deleteFile() to clean up. Throws
     * FileAccessException if file could not be created.
     * 
     * @param fileName the name of the file to create.
     * @return the temporary <code>File</code> that was created.
     */
    File createFile(String fileName);

    /**
     * checks whether a file with given name exists in this cache.
     * 
     * @param fileName name of file to check
     * @return whether the file with that name exists in the cache
     */
    boolean fileExists(String fileName);

    /**
     * Returns a file <code>java.io.File</code> which will hold the uncompressed data for the file with the given name.
     * The client should eventually call closeFile() for this file (or closeFiles()) to allow the temporary file to be
     * cleaned up.
     * 
     * @param fileName name of file whose contents are needed
     * @return the <code>java.io.File</code> pointing to the temporary file on the filesystem which will hold the
     *         uncompressed contents of the given logical file.
     */
    File getFile(String fileName);

    /**
     * Closes all temporary files opened or created by this cache and deletes the temporary directory used to store
     * them. This method should always be called at the conclusion of a session of working with file data.
     */
    void deleteFiles();

    /**
     * Deletes the temporary file previously created by this cache.
     * 
     * @param fileName name of the temporary <code>File</code> to delete.
     */
    void delete(String fileName);
}
