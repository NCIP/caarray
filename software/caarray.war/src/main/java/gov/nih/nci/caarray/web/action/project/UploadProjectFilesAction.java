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
