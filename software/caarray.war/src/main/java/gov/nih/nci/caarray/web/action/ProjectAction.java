package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectAction.
 * @author John Hedden
 *
 */
public class ProjectAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private Project project = Project.createNew();
    private List<Project> projects = new ArrayList<Project>();
    private Proposal proposal = Proposal.createNew();
    private String menu = null;

    /**
     * create new project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String list() throws Exception {
        setMenu("ProjectListLinks");
        setProjects(getDelegate().getProjectManagementService().getWorkspaceProjects());

        return SUCCESS;
    }

    /**
     * edit files associated with a project.
     * @return path String
     * @throws Exception Exception
     */
    public String edit() throws Exception {
        setMenu("ProjectEditLinks");
        setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
        return SUCCESS;
    }

    /**
     * create new project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String create() throws Exception {
        setMenu("ProjectCreateLinks");
        setProposal(Proposal.createNew());

        return SUCCESS;
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String save() throws Exception {
        setMenu("ProjectSaveLinks");
        getDelegate().getProjectManagementService().submitProposal(getProposal());
        List<String> args = new ArrayList<String>();
        args.add(getProposal().getProject().getExperiment().getTitle());
        saveMessage(getText("project.created", args));

        return SUCCESS;
    }

    /**
     * Toggles the browsability status.
     * @return success
     * @exception Exception on error
     */
    public String toggle() throws Exception {
        setMenu("ProjectEditLinks");
        getDelegate().getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return SUCCESS;
    }

    /**
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     */
    public ProjectDelegate getDelegate() {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the proposal
     */
    public Proposal getProposal() {
        return proposal;
    }

    /**
     * @param proposal the proposal to set
     */
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
