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
 * Manages a cache of uncompressed file data from CaArrayFiles in temporary files on the filesystem. Also provides a
 * facility for creating arbitrary temporary files for other purposes - this should be used in preference to
 * File.createTemporaryFile.
 * 
 * The typical lifecycle of a cache would be per request or API method call; however, other lifecycles are possible. In
 * all cases, at the end of the lifecycle the client code should take care to call
 * TemporaryFileCache.getInstance().closeFiles() to ensure timely reclamation of the temporary files. In the web tier
 * this is probably best accomplished with a Filter; other threads should take appropriate measures.
 * 
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
     * Returns a file <code>java.io.File</code> which will hold the data for the
     * <code>CaArrayFile</code> object provided. The client should eventually call closeFile() for this
     * <code>CaArrayFile</code> (or closeFiles()) to allow the temporary file to be cleaned up.
     *
     * @param caArrayFile logical file whose contents are needed
     * @param uncompressed if true, the return file will hold the uncompressed data for the given logical file,
     * otherwise it will hold the GZip-compressed data.
     * @return the <code>java.io.File</code> pointing to the temporary file on the filesystem which will
     * hold the uncompressed contents of the given logical file.
     */
    File getFile(CaArrayFile caArrayFile, boolean uncompressed);

    /**
     * Creates a temporary <code>File</code>. The client should eventually call deleteFile() to clean up.
     *
     * @param fileName the name of the file to create.
     * @return the temporary <code>File</code> that was created.
     */
    File createFile(String fileName);

    /**
     * Closes all temporary files opened or created by this cache and deletes the temporary directory used to
     * store them. This method should always be called at the conclusion of a session of working with file data.
     */
    void closeFiles();

    /**
     * Closes the file corresponding to the given logical file opened by this cache for uncompressed data. Note
     * that at the end of the session of working with file data, you should still call closeFiles() to perform final
     * cleanup even if all files had been previously closed via calls to this method. 
     * 
     * @param caarrayFile the logical file to close the filesystem file for. 
     */
    void closeFile(CaArrayFile caarrayFile);

    /**
     * Closes the file corresponding to the given logical file opened by this cache for given type of data
     * access. Note that at the end of the session of working with file data, you should still call closeFiles() to
     * perform final cleanup even if all files had been previously closed via calls to this method.
     * 
     * @param caarrayFile the logical file to close the filesystem file for.
     * @param uncompressed if true, this will close the temporary file with uncompressed data for given logical file,
     *            otherwise this will close the temporary file with compressed data.
     */
    void closeFile(CaArrayFile caarrayFile, boolean uncompressed);

    /**
     * Deletes the temporary file created by this cache (by calling createFile()).
     *
     * @param file the temporary <code>File</code> to delete.
     */
    void delete(File file);
}
