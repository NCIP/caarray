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

import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.io.FileClosingInputStream;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * @author Scott Miller
 *
 */
@Validation
public class ProjectFilesAction extends ActionSupport implements Preparable {
    private static final long serialVersionUID = 1L;

    private Project project;
    private List<File> uploads;
    private List<String> uploadFileNames = new ArrayList<String>();
    private List<String> uploadContentTypes = new ArrayList<String>();
    private List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
    private InputStream downloadStream;

    /**
     * {@inheritDoc}
     */
    public void prepare() throws Exception {
        if (getProject() != null && getProject().getId() != null) {
            setProject(getProjectManagementService().getProject(getProject().getId()));
        }
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String list() {
        return Action.INPUT;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String listTable() {
        return Action.SUCCESS;
    }

    /**
     * Method to get the list of files.
     *
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String downloadFiles() {
        return Action.SUCCESS;
    }

    /**
     * Method to delete files.
     * @return the string representing the UI to display.
     */
    @SkipValidation
    public String deleteFiles() {
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
        ActionHelper.saveMessage(deletedFiles + " files deleted.");
        if (skippedFiles > 0) {
            ActionHelper.saveMessage(skippedFiles + " files were not in a status that allows for deletion.");
        }
        return Action.INPUT;
    }

    /**
     * Method to validate the files.
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String validateFiles() {
        int validatedFiles = 0;
        int skippedFiles = 0;
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (CaArrayFile file: getSelectedFiles()) {
            if (file.getFileStatus().isValidatable()) {
                fileSet.add(file);
                validatedFiles++;
            } else {
                skippedFiles++;
            }
        }
        if (!fileSet.getFiles().isEmpty()) {
            getFileManagementService().validateFiles(fileSet);
        }
        ActionHelper.saveMessage(validatedFiles + " files validated.");
        if (skippedFiles > 0) {
            ActionHelper.saveMessage(skippedFiles + " files were not in a status that allows for validation.");
        }
        return Action.INPUT;
    }

    /**
     * Method to import the files.
     * @return the string matching the result to follow
     */
    public String importFiles() {
        int importedFiles = 0;
        int skippedFiles = 0;
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (CaArrayFile file: getSelectedFiles()) {
            if (file.getFileStatus().isImportable()) {
                fileSet.add(file);
                importedFiles++;
            } else {
                skippedFiles++;
            }
        }
        if (!fileSet.getFiles().isEmpty()) {
            getFileManagementService().importFiles(getProject(), fileSet);
        }
        ActionHelper.saveMessage(importedFiles + " files imported.");
        if (skippedFiles > 0) {
            ActionHelper.saveMessage(skippedFiles + " files were not in a status that allows for importing.");
        }
        return Action.INPUT;
    }

    /**
     * View the validation messages for the selected files.
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
    public String upload() throws Exception {
        unzipFiles();

        int index = 0;
        int fileCount = 0;
        for (File uploadedFile : getUpload()) {
            try {
                String fileName = getUploadFileName().get(index);
                if (StringUtils.isNotBlank(fileName)) {
                    getProjectManagementService().addFile(getProject(), uploadedFile, fileName);
                    fileCount++;
                }
            } catch (Exception e) {
                String msg = "Unable to upload file: " + e.getMessage();
                LOG.error(msg, e);
                addActionError(getText("errorUploading"));
                return INPUT;
            }
            index++;
        } // end for

        ActionHelper.saveMessage(fileCount + " files uploaded.");
        return "upload";
    }

    /**
     * unzips a .zip file, removes it from uploads and adds files present in zip to uploads.
     *
     * @throws IOException
     */
    private void unzipFiles() throws IOException {
        Pattern p = Pattern.compile(".zip$");
        int index = 0;

        for (int i = 0; i < getUploadFileName().size(); i++) {
            Matcher m = p.matcher(getUploadFileName().get(i).toLowerCase());
            if (m.find()) {
                File uploadedFile = getUpload().get(i);
                String uploadedFileName = uploadedFile.getAbsolutePath();
                String directoryPath = uploadedFile.getParent();
                ZipFile zipFile = new ZipFile(uploadedFileName);

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();

                    File entryFile = new File(directoryPath + "/" + entry.getName());
                    StreamUtils.copy(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(entryFile)), 1024);

                    this.uploads.add(entryFile);
                    this.uploadFileNames.add(entry.getName());
                }
                this.uploads.remove(index);
                this.uploadFileNames.remove(index);
                this.uploadContentTypes.remove(index);
            }
            index++;
        }
    }

    /**
     * Prepares for download by zipping selected files and setting the internal InputStream.
     *
     * @param selectedFiles the files to download
     * @return SUCCESS
     * @throws IOException if
     */
    @SkipValidation
    public String download() throws IOException {
        File zipFile = getProjectManagementService().prepareForDownload(getSelectedFiles());
        this.downloadStream = new FileClosingInputStream(new FileInputStream(zipFile), zipFile);
        return "download";
    }

    /**
     * @return the stream containing the zip file download
     */
    public InputStream getDownloadStream() {
        return this.downloadStream;
    }

    private ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) ServiceLocatorFactory.getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    private FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
    }

    private FileManagementService getFileManagementService() {
        return (FileManagementService) ServiceLocatorFactory.getLocator().lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
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
     * returns uploaded content type.
     *
     * @return uploadContentTypes
     */
    public List<String> getUploadContentType() {
        return this.uploadContentTypes;
    }

    /**
     * sets upload content type.
     *
     * @param inContentTypes List
     */
    public void setUploadContentType(List<String> inContentTypes) {
        this.uploadContentTypes = inContentTypes;
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

}