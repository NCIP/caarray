package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.caarray.web.exception.CaArrayException;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * ProjectAction.
 * @author John Hedden
 *
 */
@Validation
public class ProjectAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private List<Project> projects;
    private Proposal proposal;
    private List<LabelValue> navigationList;

    /**
     * list all projects.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String list() throws Exception {
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue labelValue = new LabelValue("Propose Project", "createProject.action");
        navigationList.add(labelValue);

        HttpSession session = getSession();
        setProjects(getDelegate().getProjectManagementService().getWorkspaceProjects());
        session.setAttribute("myProjects", getProjects());

        return SUCCESS;
    }

    /**
     * create new project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String create() throws Exception {
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        navigationList.add(labelValue);

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
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        navigationList.add(labelValue);

        if (getProposal().getProject().getExperiment().getTitle().length() > 0
                && getProposal().getProject().getExperiment().getTitle() != null) {
            getDelegate().getProjectManagementService().submitProposal(getProposal());
            List<String> args = new ArrayList<String>();
            args.add(getProposal().getProject().getExperiment().getTitle());
            saveMessage(getText("project.created", args));
        } else {
            addActionError(getText("projectNameRequired"));
            return INPUT;
        }
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
     * @return the navigationList
     */
    public List<LabelValue> getNavigationList() {
        return navigationList;
    }

    /**
     * @param navigationList the navigationList to set
     */
    public void setNavigationList(List<LabelValue> navigationList) {
        this.navigationList = navigationList;
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
