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
package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignDeleteException;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileExtension;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.InvalidNumberOfArgsException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.web.fileupload.MonitoredMultiPartRequest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import org.apache.commons.lang.UnhandledException;

/**
 * @author Winston Cheng
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
public class ArrayDesignAction extends ActionSupport implements Preparable {
    private static final String UPLOAD_FIELD_NAME = "upload";

    private static final long serialVersionUID = 1L;

    private ArrayDesign arrayDesign;
    private List<File> uploads;
    private List<String> uploadFileName;
    private String uploadContentType;
    private List<String> fileFormatTypes;
    private List<ArrayDesign> arrayDesigns;
    private List<Organization> providers;
    private Set<Term> featureTypes;
    private List<Organism> organisms;
    private Set<AssayType> assayTypes;
    private boolean editMode;
    private boolean locked;
    private boolean createMode;

    /**
     * @return the set of array design file types to display in UI.
     */
    public static Set<FileType> arrayDesignTypes() {
        return FileType.ARRAY_DESIGN_FILE_TYPES;
    }

    /**
     * @return the array design
     */
    public ArrayDesign getArrayDesign() {
        return arrayDesign;
    }

    /**
     * @param arrayDesign the array design to set
     */
    public void setArrayDesign(ArrayDesign arrayDesign) {
        this.arrayDesign = arrayDesign;
    }

    /**
     * uploaded file.
     * 
     * @return uploads uploaded files
     */
    public List<File> getUpload() {
        return this.uploads;
    }

    /**
     * sets file uploads.
     * 
     * @param inUploads List
     */
    public void setUpload(List<File> inUploads) {
        this.uploads = inUploads;
    }

    /**
     * @return the uploadFileName
     */
    public List<String> getUploadFileName() {
        return uploadFileName;
    }

    /**
     * @param uploadFileNames List
     */
    public void setUploadFileName(List<String> uploadFileNames) {
        this.uploadFileName = uploadFileNames;
    }

    /**
     * @return the uploadContentType
     */
    public String getUploadContentType() {
        return uploadContentType;
    }

    /**
     * @param uploadContentType the uploadContentType to set
     */
    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    /**
     * @return the uploadFormatType
     */
    public List<String> getFileFormatType() {
        return fileFormatTypes;
    }

    /**
     * @param uploadFormatTypes the uploadFormatType to set
     */
    public void setFileFormatType(List<String> uploadFormatTypes) {
        this.fileFormatTypes = uploadFormatTypes;
    }

    /**
     * @return the arrayDesigns
     */
    public List<ArrayDesign> getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * @return the manufacturers
     */
    public List<Organization> getProviders() {
        return providers;
    }

    /**
     * @return the featureTypes
     */
    public Set<Term> getFeatureTypes() {
        return featureTypes;
    }

    /**
     * @return the organisms
     */
    public List<Organism> getOrganisms() {
        return organisms;
    }

    /**
     * @return the createMode
     */
    public boolean isCreateMode() {
        return createMode;
    }

    /**
     * @param createMode the createMode to set
     */
    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return editMode;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @return the assayTypes
     */
    public Set<AssayType> getAssayTypes() {
        return assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        this.organisms = ServiceLocatorFactory.getVocabularyService().getOrganisms();
        this.providers = ServiceLocatorFactory.getArrayDesignService().getAllProviders();
        this.featureTypes = VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.TECHNOLOGY_TYPE);
        if (arrayDesign != null && arrayDesign.getId() != null) {
            ArrayDesign retrieved = ServiceLocatorFactory.getArrayDesignService().getArrayDesign(arrayDesign.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(getArrayDesign(), SecurityUtils.PERMISSIONS_PRIVILEGE,
                        UsernameHolder.getUser());
            } else {
                arrayDesign = retrieved;
            }
            locked = ServiceLocatorFactory.getArrayDesignService().isArrayDesignLocked(arrayDesign.getId());
        }
    }

    /**
     * Retrieves the list of all array designs.
     * 
     * @return list
     */
    @SkipValidation
    public String list() {
        arrayDesigns = ServiceLocatorFactory.getArrayDesignService().getArrayDesigns();
        return "list";
    }

    /**
     * Edit view of an array design.
     * 
     * @return input
     */
    @SkipValidation
    public String edit() {
        createMode = false;
        editMode = true;
        return Action.INPUT;
    }

    /**
     * Edit view of an import file.
     * 
     * @return input
     */
    @SkipValidation
    public String editFile() {
        createMode = false;
        editMode = true;
        return "metaValid";
    }

    /**
     * Read-only view of an array design.
     * 
     * @return input
     */
    @SkipValidation
    public String view() {
        createMode = false;
        editMode = false;
        return Action.INPUT;
    }

    /**
     * Creation of a new array design.
     * 
     * @return input
     */
    @SkipValidation
    public String create() {
        createMode = true;
        editMode = true;
        return Action.INPUT;
    }

    /**
     * Save a new or existing array design.
     * 
     * @return success
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Validations(fieldExpressions = { 
            @FieldExpressionValidator(fieldName = "arrayDesign.assayTypes", message = "", 
                    key = "errors.required", expression = "!arrayDesign.assayTypes.isEmpty") }, 
            requiredStrings = { @RequiredStringValidator(fieldName = "arrayDesign.version", 
                    key = "errors.required", message = "") }, requiredFields = {
            @RequiredFieldValidator(fieldName = "arrayDesign.provider", key = "errors.required", message = ""),
            @RequiredFieldValidator(fieldName = "arrayDesign.technologyType", key = "errors.required", message = ""),
            @RequiredFieldValidator(fieldName = "arrayDesign.organism", key = "errors.required", message = "") })
    /**
     * Save the array design metadata.
     */
    public String saveMeta() {
        if (!createMode && editMode) {

            if (ServiceLocatorFactory.getArrayDesignService().isDuplicate(arrayDesign)) {
                ActionHelper.saveMessage(getText("arraydesign.duplicate", new String[] {getArrayDesign().getName() }));
                return Action.INPUT;
            }

            saveImportFile();
            List<Object> args = new ArrayList<Object>();
            args.add(getArrayDesign().getName());
            args.add(getArrayDesign().getProvider().getName());
            ActionHelper.saveMessage(getText("arraydesign.saved", args));
            return Action.SUCCESS;
        }
        editMode = true;
        return "metaValid";
    }

    /**
     * download data files for an array design.
     * 
     * @return download
     * @throws IOException on file error
     */
    @SkipValidation
    public String download() throws IOException {
        if (getArrayDesign() == null || getArrayDesign().getDesignFiles().isEmpty()) {
            ActionHelper.saveMessage(getText("arrayDesign.noDataToDownload"));
            return "list";
        }

        DownloadHelper.downloadFiles(getArrayDesign().getDesignFiles(), determineDownloadFileName());
        return null;
    }

    /**
     * Returns the filename for a zip of files for the given project, assuming that the download will not be grouped.
     * 
     * @return the filename
     */
    private String determineDownloadFileName() {
        StringBuilder name = new StringBuilder("caArray_").append(arrayDesign.getName()).append("_file");
        return name.toString();
    }

    /**
     * Save/Update file associated with array design.
     * 
     * @return input
     */
    @SkipValidation
    @SuppressWarnings("deprecation")
    public String save() {
        String returnVal = null;
        Long id = null;
        // upload file is required for new array designs
        if (arrayDesign.getId() == null && (uploadFileName == null || uploadFileName.isEmpty())) {
            addFieldError(UPLOAD_FIELD_NAME, getText("fileRequired"));
        } else {
            id = arrayDesign.getId();
            if (id == null) {
                arrayDesign.setName(FilenameUtils.getBaseName(uploadFileName.get(0)));
            }

            saveImportFile();
        }

        if (this.hasErrors()) {
            // addArrayDesign overwrites the id, so reset if there is an error.
            if (id != null) {
                arrayDesign.setId(id);
            }
            returnVal = "metaValid";
        } else {
            returnVal = "importComplete";
        }

        MonitoredMultiPartRequest.releaseProgressMonitor(ServletActionContext.getRequest());

        return returnVal;
    }

    /**
     * Delete an array design.
     * 
     * @return input
     */
    @SkipValidation
    public String delete() {
        try {
            ServiceLocatorFactory.getArrayDesignService().deleteArrayDesign(getArrayDesign());
            ActionHelper
                    .saveMessage(getText("arrayDesign.deletionSuccess", new String[] {getArrayDesign().getName() }));
        } catch (ArrayDesignDeleteException e) {
            ActionHelper.saveMessage(e.getMessage());
        }
        arrayDesigns = ServiceLocatorFactory.getArrayDesignService().getArrayDesigns();
        return SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (this.hasErrors()) {
            editMode = true;
        }
    }

    private void saveImportFile() {
        try {
            // figure out if we are editing or creating.
            if (arrayDesign.getId() != null && (uploadFileName == null || uploadFileName.isEmpty())) {
                ServiceLocatorFactory.getArrayDesignService().saveArrayDesign(arrayDesign);
            } else {
                // Checks if any uploaded files are zip files. If they are, the files are unzipped,
                // and the appropriate properties are updated so that the unzipped files are
                // part of the uploads list.
                ServiceLocatorFactory.getFileAccessService().unzipFiles(uploads, uploadFileName);
                handleFiles();
            }
        } catch (RuntimeException re) {
            if (re.getCause() instanceof InvalidNumberOfArgsException) {
                InvalidNumberOfArgsException inv = (InvalidNumberOfArgsException) re.getCause();
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error." + inv.getMessage(), inv.getArguments()
                        .toArray(new String[inv.getArguments().size()])));
            } else {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.importingFile"));
            }
            arrayDesign.getDesignFiles().clear();
        } catch (InvalidDataFileException e) {
            LOG.debug("Swallowed exception saving array design file", e);
            FileValidationResult result = e.getFileValidationResult();
            for (ValidationMessage message : result.getMessages()) {
                addFieldError(UPLOAD_FIELD_NAME, message.getMessage());
            }
            arrayDesign.getDesignFiles().clear();
        } catch (IllegalAccessException iae) {
            LOG.debug("Swallowed exception saving array design file", iae);
            arrayDesign = ServiceLocatorFactory.getArrayDesignService().getArrayDesign(arrayDesign.getId());
            addActionError(iae.getMessage());
        } catch (Exception e) {
            LOG.debug("Swallowed exception saving array design file", e);
            if (arrayDesign.getId() != null) {
                arrayDesign = ServiceLocatorFactory.getArrayDesignService().getArrayDesign(arrayDesign.getId());
            }
            addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.importingFile"));
        } finally {
            // delete any files created as part of the unzipping process.
            // if the file uploaded was not a zip it will also be deleted
            if (uploads != null) {
                for (File f : uploads) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Action is used for an ajax call by the list generator.
     * 
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String generateAssayList() {
        setAssayTypes(new TreeSet<AssayType>(ServiceLocatorFactory.getProjectManagementService().getAssayTypes()));

        return "generateAssayList";
    }

    private void handleFiles() throws InvalidDataFileException, IllegalAccessException {
        // check file names against list of unsupported files
        for (String fileName : uploadFileName) {
            if (UnsupportedAffymetrixCdfFiles.isUnsupported(fileName)) {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.unsupportedFile"));
                return;
            }
        }

        // check user assigned file types and derived ext against known array designs
        int fileCount = 0;
        Map<String, File> arrayDesignFiles = new HashMap<String, File>();
        Map<String, FileType> arrayDesignFileTypes = new HashMap<String, FileType>();
        for (File file : uploads) {
            FileType userType = checkFileType(fileCount);
            FileType derivedType = checkFileExt(uploadFileName.get(fileCount));
            if (userType != null || derivedType != null) {

                arrayDesignFiles.put(uploadFileName.get(fileCount), file);

                if (userType != null) {
                    arrayDesignFileTypes.put(uploadFileName.get(fileCount), userType);
                } else {
                    arrayDesignFileTypes.put(uploadFileName.get(fileCount), derivedType);
                }

            }
            fileCount++;
        }

        doImport(arrayDesignFiles, arrayDesignFileTypes);
    }

    private FileType checkFileType(int index) {
        if (fileFormatTypes != null && !fileFormatTypes.isEmpty() && !"".equals(fileFormatTypes.get(index))
                && FileType.valueOf(fileFormatTypes.get(index)).isArrayDesign()) {

            return FileType.valueOf(fileFormatTypes.get(index));
        }

        return null;
    }

    private FileType checkFileExt(String filename) {
        FileType ft = FileExtension.getTypeFromExtension(filename);

        if (ft != null && ft.isArrayDesign()) {
            return ft;
        }

        return null;
    }

    private void doImport(Map<String, File> arrayDesignFiles, Map<String, FileType> arrayDesignFileTypes)
            throws InvalidDataFileException, IllegalAccessException {

        if (!arrayDesignFiles.isEmpty()) {
            CaArrayFileSet designFiles = new CaArrayFileSet();
            for (String fileName : arrayDesignFiles.keySet()) {
                CaArrayFile designFile = ServiceLocatorFactory.getFileAccessService().add(
                        arrayDesignFiles.get(fileName), fileName);

                designFile.setFileType(arrayDesignFileTypes.get(fileName));
                designFiles.add(designFile);

            }

            ServiceLocatorFactory.getFileManagementService().saveArrayDesign(arrayDesign, designFiles);
            ServiceLocatorFactory.getFileManagementService().importArrayDesignDetails(arrayDesign);

            // add error message for the user if there was an attempt to import non-array design files.
            if (uploads.size() > designFiles.getFiles().size()) {
                ActionHelper.saveMessage(getText("arrayDesign.warning.multiFile"));
            }

        } else {
            if (uploads.size() > 1) {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.multipleCannotDetermineType"));
            } else {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.cannotDetermineType"));
            }

        }
    }

    /**
     * Handles reimporting an array design that was previously imported-not-parsed but now can be parsed.
     * 
     * @return "list" 
     */
    public String reimport() {
        if (getArrayDesign() == null || getArrayDesign().getId() == null) {
            ActionHelper.saveMessage(getText("arrayDesign.noDesignSelected"));
            return SUCCESS;
        }

        try {
            ServiceLocatorFactory.getFileManagementService().reimportAndParseArrayDesign(getArrayDesign().getId());
            ActionHelper.saveMessage(getText("arrayDesign.importing"));
        } catch (IllegalAccessException e) {
            ActionHelper.saveMessage(getText("arrayDesign.cannotReimport"));            
        } catch (InvalidDataFileException e) {
            ActionHelper.saveMessage(getText("arrayDesign.invalid"));
            for (ValidationMessage vm : e.getFileValidationResult().getMessages()) {
                ActionHelper.saveMessage(vm.getMessage());
            }
        } catch (UnhandledException e) {
            ActionHelper.saveMessage(getText("arrayDesign.error.importingFile"));
            ActionHelper.saveMessage(e.getCause().getMessage());
        }

        return SUCCESS;
    }
}
