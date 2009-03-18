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
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileAccessService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileManagementService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.io.FileClosingInputStream;
import gov.nih.nci.caarray.web.fileupload.MonitoredMultiPartRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipException;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.set.TransformedSet;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author Scott Miller
 * 
 */
@SuppressWarnings( { "unchecked",
					"PMD.ExcessiveClassLength",
					"PMD.CyclomaticComplexity" })
@Validation
@Validations(expressions = @ExpressionValidator(message = "Files must be selected for this operation.", expression = "selectedFiles.size() > 0"))
public class ProjectFilesAction extends AbstractBaseProjectAction
																	implements
																	Preparable {

	private static final Logger			LOG						= Logger.getLogger(ProjectFilesAction.class);

	/**
	 * Maximum total uncompressed size (in bytes) of files that can be
	 * downloaded in a single ZIP. If files selected for download have a greater
	 * combined size, then the user will be presented with a group download
	 * page.
	 */
	public static final long			MAX_DOWNLOAD_SIZE		= 1024 * 1024 * 150;
	private static final String			UPLOAD_INPUT			= "upload";
	private static final long			serialVersionUID		= 1L;
	private static final String			ACTION_UNIMPORTED		= "listUnimported";
	private static final String			ACTION_IMPORTED			= "listImported";
	private static final String			ACTION_SUPPLEMENTAL		= "listSupplemental";
	private static final String			ACTION_TABLE			= "table";

	private List<File>					uploads;
	private List<String>				uploadFileNames			= new ArrayList<String>();
	private List<CaArrayFile>			selectedFiles			= new ArrayList<CaArrayFile>();
	private int							downloadSequenceNumber;
	private final List<DownloadGroup>	downloadFileGroups		= new ArrayList<DownloadGroup>();
	private String						downloadFileName;
	private InputStream					downloadStream;
	private Set<CaArrayFile>			files					= new HashSet<CaArrayFile>();
	private String						listAction;
	private String						extensionFilter;
	private Set<String>					allExtensions			= new TreeSet<String>();
	private String						fileType;
	private List<String>				fileTypes				= new ArrayList<String>();
	private static final String			UNKNOWN_FILE_TYPE		= "(Unknown File Types)";
	private static final String			KNOWN_FILE_TYPE			= "(Supported File Types)";

	private static final Transformer	EXTENSION_TRANSFORMER	= new Transformer() {
																	/**
																	 * Transforms
																	 * files to
																	 * their
																	 * extensions.
																	 */
																	public Object transform ( Object o)
																	{
																		CaArrayFile f = (CaArrayFile) o;
																		int index = f	.getName()
																						.lastIndexOf('.');
																		if (index == -1) {
																			return "(No Extension)";
																		}
																		return f.getName()
																				.substring(index)
																				.toLowerCase(Locale.US);
																	}
																};

	private void initFileTypes () {
		LOG.info("");
		fileTypes.add(KNOWN_FILE_TYPE);
		fileTypes.add(UNKNOWN_FILE_TYPE);
		for (FileType ft : FileType.values()) {
			fileTypes.add(ft.toString());
		}
	}

	private String prepListUnimportedPage () {
		LOG.info("prepListUnimportedPage");
		initFileTypes();
		setListAction(ACTION_UNIMPORTED);
		setFiles(new HashSet<CaArrayFile>());
		for (CaArrayFile f : getProject().getUnImportedFiles()) {
			if (getFileType() == null || (f.getFileType() != null && f	.getFileType()
																		.toString()
																		.equals(getFileType()))
				|| (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null)
				|| (UNKNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() == null)) {
				getFiles().add(f);
			}
		}
		return ACTION_UNIMPORTED;
	}

	private String prepListImportedPage () {
		LOG.info("prepListImportedPage");
		setListAction(ACTION_IMPORTED);
		setFiles(getProject().getImportedFiles());
		return ACTION_IMPORTED;
	}

	private String prepListSupplementalPage () {
		setListAction(ACTION_SUPPLEMENTAL);
		setFiles(getProject().getSupplementalFiles());
		return ACTION_SUPPLEMENTAL;
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listUnimported () {
		LOG.info("listUnimported");
		return prepListUnimportedPage();
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listUnimportedForm () {
		prepListUnimportedPage();
		return "listUnimportedForm";
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listUnimportedTable () {
		prepListUnimportedPage();
		return ACTION_TABLE;
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listImported () {
		return prepListImportedPage();
	}

	/**
	 * Method to get the list of supplemental files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listSupplemental () {
		return prepListSupplementalPage();
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listImportedTable () {
		prepListImportedPage();
		return ACTION_TABLE;
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String listSupplementalTable () {
		prepListSupplementalPage();
		return ACTION_TABLE;
	}

	/**
	 * Method to get the list of files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String downloadFiles () {
		for (CaArrayFile f : getProject().getFiles()) {
			if (StringUtils.isBlank(this.extensionFilter) || EXTENSION_TRANSFORMER	.transform(f)
																					.equals(this.extensionFilter)) {
				getFiles().add(f);
			}
		}
		Set s = TransformedSet.decorate(new TreeSet<String>(),
										EXTENSION_TRANSFORMER);
		s.addAll(getProject().getFiles());
		setAllExtensions(s);

		return Action.SUCCESS;
	}

	/**
	 * Ajax-only call to handle changing the filter extension.
	 * 
	 * @return success.
	 */
	@SkipValidation
	public String downloadFilesList () {
		return downloadFiles();
	}

	/**
	 * Ajax-only call to handle sorting.
	 * 
	 * @return success
	 */
	@SkipValidation
	public String downloadFilesListTable () {
		return downloadFiles();
	}

	/**
	 * Method to delete files.
	 * 
	 * @return the string representing the UI to display.
	 */
	public String deleteFiles () {
		doFileDeletion();
		return prepListUnimportedPage();
	}

	/**
	 * Method to delete supplemental files.
	 * 
	 * @return the string representing the UI to display.
	 */
	public String deleteSupplementalFiles () {
		doFileDeletion();
		return prepListSupplementalPage();
	}

	private void doFileDeletion () {
		int deletedFiles = 0;
		int skippedFiles = 0;
		for (CaArrayFile caArrayFile : getSelectedFiles()) {
			if (caArrayFile.getFileStatus().isDeletable()) {
				getFileAccessService().remove(caArrayFile);
				deletedFiles++;
			} else {
				skippedFiles++;
			}
		}
		ActionHelper.saveMessage(deletedFiles + " file(s) deleted.");
		if (skippedFiles > 0) {
			ActionHelper.saveMessage(skippedFiles + " file(s) were not in a status that allows for deletion.");
		}
	}

	/**
	 * load files for editing.
	 * 
	 * @return the string matching the result to follow
	 */
	public String editFiles () {
		return Action.SUCCESS;
	}

	/**
	 * Save the selected files.
	 * 
	 * @return the string matching the result to follow
	 */
	public String saveFiles () {
		LOG.info("saveFiles");
		if (!getSelectedFiles().isEmpty()) {
			for (CaArrayFile caArrayFile : getSelectedFiles()) {
				caArrayFile.setFileStatus(FileStatus.UPLOADED);
				if (caArrayFile.getValidationResult() != null) {
					caArrayFile.getValidationResult().getMessageSet().clear();
				}
				getFileAccessService().save(caArrayFile);
			}
			ActionHelper.saveMessage(getSelectedFiles().size() + " file(s) updated.");
		}
		return prepListUnimportedPage();
	}

	/**
	 * Method to validate the files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SuppressWarnings( { "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	// validation checks can't be easily refactored to smaller methods.
	public String validateFiles () {
		LOG.info("carplafix");
		;
		int validatedFiles = 0;
		int skippedFiles = 0;
		int arrayDesignFiles = 0;
		int unknownFiles = 0;
		int unparseableFiles = 0;
		boolean includesSdrf = includesType(getSelectedFiles(),
											FileType.MAGE_TAB_SDRF);

		boolean includesSradf = includesType(	getSelectedFiles(),
												FileType.RPLA_TAB_SRADF);

		CaArrayFileSet fileSet = new CaArrayFileSet(getProject());

		// should put my stuff in different method instead of this...
		// think of a better way to distinguish these sets...
		if (includesSradf) {
			for (CaArrayFile file : getSelectedFiles()) {
				if (file.getFileType() == null) {
					unknownFiles++;
					LOG.info("file, name=" + file.getName() + " is unknown");
				} else if (file.getFileStatus().isValidatable()) {
					LOG.info("adding file, name=" + file.getName()
								+ " to fileset as validatable");
					fileSet.add(file);
					validatedFiles++;
				} else {
					LOG.info("skipping " + file.getName());
					skippedFiles++;
				}
			}

			if (!fileSet.getFiles().isEmpty()) {
				getFileManagementService().validateFiles(getProject(), fileSet);
			}

			ActionHelper.saveMessage(getText(	"project.fileValidate.success",
												new String[] { String.valueOf(validatedFiles) }));

			if (skippedFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.invalidStatus",
													new String[] { String.valueOf(skippedFiles) }));
			}
			if (unparseableFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.unparseableFiles",
													new String[] { String.valueOf(unparseableFiles) }));
			}
			if (unknownFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.unknownType",
													new String[] { String.valueOf(unknownFiles) }));
			}

		}

		else {

			// -------------------------------------------------
			for (CaArrayFile file : getSelectedFiles()) {
				if (file.getFileType() == null) {
					unknownFiles++;
				} else if (file.getFileType().isArrayDesign()) {
					arrayDesignFiles++;
				} else if (!includesSdrf && !file	.getFileType()
													.isParseableData()) {
					unparseableFiles++;
				} else if (file.getFileStatus().isValidatable()) {
					fileSet.add(file);
					validatedFiles++;
				} else {
					skippedFiles++;
				}
			}

			if (!fileSet.getFiles().isEmpty()) {
				getFileManagementService().validateFiles(getProject(), fileSet);
			}

			ActionHelper.saveMessage(getText(	"project.fileValidate.success",
												new String[] { String.valueOf(validatedFiles) }));
			if (arrayDesignFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.arrayDesign",
													new String[] { String.valueOf(arrayDesignFiles) }));
			}
			if (skippedFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.invalidStatus",
													new String[] { String.valueOf(skippedFiles) }));
			}
			if (unparseableFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.unparseableFiles",
													new String[] { String.valueOf(unparseableFiles) }));
			}
			if (unknownFiles > 0) {
				ActionHelper.saveMessage(getText(	"project.fileValidate.error.unknownType",
													new String[] { String.valueOf(unknownFiles) }));
			}

			// -------------------------------------------------

		}
		return prepListUnimportedPage();
	}

	// ########################################################################
	private boolean includesType ( List<CaArrayFile> fileList, FileType type) {
		LOG.info("");
		for (CaArrayFile file : fileList) {
			if (type.equals(file.getFileType())) {
				return true;
			}
		}
		return false;
	}

	// ########################################################################
	private String importRplaFiles () {
		LOG.info("carplafix");
		int importedFiles = 0;
		int skippedFiles = 0;
		int arrayDesignFiles = 0;
		int unknownFiles = 0;

		CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
		for (CaArrayFile file : getSelectedFiles()) {
			if (file.getFileType() == null) {
				unknownFiles++;
				LOG.info("file, name=" + file.getName() + " is unknown");
			} else if (file.getFileStatus().isImportable()) {
				fileSet.add(file);
				importedFiles++;
				LOG.info("adding file, name=" + file.getName()
							+ " to fileset as importable");
			} else {
				LOG.info("skipping " + file.getName());
				skippedFiles++;
			}
		}
		if (!fileSet.getFiles().isEmpty()) {
			getFileManagementService().importFiles(getProject(), fileSet);
		}
		ActionHelper.saveMessage(getText(	"project.fileImport.success",
											new String[] { String.valueOf(importedFiles) }));
		
		if (skippedFiles > 0) {
			ActionHelper.saveMessage(getText(	"project.fileImport.error.invalidStatus",
												new String[] { String.valueOf(skippedFiles) }));
		}
		if (unknownFiles > 0) {
			ActionHelper.saveMessage(getText(	"project.fileImport.error.unknownType",
												new String[] { String.valueOf(unknownFiles) }));
		}
		refreshProject();
		return prepListUnimportedPage();

		
	}

	/**
	 * Method to import the files.
	 * 
	 * @return the string matching the result to follow
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	// validation checks can't
	// be easily refactored to
	// smaller methods.
	public String importFiles () {
		LOG.info("carplafix");

		// think of a better way to distinguish these sets...
		boolean includesSradf = includesType(	getSelectedFiles(),
												FileType.RPLA_TAB_SRADF);
		if (includesSradf) {

			return importRplaFiles();

		}

		int importedFiles = 0;
		int skippedFiles = 0;
		int arrayDesignFiles = 0;
		int unknownFiles = 0;

		CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
		for (CaArrayFile file : getSelectedFiles()) {
			if (file.getFileType() == null) {
				unknownFiles++;
			} else if (file.getFileType().isArrayDesign()) {
				arrayDesignFiles++;
			} else if (file.getFileStatus().isImportable()) {
				fileSet.add(file);
				importedFiles++;
			} else {
				skippedFiles++;
			}
		}
		if (!fileSet.getFiles().isEmpty()) {
			getFileManagementService().importFiles(getProject(), fileSet);
		}
		ActionHelper.saveMessage(getText(	"project.fileImport.success",
											new String[] { String.valueOf(importedFiles) }));
		if (arrayDesignFiles > 0) {
			ActionHelper.saveMessage(getText(	"project.fileImport.error.arrayDesign",
												new String[] { String.valueOf(arrayDesignFiles) }));
		}
		if (skippedFiles > 0) {
			ActionHelper.saveMessage(getText(	"project.fileImport.error.invalidStatus",
												new String[] { String.valueOf(skippedFiles) }));
		}
		if (unknownFiles > 0) {
			ActionHelper.saveMessage(getText(	"project.fileImport.error.unknownType",
												new String[] { String.valueOf(unknownFiles) }));
		}
		refreshProject();
		return prepListUnimportedPage();
	}

	/**
	 * Adds supplemental data files to the system.
	 * 
	 * @return the string matching the result to follow
	 */
	public String addSupplementalFiles () {
		LOG.info("addSupplementalFiles");
		CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
		for (CaArrayFile file : getSelectedFiles()) {
			fileSet.add(file);
		}
		if (!fileSet.getFiles().isEmpty()) {
			getFileManagementService().addSupplementalFiles(getProject(),
															fileSet);
		}
		ActionHelper.saveMessage(fileSet.getFiles().size() + " supplemental file(s) added to project.");
		refreshProject();
		return prepListUnimportedPage();
	}

	/**
	 * This method refreshes the project fromt he db. It is in its own method to
	 * allow test cases to overwrite this.
	 */
	protected void refreshProject () {
		HibernateUtil.getCurrentSession().refresh(getProject());
	}

	/**
	 * View the validation messages for the selected files.
	 * 
	 * @return the string matching the result to use.
	 */
	public String validationMessages () {
		return Action.SUCCESS;
	}

	/**
	 * uploads file.
	 * 
	 * @return the string matching the result to follow
	 * @throws IOException
	 *             on io error
	 */
	@SkipValidation
	public String upload () throws IOException {
		LOG.info("");
		List<String> conflictingFiles = new ArrayList<String>();
		int count = 0;
		try {
			count = getProjectManagementService()	.uploadFiles(	getProject(),
																	getUpload(),
																	getUploadFileName(),
																	conflictingFiles);
		} catch (ZipException e) {
			ActionHelper.saveMessage(getText("errorUploadingZip"));
			return UPLOAD_INPUT;
		} catch (Exception e) {
			String msg = "Unable to upload file: " + e.getMessage();
			LOG.error(msg, e);
			ActionHelper.saveMessage(getText("errorUploading"));
			return UPLOAD_INPUT;
		}

		for (String conflict : conflictingFiles) {
			ActionHelper.saveMessage(getText(	"experiment.files.upload.filename.exists",
												new String[] { conflict }));
		}

		ActionHelper.saveMessage(count + " file(s) uploaded.");
		MonitoredMultiPartRequest.releaseProgressMonitor(ServletActionContext.getRequest());
		return UPLOAD_INPUT;
	}

	/**
	 * Prepares for download by zipping selected files and setting the internal
	 * InputStream.
	 * 
	 * @return SUCCESS
	 * @throws IOException
	 *             if
	 */
	@SkipValidation
	public String download () throws IOException {
		computeDownloadGroups();
		if (downloadFileGroups.size() == 1) {
			File zipFile = getProjectManagementService().prepareForDownload(getSelectedFiles());
			this.downloadStream = new FileClosingInputStream(	new FileInputStream(zipFile),
																zipFile);
			this.downloadFileName = determineDownloadFileName();
			return "download";
		} else {
			return "downloadGroups";
		}
	}

	private String determineDownloadFileName () {
		return "caArray_" + getProject().getExperiment().getPublicIdentifier()
				+ "_files"
				+ (this.downloadSequenceNumber > 0	? this.downloadSequenceNumber
													: "")
				+ ".zip";
	}

	private void computeDownloadGroups () {
		this.downloadFileGroups.clear();
		for (CaArrayFile file : getSelectedFiles()) {
			addToDownloadGroups(file);
		}
	}

	/**
	 * Add given file to the download groups. The goal is to find the best
	 * possible group to put it, such that the total number of groups will be
	 * minimized. the algorithm is to put it in the group which will then have
	 * the closest to max allowable size without going over
	 * 
	 * @param file
	 *            the file to add
	 */
	private void addToDownloadGroups ( CaArrayFile file) {
		DownloadGroup bestGroup = null;
		long maxNewSize = 0;
		for (DownloadGroup group : this.downloadFileGroups) {
			long newGroupSize = group.getTotalCompressedSize() + file.getCompressedSize();
			if (newGroupSize < MAX_DOWNLOAD_SIZE && newGroupSize > maxNewSize) {
				maxNewSize = newGroupSize;
				bestGroup = group;
			}
		}
		if (bestGroup == null) {
			bestGroup = new DownloadGroup();
			this.downloadFileGroups.add(bestGroup);
		}
		bestGroup.addFile(file);
	}

	/**
	 * Action for displaying the upload in background form.
	 * 
	 * @return the string matching the result to follow
	 */
	@SkipValidation
	public String uploadInBackground () {
		return "uploadInBackground";
	}

	/**
	 * @return the stream containing the zip file download
	 */
	public InputStream getDownloadStream () {
		return this.downloadStream;
	}

	/**
	 * uploaded file.
	 * 
	 * @return uploads uploaded files
	 */
	public List<File> getUpload () {
		return this.uploads;
	}

	/**
	 * sets file uploads.
	 * 
	 * @param inUploads
	 *            List
	 */
	public void setUpload ( List<File> inUploads) {
		this.uploads = inUploads;
	}

	/**
	 * returns uploaded file name.
	 * 
	 * @return uploadFileNames
	 */
	public List<String> getUploadFileName () {
		return this.uploadFileNames;
	}

	/**
	 * sets uploaded file names.
	 * 
	 * @param inUploadFileNames
	 *            List
	 */
	public void setUploadFileName ( List<String> inUploadFileNames) {
		this.uploadFileNames = inUploadFileNames;
	}

	/**
	 * @return the selectedFiles
	 */
	public List<CaArrayFile> getSelectedFiles () {
		return this.selectedFiles;
	}

	/**
	 * @param selectedFiles
	 *            the selectedFiles to set
	 */
	public void setSelectedFiles ( List<CaArrayFile> selectedFiles) {
		this.selectedFiles = selectedFiles;
	}

	/**
	 * @return the files
	 */
	public Set<CaArrayFile> getFiles () {
		return this.files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles ( Set<CaArrayFile> files) {
		this.files = files;
	}

	/**
	 * @return the listAction
	 */
	public String getListAction () {
		return this.listAction;
	}

	/**
	 * @param listAction
	 *            the listAction to set
	 */
	public void setListAction ( String listAction) {
		this.listAction = listAction;
	}

	/**
	 * @return all extensions for the project files
	 */
	public Set<String> getAllExtensions () {
		return this.allExtensions;
	}

	/**
	 * @param allExtensions
	 *            all extensions for the project
	 */
	public void setAllExtensions ( Set<String> allExtensions) {
		this.allExtensions = allExtensions;
	}

	/**
	 * @return extensions to filter for
	 */
	public String getExtensionFilter () {
		return this.extensionFilter;
	}

	/**
	 * @param extensionFilter
	 *            extensions to filter for
	 */
	public void setExtensionFilter ( String extensionFilter) {
		this.extensionFilter = extensionFilter;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType () {
		return this.fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType ( String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileTypes
	 */
	public List<String> getFileTypes () {
		return this.fileTypes;
	}

	/**
	 * @param fileTypes
	 *            the fileTypes to set
	 */
	public void setFileTypes ( List<String> fileTypes) {
		this.fileTypes = fileTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate () {
		super.validate();
		if (hasErrors()) {
			// crappy, but see no other way
			String actionName = ActionContext.getContext().getName();
			if (actionName.contains("Supplemental")) {
				prepListSupplementalPage();
			} else {
				prepListUnimportedPage();
			}
		}
	}

	/**
	 * @return the download groups
	 */
	public List<DownloadGroup> getDownloadFileGroups () {
		return downloadFileGroups;
	}

	/**
	 * @return the downloadFileName
	 */
	public String getDownloadFileName () {
		return downloadFileName;
	}

	/**
	 * @return the downloadSequenceNumber
	 */
	public int getDownloadSequenceNumber () {
		return downloadSequenceNumber;
	}

	/**
	 * @param downloadSequenceNumber
	 *            the downloadSequenceNumber to set
	 */
	public void setDownloadSequenceNumber ( int downloadSequenceNumber) {
		this.downloadSequenceNumber = downloadSequenceNumber;
	}
}