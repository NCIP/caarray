//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;

import java.util.List;

/**
 * Dao for CollaboratorGroups.
 */
public interface CollaboratorGroupDao extends CaArrayDao {

    /**
     * @return all CollaboratorGroups in system.
     */
    List<CollaboratorGroup> getAll();
}
