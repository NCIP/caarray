//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

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
     * Adds a new file to caArray file storage.
     *
     * @param file the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @return the caArray file object.
     */
    CaArrayFile add(File file, String filename);

    /**
     * Adds a new file to caArray file storage.
     *
     * @param stream the file to store
     * @param filename the filename for the new CaArrayFile -- may be different from file.getName()
     * @return the caArray file object.
     */
    CaArrayFile add(InputStream stream, String filename);

    /**
     * Removes a file from caArray file storage.
     *
     * @param caArrayFile the caArrayFile to remove
     */
    void remove(CaArrayFile caArrayFile);

    /**
     * Saves a file to the caArray file storage.
     *
     * @param caArrayFile the caArrayFile to store
     */
    void save(CaArrayFile caArrayFile);

    /**
     * Unzips a .zip file, removes it from <code>files</code> and adds the extracted files to <code>files</code>.
     *
     * @param files the list of files to unzip and the files extracted from the zips
     * @param fileNames the list of filenames to go along with the list of files
     */
    void unzipFiles(List<File> files, List<String> fileNames);

}
