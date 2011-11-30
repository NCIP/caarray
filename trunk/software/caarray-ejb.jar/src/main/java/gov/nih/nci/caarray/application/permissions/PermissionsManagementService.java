/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.List;

/**
 * Interface to the PermissionsManagementService, provides for the creation and management
 * of authorization groups and the setting of permissions.
 */
public interface PermissionsManagementService {

    /**
     * The default JNDI name to use to lookup <code>PermissionsManagementService</code>.
     */
    String JNDI_NAME = "caarray/PermissionsManagementServiceBean/local";

    /**
     * Delete a collaborator group.
     *
     * @param group the group to delete
     * @throws CSTransactionException on CSM error
     */
    void delete(CollaboratorGroup group) throws CSTransactionException;

    /**
     * @return all collaborator groups in the system
     */
    List<CollaboratorGroup> getCollaboratorGroups();

    /**
     * Get all the collaborator groups owned by the current user.
     * @return all collaborator groups owned by the current user
     */
    List<CollaboratorGroup> getCollaboratorGroupsForCurrentUser();

    /**
     * Create a new CollaboratorGroup.  The owner of the group will be the
     * currently logged in user.  The group will have no members.
     *
     * @param name name of the collaborator group.
     * @return the new group
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    CollaboratorGroup create(String name) throws CSTransactionException, CSObjectNotFoundException;

    /**
     * Adds users to the target group.
     *
     * @param targetGroup group to add members to
     * @param users user ids to add (as strings)
     * @throws CSTransactionException  on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void addUsers(CollaboratorGroup targetGroup, List<Long> users) throws CSTransactionException,
            CSObjectNotFoundException;

    /**
     * Adds users to a CSM group.
     * @param groupName name of CSM group to add members to
     * @param usernames usernames to add
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void addUsers(String groupName, String... usernames) throws CSTransactionException, CSObjectNotFoundException;

    /**
     * Removes users from the target group.
     *
     * @param targetGroup group to remove members from
     * @param userIds user ids to remove (as strings)
     * @throws CSTransactionException  on CSM error
     */
    void removeUsers(CollaboratorGroup targetGroup, List<Long> userIds) throws CSTransactionException;

    /**
     * Renames a collaboration group.
     *
     * @param targetGroup group to rename
     * @param groupName new name
     * @throws CSTransactionException on CSM error
     * @throws CSObjectNotFoundException on CSM error
     */
    void rename(CollaboratorGroup targetGroup, String groupName) throws CSTransactionException,
            CSObjectNotFoundException;

    /**
     * Returns users matching the given example user.
     *
     * @param u example user (may be null)
     * @return users in the system meeting the criteria
     */
    List<User> getUsers(User u);

    /**
     * Creates or updates an access profile.
     *
     * @param profile the profile to create or update
     */
    void saveAccessProfile(AccessProfile profile);

    /**
     * Changes the owner of a collaboration group.
     *
     * @param targetGroupId ID of group to change owner of
     * @param username new owner
     * @throws CSException on CSM error
     */
    void changeOwner(Long targetGroupId, String username) throws CSException;

    /**
     * Get all collaboration groups owned by a user.
     *
     * @param userId owner.
     * @return the collaboration groups owned by user with given id.
     */
    List<CollaboratorGroup> getCollaboratorGroupsForOwner(long userId);
}
