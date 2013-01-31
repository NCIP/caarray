//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.web.action;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getArrayDesignService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileAccessService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileManagementService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getVocabularyService;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getAntibodyService;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.carpla.domain.antibody.Antibody;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

// @Validation
// @SuppressWarnings("PMD.CyclomaticComplexity")
public class AntibodyAction extends ActionSupport implements Preparable {
	// private static final long serialVersionUID = 1L;
	//
	// private ArrayDesign arrayDesign;
	// private File upload;
	// private String uploadFileName;
	// private String uploadContentType;
	// private String uploadFormatType;
	// private List<ArrayDesign> arrayDesigns;
	// private List<Organization> providers;
	private List<Antibody>		antibodies;
	private List<Organization>	_providers;

	// private Set<Term> featureTypes;
	// private List<Organism> organisms;
	// private boolean editMode;
	// private boolean locked;

	private boolean				editMode;
	private boolean				locked;

	//
	// /**
	// * @return the array design
	// */
	// public ArrayDesign getArrayDesign() {
	// return arrayDesign;
	// }

	private Antibody			_antibody;

	public Antibody getAntibody () {
		return _antibody;
	}

	// /**
	// * @param arrayDesign the array design to set
	// */
	// public void setArrayDesign(ArrayDesign arrayDesign) {
	// this.arrayDesign = arrayDesign;
	// }

	public void setAntibody ( Antibody antibody) {
		this._antibody = antibody;
	}

	// /**
	// * @return the upload
	// */
	// public File getUpload() {
	// return upload;
	// }
	// /**
	// * @param upload the designFile to set
	// */
	// public void setUpload(File upload) {
	// this.upload = upload;
	// }
	// /**
	// * @return the uploadFileName
	// */
	// public String getUploadFileName() {
	// return uploadFileName;
	// }
	// /**
	// * @param uploadFileName the uploadFileName to set
	// */
	// public void setUploadFileName(String uploadFileName) {
	// this.uploadFileName = uploadFileName;
	// }
	// /**
	// * @return the uploadContentType
	// */
	// public String getUploadContentType() {
	// return uploadContentType;
	// }
	// /**
	// * @param uploadContentType the uploadContentType to set
	// */
	// public void setUploadContentType(String uploadContentType) {
	// this.uploadContentType = uploadContentType;
	// }
	// /**
	// * @return the uploadFormatType
	// */
	// public String getUploadFormatType() {
	// return uploadFormatType;
	// }
	// /**
	// * @param uploadFormatType the uploadFormatType to set
	// */
	// public void setUploadFormatType(String uploadFormatType) {
	// this.uploadFormatType = uploadFormatType;
	// }
	// /**
	// * @return the arrayDesigns
	// */
	// public List<ArrayDesign> getArrayDesigns() {
	// return arrayDesigns;
	// }

	public List<Antibody> getAntibodies () {
		System.out.println("Getting list of antibodies in AntibodyAction");
		return antibodies;
	}

	// /**
	// * @return the manufacturers
	// */
	// public List<Organization> getProviders() {
	// return providers;
	// }
	public List<Organization> getProviders () {
		return _providers;
	}

	// /**
	// * @return the featureTypes
	// */
	// public Set<Term> getFeatureTypes() {
	// return featureTypes;
	// }
	// /**
	// * @return the organisms
	// */
	// public List<Organism> getOrganisms() {
	// return organisms;
	// }
	// /**
	// * @return the editMode
	// */
	// public boolean isEditMode() {
	// return editMode;
	// }

	public boolean isEditMode () {
		return editMode;
	}

	// /**
	// * @return the locked
	// */
	// public boolean isLocked() {
	// return locked;
	// }

	public boolean isLocked () {
		System.out.println("in AntibodyAction::islocked");
		return locked;
	}

	//
	// /**
	// * {@inheritDoc}
	// */
	// public void prepare() {
	// this.organisms = getVocabularyService().getOrganisms();
	// this.providers = getArrayDesignService().getAllProviders();
	// this.featureTypes =
	// CaArrayActionHelper.getTermsFromCategory(ExperimentOntologyCategory.TECHNOLOGY_TYPE);
	// if (arrayDesign != null && arrayDesign.getId() != null) {
	// ArrayDesign retrieved =
	// getArrayDesignService().getArrayDesign(arrayDesign.getId());
	// if (retrieved == null) {
	// throw new PermissionDeniedException(getArrayDesign(),
	// SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
	// } else {
	// arrayDesign = retrieved;
	// }
	// locked =
	// getArrayDesignService().isArrayDesignLocked(arrayDesign.getId());
	// }
	// }
	public void prepare () {

		this.antibodies = getAntibodyService().getAntibodies();

	}

	//
	// /**
	// * Retrieves the list of all array designs.
	// * @return list
	// */
	// @SkipValidation
	public String list () {
		System.out.println("in AntibodyAction::list");
		antibodies = getAntibodyService().getAntibodies();

		return "list";
	}

	//
	// /**
	// * Edit view of an array design.
	// * @return input
	// */
	// @SkipValidation
	// public String edit() {
	// editMode = true;
	// return Action.INPUT;
	// }
	//
	// /**
	// * Readonly view of an array design.
	// * @return input
	// */
	// @SkipValidation
	public String view () {
		editMode = false;
		return Action.INPUT;
	}
	//
	// /**
	// * Save a new or existing array design.
	// * @return success
	// */
	// @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "deprecation" })
	// @Validations(
	// requiredStrings = {
	// @RequiredStringValidator(fieldName = "arrayDesign.version", key =
	// "errors.required", message = "")
	// },
	// requiredFields = {
	// @RequiredFieldValidator(fieldName = "arrayDesign.assayType", key =
	// "errors.required", message = ""),
	// @RequiredFieldValidator(fieldName = "arrayDesign.provider", key =
	// "errors.required", message = ""),
	// @RequiredFieldValidator(fieldName = "arrayDesign.technologyType", key =
	// "errors.required", message = ""),
	// @RequiredFieldValidator(fieldName = "arrayDesign.organism", key =
	// "errors.required", message = "")
	// }
	// )
	// public String save() {
	// Long id = arrayDesign.getId();
	// if (id == null) {
	// arrayDesign.setName(FilenameUtils.getBaseName(uploadFileName));
	// }
	// try {
	// if (uploadFileName == null) {
	// getArrayDesignService().saveArrayDesign(arrayDesign);
	// } else {
	// CaArrayFile designFile = getFileAccessService().add(upload,
	// uploadFileName);
	// if (uploadFormatType != null && FileType.valueOf(uploadFormatType) !=
	// null) {
	// designFile.setFileType(FileType.valueOf(uploadFormatType));
	// }
	// getFileManagementService().saveArrayDesign(arrayDesign, designFile);
	// if (!FileStatus.IMPORTED_NOT_PARSED.equals(designFile.getFileStatus())) {
	// getFileManagementService().importArrayDesignDetails(arrayDesign);
	// }
	// }
	// } catch (InvalidDataFileException e) {
	// FileValidationResult result = e.getFileValidationResult();
	// for (ValidationMessage message : result.getMessages()) {
	// addFieldError("upload", message.getMessage());
	// }
	// } catch (IllegalAccessException iae) {
	// arrayDesign =
	// getArrayDesignService().getArrayDesign(arrayDesign.getId());
	// addActionError(iae.getMessage());
	// }
	// if (this.hasErrors()) {
	// // addArrayDesign overwrites the id, so reset if there is an error.
	// arrayDesign.setId(id);
	// return edit();
	// }
	// return Action.SUCCESS;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// @SuppressWarnings("unchecked")
	// public void validate() {
	// super.validate();
	// // upload file is required for new array designs
	// if (!ActionHelper.isSkipValidationSetOnCurrentAction()
	// && arrayDesign.getId() == null && uploadFileName == null) {
	// addFieldError("upload", getText("fileRequired"));
	// }
	// if (this.hasErrors()) {
	// editMode = true;
	// }
	// }
}
