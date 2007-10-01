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

    private List<Project> projects;
    private Proposal proposal;
    private String menu;

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
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     */
    public ProjectDelegate getDelegate() {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }
}
