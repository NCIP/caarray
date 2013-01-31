//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.search.HybridizationSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.FileClosingInputStream;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.caarray.web.ui.PaginatedListImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;

/**
 * Action implementing the samples tab.
 * 
 * @author Dan Kokotov
 */
@Validation
public class ProjectRplaHybridizationsAction
										extends
										AbstractProjectProtocolAnnotationListTabAction<LabeledExtract> {
	private static final long		serialVersionUID		= 1L;

	private Hybridization			currentHybridization	= new Hybridization();
	private List<LabeledExtract>	itemsToAssociate		= new ArrayList<LabeledExtract>();
	private List<LabeledExtract>	itemsToRemove			= new ArrayList<LabeledExtract>();

	private InputStream				downloadStream;

	/**
	 * Default constructor.
	 */
	public ProjectRplaHybridizationsAction() {
		super(	"hybridization",
				"labeledExtract",
				new PaginatedListImpl<Hybridization, HybridizationSortCriterion>(	PAGE_SIZE,
																					HybridizationSortCriterion.NAME.name(),
																					HybridizationSortCriterion.class));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws VocabularyServiceException
	 */
	@Override
	public void prepare () throws VocabularyServiceException {
		super.prepare();

		Set<ArrayDesign> arrayDesigns = getProject().getExperiment()
													.getArrayDesigns();
		if (arrayDesigns != null && arrayDesigns.size() == 1) {
			if (currentHybridization.getArray() == null) {
				currentHybridization.setArray(new Array());
			}
			currentHybridization.getArray().setDesign(arrayDesigns	.iterator()
																	.next());
		}

		if (this.currentHybridization.getId() != null) {
			Hybridization retrieved = getGenericDataService()	.getPersistentObject(	Hybridization.class,
																						this.currentHybridization.getId());
			if (retrieved == null) {
				throw new PermissionDeniedException(this.currentHybridization,
													SecurityUtils.PERMISSIONS_PRIVILEGE,
													UsernameHolder.getUser());
			} else {
				this.currentHybridization = retrieved;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SkipValidation
	public String copy () {
		return "notYetImplemented";
	}

	/**
	 * download the data for the hyb.
	 * 
	 * @return download
	 * @throws IOException
	 *             on file error
	 */
	@SkipValidation
	public String download () throws IOException {
		ProjectManagementService pms = (ProjectManagementService) ServiceLocatorFactory	.getLocator()
																						.lookup(ProjectManagementService.JNDI_NAME);
		File zipFile = pms.prepareHybsForDownload(	getProject(),
													Collections.singleton(getCurrentHybridization()));
		if (zipFile == null) {
			ActionHelper.saveMessage(getText("experiment.hybridizations.noDataToDownload"));
			return "noHybData";
		}
		this.downloadStream = new FileClosingInputStream(	new FileInputStream(zipFile),
															zipFile);
		return "download";
	}

	/**
	 * @return the stream containing the zip file download
	 */
	public InputStream getDownloadStream () {
		return this.downloadStream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCopyItem () {
		throw new NotImplementedException("Copying not supported for hybridizations");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	@FieldExpressionValidator(fieldName = "currentHybridization.array.design", key = "struts.validator.requiredString", message = "", expression = "currentHybridization.array.design != null || project.experiment.arrayDesigns.isEmpty")
	public String save () {
		// needed to added additional validation
		return super.save();
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<Hybridization> getCollection () {
		return getProject().getExperiment().getHybridizations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractCaArrayEntity getItem () {
		return getCurrentHybridization();
	}

	/**
	 * @return the currentHybridization
	 */
	@CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "resourceKeyBase", value = "experiment.hybridizations"))
	public Hybridization getCurrentHybridization () {
		return this.currentHybridization;
	}

	/**
	 * @param currentHybridization
	 *            the currentHybridization to set
	 */
	public void setCurrentHybridization ( Hybridization currentHybridization) {
		this.currentHybridization = currentHybridization;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection getAnnotationCollectionToUpdate ( LabeledExtract item) {
		return item.getHybridizations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LabeledExtract> getCurrentAssociationsCollection () {
		return getCurrentHybridization().getLabeledExtracts();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<LabeledExtract> getPossibleAssociationsCollection () {
		return getExperiment().getLabeledExtracts();
	}

	/**
	 * @return the itemsToAssociate
	 */
	@Override
	public List<LabeledExtract> getItemsToAssociate () {
		return this.itemsToAssociate;
	}

	/**
	 * @param itemsToAssociate
	 *            the itemsToAssociate to set
	 */
	public void setItemsToAssociate ( List<LabeledExtract> itemsToAssociate) {
		this.itemsToAssociate = itemsToAssociate;
	}

	/**
	 * @return the itemsToRemove
	 */
	@Override
	public List<LabeledExtract> getItemsToRemove () {
		return this.itemsToRemove;
	}

	/**
	 * @param itemsToRemove
	 *            the itemsToRemove to set
	 */
	public void setItemsToRemove ( List<LabeledExtract> itemsToRemove) {
		this.itemsToRemove = itemsToRemove;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleDelete () {
		for (LabeledExtract le : getCurrentAssociationsCollection()) {
			le.getHybridizations().remove(getCurrentHybridization());
		}
		// clean up upstream associations to the subgraph of objects
		getProject().getFiles()
					.removeAll(getCurrentHybridization().getAllDataFiles());
		// clean up factor value associations
		for (FactorValue fv : getCurrentHybridization().getFactorValues()) {
			fv.getFactor().getFactorValues().remove(fv);
		}
		return true;
	}
}
