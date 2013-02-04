//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.search.LabeledExtractSortCriterion;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Action implementing the samples tab.
 * @author Dan Kokotov
 */
public class ProjectLabeledExtractsAction extends AbstractProjectAssociatedAnnotationsListTabAction<Extract> {
    private static final long serialVersionUID = 1L;

    private LabeledExtract currentLabeledExtract = new LabeledExtract();
    private List<Extract> itemsToAssociate = new ArrayList<Extract>();
    private List<Extract> itemsToRemove = new ArrayList<Extract>();

    /**
     * Default constructor.
     */
    public ProjectLabeledExtractsAction() {
        super("labeledExtract", "extract", new SortablePaginatedList<LabeledExtract, LabeledExtractSortCriterion>(
                PAGE_SIZE, LabeledExtractSortCriterion.NAME.name(), LabeledExtractSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();
        this.currentLabeledExtract = retrieveByIdOrExternalId(LabeledExtract.class, this.currentLabeledExtract);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException {
        ServiceLocatorFactory.getProjectManagementService().copyLabeledExtract(getProject(),
                this.currentLabeledExtract.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<LabeledExtract> getCollection() {
        return getProject().getExperiment().getLabeledExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentLabeledExtract();
    }

    /**
     * @return the currentLabeledExtract
     */
    @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase",
            value = "experiment.labeledExtracts"))
    public LabeledExtract getCurrentLabeledExtract() {
        return this.currentLabeledExtract;
    }

    /**
     * @param currentLabeledExtract the currentLabeledExtract to set
     */
    public void setCurrentLabeledExtract(LabeledExtract currentLabeledExtract) {
        this.currentLabeledExtract = currentLabeledExtract;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection getAnnotationCollectionToUpdate(Extract item) {
        return item.getLabeledExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Extract> getCurrentAssociationsCollection() {
        return getCurrentLabeledExtract().getExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Extract> getPossibleAssociationsCollection() {
        return getExperiment().getExtracts();
    }

    /**
     * @return the itemsToAssociate
     */
    @Override
    public List<Extract> getItemsToAssociate() {
        return this.itemsToAssociate;
    }

    /**
     * @param itemsToAssociate the itemsToAssociate to set
     */
    public void setItemsToAssociate(List<Extract> itemsToAssociate) {
        this.itemsToAssociate = itemsToAssociate;
    }

    /**
     * @return the itemsToRemove
     */
    @Override
    public List<Extract> getItemsToRemove() {
        return this.itemsToRemove;
    }

    /**
     * @param itemsToRemove the itemsToRemove to set
     */
    public void setItemsToRemove(List<Extract> itemsToRemove) {
        this.itemsToRemove = itemsToRemove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleDelete() {
        if (!getCurrentLabeledExtract().getHybridizations().isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.annotations.cantdelete",
                                     new String[] {getText("experiment.labeledExtract"),
                                                   getText("experiment.hybridization") }));
            return false;
        }
        for (Extract e : getCurrentAssociationsCollection()) {
            e.getLabeledExtracts().remove(getCurrentLabeledExtract());
        }
        return true;
    }

    /**
     * download the data for this sample.
     * @return download
     * @throws IOException on file error
     */
    @SkipValidation
    public String download() throws IOException {
        Collection<CaArrayFile> files = getCurrentLabeledExtract().getAllDataFiles();
        if (files.isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.labeledExtracts.noDataToDownload"));
            return "noLabeledExtractData";
        }
        String baseName = ProjectFilesAction.determineDownloadFileName(getProject()).toString();
        DownloadHelper.downloadFiles(files, baseName);
        return null;
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
        return getCurrentLabeledExtract().getAllDataFiles();
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFileListAction() {
        return getDownloadFileListActionUrl(BioMaterialTypes.LABELED_EXTRACTS);
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFilesTableListSortUrlAction() {
        return getDownloadFileTableListSortActionUrl(BioMaterialTypes.LABELED_EXTRACTS);
    }
}
