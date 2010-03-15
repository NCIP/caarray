/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Collaborator group management action.
 */
@Validations(
        requiredFields = @RequiredFieldValidator(
                fieldName = "groupName", key = "struts.validator.requiredString", message = ""),
        stringLengthFields = @StringLengthFieldValidator(
                fieldName = "groupName", maxLength = "254", message = "", key = "struts.validator.stringLength"
        )
)
@SuppressWarnings("PMD.TooManyMethods")
public class CollaboratorsAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private List<CollaboratorGroup> groups;
    private CollaboratorGroup targetGroup;
    private String groupName;
    private User targetUser = new User();
    private List<Long> users;
    private List<User> allUsers;

    /**
     * @return listGroups
     */
    @SkipValidation
    public String listGroups() {
        this.groups = ServiceLocatorFactory.getPermissionsManagementService().getCollaboratorGroupsForCurrentUser();
        return "list";
    }

    /**
     * Deletes the targeted CollaboratorGroup.
     * @return listGroups
     * @throws CSTransactionException on CSM error
     */
    @SkipValidation
    public String delete() throws CSTransactionException {
        String grpName = this.targetGroup.getGroup().getGroupName();
        ServiceLocatorFactory.getPermissionsManagementService().delete(this.targetGroup);
        ActionHelper.saveMessage(getText("collaboration.group.record.deleted", new String[] {grpName}));
        return listGroups();
    }

    /**
     * Create a new group, or edit the name of an existing group.
     *
     * @return listGroups
     * @throws CSException on CSM error
     */
    public String name() throws CSException {
        if (targetGroup == null) {
            ServiceLocatorFactory.getPermissionsManagementService().create(getGroupName());
        } else {
            ServiceLocatorFactory.getPermissionsManagementService().rename(getTargetGroup(), getGroupName());
        }
        ActionHelper.saveMessage(getText("collaboration.group.record.saved", new String[] {getGroupName()}));
        return listGroups();
    }

    /**
     * Takes user to the edit group page.
     * @return edit
     */
    @SkipValidation
    public String edit() {
        return Action.SUCCESS;
    }

    /**
     * Takes the user to the user details screen.
     * @return userDetail
     */
    @SkipValidation
    public String userDetail() {
        return Action.SUCCESS;
    }

    /**
     * Adds the selected users to the current collaborator group.
     * @return success
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    @SuppressWarnings({"unchecked", "PMD" })
    @SkipValidation
    public String addUsers() throws CSTransactionException, CSObjectNotFoundException {
        if (getUsers() != null && !getUsers().isEmpty()) {
            ServiceLocatorFactory.getPermissionsManagementService().addUsers(getTargetGroup(), getUsers());
            String s = "Users";
            if (getUsers().size() == 1) {
                User u = SecurityUtils.getAuthorizationManager().getUserById(getUsers().get(0).toString());
                s = u.getFirstName() + " " + u.getLastName() + " (" + u.getLoginName() + ")";
            }
            ActionHelper.saveMessage(getText("collaboration.group.added", new String[] {s}));
        }
        setAllUsers((List<User>) CollectionUtils.subtract(ServiceLocatorFactory.getPermissionsManagementService()
                .getUsers(getTargetUser()), getTargetGroup().getGroup().getUsers()));
        return Action.SUCCESS;
    }

    /**
     * @return addUsers
     */
    @SkipValidation
    public String preAdd() {
        return "addUsers";
    }

    /**
     * Removes the selected users from the current collaborator group.
     * @return success
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    @SkipValidation
    public String removeUsers() throws CSTransactionException, CSObjectNotFoundException {
        if (getUsers() != null && !getUsers().isEmpty()) {
            ServiceLocatorFactory.getPermissionsManagementService().removeUsers(getTargetGroup(), getUsers());
            String s = "Users";
            if (getUsers().size() == 1) {
                User u = SecurityUtils.getAuthorizationManager().getUserById(getUsers().get(0).toString());
                s = u.getFirstName() + " " + u.getLastName() + " (" + u.getLoginName() + ")";
            }
            ActionHelper.saveMessage(getText("collaboration.group.removed", new String[] {s}));
        }
        return Action.INPUT;
    }

    /**
     * @return editTable
     */
    @SkipValidation
    public String editTable() {
        return "editTable";
    }

    /**
     * @return the groups
     */
    public List<CollaboratorGroup> getGroups() {
        return this.groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<CollaboratorGroup> groups) {
        this.groups = groups;
    }

    /**
     * @return the targetGroup
     */
    public CollaboratorGroup getTargetGroup() {
        return this.targetGroup;
    }

    /**
     * @param targetGroup the targetGroup to set
     */
    public void setTargetGroup(CollaboratorGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    /**
     * @return the groupName
     */
    @RequiredStringValidator(message = "Group Name is Required")
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the targetUser
     */
    public User getTargetUser() {
        return this.targetUser;
    }

    /**
     * @param targetUser the targetUser to set
     */
    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    /**
     * @return the targetUserId
     */
    public Long getTargetUserId() {
        if (this.targetUser == null) {
            return null;
        }
        return this.targetUser.getUserId();
    }

    /**
     * @param id CSM user id
     * @throws CSObjectNotFoundException if not in CSM
     */
    public void setTargetUserId(Long id) throws CSObjectNotFoundException {
        this.targetUser = SecurityUtils.getAuthorizationManager().getUserById(id.toString());
    }

    /**
     * @return the users
     */
    public List<Long> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<Long> users) {
        this.users = users;
    }

    /**
     * @return the allUsers
     */
    public List<User> getAllUsers() {
        return allUsers;
    }

    /**
     * @param allUsers the allUsers to set
     */
    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validate() {
        super.validate();
        if (!ActionHelper.isSkipValidationSetOnCurrentAction()) {
            if (StringUtils.isBlank(getGroupName())
                    || (getTargetGroup() != null
                            && getTargetGroup().getGroup().getGroupName().equals(getGroupName()))) {
                // Nothing to be done in this case
                return;
            }
            AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            Group g = new Group();
            g.setGroupName(getGroupName());
            GroupSearchCriteria gsc = new GroupSearchCriteria(g);
            List<Group> matchingGroups = am.getObjects(gsc);

            if (!matchingGroups.isEmpty()) {
                addFieldError("groupName", getText("collaboration.duplicateName", new String[] {getGroupName()}));
            }
        }
    }
}
