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

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Provides project access and management functionality to the application. Interface to the ProjectManagement
 * subsystem.
 */
public interface ProjectManagementService {
    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/ProjectManagementServiceBean/local";

    /**
     * Returns the project with the given public identifier.
     * 
     * @param publicId the project public identifier
     * @return the project with given public identifier, or null if there is no project with that public identifier
     */
    Project getProjectByPublicId(String publicId);

    /**
     * Associates a single file with a project. After calling this method, clients can expect a new
     * <code>CaArrayFile</code> to be associated with the project.
     * 
     * @param project project to add the file to
     * @param file the file to add to the project
     * @return the new <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    CaArrayFile addFile(Project project, File file) throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * Associates a single file with a project. After calling this method, clients can expect a new
     * <code>CaArrayFile</code> to be associated with the project.
     * 
     * @param project project to add the file to
     * @param file the file to add to the project
     * @param filename the filename to use for the file. Allows the created CaArrayFile to have a different name from
     *            the file containing the content. This is useful for adding uploaded temporary files that don't use the
     *            original file name.
     * @return the new <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    CaArrayFile addFile(Project project, File file, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Adds a file chunk to a project.  If the file has been partially uploaded, this chunk will be appended to it.
     * Otherwise, a new <code>CaArrayFile</code> is added to the project.
     * 
     * @param project project to add the file to
     * @param file the file to add to the project
     * @param filename the filename to use for the file. Allows the created CaArrayFile to have a different name from
     *            the file containing the content. This is useful for adding uploaded temporary files that don't use the
     *            original file name.
     * @param fileSize the size of the complete file, as opposed to the size of a chunk.
     * @return the <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    CaArrayFile addFileChunk(Project project, File file, String filename, long fileSize)
            throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * Associates a single file with a project. After calling this method, clients can expect a new
     * <code>CaArrayFile</code> to be associated with the project.
     * 
     * @param project project to add the file to
     * @param data the input stream which will provide the file data
     * @param filename the filename to use for the file
     * @return the new <code>CaArrayFile</code>.
     * @throws ProposalWorkflowException if the project cannot currently be modified due to workflow status
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    CaArrayFile addFile(Project project, InputStream data, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Saves a project. The project may be new, or be currently in the draft or in progress state, but it cannot be
     * public. If the project is new, then it is put into the draft state.
     * 
     * @param project the project to save
     * @param orphansToDelete any objects orphaned by this save that should be deleted
     * @throws ProposalWorkflowException if the project cannot currently be saved because it is public
     * @throws InconsistentProjectStateException if the project state is inconsistent and therefore it should not be
     *             saved
     */
    void saveProject(Project project, PersistentObject... orphansToDelete) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Deletes a project. The project must be in the draft state.
     * 
     * @param project the project to save
     * @throws ProposalWorkflowException if the project cannot currently be deleted because it is not a draft
     */
    void deleteProject(Project project) throws ProposalWorkflowException;

    /**
     * Lock or unlock Project.
     * 
     * @param projectId the id of the project to move to the given status
     * @param newStatus the new lock status
     * @throws ProposalWorkflowException if the project's current status does not allow a transition to the given status
     */
    void changeProjectLockStatus(long projectId, boolean newStatus) throws ProposalWorkflowException;

    /**
     * Gets a subset of the projects belonging to the current user. All projects directly related to the current user
     * are returned. A project is directly related to a user if the user is either the data owner or in a collaboration
     * group which has been granted access to the project. The subset to retrieve depends on the page and sort
     * specifications in pageSortParams
     * 
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * 
     * @param pageSortParams specifies the sorting to apply and which page of the full result set to return
     * 
     * @return all projects directly related to the current user, as described above
     */
    List<Project> getMyProjects(PageSortParams<Project> pageSortParams);

    /**
     * Gets a the projects belonging to the given user.
     * 
     * @param user owner of the desired projects.
     * @return all projects directly related to the givenuser.
     */
    List<Project> getProjectsForOwner(User user);

    /**
     * Gets the count of projects belonging to the current user. The count of all projects directly related to the
     * current user are returned. A project is directly related to a user if the user is either the data owner or in a
     * collaboration group which has been granted access to the project.
     * 
     * @return the count of all projects directly related to the current user, as described above
     */
    int getMyProjectCount();

    /**
     * Adds an empty (no access) profile for the given collaborator group to the given project.
     * 
     * @param project the project
     * @param group the group for which to add an access profile
     * @return the new access profile
     * @throws ProposalWorkflowException if the project permissions cannot be modified in the project's current state
     */
    AccessProfile addGroupProfile(Project project, CollaboratorGroup group) throws ProposalWorkflowException;

    /**
     * Make a copy of a sample belonging to given project, and add it to the new project. The new sample's name will be
     * derived from the original sample's name according to the scheme described in
     * {@link gov.nih.nci.caarray.application.GenericDataService#getIncrementingCopyName(Class, String, String)}
     * 
     * @param project the project to which the sample belongs
     * @param sampleId the id of the sample to copy
     * @return the new sample
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    Sample copySample(Project project, long sampleId) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Make a copy of a source belonging to given project, and add it to the new project. The new source's name will be
     * derived from the original source's name according to the scheme described in
     * {@link gov.nih.nci.caarray.application.GenericDataService#getIncrementingCopyName(Class, String, String)}
     * 
     * @param project the project to which the source belongs
     * @param sourceId the id of the source to copy
     * @return the new source
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    Source copySource(Project project, long sourceId) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Make a copy of a factor belonging to given project, and add it to the new project. The new factor's name will be
     * derived from the original factor's name according to the scheme described in
     * {@link gov.nih.nci.caarray.application.GenericDataService#getIncrementingCopyName(Class, String, String)}
     * 
     * @param project the project to which the factor belongs
     * @param factorId the id of the factor to copy
     * @return the new factor
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    Factor copyFactor(Project project, long factorId) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Make a copy of an extract belonging to given project, and add it to the new project. The new extract's name will
     * be derived from the original extract's name according to the scheme described in
     * {@link gov.nih.nci.caarray.application.GenericDataService#getIncrementingCopyName(Class, String, String)}
     * 
     * @param project the project to which the extract belongs
     * @param extractId the id of the extract to copy
     * @return the new extract
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    Extract copyExtract(Project project, long extractId) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Make a copy of a labeled extract belonging to given project, and add it to the new project. The new labeled
     * extract's name will be derived from the original labeled extract's name according to the scheme described in
     * {@link gov.nih.nci.caarray.application.GenericDataService#getIncrementingCopyName(Class, String, String)}
     * 
     * @param project the project to which the labeled extract belongs
     * @param extractId the id of the extract to copy
     * @return the new labeled extract
     * @throws ProposalWorkflowException if the project cannot currently be modified because it is public
     * @throws InconsistentProjectStateException if the project cannot currently be modified because its state is not
     *             internally consistent
     */
    LabeledExtract copyLabeledExtract(Project project, long extractId) throws ProposalWorkflowException,
            InconsistentProjectStateException;

    /**
     * Performs a query for experiments by text matching for the given keyword.
     * 
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * 
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching experiments
     */
    List<Project> searchByCategory(PageSortParams<Project> params, String keyword, SearchCategory... categories);

    /**
     * Performs a query for experiments by the given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching experiments
     */
    List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria);

    /**
     * Gets the count of search results matching the given keyword.
     * 
     * @param keyword keyword to search for
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, SearchCategory... categories);

    /**
     * Get tissue sites for the experiment and category.
     * 
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getTissueSitesForExperiment(Experiment experiment);

    /**
     * Get material types for the experiment and category.
     * 
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getMaterialTypesForExperiment(Experiment experiment);

    /**
     * Returns the list of all Assay Types.
     * 
     * @return the List&lt;AssayType&gt; of assay types
     */
    List<AssayType> getAssayTypes();

    /**
     * Get cell types for the experiment and category.
     * 
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getCellTypesForExperiment(Experiment experiment);

    /**
     * Get disease states for the experiment and category.
     * 
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getDiseaseStatesForExperiment(Experiment experiment);

    /**
     * Get all characteristics for the experiment.
     * 
     * @param experiment the experiment
     * @return the list of characteristics
     */
    List<AbstractCharacteristic> getArbitraryCharacteristicsForExperimentSamples(Experiment experiment);

    /**
     * Get all characteristics for the experiment.
     * 
     * @param experiment the experiment
     * @return the list of characteristics categories
     */
    List<Category> getArbitraryCharacteristicsCategoriesForExperimentSamples(Experiment experiment);

    /**
     * Returns the biomaterial of given type for a given project with the given public identifier.
     * 
     * @param <T> the type of biomaterial desired
     * @param project the project to which the sample belongs
     * @param externalId the biomaterial's external identifier
     * @param biomaterialClass the AbstractBioMaterial subclass corresponding to the type of biomaterial desired
     * @return the sample with given external identifier, or null if there is no sample with that public identifier
     */
    <T extends AbstractBioMaterial> T getBiomaterialByExternalId(Project project, String externalId,
            Class<T> biomaterialClass);

    /**
     * Performs a query for samples and sources by text matching for the given keyword.
     * 
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * 
     * @param <T> subclass of AbstractBioMaterial, must be either Sample or Source
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching biomaterials
     */
    <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all samples which contain a characteristic and category supplied.
     * 
     * @param params sort params
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params, Category c, String keyword);

    /**
     * Performs a query for all samples which are related to an experiment and also contain a match on category keyword.
     * 
     * @param c category
     * @param keyword text keyword
     * @param e experiment
     * @return a list if samples with experiment matching e and category matching c
     */
    List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c);

    /**
     * Performs a query for all samples which are related to an experiment and contain a value match in given arbitrary 
     * characteristic category.
     * 
     * @param keyword text keyword
     * @param e experiment
     * @param c arbitrary characteristic category to perform search within
     * @return a list of samples matching this arbitrary characteristic value
     */
    List<Sample> searchSamplesByExperimentAndArbitraryCharacteristicValue(String keyword, Experiment e, Category c);

    /**
     * Performs a query for all sources which contain a characteristic and category supplied. Keyword is matched on
     * %keyword%.
     * 
     * @param params sort params
     * @param c category
     * @param keyword text
     * @return a list of sources
     */
    List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params, Category c, String keyword);

    /**
     * Count of samples matching category and term value.
     * 
     * @param c category
     * @param keyword text
     * @return number of samples
     */
    int countSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Count of sources matching category and term value.
     * 
     * @param c category
     * @param keyword text
     * @return nmumber of sources
     */
    int countSourcesByCharacteristicCategory(Category c, String keyword);

    /**
     * Gets the count of search results matching the given keyword.
     * 
     * @param keyword keyword to search for
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories);

    /**
     * Changes the owner of a project.
     * 
     * @param projectId ID of project to change owner of
     * @param newOwner username of new owner
     * @throws CSException on CSM error
     */
    void changeOwner(Long projectId, String newOwner) throws CSException;

    /**
     * Find files belonging to given project that can be deleted.
     * 
     * @param projectId id of the project
     * @return a list of deletable files in given project
     */
    List<CaArrayFile> getDeletableFiles(Long projectId);

    /**
     * Get projects that have files that were uploaded but not parsed by earlier caArray versions, but can now be
     * parsed.
     * 
     * @return projects with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<Project> getProjectsWithReImportableFiles();

    /**
     * Returns a list of all characteristic categories held by any biomaterial within the given experiment.
     * 
     * @param experiment the experiment
     * @return the list of characteristic categories
     */
    List<Category> getAllCharacteristicCategories(Experiment experiment);

}
