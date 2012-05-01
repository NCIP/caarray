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
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Acton Class that handles download, import, and delete interactions with
 * files.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CyclomaticComplexity", "PMD.TooManyFields", "PMD.TooManyMethods" })
@Validations(expressions = @ExpressionValidator(message = "Files must be selected for this operation.",
        expression = "!selectedFiles.empty"))
public class ProjectFilesAction extends AbstractBaseProjectAction implements Preparable {
    private static final String ASSOC_NODES_EXPR =
            "@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@ASSOCIATE_TO_NODES";
    private static final String AUTOCREATE_SINGLE_EXPR =
            "@gov.nih.nci.caarray.application.arraydata.DataImportTargetAnnotationOption@AUTOCREATE_SINGLE";

    private static final Logger LOG = Logger.getLogger(ProjectFilesAction.class);

    /**
     * Object to keep count of errors and generate error messages.
     */
    private class ErrorCounts {
        private final Map<String, Integer> counts = new HashMap<String, Integer>();

        void incrementCount(String msgKey) {
            final Integer count = this.counts.get(msgKey);
            this.counts.put(msgKey, (count == null) ? 1 : count + 1);
        }

        List<String> getMessages() {
            final List<String> messages = new ArrayList<String>();
            for (final String key : this.counts.keySet()) {
                messages.add(getText(key, Lists.newArrayList(this.counts.get(key).toString())));
            }
            return messages;
        }
    }

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

    @Inject
    private static FileTypeRegistry fileTypeRegistry;

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
    private final Map<CaArrayFile, Boolean> deletableFiles = new HashMap<CaArrayFile, Boolean>();
    private String importDescription;

    private String prepListUnimportedPage() {
        if (this.clearCheckboxes) {
            this.selectedFileIds.clear();
        }
        setListAction(ACTION_UNIMPORTED);
        SortedSet<CaArrayFile> unimportedFiles = getProject().getUserVisibleUnImportedFiles();
        setFilesMatchingTypeAndStatus(unimportedFiles);
        setFileTypeNamesAndStatuses(unimportedFiles);
        setFileStatusCountMap(computeFileStatusCounts());
        findDeletableFiles();
        return ACTION_UNIMPORTED;
    }

    private String prepListImportedPage() {
        setListAction(ACTION_IMPORTED);
        setFiles(new HashSet<CaArrayFile>());
        SortedSet<CaArrayFile> importedFiles = getProject().getUserVisibleImportedFiles();
        for (final CaArrayFile f : importedFiles) {
            if (getFileType() == null
                    || (f.getFileType() != null && f.getFileType().getName().equals(getFileType())
                            || (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null) || (UNKNOWN_FILE_TYPE
                            .equals(getFileType()) && f.getFileType() == null))) {
                getFiles().add(f);
            }
        }
        setFileTypeNamesAndStatuses(importedFiles);
        findDeletableFiles();
        return ACTION_IMPORTED;
    }

    private void findDeletableFiles() {
        final List<CaArrayFile> deletables = ServiceLocatorFactory.getProjectManagementService().getDeletableFiles(
                getProject().getId());
        this.deletableFiles.clear();
        for (final CaArrayFile f : getFiles()) {
            this.deletableFiles.put(f, deletables.contains(f));
        }
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String downloadFiles() {
        SortedSet<CaArrayFile> allFiles = getProject().getUserVisibleFiles();
        setFilesMatchingTypeAndStatus(allFiles);
        setFileTypeNamesAndStatuses(allFiles);
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
        final Iterator<String> it = getFileTypes().iterator();
        while (it.hasNext()) {
            final String typeStr = it.next();
            final FileType type = fileTypeRegistry.getTypeByName(typeStr);
            if (type == null || !type.isArrayData()) {
                it.remove();
            }
        }
        return Action.SUCCESS;
    }

    private void setFilesMatchingTypeAndStatus(SortedSet<CaArrayFile> fileSet) {
        setFiles(new HashSet<CaArrayFile>());
        for (final CaArrayFile f : fileSet) {
            final boolean fileStatusMatch = (getFileStatus() == null || getFileStatus()
                    .equals(f.getFileStatus().name()));
            final boolean fileTypeMatch = (getFileType() == null || (f.getFileType() != null
                    && f.getFileType().getName().equals(getFileType())
                    || (KNOWN_FILE_TYPE.equals(getFileType()) && f.getFileType() != null) || (UNKNOWN_FILE_TYPE
                    .equals(getFileType()) && f.getFileType() == null)));
            if (fileStatusMatch && fileTypeMatch) {
                getFiles().add(f);
            }
        }
    }

    private void setFileTypeNamesAndStatuses(SortedSet<CaArrayFile> fileSet) {
        final Set<String> fileTypeNames = new TreeSet<String>();
        final List<String> fileStatusList = new ArrayList<String>();
        for (final CaArrayFile file : fileSet) {
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
        setFiles(getProject().getUserVisibleSupplementalFiles());
        findDeletableFiles();
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
        final Injector injector = InjectorFactory.getInjector();
        final FileUploadUtils fileUploadUtils = injector.getInstance(FileUploadUtils.class);
        try {
            final FileProcessingResult result = fileUploadUtils.unpackFiles(getProject(), this.getSelectedFiles());

            for (final String conflict : result.getConflictingFiles()) {
                ActionHelper.saveMessage(getText("experiment.files.unpack.filename.exists",
                        Lists.newArrayList(conflict)));
            }
            ActionHelper.saveMessage(result.getCount() + " file(s) unpacked.");
        } catch (final InvalidFileException ue) {
            ActionHelper.saveMessage(getText("errors.unpackingErrorWithZip",
                    Lists.newArrayList(ue.getFile(), getText(ue.getResourceKey()))));
        } catch (final Exception e) {
            final String msg = "Unable to unpack file: " + e.getMessage();
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
        for (final CaArrayFile caArrayFile : getSelectedFiles()) {
            final boolean removed = ServiceLocatorFactory.getFileAccessService().remove(caArrayFile);
            if (removed) {
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
            for (final CaArrayFile caArrayFile : getSelectedFiles()) {
                caArrayFile.setFileStatus(FileStatus.UPLOADED);
                if (caArrayFile.getValidationResult() != null) {
                    caArrayFile.getValidationResult().getMessageSet().clear();
                }
                ServiceLocatorFactory.getGenericDataService().save(caArrayFile);
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
            for (final CaArrayFile caArrayFile : getSelectedFiles()) {
                caArrayFile.setFileType(fileTypeRegistry.getTypeByName(this.getChangeToFileType()));
            }
        }
        return saveFiles();
    }

    /**
     * Method to validate the files.
     *
     * @return the string matching the result to follow
     */
    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    // validation checks can't be easily refactored to smaller methods.
    public String validateFiles() {
        final ErrorCounts errors = new ErrorCounts();
        final CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (final CaArrayFile file : getSelectedFiles()) {
            if (file.getFileType() == null) {
                errors.incrementCount("project.fileValidate.error.unknownType");
            } else if (file.getFileType().isArrayDesign()) {
                errors.incrementCount("project.fileValidate.error.arrayDesign");
            } else if (file.getFileStatus().isValidatable()) {
                fileSet.add(file);
            } else {
                errors.incrementCount("project.fileValidate.error.invalidStatus");
            }
        }
        if (!fileSet.getFiles().isEmpty()) {
            ServiceLocatorFactory.getFileManagementService().validateFiles(getProject(), fileSet);
        }
        ActionHelper.saveMessage(getText("project.fileValidate.success",
                Lists.newArrayList(String.valueOf(fileSet.getFiles().size()))));
        for (final String msg : errors.getMessages()) {
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
        // we are only interested in an IDF selected and all SDRFs in the
        // project files

        // check that there is only 1 file and that it is the idf file
        if (getSelectedFiles().size() > 1) {
            ActionHelper.saveMessage(getText("project.selectRefFile.error.moreThanOneFile"));
        } else if (!getSelectedFiles().get(0).getFileType().equals(FileTypeRegistry.MAGE_TAB_IDF)) {
            ActionHelper.saveMessage(getText("project.selectRefFile.error.notIdf"));
        } else {
            generateRefFileList(getSelectedFiles().get(0));
        }

        ActionHelper.saveMessage(getText("project.selectRefFile.success",
                Lists.newArrayList(String.valueOf(this.selectedFiles.size()))));

        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    private void generateRefFileList(CaArrayFile idfFile) {

        this.selectedFiles.clear();

        if (idfFile != null) {
            this.selectedFiles.add(idfFile);
            // find files ref'ing sdrf file.
            final List<String> filenames = ServiceLocatorFactory.getFileManagementService().findIdfRefFileNames(
                    idfFile, getProject());
            if (!filenames.isEmpty() && validateReferencedFilesPresent(filenames)) {
                findFilesByName(filenames);
                boolean addErrorMessage = false;
                for (final CaArrayFile caf : this.selectedFiles) {
                    if (!this.selectedFileIds.contains(caf.getId())) {
                        this.selectedFileIds.add(caf.getId());
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
        final Set<String> fileNames = getSetOfCaArrayFileNames();
        for (final String referencedFileName : referencedFileNames) {
            if (!fileNames.contains(referencedFileName)) {
                noFilesAreMissing = false;
                ActionHelper.saveMessage(getText("project.selectRefFile.error.validation.missingFile",
                        Lists.newArrayList("'" + referencedFileName + "'")));
            }
        }
        return noFilesAreMissing;
    }

    private Set<String> getSetOfCaArrayFileNames() {
        final Set<String> fileNames = new HashSet<String>();
        for (final CaArrayFile caArrayFile : getProject().getFiles()) {
            fileNames.add(caArrayFile.getName());
        }
        return fileNames;
    }

    private void findFilesByName(List<String> thesefiles) {
        for (final CaArrayFile caf : getProject().getFiles()) {
            if (thesefiles.contains(caf.getName())) {
                this.selectedFiles.add(caf);
            }
        }
    }

    /**
     * AJAX call to determine if all selected files can be imported.
     *
     * @return null
     */
    @SkipValidation
    public String validateSelectedImportFiles() {
        final ErrorCounts errors = new ErrorCounts();
        final CaArrayFileSet fileSet = checkImportFiles(errors);
        try {
            final JSONObject json = new JSONObject();
            final List<String> errorMsgs = errors.getMessages();
            if (errorMsgs.isEmpty()) {
                json.element("validated", true);
            } else {
                final StringBuffer buffer = new StringBuffer();
                for (final String msg : errorMsgs) {
                    buffer.append(msg).append('\n');
                }
                buffer.append("Would you like to continue importing the remaining " + fileSet.getFiles().size()
                        + " file(s)?");
                json.element("confirmMessage", buffer.toString());
            }
            ServletActionContext.getResponse().getWriter().write(json.toString());
        } catch (final IOException e) {
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
    @Validations(
            expressions = {
                    @ExpressionValidator(message = "You must select at least one biomaterial or hybridization.",
                            expression = "targetNodeIds.size() > 0 || targetAnnotationOption != " + ASSOC_NODES_EXPR),
                    @ExpressionValidator(message = "You must enter a new annotation name.",
                            expression = "newAnnotationName != null && "
                                    + " newAnnotationName.length() > 0 || targetAnnotationOption != "
                                    + AUTOCREATE_SINGLE_EXPR) })
    public String importFiles() {
        final ErrorCounts errors = new ErrorCounts();
        final CaArrayFileSet fileSet = checkImportFiles(errors);
        if (!fileSet.getFiles().isEmpty()) {
            final List<Long> entityIds = new ArrayList<Long>(this.targetNodeIds);
            final ExperimentDesignNodeType targetNodeType =
                    (this.nodeType == null ? null : this.nodeType.getNodeType());
            final DataImportOptions dataImportOptions = DataImportOptions.getDataImportOptions(
                    this.targetAnnotationOption, this.newAnnotationName, targetNodeType, entityIds);
            dataImportOptions.setImportDescription(importDescription);
            ServiceLocatorFactory.getFileManagementService().importFiles(getProject(), fileSet, dataImportOptions);
        }
        ActionHelper.saveMessage(getText("project.fileImport.success",
                Lists.newArrayList(String.valueOf(fileSet.getFiles().size()))));
        for (final String msg : errors.getMessages()) {
            ActionHelper.saveMessage(msg);
        }
        refreshProject();
        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    /**
     * Checks on which of the selected files can be imported, and stores counts
     * of those that cannot be.
     *
     * @param errors object that stores the error counts
     * @return the set of importable files
     */
    private CaArrayFileSet checkImportFiles(ErrorCounts errors) {
        final CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (final CaArrayFile file : getSelectedFiles()) {
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
     * Method to reparse the imported-not-parsed files which now have parsers.
     *
     * @return the string matching the result to follow
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public String reparseFiles() {
        if (!getProject().getExperiment().hasParsedArrayDesigns()) {
            ActionHelper.saveMessage(getText("project.fileReparse.error.noParsedDesigns"));
            return prepListImportedPage();
        }

        final ErrorCounts errors = new ErrorCounts();
        final CaArrayFileSet fileSet = new CaArrayFileSet(getProject());

        for (final CaArrayFile file : getSelectedFiles()) {
            if (!file.isUnparsedAndReimportable()) {
                errors.incrementCount("project.fileReparse.error.notEligible");
            } else {
                fileSet.add(file);
            }
        }

        for (final String msg : errors.getMessages()) {
            ActionHelper.saveMessage(msg);
        }

        if (!fileSet.getFiles().isEmpty()) {
            ServiceLocatorFactory.getFileManagementService().reimportAndParseProjectFiles(getProject(), fileSet);
        }
        ActionHelper.saveMessage(getText("project.fileImport.success",
                Lists.newArrayList(String.valueOf(fileSet.getFiles().size()))));
        refreshProject();

        this.clearCheckboxes = false;
        return prepListUnimportedPage();
    }

    /**
     * Adds supplemental data files to the system.
     *
     * @return the string matching the result to follow
     */
    public String addSupplementalFiles() {
        final CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (final CaArrayFile file : getSelectedFiles()) {
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
     * This method refreshes the project from the db. It is in its own method to
     * allow test cases to overwrite this.
     */
    protected void refreshProject() {
        ServiceLocatorFactory.getGenericDataService().refresh(getProject());
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
     * Implements file download. Writes a zip of the selected files to the
     * servlet output stream
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
     * This method will download a group of files if the group number is
     * specified or if there is only one download group.
     *
     * @param project the project
     * @param contentFiles all selected files
     * @return downloadGroups if no group number is specified and there are multiple groups
     * @throws IOException if there is an error writing to the stream
     */
    protected String downloadArchive(Project project, Collection<CaArrayFile> contentFiles) throws IOException {
        final StringBuilder baseName = determineDownloadFileName(project);
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
     *
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
     * Returns the filename for a zip of files for the given project, assuming
     * that the download will not be grouped.
     *
     * @param project the project whose files are downloaded
     * @return the filename
     */
    public static StringBuilder determineDownloadFileName(Project project) {
        final StringBuilder name = new StringBuilder("caArray_").append(project.getExperiment().getPublicIdentifier())
                .append("_files");
        return name;
    }

    /**
     * Calculates and returns the JSON for the nodes that are the children of
     * the passed in node. in the experiment tree
     *
     * @return null - the JSON is written directly to the response stream
     */
    @SkipValidation
    public String importTreeNodesJson() {
        try {
            final JSONArray jsArray = new JSONArray();

            if (this.nodeType == ExperimentDesignTreeNodeType.ROOT) {
                addJsonForExperimentRoots(jsArray);
            } else if (this.nodeType.isExperimentRootNode()) {
                addJsonForExperimentDesignNodes(jsArray, this.nodeType.getChildrenNodeType(),
                        this.nodeType.getContainedNodes(getExperiment()), this.nodeId);
            } else if (this.nodeType.isBiomaterialRootNode()) {
                // the node id of an associated biomaterials container node will end in [number]_[type]
                // where [type] is the container type and [number] is the id of the biomaterial parent
                final Long biomaterialParentId =
                        Long.parseLong(StringUtils.substringAfterLast(
                                StringUtils.substringBeforeLast(this.nodeId, "_"), "_"));
                final AbstractBioMaterial bioMaterialParent =
                        ServiceLocatorFactory.getGenericDataService().getPersistentObject(AbstractBioMaterial.class,
                                biomaterialParentId);
                addJsonForExperimentDesignNodes(jsArray, this.nodeType.getChildrenNodeType(),
                        this.nodeType.getContainedNodes(bioMaterialParent), this.nodeId);
            } else if (this.nodeType.isBiomaterialNode()) {
                // note that we should never get requested for biomaterial or hybrdization nodes
                // since they are returned with their children already or marked as leaves
                throw new IllegalStateException("Unsupported node type" + this.nodeType);
            }

            ServletActionContext.getResponse().getWriter().write(jsArray.toString());
        } catch (final IOException e) {
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

    private void addJsonForSamplesRoot(JSONArray jsonArray, AbstractBioMaterial parent, String nodeIdPrefix) {
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

    private void addJsonForExtractsRoot(JSONArray jsonArray, AbstractBioMaterial parent, String nodeIdPrefix) {
        final JSONObject json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_Extracts");
        json.element(TEXT_PROPERTY, "Associated Extracts");
        json.element(SORT_PROPERTY, "3");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForLabeledExtractsRoot(JSONArray jsonArray, AbstractBioMaterial parent, String nodeIdPrefix) {
        final JSONObject json = new JSONObject();
        json.element(ID_PROPERTY, nodeIdPrefix + "_LabeledExtracts");
        json.element(TEXT_PROPERTY, "Associated Labeled Extracts");
        json.element(SORT_PROPERTY, "4");
        json.element(NODE_TYPE_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_LABELED_EXTRACTS);
        json.element(LEAF_PROPERTY, ExperimentDesignTreeNodeType.BIOMATERIAL_LABELED_EXTRACTS.getContainedNodes(parent)
                .isEmpty());
        jsonArray.element(json);
    }

    private void addJsonForHybridizationsRoot(JSONArray jsonArray, AbstractBioMaterial parent, String nodeIdPrefix) {
        final JSONObject json = new JSONObject();
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
        for (final AbstractExperimentDesignNode node : nodes) {
            final JSONObject json = new JSONObject();
            final String newNodeId = nodeIdPrefix + "_" + node.getId();
            json.element(ID_PROPERTY, newNodeId);
            json.element("entityId", node.getId());
            json.element(TEXT_PROPERTY, node.getName());
            json.element(SORT_PROPERTY, node.getName());
            json.element(NODE_TYPE_PROPERTY, newNodesType);
            json.element("iconCls", newNodesType.name().toLowerCase(Locale.getDefault()) + "_node");
            json.element("checked", false);

            final JSONArray associatedRoots = new JSONArray();
            if (newNodesType == ExperimentDesignTreeNodeType.SOURCE) {
                addJsonForSamplesRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (EnumSet.of(ExperimentDesignTreeNodeType.SOURCE, ExperimentDesignTreeNodeType.SAMPLE).contains(
                    newNodesType)) {
                addJsonForExtractsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (EnumSet.range(ExperimentDesignTreeNodeType.SOURCE, ExperimentDesignTreeNodeType.EXTRACT).contains(
                    newNodesType)) {
                addJsonForLabeledExtractsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
            }
            if (newNodesType.isBiomaterialNode()) {
                addJsonForHybridizationsRoot(associatedRoots, (AbstractBioMaterial) node, newNodeId);
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
        return this.selectedFileIds;
    }

    /**
     * @param selectedFileIds the ids of the selected files
     */
    public void setSelectedFileIds(Set<Long> selectedFileIds) {
        this.selectedFileIds = selectedFileIds;
        this.selectedFiles = ServiceLocatorFactory.getGenericDataService().retrieveByIds(CaArrayFile.class,
                new ArrayList<Long>(selectedFileIds));
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
            final String actionName = ActionContext.getContext().getName();
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
     * Verify that all files selected by their ids are retrieved.
     */
    private boolean checkInconsistentFileSelect() {
        // most likely there was a session timeout which resulted in the user
        // becoming anon
        // have the user log in again.
        // this will make the validation fail and the user will be kicked back
        // to the
        // overview screen. but because the user is anon, they will have to log
        // in again.
        return (this.selectedFiles.size() != this.selectedFileIds.size());
    }

    /**
     * @return the nodeId
     */
    public String getNode() {
        return this.nodeId;
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
        return this.nodeType;
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
        return this.targetAnnotationOption;
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
        return this.targetNodeIds;
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
        return this.newAnnotationName;
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
        final EnumMap<FileStatus, Integer> countMap = new EnumMap<FileStatus, Integer>(FileStatus.class);

        for (final FileStatus f : FileStatus.values()) {
            countMap.put(f, 0);
        }

        for (final CaArrayFile f : getFiles()) {
            countMap.put(FileStatus.valueOf(f.getStatus()), countMap.get(FileStatus.valueOf(f.getStatus())) + 1);
        }
        return countMap;
    }

    /**
     * @return the fileStatus
     */
    public String getFileStatus() {
        return this.fileStatus;
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
        return this.fileStatuses;
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
        return this.fileStatusCountMap;
    }

    /**
     * @param fileStatusCountMap the fileStatusCountMap to set
     */
    public void setFileStatusCountMap(EnumMap<FileStatus, Integer> fileStatusCountMap) {
        this.fileStatusCountMap = fileStatusCountMap;
    }

    /**
     * @return the deletableFiles
     */
    public Map<CaArrayFile, Boolean> getDeletableFiles() {
        return this.deletableFiles;
    }

    /**
     * @return the set of files types currently known to caArray.
     */
    public Set<FileType> getAvailableFileTypes() {
        return fileTypeRegistry.getAllTypes();
    }

    /**
     * @return the importDescription
     */
    public String getImportDescription() {
        return importDescription;
    }

    /**
     * @param importDescription the importDescription to set
     */
    public void setImportDescription(String importDescription) {
        this.importDescription = importDescription;
    }
}
