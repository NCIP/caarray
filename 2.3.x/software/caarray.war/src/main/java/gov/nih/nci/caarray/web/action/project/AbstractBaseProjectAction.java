//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Base Action class for all actions dealing with Project lifecycle.
 *
 * @author Dan Kokotov
 */
@Validation
public abstract class AbstractBaseProjectAction extends ActionSupport implements Preparable {
    private static final long serialVersionUID = 1L;

    /**
     * workspace.
     */
    public static final String WORKSPACE_RESULT = "workspace";
    /**
     * details.
     */
    public static final String DETAILS_RESULT = "details";
    /**
     * reload the project in top level frame.
     */
    public static final String RELOAD_PROJECT_RESULT = "reload-project";

    /**
     * Width (in characters) to which the experiment title should be truncated to prevent messages from overflowing
     * the UI.
     */
    public static final int TRUNCATED_TITLE_WIDTH = 80;

    private Project project = new Project();

    private boolean editMode;

    /**
     *
     */
    public AbstractBaseProjectAction() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void prepare() throws VocabularyServiceException {
        Project retrieved = null;
        if (this.project.getId() != null) {
            retrieved = getProjectManagementService().getProject(this.project.getId());
        } else if (this.project.getExperiment().getPublicIdentifier() != null) {
            retrieved = getProjectManagementService().getProjectByPublicId(
                    this.project.getExperiment().getPublicIdentifier());
        }
        if (retrieved != null) {
            this.project = retrieved;
        }
    }

    /**
     * get the csm user.  This method is extracted so it can be overwritten in test cases.
     * @return the csm user.
     */
    protected User getCsmUser() {
        return UsernameHolder.getCsmUser();
    }

    /**
     * @return the project
     */
    @CustomValidator(type = "hibernate")
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Convenience method for getting the experiment of the current project.
     * @return the project's experiment
     */
    protected Experiment getExperiment() {
        return getProject().getExperiment();
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return this.editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
