//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;

import java.net.URI;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.file</code> package.
 *
 */
public interface FileDao extends CaArrayDao {
    /**
     * Performs a query for files by the given criteria.
     *
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching files
     */
    List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria);

    /**
     * Find files belonging to given project that can be deleted.
     *
     * @param projectId id of the project
     * @return a list of deletable files in given project
     */
    List<CaArrayFile> getDeletableFiles(Long projectId);

    /**
     * Get the data handles for all file data in the persistent store.
     *
     * @return a List of URIs for the data handles corresponding to the data underlying all CaArrayFiles in the system
     */
    List<URI> getAllFileHandles();

    /**
     * Cleans up unreferenced children files.
     *
     * This method deletes all files with parents and should only be called on startup, when the job queue is empty.
     * It allows the clean up any child files in the database not currently associated to any jobs.
     */
    void cleanupUnreferencedChildren();
    
    /**
     * Finds a partially uploaded file.
     * 
     * @param projectId id of the project
     * @param fileName file name
     * @param fileSize size of the complete file
     * @return the CaArrayFile if any is found
     */
    CaArrayFile getPartialFile(Long projectId, String fileName, long fileSize);

}
