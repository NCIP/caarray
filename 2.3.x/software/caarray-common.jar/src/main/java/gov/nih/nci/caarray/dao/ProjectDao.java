//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.project</code> package.
 *
 * @author Rashmi Srinivasa
 */
public interface ProjectDao extends CaArrayDao {
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
    List<Project> getProjectsForCurrentUser(boolean showPublic, PageSortParams<Project> pageSortParams);

    /**
     * Gets the count of projects belonging to the current user. The count of either public or non-public projects
     * directly related to the current user are returned. A project is directly related to a user if the user is
     * either the data owner or in a collaboration group which has been granted access to the project.
     *
     * @param showPublic if true, then only projects in the "Public" workflow status are included; if false,
     * then only projects in workflow statuses other than "Public" are included in the count.
     *
     * @return the count of public or non-public projects directly related to the current user, as described above
     */
    int getProjectCountForCurrentUser(boolean showPublic);

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
     * @param keyword keyword to search for
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, SearchCategory... categories);

    /**
     * Retrieve a project based on its public identifier.
     * @param publicId the public identifier of the requested project
     * @return the Project with given public id, or null if no such Project
     */
    Project getProjectByPublicId(String publicId);

    /**
     * Get tissue sites for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getTissueSitesForExperiment(Experiment experiment);

    /**
     * Get material types for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getMaterialTypesForExperiment(Experiment experiment);

    /**
     * Returns the list of all Assay Types.
     * @return the List&lt;AssayType&gt; of assay types
     */
    List<AssayType> getAssayTypes();

    /**
     * Get cell types for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getCellTypesForExperiment(Experiment experiment);

    /**
     * Get disease states for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getDiseaseStatesForExperiment(Experiment experiment);

    /**
     * Return an unfiltered list of Samples for a Project (by-passes security @Filter).
     * @param project to find all samples associated to
     * @return all samples persisted for project
     */
    Set<Sample> getUnfilteredSamplesForProject(Project project);
}
