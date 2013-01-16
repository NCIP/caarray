//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.Privileges;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.InstanceLevelMappingElement;
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
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.inject.Inject;

/**
 * Utility class containing methods for synchronizing our security data model with CSM, as well as a facade for querying
 * CSM.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.AvoidDuplicateLiterals",
        "PMD.TooManyMethods" })
public final class SecurityUtils {
    @Inject
    private static CaArrayHibernateHelper hibernateHelper;
    private static final Logger LOG = Logger.getLogger(SecurityUtils.class);
    private static final long serialVersionUID = -2071964672876972370L;

    private static final String CSM_HIBERNATE_CONFIG_PREFIX = "/csm/*";

    /** The username of the synthetic user for anonymous access permissions. */
    public static final String ANONYMOUS_USERNAME = "__anonymous__";
    /** The name of the group for anonymous access permissions. */
    public static final String ANONYMOUS_GROUP = "__anonymous__";
    /** The name of the group for anonymous access permissions. */
    public static final String SELF_GROUP_PREFIX = "__selfgroup__";
    /** The name of the group for system administrator access permissions. */
    private static final String SYSTEM_ADMINISTRATOR_GROUP = "SystemAdministrator";

    /** The privilege for Browsing a Protectable. * */
    public static final String BROWSE_PRIVILEGE = "ACCESS";
    /** The privilege for Reading a Protectable. * */
    public static final String READ_PRIVILEGE = "READ";
    /** The privilege for Writing a Protectable. * */
    public static final String WRITE_PRIVILEGE = "WRITE";
    /** The privilege for modifying the permissions of a Protectable. * */
    public static final String PERMISSIONS_PRIVILEGE = "PERMISSIONS";
    /** The privilege for Partial Reading of a Protectable. * */
    public static final String PARTIAL_READ_PRIVILEGE = "PARTIAL_READ";
    /** The privilege for Partial Writing of a Protectable. * */
    public static final String PARTIAL_WRITE_PRIVILEGE = "PARTIAL_WRITE";

    /** The role for Browsing a Protectable. * */
    public static final String BROWSE_ROLE = "Access";
    /** The role for Reading a Protectable. * */
    public static final String READ_ROLE = "Read";
    /** The role for Writing a Protectable. * */
    public static final String WRITE_ROLE = "Write";
    /** The role for modifying the permissions of a Protectable. * */
    public static final String PERMISSIONS_ROLE = "Permissions";
    /** The role for Partial Reading of a Protectable. * */
    public static final String PARTIAL_READ_ROLE = "Partial_Read";
    /** The role for Partial Writing of a Protectable. * */
    public static final String PARTIAL_WRITE_ROLE = "Partial_Write";

    private static final String[] OWNER_ROLES;

    private static String caarrayAppName;
    private static AuthorizationManager authMgr;
    private static Application caarrayApp;
    private static User anonymousUser;
    private static InstanceLevelMappingElement projectMapping;
    private static InstanceLevelMappingElement sampleMapping;

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
            final URL appConfigUrl = SecurityUtils.class.getClassLoader().getResource("app-config.properties");
            if (appConfigUrl == null) {
                throw new IllegalStateException("resource app-config.properties not found in classpath");
            }
            final InputStream in = appConfigUrl.openStream();
            final Properties appConfig = new Properties();
            appConfig.load(in);
            in.close();
            caarrayAppName = appConfig.getProperty("csm.application.name");
            final String csmConfigName = "csm/" + caarrayAppName + Constants.FILE_NAME_SUFFIX;
            final URL csmConfigUrl = SecurityUtils.class.getClassLoader().getResource(csmConfigName);
            if (csmConfigUrl == null) {
                throw new IllegalStateException("resource " + csmConfigName + " not found in classpath");
            }
            authMgr = new AuthorizationManagerImpl(caarrayAppName, csmConfigUrl);
        } catch (final IOException e) {
            throw new IllegalStateException("Could not initialize CSM: " + e.getMessage(), e);
        } catch (final CSConfigurationException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        }
        OWNER_ROLES =
                new String[] {getRoleByName(BROWSE_ROLE).getId().toString(),
                        getRoleByName(READ_ROLE).getId().toString(), getRoleByName(WRITE_ROLE).getId().toString(),
                        getRoleByName(PERMISSIONS_ROLE).getId().toString() };
    }

    /**
     * Method that must be called prior to usage of SecurityUtils, initializing some cached values. Intended to be
     * called at system startup, such as from a web application startup listener
     */
    public static void init() {
        try {
            caarrayApp = authMgr.getApplication(caarrayAppName);
            anonymousUser = authMgr.getUser(ANONYMOUS_USERNAME);
            projectMapping = authMgr.getInstanceLevelMappingElementById("1");
            sampleMapping = authMgr.getInstanceLevelMappingElementById("2");
        } catch (final CSObjectNotFoundException e) {
            throw new IllegalStateException("Could not retrieve caarray application or anonymous user", e);
        }
    }

    // private static Resource getCsmHibernateConfig() throws IOException {
    // ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    // Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
    // + CSM_HIBERNATE_CONFIG_PREFIX + Constants.FILE_NAME_SUFFIX);
    // if (resources.length == 0) {
    // throw new IllegalStateException("Could not locate a CSM hibernate configuration");
    // }
    // return resources[0];
    // }

    /**
     * @return the CSM AuthorizationManager
     */
    public static AuthorizationManager getAuthorizationManager() {
        return authMgr;
    }

    /**
     * @return the CSM application instance for CAArray
     */
    public static Application getApplication() {
        return caarrayApp;
    }

    /**
     * @return the CSM user instance for the fake anonymous user
     */
    public static User getAnonymousUser() {
        return anonymousUser;
    }

    static InstanceLevelMappingElement findMappingElement(String className, String attributeName) {
        final String packageName = StringUtils.substringBeforeLast(className, ".");
        final String objectName = StringUtils.substringAfterLast(className, ".");

        for (final InstanceLevelMappingElement elt : Arrays.asList(projectMapping, sampleMapping)) {
            if (packageName.equals(elt.getObjectPackageName()) && objectName.equals(elt.getObjectName())
                    && attributeName.equals(elt.getAttributeName())) {
                return elt;
            }
        }
        return null;
    }

    static void handleBiomaterialChanges(Collection<Project> projects, Collection<Protectable> protectables) {
        if (projects == null) {
            return;
        }

        try {
            for (final Project p : projects) {
                LOG.debug("Modifying biomaterial collections for project: " + p.getId());
                for (final Sample s : p.getExperiment().getSamples()) {
                    if (protectables != null && protectables.contains(s)) {
                        handleNewSample(s, p);
                    }
                }
            }
        } catch (final CSTransactionException e) {
            LOG.warn("Unable to update biomaterial collections: " + e.getMessage(), e);
        } catch (final CSObjectNotFoundException e) {
            LOG.warn("Unable to update biomaterial collections: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "unchecked" })
    static void handleDeleted(Collection<Protectable> deletedInstances) {
        if (deletedInstances == null) {
            return;
        }

        try {
            for (final Protectable p : deletedInstances) {
                LOG.debug("Deleting records for obj of type: " + p.getClass().getName() + " for user "
                        + CaArrayUsernameHolder.getUser());
                final List<UserGroupRoleProtectionGroup> l = getUserGroupRoleProtectionGroups(p);
                for (final UserGroupRoleProtectionGroup ugrpg : l) {
                    if (ugrpg.getGroup() != null) {
                        authMgr.removeGroupRoleFromProtectionGroup(ugrpg.getProtectionGroup().getProtectionGroupId()
                                .toString(), ugrpg.getGroup().getGroupId().toString(), new String[] {ugrpg.getRole()
                                .getId().toString() });
                    } else {
                        authMgr.removeUserRoleFromProtectionGroup(ugrpg.getProtectionGroup().getProtectionGroupId()
                                .toString(), ugrpg.getUser().getUserId().toString(), new String[] {ugrpg.getRole()
                                .getId().toString() });
                    }
                }
                final ProtectionGroup pg = getProtectionGroup(p);
                LOG.debug("HAndling delete for protection group " + pg.getProtectionGroupName());
                final Set<ProtectionElement> protElements = pg.getProtectionElements();
                final ProtectionElement pe = protElements.iterator().next();
                authMgr.removeProtectionGroup(pg.getProtectionGroupId().toString());
                authMgr.removeProtectionElement(pe.getProtectionElementId().toString());
            }
        } catch (final CSTransactionException e) {
            LOG.warn("Unable to remove CSM elements from deleted object: " + e.getMessage(), e);
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

        final User csmUser = CaArrayUsernameHolder.getCsmUser();

        try {
            for (final Protectable p : protectables) {
                LOG.debug("Creating access record for obj of type: " + p.getClass().getName() + " for user "
                        + csmUser.getLoginName());
                final ProtectionGroup pg = createProtectionGroup(p, csmUser);

                if (p instanceof Project) {
                    handleNewProject((Project) p, pg);
                }
            }
        } catch (final CSObjectNotFoundException e) {
            LOG.warn("Could not find the " + caarrayAppName + " application: " + e.getMessage(), e);
        } catch (final CSTransactionException e) {
            LOG.warn("Could not save new protection element: " + e.getMessage(), e);
        }
    }

    /**
     * @param profilesHolder
     */
    static void handleAccessProfiles(Collection<AccessProfile> profiles) {
        if (profiles == null) {
            return;
        }

        for (final AccessProfile ap : profiles) {
            handleAccessProfile(ap);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static void handleAccessProfile(AccessProfile ap) {
        LOG.debug("Handling access profile");

        try {
            // to populate inverse properties
            final Group targetGroup = getTargetGroup(ap);
            if (targetGroup == null) {
                return;
            }

            handleProjectSecurity(targetGroup, ap.getProject(), ap.getSecurityLevel());

            final Map<Long, ProtectionGroup> samplesToProjectionGroups = new HashMap<Long, ProtectionGroup>();

            for (final Sample sample : ap.getProject().getExperiment().getSamples()) {
                final ProtectionGroup sampleProtectionGroup = getProtectionGroup(sample);
                samplesToProjectionGroups.put(sample.getId(), sampleProtectionGroup);
            }

            if (!samplesToProjectionGroups.isEmpty()) {
                LOG.debug("clearing existing sample-level security");
                AuthorizationManagerExtensions.clearProtectionGroupRoles(targetGroup,
                        samplesToProjectionGroups.values(), getApplication());
                LOG.debug("done clearing existing sample-level security");
            }

            LOG.debug("setting new sample-level security");
            final Role readRole = getRoleByName(READ_ROLE);
            final Role writeRole = getRoleByName(WRITE_ROLE);
            for (final Sample sample : ap.getProject().getExperiment().getSamples()) {
                final SampleSecurityLevel sampleSecLevel = getSampleSecurityLevel(ap, sample);

                if (sampleSecLevel.isAllowsRead() || sampleSecLevel.isAllowsWrite()) {
                    final ProtectionGroup pg = samplesToProjectionGroups.get(sample.getId());
                    handleSampleSecurity(targetGroup, pg, sampleSecLevel, readRole, writeRole);
                }
            }
        } catch (final CSException e) {
            LOG.error("Could not update permissions for profile " + e.getMessage(), e);
        }

        LOG.debug("Done handling access profile");
    }

    private static Group getTargetGroup(AccessProfile ap) throws CSTransactionException {
        if (ap.isPublicProfile()) {
            return findGroupByName(ANONYMOUS_GROUP);
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
                throw new IllegalStateException("Encountered unknown project security level: " + ap.getSecurityLevel());
            }
        }
        return sampleSecLevel;
    }

    private static void handleProjectSecurity(Group targetGroup, Project project, SecurityLevel securityLevel) {
        final ProtectionGroup pg = getProtectionGroup(project);
        final List<String> roleIds = new ArrayList<String>();
        if (securityLevel != SecurityLevel.NONE && securityLevel != SecurityLevel.NO_VISIBILITY) {
            roleIds.add(getRoleByName(BROWSE_ROLE).getId().toString());
        }
        if (securityLevel.isAllowsRead()) {
            roleIds.add(getRoleByName(READ_ROLE).getId().toString());
        }
        if (securityLevel.isAllowsWrite()) {
            roleIds.add(getRoleByName(WRITE_ROLE).getId().toString());
        }
        if (securityLevel.isPartialRead()) {
            roleIds.add(getRoleByName(PARTIAL_READ_ROLE).getId().toString());
        }
        if (securityLevel.isPartialWrite()) {
            roleIds.add(getRoleByName(PARTIAL_WRITE_ROLE).getId().toString());
        }

        try {
            authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), targetGroup.getGroupId()
                    .toString(), roleIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        } catch (final CSTransactionException e) {
            LOG.warn("Could not assign project group roles corresponding to profile " + e.getMessage(), e);
        }
    }

    private static void handleSampleSecurity(Group targetGroup, ProtectionGroup samplePg,
            SampleSecurityLevel securityLevel, Role readRole, Role writeRole) {
        final List<Role> roles = new ArrayList<Role>();
        if (securityLevel.isAllowsRead()) {
            roles.add(readRole);
        }
        if (securityLevel.isAllowsWrite()) {
            roles.add(writeRole);
        }
        try {
            AuthorizationManagerExtensions.assignGroupRoleToProtectionGroup(samplePg, targetGroup, roles,
                    getApplication());
        } catch (final CSTransactionException e) {
            LOG.warn("Could not assign sample group roles corresponding to profile " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static ProtectionGroup createProtectionGroup(Protectable p, User csmUser) throws CSObjectNotFoundException,
            CSTransactionException {

        final ProtectionElement pe = new ProtectionElement();
        final Application application = getApplication();
        pe.setApplication(application);
        pe.setObjectId(p.getClass().getName());
        pe.setAttribute("id");
        pe.setValue(p.getId().toString());
        pe.setUpdateDate(new Date());
        authMgr.createProtectionElement(pe);

        final ProtectionGroup pg = new ProtectionGroup();
        pg.setApplication(application);
        pg.setProtectionElements(Collections.singleton(pe));
        pg.setProtectionGroupName("PE(" + pe.getProtectionElementId() + ") group");
        pg.setUpdateDate(new Date());
        authMgr.createProtectionGroup(pg);

        addOwner(pg, csmUser);
        assignSystemAdministratorAccess(pg);
        return pg;
    }

    private static void addOwner(ProtectionGroup pg, User user) throws CSObjectNotFoundException,
            CSTransactionException {
        final ProtectionElement pe = (ProtectionElement) pg.getProtectionElements().iterator().next();
        AuthorizationManagerExtensions.addOwner(pe.getProtectionElementId(), user, caarrayAppName);

        // This shouldn't be necessary, because the filter should take into account
        // the ownership status (set above.) However, such a filter uses a UNION
        // mechanism and this runs into a hibernate bug:
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2593
        // Thus, we do an extra association here. Yuck!
        final Group g = getSingletonGroup(user);
        authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), g.getGroupId().toString(),
                OWNER_ROLES);
    }

    /**
     * Get or create the singleton group for the given user.
     * 
     * @param user user to get the singleton group for
     * @return the group
     * @throws CSTransactionException on CSM error
     */
    @SuppressWarnings("unchecked")
    private static Group getSingletonGroup(User user) throws CSTransactionException {
        final Group g = new Group();
        g.setGroupName(SELF_GROUP_PREFIX + user.getLoginName() + " (" + user.getUserId() + ")");
        final GroupSearchCriteria gsc = new GroupSearchCriteria(g);
        final List<Group> groupList = authMgr.getObjects(gsc);
        if (groupList == null || groupList.isEmpty()) {
            g.setApplication(getApplication());
            g.setGroupDesc("Singleton group for CSM filter performance.  Do not edit.");
            authMgr.createGroup(g);
            authMgr.assignUserToGroup(user.getLoginName(), g.getGroupName());
            return g;
        }
        return groupList.get(0);
    }

    /**
     * Change the owner of a Protectable. If there is more than one owner, only the first owner will be changed to
     * <code>newOwner</code>, and the others will be left untouched.
     * 
     * @param p the Protectable to change the owner of
     * @param newOwner the new owner of the Protectable
     * @throws CSException on a CSM error
     */
    public static void changeOwner(Protectable p, User newOwner) throws CSException {
        final User oldOwner = getOwner(p);

        final List<Protectable> protectables = new ArrayList<Protectable>();
        protectables.add(p);
        changeOwner(protectables, oldOwner, newOwner);

        AuthorizationManagerExtensions.refreshInstanceTables(SecurityUtils.getApplication());
    }

    /**
     * Change the owner of a Project and replaces the old project owner with the new owner in the set of owners for each
     * sample (other sample owners are unaffected).
     * 
     * @param p the Project to change the owner of
     * @param newOwner the new owner of the Project
     * @throws CSException on a CSM error
     */
    public static void changeOwner(Project p, User newOwner) throws CSException {
        final User oldOwner = getOwner(p);
        final List<Protectable> project = new ArrayList<Protectable>();
        project.add(p);
        final List<Protectable> samples = new ArrayList<Protectable>(p.getExperiment().getSamples());

        changeOwner(project, oldOwner, newOwner);
        changeOwner(samples, oldOwner, newOwner);

        AuthorizationManagerExtensions.refreshInstanceTables(SecurityUtils.getApplication());
    }

    private static void changeOwner(List<? extends Protectable> protectables, User oldOwner, User newOwner)
            throws CSTransactionException {
        if (!protectables.isEmpty()) {
            final List<String> protectableIds = new ArrayList<String>();
            for (final Protectable p : protectables) {
                protectableIds.add(p.getId().toString());
            }

            final String protectableClassName = getUnderlyingEntityClass(protectables.get(0)).getName();

            final List<Long> protectionElementIds = getProtectionElementIds(protectableIds, protectableClassName);
            final List<Long> protectionGroupIds = getProtectionGroupIds(protectableIds, protectableClassName);
            final Group newOwnerGroup = getSingletonGroup(newOwner);
            final Group oldOwnerGroup = getSingletonGroup(oldOwner);
            AuthorizationManagerExtensions.updateGroupForRoleProtectionGroup(protectionGroupIds, oldOwnerGroup,
                    newOwnerGroup, caarrayAppName);
            AuthorizationManagerExtensions.replaceOwner(protectionElementIds, oldOwner, newOwner, caarrayAppName);
        }
    }

    private static void handleNewProject(Project p, ProtectionGroup pg) throws CSTransactionException {
        if (p.getPublicProfile().getSecurityLevel() != SecurityLevel.NO_VISIBILITY) {
            assignAnonymousAccess(pg);
        }
    }

    private static void handleNewSample(Sample s, Project p) throws CSTransactionException, CSObjectNotFoundException {
        final ProtectionGroup pg = getProtectionGroup(s);
        final User csmUser = CaArrayUsernameHolder.getCsmUser();

        for (final User u : p.getOwners()) {
            if (!u.equals(csmUser)) {
                addOwner(pg, u);
            }
        }

        final Role readRole = getRoleByName(READ_ROLE);
        final Role writeRole = getRoleByName(WRITE_ROLE);
        for (final AccessProfile ap : p.getAllAccessProfiles()) {
            final Group targetGroup = getTargetGroup(ap);
            if (targetGroup == null) {
                continue;
            }
            final SampleSecurityLevel sampleSecLevel = getSampleSecurityLevel(ap, s);
            handleSampleSecurity(targetGroup, pg, sampleSecLevel, readRole, writeRole);
        }
    }

    private static void assignAnonymousAccess(ProtectionGroup pg) throws CSTransactionException {
        // We could cache the ids for the group and role
        final Group group = findGroupByName(ANONYMOUS_GROUP);
        authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), group.getGroupId().toString(),
                new String[] {getRoleByName(BROWSE_ROLE).getId().toString() });
    }

    private static void assignSystemAdministratorAccess(ProtectionGroup pg) throws CSTransactionException {
        // We could cache the ids for the group and role
        final Group group = findGroupByName(SYSTEM_ADMINISTRATOR_GROUP);
        authMgr.assignGroupRoleToProtectionGroup(pg.getProtectionGroupId().toString(), group.getGroupId().toString(),
                OWNER_ROLES);
    }

    @SuppressWarnings("unchecked")
    private static Role getRoleByName(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        final RoleSearchCriteria rsc = new RoleSearchCriteria(role);
        final List<Role> roleList = authMgr.getObjects(rsc);
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
        final String queryString =
                "SELECT ugrpg FROM " + UserGroupRoleProtectionGroup.class.getName() + " ugrpg, "
                        + ProtectionElement.class.getName() + " pe "
                        + "WHERE pe in elements(ugrpg.protectionGroup.protectionElements) "
                        + "  AND size(ugrpg.protectionGroup.protectionElements) = 1" + "  AND pe.attribute = 'id' "
                        + "  AND pe.objectId = :objectId " + "  AND pe.value = :value "
                        + "  AND ugrpg.role.name in (:roleNames)";
        final Query q = hibernateHelper.getCurrentSession().createQuery(queryString);
        q.setParameterList("roleNames", new String[] {BROWSE_ROLE, READ_ROLE, WRITE_ROLE, PERMISSIONS_ROLE,
                PARTIAL_READ_ROLE, PARTIAL_WRITE_ROLE });
        q.setString("objectId", getUnderlyingEntityClass(p).getName());
        q.setString("value", p.getId().toString());
        return q.list();
    }

    private static ProtectionGroup getProtectionGroup(Protectable p) {
        final String queryString =
                "SELECT pg FROM " + ProtectionGroup.class.getName()
                        + " pg join pg.protectionElements pe inner join fetch pg.protectionElements pe2"
                        + " left join fetch pe2.owners "
                        + " WHERE pg.protectionGroupName LIKE 'PE(%) group' AND pe.objectId = :objectId "
                        + "  AND pe.attribute = 'id' AND pe.value = :value";
        Session s = null;
        try {
            s =
                    HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory
                            .getSessionFactory(caarrayAppName));
            final Query q = s.createQuery(queryString);
            q.setString("objectId", getUnderlyingEntityClass(p).getName());
            q.setString("value", p.getId().toString());
            return (ProtectionGroup) q.uniqueResult();
        } catch (final Exception e) {
            throw new IllegalStateException("couldn't execute hibernate query: " + e);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    private static List<Long> getProtectionGroupIds(List<String> protectableIds, String objectClassName) {
        return getCsmObjectIds(protectableIds, objectClassName, "SELECT pg.id FROM " + ProtectionGroup.class.getName()
                + " pg join pg.protectionElements pe"
                + " WHERE pg.protectionGroupName LIKE 'PE(%) group' AND pe.objectId = :objectId "
                + "  AND pe.attribute = 'id' AND ");
    }

    private static List<Long> getProtectionElementIds(List<String> protectableIds, String objectClassName) {
        return getCsmObjectIds(protectableIds, objectClassName,
                "SELECT pe.id FROM " + ProtectionElement.class.getName() + " pe"
                        + " WHERE pe.objectId = :objectId AND pe.attribute = 'id' AND ");
    }

    @SuppressWarnings("unchecked")
    private static List<Long> getCsmObjectIds(List<String> protectableIds, String objectClassName, String queryBase) {
        final Map<String, List<? extends Serializable>> idBlocks = new HashMap<String, List<? extends Serializable>>();
        final String queryString =
                queryBase + "(" + HibernateHelper.buildInClause(protectableIds, "pe.value", idBlocks) + ")";
        Session s = null;
        try {
            s =
                    HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory
                            .getSessionFactory(caarrayAppName));
            final Query q = s.createQuery(queryString);
            q.setString("objectId", objectClassName);
            HibernateHelper.bindInClauseParameters(q, idBlocks);
            return q.list();
        } catch (final Exception e) {
            throw new IllegalStateException("couldn't execute hibernate query: " + e);
        } finally {
            if (s != null) {
                s.close();
            }
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
        final ProtectionGroup pg = getProtectionGroup(p);
        final ProtectionElement pe = (ProtectionElement) pg.getProtectionElements().iterator().next();
        return new HashSet<User>(pe.getOwners());
    }

    private static User getOwner(Protectable p) {
        final Set<User> owners = getOwners(p);
        if (owners.isEmpty()) {
            return null;
        }
        return owners.iterator().next();
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
     * Returns whether the given user has READ or PARTIAL_READ privileges for the given Protectable.
     * 
     * @param p the protectable to check
     * @param user the user to check
     * @param allowPartial set to true to allow the PARTIAL_READ permission
     * @return whether the given user has READ privilege for the given Protectable
     */
    private static boolean canRead(Protectable p, User user, boolean allowPartial) {
        return hasPrivilege(p, user, READ_PRIVILEGE) || (allowPartial && hasPrivilege(p, user, PARTIAL_READ_PRIVILEGE));
    }

    /**
     * Returns whether the given user has READ or PARTIAL_READ privileges for the given Protectable.
     * 
     * @param p the protectable to check
     * @param user the user to check
     * @return whether the given user has READ privilege for the given Protectable
     */
    public static boolean canRead(Protectable p, User user) {
        return canRead(p, user, true);
    }

    /**
     * Returns whether the given user has READ privileges for the given Protectable.
     * 
     * @param p the protectable to check
     * @param user the user to check
     * @return whether the given user has READ privilege for the given Protectable
     */
    public static boolean canFullRead(Protectable p, User user) {
        return canRead(p, user, false);
    }

    /**
     * Returns whether the given user has WRITE or PARTIAL_WRITE privileges for a given entity.
     * This is determined by either checking whether the user has WRITE or PARTIAL_WRITE privileges for that entity
     * directly (if it's a Protectable) or by checking whether the user has the WRITE privilege for any of the related
     * Protectables (if it's a ProtectableDescendent). If it is neither of those, then return true.
     * 
     * @param o the entity to check
     * @param user the user to check
     * @param allowPartial set to true to allow the PARTIAL_WRITE permission
     * @return whether the given user has WRITE privilege for the given entity
     */
    private static boolean canWrite(PersistentObject o, User user, boolean allowPartial) {
        if (o instanceof Protectable) {
            return hasPrivilege((Protectable) o, user, WRITE_PRIVILEGE)
                    || (allowPartial && hasPrivilege((Protectable) o, user, PARTIAL_WRITE_PRIVILEGE));
        }
        if (o instanceof ProtectableDescendent) {
            final Collection<? extends Protectable> protectables = ((ProtectableDescendent) o).relatedProtectables();
            if (protectables == null) {
                return true;
            }
            for (final Protectable p : protectables) {
                if (hasPrivilege(p, user, WRITE_PRIVILEGE)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns whether the given user has WRITE or PARTIAL_WRITE privileges for a given entity.
     * This is determined by either checking whether the user has WRITE or PARTIAL_WRITE privileges for that entity
     * directly (if it's a Protectable) or by checking whether the user has the WRITE privilege for any of the related
     * Protectables (if it's a ProtectableDescendent). If it is neither of those, then return true.
     * 
     * @param o the entity to check
     * @param user the user to check
     * @return whether the given user has WRITE or PARTIAL_WRITE privilege for the given entity
     */
    public static boolean canWrite(PersistentObject o, User user) {
        return canWrite(o, user, true);
    }

    /**
     * Returns whether the given user has WRITE privileges for a given entity.
     * This is determined by either checking whether the user has WRITE privileges for that entity
     * directly (if it's a Protectable) or by checking whether the user has the WRITE privilege for any of the related
     * Protectables (if it's a ProtectableDescendent). If it is neither of those, then return true.
     * 
     * @param o the entity to check
     * @param user the user to check
     * @return whether the given user has WRITE privilege for the given entity
     */
    public static boolean canFullWrite(PersistentObject o, User user) {
        return canWrite(o, user, false);
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

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private static boolean hasPrivilege(Protectable p, User user, String privilege) {
        // if the protectable is not yet saved, assume user only has access if he is the current user
        if (p.getId() == null) {
            return CaArrayUsernameHolder.getCsmUser().equals(user);
        }
        try {
            final Application app = getApplication();
            return AuthorizationManagerExtensions.checkPermission(user.getLoginName(), getUnderlyingEntityClass(p)
                    .getName(), "id", p.getId().toString(), privilege, app);
        } catch (final CSException e) {
            LOG.warn(String.format(
                    "Could not check if User %s had privilege %s for protectable of class %s with id %s",
                    user.getLoginName(), privilege, p.getClass().getName(), p.getId()));
            return false;
        }
    }

    /**
     * The method returns a mapping of Privileges for the given Protectables for the given user.
     * 
     * @param user The user for whom to calculate the privileges map
     * @param protectables the Protectable instances for which to look up permissions
     * 
     * @return Map from a Protectable id to a Privileges object showing the privileges the user with given user name has
     *         for the Protectable with that id
     */
    public static Map<Long, Privileges> getPrivileges(Collection<? extends Protectable> protectables, User user) {
        if (protectables.isEmpty()) {
            return Collections.emptyMap();
        }

        final Application app = getApplication();
        final Collection<Long> ids = new ArrayList<Long>();
        for (final Protectable p : protectables) {
            ids.add(p.getId());
        }
        final String className = getUnderlyingEntityClass(protectables.iterator().next()).getName();

        final InstanceLevelMappingElement mappingElt = findMappingElement(className, "id");

        if (mappingElt != null) {
            return getPermissionsWithMappingTable(mappingElt.getTableNameForGroup(), user.getLoginName(), ids);
        } else {
            return getPermissionsWithCanonicalTable(user.getLoginName(), className, "id", ids, app);
        }
    }

    private static Map<Long, Privileges> getPermissionsWithMappingTable(String groupTableName, String userName,
            Collection<Long> protectableIds) {
        final String sql =
                " select distinct pe.attribute_value, p.privilege_name from " + groupTableName + " pe "
                        + "inner join csm_user_group ug on pe.group_id = ug.group_id "
                        + "inner join csm_privilege p on pe.privilege_id = p.privilege_id "
                        + "inner join csm_user u on ug.user_id = u.user_id "
                        + "where pe.attribute_value in (:attr_values) and u.login_name = :login_name "
                        + "order by pe.attribute_value, p.privilege_name";
        final SQLQuery query = hibernateHelper.getCurrentSession().createSQLQuery(sql);
        query.setParameterList("attr_values", protectableIds);
        query.setString("login_name", userName);

        @SuppressWarnings("unchecked")
        final List<Object[]> results = query.list();
        return createPrivilegesMapFromResults(results);
    }

    private static Map<Long, Privileges> getPermissionsWithCanonicalTable(String userName, String className,
            String attributeName, Collection<Long> protectableIds, Application application) {
        final String sql =
                " select distinct cast(pe.attribute_value as unsigned), "
                        + "p.privilege_name from csm_protection_element pe "
                        + "inner join csm_pg_pe pgpe on pe.protection_element_id = pgpe.protection_element_id "
                        + "inner join csm_user_group_role_pg ugrpg "
                        + "on pgpe.protection_group_id = ugrpg.protection_group_id "
                        + "inner join csm_role r on ugrpg.role_id = r.role_id "
                        + "inner join csm_user_group ug on ugrpg.group_id = ug.group_id "
                        + "inner join csm_role_privilege rp on r.role_id = rp.role_id "
                        + "inner join csm_privilege p on rp.privilege_id = p.privilege_id "
                        + "inner join csm_user u on ug.user_id = u.user_id "
                        + "where pe.object_id = :class_name and pe.attribute = :attr_name "
                        + "and pe.attribute_value in (:attr_values) and u.login_name = :login_name "
                        + "and pe.application_id = :app_id order by pe.attribute_value, p.privilege_name";
        final SQLQuery query = hibernateHelper.getCurrentSession().createSQLQuery(sql);
        query.setParameterList("attr_values", protectableIds);
        query.setString("login_name", userName);
        query.setString("class_name", className);
        query.setString("attr_name", attributeName);
        query.setLong("app_id", application.getApplicationId());

        @SuppressWarnings("unchecked")
        final List<Object[]> results = query.list();
        return createPrivilegesMapFromResults(results);
    }

    private static Map<Long, Privileges> createPrivilegesMapFromResults(List<Object[]> results) {
        final Map<Long, Privileges> permissionsMap = new HashMap<Long, Privileges>();
        BigInteger currId = null;
        Privileges perm = null;
        for (final Object[] result : results) {
            final BigInteger id = (BigInteger) result[0];
            final String privilegeName = (String) result[1];
            if (!id.equals(currId)) {
                currId = id;
                perm = new Privileges();
                permissionsMap.put(currId.longValue(), perm);
            }
            perm.getPrivilegeNames().add(privilegeName);
        }

        return permissionsMap;
    }

    private static Class<?> getUnderlyingEntityClass(Object o) {
        return hibernateHelper.unwrapProxy(o).getClass();
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

    /**
     * create a group. This method is a partial replacement for
     * {@link AuthorizationManager#createGroup(gov.nih.nci.security.authorization.domainobjects.Group)} to ensure that
     * groups are loaded using the caArray (audited) Hibernate session. It does not perform some of the validations that
     * caArray already does. Does not demarcate transactions; should be called from a service layer.
     * 
     * @param group the CSM group to creat.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    public static void createGroup(Group group) throws CSTransactionException {
        try {
            group.setUpdateDate(new Date());
            group.setApplication(getApplication());
            final Session s = hibernateHelper.getCurrentSession();
            s.save(group);
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }

    /**
     * add a multiple users from a group. This method is a partial replacement for
     * {@link AuthorizationManager#assignUsersToGroup(java.lang.String, java.lang.String[])} to ensure that groups are
     * loaded using the caArray (audited) Hibernate session. Does not demarcate transactions; should be called from a
     * service layer.
     * 
     * @param groupId the CSM group Id to add the users to.
     * @param userIds the CSM user Ids to add to the group.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    @SuppressWarnings("unchecked")
    public static void assignUsersToGroup(Long groupId, Set<Long> userIds) throws CSTransactionException {
        try {
            final Session s = hibernateHelper.getCurrentSession();
            final Group group = (Group) s.load(Group.class, groupId);
            if (group.getUsers() == null) {
                group.setUsers(new HashSet<User>(userIds.size()));
            }
            for (final Long uId : userIds) {
                final User u = (User) s.load(User.class, uId);
                group.getUsers().add(u);
            }
            s.update(group);
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }

    /**
     * remove a user from a group. This method is a partial replacement for
     * {@link AuthorizationManager#removeUserFromGroup(java.lang.String, java.lang.String)} to ensure that groups are
     * loaded using the caArray (audited) Hibernate session. Does not demarcate transactions; should be called from a
     * service layer.
     * 
     * @param groupId the CSM group Id to add the users to.
     * @param userId Id of the CSM user to remove to the group.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    public static void removeUserFromGroup(Long groupId, Long userId) throws CSTransactionException {
        try {
            final Session s = hibernateHelper.getCurrentSession();
            final Group group = (Group) s.load(Group.class, groupId);
            final User user = (User) s.load(User.class, userId);
            user.getGroups().remove(group);
            group.getUsers().remove(user);
            s.update(user);
            s.update(group);
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }

    /**
     * delete the group identified by it's Id. This method is a partial replacement for
     * {@link AuthorizationManager#removeGroup(java.lang.String)} to ensure that groups are loaded using the caArray
     * (audited) Hibernate session.
     * 
     * @param groupId the CSM group Id to delete.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    public static void removeGroup(Long groupId) throws CSTransactionException {
        try {
            final Session s = hibernateHelper.getCurrentSession();
            final Group group = (Group) s.load(Group.class, groupId);
            s.delete(group);
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }

    /**
     * get all users belonging in the group identified by it's Id. This method is a partial replacement for
     * {@link AuthorizationManager#getUsers(java.lang.String)} to ensure that groups are loaded using the caArray
     * (audited) Hibernate session. Does not demarcate transactions; should be called from a service layer.
     * 
     * @param groupId CSM group id.
     * @return list of members.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    public static Set<User> getUsers(Long groupId) throws CSTransactionException {
        try {
            final Session s = hibernateHelper.getCurrentSession();
            final Group group = (Group) s.load(Group.class, groupId);
            @SuppressWarnings("unchecked")
            Set<User> us = group.getUsers();
            if (us == null) {
                us = Collections.emptySet();
            }
            return us;
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }

    /**
     * find a group my it's name in this the caArray application context. This method is a partial replacement for
     * {@link AuthorizationManager#getObjects(gov.nih.nci.security.dao.SearchCriteria)} to ensure that groups are loaded
     * using the caArray (audited) Hibernate session. Does not demarcate transactions; should be called from a service
     * layer.
     * 
     * @param g group name
     * @return a group in this app that has the given name.
     * @throws CSTransactionException if a hibernate exception occures.
     */
    public static Group findGroupByName(String g) throws CSTransactionException {
        try {
            final Session s = hibernateHelper.getCurrentSession();
            final Criteria c = s.createCriteria(Group.class);
            c.add(Restrictions.eq("groupName", g));
            c.add(Restrictions.eq("application", getApplication()));
            return (Group) c.uniqueResult();
        } catch (final HibernateException e) {
            throw new CSTransactionException(e);
        }
    }
}
