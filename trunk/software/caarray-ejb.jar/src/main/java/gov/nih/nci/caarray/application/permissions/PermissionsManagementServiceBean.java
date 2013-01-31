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
import gov.nih.nci.security.dao.UserSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

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

        for (AccessProfile ap : group.getAccessProfiles()) {
            ap.getProject().removeGroupProfile(group);
            getGenericDataService().delete(ap);
        }
        getGenericDataService().delete(group);
        SecurityUtils.removeGroup(group.getGroup().getGroupId());

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
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CollaboratorGroup> getCollaboratorGroupsForCurrentUser() {
        LogUtil.logSubsystemEntry(LOG);
        List<CollaboratorGroup> result = getDaoFactory().getCollaboratorGroupDao().getAllForCurrentUser();
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
        
        Group group = new Group();
        group.setGroupName(name);
        group.setGroupDesc("Collaborator Group");
        SecurityUtils.createGroup(group);

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
        Group group = SecurityUtils.findGroupByName(groupName);
        group.setGroupName(groupName);
        List<Long> users = new ArrayList<Long>();
        for (String username : usernames) {
            users.add(am.getUser(username).getUserId());
        }
        addUsersToGroup(group.getGroupId(), users, SecurityUtils.ANONYMOUS_GROUP.equals(groupName));
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void addUsers(CollaboratorGroup targetGroup, List<Long> users)
            throws CSObjectNotFoundException, CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, users);
        Long groupId = targetGroup.getGroup().getGroupId();
        addUsersToGroup(groupId, users, false);
        LogUtil.logSubsystemExit(LOG);
    }

    private void addUsersToGroup(Long groupId, List<Long> users, boolean allowAnonymousUser)
            throws CSObjectNotFoundException, CSTransactionException {
        Set<User> curUsers = SecurityUtils.getUsers(groupId);
        Set<Long> newUsers = new HashSet<Long>(curUsers.size() + users.size());
        newUsers.addAll(users);
        for (User u : curUsers) {
            newUsers.add(u.getUserId());
        }
        if (!allowAnonymousUser) {
            newUsers.remove(SecurityUtils.getAnonymousUser().getUserId());           
        }

        SecurityUtils.assignUsersToGroup(groupId, newUsers);
    }


    /**
     * {@inheritDoc}
     */
    public void removeUsers(CollaboratorGroup targetGroup, List<Long> userIds) throws CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, userIds);
        for (Long u : userIds) {
            SecurityUtils.removeUserFromGroup(targetGroup.getGroup().getGroupId(), u);
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void rename(CollaboratorGroup targetGroup, String groupName) throws CSTransactionException,
            CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, groupName);
        
        Group g = targetGroup.getGroup();
        g.setGroupName(groupName);
        HibernateUtil.getCurrentSession().update(g);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User u) {
        LogUtil.logSubsystemEntry(LOG);
        List<User> resultUsers;
        try {
            resultUsers = SecurityUtils.getAuthorizationManager()
                    .getObjects(new UserSearchCriteria(convertToLikeProperties(u)));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not create an example user", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not create an example user", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not create an example user", e);
        }
        LogUtil.logSubsystemExit(LOG);
        return resultUsers;
    }
        
    private User convertToLikeProperties(User u) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        User newUser = new User();        
        if (u != null) {
            PropertyUtils.copyProperties(newUser, u);
            for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(newUser)) {
                convertToLikeProperty(u, newUser, pd);
            }            
        }
        return newUser;
    }

    private void convertToLikeProperty(User u, User newUser, PropertyDescriptor pd) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (pd.getPropertyType().equals(String.class)) {
            String value = (String) PropertyUtils.getSimpleProperty(newUser, pd.getName());
            if (value != null) {
                PropertyUtils.setSimpleProperty(newUser, pd.getName(), value + "%");
            }
        }
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

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeOwner(Long targetGroupId, String username) throws CSException {
        LogUtil.logSubsystemEntry(LOG, targetGroupId, username);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        CollaboratorGroup cg = getDaoFactory().getSearchDao().retrieve(CollaboratorGroup.class, targetGroupId);
        User newOwner = am.getUser(username);
        cg.setOwner(newOwner);

        SecurityUtils.changeOwner(cg, newOwner);

        getDaoFactory().getCollaboratorGroupDao().save(cg);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getCollaboratorGroupsForOwner(long userId) {
        LogUtil.logSubsystemEntry(LOG, userId);
        List<CollaboratorGroup> result = getDaoFactory().getCollaboratorGroupDao().getAllForUser(userId);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }
}
