package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Action class for performing actions on project as a whole (ie create, edit, etc)
 * Actions for individual tabs in editing a project should subclass ProjectTabAction
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectAction extends BaseProjectAction {
    private static final long serialVersionUID = 1L;

    /**
     * create new project
     *
     * @return path String
     */
    @SkipValidation
    public String create() {
        return INPUT;
    }

    /**
     * edit existing project
     *
     * @return path String
     */
    @SkipValidation
    public String edit() {
        return INPUT;
    }

    /**
     * edit existing project permissions
     *
     * @return path String
     */
    @SkipValidation
    public String editPermissions() {
        return "permissions";
    }

    /**
     * show details for a project
     *
     * @return path String
     */
    @SkipValidation
    public String details() {
        setProject(getProjectManagementService().getProject(getProject().getId()));
        return DETAILS_RESULT;
    }
}