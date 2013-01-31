//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.hibernate.criterion.MatchMode;

/**
 * Local implementation of interface.
 */
@Local(PermissionsManagementService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@SuppressWarnings("unchecked") // CSM API is unchecked
public class PermissionsManagementServiceBean implements PermissionsManagementService {

    private static final Logger LOG = Logger.getLogger(PermissionsManagementServiceBean.class);

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    @EJB private GenericDataService genericDataService;

    /**
     * {@inheritDoc}
     */
    public void delete(CollaboratorGroup group) throws CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, group);
        if (!group.getOwner().equals(UsernameHolder.getCsmUser())) {
            throw new IllegalArgumentException(
                    String.format("%s cannot delete group %s, because they are not the group owner.",
                                  UsernameHolder.getUser(), group.getGroup().getGroupName()));
        }

        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        for (AccessProfile ap : group.getAccessProfiles()) {
            ap.getProject().removeGroupProfile(group);
            getGenericDataService().delete(ap);
        }
        getGenericDataService().delete(group);
        am.removeGroup(group.getGroup().getGroupId().toString());

        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * @return the genericDataService
     */
    public GenericDataService getGenericDataService() {
        return genericDataService;
    }

    /**
     * @param genericDataService the genericDataService to set
     */
    public void setGenericDataService(GenericDataService genericDataService) {
        this.genericDataService = genericDataService;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CollaboratorGroup> getCollaboratorGroups() {
        LogUtil.logSubsystemEntry(LOG);
        List<CollaboratorGroup> result = getDaoFactory().getCollaboratorGroupDao().getAll();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * @return the daoFactory
     */
    public CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * @param daoFactory the daoFactory to set
     */
    public void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    public CollaboratorGroup create(String name) throws CSTransactionException, CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, name);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        Group group = new Group();
        group.setGroupName(name);
        group.setGroupDesc("Collaborator Group");
        group.setApplication(SecurityUtils.getApplication());
        am.createGroup(group);

        User user = UsernameHolder.getCsmUser();

        CollaboratorGroup cg = new CollaboratorGroup(group, user);
        getDaoFactory().getCollaboratorGroupDao().save(cg);

        LogUtil.logSubsystemExit(LOG);
        return cg;

    }

    /**
     * {@inheritDoc}
     */
    public void addUsers(String groupName, String... usernames)
    throws CSTransactionException, CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, groupName, usernames);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        Group group = new Group();
        group.setGroupName(groupName);
        GroupSearchCriteria gsc = new GroupSearchCriteria(group);
        List<Group> groupList = am.getObjects(gsc);
        String groupId = groupList.get(0).getGroupId().toString();
        List<String> users = new ArrayList<String>();
        for (String username : usernames) {
            String userId = am.getUser(username).getUserId().toString();
            users.add(userId);
        }
        addUsersToGroup(groupId, users, SecurityUtils.ANONYMOUS_GROUP.equals(groupName));
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void addUsers(CollaboratorGroup targetGroup, List<String> users)
    throws CSTransactionException, CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, users);
        String groupId = targetGroup.getGroup().getGroupId().toString();
        addUsersToGroup(groupId, users, false);
        LogUtil.logSubsystemExit(LOG);
    }

    private void addUsersToGroup(String groupId, List<String> users, boolean allowAnonymousUser)
        throws CSTransactionException, CSObjectNotFoundException {
        // This is a hack.  We should simply call am.assignUserToGroup, but that method appears to be buggy.
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        Set<User> curUsers = am.getUsers(groupId);
        Set<String> newUsers = new HashSet<String>(curUsers.size() + users.size());
        newUsers.addAll(users);
        for (User u : curUsers) {
            newUsers.add(u.getUserId().toString());
        }
        if (!allowAnonymousUser) {
            newUsers.remove(SecurityUtils.getAnonymousUser().getUserId().toString());           
        }
 
        String[] userIds = newUsers.toArray(new String[] {});
        am.assignUsersToGroup(groupId, userIds);
    }


    /**
     * {@inheritDoc}
     */
    public void removeUsers(CollaboratorGroup targetGroup, List<String> users) throws CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, users);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        for (String u : users) {
            am.removeUserFromGroup(targetGroup.getGroup().getGroupId().toString(), u);
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void rename(CollaboratorGroup targetGroup, String groupName)
    throws CSTransactionException, CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, groupName);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        Group g = am.getGroupById(targetGroup.getGroup().getGroupId().toString());
        g.setGroupName(groupName);
        am.modifyGroup(g);
        HibernateUtil.getCurrentSession().refresh(targetGroup.getGroup());
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User u) {
        LogUtil.logSubsystemEntry(LOG);
        List<User> result =
                new ArrayList<User>(getDaoFactory().getArrayDao().queryEntityByExample(u == null ? new User() : u,
                        MatchMode.START));
        // do not include the anonymous user
        result.remove(SecurityUtils.getAnonymousUser());

        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAccessProfile(AccessProfile profile) {
        LogUtil.logSubsystemEntry(LOG, profile);
        getDaoFactory().getCollaboratorGroupDao().save(profile);
        LogUtil.logSubsystemExit(LOG);
    }
}
