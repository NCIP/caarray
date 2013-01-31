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
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.Collections;
import java.util.List;

/**
 * Stub impl.
 */
public class PermissionsManagementServiceStub implements PermissionsManagementService {

    private CollaboratorGroup currentGroup;
    private boolean getCalled = false;
    private String name = null;
    private List<String> addedUsers;
    private List<String> removedUsers;
    private boolean getUsersCalled = false;
    private AccessProfile savedProfile;

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
     * @return the deletedGroup
     */
    public CollaboratorGroup getCurrentGroup() {
        return currentGroup;
    }

    /**
     * @return the getCalled
     */
    public boolean isGetCalled() {
        return getCalled;
    }

    /**
     * resets stub state.
     */
    public void reset() {
        currentGroup = null;
        getCalled = false;
        name = null;
        addedUsers = null;
        removedUsers = null;
        getUsersCalled = false;
        savedProfile = null;
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
    public void addUsers(CollaboratorGroup targetGroup, List<String> users) {
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
    public void removeUsers(CollaboratorGroup targetGroup, List<String> users) throws CSTransactionException {
        removedUsers = users;
        currentGroup = targetGroup;
    }

    /**
     * @return uses added via addUsers
     */
    public List<String> getAddedUsers() {
        return addedUsers;
    }

    /**
     * @return users removed via removeusers
     */
    public List<String> getRemovedUsers() {
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
}
