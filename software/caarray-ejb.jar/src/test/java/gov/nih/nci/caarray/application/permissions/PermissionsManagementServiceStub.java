//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.permissions;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.Collections;
import java.util.List;

/**
 * Stub impl.
 */
public class PermissionsManagementServiceStub implements PermissionsManagementService {

    private CollaboratorGroup currentGroup;
    private Long currentGroupId;
    private boolean getCalled = false;
    private boolean getForUserCalled = false;
    private String name = null;
    private List<Long> addedUsers;
    private List<Long> removedUsers;
    private boolean getUsersCalled = false;
    private AccessProfile savedProfile;
    private boolean changeOwnerCalled = false;

    /**
     * @return the getUsersCalled
     */
    public boolean isGetUsersCalled() {
        return getUsersCalled;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(CollaboratorGroup group) {
        currentGroup = group;
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getCollaboratorGroups() {
        getCalled = true;
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getCollaboratorGroupsForCurrentUser() {
        getForUserCalled = true;
        return Collections.emptyList();
    }

    /**
     * @return the currentGroup
     */
    public CollaboratorGroup getCurrentGroup() {
        return currentGroup;
    }

    /**
     * @return the currentGroupId
     */
    public Long getCurrentGroupId() {
        return currentGroupId;
    }

    /**
     * @return the getCalled
     */
    public boolean isGetCalled() {
        return getCalled;
    }

    /**
     * @return the getForUserCalled
     */
    public boolean isGetForUserCalled() {
        return getForUserCalled;
    }

    /**
     * resets stub state.
     */
    public void reset() {
        currentGroup = null;
        getCalled = false;
        getForUserCalled = false;
        name = null;
        addedUsers = null;
        removedUsers = null;
        getUsersCalled = false;
        savedProfile = null;
        changeOwnerCalled = false;
    }

    /**
     * {@inheritDoc}
     */
    public CollaboratorGroup create(String n) throws CSTransactionException, CSObjectNotFoundException {
        name = n;
        return null;
    }

    /**
     * @return name of last group 'created'
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void addUsers(CollaboratorGroup targetGroup, List<Long> users) {
        addedUsers = users;
        currentGroup = targetGroup;
    }

    /**
     * {@inheritDoc}
     */
    public void addUsers(String groupName, String... usernames)
    throws CSTransactionException, CSObjectNotFoundException {
        // no op
    }

    /**
     * {@inheritDoc}
     */
    public void removeUsers(CollaboratorGroup targetGroup, List<Long> users) throws CSTransactionException {
        removedUsers = users;
        currentGroup = targetGroup;
    }

    /**
     * @return uses added via addUsers
     */
    public List<Long> getAddedUsers() {
        return addedUsers;
    }

    /**
     * @return users removed via removeusers
     */
    public List<Long> getRemovedUsers() {
        return removedUsers;
    }

    /**
     * {@inheritDoc}
     */
    public void rename(CollaboratorGroup targetGroup, String groupName) {
        name = groupName;
        currentGroup = targetGroup;
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User u) {
        getUsersCalled = true;
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public void saveAccessProfile(AccessProfile profile) {
        savedProfile = profile;
    }

    /**
     * @return the savedProfile
     */
    public AccessProfile getSavedProfile() {
        return savedProfile;
    }

    /**
     * {@inheritDoc}
     */
    public void changeOwner(Long targetGroupId, String username) throws CSTransactionException {
        changeOwnerCalled = true;
        currentGroupId = targetGroupId;
    }

    /**
     * @return the changeOwnerCalled
     */
    public boolean isChangeOwnerCalled() {
        return changeOwnerCalled;
    }
    public List<CollaboratorGroup> getCollaboratorGroupsForOwner(long userId) {
        return Collections.emptyList();
    }

}
