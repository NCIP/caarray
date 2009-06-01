package gov.nih.nci.caarray.web.action.project;

import java.util.Collections;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.views.util.UrlHelper;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
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

    private boolean workflowStatus;

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
        if (!getProject().hasWritePermission(getCsmUser())) {
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
        if (!getProject().hasReadPermission(getCsmUser())) {
            return permissionDenied("role.read");
        }
        setEditMode(false);
        return INPUT;
    }

    /**
     * handles the case where an attempt is made to view/edit a non-existent project.
     * @return the result name
     */
    private String projectNotFound() {
        if (UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            return loggedinDetails();
        } else {
            ActionHelper.saveMessage(getText("project.notFound"));
            return WORKSPACE_RESULT;
        }
    }

    /**
     * handles the case where an attempt is made to view/edit a project to which
     * the user does not have the appropriate permission.
     * @return the result name
     */
    private String permissionDenied(String roleKey) {
        if (UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            return loggedinDetails();
        } else {
            List<String> args = new ArrayList<String>();
            args.add(getText(roleKey));
            ActionHelper.saveMessage(getText("project.permissionDenied", args));
            return WORKSPACE_RESULT;
        }
    }

    private String loggedinDetails() {
        if (getProject().getId() != null) {
            return "login-details-id";
        } else if (getProject().getExperiment().getPublicIdentifier() != null) {
            return "login-details-publicid";
        } else {
            return WORKSPACE_RESULT;
        }

    }

    /**
     * @return a dynamic action result which includes all request parameters from the originating request
     */
    public String getRequestParameters() {
        StringBuffer link = new StringBuffer();
        UrlHelper.buildParametersString(ServletActionContext.getRequest().getParameterMap(), link, "&");
        return link.toString();
    }
    
    /**
     * change the workflow status of a project.
     *
     * @return path String
     */
    public String changeWorkflowStatus() {
        try {
            getProjectManagementService().changeProjectLockStatus(getProject().getId(), workflowStatus);
            String msgKey = "project.workflowStatusUpdated." + (getProject().islocked() ? "locked" : "unlocked");
            List<String> arg = Collections.singletonList(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText(msgKey, arg));
            return WORKSPACE_RESULT;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            args.add(workflowStatus ? "Locked" : "In Progress");
            ActionHelper.saveMessage(getText("project.workflowProblem", args));
            return INPUT;
        }
    }

    /**
     * Delete a project.
     * @return path String
     */
    public String delete() {
        if (getProject().getId() == null) {
            return projectNotFound();
        }
        try {
            getProjectManagementService().deleteProject(getProject());
            ActionHelper.saveMessage(getText("project.deleted"));
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add("Unlocked");
            ActionHelper.saveMessage(getText("project.deleteOnlyDrafts", args));
        }
        return WORKSPACE_RESULT;
    }

    /**
     * Returns the view details link for a project with given id.
     * @param urlBase the URL Base - this should include everything up to and including the context path, e.g.
     * http://array.dev.nih.gov/carray
     * @param projectPublicId the public id of the project to link to
     * @return the link URL
     */
    public static String getProjectDetailsLink(String urlBase, String projectPublicId) {
        return urlBase + "/project/" + projectPublicId;
    }

    /**
     * @return the lock workflowStatus
     */
    public boolean isWorkflowStatus() {
        return this.workflowStatus;
    }

    /**
     * @param workflowStatus the lock workflowStatus to set
     */
    public void setWorkflowStatus(boolean workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}
