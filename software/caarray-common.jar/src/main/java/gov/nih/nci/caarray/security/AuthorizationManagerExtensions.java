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

import gov.nih.nci.caarray.util.HibernateUtil;
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
            
            s = HibernateUtil.getSessionFactory().openSession();            
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
                String peiTableName, tableNameGroup, viewNameGroup = null;
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
                    tableNameGroup = "CSM_" + instanceLevelMappingEntry.getObjectName() + "_"
                            + instanceLevelMappingEntry.getAttributeName() + "_GROUP";
                } else {
                    tableNameGroup = instanceLevelMappingEntry.getTableNameForGroup();
                }
                if (StringUtilities.isBlank(instanceLevelMappingEntry.getViewNameForGroup())) {
                    viewNameGroup = "CSM_VW_" + instanceLevelMappingEntry.getObjectName() + "_"
                            + instanceLevelMappingEntry.getAttributeName() + "_GROUP";
                } else {
                    viewNameGroup = instanceLevelMappingEntry.getViewNameForGroup();
                }

                byte activeFlag = instanceLevelMappingEntry.getActiveFlag();
                if (activeFlag == 1) {

                    // refresh PEI Table
                    statement.addBatch("alter table " + peiTableName + " disable keys");
                    statement.addBatch("truncate " + peiTableName);

                    statement.executeBatch();
                    statement
                            .addBatch("insert into "
                                    + peiTableName
                                    + " (protection_element_id, attribute_value) "
                                    + "   select protection_element_id, attribute_value from csm_protection_element pe"
                                    + "   where  pe.object_id = '" + peiObjectId + "' and  pe.attribute = '"
                                    + peiAttribute + "' and pe.application_id = " + applicationID
                                    + "        and pe.attribute_value is not null ");
                    statement.addBatch("alter table " + peiTableName + " enable keys");
                    statement.executeBatch();

                    statement.addBatch("alter table " + tableNameGroup + " disable keys");
                    statement.addBatch("truncate " + tableNameGroup);
                    statement
                            .addBatch("insert into " + tableNameGroup + " (group_id, privilege_id, attribute_value) "
                                    + "select distinct group_id, privilege_id, attribute_value from " + viewNameGroup);
                    statement.addBatch("alter table " + tableNameGroup + " enable keys");
                    statement.executeBatch();
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
}
