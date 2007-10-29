package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.domain.project.Project;

import java.util.ArrayList;
import java.util.List;

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

    private String menu = null;

    private List<Project> projects = new ArrayList<Project>();

    /**
     * Retrieve list of workspace projects
     *
     * @return path String
     */
    @SkipValidation
    public String list() {
        setMenu("ProjectListLinks");
        setProjects(getProjectManagementService().getWorkspaceProjects());
        return LIST_RESULT;
    }

    /**
     * create new project
     *
     * @return path String
     */
    @SkipValidation
    public String create() {
        setMenu("ProjectCreateLinks");
        return INPUT;
    }

    /**
     * edit existing project
     *
     * @return path String
     */
    @SkipValidation
    public String edit() {
        setMenu("ProjectEditLinks");
        return INPUT;
    }

    /**
     * edit existing project permissions
     *
     * @return path String
     */
    @SkipValidation
    public String editPermissions() {
        setMenu("ProjectEditLinks");
        return "permissions";
    }

    /**
     * show details for a project
     *
     * @return path String
     */
    @SkipValidation
    public String details() {
        setMenu("ProjectEditLinks");
        setProject(getProjectManagementService().getProject(getProject().getId()));
        return DETAILS_RESULT;
    }

    /**
     * Toggles the browsability status.
     *
     * @return success
     */
    @SkipValidation
    public String toggle() {
        setMenu("ProjectEditLinks");
        getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return WORKSPACE_RESULT;
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return this.menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }
}
