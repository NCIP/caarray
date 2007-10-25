package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.io.FileClosingInputStream;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ManageFilesDelegate;
import gov.nih.nci.caarray.web.util.CacheManager;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Manages Files for a particular project.
 *
 * @author John Hedden
 *
 */
@Validation
public class FileManageAction extends BaseAction implements Preparable {

    private static final String FILE_MANAGE_LINKS = "FileManageLinks";
    private static final long serialVersionUID = 1L;
    private String menu = null;
    private CaArrayFile file = new CaArrayFile();
    private Project project = new Project();
    private List<File> uploads = new ArrayList<File>();
    private List<String> uploadFileNames = new ArrayList<String>();
    private List<String> uploadContentTypes = new ArrayList<String>();
    private String downloadIds;
    private InputStream downloadStream;

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        if (getProject() != null && getProject().getId() != null) {
            setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
        }
    }

    /**
     * manage files for a project.
     *
     * @return path String
     */
    @SkipValidation
    public String manage() {
        setMenu(FILE_MANAGE_LINKS);
        return SUCCESS;
    }

    /**
     * view messages for file.
     *
     * @return String
     */
    @SkipValidation
    public String messages() {
        setMenu("FileMessagesLinks");

        for (CaArrayFile caArrayFile : getProject().getFilesList()) {
            if (caArrayFile.getId().compareTo(getFile().getId()) == 0) {
                setFile(caArrayFile);
            }
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on Struts2 indexed properties first. For now
     * just go through request.getParamenterNames().
     *
     * validates file.
     *
     * @return String
     */
    @SkipValidation
    public String validateFile() {
        setMenu(FILE_MANAGE_LINKS);

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            getDelegate().getFileManagementService().validateFiles(fileSet);
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on Struts2 indexed properties first. For now
     * just go through request.getParamenterNames().
     *
     * validates file.
     *
     * @return String
     */
    @SkipValidation
    public String importFile() {
        setMenu(FILE_MANAGE_LINKS);

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            getDelegate().getFileManagementService().importFiles(getProject(), fileSet);
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on Struts2 indexed properties first. For now
     * just go through request.getParamenterNames().
     *
     * validates file.
     *
     * @return String
     */
    @SkipValidation
    public String removeFile() {
        setMenu(FILE_MANAGE_LINKS);

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            for (CaArrayFile caArrayFile : fileSet.getFiles()) {
                getDelegate().getFileAccessService().remove(caArrayFile);
            }
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on Struts2 indexed properties first. For now
     * just go through request.getParamenterNames().
     *
     * validates file.
     *
     * @return String
     */
    @SkipValidation
    public String saveExtension() {
        setMenu(FILE_MANAGE_LINKS);

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            for (CaArrayFile caArrayFile : fileSet.getFiles()) {
                getDelegate().getFileAccessService().save(caArrayFile);
            }
        }
        return SUCCESS;
    }

    /**
     * uploads file.
     *
     * @return String
     */
    public String upload() throws Exception {
        setMenu(FILE_MANAGE_LINKS);

        unzipFiles();

        int index = 0;
        for (File uploadedFile : getUpload()) {
            try {
                String fileName = getUploadFileName(index);
                getDelegate().getProjectManagementService().addFile(getProject(), uploadedFile, fileName);
            } catch (Exception e) {
                String msg = "Unable to upload file: " + e.getMessage();
                LOG.error(msg, e);
                addActionError(getText("errorUploading"));
                return INPUT;
            }
            index++;
        } // end for
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private void addSelectedFiles(CaArrayFileSet fileSet, HttpServletRequest request) {
        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
            String name = myenum.nextElement();
            String myREGEX = ":";
            Pattern p = Pattern.compile(myREGEX);
            String[] items = p.split(name);
            // if pattern is like fileEntries:0:selected we know a checkbox has been selected
            if (items[0].equalsIgnoreCase("file") && items[2].equalsIgnoreCase("selected")) {
                String fileIndex = items[1];
                // set the file type
                String value = request.getParameter("file:" + fileIndex + ":fileType");
                CaArrayFile caArrayFile = getFile(fileIndex);

                caArrayFile.setType(FileType.getInstance(value));
                fileSet.add(getFile(fileIndex));
            }
        }
    }

    /**
     * unzips a .zip file, removes it from uploads
     * and adds files present in zip to uploads.
     * @throws IOException
     */
    protected void unzipFiles() throws IOException {
        Pattern p = Pattern.compile(".zip$");
        int index = 0;

        for (int i = 0; i < getUploadFileName().size(); i++) {
            Matcher m = p.matcher(getUploadFileName(i).toLowerCase());
            if (m.find()) {
                File uploadedFile = getUpload().get(i);
                String uploadedFileName = uploadedFile.getAbsolutePath();
                String directoryPath = uploadedFile.getParent();
                ZipFile zipFile = new ZipFile(uploadedFileName);

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();

                    File entryFile = new File(directoryPath + "/" + entry.getName());
                    copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(entryFile)));

                    uploads.add(entryFile);
                    uploadFileNames.add(entry.getName());
                }
                uploads.remove(index);
                uploadFileNames.remove(index);
                uploadContentTypes.remove(index);
            }
            index++;
        }
    }

    /**
     * method to copy compressed file contained in zip file.
     * @param in InputStream
     * @param out OutputStream
     * @throws IOException
     */
    public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
      byte[] buffer = new byte[1024];
      int len;

      while((len = in.read(buffer)) >= 0) {
          out.write(buffer, 0, len);
      }
      in.close();
      out.close();
    }

    /**
     * @return the fileTypes
     */
    public List<LabelValue> getFileTypes() {
        return CacheManager.getInstance().getFileTypes();
    }

    /**
     * gets the delegate from factory.
     *
     * @return Delegate ProjectDelegate
     */
    public ManageFilesDelegate getDelegate() {
        return (ManageFilesDelegate) DelegateFactory.getDelegate(DelegateFactory.MANAGE_FILES);
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the downloadIds
     */
    public String getDownloadIds() {
        return downloadIds;
    }

    /**
     * @param downloadIds the downloadIds to set
     */
    public void setDownloadIds(String downloadIds) {
        this.downloadIds = downloadIds;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @param index
     * @return CaArrayFile
     */
    private CaArrayFile getFile(int index) {
        return getProject().getFilesList().get(index);
    }

    /**
     * @param indexValue
     * @return CaArrayFile
     */
    private CaArrayFile getFile(String indexValue) {
        return getFile(Integer.parseInt(indexValue));
    }

    /**
     * @return the file
     */
    public CaArrayFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(CaArrayFile file) {
        this.file = file;
    }

    /**
     * method to get file name by index.
     *
     * @param index int
     * @return String file name.
     */
    public String getUploadFileName(int index) {
        return getUploadFileName().get(index);
    }

    /**
     * overloaded method to go file entry at index.
     *
     * @param index int
     * @return String content type.
     */
    public String getUploadContentType(int index) {
        return getUploadContentType().get(index);
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
     * Prepares for download by zipping selected files and setting the internal InputStream.
     *
     * @param selectedFiles the files to download
     * @return SUCCESS
     * @throws IOException if
     */
    @SkipValidation
    public String download() throws IOException {
        String[] strings = getDownloadIds().split(",");
        Collection<Long> ids = new HashSet<Long>(strings.length);
        for (String curString : strings) {
            ids.add(Long.parseLong(curString));
        }
        File zipFile = getDelegate().getProjectManagementService().prepareForDownload(ids);

        downloadStream = new FileClosingInputStream(new FileInputStream(zipFile), zipFile);

        return SUCCESS;
    }

    /**
     * @return the stream containing the zip file download
     */
    public InputStream getDownloadStream() {
        return downloadStream;
    }

}
