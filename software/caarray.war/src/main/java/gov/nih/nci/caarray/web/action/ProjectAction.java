package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.caarray.web.exception.CaArrayException;
import gov.nih.nci.caarray.web.helper.FileEntry;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ProjectAction.
 * @author John Hedden
 *
 */
public class ProjectAction extends BaseAction {

    private static final long serialVersionUID = 1L;
    private Proposal proposal;
    private Project project;
    private List<FileEntry> fileEntries;
    private String projectName = null;

    /**
     * list all projects.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String list() throws Exception {
        HttpServletRequest request = getRequest();
        request.setAttribute("contentLabel", "Experiment Workspace");

        LabelValue labelValue = new LabelValue("Propose Project", "createProject.action");
        request.setAttribute("proproseProject", labelValue);

        List<Project> projects = getDelegate().getProjectManagementService().getWorkspaceProjects();
        //did this b/c using projects throughout.
        HttpSession session = getSession();
        session.setAttribute("projects", projects);

        return SUCCESS;
    }

    /**
     * create new project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String create() throws Exception {
        HttpServletRequest request = getRequest();
        request.setAttribute("contentLabel", "Propose Experiment");

        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);

        return SUCCESS;
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String save() throws Exception {
        proposal = Proposal.createNew();
        proposal.getProject().getExperiment().setTitle(getProjectName());
        getDelegate().getProjectManagementService().submitProposal(proposal);
        List<String> args = new ArrayList<String>();
        args.add(getProjectName());
        saveMessage(getText("project.created", args));

        HttpSession session = getSession();
        session.setAttribute("projectName", getProjectName());

        return SUCCESS;
    }

    /**
     * edit a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String edit() throws Exception {
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        String myProjectName = (String) request.getAttribute("projectName");
        session.setAttribute("projectName", myProjectName);

        List<Project> projects = (List<Project>) session.getAttribute("projects");
        String myProject = (String) session.getAttribute("projectName");

        for (Project projectList : projects) {
            if (projectList.getExperiment().getTitle().equalsIgnoreCase(myProject))
            {
                project = projectList;
            }
        }

        session.setAttribute("project", project);

        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);

        LabelValue manageFiles = new LabelValue("Manage Files", "manageFiles.action");
        request.setAttribute("manageFiles", manageFiles);

        loadFileEntries();

        return SUCCESS;
    }

    private void loadFileEntries() {
        fileEntries = new ArrayList<FileEntry>(project.getFiles().size());
        for (CaArrayFile nextCaArrayFile : project.getFilesList()) {
            fileEntries.add(new FileEntry(nextCaArrayFile));
        }
        HttpSession session = getSession();
        session.setAttribute("fileEntries", fileEntries);
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
