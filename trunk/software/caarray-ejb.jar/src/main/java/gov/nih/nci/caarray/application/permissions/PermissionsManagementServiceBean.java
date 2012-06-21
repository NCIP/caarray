/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
