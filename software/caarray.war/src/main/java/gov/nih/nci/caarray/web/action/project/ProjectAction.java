//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.helper.EmailHelper;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
            ProposalStatus oldStatus = getProject().getStatus();
            getProjectManagementService().changeProjectStatus(getProject().getId(), this.workflowStatus);
            List<String> args = new ArrayList<String>();
            args.add(StringUtils.abbreviate(getProject().getExperiment().getTitle(), TRUNCATED_TITLE_WIDTH));
            args.add(getText(this.workflowStatus.getResourceKey()));
            if (oldStatus == ProposalStatus.PUBLIC) {
                args.add(getText("project.workflowStatusUpdated.retractPublic"));
            } else {
                args.add("");
            }
            ActionHelper.saveMessage(getText("project.workflowStatusUpdated", args));
            if (oldStatus == ProposalStatus.DRAFT && this.workflowStatus == ProposalStatus.IN_PROGRESS) {
                sendSubmitExperimentEmail();
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
            args.add(getText(ProposalStatus.DRAFT.getResourceKey()));
            ActionHelper.saveMessage(getText("project.deleteOnlyDrafts", args));
        }
        return WORKSPACE_RESULT;
    }

    /**
     * @throws MessagingException
     *
     */
    private void sendSubmitExperimentEmail() throws MessagingException {
        HttpServletRequest request = ServletActionContext.getRequest();
        String ctxPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String fullUrl = request.getRequestURL().toString();
        String projectLink = getProjectDetailsLink(StringUtils.substringBefore(fullUrl, requestUri) + ctxPath,
                getProject().getExperiment().getPublicIdentifier());
        EmailHelper.sendSubmitExperimentEmail(getProject(), projectLink);
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
