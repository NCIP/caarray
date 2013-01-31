//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getTermsFromCategory;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.search.PublicationSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;

/**
 * Action implementing the publications tab.
 *
 * @author Dan Kokotov
 */
public class ProjectPublicationsAction extends AbstractProjectListTabAction {
    private static final long serialVersionUID = 1L;

    private Publication currentPublication = new Publication();

    private Set<Term> publicationTypes;
    private Set<Term> publicationStatuses;

    /**
     * Default constructor.
     */
    public ProjectPublicationsAction() {
        super("publication", new SortablePaginatedList<Publication, PublicationSortCriterion>(PAGE_SIZE,
                PublicationSortCriterion.TITLE.name(), PublicationSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();

        if (this.currentPublication.getId() != null) {
            Publication retrieved = getGenericDataService().getPersistentObject(Publication.class,
                    this.currentPublication.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.currentPublication,
                        SecurityUtils.READ_PRIVILEGE, UsernameHolder.getUser());
            } else {
                this.currentPublication = retrieved;
            }
        }

        this.publicationTypes = getTermsFromCategory(ExperimentOntologyCategory.PUBLICATION_TYPE);
        this.publicationStatuses = getTermsFromCategory(ExperimentOntologyCategory.PUBLICATION_STATUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @UrlValidator(message = "", fieldName = "currentPublication.uri", key = "struts.validator.url")
    @RequiredFieldValidator(message = "", fieldName = "currentPublication.title",
            key = "struts.validator.requiredString")
    public String save() { //NOPMD
        return super.save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCopyItem() {
        throw new NotImplementedException("Copying of publications not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Publication> getCollection() {
        return getProject().getExperiment().getPublications();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentPublication();
    }

    /**
     * @return the currentPublication
     */
    @CustomValidator(type = "hibernate")
    public Publication getCurrentPublication() {
        return this.currentPublication;
    }

    /**
     * @param currentPublication the currentPublication to set
     */
    public void setCurrentPublication(Publication currentPublication) {
        this.currentPublication = currentPublication;
    }

    /**
     * @return the publicationTypes
     */
    public Set<Term> getPublicationTypes() {
        return this.publicationTypes;
    }

    /**
     * @param publicationTypes the publicationTypes to set
     */
    public void setPublicationTypes(Set<Term> publicationTypes) {
        this.publicationTypes = publicationTypes;
    }

    /**
     * @return the publicationStatuses
     */
    public Set<Term> getPublicationStatuses() {
        return this.publicationStatuses;
    }

    /**
     * @param publicationStatuses the publicationStatuses to set
     */
    public void setPublicationStatuses(Set<Term> publicationStatuses) {
        this.publicationStatuses = publicationStatuses;
    }
}
