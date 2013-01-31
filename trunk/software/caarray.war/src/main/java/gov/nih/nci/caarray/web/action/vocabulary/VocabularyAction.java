//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.vocabulary;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action class for managing a vocabulary.
 *
 * @author Scott Miller
 */
@Validation
public class VocabularyAction extends ActionSupport implements Preparable {
    private static final long serialVersionUID = 1L;
    private Set<Term> terms = new HashSet<Term>();
    private Term currentTerm;
    private ExperimentOntologyCategory category;;
    private List<TermSource> sources = new ArrayList<TermSource>();
    private Long returnProjectId;
    private boolean returnToProjectOnCompletion = false;
    private String returnInitialTab1 = "overview";
    private String returnInitialTab2;
    private String returnInitialTab2Url;
    private boolean createNewSource = false;
    private TermSource newSource;

    private boolean editMode = false;

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        if (getCurrentTerm() != null && getCurrentTerm().getId() != null) {
            Term retrieved = ServiceLocatorFactory.getVocabularyService().getTerm(getCurrentTerm().getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(getCurrentTerm(),
                        SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
            } else {
                setCurrentTerm(retrieved);
            }
        }
        populateCurrentTerm();
    }

    /**
     *
     */
    private void populateCurrentTerm() {
        if (getCurrentTerm() != null) {
            if (isCreateNewSource()) {
                getCurrentTerm().setSource(getNewSource());
            }
            if (getCurrentTerm().getCategories().isEmpty()) {
                getCurrentTerm().setCategory(retrieveCategory());
            }
        }
    }

    /**
     * Action to list the terms in a given category.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String list() {
        if (Boolean.valueOf((String) ServletActionContext.getRequest().getSession().getAttribute("startWithEdit"))) {
            HttpSession session = ServletActionContext.getRequest().getSession();
            String returnProjectIdString = (String) session.getAttribute("returnProjectId");
            if (StringUtils.isNotBlank(returnProjectIdString)) {
                setReturnProjectId(Long.parseLong(returnProjectIdString));
            } else {
                setReturnProjectId(null);
            }
            setReturnInitialTab1((String) session.getAttribute("returnInitialTab1"));
            setReturnInitialTab2((String) session.getAttribute("returnInitialTab2"));
            setReturnInitialTab2Url((String) session.getAttribute("returnInitialTab2Url"));
            setReturnToProjectOnCompletion(true);
            session.removeAttribute("startWithEdit");
            session.removeAttribute("returnProjectId");
            session.removeAttribute("returnInitialTab1");
            session.removeAttribute("returnInitialTab2");
            session.removeAttribute("returnInitialTab2Url");
            return edit();
        }
        this.setTerms(VocabularyUtils.getTermsFromCategory(this.getCategory()));
        return SUCCESS;
    }

    /**
     * Action to search for terms by value and category.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String searchForTerms() {
        this.setTerms(ServiceLocatorFactory.getVocabularyService().getTerms(
                VocabularyUtils.getCategory(this.getCategory()), getCurrentTerm().getValue()));
        return "termAutoCompleterValues";
    }

    /**
     * Action for loading a term to edit.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String edit() {
        setEditMode(true);
        setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        return INPUT;
    }

    /**
     * Action for loading a term to view.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String details() {
        setEditMode(false);
        setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        return INPUT;
    }

    /**
     * Action that saves the term being edited or created.
     *
     * @return the string indicating which result to follow.
     */
    @Validations(
        urls = {
                @UrlValidator(message = "", fieldName = "currentTerm.accession.url", key = "struts.validator.url"),
                @UrlValidator(message = "", fieldName = "newSource.url", key = "struts.validator.url")
        }
    )
    public String save() {
        ServiceLocatorFactory.getVocabularyService().saveTerm(getCurrentTerm());
        if (getCurrentTerm().getId() == null) {
            ActionHelper.saveMessage(getText("vocabulary.term.created", new String[] {getCurrentTerm().getValue()}));
        } else {
            ActionHelper.saveMessage(getText("vocabulary.term.updated", new String[] {getCurrentTerm().getValue()}));
        }
        if (isReturnToProjectOnCompletion()) {
            return "projectEdit";
        }
        return list();
    }

    /**
     * handles the return to project / cancel action.
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String projectEdit() {
        return "projectEdit";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (hasErrors()) {
            setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        }
    }

    /**
     * @return the category
     */
    public ExperimentOntologyCategory getCategory() {
        return this.category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(ExperimentOntologyCategory category) {
        this.category = category;
    }

    private Category retrieveCategory() {
        return VocabularyUtils.getCategory(getCategory());
    }

    /**
     * @return the terms
     */
    public Set<Term> getTerms() {
        return this.terms;
    }

    /**
     * @param terms the terms to set
     */
    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    /**
     * @return the currentTerm
     */
    @CustomValidator(type = "hibernate")
    public Term getCurrentTerm() {
        return this.currentTerm;
    }

    /**
     * @param currentTerm the currentTerm to set
     */
    public void setCurrentTerm(Term currentTerm) {
        this.currentTerm = currentTerm;
    }

    /**
     * @return the sources
     */
    public List<TermSource> getSources() {
        return this.sources;
    }

    /**
     * @param sources the sources to set
     */
    public void setSources(List<TermSource> sources) {
        this.sources = sources;
    }

    /**
     * @return the returnProjectId
     */
    public Long getReturnProjectId() {
        return this.returnProjectId;
    }

    /**
     * @param returnProjectId the returnProjectId to set
     */
    public void setReturnProjectId(Long returnProjectId) {
        this.returnProjectId = returnProjectId;
    }

    /**
     * @return the returnToProjectOnCompletion
     */
    public boolean isReturnToProjectOnCompletion() {
        return this.returnToProjectOnCompletion;
    }

    /**
     * @param returnToProjectOnCompletion the returnToProjectOnCompletion to set
     */
    public void setReturnToProjectOnCompletion(boolean returnToProjectOnCompletion) {
        this.returnToProjectOnCompletion = returnToProjectOnCompletion;
    }

    /**
     * @return the returnInitialTab1
     */
    public String getReturnInitialTab1() {
        return this.returnInitialTab1;
    }

    /**
     * @param returnInitialTab1 the returnInitialTab1 to set
     */
    public void setReturnInitialTab1(String returnInitialTab1) {
        this.returnInitialTab1 = returnInitialTab1;
    }

    /**
     * @return the returnInitialTab2
     */
    public String getReturnInitialTab2() {
        return this.returnInitialTab2;
    }

    /**
     * @param returnInitialTab2 the returnInitialTab2 to set
     */
    public void setReturnInitialTab2(String returnInitialTab2) {
        this.returnInitialTab2 = returnInitialTab2;
    }

    /**
     * @return the returnInitialTab2Url
     */
    public String getReturnInitialTab2Url() {
        return this.returnInitialTab2Url;
    }

    /**
     * @param returnInitialTab2Url the returnInitialTab2Url to set
     */
    public void setReturnInitialTab2Url(String returnInitialTab2Url) {
        this.returnInitialTab2Url = returnInitialTab2Url;
    }

    /**
     * @return the createNewSource
     */
    public boolean isCreateNewSource() {
        return this.createNewSource;
    }

    /**
     * @param createNewSource the createNewSource to set
     */
    public void setCreateNewSource(boolean createNewSource) {
        this.createNewSource = createNewSource;
    }

    /**
     * @return the newSource
     */
    @CustomValidator(type = "hibernate",
            parameters = @ValidationParameter(name = "conditionalExpression", value = "createNewSource == true"))
    public TermSource getNewSource() {
        return this.newSource;
    }

    /**
     * @param newSource the newSource to set
     */
    public void setNewSource(TermSource newSource) {
        this.newSource = newSource;
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return this.editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
