//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;

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
     * Returns the project corresponding to the id given.
     *
     * @param id the project id
     * @return the corresponding project, or null if there is no project corresponding to that id
     */
    Project getProject(long id);

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
     * internally consistent
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
     * internally consistent
     */
    CaArrayFile addFile(Project project, File file, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException;

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
     * internally consistent
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
     * @throws InconsistentProjectStateException if the project state is inconsistent and therefore it should
     * not be saved
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
     * Moves a project into a new workflow status.
     *
     * @param projectId the id of the project to move to the given status
     * @param newStatus the new workflow status
     * @throws ProposalWorkflowException if the project's current status does not allow a transition to the given status
     */
    void changeProjectStatus(long projectId, ProposalStatus newStatus) throws ProposalWorkflowException;

    /**
     * Gets a subset of the projects belonging to the current user. Either public or non-public projects directly
     * related to the current user are returned. A project is directly related to a user if the user is either the data
     * owner or in a collaboration group which has been granted access to the project. The subset to retrieve depends on
     * the page and sort specifications in pageSortParams
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     *
     * @param showPublic if true, then only projects in the "Public" workflow status are returned; if false, then only
     *            projects in workflow statuses other than "Public" are returned.
     * @param pageSortParams specifies the sorting to apply and which page of the full result set to return
     *
     * @return public or non-public projects directly related to the current user, as described above
     */
    List<Project> getMyProjects(boolean showPublic, PageSortParams<Project> pageSortParams);

    /**
     * Gets the count of projects belonging to the current user. The count of either public or non-public projects
     * directly related to the current user are returned. A project is directly related to a user if the user is either
     * the data owner or in a collaboration group which has been granted access to the project.
     *
     * @param showPublic if true, then only projects in the "Public" workflow status are included; if false, then only
     *            projects in workflow statuses other than "Public" are included in the count.
     *
     * @return the count of public or non-public projects directly related to the current user, as described above
     */
    int getMyProjectCount(boolean showPublic);

    /**
     * sets whether the project with given id uses the tcga policy.
     *
     * @param projectId the id of the project
     * @param useTcgaPolicy whether the tcga policy should be used
     * @return the modified project
     * @throws ProposalWorkflowException if the tcga policy status cannot be modified in the project's current state
     */
    Project setUseTcgaPolicy(long projectId, boolean useTcgaPolicy) throws ProposalWorkflowException;

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
     * internally consistent
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
     * internally consistent
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
     * internally consistent
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
     * internally consistent
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
     * internally consistent
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
     * Returns the sample for a given project with the given public identifier.
     *
     * @param project the project to which the sample belongs
     * @param externalSampleId the sample's external identifier
     * @return the sample with given external identifier, or null if there is no sample with that public identifier
     */
    Sample getSampleByExternalId(Project project, String externalSampleId);

    /**
     * Performs a query for samples and sources by text matching for the given keyword.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     *
     * @param <T> child of AbstractBioMaterial
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching experiments
     */
    <T extends AbstractBioMaterial>List<T>  searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories);


    /**
     * Performs a query for all samples which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Performs a query for all samples which are related to an experiment and also
     * contain a match on category keyword.
     * @param c category
     * @param keyword text keyword
     * @param e experiment
     * @return a list if samples with experiment matching e and category matching c
     */
    List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c);

    /**
     * Performs a query for all sources which contain a characteristic and category supplied.
     * Keyword is matched on %keyword%.
     * @param c category
     * @param keyword text
     * @return a list of sources
     */
    List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword);

    /**
     * Count of samples matching category and term value.
     * @param c category
     * @param keyword text
     * @return number of samples
     */
    int countSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Count of sources matching category and term value.
     * @param c category
     * @param keyword text
     * @return nmumber of sources
     */
    int countSourcesByCharacteristicCategory(Category c, String keyword);

    /**
     * Gets the count of search results matching the given keyword.
     *
     * @param keyword keyword to search for
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, BiomaterialSearchCategory... categories);


}
