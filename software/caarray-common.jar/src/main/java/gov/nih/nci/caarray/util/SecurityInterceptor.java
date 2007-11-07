/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Query;
import org.hibernate.type.Type;

/**
 * Hibernate interceptor that adds CSM protection elements and other security
 * features as objects are saved.
 */
public class SecurityInterceptor extends EmptyInterceptor {
    //
    // DEVELOPER NOTE: this class must be thread safe, because we enable it at the
    //                 SessionFactory level in hibernate.
    //

    private static final Log LOG = LogFactory.getLog(SecurityInterceptor.class);
    private static final long serialVersionUID = -2071964672876972370L;

    /**
     * Key for looking up the authorization manager instance from CSM
     */
    public static final String AUTHORIZATION_MANAGER_KEY = "caarray";

    /** The synthetic user for anonymous access permissions. */
    public static final String ANONYMOUS_USER = "__anonymous__";
    /** The synthetic group for anonymous access permissions. */
    public static final String ANONYMOUS_GROUP = "__anonymous__";
    private static final String READ_ROLE = "Read";
    private static final String WRITE_ROLE = "Write";

    private static final AuthorizationManager AUTH_MGR;
    private static final String APPLICATION_NAME = "caarray";
    // Indicates that some activity of interest occurred
    private static final ThreadLocal<Object> MARKER = new ThreadLocal<Object>();
    // New objects being saved
    private static final ThreadLocal<Collection<Protectable>> NEWOBJS = new ThreadLocal<Collection<Protectable>>();
    // Objects being deleted
    private static final ThreadLocal<Collection<Protectable>> DELETEDOBJS = new ThreadLocal<Collection<Protectable>>();
    // Projects whose browsable status changed
    private static final ThreadLocal<Collection<Project>> BROWSABLE_CHANGES = new ThreadLocal<Collection<Project>>();

    static {
        AuthorizationManager am = null;
        try {
            am = SecurityServiceProvider.getAuthorizationManager(AUTHORIZATION_MANAGER_KEY);
        } catch (CSConfigurationException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        } catch (CSException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        }

        AUTH_MGR = am;
        LOG.debug("Set up new authManager: " + AUTH_MGR);
    }

    /**
     * @return the configured authorization manager
     */
    public static AuthorizationManager getAuthorizationManager() {
        return AUTH_MGR;
    }

    /**
     * Finds protectables and queues them to have protection elements assigned.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof Protectable) {
            if (NEWOBJS.get() == null) {
                NEWOBJS.set(new ArrayList<Protectable>());
                MARKER.set(new Object());
            }
            NEWOBJS.get().add((Protectable) entity);
        }

        return false;
    }

    /**
     * Finds deleted protectables and queues them to have associated protection elements removed.
     *
     * {@inheritDoc}
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof Protectable) {
            if (DELETEDOBJS.get() == null) {
                DELETEDOBJS.set(new ArrayList<Protectable>());
                MARKER.set(new Object());
            }
            DELETEDOBJS.get().add((Protectable) entity);
        }
    }

    /**
     * Finds modified protectables and queues them for sync with CSM.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
        if (entity instanceof Project && previousState != null) {
            // figure out if browsable changed
            for (int i = 0; i < propertyNames.length; ++i) {
                if (propertyNames[i].equals("browsable")
                        && !ObjectUtils.equals(currentState[i], previousState[i])) {
                    Project p = (Project) entity;
                    if (BROWSABLE_CHANGES.get() == null) {
                        BROWSABLE_CHANGES.set(new ArrayList<Project>());
                        MARKER.set(new Object());
                    }
                    BROWSABLE_CHANGES.get().add(p);
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void postFlush(Iterator entities) {
        if (MARKER.get() == null) {
            MARKER.set(null);
            return;
        }

        String user = UsernameHolder.getUser();
        User csmUser = AUTH_MGR.getUser(user);
        handleNew(user, csmUser);
        handleBrowsableChanges();
        handleDeleted(user);
    }

    private void handleBrowsableChanges() {
        if (BROWSABLE_CHANGES.get() == null) {
            return;
        }

        for (Project p : BROWSABLE_CHANGES.get()) {
            LOG.debug("Modifying browsable status for project: " + p.getId());
            try {
                if (p.isBrowsable()) {
                    assignAnonymousAccess(getProtectionGroup(p));
                } else {
                    List<UserGroupRoleProtectionGroup> l = getUserGroupRoleProtectionGroups(p);
                    for (UserGroupRoleProtectionGroup ugrp : l) {
                        if (ugrp.getGroup() != null) {
                        AUTH_MGR.removeGroupRoleFromProtectionGroup(
                                ugrp.getProtectionGroup().getProtectionGroupId().toString(),
                                ugrp.getGroup().getGroupId().toString(),
                                new String[] {ugrp.getRole().getId().toString() });
                    }
                }
                }
            } catch (CSTransactionException e) {
                LOG.warn("Unable to change browsable status: " + e.getMessage(), e);
            }
        }

        BROWSABLE_CHANGES.set(null);
    }

    private void handleDeleted(String user) {
        if (DELETEDOBJS.get() == null) {
            return;
        }
        for (Protectable p : DELETEDOBJS.get()) {
            LOG.debug("Deleting records for obj of type: " + p.getClass().getName() + " for user "
                      + user);
            try {
                List<UserGroupRoleProtectionGroup> l = getUserGroupRoleProtectionGroups(p);
                for (UserGroupRoleProtectionGroup ugrpg : l) {
                    if (ugrpg.getGroup() != null) {
                    AUTH_MGR.removeGroupRoleFromProtectionGroup(
                            ugrpg.getProtectionGroup().getProtectionGroupId().toString(),
                            ugrpg.getGroup().getGroupId().toString(),
                            new String[] {ugrpg.getRole().getId().toString() });
                    } else {
                        AUTH_MGR.removeUserRoleFromProtectionGroup(
                                ugrpg.getProtectionGroup().getProtectionGroupId().toString(),
                                ugrpg.getUser().getUserId().toString(),
                                new String[] {ugrpg.getRole().getId().toString() });
                }
                }
                ProtectionGroup pg = getProtectionGroup(p);
                ProtectionElement pe = (ProtectionElement) pg.getProtectionElements().iterator().next();
                AUTH_MGR.removeProtectionGroup(pg.getProtectionGroupId().toString());
                AUTH_MGR.removeProtectionElement(pe.getProtectionElementId().toString());

            } catch (CSTransactionException e) {
                LOG.warn("Unable to remove CSM elements from deleted object: " + e.getMessage(), e);
            }
        }

        DELETEDOBJS.set(null);
    }

    /**
     * @param user
     * @param csmUser
     */
    private void handleNew(String user, User csmUser) {
        if (NEWOBJS.get() == null) {
            return;
        }

        for (Protectable p : NEWOBJS.get()) {
            LOG.debug("Creating access record for obj of type: " + p.getClass().getName() + " for user "
                    + user);

            try {
                ProtectionGroup pg = createProtectionGroup(p, csmUser);

                if (p instanceof Project) {
                    handleNewProject((Project) p, pg);
                }

            } catch (CSObjectNotFoundException e) {
                LOG.warn("Could not find the " + APPLICATION_NAME + " application: " + e.getMessage(), e);
            } catch (CSTransactionException e) {
                LOG.warn("Could not save new protection element: " + e.getMessage(), e);
            }
        }

        NEWOBJS.set(null);
    }

    private ProtectionGroup createProtectionGroup(Protectable p, User csmUser)
        throws CSObjectNotFoundException, CSTransactionException {

        ProtectionElement pe = new ProtectionElement();
        Application application = AUTH_MGR.getApplication(APPLICATION_NAME);
        pe.setApplication(application);
        pe.setObjectId(p.getClass().getName());
        pe.setAttribute("id");
        pe.setValue(p.getId().toString());
        pe.setUpdateDate(new Date());
        AUTH_MGR.createProtectionElement(pe);
        AUTH_MGR.assignOwners(pe.getProtectionElementId().toString(),
                              new String[] {csmUser.getUserId().toString()});

        ProtectionGroup pg = new ProtectionGroup();
        pg.setApplication(application);
        pg.setProtectionElements(Collections.singleton(pe));
        pg.setProtectionGroupName("PE(" + pe.getProtectionElementId() + ") group");
        pg.setUpdateDate(new Date());
        AUTH_MGR.createProtectionGroup(pg);

        // This shouldn't be necessary, because the filter should take into account
        // the ownership status (set above.)  However, such a filter uses a UNION
        // mechanism and this runs into a hibernate bug:
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2593
        // Thus, we do an extra association here.  Yuck!
        AUTH_MGR.assignUserRoleToProtectionGroup(
                csmUser.getUserId().toString(),
                new String[] {getReadRole().getId().toString() },
                pg.getProtectionGroupId().toString());
        return pg;
    }

    private void handleNewProject(Project p, ProtectionGroup pg) throws CSTransactionException {
        if (p.isBrowsable()) {
            assignAnonymousAccess(pg);
        }
    }

    @SuppressWarnings("unchecked")
    private static void assignAnonymousAccess(ProtectionGroup pg) throws CSTransactionException {
        // We could cache the ids for the group and role
        Group group = new Group();
        group.setGroupName(ANONYMOUS_GROUP);
        GroupSearchCriteria gsc = new GroupSearchCriteria(group);
        List<Group> groupList = AUTH_MGR.getObjects(gsc);
        group = groupList.get(0);

        AUTH_MGR.assignGroupRoleToProtectionGroup(
                pg.getProtectionGroupId().toString(),
                group.getGroupId().toString(),
                new String[] {getReadRole().getId().toString() });
    }

    @SuppressWarnings("unchecked")
    private static Role getReadRole() {
        return getRoleByName(READ_ROLE);
    }

    @SuppressWarnings("unchecked")
    private static Role getWriteRole() {
        return getRoleByName(WRITE_ROLE);
    }

    @SuppressWarnings("unchecked")
    private static Role getRoleByName(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        RoleSearchCriteria rsc = new RoleSearchCriteria(role);
        List<Role> roleList = AUTH_MGR.getObjects(rsc);
        role = roleList.get(0);
        return role;
    }

    /**
     * @param p the protectable to get the protection group for
     * @return the UserGroupRoleProtectionGroup.  <b>This object is NOT associated with the current
     *         hibernate session.</b>
     */
    @SuppressWarnings("unchecked")
    public static List<UserGroupRoleProtectionGroup> getUserGroupRoleProtectionGroups(Protectable p) {
        // Unfortunately, CSM doesn't provide a way to find out if the UserGroupRoleProtectionGroup
        // has been created (down to attribute level).  So we need to query for it,
        // using the known values for various ids from the csm script
        String queryString = "SELECT ugrpg FROM " + UserGroupRoleProtectionGroup.class.getName() + " ugrpg, "
                             + ProtectionElement.class.getName() + " pe "
                             + "WHERE pe in elements(ugrpg.protectionGroup.protectionElements) "
                             + "  AND size(ugrpg.protectionGroup.protectionElements) = 1"
                             + "  AND pe.attribute = 'id' "
                             + "  AND pe.objectId = :objectId "
                             + "  AND pe.value = :value "
                             + "  AND ugrpg.role.name = :roleName";
        Query q = HibernateUtil.getCurrentSession().createQuery(queryString);
        q.setString("roleName", "Read");
        q.setString("objectId", getNonGLIBClass(p).getName());
        q.setString("value", p.getId().toString());
        return q.list();
    }

    private static ProtectionGroup getProtectionGroup(Protectable p) {
        String queryString = "SELECT pg FROM " + ProtectionGroup.class.getName() + " pg, "
                             + ProtectionElement.class.getName() + " pe "
                             + "WHERE pe in elements(pg.protectionElements) "
                             + "  AND pe.objectId = :objectId "
                             + "  AND pe.attribute = 'id' "
                             + "  AND pe.value = :value "
                             + "  AND pg.protectionGroupName LIKE 'PE(%) group'";
        Query q = HibernateUtil.getCurrentSession().createQuery(queryString);
        q.setString("objectId", getNonGLIBClass(p).getName());
        q.setString("value", p.getId().toString());
        return (ProtectionGroup) q.uniqueResult();
    }

    private static Class<?> getNonGLIBClass(Object o) {
        Class<?> result = o.getClass();
        if (result.getName().contains("$$EnhancerByCGLIB$$")) {
            result = result.getSuperclass();
        }
        return result;
    }
}
