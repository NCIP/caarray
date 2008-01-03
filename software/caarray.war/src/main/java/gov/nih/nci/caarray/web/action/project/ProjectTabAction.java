package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getVocabularyService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
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
            VocabularyService vocabService = getVocabularyService();
            TermSource mged = vocabService.getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
            Category roleCat = vocabService.getCategory(mged, ExperimentOntologyCategory.ROLES.getCategoryName());
            Term piRole = vocabService.getTerm(mged, roleCat, ExperimentContact.PI_ROLE);
            Term mainPocRole = vocabService.getTerm(mged, roleCat, ExperimentContact.MAIN_POC_ROLE);

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
            return initialSave ? "initial-save" : SUCCESS;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText("project.saveProblem", args));
            return INPUT;
        }
    }

    /**
     * @param orphan object orphaned during this operation
     */
    public void addOrphan(PersistentObject orphan) {
        this.orphans.add(orphan);
    }
}
