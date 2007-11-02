package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getCurrentUser;
import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getVocabularyService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.web.action.ActionHelper;

import java.util.ArrayList;
import java.util.Arrays;
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
        boolean initialSave = getProject().getId() == null;
        setInitialSave(initialSave);
        if (initialSave && getProject().getExperiment().getPrimaryInvestigator() == null) {
            // make sure PI is set so that the experiment has a public ID
            // assume PI is user
            VocabularyService vocabService = getVocabularyService();
            TermSource mged = vocabService.getSource(ExperimentOntology.MGED.getOntologyName());
            Category roleCat = vocabService.getCategory(mged, ExperimentOntologyCategory.ROLES.getCategoryName());
            Term piRole = vocabService.getTerm(mged, roleCat, ExperimentContact.PI_ROLE);
            Term mainPocRole = vocabService.getTerm(mged, roleCat, ExperimentContact.MAIN_POC_ROLE);

            ExperimentContact pi = new ExperimentContact(getExperiment(), new Person(getCurrentUser()), Arrays.asList(piRole, mainPocRole));
            getExperiment().getExperimentContacts().add(pi);
        }
        if (SAVE_MODE_DRAFT.equals(getSaveMode()) || SAVE_MODE_SUBMIT.equals(getSaveMode())) {
            try {
                if (SAVE_MODE_DRAFT.equals(getSaveMode())) {
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
