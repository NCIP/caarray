//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Action implementing the samples tab.
 * @author Dan Kokotov
 */
@Validation
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProjectSamplesAction extends AbstractProjectAssociatedAnnotationsListTabAction<Source> {
    private static final long serialVersionUID = 1L;

    private Sample currentSample = new Sample();
    private List<Source> itemsToAssociate = new ArrayList<Source>();
    private List<Source> itemsToRemove = new ArrayList<Source>();


    /**
     * Default constructor.
     */
    public ProjectSamplesAction() {
        super("sample", "source", new SortablePaginatedList<Sample, SampleSortCriterion>(PAGE_SIZE,
                SampleSortCriterion.NAME.name(), SampleSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     * @throws VocabularyServiceException
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();
        Sample retrieved = null;
        if (this.currentSample.getId() != null) {
            retrieved = getGenericDataService().getPersistentObject(Sample.class, this.currentSample.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.currentSample,
                        SecurityUtils.READ_PRIVILEGE, UsernameHolder.getUser());
            }
        } else if (!isEditMode() && this.currentSample.getExternalSampleId() != null) {
            retrieved = getProjectManagementService().getSampleByExternalId(getProject(),
                    this.currentSample.getExternalSampleId());
            if (retrieved == null) {
                throw new SampleExternalIdPermissionDeniedException(this.currentSample,
                        SecurityUtils.READ_PRIVILEGE, UsernameHolder.getUser());
            }
        }

        if (retrieved != null) {
            this.currentSample = retrieved;
        }
    }

    /**
     * A custom exception to show a message specific to searching by Sample.externalSampleId.
     */
    public class SampleExternalIdPermissionDeniedException extends PermissionDeniedException {

        private static final long serialVersionUID = -1271512112927323609L;

        /**
         * Create a new SampleExternalIdPermissionDeniedException for a given user not having the given
         * privilege to the given entity.
         *
         * @param s the sample on which an operation was requested
         * @param privilege the privilege that was needed to perform the operation
         * @param userName the user attempting the operation
         */
        public SampleExternalIdPermissionDeniedException(Sample s, String privilege, String userName) {
            super(s, privilege, userName, String.format(
                    "User %s does not have privilege %s for entity of type %s with externalSampleId %s", userName,
                    privilege, s.getClass().getName(), s.getExternalSampleId()));
        }
    }

    /**
     * download all of the data for this sample.
     * @return download
     * @throws IOException on file error
     */
    @SkipValidation
    public String download() throws IOException {
        Collection<CaArrayFile> files = getAllDataFiles();
        if (files.isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.samples.noDataToDownload"));
            return "noSampleData";
        }
        setDownloadFileGroups(ProjectFilesAction.computeDownloadGroups(files));
        return ProjectFilesAction.downloadByGroup(getProject(), files,
                getDownloadGroupNumber(), getDownloadFileGroups());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException {
        getProjectManagementService().copySample(getProject(), this.currentSample.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Sample> getCollection() {
        return getProject().getExperiment().getSamples();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentSample();
    }

    /**
     * @return the currentSample
     */
    @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase",
            value = "experiment.samples"))
    public Sample getCurrentSample() {
        return this.currentSample;
    }

    /**
     * @param currentSample the currentSample to set
     */
    public void setCurrentSample(Sample currentSample) {
        this.currentSample = currentSample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Source> getPossibleAssociationsCollection() {
        return getExperiment().getSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Source> getCurrentAssociationsCollection() {
        return getCurrentSample().getSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection getAnnotationCollectionToUpdate(Source item) {
        return item.getSamples();
    }

    /**
     * @return the itemsToAssociate
     */
    @Override
    public List<Source> getItemsToAssociate() {
        return this.itemsToAssociate;
    }

    /**
     * @param itemsToAssociate the itemsToAssociate to set
     */
    public void setItemsToAssociate(List<Source> itemsToAssociate) {
        this.itemsToAssociate = itemsToAssociate;
    }

    /**
     * @return the itemsToRemove
     */
    @Override
    public List<Source> getItemsToRemove() {
        return this.itemsToRemove;
    }

    /**
     * @param itemsToRemove the itemsToRemove to set
     */
    public void setItemsToRemove(List<Source> itemsToRemove) {
        this.itemsToRemove = itemsToRemove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleDelete() {
        if (!getCurrentSample().getExtracts().isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.annotations.cantdelete",
                    new String[] {getText("experiment.sample"),
                                  getText("experiment.extract") }));
            return false;
        }
        for (Source s : getCurrentAssociationsCollection()) {
            s.getSamples().remove(getCurrentSample());
        }
        // clear the sample from any access profiles
        // this is perhaps not the ideal place for this - would be preferrable in the business layer
        for (AccessProfile ap : getCurrentSample().getExperiment().getProject().getAllAccessProfiles()) {
            ap.getSampleSecurityLevels().remove(getCurrentSample());
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @SkipValidation
    @Override
    public String view() {
        downloadFiles();
        return super.view();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String edit() {
        downloadFiles();
        return super.edit();
    }

    /**
     * {@inheritDoc}
     */
    protected Collection<CaArrayFile> getAllDataFiles() {
        return getCurrentSample().getAllDataFiles();
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFileListAction() {
        return getDownloadFileListActionUrl(BioMaterialTypes.SAMPLES);
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFilesTableListSortUrlAction() {
        return getDownloadFileTableListSortActionUrl(BioMaterialTypes.SAMPLES);
    }
}
