//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.InstanceLevelMappingElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.UserProtectionElement;
import gov.nih.nci.security.dao.InstanceLevelMappingElementSearchCriteria;
import gov.nih.nci.security.exceptions.CSDataAccessException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.system.ApplicationSessionFactory;
import gov.nih.nci.security.util.StringUtilities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.inject.Inject;

/**
 * Class with methods supplementing missing AuthorizationManager APIs. We expect these to eventually be implemented by
 * CSM proper, at which point they can be punted
 *
 * @author dkokotov
 */
@SuppressWarnings("PMD")
// adapted from CSM code
public final class AuthorizationManagerExtensions {
    private static final Logger LOG = Logger.getLogger(AuthorizationManagerExtensions.class);
    @Inject private static CaArrayHibernateHelper hibernateHelper; 

    private AuthorizationManagerExtensions() {
    }

    /**
     * Add the given user to the set of owners of the protection element with given ID. If that user was already an
     * owner of that protection element, then this method is a no-op.
     * @param protectionElementId ID of the protection element to modify
     * @param user user to add to the set of owners.
     * @param appName application context name
     * @throws CSTransactionException if there is an error in performing the operation
     */
    public static void addOwner(Long protectionElementId, User user, String appName) throws CSTransactionException {
        updateOwner(protectionElementId, null, user, appName, true);
    }

    /**
     * Replace the current owner with a new owner, for the protection element with given id.  If the protection
     * element has multiple owners, only <code>oldOwner</code> will be modified.
     * @param protectionElementId ID of the protection element to modify
     * @param oldOwner owner to remove from the set of owners
     * @param newOwner owner to add to the set of owners
     * @param appName application context name
     * @throws CSTransactionException if there is an error in performing the operation
     */
    public static void replaceOwner(Long protectionElementId, User oldOwner, User newOwner, String appName)
            throws CSTransactionException {
        updateOwner(protectionElementId, oldOwner, newOwner, appName, false);
    }

    /**
     * Add the user with given ID to the set of owners of the protection element with given id or set the user as the
     * only owner, depending on the value of <code>addOwnder</code>. If that user was already an owner of that
     * protection element, then this method is a no-op.
     * @param protectionElementId ID of the protection element to modify
     * @param oldOwner current owner
     * @param newOwner user to add to the set of owners.
     * @param appName application context name
     * @param addOwner true if the user should be added to the existing set of owners, false to replace the old owner
     * with the new owner
     * @throws CSTransactionException if there is an error in performing the operation
     */
    @SuppressWarnings("unchecked")
    private static void updateOwner(Long protectionElementId, User oldOwner, User newOwner, String appName,
            boolean addOwner) throws CSTransactionException {
        Session s = null;
        Transaction t = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(appName));
            t = s.beginTransaction();

            ProtectionElement pe = (ProtectionElement) s.load(ProtectionElement.class, protectionElementId);
            Set<User> owners = pe.getOwners();
            if (owners == null) {
                owners = new HashSet<User>();
                pe.setOwners(owners);
            }
            if (addOwner) {
                owners.add(newOwner);
            } else {
                owners.remove(oldOwner);
                owners.add(newOwner);
            }
            s.update(pe);
            s.flush();
            t.commit();
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateOwner|Failure|Error in Rolling Back Transaction|"
                            + ex3.getMessage());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization|||updateOwner|Failure|Error in setting " + newOwner.getLoginName()
                        + " as the owner for the Protection Element Id " + protectionElementId + "|");
            }
            throw new CSTransactionException("An error occured in updating owners of the Protection Element\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateOwner|Failure|Error in Closing Session |" + ex2.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization|||updateOwner|Success|Successful in updating " + newOwner
                    + " as the owner for the Protection Element Id " + protectionElementId + "|");
        }
    }

    /**
     * Replace an existing owner of the protection elements with given IDs with a new owner.
     * @param protectionElementIds IDs of the protection elements to modify
     * @param oldOwner existing owner of the protection elements
     * @param newOwner the new owner of the protection elements
     * @param appName application context name
     * @throws CSTransactionException if there is an error in performing the operation
     */
    public static void replaceOwner(List<Long> protectionElementIds, User oldOwner, User newOwner, String appName)
            throws CSTransactionException {
        Session s = null;
        Transaction t = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(appName));
            t = s.beginTransaction();

            Map<String, List<? extends Serializable>> idBlocks = new HashMap<String, List<? extends Serializable>>();
            String inClause = HibernateHelper.buildInClause(protectionElementIds, "protectionElement.id", idBlocks);
            String queryString = "update " + UserProtectionElement.class.getName()
                    + " set user = :newOwner where user = :oldOwner and (" + inClause + ")";
            Query q = s.createQuery(queryString);
            q.setParameter("newOwner", newOwner);
            q.setParameter("oldOwner", oldOwner);
            HibernateHelper.bindInClauseParameters(q, idBlocks);
            q.executeUpdate();
            s.flush();
            t.commit();
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateOwner|Failure|Error in Rolling Back Transaction|"
                            + ex3.getMessage());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization|||updateOwner|Failure|Error in setting the Owner " + newOwner.getLoginName()
                        + " for " + protectionElementIds.size() + " Protection Elements|");
            }
            throw new CSTransactionException("An error occured in assigning Owner to the Protection Elements\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateOwner|Failure|Error in Closing Session |" + ex2.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization|||updateOwner|Success|Successful in adding the Owner " + newOwner.getLoginName()
                    + " for " + protectionElementIds.size() + " Protection Elements|");
        }
    }

    /**
     * Change the group owner in the Group/Role/ProtectionGroup mapping for the given Protection Groups.
     * @param protectionGroupIds IDs of the protection groups to modify - must not be null or empty
     * @param oldOwnerGroup the current owner group
     * @param newOwnerGroup the new owner group
     * @param appName application context name
     * @throws CSTransactionException if there is an error in performing the operation
     */
    public static void updateGroupForRoleProtectionGroup(List<Long> protectionGroupIds, Group oldOwnerGroup,
            Group newOwnerGroup, String appName) throws CSTransactionException {
        Session s = null;
        Transaction t = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(appName));
            t = s.beginTransaction();

            Map<String, List<? extends Serializable>> idBlocks = new HashMap<String, List<? extends Serializable>>();
            String inClause = HibernateHelper.buildInClause(protectionGroupIds,
                    "ugrpg.protectionGroup.protectionGroupId", idBlocks);
            String queryString = "update " + UserGroupRoleProtectionGroup.class.getName()
                    + " ugrpg set ugrpg.group = :newOwnerGroup where ugrpg.group = :oldOwnerGroup and (" + inClause
                    + ")";
            Query q = s.createQuery(queryString);
            q.setParameter("newOwnerGroup", newOwnerGroup);
            q.setParameter("oldOwnerGroup", oldOwnerGroup);
            HibernateHelper.bindInClauseParameters(q, idBlocks);
            q.executeUpdate();
            s.flush();
            t.commit();
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateGroupForRoleProtectionGroup|Failure|"
                            + "Error in Rolling Back Transaction|" + ex3.getMessage());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization|||updateGroupForRoleProtectionGroup|Failure|Error in changing the group to "
                        + newOwnerGroup + " for " + protectionGroupIds.size() + " Protection Groups|");
            }
            throw new CSTransactionException("An error occured in changing the group owner of the Protection Groups\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||updateGroupForRoleProtectionGroup|Failure|Error in Closing Session |"
                            + ex2.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization|||updateGroupForRoleProtectionGroup|Success|Successful in changing the group to "
                    + newOwnerGroup + " for " + protectionGroupIds.size() + " Protection Groups|");
        }
    }

    /**
     * The method checks the permission for a user for a given Instance. The ProtectionElement for the instance is
     * obtained using the class name, attribute name, and value of that attribute The userName is used to to obtain the
     * User object. Then the check permission operation is performed to see if the user has the required access or not.
     * If caching is enabled for the user then the permissions are validated against the internal stored cache else the
     * query is fired against the database to check the permissions
     *
     * @param userName The user name of the user which is trying to perform the operation
     * @param className The name of the instance class
     * @param attributeName The attribute (property) of the class used to identify the instance
     * @param value the value of the property for the instance
     * @param privilegeName The operation which the user wants to perform on the protection element
     * @param application the application to which the instance belongs
     *
     * @return boolean Returns true if the user has permission to perform the operation on that particular instance
     * @throws CSException If there are any errors while checking for permission
     *
     * DEVELOPER NOTE: This code is adapted from the CSM Source code for getPermission(userName, objectId, attribute,
     * privilege) with appropriate modifications to allow identifying the specific instance
     */
    @SuppressWarnings("PMD")
    // method adapted from CSM code
    public static boolean checkPermission(String userName, String className, String attributeName, String value,
            String privilegeName, Application application) throws CSException {
        if (StringUtilities.isBlank(userName)) {
            throw new CSException("user name can't be null!");
        }
        if (StringUtilities.isBlank(className)) {
            throw new CSException("objectId can't be null!");
        }

        if (attributeName == null || privilegeName == null) {
            return false;
        }

        Session s = null;
        try {
            InstanceLevelMappingElement mappingElt = SecurityUtils.findMappingElement(className, attributeName);
            
            s = hibernateHelper.getSessionFactory().openSession();            
            if (mappingElt != null) {
                return checkPermissionWithMappingTable(mappingElt.getTableNameForGroup(), userName, value,
                        privilegeName, s);
            } else {
                return checkPermissionWithCanonicalTable(userName, className, attributeName, value, privilegeName,
                        application, s);
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to get privileges for " + userName + "|" + ex.getMessage());
            }
            throw new CSException("Failed to get privileges for " + userName + "|" + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                LOG.debug("Authorization|||checkPermission|Failure|Error in Closing Session |"
                        + ex2.getMessage());
            }
        }
    }
     
    private static boolean checkPermissionWithCanonicalTable(String userName, String className, String attributeName,
            String value, String privilegeName, Application application, Session s) throws CSException {
        String sql = " select pe.protection_element_id from csm_protection_element pe "
                + "inner join csm_pg_pe pgpe on pe.protection_element_id = pgpe.protection_element_id "
                + "inner join csm_user_group_role_pg ugrpg on pgpe.protection_group_id = ugrpg.protection_group_id "
                + "inner join csm_role r on ugrpg.role_id = r.role_id "
                + "inner join csm_user_group ug on ugrpg.group_id = ug.group_id "
                + "inner join csm_role_privilege rp on r.role_id = rp.role_id "
                + "inner join csm_privilege p on rp.privilege_id = p.privilege_id "
                + "inner join csm_user u on ug.user_id = u.user_id "
                + "where pe.object_id = :class_name and pe.attribute = :attr_name "
                + "and pe.attribute_value = :attr_value and u.login_name = :login_name "
                + "and p.privilege_name= :priv_name and pe.application_id = :app_id";
        SQLQuery query = s.createSQLQuery(sql);
        query.setString("class_name", className);
        query.setString("attr_name", attributeName);
        query.setString("attr_value", value);
        query.setString("login_name", userName);
        query.setString("priv_name", privilegeName);
        query.setLong("app_id", application.getApplicationId());

        List<?> results = query.list();
        return !results.isEmpty();
    }

    private static boolean checkPermissionWithMappingTable(String groupTableName, String userName, String value,
            String privilegeName, Session s) throws CSException {
        String sql = " select pe.attribute_value from " + groupTableName + " pe "
                + "inner join csm_user_group ug on pe.group_id = ug.group_id "
                + "inner join csm_privilege p on pe.privilege_id = p.privilege_id "
                + "inner join csm_user u on ug.user_id = u.user_id "
                + "where pe.attribute_value = :attr_value and u.login_name = :login_name " 
                + "and p.privilege_name= :priv_name";
        SQLQuery query = s.createSQLQuery(sql);
        query.setString("attr_value", value);
        query.setString("login_name", userName);
        query.setString("priv_name", privilegeName);

        List<?> results = query.list();
        return !results.isEmpty();
    }

    /**
     * Removes all roles from a Protection Group for a particular user group.
     * @param group user group
     * @param protectionGroups protection group from which to remove roles
     * @param application the application to which the instance belongs
     * @throws CSException on error
     */
    // Note: not directly adapted from existing CSM code but follows the conventions of other CSM code
    public static void clearProtectionGroupRoles(Group group, Collection<ProtectionGroup> protectionGroups,
            Application application) throws CSException {
        LOG.debug("Authorization|||clearProtectionGroupRoles|Start|Starting to clear protection group roles|");
        Session s = null;
        Transaction t = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(application
                    .getApplicationName()));
            t = s.beginTransaction();
            String queryString = "delete from " + UserGroupRoleProtectionGroup.class.getName()
                    + " where group = :group and (";
            Map<String, List<? extends Serializable>> blocks = new HashMap<String, List<? extends Serializable>>();
            List<Long> protectionGroupIds = new ArrayList<Long>();
            for (ProtectionGroup pg : protectionGroups) {
                protectionGroupIds.add(pg.getProtectionGroupId());
            }
            queryString += HibernateHelper.buildInClause(protectionGroupIds, "protectionGroup.id", blocks);
            queryString += ")";

            Query q = s.createQuery(queryString);
            q.setParameter("group", group);
            HibernateHelper.bindInClauseParameters(q, blocks);

            q.executeUpdate();

            s.flush();
            t.commit();
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                LOG.debug("Authorization|||clearProtectionGroupRoles|Failure|Error in Rolling Back Transaction|"
                        + ex3.getMessage());
            }
            LOG.debug("Authorization|||clearProtectionGroupRoles|Failure|Error in clearing protection group roles|");
            throw new CSTransactionException("An error occured in clearing protection group roles\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                LOG.debug("Authorization|||clearProtectionGroupRoles|Failure|Error in Closing Session |"
                        + ex2.getMessage());
            }
        }
        LOG.debug("Authorization|||clearProtectionGroupRoles|Success|Successful in clearing protection group roles|");

    }


    /**
     * Add roles to a protection group for a group.  Adapted from
     * {@link gov.nih.nci.security.dao.AuthorizationDAO#assignGroupRoleToProtectionGroup(String, String, String[])}
     * but can be used if the Group and ProtectionGroup have already been loaded and removes any roles for the group
     * that aren't in the list of given roles.
     * @param protectionGroup protection group to which to add roles
     * @param group group for which to add roles
     * @param roles roles to add
     * @param application the application to which the instance belongs
     * @throws CSTransactionException on error
     */
    // adapted from CSM code
    @SuppressWarnings("unchecked")
    public static void assignGroupRoleToProtectionGroup(ProtectionGroup protectionGroup, Group group, List<Role> roles,
            Application application) throws CSTransactionException {
        Session s = null;
        Transaction t = null;
        String[] roleIds = new String[roles.size()];
        int indx = 0;
        for (Role role : roles) {
            roleIds[indx++] = role.getId().toString();
        }
        try {

            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(application
                    .getApplicationName()));

            Criteria criteria = s.createCriteria(UserGroupRoleProtectionGroup.class);
            criteria.add(Restrictions.eq("protectionGroup", protectionGroup));
            criteria.add(Restrictions.eq("group", group));

            List<UserGroupRoleProtectionGroup> list = criteria.list();
            t = s.beginTransaction();
            for (int k = 0; k < list.size(); k++) {
                UserGroupRoleProtectionGroup ugrpg = list.get(k);
                Role r = ugrpg.getRole();
                if (!roles.contains(r)) {
                    s.delete(ugrpg);
                } else {
                    roles.remove(r);
                }
            }

            for (int j = 0; j < roles.size(); j++) {
                Role leftOverRole = roles.get(j);
                UserGroupRoleProtectionGroup toBeSaved = new UserGroupRoleProtectionGroup();
                toBeSaved.setGroup(group);
                toBeSaved.setProtectionGroup(protectionGroup);
                toBeSaved.setRole(leftOverRole);
                toBeSaved.setUpdateDate(new Date());
                s.save(toBeSaved);
            }
            t.commit();
            s.flush();
            LOG.info("Assigning Roles to Group " + group.getGroupName() + " for Protection Group "
                    + protectionGroup.getProtectionGroupName());
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||assignGroupsToUser|Failure|Error in Rolling Back Transaction|"
                            + ex3.getMessage());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization|||assignGroupRoleToProtectionGroup|Failure|Error Occured in assigning Roles "
                        + StringUtilities.stringArrayToString(roleIds) + " to Group "
                        + group.getGroupId() + " and Protection Group" + protectionGroup.getProtectionGroupId() + "|"
                        + ex.getMessage());
            }
            throw new CSTransactionException("An error occurred in assigning Protection Group and Roles to a Group\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||assignGroupRoleToProtectionGroup|Failure|Error in Closing Session |"
                            + ex2.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization|||assignGroupRoleToProtectionGroup|Success|Successful in assigning Roles "
                    + StringUtilities.stringArrayToString(roleIds) + " to Group "
                    + group.getGroupId() + " and Protection Group" + protectionGroup.getProtectionGroupId() + "|");
        }
    }

    /**
     * Refresh instance tables.
     * @param application the caarray application
     * @throws CSObjectNotFoundException on error
     * @throws CSDataAccessException on error
     */
    public static void refreshInstanceTables(Application application) throws CSObjectNotFoundException,
            CSDataAccessException {
        // Get Mapping Table Entries for Instance Level Security performance.
        InstanceLevelMappingElement mappingElement = new InstanceLevelMappingElement();
        @SuppressWarnings("unchecked")
        List<InstanceLevelMappingElement> mappingElements = SecurityUtils.getAuthorizationManager().getObjects(
                new InstanceLevelMappingElementSearchCriteria(mappingElement));
        if (mappingElements == null || mappingElements.size() == 0) {
            throw new CSObjectNotFoundException("Instance Level Mapping Elements do not exist.");
        }

        Statement statement = null;
        Transaction transaction = null;
        Session session = null;
        Connection connection = null;

        try {
            session = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory
                    .getSessionFactory(application.getApplicationName()));
            transaction = session.beginTransaction();
            // transaction.setTimeout(10000);
            connection = session.connection();
            statement = connection.createStatement();

            Iterator<InstanceLevelMappingElement> mappingElementsIterator = mappingElements.iterator();
            while (mappingElementsIterator.hasNext()) {
                InstanceLevelMappingElement instanceLevelMappingEntry = mappingElementsIterator.next();
                if (instanceLevelMappingEntry != null) {
                    if (instanceLevelMappingEntry.getActiveFlag() == 0) {
                        // Not active, so ignore this Object + Attribute from refresh logic.
                        continue;
                    }
                    if (!StringUtilities.isAlphaNumeric(instanceLevelMappingEntry.getAttributeName())
                            || !StringUtilities.isAlphaNumeric(instanceLevelMappingEntry.getObjectName())) {
                        // Mapping Entry is invalid.
                        throw new CSObjectNotFoundException(
                                "Invalid Instance Level Mapping Element. Instance Level Security breach is possible.");
                    }
                } else {
                    // Mapping Entry is invalid.
                    continue;
                }
                // get the Table Name and View Name for each object.

                String applicationID = application.getApplicationId().toString();
                String peiTableName, groupPrivilegeMappingTable, viewNameGroup = null;
                String peiObjectId = null;
                if (StringUtilities.isBlank(instanceLevelMappingEntry.getObjectPackageName())) {
                    peiObjectId = instanceLevelMappingEntry.getObjectName().trim();
                } else {
                    peiObjectId = instanceLevelMappingEntry.getObjectPackageName().trim() + "."
                            + instanceLevelMappingEntry.getObjectName().trim();
                }

                String peiAttribute = instanceLevelMappingEntry.getAttributeName().trim();

                if (StringUtilities.isBlank(instanceLevelMappingEntry.getTableName())) {
                    peiTableName = "CSM_PEI_" + instanceLevelMappingEntry.getObjectName() + "_"
                            + instanceLevelMappingEntry.getAttributeName();
                } else {
                    peiTableName = instanceLevelMappingEntry.getTableName();
                }

                if (StringUtilities.isBlank(instanceLevelMappingEntry.getTableNameForGroup())) {
                    groupPrivilegeMappingTable = "CSM_" + instanceLevelMappingEntry.getObjectName() + "_"
                            + instanceLevelMappingEntry.getAttributeName() + "_GROUP";
                } else {
                    groupPrivilegeMappingTable = instanceLevelMappingEntry.getTableNameForGroup();
                }
                if (StringUtilities.isBlank(instanceLevelMappingEntry.getViewNameForGroup())) {
                    viewNameGroup = "CSM_VW_" + instanceLevelMappingEntry.getObjectName() + "_"
                            + instanceLevelMappingEntry.getAttributeName() + "_GROUP";
                } else {
                    viewNameGroup = instanceLevelMappingEntry.getViewNameForGroup();
                }

                byte activeFlag = instanceLevelMappingEntry.getActiveFlag();
                if (activeFlag == 1) {

                    // refresh PEI Table, e.g. csm_pei_project_id or csm_pei_sample_id
                    deleteStaleEntriesFromPeiTable(statement, peiTableName);
                    insertNewEntriesIntoPeiTable(statement, applicationID, peiTableName, peiObjectId, peiAttribute);
                    
                    //refresh groupPrivilegeMappingTable, e.g. csm_project_id_group or csm_sample_id_group 
                    deleteStaleEntriesFromGroupPrivilegeMap(statement, groupPrivilegeMappingTable, viewNameGroup);
                    insertNewEntriesIntoGroupPrivilegeMap(statement, groupPrivilegeMappingTable, viewNameGroup);

                }
            }

            transaction.commit();
            statement.close();
        } catch (CSObjectNotFoundException e1) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex3) {
                }
            }
            throw e1;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex3) {
                }
            }
            throw new CSDataAccessException("Unable to perform data refresh for instance level security.", e);
        } finally {
            try {
                connection.close();
            } catch (Exception ex2) {
            }
            try {
                session.close();
            } catch (Exception ex2) {
                LOG
                        .debug("Authorization|||refreshInstanceTables|Failure|Error in Closing Session |"
                                + ex2.getMessage());
            }
        }
    }

    private static void insertNewEntriesIntoGroupPrivilegeMap(Statement statement, String groupPrivilegeMappingTable,
            String viewNameGroup) throws SQLException {
        ResultSet rs = statement.executeQuery(
                "select distinct group_id, privilege_id, attribute_value from " + viewNameGroup + " vw "
                + " where VW.GROUP_ID NOT IN (SELECT GROUP_ID FROM " + groupPrivilegeMappingTable 
                + " WHERE PRIVILEGE_ID=VW.PRIVILEGE_ID AND ATTRIBUTE_VALUE=VW.ATTRIBUTE_VALUE) ");
        while (rs.next()) {
            Long groupId = rs.getLong("group_id");
            Long privilegeId = rs.getLong("privilege_id");
            Long attributeValue = rs.getLong("attribute_value");

            String insertStmt = String.format(
                    "insert into %s (group_id, privilege_id, attribute_value) values(%s, %s, %s)", 
                    groupPrivilegeMappingTable, groupId, privilegeId, attributeValue);
            statement.addBatch(insertStmt);
        }
        statement.executeBatch();
    }

    private static void deleteStaleEntriesFromGroupPrivilegeMap(Statement statement, 
            String groupPrivilegeMappingTable, String viewNameGroup) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT GROUP_ID, PRIVILEGE_ID, ATTRIBUTE_VALUE FROM " 
                + groupPrivilegeMappingTable + " GROUP_PRIVILEGE_MAP WHERE GROUP_PRIVILEGE_MAP.GROUP_ID NOT IN ( " 
                + " SELECT GROUP_ID FROM " + viewNameGroup 
                + " WHERE GROUP_ID=GROUP_PRIVILEGE_MAP.GROUP_ID AND " 
                + " PRIVILEGE_ID=GROUP_PRIVILEGE_MAP.PRIVILEGE_ID AND "
                + " ATTRIBUTE_VALUE=GROUP_PRIVILEGE_MAP.ATTRIBUTE_VALUE) "
                );

        while (rs.next()) {
            Long groupId = rs.getLong("group_id");
            Long privilegeId = rs.getLong("privilege_id");
            Long attributeValue = rs.getLong("attribute_value");
            String deleteStmt = String.format(
                    "delete from %s where group_id=%s and privilege_id=%s and attribute_value=%s ", 
                    groupPrivilegeMappingTable, groupId, privilegeId, attributeValue);
            statement.addBatch(deleteStmt);
        }
        statement.executeBatch();
    }

    private static void insertNewEntriesIntoPeiTable(Statement statement, String applicationID, String peiTableName,
            String peiObjectId, String peiAttribute) throws SQLException {
        ResultSet rs = statement.executeQuery(
                "select protection_element_id, attribute_value from csm_protection_element pe"
                        + " where  pe.object_id = '" + peiObjectId + "' and  pe.attribute = '"
                        + peiAttribute + "' and pe.application_id = " + applicationID
                        + " and pe.attribute_value is not null "
                        + " and PE.PROTECTION_ELEMENT_ID NOT IN (SELECT PROTECTION_ELEMENT_ID FROM " 
                        + peiTableName + " WHERE ATTRIBUTE_VALUE=PE.ATTRIBUTE_VALUE) ");
        while (rs.next()) {
            Long protectionElementId = rs.getLong("protection_element_id");
            String attributeValue = rs.getString("attribute_value");

            String insertStmt = String.format(
                    "insert into %s (protection_element_id, attribute_value) values(%s, %s)", 
                    peiTableName, protectionElementId, attributeValue);
            statement.addBatch(insertStmt);
        }
        statement.executeBatch();
    }

    private static void deleteStaleEntriesFromPeiTable(Statement statement, String peiTableName) 
    throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT PROTECTION_ELEMENT_ID FROM " 
                + peiTableName + " PEI WHERE PEI.PROTECTION_ELEMENT_ID NOT IN ( "
                + " SELECT PROTECTION_ELEMENT_ID FROM CSM_PROTECTION_ELEMENT " 
                + " WHERE PROTECTION_ELEMENT_ID=PEI.PROTECTION_ELEMENT_ID) "
                );

        while (rs.next()) {
            Long protectionElementId = rs.getLong("protection_element_id");
            String deleteStmt = String.format("delete from %s where protection_element_id=%s", 
                    peiTableName, protectionElementId);
            statement.addBatch(deleteStmt);
        }
        statement.executeBatch();
    }
}
