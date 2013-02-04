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
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.search.ExtractSortCriterion;
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
 * Action implementing the extracts tab.
 *
 * @author Dan Kokotov
 */
public class ProjectExtractsAction extends AbstractProjectAssociatedAnnotationsListTabAction<Sample> {
    private static final long serialVersionUID = 1L;

    private Extract currentExtract = new Extract();
    private List<Sample> itemsToAssociate = new ArrayList<Sample>();
    private List<Sample> itemsToRemove = new ArrayList<Sample>();

    /**
     * Default constructor.
     */
    public ProjectExtractsAction() {
        super("extract", "sample", new SortablePaginatedList<Extract, ExtractSortCriterion>(PAGE_SIZE,
                ExtractSortCriterion.NAME.name(), ExtractSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();
        this.currentExtract = retrieveByIdOrExternalId(Extract.class, this.currentExtract);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException {
        ServiceLocatorFactory.getProjectManagementService().copyExtract(getProject(), this.currentExtract.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Extract> getCollection() {
        return getProject().getExperiment().getExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentExtract();
    }

    /**
     * @return the currentExtract
     */
    @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase",
            value = "experiment.extracts"))
    public Extract getCurrentExtract() {
        return this.currentExtract;
    }

    /**
     * @param currentExtract the currentExtract to set
     */
    public void setCurrentExtract(Extract currentExtract) {
        this.currentExtract = currentExtract;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection getAnnotationCollectionToUpdate(Sample item) {
        return item.getExtracts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Sample> getCurrentAssociationsCollection() {
        return getCurrentExtract().getSamples();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Sample> getPossibleAssociationsCollection() {
        return getExperiment().getSamples();
    }

    /**
     * @return the itemsToAssociate
     */
    @Override
    public List<Sample> getItemsToAssociate() {
        return this.itemsToAssociate;
    }

    /**
     * @param itemsToAssociate the itemsToAssociate to set
     */
    public void setItemsToAssociate(List<Sample> itemsToAssociate) {
        this.itemsToAssociate = itemsToAssociate;
    }

    /**
     * @return the itemsToRemove
     */
    @Override
    public List<Sample> getItemsToRemove() {
        return this.itemsToRemove;
    }

    /**
     * @param itemsToRemove the itemsToRemove to set
     */
    public void setItemsToRemove(List<Sample> itemsToRemove) {
        this.itemsToRemove = itemsToRemove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleDelete() {
        if (!getCurrentExtract().getLabeledExtracts().isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.annotations.cantdelete",
                    new String[] {getText("experiment.extract"),
                                  getText("experiment.labeledExtract") }));
            return false;
        }
        for (Sample s : getCurrentAssociationsCollection()) {
            s.getExtracts().remove(getCurrentExtract());
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
        Collection<CaArrayFile> files = getCurrentExtract().getAllDataFiles();
        if (files.isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.extracts.noDataToDownload"));
            return "noExtractData";
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
        return getCurrentExtract().getAllDataFiles();
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFileListAction() {
        return getDownloadFileListActionUrl(BioMaterialTypes.EXTRACTS);
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFilesTableListSortUrlAction() {
        return getDownloadFileTableListSortActionUrl(BioMaterialTypes.EXTRACTS);
    }
}
