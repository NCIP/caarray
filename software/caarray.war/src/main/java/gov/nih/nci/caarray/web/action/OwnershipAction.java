//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author gax
 */
public class OwnershipAction extends ActionSupport {
    private static final long serialVersionUID = 1L;

    private List<User> users;
    private List<CollaboratorGroup> groups;
    private List<Project> projects;
    private User targetUser;
    private User owner;
    private User user = new User();
    private List<Long> groupIds = new ArrayList<Long>();
    private List<Long> projectIds = new ArrayList<Long>();

    /**
     * @return list of all users.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @return the groups
     */
    public List<CollaboratorGroup> getGroups() {
        return this.groups;
    }

    /**
     * load the list of all users.
     * @return SUCCESS.
     */
    public String listOwners() {
        users = ServiceLocatorFactory.getPermissionsManagementService().getUsers(user);
        return SUCCESS;
    }

    /**
     * @return the targetUser
     */
    public User getTargetUser() {
        return this.targetUser;
    }

    /**
     * @return the owner
     */
    public User getOwner() {
        return this.owner;
    }

    /**
     * @param targetUser the targetUser to set
     */
    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    /**
     * @param owner the current owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
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
     * @return the current owner's user id.
     */
    public Long getOwnerId() {
        if (this.owner == null) {
            return null;
        }
        return this.owner.getUserId();
    }

    /**
     * @return list of owner's projects.
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @param id CSM user id
     * @throws CSObjectNotFoundException if not in CSM
     */
    public void setTargetUserId(Long id) throws CSObjectNotFoundException {
        setTargetUser(SecurityUtils.getAuthorizationManager().getUserById(id.toString()));
    }

    /**
     * @param id CSM user id
     * @throws CSObjectNotFoundException if not in CSM
     */
    public void setOwnerId(Long id) throws CSObjectNotFoundException {
        setOwner(SecurityUtils.getAuthorizationManager().getUserById(id.toString()));
    }

    /**
     * @return user search holder.
     */
    public User getUser() {
        return user;
    }

    /**
     * set the search user.
     * @param user user search form holder.
     */
    public void setUser(User user) {
        this.user = user;
    }



    /**
     * @return selected project ids
     */
    public List<Long> getProjectIds() {
        return projectIds;
    }

    /**
     * @param projectIds selected project ids
     */
    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    /**
     * @return selected group ids
     */
    public List<Long> getGroupIds() {
        return groupIds;
    }

    /**
     * @param groupIds selected group ids
     */
    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * @return SUCCESS.
     */
    public String assets() {
        if (owner == null) {
            ActionHelper.saveMessage("Select a user");
            return "listOwners";
        }
        long id = owner.getUserId().longValue();
        groups = ServiceLocatorFactory.getPermissionsManagementService().getCollaboratorGroupsForOwner(id);
        projects = ServiceLocatorFactory.getProjectManagementService().getProjectsForOwner(owner);
        
        return SUCCESS;
    }

    /**
     * @return SUCCESS.
     * @throws CSException is unable to change owner.
     */
    public String reassign() throws CSException {
        if (owner == null) {
            addActionError("Select a new Owner");
            users = ServiceLocatorFactory.getPermissionsManagementService().getUsers(targetUser);
            users.remove(targetUser);
            return "newOwner";
        }

        for (long gId : groupIds) {
            ServiceLocatorFactory.getPermissionsManagementService().changeOwner(gId, owner.getLoginName());
            ActionHelper.saveMessage("Collaboration Group " + gId + " assigned to " + owner.getLoginName());
        }
        for (long pId : projectIds) {
            ServiceLocatorFactory.getProjectManagementService().changeOwner(pId, owner.getLoginName());
            ActionHelper.saveMessage("Experiment " + pId + " assigned to " + owner.getLoginName());
        }

        return "listOwners";
    }


    /**
     * @return SUCCESS
     */
    public String usersTable() {
        users = ServiceLocatorFactory.getPermissionsManagementService().getUsers(user);
        return SUCCESS;
    }

    /**
     * @return SUCCESS
     */
    public String newOwner() {
        if ((projectIds == null || projectIds.isEmpty()) && (groupIds == null || groupIds.isEmpty())) {
            addActionError("Select a Experiment or Group");
            owner =  targetUser;
            assets();
            return "assets";
        }
        users = ServiceLocatorFactory.getPermissionsManagementService().getUsers(user);
        users.remove(targetUser);
        return SUCCESS;
    }
}
