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
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.constants.Constants;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.provisioning.AuthorizationManagerImpl;
import gov.nih.nci.security.system.ApplicationSessionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Utility class containing methods for synchronizing our security data model with CSM, as well as a facade for querying
 * CSM.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.AvoidDuplicateLiterals" })
public final class SecurityUtils {
    private static final Logger LOG = Logger.getLogger(SecurityUtils.class);
    private static final long serialVersionUID = -2071964672876972370L;

    private static final String CSM_HIBERNATE_CONFIG_PREFIX = "/csm/*";

    /** The username of the synthetic user for anonymous access permissions. */
    public static final String ANONYMOUS_USERNAME = "__anonymous__";
    /** The name of the group for anonymous access permissions. */
    public static final String ANONYMOUS_GROUP = "__anonymous__";

    /** The privilege for Browsing a Protectable. * */
    public static final String BROWSE_PRIVILEGE = "ACCESS";
    /** The privilege for Reading a Protectable. * */
    public static final String READ_PRIVILEGE = "READ";
    /** The privilege for Writing a Protectable. * */
    public static final String WRITE_PRIVILEGE = "WRITE";
    /** The privilege for modifying the permissions of a Protectable. * */
    public static final String PERMISSIONS_PRIVILEGE = "PERMISSIONS";

    /** The role for Browsing a Protectable. * */
    public static final String BROWSE_ROLE = "Access";
    /** The role for Reading a Protectable. * */
    public static final String READ_ROLE = "Read";
    /** The role for Writing a Protectable. * */
    public static final String WRITE_ROLE = "Write";
    /** The role for modifying the permissions of a Protectable. * */
    public static final String PERMISSIONS_ROLE = "Permissions";

    private static String caarrayAppName;
    private static AuthorizationManager authMgr;
    private static Application caarrayApp;
    private static User anonymousUser;

    private static final ThreadLocal<Boolean> PRIVILEGED_MODE = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    // hidden constructor for static utility class
    private SecurityUtils() {
    }

    static {
        try {
            Resource csmHibernateConfig = getCsmHibernateConfig();
            caarrayAppName = StringUtils.substringBefore(csmHibernateConfig.getFilename(), Constants.FILE_NAME_SUFFIX);
            authMgr = new AuthorizationManagerImpl(caarrayAppName, csmHibernateConfig.getURL());
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize CSM: " + e.getMessage(), e);
        } catch (CSConfigurationException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        }
    }

    /**
     * Method that must be called prior to usage of SecurityUtils, initializing some cached values.
     * Intended to be called at system startup, such as from a web application startup listener
     */
    public static void init() {
        try {
            caarrayApp = authMgr.getApplication(caarrayAppName);
            anonymousUser = authMgr.getUser(ANONYMOUS_USERNAME);
        } catch (CSObjectNotFoundException e) {
            throw new IllegalStateException("Could not retrieve caarray application or anonymous user", e);
        }
    }

    private static Resource getCsmHibernateConfig() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + CSM_HIBERNATE_CONFIG_PREFIX + Constants.FILE_NAME_SUFFIX);
        if (resources.length == 0) {
            throw new IllegalStateException("Could not locate a CSM hibernate configuration");
        }
        return resources[0];
    }

    /**
     * @return the CSM AuthorizationManager
     */
    public static AuthorizationManager getAuthorizationManager() {
        return authMgr;
    }

    /**
     * @return the CSM application instance for CAArray
     * @throws CSObjectNotFoundException
     */
    public static Application getApplication() {
        return caarrayApp;
    }

    /**
     * @return the CSM user instance for the fake anonymous user
     * @throws CSObjectNotFoundException
     */
    public static User getAnonymousUser() {
        return anonymousUser;
    }



    static void handleBiomaterialChanges(Collection<Project> projects, Collection<Protectable> protectables) {
        if (projects == null) {
            return;
        }

        for (Project p : projects) {
            LOG.debug("Modifying biomaterial collections for project: " + p.getId());
            try {
                for (Sample s : p.getExperiment().getSamples()) {
                    if (protectables != null && protectables.contains(s)) {
                        handleNewSample(s, p);
                    }
                }
            } catch (CSTransactionException e) {
                LOG.warn("Unable to update biomaterial collections: " + e.getMessage(), e);
            } catch (CSObjectNotFoundException e) {
                LOG.warn("Unable to update biomaterial collections: " + e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "unchecked" })
    static void handleDeleted(Collection<Protectable> deletedInstances) {
        if (deletedInstances == null) {
            return;
        }

        for (Protectable p : deletedInstances) {
            LOG.debug("Deleting records for obj of type: " + p.getClass().getName() + " for user "
                    + UsernameHolder.getUser());
            try {
                List<UserGroupRoleProtectionGroup> l = getUserGroupRoleProtectionGroups(p);
                for (UserGroupRoleProtectionGroup ugrpg : l) {
                    if (ugrpg.getGroup() != null) {
                        authMgr.removeGroupRoleFromProtectionGroup(ugrpg.getProtectionGroup().getProtectionGroupId()
                                .toString(), ugrpg.getGroup().getGroupId().toString(), new String[]{ugrpg.getRole()
                                .getId().toString() });
                    } else {
                        authMgr.removeUserRoleFromProtectionGroup(ugrpg.getProtectionGroup().getProtectionGroupId()
                                .toString(), ugrpg.getUser().getUserId().toString(), new String[]{ugrpg.getRole()
                                .getId().toString() });
                    }
                }
                ProtectionGroup pg = getProtectionGroup(p);
                LOG.debug("HAndling delete for protection group " + pg.getProtectionGroupName());
                Set<ProtectionElement> protElements = pg.getProtectionElements();
                ProtectionElement pe = protElements.iterator().next();
                authMgr.removeProtectionGroup(pg.getProtectionGroupId().toString());
                authMgr.removeProtectionElement(pe.getProtectionElementId().toString());

            } catch (CSTransactionException e) {
                LOG.warn("Unable to remove CSM elements from deleted object: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @param user
     * @param csmUser
     */
    static void handleNewProtectables(Collection<Protectable> protectables) {
        if (protectables == null) {
            return;
        }

        User csmUser = UsernameHolder.getCsmUser();
        for (Protectable p : protectables) {
            LOG.debug("Creating access record for obj of type: " + p.getClass().getName() + " for user "
                    + csmUser.getLoginName());

            try {
                ProtectionGroup pg = createProtectionGroup(p, csmUser);

                if (p instanceof Project) {
                    handleNewProject((Project) p, pg);
                }
            } catch (CSObjectNotFoundException e) {
                LOG.warn("Could not find the " + caarrayAppName + " application: " + e.getMessage(), e);
            } catch (CSTransactionException e) {
                LOG.warn("Could not save new protection element: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @param profilesHolder
     */
    static void handleAccessProfiles(Collection<AccessProfile> profiles) {
        if (profiles == null) {
            return;
        }

        for (AccessProfile ap : profiles) {
            handleAccessProfile(ap);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static void handleAccessProfile(AccessProfile ap) {
        LOG.debug("Handling access profile");
        // to populate inverse properties
        Group targetGroup = getTargetGroup(ap);
        if (targetGroup == null) {
            return;
        }

        handleProjectSecurity(targetGroup, ap.getProject(), ap.getSecurityLevel());

        Map<Long, ProtectionGroup> samplesToProjectionGroups = new HashMap<Long, ProtectionGroup>();

        for (Sample sample : ap.getProject().getExperiment().getSamples()) {
            ProtectionGroup sampleProtectionGroup = getProtectionGroup(sample);
            samplesToProjectionGroups.put(sample.getId(), sampleProtectionGroup);
        }

        if (!samplesToProjectionGroups.isEmpty()) {
            try {
                LOG.debug("clearing existing sample-level security");
                    AuthorizationManagerExtensions.clearProtectionGroupRoles(targetGroup, samplesToProjectionGroups
                            .values(), getApplication());
                LOG.debug("done clearing existing sample-level security");
            } catch (CSException e) {
                LOG.error("Exception clearing roles from protection groups", e);
            }
        }

        LOG.debug("setting new sample-level security");
        Role readRole = getRoleByName(READ_ROLE);
        Role writeRole = getRoleByName(WRITE_ROLE);
        for (Sample sample : ap.getProject().getExperiment().getSamples()) {
            SampleSecurityLevel sampleSecLevel = getSampleSecurityLevel(ap, sample);

            if (sampleSecLevel.isAllowsRead() || sampleSecLevel.isAllowsWrite()) {
                ProtectionGroup pg = samplesToProjectionGroups.get(sample.getId());
                handleSampleSecurity(targetGroup, pg, sampleSecLevel, readRole, writeRole);
            }
        }
        LOG.debug("Done handling access profile");
    }

    private static Group getTargetGroup(AccessProfile ap) {
        if (ap.isHostProfile()) {
            // not supporting host profiles for now
            return null;
        } else if (ap.isPublicProfile()) {
            return getAnonymousGroup();
        } else if (ap.isGroupProfile()) {
            return ap.getGroup().getGroup();
        } else {
            throw new IllegalStateException("Unsupported access profile type: " + ap);
        }
    }

    private static SampleSecurityLevel getSampleSecurityLevel(AccessProfile ap, Sample s) {
        SampleSecurityLevel sampleSecLevel = SampleSecurityLevel.NONE;
        if (ap.getSecurityLevel().isSampleLevelPermissionsAllowed()) {
            if (ap.getSampleSecurityLevels().containsKey(s)) {
                sampleSecLevel = ap.getSampleSecurityLevels().get(s);
            }
        } else {
            switch (ap.getSecurityLevel()) {
            case READ:
                sampleSecLevel = SampleSecurityLevel.READ;
                break;
            case WRITE:
                sampleSecLevel = SampleSecurityLevel.READ_WRITE;
                break;
            case VISIBLE:
            case NONE:
            case NO_VISIBILITY:
                sampleSecLevel = SampleSecurityLevel.NONE;
                break;
            default:
                throw new IllegalStateException("Encountered unknown project security level: "
                        + ap.getSecurityLevel());
            }
        }
        return sampleSecLevel;
    }

    private static void handleProjectSecurity(Group targetGroup, Project project, SecurityLevel securityLevel) {
        ProtectionGroup pg = getProtectionGroup(project);
        List<String> roleIds = new ArrayList<String>();
        if (securityLevel != SecurityLevel.NONE && securityLevel != SecurityLevel.NO_VISIBILITY) {
            roleIds.add(getRoleByName(BROWSE_ROLE).getId().toString());
        }
        if (securityLevel.isAllowsRead()) {
            roleIds.add(getRoleByName(READ_ROLE).getId().toString());
        }
        if (securityLevel.isAllowsWrite()) {
            roleIds.add(getRoleByName(WRITE_ROLE).getId().toString());
        }

        try {
            authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), targetGroup.getGroupId()
                    .toString(), roleIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        } catch (CSTransactionException e) {
            LOG.warn("Could not assign project group roles corresponding to profile " + e.getMessage(), e);
        }
    }

    private static void handleSampleSecurity(Group targetGroup, ProtectionGroup samplePg,
            SampleSecurityLevel securityLevel, Role readRole, Role writeRole) {
        List<Role> roles = new ArrayList<Role>();
        if (securityLevel.isAllowsRead()) {
            roles.add(readRole);
        }
        if (securityLevel.isAllowsWrite()) {
            roles.add(writeRole);
        }
        try {
            AuthorizationManagerExtensions.assignGroupRoleToProtectionGroup(samplePg, targetGroup, roles,
                    getApplication());
        } catch (CSTransactionException e) {
            LOG.warn("Could not assign sample group roles corresponding to profile " + e.getMessage(), e);
        }
    }

    /**
     * @return the CSM group instance for the anonymous group
     * @throws CSObjectNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static Group getAnonymousGroup() {
        Group group = new Group();
        group.setGroupName(ANONYMOUS_GROUP);
        GroupSearchCriteria gsc = new GroupSearchCriteria(group);
        List<Group> groupList = authMgr.getObjects(gsc);
        group = groupList.get(0);
        return group;
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static ProtectionGroup createProtectionGroup(Protectable p, User csmUser)
            throws CSObjectNotFoundException, CSTransactionException {

        ProtectionElement pe = new ProtectionElement();
        Application application = getApplication();
        pe.setApplication(application);
        pe.setObjectId(p.getClass().getName());
        pe.setAttribute("id");
        pe.setValue(p.getId().toString());
        pe.setUpdateDate(new Date());
        authMgr.createProtectionElement(pe);

        ProtectionGroup pg = new ProtectionGroup();
        pg.setApplication(application);
        pg.setProtectionElements(Collections.singleton(pe));
        pg.setProtectionGroupName("PE(" + pe.getProtectionElementId() + ") group");
        pg.setUpdateDate(new Date());
        authMgr.createProtectionGroup(pg);

        addOwner(pg, csmUser);
        return pg;
    }

    private static void addOwner(ProtectionGroup pg, User user) throws CSObjectNotFoundException,
            CSTransactionException {
        ProtectionElement pe = (ProtectionElement) pg.getProtectionElements().iterator().next();
        AuthorizationManagerExtensions.addOwner(pe.getProtectionElementId(), user, caarrayAppName);

        // This shouldn't be necessary, because the filter should take into account
        // the ownership status (set above.) However, such a filter uses a UNION
        // mechanism and this runs into a hibernate bug:
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2593
        // Thus, we do an extra association here. Yuck!
        Group g = getSingletonGroup(user);
        String[] ownerRoles =
                {getRoleByName(BROWSE_ROLE).getId().toString(), getRoleByName(READ_ROLE).getId().toString(),
                        getRoleByName(WRITE_ROLE).getId().toString(),
                        getRoleByName(PERMISSIONS_ROLE).getId().toString() };
        authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), g.getGroupId().toString(),
                ownerRoles);
    }

    @SuppressWarnings("unchecked")
    private static Group getSingletonGroup(User user) throws CSTransactionException {
        Group g = new Group();
        g.setGroupName("__selfgroup__" + user.getLoginName() + " (" + user.getUserId() + ")");
        GroupSearchCriteria gsc = new GroupSearchCriteria(g);
        List<Group> groupList = authMgr.getObjects(gsc);
        if (groupList == null || groupList.isEmpty()) {
            g.setApplication(getApplication());
            g.setGroupDesc("Singleton group for CSM filter performance.  Do not edit.");
            authMgr.createGroup(g);
            authMgr.assignUserToGroup(user.getLoginName(), g.getGroupName());
            return g;
        }
        return groupList.get(0);
    }

    private static void handleNewProject(Project p, ProtectionGroup pg) throws CSTransactionException {
        if (p.getPublicProfile().getSecurityLevel() != SecurityLevel.NO_VISIBILITY) {
            assignAnonymousAccess(pg);
        }
    }

    private static void handleNewSample(Sample s, Project p) throws CSTransactionException, CSObjectNotFoundException {
        ProtectionGroup pg = getProtectionGroup(s);
        User csmUser = UsernameHolder.getCsmUser();

        for (User u : p.getOwners()) {
            if (!u.equals(csmUser)) {
                addOwner(pg, u);
            }
        }

        Role readRole = getRoleByName(READ_ROLE);
        Role writeRole = getRoleByName(WRITE_ROLE);
        for (AccessProfile ap : p.getAllAccessProfiles()) {
            Group targetGroup = getTargetGroup(ap);
            if (targetGroup == null) {
                continue;
            }
            SampleSecurityLevel sampleSecLevel = getSampleSecurityLevel(ap, s);
            handleSampleSecurity(targetGroup, pg, sampleSecLevel, readRole, writeRole);
        }
    }

    private static void assignAnonymousAccess(ProtectionGroup pg) throws CSTransactionException {
        // We could cache the ids for the group and role
        Group group = getAnonymousGroup();
        authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(),
                group.getGroupId().toString(), new String[]{getRoleByName(BROWSE_ROLE).getId().toString() });
    }

    @SuppressWarnings("unchecked")
    private static Role getRoleByName(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        RoleSearchCriteria rsc = new RoleSearchCriteria(role);
        List<Role> roleList = authMgr.getObjects(rsc);
        role = roleList.get(0);
        return role;
    }

    /**
     * @param p the protectable to get the protection group for
     * @return the UserGroupRoleProtectionGroup. <b>Note: This object is NOT associated with the current hibernate
     *         session.</b>
     */
    @SuppressWarnings("unchecked")
    public static List<UserGroupRoleProtectionGroup> getUserGroupRoleProtectionGroups(Protectable p) {
        // Unfortunately, CSM doesn't provide a way to find out if the UserGroupRoleProtectionGroup
        // has been created (down to attribute level). So we need to query for it,
        // using the known values for various ids from the csm script
        String queryString =
                "SELECT ugrpg FROM " + UserGroupRoleProtectionGroup.class.getName() + " ugrpg, "
                        + ProtectionElement.class.getName() + " pe "
                        + "WHERE pe in elements(ugrpg.protectionGroup.protectionElements) "
                        + "  AND size(ugrpg.protectionGroup.protectionElements) = 1" + "  AND pe.attribute = 'id' "
                        + "  AND pe.objectId = :objectId " + "  AND pe.value = :value "
                        + "  AND ugrpg.role.name in (:roleNames)";
        Query q = HibernateUtil.getCurrentSession().createQuery(queryString);
        q.setParameterList("roleNames", new String[]{BROWSE_ROLE, READ_ROLE, WRITE_ROLE, PERMISSIONS_ROLE });
        q.setString("objectId", getNonGLIBClass(p).getName());
        q.setString("value", p.getId().toString());
        return q.list();
    }

    private static ProtectionGroup getProtectionGroup(Protectable p) {
        String queryString = "SELECT pg FROM " + ProtectionGroup.class.getName() + " pg join pg.protectionElements pe"
                + " WHERE pg.protectionGroupName LIKE 'PE(%) group' AND pe.objectId = :objectId "
                + "  AND pe.attribute = 'id' AND pe.value = :value";
        try {
            Query q =
                    HibernateSessionFactoryHelper.getAuditSession(
                            ApplicationSessionFactory.getSessionFactory(caarrayAppName)).createQuery(queryString);
            q.setString("objectId", getNonGLIBClass(p).getName());
            q.setString("value", p.getId().toString());
            return (ProtectionGroup) q.uniqueResult();
        } catch (CSConfigurationException e) {
            throw new IllegalStateException("couldn't execute hibernate query: " + e);
        }
    }

    /**
     * Returns the owner for the given protectable.
     *
     * @param p the protectable to get the owner for
     * @return the User who owns the given Protectable instance
     */
    @SuppressWarnings("unchecked")
    public static Set<User> getOwners(Protectable p) {
        ProtectionGroup pg = getProtectionGroup(p);
        ProtectionElement pe = (ProtectionElement) pg.getProtectionElements().iterator().next();
        return new HashSet<User>(pe.getOwners());
    }

    /**
     * Checks whether a given user is an owner of the given protectable.
     *
     * @param p protectable to check
     * @param user user to check
     * @return whether the given user is an owner of the protectable
     */
    public static boolean isOwner(Protectable p, User user) {
        return getOwners(p).contains(user);
    }

    /**
     * Returns whether the given user has READ privilege for the given Protectable.
     *
     * @param p the protectable to check
     * @param user the user to check
     * @return whether the given user has READ privilege for the given Protectable
     */
    public static boolean canRead(Protectable p, User user) {
        return hasPrivilege(p, user, READ_PRIVILEGE);
    }

    /**
     * Returns whether the given user has WRITE privilege for a given entity. this is determined
     * by either checking whether the user has the privilege for that entity directly (if it's a
     * Protectable) or by checking whether the user has the privilege for any of the related
     * Protectables (if it's a ProtectableDescendent). If it is neither of those, then return true.
     *
     * @param o the entity to check
     * @param user the user to check
     * @return whether the given user has WRITE privilege for the given entity
     */
    public static boolean canWrite(PersistentObject o, User user) {
        if (o instanceof Protectable) {
            return hasPrivilege((Protectable) o, user, WRITE_PRIVILEGE);
        }
        if (o instanceof ProtectableDescendent) {
            Collection<? extends Protectable> protectables = ((ProtectableDescendent) o).relatedProtectables();
          if (protectables == null) {
                return true;
            }
            for (Protectable p : protectables) {
                if (hasPrivilege(p, user, WRITE_PRIVILEGE)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns whether the given user has PERMISSIONS privilege for the given Protectable.
     *
     * @param p the protectable to check
     * @param user the user to check
     * @return whether the given user has PERMISSIONS privilege for the given Protectable
     */
    public static boolean canModifyPermissions(Protectable p, User user) {
        return hasPrivilege(p, user, PERMISSIONS_PRIVILEGE);
    }

    private static boolean hasPrivilege(Protectable p, User user, String privilege) {
        // if the protectable is not yet saved, assume user only has access if he is the current user
        if (p.getId() == null) {
            return UsernameHolder.getCsmUser().equals(user);
        }
        try {
            Application app = getApplication();
            return AuthorizationManagerExtensions.checkPermission(user.getLoginName(), getNonGLIBClass(p).getName(),
                    "id", p.getId().toString(), privilege, app);
        } catch (CSException e) {
            LOG.warn(String.format(
                    "Could not check if User %s had privilege %s for protectable of class %s with id %s", user
                            .getLoginName(), privilege, p.getClass().getName(), p.getId()));
            return false;
        }
    }

    static Class<?> getNonGLIBClass(Object o) {
        Class<?> result = o.getClass();
        if (result.getName().contains("$$EnhancerByCGLIB$$")) {
            result = result.getSuperclass();
        }
        return result;
    }

    /**
     * @return the privilegedMode
     */
    public static boolean isPrivilegedMode() {
        return PRIVILEGED_MODE.get();
    }

    /**
     * @param privilegedMode the privilegedMode to set
     */
    public static void setPrivilegedMode(boolean privilegedMode) {
        PRIVILEGED_MODE.set(privilegedMode);
    }
}
