package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.ActionHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Action class for performing actions on project as a whole (ie create, edit, etc) Actions for individual tabs in
 * editing a project should subclass ProjectTabAction.
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectAction extends AbstractBaseProjectAction {
    private static final long serialVersionUID = 1L;

    private ProposalStatus workflowStatus;

    /**
     * create new project.
     *
     * @return path String
     */
    @SkipValidation
    public String create() {
        setEditMode(true);
        return INPUT;
    }

    /**
     * edit existing project.
     *
     * @return path String
     */
    @SkipValidation
    public String edit() {
        if (getProject().getId() == null) {
            return projectNotFound();
        }
        if (!getProject().hasWritePermission(UsernameHolder.getCsmUser())) {
            return permissionDenied("role.write");
        }
        setEditMode(true);
        return INPUT;
    }

    /**
     * show details for a project.
     *
     * @return path String
     */
    @SkipValidation
    public String details() {
        if (getProject().getId() == null) {
            return projectNotFound();
        }
        if (!getProject().hasReadPermission(UsernameHolder.getCsmUser())) {
            return permissionDenied("role.read");
        }
        setEditMode(false);
        return INPUT;
    }

    /**
     * show browse view for a project.
     * 
     * @return path String
     */
    @SkipValidation
    public String browse() {
        if (getProject().getId() == null) {
            return projectNotFound();
        }
        setEditMode(false);
        return "browse";
    }
    
    /**
     * handles the case where an attempt is made to view/edit a non-existent project.
     * @return the result name
     */
    private String projectNotFound() {
        ActionHelper.saveMessage(getText("project.notFound"));
        return WORKSPACE_RESULT;
    }

    /**
     * handles the case where an attempt is made to view/edit a project to which
     * the user does not have the appropriate permission.
     * @return the result name
     */
    private String permissionDenied(String roleKey) {
        List<String> args = new ArrayList<String>();
        args.add(getText(roleKey));
        ActionHelper.saveMessage(getText("project.permissionDenied", args));
        return WORKSPACE_RESULT;
    }

    /**
     * change the workflow status of a project.
     * 
     * @return path String
     */
    public String changeWorkflowStatus() {
        try {
            getProjectManagementService().changeProjectStatus(getProject().getId(), this.workflowStatus);
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            args.add(getText(this.workflowStatus.getResourceKey()));
            ActionHelper.saveMessage(getText("project.workflowStatusUpdated", args));
            return WORKSPACE_RESULT;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            args.add(getText(this.workflowStatus.getResourceKey()));
            ActionHelper.saveMessage(getText("project.workflowProblem", args));
            return INPUT;
        }
    }

    /**
     * @return the workflowStatus
     */
    public ProposalStatus getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * @param workflowStatus the workflowStatus to set
     */
    public void setWorkflowStatus(ProposalStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}