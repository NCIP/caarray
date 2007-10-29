package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.web.action.ActionHelper;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Base Action for implementing a single tab of the Project management ui
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectTabAction extends BaseProjectAction {
    private static final long serialVersionUID = 1L;

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String load() {
        return INPUT;
    }

    /**
     * save a tab and possibly submit the project.
     *
     * @return path String
     * @throws Exception Exception
     */
    public String save() {
        String result = SUCCESS;
        if (SAVE_MODE_DRAFT.equals(getSaveMode()) || SAVE_MODE_SUBMIT.equals(getSaveMode())) {
            try {
                if (SAVE_MODE_DRAFT.equals(getSaveMode())) {
                    setInitialSave(getProject().getId() == null);
                    getProjectManagementService().saveDraftProject((getProject()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProject().getExperiment().getTitle());
                    ActionHelper.saveMessage(getText("project.saved", args));
                } else {
                    getProjectManagementService().submitProject((getProject()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProject().getExperiment().getTitle());
                    ActionHelper.saveMessage(getText("project.submitted", args));
                    result = WORKSPACE_RESULT;
                }
            } catch (ProposalWorkflowException e) {
                List<String> args = new ArrayList<String>();
                args.add(e.getMessage());
                ActionHelper.saveMessage(getText("project.workflowProblem", args));
            }
        } else {
            ActionHelper.saveMessage(getText("project.updated"));
        }
        return result;
    }
}
