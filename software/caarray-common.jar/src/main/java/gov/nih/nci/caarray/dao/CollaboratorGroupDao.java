//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;

import java.util.List;

/**
 * Dao for CollaboratorGroups.
 */
public interface CollaboratorGroupDao extends CaArrayDao {

    /**
     * Gets all the CollaboratorGroups visible to the current user.  The returned listed will generally be the same
     * as the list returned by {@link #getAllForCurrentUser()} except when called by users with the
     * SystemAdministrator role.
     * @return all CollaboratorGroups in system visible to the current user
     */
    List<CollaboratorGroup> getAll();

    /**
     * Get all the collaborator groups owned by the current user.
     * @return all CollaboratorGroups owned by the current user
     */
    List<CollaboratorGroup> getAllForCurrentUser();

    /**
     * Get all the collaborator groups owned by the given user.
     *
     * @param userId owner's userId.
     * @return all CollaboratorGroups owned by the given user
     */
    List<CollaboratorGroup> getAllForUser(long userId);

}
