//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
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

    /**
     * Renders the workspace page, with the list of all the users projects.
     *
     * @return path String
     */
    @SkipValidation
    public String workspace() {
        this.projects.setList(ServiceLocatorFactory.getProjectManagementService().getMyProjects(
                this.projects.getPageSortParams()));
        this.projects.setFullListSize(ServiceLocatorFactory.getProjectManagementService().getMyProjectCount());
        return Action.SUCCESS;
    }

    /**
     * @return the experiments
     */
    public SortablePaginatedList<Project, ProjectSortCriterion> getProjects() {
        return projects;
    }
}
