package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.ActionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Base Action for implementing a single tab of the Project management ui.
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectTabAction extends AbstractBaseProjectAction {
    private static final long serialVersionUID = 1L;
    private final List<PersistentObject> orphans = new ArrayList<PersistentObject>();;

    /**
     * load a given tab in the submit experiment workflow.
     *
     * @return name of result to forward to
     */
    @SkipValidation
    public String load() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        return INPUT;
    }

    /**
     * save a tab and possibly submit the project.
     *
     * @return path String
     */
    public String save() {
        boolean initialSave = getProject().getId() == null;
        if (initialSave && getProject().getExperiment().getPrimaryInvestigator() == null) {
            // make sure PI is set so that the experiment has a public ID - assume PI is user
            Term piRole = ActionHelper.getMOTerm(ExperimentContact.PI_ROLE);
            Term mainPocRole = ActionHelper.getMOTerm(ExperimentContact.MAIN_POC_ROLE);
            ExperimentContact pi =
                    new ExperimentContact(getExperiment(), new Person(UsernameHolder.getCsmUser()), Arrays.asList(
                            piRole, mainPocRole));
            getExperiment().getExperimentContacts().add(pi);
        }
        try {
            getProjectManagementService().saveProject(getProject(),
                    this.orphans.toArray(new PersistentObject[this.orphans.size()]));
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText("project.saved", args));
            setEditMode(true);
            return initialSave ? RELOAD_PROJECT_RESULT : SUCCESS;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText("project.saveProblem", args));
            return INPUT;
        }
    }

    /**
     * Helper method for checking whether the project was correctly loaded and has at least
     * read rights. if not, returns the result name that the action should redirect to; otherwise returns
     * null. Action methods should call this helper method first; if it returns a non-null result,
     * they should in turn return this result immediately, otherwise they can proceed with their work
     * 
     * @return a result name in case the project was not found or the current user does not have read permissions
     * for it. null otherwise.
     */
    protected String checkProject() {
        if (!getProject().hasReadPermission(getCsmUser())) {
            return permissionDenied("role.read");
        }
        return null;
    }
    
    /**
     * handles the case where an attempt is made to view/edit a project to which
     * the user does not have the appropriate permission.
     * @param roleKey key for the resource string for the name of the permission being checked
     * @return the result name
     */
    protected String permissionDenied(String roleKey) {
        if (UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            return "reload-project";
        } else {
            List<String> args = new ArrayList<String>();
            args.add(getText(roleKey));
            ActionHelper.saveMessage(getText("project.permissionDenied", args));
            return WORKSPACE_RESULT;
        }
    }

    /**
     * @param orphan object orphaned during this operation
     */
    public void addOrphan(PersistentObject orphan) {
        this.orphans.add(orphan);
    }
}
