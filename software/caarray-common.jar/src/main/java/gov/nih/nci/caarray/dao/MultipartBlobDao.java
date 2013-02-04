//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.file</code> package.
 * 
 */
public interface MultipartBlobDao extends CaArrayDao {
    /**
     * Delete all multipart blobs (and their contents) with given ids.
     * 
     * @param ids the ids of the blobs to delte
     */
    void deleteByIds(Iterable<Long> ids);
}
