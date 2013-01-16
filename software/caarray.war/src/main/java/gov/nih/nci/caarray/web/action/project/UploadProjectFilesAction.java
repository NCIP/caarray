//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.project.FileProcessingResult;
import gov.nih.nci.caarray.application.project.FileUploadUtils;
import gov.nih.nci.caarray.application.project.FileWrapper;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.web.fileupload.MonitoredMultiPartRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action Class to handle Upload File functionality.
 */
@Validations(expressions = @ExpressionValidator(message = "Files must be selected for this operation.",
        expression = "!selectedFiles.empty"))
public class UploadProjectFilesAction extends AbstractBaseProjectAction implements Preparable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UploadProjectFilesAction.class);

    /**
     * Maximum total uncompressed size (in bytes) of files that can be
     * downloaded in a single ZIP. If files selected for download have a greater
     * combined size, then the user will be presented with a group download
     * page.
     */
    static final String UPLOAD_IN_BACKGROUND = "uploadInBackground";
    static final String IMPORTING_FILES_KEY = "project.inconsistentState.importing_files";
    static final String UPLOADING_ERROR_KEY = "errors.uploading";
    static final String FILE_CONFLICT_ERROR_KEY = "experiment.files.upload.filename.exists";
    static final String ZIP_UPLOAD_ERROR_KEY = "errors.uploadingErrorWithZip";
    static final String FILE_ADD_ERROR_KEY = "errors.uploadingErrorWithAdding";
    static final String PERMISSION_DENIED_ERROR_KEY = "project.permissionDenied";
    
    static final String FILE_UPLOADED_MSG_SUFFIX = " file(s) uploaded.";

    private FileUploadUtils fileUploadUtils;
    private FileDao fileDao;

    private List<String> selectedFilesToUnpack = Lists.newArrayList();
    private List<File> upload = Lists.newArrayList();
    private List<String> uploadFileName = Lists.newArrayList();
    private Long chunkedFileSize;
    private String chunkedFileName;

    /**
     *
     */
    @Override
    public void prepare() {
        super.prepare();
        final Injector injector = InjectorFactory.getInjector();
        setFileUploadUtils(injector.getInstance(FileUploadUtils.class));
        setFileDao(injector.getInstance(FileDao.class));
    }

    /**
     * Uploads all of the files in the request.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String upload() {
        StringBuilder errorString = new StringBuilder();
        try {
            if (validateUpload()) {
                initChunkedUploads();
                FileProcessingResult uploadResult = fileUploadUtils.uploadFiles(getProject(), getFileWrappers());

                handleConflicts(uploadResult, errorString);
                if (!uploadResult.isPartialUpload()) {
                    updateUploadMessage(uploadResult);
                }
            }
        } catch (InvalidFileException e) {
            handleInvalidFileException(errorString, e, selectedFilesToUnpack);
        } catch (Exception e) {
            LOG.error("Unable to upload file: " + e.getMessage(), e);
            addErrorMessage(errorString, UPLOADING_ERROR_KEY);
        } finally {
            /*
             * Bit of a hack. Data File Uploads do not use the Progress Monitor,
             * but it uses the MonitoredMultiPartRequest. Thus, we need to
             * release the progress monitor here.
             */
            MonitoredMultiPartRequest.releaseProgressMonitor(ServletActionContext.getRequest());
        }

        if (this.hasErrors()) {
            errorString.append(getActionErrorsAsString());
        }
        writeJsonOutputToResponse(getUploadFileName(), errorString.toString());
        return NONE;
    }
    
    private void updateUploadMessage(FileProcessingResult uploadResult) {
        int totalUploadCount = uploadResult.getCount();
        List<String> msgs = ActionHelper.getMessages();
        if (msgs != null) {
            Iterator<String> it = msgs.iterator();
            while (it.hasNext()) {
                String msg = it.next();
                int endIndex = msg.lastIndexOf(FILE_UPLOADED_MSG_SUFFIX);
                if (endIndex > 0) {
                    totalUploadCount += Integer.parseInt(msg.substring(0, endIndex));
                    it.remove();
                }
            }
        }
        ActionHelper.saveMessage(totalUploadCount + FILE_UPLOADED_MSG_SUFFIX);
    }

    /**
     * Checks for an existing partial upload of a file.
     * @return the size of the file, if it exists.
     */
    @SkipValidation
    public String partialUploadCheck() {
        JSONObject result = new JSONObject();
        if (chunkedFileName != null && chunkedFileSize != null) {
            CaArrayFile file = fileDao.getPartialFile(getProject().getId(), chunkedFileName, chunkedFileSize);
            if (file != null) {
                result.put("size", file.getPartialSize());
            }
            try {
                ServletActionContext.getResponse().setContentType("text/plain");
                ServletActionContext.getResponse().getWriter().write(result.toString());
            } catch (IOException e) {
                LOG.warn("Swallowed exception - Cannot write to the response", e);
            }
        }
        return NONE;
    }
    
    private void initChunkedUploads() {
        if (chunkedFileName != null) {
            setUploadFileName(Lists.newArrayList(chunkedFileName));
        }
    }
    
    private void handleConflicts(FileProcessingResult uploadResult, StringBuilder sbf) {
        for (String conflict : uploadResult.getConflictingFiles()) {
            addErrorMessage(sbf, FILE_CONFLICT_ERROR_KEY, conflict);
        }
    }

    private void addErrorMessage(StringBuilder errorBuffer, String key, Object... params) {
        String errorMsg = getText(key, Arrays.asList(params));
        ActionHelper.saveMessage(errorMsg);
        errorBuffer.append(errorMsg).append("; ");
    }

    private void handleInvalidFileException(StringBuilder sbf, InvalidFileException e, List<String> fileNamesToUnpack) {
        Object[] messageParams = new Object[] {e.getFile(), getText(e.getResourceKey()) };
        String errorKey = fileNamesToUnpack.contains(e.getFile()) ? ZIP_UPLOAD_ERROR_KEY : FILE_ADD_ERROR_KEY;
        addErrorMessage(sbf, errorKey, messageParams);
    }

    private void writeJsonOutputToResponse(List<String> filenames, String errors) {
        List<Map<String, Object>> uploadsList = Lists.newArrayList();
        for (String filename : filenames) {
            Map<String, Object> result = Maps.newHashMap();
            result.put("name", filename);
            result.put("error", errors);
            uploadsList.add(result);
        }

        String jsonString = JSONArray.fromObject(uploadsList.toArray()).toString();
        ServletActionContext.getResponse().setContentType("text/plain");
        try {
            ServletActionContext.getResponse().getWriter().write(jsonString);
        } catch (IOException e) {
            LOG.warn("Swallowed exception - Cannot write to the response", e);
        }
    }

    private String getActionErrorsAsString() {
        StrBuilder sbf = new StrBuilder();
        sbf.appendWithSeparators(getActionErrors(), "; ");

        for (Map.Entry<String, List<String>> entry : this.getFieldErrors().entrySet()) {
            sbf.appendWithSeparators(entry.getValue(), "; ");
        }
        return sbf.toString();
    }

    /**
     * validates user permissions and required file for upload.
     *
     * @return true if validation passes
     */
    private boolean validateUpload() {
        return validatePermissions() && getUpload().size() > 0;
    }

    /**
     * validates user permissions.
     *
     * @return true if validation passes
     */
    private boolean validatePermissions() {
        if (CaArrayUsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            ActionHelper.saveMessage(getText("upload.session.expired"));
            return false;
        }
        if (!getProject().hasWritePermission(getCsmUser())) {
            ActionHelper.saveMessage(getText(PERMISSION_DENIED_ERROR_KEY, Lists.newArrayList(getText("role.write"))));
            return false;
        }
        return true;
    }

    private List<FileWrapper> getFileWrappers() {
        List<FileWrapper> files = new ArrayList<FileWrapper>();
        for (int i = 0; i < getUpload().size(); i++) {
            File file = getUpload().get(i);
            FileWrapper wrapper = new FileWrapper();
            String fileName = getUploadFileName().get(i);
            wrapper.setFile(file);
            wrapper.setFileName(fileName);
            wrapper.setCompressed((Iterables.contains(selectedFilesToUnpack, fileName)));
            if (getChunkedFileSize() != null) {
                wrapper.setTotalFileSize(getChunkedFileSize());
            } else {
                wrapper.setTotalFileSize(file.length());
            }
            files.add(wrapper);
        }
        return files;
    }

    /**
     * Action for displaying the upload in background form.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String uploadInBackground() {
        if (getProject().isImportingData()) {
            ActionHelper.saveMessage(getText(IMPORTING_FILES_KEY));
        }
        return UPLOAD_IN_BACKGROUND;
    }

    /**
     * uploaded file.
     *
     * @return uploads uploaded files
     */
    public List<File> getUpload() {
        return this.upload;
    }

    /**
     * sets file uploads.
     *
     * @param inUploads
     *            List
     */
    public void setUpload(List<File> inUploads) {
        this.upload = inUploads;
    }

    /**
     * returns uploaded file name.
     *
     * @return uploadFileNames
     */
    public List<String> getUploadFileName() {
        return this.uploadFileName;
    }

    /**
     * sets uploaded file names.
     *
     * @param inUploadFileNames
     *            List
     */
    public void setUploadFileName(List<String> inUploadFileNames) {
        this.uploadFileName = inUploadFileNames;
    }

    /**
     * @return the total size of the file being uploaded.
     */
    public Long getChunkedFileSize() {
        return chunkedFileSize;
    }

    /**
     * @param chunkedFileSize the total size of the file being uploaded.
     */
    public void setChunkedFileSize(Long chunkedFileSize) {
        this.chunkedFileSize = chunkedFileSize;
    }

    /**
     * @return the name of the file being uploaded
     */
    public String getChunkedFileName() {
        return chunkedFileName;
    }

    /**
     * @param chunkedFileName the name of the file being uploaded
     */
    public void setChunkedFileName(String chunkedFileName) {
        this.chunkedFileName = chunkedFileName;
    }

    /**
     * @return the selectedFilesToUnpack
     */
    public List<String> getSelectedFilesToUnpack() {
        return this.selectedFilesToUnpack;
    }

    /**
     * @param selectedFilesToUnpack
     *            the selectedFilesToUnpack to set
     */
    public void setSelectedFilesToUnpack(List<String> selectedFilesToUnpack) {
        this.selectedFilesToUnpack = selectedFilesToUnpack;
    }

    /**
     * @param uploadUtils
     *            .
     */
    public void setFileUploadUtils(FileUploadUtils uploadUtils) {
        this.fileUploadUtils = uploadUtils;
    }

    /**
     * @param fileDao the fileDao to set
     */
    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

}
