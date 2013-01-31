//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Collaborator group management action.
 */
@Validation
@Validations(
        requiredFields = @RequiredFieldValidator(
                fieldName = "groupName", key = "struts.validator.requiredString", message = ""),
        stringLengthFields = @StringLengthFieldValidator(
                fieldName = "groupName", maxLength = "254", message = "", key = "struts.validator.stringLength"
        )
)
@SuppressWarnings("PMD.TooManyMethods")
public class CollaboratorsAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private List<CollaboratorGroup> groups;
    private CollaboratorGroup targetGroup;
    private String groupName;
    private User targetUser = new User();
    private List<Long> users;
    private List<User> allUsers;

    /**
     * @return listGroups
     */
    @SkipValidation
    public String listGroups() {
        this.groups = ServiceLocatorFactory.getPermissionsManagementService().getCollaboratorGroupsForCurrentUser();
        return "list";
    }

    /**
     * Deletes the targeted CollaboratorGroup.
     * @return listGroups
     * @throws CSTransactionException on CSM error
     */
    @SkipValidation
    public String delete() throws CSTransactionException {
        String grpName = this.targetGroup.getGroup().getGroupName();
        ServiceLocatorFactory.getPermissionsManagementService().delete(this.targetGroup);
        ActionHelper.saveMessage(getText("collaboration.group.record.deleted", new String[] {grpName}));
        return listGroups();
    }

    /**
     * Create a new group, or edit the name of an existing group.
     *
     * @return listGroups
     * @throws CSException on CSM error
     */
    public String name() throws CSException {
        if (targetGroup == null) {
            ServiceLocatorFactory.getPermissionsManagementService().create(getGroupName());
        } else {
            ServiceLocatorFactory.getPermissionsManagementService().rename(getTargetGroup(), getGroupName());
        }
        ActionHelper.saveMessage(getText("collaboration.group.record.saved", new String[] {getGroupName()}));
        return listGroups();
    }

    /**
     * Takes user to the edit group page.
     * @return edit
     */
    @SkipValidation
    public String edit() {
        return Action.SUCCESS;
    }

    /**
     * Takes the user to the user details screen.
     * @return userDetail
     */
    @SkipValidation
    public String userDetail() {
        return Action.SUCCESS;
    }

    /**
     * Adds the selected users to the current collaborator group.
     * @return success
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    @SuppressWarnings({"unchecked", "PMD" })
    @SkipValidation
    public String addUsers() throws CSTransactionException, CSObjectNotFoundException {
        if (CollectionUtils.isNotEmpty(getUsers())) {
            ServiceLocatorFactory.getPermissionsManagementService().addUsers(getTargetGroup(), getUsers());
            String s = "Users";
            if (getUsers().size() == 1) {
                User u = SecurityUtils.getAuthorizationManager().getUserById(getUsers().get(0).toString());
                s = u.getFirstName() + " " + u.getLastName() + " (" + u.getLoginName() + ")";
            }
            ActionHelper.saveMessage(getText("collaboration.group.added", new String[] {s}));
        }
        setAllUsers((List<User>) CollectionUtils.subtract(ServiceLocatorFactory.getPermissionsManagementService()
                .getUsers(getTargetUser()), getTargetGroup().getGroup().getUsers()));
        return Action.SUCCESS;
    }

    /**
     * @return addUsers
     */
    @SkipValidation
    public String preAdd() {
        return "addUsers";
    }

    /**
     * Removes the selected users from the current collaborator group.
     * @return success
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    @SkipValidation
    public String removeUsers() throws CSTransactionException, CSObjectNotFoundException {
        if (CollectionUtils.isNotEmpty(getUsers())) {
            ServiceLocatorFactory.getPermissionsManagementService().removeUsers(getTargetGroup(), getUsers());
            String s = "Users";
            if (getUsers().size() == 1) {
                User u = SecurityUtils.getAuthorizationManager().getUserById(getUsers().get(0).toString());
                s = u.getFirstName() + " " + u.getLastName() + " (" + u.getLoginName() + ")";
            }
            ActionHelper.saveMessage(getText("collaboration.group.removed", new String[] {s}));
        }
        return Action.INPUT;
    }

    /**
     * @return editTable
     */
    @SkipValidation
    public String editTable() {
        return "editTable";
    }

    /**
     * @return the groups
     */
    public List<CollaboratorGroup> getGroups() {
        return this.groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<CollaboratorGroup> groups) {
        this.groups = groups;
    }

    /**
     * @return the targetGroup
     */
    public CollaboratorGroup getTargetGroup() {
        return this.targetGroup;
    }

    /**
     * @param targetGroup the targetGroup to set
     */
    public void setTargetGroup(CollaboratorGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    /**
     * @return the groupName
     */
    @RequiredStringValidator(message = "Group Name is Required")
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the targetUser
     */
    public User getTargetUser() {
        return this.targetUser;
    }

    /**
     * @param targetUser the targetUser to set
     */
    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    /**
     * @return the targetUserId
     */
    public Long getTargetUserId() {
        if (this.targetUser == null) {
            return null;
        }
        return this.targetUser.getUserId();
    }

    /**
     * @param id CSM user id
     * @throws CSObjectNotFoundException if not in CSM
     */
    public void setTargetUserId(Long id) throws CSObjectNotFoundException {
        this.targetUser = SecurityUtils.getAuthorizationManager().getUserById(id.toString());
    }

    /**
     * @return the users
     */
    public List<Long> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<Long> users) {
        this.users = users;
    }

    /**
     * @return the allUsers
     */
    public List<User> getAllUsers() {
        return allUsers;
    }

    /**
     * @param allUsers the allUsers to set
     */
    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate() {
        super.validate();
        if (!ActionHelper.isSkipValidationSetOnCurrentAction()) {
            if (StringUtils.isBlank(getGroupName())
                    || (getTargetGroup() != null
                            && getTargetGroup().getGroup().getGroupName().equals(getGroupName()))) {
                // Nothing to be done in this case
                return;
            }
            AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            Group g = new Group();
            g.setGroupName(getGroupName());
            GroupSearchCriteria gsc = new GroupSearchCriteria(g);
            List<Group> matchingGroups = am.getObjects(gsc);

            if (!matchingGroups.isEmpty()) {
                addFieldError("groupName", getText("collaboration.duplicateName", new String[] {getGroupName()}));
            }
        }
    }
}
