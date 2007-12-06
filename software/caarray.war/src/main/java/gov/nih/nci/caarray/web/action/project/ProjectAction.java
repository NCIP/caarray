package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.ActionHelper;
import gov.nih.nci.caarray.web.helper.EmailHelper;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

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
        if (!getProject().hasWritePermission(getCsmUser())) {
            return permissionDenied("role.write");
        }
        setEditMode(true);
        return INPUT;
    }

    /**
     * get the csm user.  This method is extracted so it can be overwritten in test cases.
     * @return the csm user.
     */
    protected User getCsmUser() {
        return UsernameHolder.getCsmUser();
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
            ProposalStatus oldStatus = getProject().getStatus();
            getProjectManagementService().changeProjectStatus(getProject().getId(), this.workflowStatus);
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            args.add(getText(this.workflowStatus.getResourceKey()));
            ActionHelper.saveMessage(getText("project.workflowStatusUpdated", args));
            if (oldStatus == ProposalStatus.DRAFT && this.workflowStatus == ProposalStatus.IN_PROGRESS) {
                EmailHelper.sendSubmitExperimentEmail(getProject());
            }
            return WORKSPACE_RESULT;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            args.add(getText(this.workflowStatus.getResourceKey()));
            ActionHelper.saveMessage(getText("project.workflowProblem", args));
            return INPUT;
        } catch (MessagingException e) {
            LOG.warn("Could not send email for experiment submission", e);
            return WORKSPACE_RESULT;
        }
    }

    /**
     * @return the workflowStatus
     */
    public ProposalStatus getWorkflowStatus() {
        return this.workflowStatus;
    }

    /**
     * @param workflowStatus the workflowStatus to set
     */
    public void setWorkflowStatus(ProposalStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}