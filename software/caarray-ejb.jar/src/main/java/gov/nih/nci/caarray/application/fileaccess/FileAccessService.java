//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;
import java.io.InputStream;

/**
 * Provides file storage and retrieval services to the system.
 */
public interface FileAccessService {

    /**
     * The default JNDI name to use to lookup <code>FileAccessService</code>.
     */
    String JNDI_NAME = "caarray/FileAccessServiceBean/local";

    /**
     * Adds a new file to caArray file storage.
     *
     * @param file the file to store
     * @return the caArray file object.
     */
    CaArrayFile add(File file);

    /**
     * Adds a new child file to caArray file storage if the parent is not null. Else,
     * adds a new file with no parent.
     *
     * @param file the child file to store
     * @param parent the parent file. A null value implies a CaArrayFile with no parent.
     * @return the caArray file object.
     */
    CaArrayFile add(File file, CaArrayFile parent);

    /**
     * Adds a new file to caArray file storage.
     *
     * @param file the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @return the caArray file object.
     */
    CaArrayFile add(File file, String filename);

    /**
     * Adds a new child file to caArray file storage if the parent is not null. Else,
     * adds a new file with no parent.
     *
     * @param file the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @param parent the parent file. A null value implies a CaArrayFile with no parent.
     * @return the caArray file object.
     */
    CaArrayFile add(File file, String filename, CaArrayFile parent);

    /**
     * Adds a new file to caArray file storage.
     *
     * @param stream the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @return the caArray file object.
     */
    CaArrayFile add(InputStream stream, String filename);

    /**
     * Adds a new child file to caArray file storage if the parent is not null. Else,
     * adds a new file with no parent.
     *
     * @param stream the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @param parent the parent file. A null value implies a CaArrayFile with no parent.
     * @return the caArray file object.
     */
    CaArrayFile add(InputStream stream, String filename, CaArrayFile parent);

    /**
     * Removes a file from caArray file storage if it is legal for it to be removed. Returns true if the file was
     * actually removed.
     * If a CaArrayFile has children, they are also removed. If a child CaArrayFile is removed, it does not affect
     * the parent CaArrayFile.
     *
     * @param caArrayFile the caArrayFile to remove
     * @return true if the file was removed, false if the file could not be removed.
     */
    boolean remove(CaArrayFile caArrayFile);

    /**
     * Syncrhonize the state of data storage engines with the caArray database, ensuring that any data items that are no
     * longer referenced from caArray are removed from storage engines.
     *
     * This method should be called on a regular scheduled basis, eg by a timer task
     */
    void synchronizeDataStorage();

    /**
     * Cleans up unreferenced children files.
     *
     * This method deletes all files with parents and should only be called on startup, when the job queue is empty.
     * It allows the clean up any child files in the database not currently associated to any jobs.
     */
    void cleanupUnreferencedChildren();
    
    /**
     * Adds a new file chunk to caArray file storage.
     * 
     * @param file the file chunk to add
     * @param fileName the file name
     * @param fileSize the complete file size
     * @param caArrayFile the file to append to or null to create a new file
     * @return the CaArrayFile
     */
    CaArrayFile addChunk(File file, String fileName, Long fileSize, CaArrayFile caArrayFile);
}
