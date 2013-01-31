//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.search.PersonSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;

/**
 * Action implementing the publications tab.
 *
 * @author Dan Kokotov, Jevon Gill
 */
public class ProjectExperimentContactsAction extends
        AbstractProjectListTabAction {

    private static final long serialVersionUID = 1L;
    private static final String REQUIRED_STRING_KEY = "struts.validator.requiredString";

    private ExperimentContact currentExperimentContact = new ExperimentContact();

    /**
     * Default constructor.
     */
    public ProjectExperimentContactsAction() {
        super("experimentContact",
                new SortablePaginatedList<Person, PersonSortCriterion>(
                        PAGE_SIZE, PersonSortCriterion.FIRST_NAME.name(),
                        PersonSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();

        if (this.currentExperimentContact.getId() != null) {
            ExperimentContact retrieved = getGenericDataService()
                    .getPersistentObject(ExperimentContact.class,
                            this.currentExperimentContact.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(
                        this.currentExperimentContact,
                        SecurityUtils.READ_PRIVILEGE, UsernameHolder.getUser());
            } else {
                this.currentExperimentContact = retrieved;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    /**
     * save a project.
     *
     * @return path String
     */
    @Override
    @SuppressWarnings("PMD")
    @Validations(requiredStrings = {
                   @RequiredStringValidator(fieldName = "currentExperimentContact.person.firstName",
                    key = REQUIRED_STRING_KEY, message = ""),
                   @RequiredStringValidator(fieldName = "currentExperimentContact.person.lastName",
                    key = REQUIRED_STRING_KEY, message = ""),
                   @RequiredStringValidator(fieldName = "currentExperimentContact.person.email",
                    key = REQUIRED_STRING_KEY, message = "") },
                fieldExpressions = { @FieldExpressionValidator(fieldName = "currentExperimentContact.roles",
                    message = "", key = REQUIRED_STRING_KEY, expression = "!currentExperimentContact.roles.isEmpty") },
                    emails = @EmailValidator(fieldName = "currentExperimentContact.person.email",
                    key = "struts.validator.email", message = ""))
    public String save() {
        if (!getCurrentExperimentContact().isPrimaryInvestigator()
                && getProject().getExperiment().getPrimaryInvestigatorCount() < 1) {
            addFieldError(
                    "currentExperimentContact.roles",
                    "Cannot remove investigator role - at least one contact must have the investigator role.");
            setEditMode(true);
            return INPUT;
        }
        return super.save();
    }

    /**
     * delete a contact.
     *
     * @return path String
     */
    @SkipValidation
    @Override
    public String delete() {
        if (getCurrentExperimentContact().isPrimaryInvestigator()
                && getProject().getExperiment().getPrimaryInvestigatorCount() < 2) {
            ActionHelper.saveMessage("Unable to delete contact. At least one Primary Investigator is required.");
            super.load();
            return "list";
        }
        return super.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCopyItem() {
        throw new NotImplementedException("Copying of contacts not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<ExperimentContact> getCollection() {
        return getProject().getExperiment().getExperimentContacts();
    }

    /**
     * @return current experiment contact.
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentExperimentContact();
    }

    /**
     * @return the currentExperimentContact
     */
    @CustomValidator(type = "hibernate")
    public ExperimentContact getCurrentExperimentContact() {
        return currentExperimentContact;
    }
    /**
     * @param currentExperimentContact the current experiment contact
     */
    public void setCurrentExperimentContact(
            ExperimentContact currentExperimentContact) {
        this.currentExperimentContact = currentExperimentContact;
    }
}
