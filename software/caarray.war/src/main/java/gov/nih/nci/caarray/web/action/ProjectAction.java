package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.caarray.web.exception.CaArrayException;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * ProjectAction.
 * @author John Hedden
 *
 */
public class ProjectAction extends BaseAction {

    private static final long serialVersionUID = 1L;
    private Proposal proposal;
    private String projectName = null;

    /**
     * list all projects.
     * @return path String
     * @throws Exception Exception
     */
    public String list() throws Exception {
        HttpServletRequest request = getRequest();
        request.setAttribute("contentLabel", "Experiment Workspace");

        LabelValue labelValue = new LabelValue("Propose Project", "createProject.action");
        request.setAttribute("labelValue", labelValue);

        List<Project> projects = getDelegate().getProjectManagementService().getWorkspaceProjects();
        request.setAttribute("projects", projects);

        return SUCCESS;
    }

    /**
     * create new project.
     * @return path String
     * @throws Exception Exception
     */
    public String create() throws Exception {
        HttpServletRequest request = getRequest();
        request.setAttribute("contentLabel", "Propose Experiment");

        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("labelValue", labelValue);

        return SUCCESS;
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    public String save() throws Exception {
        proposal = Proposal.createNew();
        proposal.getProject().getExperiment().setTitle(getProjectName());
        getDelegate().getProjectManagementService().submitProposal(proposal);
        List<String> args = new ArrayList<String>();
        args.add(getProjectName());
        saveMessage(getText("project.created", args));

        return SUCCESS;
    }

    /**
     * edit a project.
     * @return path String
     * @throws Exception Exception
     */
    public String edit() throws Exception {
        HttpServletRequest request = getRequest();
        request.setAttribute("projectName", getProjectName());

        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("labelValue", labelValue);

        LabelValue manageFiles = new LabelValue("Manage Files", "manageFiles.action");
        request.setAttribute("manageFiles", manageFiles);

        return SUCCESS;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     * @throws CaArrayException
     */
    private ProjectDelegate getDelegate() throws CaArrayException {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }

}
