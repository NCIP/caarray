//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.domainobjects.UserGroupRoleProtectionGroup;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import gov.nih.nci.security.system.ApplicationSessionFactory;
import gov.nih.nci.security.util.StringUtilities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.util.HibernateHelper;

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

    private AuthorizationManagerExtensions() {
    }

    /**
     * Add the user with given id to the set of owners of the protection element with given id. If that user was
     * already an owner of that protection element, then this method is a no-op.
     * @param protectionElementId if of the protection element to modify.
     * @param user id of the user to add to the set of owners.
     * @param appName application context name
     * @throws CSTransactionException if there is an error in performing the operation
     */
    @SuppressWarnings("unchecked")
    public static void addOwner(Long protectionElementId, User user, String appName) throws CSTransactionException {

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
            owners.add(user);
            s.update(pe);
            s.flush();
            t.commit();
        } catch (Exception ex) {
            LOG.error(ex);
            try {
                t.rollback();
            } catch (Exception ex3) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||addOwner|Failure|Error in Rolling Back Transaction|" + ex3.getMessage());
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization|||addOwner|Failure|Error in adding the Owner " + user.getLoginName()
                        + "for the Protection Element Id " + protectionElementId + "|");
            }
            throw new CSTransactionException("An error occured in assigning Owners to the Protection Element\n"
                    + ex.getMessage(), ex);
        } finally {
            try {
                s.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||addOwner|Failure|Error in Closing Session |" + ex2.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization|||addOwner|Success|Successful in adding the Owner " + user
                    + "for the Protection Element Id " + protectionElementId + "|");
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
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        boolean test = false;
        Session s = null;

        Connection connection = null;
        if (StringUtilities.isBlank(userName)) {
            throw new CSException("user name can't be null!");
        }
        if (StringUtilities.isBlank(className)) {
            throw new CSException("objectId can't be null!");
        }

        test = checkOwnership(userName, className, attributeName, value, application.getApplicationName());
        if (test) {
            return true;
        }

        if (attributeName == null || privilegeName == null) {
            return false;
        }

        try {

            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(
                    application.getApplicationName()));

            connection = s.connection();

            preparedStatement =
                    CaarrayQueries.getQueryForUserAndGroupForAttributeValue(userName, className, attributeName,
                            value, privilegeName, application.getApplicationId().intValue(), connection);

            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                test = true;
            }
            rs.close();

            preparedStatement.close();

        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to get privileges for " + userName + "|" + ex.getMessage());
            }
            throw new CSException("Failed to get privileges for " + userName + "|" + ex.getMessage(), ex);
        } finally {
            try {

                s.close();
                rs.close();
                preparedStatement.close();
            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG
                            .debug("Authorization|||getPrivilegeMap|Failure|Error in Closing Session |"
                                    + ex2.getMessage());
                }
            }
        }

        return test;
    }

    @SuppressWarnings("PMD")
    // adapted from CSM code
    private static boolean checkOwnership(String userName, String className, String attribute,
            String value, String appName) {
        boolean test = false;
        Session s = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet rs = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(ApplicationSessionFactory.getSessionFactory(appName));

            connection = s.connection();

            StringBuffer stbr = new StringBuffer();
            stbr.append("select  user_protection_element_id from"
                    + " csm_user_pe upe, csm_user u, csm_protection_element pe"
                    + " where pe.object_id = ? and pe.attribute = ? and pe.attribute_value = ? and u.login_name = ?"
                    + " and upe.protection_element_id=pe.protection_element_id" + " and upe.user_id = u.user_id");

            preparedStatement = connection.prepareStatement(stbr.toString());
            int i = 1;
            preparedStatement.setString(i++, className);
            preparedStatement.setString(i++, attribute);
            preparedStatement.setString(i++, value);
            preparedStatement.setString(i++, userName);

            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                test = true;
            }

        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Authorization||" + userName
                        + "|checkOwnerShip|Failure|Error in checking ownership for user " + userName
                        + " and Protection Element " + className + "|" + ex.getMessage());
            }
        } finally {
            try {
                rs.close();
                preparedStatement.close();

            } catch (Exception ex2) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||checkOwnerShip|Failure|Error in Closing Session |" + ex2.getMessage());
                }
            }

            try {
                s.close();
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authorization|||checkOwnerShip|Failure|Error in Closing Session |" + ex.getMessage());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorization||" + userName
                    + "|checkOwnerShip|Success|Successful in checking ownership for user " + userName
                    + " and Protection Element " + className + "|");
        }
        return test;
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
     * but can be used if the Group and ProtectionGroup have already been loaded.
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
     * Utility class to construct PreparedStatement queries, adapted from CSM.
     */
    @SuppressWarnings("PMD")
    // method adopted from CSM
    private static final class CaarrayQueries {
        private CaarrayQueries() {
        }

        protected static PreparedStatement getQueryForUserAndGroupForAttributeValue(String loginName,
                String objectId, String attribute, String value, String privilegeName, int applicationId,
                Connection cn) throws SQLException {

            StringBuffer stbr = new StringBuffer();
            stbr.append("and pe.object_id=?");
            stbr.append(" and pe.attribute=?");
            stbr.append(" and pe.attribute_value=?");
            stbr.append(" and u.login_name=?");
            stbr.append(" and p.privilege_name=?");
            stbr.append(" and pg.application_id=?");
            stbr.append(" and pe.application_id=?");

            StringBuffer sqlBfr = new StringBuffer();
            sqlBfr.append(getStaticStringForUserAndGroupForAttribute());
            sqlBfr.append(stbr.toString());
            sqlBfr.append(" union ");
            sqlBfr.append(getStaticStringForUserAndGroupForAttribute2());
            sqlBfr.append(stbr.toString());

            int i = 1;
            PreparedStatement pstmt = cn.prepareStatement(sqlBfr.toString());
            pstmt.setString(i++, objectId);
            pstmt.setString(i++, attribute);
            pstmt.setString(i++, value);
            pstmt.setString(i++, loginName);
            pstmt.setString(i++, privilegeName);
            pstmt.setInt(i++, applicationId);
            pstmt.setInt(i++, applicationId);

            pstmt.setString(i++, objectId);
            pstmt.setString(i++, attribute);
            pstmt.setString(i++, value);
            pstmt.setString(i++, loginName);
            pstmt.setString(i++, privilegeName);
            pstmt.setInt(i++, applicationId);
            pstmt.setInt(i++, applicationId);

            return pstmt;
        }

        @SuppressWarnings("PMD")
        // method adopted from CSM
        private static String getStaticStringForUserAndGroupForAttribute() {
            StringBuffer stbr = new StringBuffer();
            stbr.append("select 'X'");
            stbr.append(" from csm_protection_group pg,");
            stbr.append(" csm_protection_element pe,");
            stbr.append(" csm_pg_pe pgpe,");
            stbr.append(" csm_user_group_role_pg ugrpg,");
            stbr.append(" csm_user u,");
            stbr.append(" csm_role_privilege rp,");
            stbr.append(" csm_role r,");
            stbr.append(" csm_privilege p");
            stbr.append(" where ugrpg.role_id = r.role_id and");
            stbr.append(" ugrpg.user_id = u.user_id and");
            stbr.append(" ugrpg.protection_group_id  = ANY");
            stbr.append(" (select pg1.protection_group_id from csm_protection_group pg1 where");
            stbr.append(" pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = ");
            stbr.append(" (select pg2.parent_protection_group_id from csm_protection_group pg2 where");
            stbr.append(" pg2.protection_group_id = pg.protection_group_id)) and");
            stbr.append(" pg.protection_group_id = pgpe.protection_group_id and");
            stbr.append(" pgpe.protection_element_id = pe.protection_element_id and");
            stbr.append(" r.role_id = rp.role_id and");
            stbr.append(" rp.privilege_id = p.privilege_id ");

            return stbr.toString();
        }

        @SuppressWarnings("PMD")
        // method adopted from CSM
        private static String getStaticStringForUserAndGroupForAttribute2() {
            StringBuffer stbr = new StringBuffer();
            stbr.append("select 'X'");
            stbr.append(" from csm_protection_group pg,");
            stbr.append(" csm_protection_element pe,");
            stbr.append(" csm_pg_pe pgpe,");
            stbr.append(" csm_user_group_role_pg ugrpg,");
            stbr.append(" csm_user u,");
            stbr.append(" csm_user_group ug,");
            stbr.append(" csm_group g,");
            stbr.append(" csm_role_privilege rp,");
            stbr.append(" csm_role r,");
            stbr.append(" csm_privilege p");
            stbr.append(" where ugrpg.role_id = r.role_id and");
            stbr.append(" ugrpg.group_id = g.group_id and");
            stbr.append(" g.group_id = ug.group_id and");
            stbr.append(" ug.user_id = u.user_id and");
            stbr.append(" ugrpg.protection_group_id  = ANY");
            stbr.append(" (select pg1.protection_group_id from csm_protection_group pg1 where");
            stbr.append(" pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = ");
            stbr.append(" (select pg2.parent_protection_group_id from csm_protection_group pg2 where");
            stbr.append(" pg2.protection_group_id = pg.protection_group_id)) and");
            stbr.append(" pg.protection_group_id = pgpe.protection_group_id and");
            stbr.append(" pgpe.protection_element_id = pe.protection_element_id and");
            stbr.append(" r.role_id = rp.role_id and");
            stbr.append(" rp.privilege_id = p.privilege_id ");

            return stbr.toString();
        }
    }
}
