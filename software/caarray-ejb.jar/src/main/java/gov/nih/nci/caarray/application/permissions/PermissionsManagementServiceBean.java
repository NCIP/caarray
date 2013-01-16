//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.dao.CollaboratorGroupDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
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

import com.google.inject.Inject;

/**
 * Local implementation of interface.
 */
@Local(PermissionsManagementService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
@SuppressWarnings("unchecked")
// CSM API is unchecked
public class PermissionsManagementServiceBean implements PermissionsManagementService {

    private static final Logger LOG = Logger.getLogger(PermissionsManagementServiceBean.class);

    @EJB
    private GenericDataService genericDataService;

    private CaArrayHibernateHelper hibernateHelper;
    private CollaboratorGroupDao collaboratorGroupDao;
    private SearchDao searchDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(CollaboratorGroup group) throws CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, group);
        if (!group.getOwner().equals(CaArrayUsernameHolder.getCsmUser())) {
            throw new IllegalArgumentException(String.format(
                    "%s cannot delete group %s, because they are not the group owner.",
                    CaArrayUsernameHolder.getUser(), group.getGroup().getGroupName()));
        }

        for (final AccessProfile ap : group.getAccessProfiles()) {
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
        return this.genericDataService;
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
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CollaboratorGroup> getCollaboratorGroups() {
        LogUtil.logSubsystemEntry(LOG);
        final List<CollaboratorGroup> result = this.collaboratorGroupDao.getAll();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CollaboratorGroup> getCollaboratorGroupsForCurrentUser() {
        LogUtil.logSubsystemEntry(LOG);
        final List<CollaboratorGroup> result = this.collaboratorGroupDao.getAllForCurrentUser();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollaboratorGroup create(String name) throws CSTransactionException, CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, name);

        final Group group = new Group();
        group.setGroupName(name);
        group.setGroupDesc("Collaborator Group");
        SecurityUtils.createGroup(group);

        final User user = CaArrayUsernameHolder.getCsmUser();

        final CollaboratorGroup cg = new CollaboratorGroup(group, user);
        this.collaboratorGroupDao.save(cg);

        LogUtil.logSubsystemExit(LOG);
        return cg;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUsers(String groupName, String... usernames) throws CSTransactionException,
            CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, groupName, usernames);
        final AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        final Group group = SecurityUtils.findGroupByName(groupName);
        group.setGroupName(groupName);
        final List<Long> users = new ArrayList<Long>();
        for (final String username : usernames) {
            users.add(am.getUser(username).getUserId());
        }
        addUsersToGroup(group.getGroupId(), users, SecurityUtils.ANONYMOUS_GROUP.equals(groupName));
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUsers(CollaboratorGroup targetGroup, List<Long> users) throws CSObjectNotFoundException,
            CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, users);
        final Long groupId = targetGroup.getGroup().getGroupId();
        addUsersToGroup(groupId, users, false);
        LogUtil.logSubsystemExit(LOG);
    }

    private void addUsersToGroup(Long groupId, List<Long> users, boolean allowAnonymousUser)
            throws CSObjectNotFoundException, CSTransactionException {
        final Set<User> curUsers = SecurityUtils.getUsers(groupId);
        final Set<Long> newUsers = new HashSet<Long>(curUsers.size() + users.size());
        newUsers.addAll(users);
        for (final User u : curUsers) {
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
    @Override
    public void removeUsers(CollaboratorGroup targetGroup, List<Long> userIds) throws CSTransactionException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, userIds);
        for (final Long u : userIds) {
            SecurityUtils.removeUserFromGroup(targetGroup.getGroup().getGroupId(), u);
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rename(CollaboratorGroup targetGroup, String groupName) throws CSTransactionException,
            CSObjectNotFoundException {
        LogUtil.logSubsystemEntry(LOG, targetGroup, groupName);

        final Group g = targetGroup.getGroup();
        g.setGroupName(groupName);
        this.hibernateHelper.getCurrentSession().update(g);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers(User u) {
        LogUtil.logSubsystemEntry(LOG);
        List<User> resultUsers;
        try {
            resultUsers =
                    SecurityUtils.getAuthorizationManager().getObjects(
                            new UserSearchCriteria(convertToLikeProperties(u)));
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Could not create an example user", e);
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException("Could not create an example user", e);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException("Could not create an example user", e);
        }
        LogUtil.logSubsystemExit(LOG);
        return resultUsers;
    }

    private User convertToLikeProperties(User u) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        final User newUser = new User();
        if (u != null) {
            PropertyUtils.copyProperties(newUser, u);
            for (final PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(newUser)) {
                convertToLikeProperty(u, newUser, pd);
            }
        }
        return newUser;
    }

    private void convertToLikeProperty(User u, User newUser, PropertyDescriptor pd) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (pd.getPropertyType().equals(String.class)) {
            final String value = (String) PropertyUtils.getSimpleProperty(newUser, pd.getName());
            if (value != null) {
                PropertyUtils.setSimpleProperty(newUser, pd.getName(), value + "%");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAccessProfile(AccessProfile profile) {
        LogUtil.logSubsystemEntry(LOG, profile);
        this.collaboratorGroupDao.save(profile);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeOwner(Long targetGroupId, String username) throws CSException {
        LogUtil.logSubsystemEntry(LOG, targetGroupId, username);
        final AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        final CollaboratorGroup cg = this.searchDao.retrieve(CollaboratorGroup.class, targetGroupId);
        final User newOwner = am.getUser(username);
        cg.setOwner(newOwner);

        SecurityUtils.changeOwner(cg, newOwner);

        this.collaboratorGroupDao.save(cg);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CollaboratorGroup> getCollaboratorGroupsForOwner(long userId) {
        LogUtil.logSubsystemEntry(LOG, userId);
        final List<CollaboratorGroup> result = this.collaboratorGroupDao.getAllForUser(userId);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * @param hibernateHelper the hibernateHelper to set
     */
    @Inject
    public void setHibernateHelper(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * @param collaboratorGroupDao the collaboratorGroupDao to set
     */
    @Inject
    public void setCollaboratorGroupDao(CollaboratorGroupDao collaboratorGroupDao) {
        this.collaboratorGroupDao = collaboratorGroupDao;
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }
}
