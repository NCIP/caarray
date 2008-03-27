/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import gov.nih.nci.carpla.domain.Antibody;

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
	private List<Antibody>		_antibodies;
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
		return _antibodies;
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
	
	
	 public boolean isEditMode() {
	 return editMode;
	 }
	
	
	
	
	
	
	
	// /**
	// * @return the locked
	// */
	// public boolean isLocked() {
	// return locked;
	// }
	 
	 public boolean isLocked() {
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

	}

	//
	// /**
	// * Retrieves the list of all array designs.
	// * @return list
	// */
	// @SkipValidation
	 public String list() {
		 System.out.println("in AntibodyAction::list");
		 _antibodies = getAntibodyService().getAntibodies();
	// arrayDesigns = getArrayDesignService().getArrayDesigns();
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
	 public String view() {
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
