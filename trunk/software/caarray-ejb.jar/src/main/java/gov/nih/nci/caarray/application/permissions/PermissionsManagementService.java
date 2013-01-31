//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.List;

/**
 * Interface to the PermissionsManagementService, provides for the creation and management
 * of authorization groups and the setting of permissions.
 */
public interface PermissionsManagementService {

    /**
     * The default JNDI name to use to lookup <code>PermissionsManagementService</code>.
     */
    String JNDI_NAME = "caarray/PermissionsManagementServiceBean/local";

    /**
     * Delete a collaborator group.
     *
     * @param group the group to delete
     * @throws CSTransactionException on CSM error
     */
    void delete(CollaboratorGroup group) throws CSTransactionException;

    /**
     * @return all collaborator groups in the system
     */
    List<CollaboratorGroup> getCollaboratorGroups();

    /**
     * Get all the collaborator groups owned by the current user.
     * @return all collaborator groups owned by the current user
     */
    List<CollaboratorGroup> getCollaboratorGroupsForCurrentUser();

    /**
     * Create a new CollaboratorGroup.  The owner of the group will be the
     * currently logged in user.  The group will have no members.
     *
     * @param name name of the collaborator group.
     * @return the new group
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    CollaboratorGroup create(String name) throws CSTransactionException, CSObjectNotFoundException;

    /**
     * Adds users to the target group.
     *
     * @param targetGroup group to add members to
     * @param users user ids to add (as strings)
     * @throws CSTransactionException  on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void addUsers(CollaboratorGroup targetGroup, List<Long> users) throws CSTransactionException,
            CSObjectNotFoundException;

    /**
     * Adds users to a CSM group.
     * @param groupName name of CSM group to add members to
     * @param usernames usernames to add
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void addUsers(String groupName, String... usernames) throws CSTransactionException, CSObjectNotFoundException;

    /**
     * Removes users from the target group.
     *
     * @param targetGroup group to remove members from
     * @param userIds user ids to remove (as strings)
     * @throws CSTransactionException  on CSM error
     */
    void removeUsers(CollaboratorGroup targetGroup, List<Long> userIds) throws CSTransactionException;

    /**
     * Renames a collaboration group.
     *
     * @param targetGroup group to rename
     * @param groupName new name
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void rename(CollaboratorGroup targetGroup, String groupName) throws CSTransactionException,
            CSObjectNotFoundException;

    /**
     * Returns users matching the given example user.
     *
     * @param u example user (may be null)
     * @return users in the system meeting the criteria
     */
    List<User> getUsers(User u);

    /**
     * Creates or updates an access profile.
     *
     * @param profile the profile to create or update
     */
    void saveAccessProfile(AccessProfile profile);

    /**
     * Changes the owner of a collaboration group.
     *
     * @param targetGroupId ID of group to change owner of
     * @param username new owner
     * @throws CSException on CSM error
     */
    void changeOwner(Long targetGroupId, String username) throws CSException;

    /**
     * Get all collaboration groups owned by a user.
     *
     * @param userId owner.
     * @return the collaboration groups owned by user with given id.
     */
    List<CollaboratorGroup> getCollaboratorGroupsForOwner(long userId);
}
