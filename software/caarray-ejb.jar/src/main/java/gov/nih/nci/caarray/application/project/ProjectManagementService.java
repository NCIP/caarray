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
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Provides project access and management functionality to the application. Interface to the
 * ProjectManagement subsystem.
 */
public interface ProjectManagementService {

    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/ProjectManagementServiceBean/local";

    /**
     * Returns the project corresponding to the id given.
     *
     * @param id the project id
     * @return the corresponding project.
     */
    Project getProject(long id);

    /**
     * Returns the organization corresponding to the id given.
     *
     * @param id the organization id
     * @return the corresponding organization.
     */
    Organization getOrganization(long id);

    /**
     * Associates a single file with a project. After calling this method, clients can expect a new
     * <code>CaArrayFile</code> to be associated with the project.
     *
     * @param project project to add the file to
     * @param file the file to add to the project
     * @return the new <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     */
    CaArrayFile addFile(Project project, File file) throws ProposalWorkflowException;

    /**
     * Associates a single file with a project. After calling this method, clients can expect a new
     * <code>CaArrayFile</code> to be associated with the project.
     *
     * @param project project to add the file to
     * @param file the file to add to the project
     * @param filename the filename to use for the file. Allows the created CaArrayFile to have a different name
     * from the file containing the content. This is useful for adding uploaded temporary files that don't use the
     * original file name.
     * @return the new <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     */
    CaArrayFile addFile(Project project, File file, String filename) throws ProposalWorkflowException;

    /**
     * Saves a project. The project may be new, or be currently in the draft or submitted state,
     * but it cannot be public.
     * If the project is new, then it is put into the draft state.
     *
     * @param project the project to save
     * @throws ProposalWorkflowException if the project cannot currently be saved because it is public
     */
    void saveProject(Project project) throws ProposalWorkflowException;

    /**
     * Moves a project into a new workflow status.
     *
     * @param projectId the id of the project to move to the given status
     * @param newStatus the new workflow status
     * @throws ProposalWorkflowException if the project's current status does not allow
     * a transition to the given status
     */
    void changeProjectStatus(long projectId, ProposalStatus newStatus) throws ProposalWorkflowException;

    /**
     * Gets all projects belonging to the current user.
     *
     * @return all projects belonging to the user.
     */
    List<Project> getMyNonPublicProjects();

    /**
     * Gets all public projects.
     *
     * @return allpublic projects
     */
    List<Project> getPublicProjects();

    /**
     * Toggles the browsable status for the given project.
     *
     * @param projectId the id of the project
     * @return the modified project
     */
    Project toggleBrowsableStatus(long projectId);

    /**
     * Adds an empty (no access) profile for the given collaborator group to the given project.
     *
     * @param project the project
     * @param group the group for which to add an access profile
     * @return the new access profile
     */
    AccessProfile addGroupProfile(Project project, CollaboratorGroup group);

    /**
     * Prepares files for download.
     *
     * @param files the files to download
     * @return the single zip archive with all files
     * @throws IOException on I/O error
     */
    File prepareForDownload(Collection<CaArrayFile> files) throws IOException;

    /**
     * Make a copy of a sample belonging to given project, and add it to the new project.
     * The new sample's name will be derived from the original sample's name
     * according to the scheme described in {@link GenericDataService#getIncrementingCopyName(Class, String, String)}
     * @param project the project to which the sample belongs
     * @param sampleId the id of the sample to copy
     * @return the new sample
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     */
    Sample copySample(Project project, long sampleId) throws ProposalWorkflowException;

    /**
     * Make a copy of a source belonging to given project, and add it to the new project.
     * The new source's name will be derived from the original source's name
     * according to the scheme described in {@link GenericDataService#getIncrementingCopyName(Class, String, String)}
     * @param project the project to which the source belongs
     * @param sourceId the id of the source to copy
     * @return the new source
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     */
    Source copySource(Project project, long sourceId) throws ProposalWorkflowException;

    /**
     * Make a copy of a factor belonging to given project, and add it to the new project.
     * The new factor's name will be derived from the original factor's name
     * according to the scheme described in {@link GenericDataService#getIncrementingCopyName(Class, String, String)}
     * @param project the project to which the factor belongs
     * @param factorId the id of the factor to copy
     * @return the new factor
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     */
    Factor copyFactor(Project project, long factorId) throws ProposalWorkflowException;
}
