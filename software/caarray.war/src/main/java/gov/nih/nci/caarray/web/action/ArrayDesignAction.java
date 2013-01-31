//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getArrayDesignService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileAccessService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getFileManagementService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getVocabularyService;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignDeleteException;
import gov.nih.nci.caarray.application.fileaccess.FileExtension;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author Winston Cheng
 *
 */
@Validation
@SuppressWarnings("PMD.CyclomaticComplexity")
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
    private boolean editMode;
    private boolean locked;
    private boolean createMode;

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
     * {@inheritDoc}
     */
    public void prepare() {
        this.organisms = getVocabularyService().getOrganisms();
        this.providers = getArrayDesignService().getAllProviders();
        this.featureTypes = CaArrayActionHelper.getTermsFromCategory(ExperimentOntologyCategory.TECHNOLOGY_TYPE);
        if (arrayDesign != null && arrayDesign.getId() != null) {
            ArrayDesign retrieved = getArrayDesignService().getArrayDesign(arrayDesign.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(getArrayDesign(),
                        SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
            } else {
                arrayDesign = retrieved;
            }
            locked = getArrayDesignService().isArrayDesignLocked(arrayDesign.getId());
        }
    }

    /**
     * Retrieves the list of all array designs.
     * @return list
     */
    @SkipValidation
    public String list() {
        arrayDesigns = getArrayDesignService().getArrayDesigns();
        return "list";
    }

    /**
     * Edit view of an array design.
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
     * @return success
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Validations(
        requiredStrings = {
            @RequiredStringValidator(fieldName = "arrayDesign.version", key = "errors.required", message = "")
        },
        requiredFields = {
            @RequiredFieldValidator(fieldName = "arrayDesign.assayType", key = "errors.required", message = ""),
            @RequiredFieldValidator(fieldName = "arrayDesign.provider", key = "errors.required", message = ""),
            @RequiredFieldValidator(fieldName = "arrayDesign.technologyType", key = "errors.required", message = ""),
            @RequiredFieldValidator(fieldName = "arrayDesign.organism", key = "errors.required", message = "")
        }
    )
    public String saveMeta() {
        if (!createMode && editMode) {

            if (getArrayDesignService().isDuplicate(arrayDesign)) {
                List<String> args = new ArrayList<String>();
                args.add(getArrayDesign().getName());
                ActionHelper.saveMessage(getText("arraydesign.duplicate", args));
                return Action.INPUT;
            }

            saveImportFile();
            List<String> args = new ArrayList<String>();
            args.add(getArrayDesign().getName());
            args.add(getArrayDesign().getProvider().getName());
            ActionHelper.saveMessage(getText("arraydesign.saved", args));
            return Action.SUCCESS;
        }
        editMode = true;
        return "metaValid";
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
            ActionHelper.saveMessage(getText("arrayDesign.noDataToDownload"));
            return "list";
        }

        DownloadHelper.downloadFiles(files, determineDownloadFileName());
        return null;
    }


   /**
    * Returns the filename for a zip of files for the given project, assuming that the download will not be grouped.
    *
    * @return the filename
    */
   private String determineDownloadFileName() {
       StringBuilder name = new StringBuilder("caArray_").append(arrayDesign.getName())
               .append("_file.zip");
       return name.toString();
   }

    /**
     * {@inheritDoc}
     */
    protected Collection<CaArrayFile> getAllDataFiles() {
        Collection<CaArrayFile> returnVal = null;
        if (getArrayDesign() != null) {
            returnVal = getArrayDesign().getDesignFiles();
        } else {
            returnVal = new ArrayList<CaArrayFile>();
        }

        return returnVal;
    }

    /**
     * Save/Update file associated with array design.
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
            getArrayDesignService().deleteArrayDesign(getArrayDesign());
            ActionHelper.saveMessage(getText("arrayDesign.deletionSuccess",
                    new String[] {getArrayDesign().getName() }));
        } catch (ArrayDesignDeleteException e) {
            ActionHelper.saveMessage(e.getMessage());
        }
        arrayDesigns = getArrayDesignService().getArrayDesigns();
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

    private void saveImportFile() {
        try {
            // figure out if we are editing or creating.
            if (arrayDesign.getId() != null && (uploadFileName == null || uploadFileName.isEmpty())) {
                getArrayDesignService().saveArrayDesign(arrayDesign);
            } else {
                // Checks if any uploaded files are zip files. If they are, the files are unzipped,
                // and the appropriate properties are updated so that the unzipped files are
                // part of the uploads list.
                getFileAccessService().unzipFiles(uploads, uploadFileName);
                handleFiles();
            }
        } catch (RuntimeException re) {
            if (re.getCause() instanceof InvalidNumberOfArgsException) {
                InvalidNumberOfArgsException inv = (InvalidNumberOfArgsException) re.getCause();
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error." + inv.getMessage(), inv.getArguments()));
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
            arrayDesign = getArrayDesignService().getArrayDesign(arrayDesign.getId());
            addActionError(iae.getMessage());
        } catch (Exception e) {
            LOG.debug("Swallowed exception saving array design file", e);
            if (arrayDesign.getId() != null) {
                arrayDesign = getArrayDesignService().getArrayDesign(arrayDesign.getId());
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
                CaArrayFile designFile = getFileAccessService().add(arrayDesignFiles.get(fileName), fileName);

                designFile.setFileType(arrayDesignFileTypes.get(fileName));
                designFiles.add(designFile);

            }

            getFileManagementService().saveArrayDesign(arrayDesign, designFiles);

            // even if some of the file could not be parsed still import the array design details
            for (CaArrayFile designFile : designFiles.getFiles()) {
                if (!FileStatus.IMPORTED_NOT_PARSED.equals(designFile.getFileStatus())) {
                    getFileManagementService().importArrayDesignDetails(arrayDesign);
                    break;
                }
            }

              // add error message for the user if there was an attempt to import non-array design files.
             if (uploads.size() > designFiles.getFiles().size()) {
                 ActionHelper.saveMessage(getText("arrayDesign.warning.multiFile"));
             }

         } else {
           if (uploads.size() > 1) {
               addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.multipleUnsupportedFiles"));
           } else {
               addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.unsupportedFile"));
           }

         }
    }
}
