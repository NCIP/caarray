//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * Base Action for implementing a single tab of the Project management ui.
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
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
            Term piRole = VocabularyUtils.getMOTerm(ExperimentContact.PI_ROLE);
            Term mainPocRole = VocabularyUtils.getMOTerm(ExperimentContact.MAIN_POC_ROLE);
            ExperimentContact pi =
                    new ExperimentContact(getExperiment(), new Person(CaArrayUsernameHolder.getCsmUser()),
                            Arrays.asList(piRole, mainPocRole));
            getExperiment().getExperimentContacts().add(pi);
        }
        try {
            ServiceLocatorFactory.getProjectManagementService().saveProject(getProject(),
                    this.orphans.toArray(new PersistentObject[this.orphans.size()]));
            ActionHelper.saveMessage(getText("project.saved"));
            setEditMode(true);
            return initialSave ? RELOAD_PROJECT_RESULT : SUCCESS;
        } catch (ProposalWorkflowException e) {
            handleWorkflowError();
            return INPUT;
        } catch (InconsistentProjectStateException e) {
            handleInconsistentStateError(e);
            return INPUT;
        } catch (Exception e) {
            handleSaveError(e);
            return "saveError";
        }
    }

    /**
     * Helper method for creating and saving appropriate error message when a
     * write operation on a project causes a inconsistent state exception.
     * @param e the exception containing information about the inconsistency
     */
    protected void handleInconsistentStateError(InconsistentProjectStateException e) {
        List<String> args = new ArrayList<String>();
        args.add(StringUtils.abbreviate(getProject().getExperiment().getTitle(), TRUNCATED_TITLE_WIDTH));
        if (e.getReason() == Reason.INCONSISTENT_ARRAY_DESIGNS) {
            addFieldError("project.experiment.arrayDesigns", getText("project.inconsistentState."
                    + e.getReason().name().toLowerCase(), new String[] {StringUtils.join(e.getArguments(), ", ") }));
        } else if (e.getReason() == Reason.ARRAY_DESIGNS_DONT_MATCH_MANUF_OR_TYPE) {
            addFieldError("project.experiment.arrayDesigns", getText("project.inconsistentState."
                    + e.getReason().name().toLowerCase()));

        } else {
            addActionError(getText("project.inconsistentState." + e.getReason().name().toLowerCase()));
        }
    }

    /**
     * Helper method for creating and saving appropriate error message when a
     * write operation on a project causes a workflow exception.
     */
    protected void handleWorkflowError() {
        ActionHelper.saveMessage(getText("project.saveProblem", new String[] {StringUtils.abbreviate(getProject()
                .getExperiment().getTitle(), TRUNCATED_TITLE_WIDTH) }));
    }
    
    /**
     * Helper method for creating and saving appropriate error message when experiment creation fails due to any reason 
     * other than an InconsistentStateError and/or a WorkflowError.
     * @param e the exception containing information about the error.
     */
    protected void handleSaveError(Exception e) {
        ActionHelper.saveMessage(getText("project.saveError", new String[] {StringUtils.abbreviate(getProject()
                .getExperiment().getTitle(), TRUNCATED_TITLE_WIDTH) }));
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
        if (CaArrayUsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            return ProjectTabAction.RELOAD_PROJECT_RESULT;
        } else {
            ActionHelper.saveMessage(getText("project.permissionDenied", new String[] {getText(roleKey) }));
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
