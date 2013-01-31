//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.search.FactorSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Action implementing the factors tab.
 *
 * @author Dan Kokotov
 */
public class ProjectFactorsAction extends AbstractProjectListTabAction {
    private static final long serialVersionUID = 1L;

    private Factor currentFactor = new Factor();
    private Set<Term> categories = new HashSet<Term>();

    /**
     * Default constructor.
     */
    public ProjectFactorsAction() {
        super("factor", new SortablePaginatedList<Factor, FactorSortCriterion>(PAGE_SIZE, FactorSortCriterion.NAME
                .name(), FactorSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();

        if (this.currentFactor.getId() != null) {
            Factor retrieved = ServiceLocatorFactory.getGenericDataService().getPersistentObject(Factor.class,
                    this.currentFactor.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.currentFactor, SecurityUtils.READ_PRIVILEGE,
                        UsernameHolder.getUser());
            } else {
                this.currentFactor = retrieved;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String view() {
        setCategories(VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY));
        return super.view();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String edit() {
        setCategories(VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY));
        return super.edit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (hasErrors()) {
            setCategories(
                    VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ProposalWorkflowException
     */
    @Override
    protected void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException {
        ServiceLocatorFactory.getProjectManagementService().copyFactor(getProject(), this.currentFactor.getId());
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    @Override
    public String delete() {
        // clean up factor value associations
        for (AbstractFactorValue fv : getCurrentFactor().getFactorValues()) {
            fv.getHybridization().getFactorValues().remove(fv);
        }
        return super.delete();
    }

    /**
     * Ajax-only call to handle sorting.
     *
     * @return factorValuesList
     */
    @SkipValidation
    public String factorValuesList() {
        return "factorValuesList";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Factor> getCollection() {
        return getProject().getExperiment().getFactors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentFactor();
    }

    /**
     * @return the currentFactor
     */
    @CustomValidator(type = "hibernate", parameters =
        @ValidationParameter(name = "resourceKeyBase", value = "experiment.factors"))
    public Factor getCurrentFactor() {
        return this.currentFactor;
    }

    /**
     * @param currentFactor the currentFactor to set
     */
    public void setCurrentFactor(Factor currentFactor) {
        this.currentFactor = currentFactor;
    }

    /**
     * @return the categories
     */
    public Set<Term> getCategories() {
        return this.categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(Set<Term> categories) {
        this.categories = categories;
    }
}
