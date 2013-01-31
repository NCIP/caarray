//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;

/**
 * Class to handle the workspace pages.
 *
 * @author Scott Miller
 */
public class ProjectWorkspaceAction {
    private static final int PAGE_SIZE = 20;

    private final SortablePaginatedList<Project, ProjectSortCriterion> projects =
            new SortablePaginatedList<Project, ProjectSortCriterion>(PAGE_SIZE, ProjectSortCriterion.PUBLIC_ID.name(),
                    ProjectSortCriterion.class);
    private int publicCount;
    private int workQueueCount;

    /**
     * Renders the workspace page.
     *
     * @return path String
     */
    @SkipValidation
    public String workspace() {
        updateCounts();
        return Action.SUCCESS;
    }

    /**
     * Retrieve list of public projects.
     *
     * @return path String
     */
    @SkipValidation
    public String publicProjects() {
        updateCounts();
        this.projects.setList(getProjectManagementService().getMyProjects(true, this.projects.getPageSortParams()));
        this.projects.setFullListSize(getPublicCount());
        return Action.SUCCESS;
    }

    /**
     * Retrieve the list of my experiments.
     *
     * @return path string.
     */
    @SkipValidation
    public String myProjects() {
        updateCounts();
        this.projects.setList(getProjectManagementService().getMyProjects(false, this.projects.getPageSortParams()));
        this.projects.setFullListSize(getWorkQueueCount());
        return Action.SUCCESS;
    }

    private void updateCounts() {
        this.publicCount = getProjectManagementService().getMyProjectCount(true);
        this.workQueueCount = getProjectManagementService().getMyProjectCount(false);
    }

    /**
     * @return the publicCount
     */
    public int getPublicCount() {
        return publicCount;
    }

    /**
     * @return the workQueueCount
     */
    public int getWorkQueueCount() {
        return workQueueCount;
    }

    /**
     * @return the experiments
     */
    public SortablePaginatedList<Project, ProjectSortCriterion> getProjects() {
        return projects;
    }
}
