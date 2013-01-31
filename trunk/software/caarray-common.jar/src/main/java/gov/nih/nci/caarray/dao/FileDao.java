//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;

import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.file</code> package.
 *
 */
public interface FileDao extends CaArrayDao {

    /**
     * Method to delete associated Blobs from a project in anticipation of project
     * deletion using native sql.
     * @param projectId the id of the project being manipulated
     */
    void deleteSqlBlobsByProjectId(Long projectId);

    /**
     * Method to delete associated Blobs from a project in anticipation of project
     * deletion using hql.
     * @param projectId the id of the project being manipulated
     */
    void deleteHqlBlobsByProjectId(Long projectId);
    
    /**
     * Performs a query for files by the given criteria.
     *
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching files
     */
    List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria);
}
