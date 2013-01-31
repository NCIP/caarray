//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.project.FileProcessingResult;
import gov.nih.nci.caarray.application.project.FileUploadUtils;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.fileupload.MonitoredMultiPartRequest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import java.util.Iterator;

/**
 * @author Scott Miller
 *
 */
@SuppressWarnings({"unchecked", "PMD.ExcessiveClassLength", "PMD.CyclomaticComplexity", "PMD.TooManyFields" })
@Validation
@Validations(expressions = @ExpressionValidator(message = "Files must be selected for this operation.",
        expression = "selectedFiles.size() > 0"))
public class ProjectFilesAction extends AbstractBaseProjectAction implements Preparable {
    private static final Logger LOG = Logger.getLogger(ProjectFilesAction.class);

    /**
     * The maximum allowable size of an import job (defined as sum of uncompressed sizes of files in the job).
     */
    public static final long MAX_IMPORT_TOTAL_SIZE = 3221225472L;

    /**
     * Object to keep count of errors and generate error messages.
     */
    private class ErrorCounts {
        private final Map<String, Integer> counts = new HashMap<String, Integer>();

        void incrementCount(String msgKey) {
            Integer count = counts.get(msgKey);
            counts.put(msgKey, (count == null) ? 1 : count + 1);
        }

        List<String> getMessages() {
            List<String> messages = new ArrayList<String>();
            for (String key : counts.keySet()) {
                messages.add(getText(key, new String[] {counts.get(key).toString() }));
            }
            return messages;
        }
    }

    /**
     * Maximum total uncompressed size (in bytes) of files that can be downloaded in a single ZIP. If files selected for
     * download have a greater combined size, then the user will be presented with a group download page.
     */
    public static final long MAX_DOWNLOAD_SIZE = 1024 * 1024 * 1536;

    private static final String UPLOAD_INPUT = "upload";
    private static final long serialVersionUID = 1L;
    private static final String ACTION_UNIMPORTED = "listUnimported";
    private static final String ACTION_IMPORTED = "listImported";
    private static final String ACTION_SUPPLEMENTAL = "listSupplemental";
    private static final String ACTION_TABLE = "table";

    private static final String UNKNOWN_FILE_TYPE = "(Unknown File Types)";
    private static final String KNOWN_FILE_TYPE = "(Supported File Types)";

    private static final String IMPORTED = "IMPORTED";
    private static final String VALIDATED = "VALIDATED";

    private static final String ID_PROPERTY = "id";
    private static final String TEXT_PROPERTY = "text";
    private static final String SORT_PROPERTY = "sort";
    private static final String NODE_TYPE_PROPERTY = "nodeType";
    private static final String LEAF_PROPERTY = "leaf";

    private List<Long> selectedFilesToUnpack = new ArrayList<Long>();
    private List<File> uploads = new ArrayList<File>();
    private List<String> uploadFileNames = new ArrayList<String>();
    private List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
    private Set<Long> selectedFileIds = new HashSet<Long>();
    private Set<CaArrayFile> files = new HashSet<CaArrayFile>();
    private String listAction;
    private String fileType;
    private String fileStatus;
    private String changeToFileType;
    private Set<String> fileTypes = new TreeSet<String>();
    private List<String> fileStatuses = new ArrayList<String>();
    private String nodeId;
    private ExperimentDesignTreeNodeType nodeType;
    private DataImportTargetAnnotationOption targetAnnotationOption;
    private List<Long> targetNodeIds = new ArrayList<Long>();
    private String newAnnotationName;
    private EnumMap<FileStatus, Integer> fileStatusCountMap = new EnumMap<FileStatus, Integer>(FileStatus.class);
    private boolean clearCheckboxes = true;

    private String prepListUnimportedPage() {
        if (clearCheckboxes) {
            this.selectedFileIds.clear();
        }
        setListAction(ACTION_UNIMPORTED);
        setFilesMatchingTypeAndStatus(getProject().getUnImportedFiles());
        setFileTypeNamesAndStatuses(getProject().getUnImportedFiles());
        setFileStatusCountMap(computeFileStatusCounts());
        return ACTION_UNIMPORTED;
    }

    private String prepListImportedPage() {
        setListAction(ACTION_IMPORTED);
        setFiles(new HashSet<CaArrayFile>());
        for (CaArrayFile f : getProject().getImportedFiles()) {
            if (getFileType() == null
                    || (f.getFileType() != null && f.getFileType().toString().equals(getFileType())
                            || (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null) || (UNKNOWN_FILE_TYPE
                            .equals(getFileType()) && f.getFileType() == null))) {
                getFiles().add(f);
            }
        }
        setFileTypeNamesAndStatuses(getProject().getImportedFiles());
        return ACTION_IMPORTED;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String downloadFiles() {
        setFilesMatchingTypeAndStatus(getProject().getFiles());
        setFileTypeNamesAndStatuses(getProject().getFiles());
        return Action.SUCCESS;
    }

    /**
     * display dowload options.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String downloadOptions() {
        downloadFiles();
        Iterator<String> it = getFileTypes().iterator();
        while (it.hasNext()) {
            String type = it.next();
            try {
                FileType t = FileType.valueOf(type);
                if (!t.isDerivedArrayData() && !t.isRawArrayData()) {
                    it.remove();
                }
            } catch (IllegalArgumentException e) {
                it.remove();
            }
        }
        return Action.SUCCESS;
    }

    private void setFilesMatchingTypeAndStatus(SortedSet<CaArrayFile> fileSet) {
        setFiles(new HashSet<CaArrayFile>());
        for (CaArrayFile f : fileSet) {
            boolean fileStatusMatch = (getFileStatus() == null || getFileStatus().equals(f.getFileStatus().name()));
            boolean fileTypeMatch = (getFileType() == null || (f.getFileType() != null
                    && f.getFileType().toString().equals(getFileType())
                    || (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null) || (UNKNOWN_FILE_TYPE
                    .equals(getFileType()) && f.getFileType() == null)));
            if (fileStatusMatch && fileTypeMatch) {
                getFiles().add(f);
            }
        }
    }

    private void setFileTypeNamesAndStatuses(SortedSet<CaArrayFile> fileSet) {
        Set<String> fileTypeNames = new TreeSet<String>();
        List<String> fileStatusList = new ArrayList<String>();
        for (CaArrayFile file : fileSet) {
            if (file.getFileType() != null) {
                fileTypeNames.add(file.getFileType().getName());
            }
            if (file.getStatus().contains(IMPORTED) && !fileStatusList.contains(IMPORTED)) {
                fileStatusList.add(IMPORTED);
            } else if (file.getStatus().contains(VALIDATED) && !fileStatusList.contains(VALIDATED)) {
                fileStatusList.add(VALIDATED);
            } else if (!fileStatusList.contains(file.getStatus())) {
                fileStatusList.add(file.getStatus());
            }
        }
        fileTypeNames.add(KNOWN_FILE_TYPE);
        fileTypeNames.add(UNKNOWN_FILE_TYPE);
        setFileTypes(fileTypeNames);
        setFileStatuses(fileStatusList);
    }

    private String prepListSupplementalPage() {
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
    public String listUnimported() {
        return prepListUnimportedPage();
    }

    /**
     * Method to get the list of files while leaving checkboxes checked.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listUnimportedWithoutClearingCheckboxes() {
        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listUnimportedForm() {
        prepListUnimportedPage();
        return "listUnimportedForm";
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listImportedForm() {
        prepListImportedPage();
        return "listImportedForm";
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listUnimportedTable() {
        prepListUnimportedPage();
        return ACTION_TABLE;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listImported() {
        return prepListImportedPage();
    }

    /**
     * Method to get the list of supplemental files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listSupplemental() {
        return prepListSupplementalPage();
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listImportedTable() {
        prepListImportedPage();
        return ACTION_TABLE;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listSupplementalTable() {
        prepListSupplementalPage();
        return ACTION_TABLE;
    }

    /**
     * Ajax-only call to handle changing the filter extension.
     *
     * @return success.
     */
    @SkipValidation
    public String downloadFilesList() {
        return downloadFiles();
    }

    /**
     * Ajax-only call to handle sorting.
     *
     * @return success
     */
    @SkipValidation
    public String downloadFilesListTable() {
        return downloadFiles();
    }

    /**
     * Method to delete files.
     *
     * @return the string representing the UI to display.
     */
    public String deleteFiles() {
        doFileDeletion();
        return prepListUnimportedPage();
    }

    /**
     * Method to delete files that have been imported.
     *
     * @return the string representing the UI to display.
     */
    public String deleteImportedFiles() {
        doFileDeletion();
        return prepListImportedPage();
    }

    /**
     * Method to unpack files.
     *
     * @return the string representing the UI to display.
     */
    public String unpackFiles() {
        try {
            FileProcessingResult result = FileUploadUtils.unpackFiles(getProject(), this.getSelectedFiles());

            for (String conflict : result.getConflictingFiles()) {
                ActionHelper.saveMessage(getText("experiment.files.unpack.filename.exists", new String[] {conflict }));
            }
            ActionHelper.saveMessage(result.getCount() + " file(s) unpacked.");
        } catch (InvalidFileException ue) {
            ActionHelper.saveMessage(getText("errors.unpackingErrorWithZip",
                    new String[] {ue.getFile(), getText(ue.getResourceKey())}));
        } catch (Exception e) {
            String msg = "Unable to unpack file: " + e.getMessage();
            LOG.error(msg, e);
            ActionHelper.saveMessage(getText("errors.unpacking"));
        }

        return prepListUnimportedPage();
    }

    /**
     * Method to delete supplemental files.
     *
     * @return the string representing the UI to display.
     */
    public String deleteSupplementalFiles() {
        doFileDeletion();
        return prepListSupplementalPage();
    }

    private void doFileDeletion() {
        int deletedFiles = 0;
        int skippedFiles = 0;
        for (CaArrayFile caArrayFile : getSelectedFiles()) {
            if (caArrayFile.isDeletable()) {
                ServiceLocatorFactory.getFileAccessService().remove(caArrayFile);
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
    public String editFiles() {
        return Action.SUCCESS;
    }

    /**
     * Save the selected files.
     *
     * @return the string matching the result to follow
     */
    public String saveFiles() {
        if (!getSelectedFiles().isEmpty()) {
            for (CaArrayFile caArrayFile : getSelectedFiles()) {
                caArrayFile.setFileStatus(FileStatus.UPLOADED);
                if (caArrayFile.getValidationResult() != null) {
                    caArrayFile.getValidationResult().getMessageSet().clear();
                }
                ServiceLocatorFactory.getFileAccessService().save(caArrayFile);
            }
            ActionHelper.saveMessage(getSelectedFiles().size() + " file(s) updated.");
        }
        return prepListUnimportedPage();
    }

    /**
     * Save the selected files with a new file type.
     *
     * @return the string matching the result to follow
     */
    public String changeFileType() {
        if (!getSelectedFiles().isEmpty()) {
            for (CaArrayFile caArrayFile : getSelectedFiles()) {
                caArrayFile.setFileType(FileType.valueOf(this.getChangeToFileType()));
            }
        }
        return saveFiles();
    }

    /**
     * Method to validate the files.
     *
     * @return the string matching the result to follow
     */
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    // validation checks can't be easily refactored to smaller methods.
    public String validateFiles() {
        ErrorCounts errors = new ErrorCounts();
        boolean includesSdrf = includesType(getSelectedFiles(), FileType.MAGE_TAB_SDRF);
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (CaArrayFile file : getSelectedFiles()) {
            if (file.getFileType() == null) {
                errors.incrementCount("project.fileValidate.error.unknownType");
            } else if (file.getFileType().isArrayDesign()) {
                errors.incrementCount("project.fileValidate.error.arrayDesign");
            } else if (!includesSdrf && !file.getFileType().isParseableData()) {
                errors.incrementCount("project.fileValidate.error.unparseableFiles");
            } else if (file.getFileStatus().isValidatable()) {
                fileSet.add(file);
            } else {
                errors.incrementCount("project.fileValidate.error.invalidStatus");
            }
        }
        if (!fileSet.getFiles().isEmpty()) {
            ServiceLocatorFactory.getFileManagementService().validateFiles(getProject(), fileSet);
        }
        ActionHelper.saveMessage(getText("project.fileValidate.success", new String[] {String.valueOf(fileSet
                .getFiles().size()) }));
        for (String msg : errors.getMessages()) {
            ActionHelper.saveMessage(msg);
        }
        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    /**
     * Method to select all files that ref an sdrf.
     *
     * @return the string matching the result to follow
     */
    public String findRefFiles() {
        // we are only interested in an IDF selected and all SDRFs in the project files

        // check that there is only 1 file and that it is the idf file
        if (getSelectedFiles().size() > 1) {
            ActionHelper.saveMessage(getText("project.selectRefFile.error.moreThanOneFile"));
        } else if (!getSelectedFiles().get(0).getFileType().equals(FileType.MAGE_TAB_IDF)) {
            ActionHelper.saveMessage(getText("project.selectRefFile.error.notIdf"));
        } else {
            generateRefFileList(getSelectedFiles().get(0));
        }

        ActionHelper.saveMessage(getText("project.selectRefFile.success", new String[] {String.valueOf(selectedFiles
                .size()) }));

        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    private void generateRefFileList(CaArrayFile idfFile) {

        this.selectedFiles.clear();

        if (idfFile != null) {
            selectedFiles.add(idfFile);
            // find files ref'ing sdrf file.
            List<String> filenames = ServiceLocatorFactory.getFileManagementService().findIdfRefFileNames(idfFile,
                    getProject());
            if (!filenames.isEmpty() && validateReferencedFilesPresent(filenames)) {
                findFilesByName(filenames);
                boolean addErrorMessage = false;
                for (CaArrayFile caf : selectedFiles) {
                    if (!selectedFileIds.contains(caf.getId())) {
                        selectedFileIds.add(caf.getId());
                    }
                    if (caf.getValidationResult() != null && !caf.getValidationResult().isValid()) {
                        addErrorMessage = true;
                    }
                }

                if (addErrorMessage) {
                    ActionHelper.saveMessage(getText("project.selectRefFile.error.validation"));
                }
            }
        }
    }

    private boolean validateReferencedFilesPresent(final List<String> referencedFileNames) {
        boolean noFilesAreMissing = true;
        Set<String> fileNames = getSetOfCaArrayFileNames();
        for (String referencedFileName : referencedFileNames) {
            if (!fileNames.contains(referencedFileName)) {
                noFilesAreMissing = false;
                ActionHelper.saveMessage(getText("project.selectRefFile.error.validation.missingFile",
                    new String[] {"'" + referencedFileName + "'"}));
            }
        }
        return noFilesAreMissing;
    }

    private Set<String> getSetOfCaArrayFileNames() {
        Set<String> fileNames = new HashSet<String>();
        for (CaArrayFile caArrayFile : getProject().getFiles()) {
            fileNames.add(caArrayFile.getName());
        }
        return fileNames;
    }

    private void findFilesByName(List<String> thesefiles) {
        for (CaArrayFile caf : getProject().getFiles()) {
            if (thesefiles.contains(caf.getName())) {
                selectedFiles.add(caf);
            }
        }
    }

    private boolean includesType(List<CaArrayFile> fileList, FileType type) {
        for (CaArrayFile file : fileList) {
            if (type.equals(file.getFileType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * AJAX call to determine if all selected files can be imported.
     *
     * @return null
     */
    @SkipValidation
    public String validateSelectedImportFiles() {
        ErrorCounts errors = new ErrorCounts();
        CaArrayFileSet fileSet = checkImportFiles(errors);
        try {
            JSONObject json = new JSONObject();
            List<String> errorMsgs = errors.getMessages();
            if (errorMsgs.isEmpty()) {
                json.element("validated", true);
            } else {
                StringBuffer buffer = new StringBuffer();
                for (String msg : errorMsgs) {
                    buffer.append(msg).append('\n');
                }
                buffer.append("Would you like to continue importing the remaining " + fileSet.getFiles().size()
                        + " file(s)?");
                json.element("confirmMessage", buffer.toString());
            }
            ServletActionContext.getResponse().getWriter().write(json.toString());
        } catch (IOException e) {
            LOG.error("unable to write response", e);
        }
        return null;
    }

    /**
     * Method to import the files.
     *
     * @return the string matching the result to follow
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    @Validations(expressions = {
            @ExpressionValidator(message = "You must select at least one biomaterial or hybridization.",
                    expression = "targetNodeIds.size() > 0 || targetAnnotationOption != "
                    + "@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@ASSOCIATE_TO_NODES"),
            @ExpressionValidator(message = "You must enter a new annotation name.",
                    expression = "newAnnotationName != null && "
                    + " newAnnotationName.length() > 0 || targetAnnotationOption != "
                    + "@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@AUTOCREATE_SINGLE"
                    ) })
    public String importFiles() {
        ErrorCounts errors = new ErrorCounts();
        CaArrayFileSet fileSet = checkImportFiles(errors);
        if (fileSet.getTotalUncompressedSize() > MAX_IMPORT_TOTAL_SIZE) {
            ActionHelper.saveMessage(getText("project.fileImport.error.jobTooLarge"));
        } else {
            if (!fileSet.getFiles().isEmpty()) {
                List<Long> entityIds = new ArrayList<Long>(this.targetNodeIds);
                ExperimentDesignNodeType targetNodeType = (this.nodeType == null ? null : this.nodeType.getNodeType());
                DataImportOptions dataImportOptions = DataImportOptions.getDataImportOptions(
                        this.targetAnnotationOption, this.newAnnotationName, targetNodeType, entityIds);
                ServiceLocatorFactory.getFileManagementService().importFiles(getProject(), fileSet, dataImportOptions);
            }
            ActionHelper.saveMessage(getText("project.fileImport.success", new String[] {String.valueOf(fileSet
                    .getFiles().size()) }));
            for (String msg : errors.getMessages()) {
                ActionHelper.saveMessage(msg);
            }
            refreshProject();
        }
        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    /**
     * Checks on which of the selected files can be imported, and stores counts of those that cannot be.
     *
     * @param errors object that stores the error counts
     * @return the set of importable files
     */
    private CaArrayFileSet checkImportFiles(ErrorCounts errors) {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (CaArrayFile file : getSelectedFiles()) {
            if (file.getFileType() == null) {
                errors.incrementCount("project.fileImport.error.unknownType");
            } else if (file.getFileType().isArrayDesign()) {
                errors.incrementCount("project.fileImport.error.arrayDesign");
            } else if (file.getFileStatus().isImportable()) {
                fileSet.add(file);
            } else {
                errors.incrementCount("project.fileImport.error.invalidStatus");
            }
        }
        return fileSet;
    }

    /**
     * Adds supplemental data files to the system.
     *
     * @return the string matching the result to follow
     */
    public String addSupplementalFiles() {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (CaArrayFile file : getSelectedFiles()) {
            fileSet.add(file);
        }
        if (!fileSet.getFiles().isEmpty()) {
            ServiceLocatorFactory.getFileManagementService().addSupplementalFiles(getProject(), fileSet);
        }
        ActionHelper.saveMessage(fileSet.getFiles().size() + " supplemental file(s) added to project.");
        refreshProject();
        return prepListUnimportedPage();
    }

    /**
     * This method refreshes the project from the db. It is in its own method to allow test cases to overwrite this.
     */
    protected void refreshProject() {
        HibernateUtil.getCurrentSession().refresh(getProject());
    }

    /**
     * View the validation messages for the selected files.
     *
     * @return the string matching the result to use.
     */
    public String validationMessages() {
        return Action.SUCCESS;
    }

    /**
     * uploads file.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String upload() {
        if (validateUpload()) {
            try {
                FileProcessingResult uploadResult = FileUploadUtils.uploadFiles(getProject(), getUpload(),
                        getUploadFileName(), fileNamesToUnpack());

                for (String conflict : uploadResult.getConflictingFiles()) {
                    ActionHelper.saveMessage(getText("experiment.files.upload.filename.exists",
                            new String[] {conflict }));
                }
                ActionHelper.saveMessage(uploadResult.getCount() + " file(s) uploaded.");
            } catch (InvalidFileException ue) {
                String errorKey = fileNamesToUnpack().contains(ue.getFile()) ? "errors.uploadingErrorWithZip"
                        : "errors.uploadingErrorWithAdding";
                ActionHelper
                        .saveMessage(getText(errorKey, new String[] {ue.getFile(), getText(ue.getResourceKey()) }));
                ActionHelper.saveMessage(getText("errors.unpackingErrorWithZip", new String[] {ue.getFile(),
                        getText(ue.getResourceKey()) }));
            } catch (Exception e) {
                String msg = "Unable to upload file: " + e.getMessage();
                LOG.error(msg, e);
                ActionHelper.saveMessage(getText("errors.uploading"));
            } finally {
                MonitoredMultiPartRequest.releaseProgressMonitor(ServletActionContext.getRequest());
            }
        }
        return UPLOAD_INPUT;
    }

    /**
     * Implements file download. Writes a zip of the selected files to the servlet output stream
     *
     * @return null - the result is written to the servlet output stream
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String download() throws IOException {
        if (this.checkInconsistentFileSelect()) {
            if (getProject().hasReadPermission(getCsmUser())) {
                ActionHelper.saveMessage(getText("download.session.expired"));
            }

            return "denied";
        }

        return downloadArchive(getProject(), getSelectedFiles());
    }

    /**
     * This method will download a group of files if the group number is specified or if there is only one download
     * group.
     *
     * @param project the project
     * @param contentFiles all selected files
     * @return downloadGroups if no group number is specified and there are multiple groups
     * @throws IOException if there is an error writing to the stream
     */
    protected String downloadArchive(Project project, Collection<CaArrayFile> contentFiles) throws IOException {
        StringBuilder baseName = determineDownloadFileName(project);
        if (getFileType() != null) {
            baseName.append('-').append(getFileType());
        }
        if (getFileStatus() != null) {
            baseName.append('-').append(getFileStatus());
        }
        DownloadHelper.downloadFiles(contentFiles, baseName.toString());
        return null;

    }

    /**
     * Download an archive containg files filterd by fileType property.
     * @return null
     * @throws IOException if there is an error writing to the stream.
     */
    @SkipValidation
    public String downloadByType() throws IOException {
        downloadFiles();
        getSelectedFiles().addAll(getFiles());
        return downloadArchive(getProject(), getSelectedFiles());
    }

    /**
     * Returns the filename for a zip of files for the given project, assuming that the download will not be grouped.
     *
     * @param project the project whose files are downloaded
     * @return the filename
     */
    public static StringBuilder determineDownloadFileName(Project project) {
        StringBuilder name = new StringBuilder("caArray_").append(project.getExperiment().getPublicIdentifier())
                .append("_files");
        return name;
    }

    /**
     * validates user permissions and required file for upload.
     *
     * @return true if validation passes
     */
    private boolean validateUpload() {
        if (!validatePermissions()) {
            return false;
        }

        List<String> fileNames = getUploadFileName();
        if (fileNames == null || fileNames.isEmpty()) {
            ActionHelper.saveMessage(getText("fileRequired"));
            return false;
        }
        return true;
    }

    /**
     * validates user permissions.
     *
     * @return true if validation passes
     */
    private boolean validatePermissions() {
        if (UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            ActionHelper.saveMessage(getText("upload.session.expired"));
            return false;
        }
        if (!getProject().hasWritePermission(getCsmUser())) {
            ActionHelper.saveMessage(getText("project.permissionDenied", new String[] {getText("role.write") }));
            return false;
        }
        return true;
    }

    /**
     * Calculates and returns the JSON for the nodes that are the children of the passed in node. in the experiment tree
     *
     * @return null - the JSON is written directly to the response stream
     */
    @SkipValidation
    public String importTreeNodesJson() {
        try {
            JSONArray jsArray = new JSONArray();

            if (this.nodeType == ExperimentDesignTreeNodeType.ROOT) {
                addJsonForExperimentRoots(jsArray);
            } else if (this.nodeType.isExperimentRootNode()) {
                addJsonForExperimentDesignNodes(jsArray, this.nodeType.getChildrenNodeType(), this.nodeType
                        .getContainedNodes(getExperiment()), this.nodeId);
            } else if (this.nodeType.isBiomaterialRootNode()) {
                // the node id of an associated biomaterials container node will end in [number]_[type]
                // where [type] is the container type and [number] is the id of the biomaterial parent
                Long biomaterialParentId = Long.parseLong(StringUtils.substringAfterLast(StringUtils
                        .substringBeforeLast(this.nodeId, "_"), "_"));
                AbstractBioMaterial bioMaterialParent = ServiceLocatorFactory.getGenericDataService()
                        .getPersistentObject(AbstractBioMaterial.class, biomaterialParentId);
                addJsonForExperimentDesignNodes(jsArray, this.nodeType.getChildrenNodeType(), this.nodeType
                        .getContainedNodes(bioMaterialParent), this.nodeId);
            } else if (this.nodeType.isBiomaterialNode()) {
                // note that we should never get requested for biomaterial or hybrdization nodes
                // since they are returned with their children already or marked as leaves
                throw new IllegalStateException("Unsupported node type" + this.nodeType);
            }

            ServletActionContext.getResponse().getWriter().write(jsArray.toString());
        } catch (IOException e) {
            LOG.error("unable to write response", e);
        }
        return null;
    }

    private void addJsonForExperimentRoots(JSONArray jsonArray) {
        JSONObject json = new JSONObject();

        json.element(ID_PROPERTY, "Sources");
        json.element(TEXT_PROPERTY, "Sources");
        json.element(SORT_PROPERTY, "1");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.EXPERIMENT_SOURCES);
        json.element(LEAF_PROPERTY, getExperiment().getSources().isEmpty());
        jsonArray.element(json);

        json = new JSONObject();
        json.element(ID_PROPERTY, "Samples");
        json.element(TEXT_PROPERTY, "Samples");
        json.element(SORT_PROPERTY, "2");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.EXPERIMENT_SAMPLES);
        json.element(LEAF_PROPERTY, getExperiment().getSamples().isEmpty());
        jsonArray.element(json);

        json = new JSONObject();
        json.element(ID_PROPERTY, "Extracts");
        json.element(TEXT_PROPERTY, "Extracts");
        json.element(SORT_PROPERTY, "3");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.EXPERIMENT_EXTRACTS);
        json.element(LEAF_PROPERTY, getExperiment().getExtracts().isEmpty());
        jsonArray.element(json);

        json = new JSONObject();
        json.element(ID_PROPERTY, "LabeledExtracts");
        json.element(TEXT_PROPERTY, "Labeled Extracts");
        json.element(SORT_PROPERTY, "4");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.EXPERIMENT_LABELED_EXTRACTS);
        json.element(LEAF_PROPERTY, getExperiment().getLabeledExtracts().isEmpty());
        jsonArray.element(json);

        json = new JSONObject();
        json.element(ID_PROPERTY, "Hybridizations");
        json.element(TEXT_PROPERTY, "Hybridizations");
        json.element(SORT_PROPERTY, "5");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.EXPERIMENT_HYBRIDIZATIONS);
        json.element(LEAF_PROPERTY, getExperiment().getHybridizations().isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForBiomaterialSamplesRoot(JSONArray jsonArray, AbstractBioMaterial parent,
            String nodeIdPrefix) {
        JSONObject json = new JSONObject();
        json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_Samples");
        json.element(TEXT_PROPERTY, "Associated Samples");
        json.element(SORT_PROPERTY, "2");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_SAMPLES);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_SAMPLES.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForBiomaterialExtractsRoot(JSONArray jsonArray, AbstractBioMaterial parent,
            String nodeIdPrefix) {
        JSONObject json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_Extracts");
        json.element(TEXT_PROPERTY, "Associated Extracts");
        json.element(SORT_PROPERTY, "3");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForBiomaterialLabeledExtractsRoot(JSONArray jsonArray, AbstractBioMaterial parent,
            String nodeIdPrefix) {
        JSONObject json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_LabeledExtracts");
        json.element(TEXT_PROPERTY, "Associated Labeled Extracts");
        json.element(SORT_PROPERTY, "4");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_LABELED_EXTRACTS);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_LABELED_EXTRACTS.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForBiomaterialHybridizationsRoot(JSONArray jsonArray, AbstractBioMaterial parent,
            String nodeIdPrefix) {
        JSONObject json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_Hybridizations");
        json.element(TEXT_PROPERTY, "Associated Hybridizations");
        json.element(SORT_PROPERTY, "5");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_HYBRIDIZATIONS);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_HYBRIDIZATIONS.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForExperimentDesignNodes(JSONArray jsonArray, ExperimentDesignTreeNodeType newNodesType,
            Collection<? extends AbstractExperimentDesignNode> nodes, String nodeIdPrefix) {
        for (AbstractExperimentDesignNode node : nodes) {
            JSONObject json = new JSONObject();
            String newNodeId = nodeIdPrefix + "_" + node.getId();
            json.element(ID_PROPERTY, newNodeId);
            json.element("entityId", node.getId());
            json.element(TEXT_PROPERTY, node.getName());
            json.element(SORT_PROPERTY, node.getName());
            json.element(NODE_TYPE_PROPERTY, newNodesType);
            json.element("iconCls", newNodesType.name().toLowerCase(Locale.getDefault()) + "_node");
            json.element("checked", false);

            JSONArray associatedRoots = new JSONArray();
            if (newNodesType == ExperimentDesignTreeNodeType.SOURCE) {
                addJsonForBiomaterialSamplesRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (EnumSet.of(ExperimentDesignTreeNodeType.SOURCE, ExperimentDesignTreeNodeType.SAMPLE).contains(
                    newNodesType)) {
                addJsonForBiomaterialExtractsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (EnumSet.range(ExperimentDesignTreeNodeType.SOURCE, ExperimentDesignTreeNodeType.EXTRACT).contains(
                    newNodesType)) {
                addJsonForBiomaterialLabeledExtractsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (newNodesType.isBiomaterialNode()) {
                addJsonForBiomaterialHybridizationsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }

            if (associatedRoots.isEmpty()) {
                json.element("leaf", true);
            } else {
                json.element("children", associatedRoots);
            }
            jsonArray.element(json);
        }
    }

    /**
     * Action for displaying the upload in background form.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String uploadInBackground() {
        if (getProject().isImportingData()) {
            ActionHelper.saveMessage(getText("project.inconsistentState.importing_files"));
        }
        return "uploadInBackground";
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
     * returns uploaded file name.
     *
     * @return uploadFileNames
     */
    public List<String> getUploadFileName() {
        return this.uploadFileNames;
    }

    /**
     * sets uploaded file names.
     *
     * @param inUploadFileNames List
     */
    public void setUploadFileName(List<String> inUploadFileNames) {
        this.uploadFileNames = inUploadFileNames;
    }

    /**
     * @return the selectedFiles
     */
    public List<CaArrayFile> getSelectedFiles() {
        return this.selectedFiles;
    }

    /**
     * @param selectedFiles the selectedFiles to set
     */
    public void setSelectedFiles(List<CaArrayFile> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    /**
     * @return the selected file ids
     */
    public Set<Long> getSelectedFileIds() {
        return selectedFileIds;
    }

    /**
     * @param selectedFileIds the ids of the selected files
     */
    public void setSelectedFileIds(Set<Long> selectedFileIds) {
        this.selectedFileIds = selectedFileIds;
        this.selectedFiles = ServiceLocatorFactory.getGenericDataService().retrieveByIds(CaArrayFile.class,
                new ArrayList(selectedFileIds));
    }

    /**
     * @return the files
     */
    public Set<CaArrayFile> getFiles() {
        return this.files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(Set<CaArrayFile> files) {
        this.files = files;
    }

    /**
     * @return the listAction
     */
    public String getListAction() {
        return this.listAction;
    }

    /**
     * @param listAction the listAction to set
     */
    public void setListAction(String listAction) {
        this.listAction = listAction;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return this.fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the changeToFileType
     */
    public String getChangeToFileType() {
        return this.changeToFileType;
    }

    /**
     * @param changeToFileType new file type to set
     */
    public void setChangeToFileType(String changeToFileType) {
        this.changeToFileType = changeToFileType;
    }

    /**
     * @return the fileTypes
     */
    public Set<String> getFileTypes() {
        return this.fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(Set<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (hasErrors()) {
            // crappy, but see no other way
            String actionName = ActionContext.getContext().getName();
            if (actionName.contains("Supplemental")) {
                prepListSupplementalPage();
            } else if (actionName.contains("Imported")) {
                prepListImportedPage();
            } else if (actionName.contains("Unimported")) {
                // prevent a refresh or re-sort from failing
                // due to lack of selected files.
                this.clearErrorsAndMessages();
            } else {
                prepListUnimportedPage();
            }
        }
    }

    /**
     * Returns the names of the files selected to be unpacked.
     *
     * @return fileNamesToUnpack
     *
     */
    private List<String> fileNamesToUnpack() {
        List<String> fileNamesToUnpack = null;

        if (this.getSelectedFilesToUnpack() != null && this.getSelectedFilesToUnpack().size() > 1) {
            fileNamesToUnpack = new ArrayList();
            for (Long unpackIndex : this.getSelectedFilesToUnpack()) {
                if (unpackIndex.intValue() == -1) {
                    continue;
                }
                fileNamesToUnpack.add(this.uploadFileNames.get(unpackIndex.intValue()));
            }
        }
        return fileNamesToUnpack;
    }

    /**
     * Verify that all files selected by their ids are retrieved.
     */
    private boolean checkInconsistentFileSelect() {
        // most likely there was a session timeout which resulted in the user becoming anon
        // have the user log in again.
        // this will make the validation fail and the user will be kicked back to the
        // overview screen. but because the user is anon, they will have to log in again.
        return (this.selectedFiles.size() != this.selectedFileIds.size());
    }

    /**
     * @return the selectedFilesToUnpack
     */
    public List<Long> getSelectedFilesToUnpack() {
        return selectedFilesToUnpack;
    }

    /**
     * @param selectedFilesToUnpack the selectedFilesToUnpack to set
     */
    public void setSelectedFilesToUnpack(List<Long> selectedFilesToUnpack) {
        this.selectedFilesToUnpack = selectedFilesToUnpack;
    }

    /**
     * @return the nodeId
     */
    public String getNode() {
        return nodeId;
    }

    /**
     * @param node the node to set
     */
    public void setNode(String node) {
        this.nodeId = node;
    }

    /**
     * @return the nodeType
     */
    public ExperimentDesignTreeNodeType getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType the nodeType to set
     */
    public void setNodeType(ExperimentDesignTreeNodeType nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the targetAnnotationOption
     */
    public DataImportTargetAnnotationOption getTargetAnnotationOption() {
        return targetAnnotationOption;
    }

    /**
     * @param targetAnnotationOption the targetAnnotationOption to set
     */
    public void setTargetAnnotationOption(DataImportTargetAnnotationOption targetAnnotationOption) {
        this.targetAnnotationOption = targetAnnotationOption;
    }

    /**
     * @return the targetBiomaterialIds
     */
    public List<Long> getTargetNodeIds() {
        return targetNodeIds;
    }

    /**
     * @param targetNodeIds the targetNodeIds to set
     */
    public void setTargetNodeIds(List<Long> targetNodeIds) {
        this.targetNodeIds = targetNodeIds;
    }

    /**
     * @return the newAnnotationName
     */
    public String getNewAnnotationName() {
        return newAnnotationName;
    }

    /**
     * @param newAnnotationName the newAnnotationName to set
     */
    public void setNewAnnotationName(String newAnnotationName) {
        this.newAnnotationName = newAnnotationName;
    }

    /**
     * Computes a file status count for each type of file status.
     *
     * @return Map, map contains key value pair (status, count)
     */
    public EnumMap<FileStatus, Integer> computeFileStatusCounts() {
        EnumMap<FileStatus, Integer> countMap = new EnumMap<FileStatus, Integer>(FileStatus.class);

        for (FileStatus f : FileStatus.values()) {
            countMap.put(f, 0);
        }

        for (CaArrayFile f : getFiles()) {
            countMap.put(FileStatus.valueOf(f.getStatus()), countMap.get(FileStatus.valueOf(f.getStatus())) + 1);
        }
        return countMap;
    }

    /**
     * @return the fileStatus
     */
    public String getFileStatus() {
        return fileStatus;
    }

    /**
     * @param fileStatus the fileStatus to set
     */
    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    /**
     * @return the fileStatuses
     */
    public List<String> getFileStatuses() {
        return fileStatuses;
    }

    /**
     * @param fileStatuses the fileStatuses to set
     */
    public void setFileStatuses(List<String> fileStatuses) {
        this.fileStatuses = fileStatuses;
    }

    /**
     * @return the fileStatusCountMap
     */
    public EnumMap<FileStatus, Integer> getFileStatusCountMap() {
        return fileStatusCountMap;
    }

    /**
     * @param fileStatusCountMap the fileStatusCountMap to set
     */
    public void setFileStatusCountMap(EnumMap<FileStatus, Integer> fileStatusCountMap) {
        this.fileStatusCountMap = fileStatusCountMap;
    }

}
