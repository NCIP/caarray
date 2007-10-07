package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ManageFilesDelegate;
import gov.nih.nci.caarray.web.util.CacheManager;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Manages Files for a particular project.
 * @author John Hedden
 *
 */
@Validation
public class FileManageAction extends BaseAction implements Preparable {

    private static final long serialVersionUID = 1L;
    private String menu = null;
    private CaArrayFile file = new CaArrayFile();
    private Project project = Project.createNew();
    private List<CaArrayFile> files = new ArrayList<CaArrayFile>();
    private List<File> uploads = new ArrayList<File>();
    private List<String> uploadFileNames = new ArrayList<String>();
    private List<String> uploadContentTypes = new ArrayList<String>();

    private Long fileId;

    /**
     * {@inheritDoc}
     */
    public void prepare() throws Exception {
        if (getProject() != null && getProject().getId() != null) {
            setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
            loadFileEntries();
        }
    }

    /**
     * edit files associated with a project.
     * @return path String
     * @throws Exception Exception
     */
    public String edit() throws Exception {
        setMenu("FileEditLinks");
        return SUCCESS;
    }

    /**
     * Toggles the browsability status.
     * @return success
     * @exception Exception on error
     */
    public String toggle() throws Exception {
        setMenu("FileEditLinks");
        getDelegate().getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return SUCCESS;
    }

    /**
     * manage files for a project.
     * @return path String
     * @throws Exception Exception
     */
    public String manage() throws Exception {
        setMenu("FileManageLinks");
        return SUCCESS;
    }

    /**
     * view messages for file.
     * @return String
     * @throws Exception Exception
     */
    public String messages() throws Exception {
        setMenu("FileMessagesLinks");

        for (CaArrayFile caArrayFile : getFiles()) {
            if (caArrayFile.getId().compareTo(fileId) == 0) {
                setFile(caArrayFile);
            }
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on
     * Struts2 indexed properties first.  For now just go through request.getParamenterNames().
     *
     * validates file.
     * @return String
     * @throws Exception Exception
     */
    public String validateFile() throws Exception {
        setMenu("FileManageLinks");

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            getDelegate().getFileManagementService().validateFiles(fileSet);
        }
        return SUCCESS;
    }

    /**
     * This method needs to be re-worked at some pt. I need to get a handle on
     * Struts2 indexed properties first.  For now just go through request.getParamenterNames().
     *
     * validates file.
     * @return String
     * @throws Exception Exception
     */
    public String importFile() throws Exception {
        setMenu("FileManageLinks");

        HttpServletRequest request = getRequest();
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        addSelectedFiles(fileSet, request);
        if (!fileSet.getFiles().isEmpty()) {
            getDelegate().getFileManagementService().importFiles(getProject(), fileSet);
        }
        return SUCCESS;
    }
    /**
     * This method needs to be re-worked at some pt. I need to get a handle on
     * Struts2 indexed properties first.  For now just go through request.getParamenterNames().
     *
     * validates file.
     * @return String
     * @throws Exception Exception
     */
    public String removeFile() throws Exception {
        setMenu("FileManageLinks");

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
     * uploads file.
     * @return String
     * @throws Exception Exception
     */
    public String upload() throws Exception {

        setMenu("FileManageLinks");

        int index = 0;
        for (Iterator<File> iter = getUpload().iterator(); iter.hasNext();) {
            File uploadedFile = iter.next();
            try {

                String fileName = getUploadFileName(index);

                CaArrayFile caArrayFile
                    = getDelegate().getProjectManagementService().addFile(getProject(), uploadedFile, fileName);
                files.add(caArrayFile);

            } catch (Exception e) {
                String msg = "Unable to upload file: " + e.getMessage();
                LOG.error(msg, e);
                addActionError(getText("errorUploading"));
                return INPUT;
            }
            index++;
        } //end for

        return SUCCESS;
    }

    private void addSelectedFiles(CaArrayFileSet fileSet, HttpServletRequest request) {
        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
          String name = myenum.nextElement();
          String myREGEX = ":";
          Pattern p = Pattern.compile(myREGEX);
          String[] items = p.split(name);
          //if pattern is like fileEntries:0:selected we know a checkbox has been selected
          if (items[0].equalsIgnoreCase("files") && items[2].equalsIgnoreCase("selected")) {
              String fileIndex = items[1];
              fileSet.add(getFile(fileIndex));
          }
        }
    }

    /**
     * loads the file entries.
     */
    private void loadFileEntries() {
        setFiles(new ArrayList<CaArrayFile>(getProject().getFiles().size()));
        for (CaArrayFile caArrayFile : getProject().getFilesList()) {
            files.add(caArrayFile);
        }
    }

    /**
     * @return the fileTypes
     */
    public List<LabelValue> getFileTypes() {
        return CacheManager.getInstance().getFileTypes();
    }

    /**
     * gets the delegate from factory.
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
     * @return the id
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
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
     * @return the files
     */
    public List<CaArrayFile> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<CaArrayFile> files) {
        this.files = files;
    }

    /**
     * @param index
     * @return CaArrayFile
     */
    private CaArrayFile getFile(int index) {
        return getFiles().get(index);
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
     * @param index int
     * @return String file name.
     */
    public String getUploadFileName(int index) {
        return getUploadFileName().get(index);
    }

    /**
     * overloaded method to go file entry at index.
     * @param index int
     * @return String content type.
     */
    public String getUploadContentType(int index) {
        return getUploadContentType().get(index);
    }

    /**
     * uploaded file.
     * @return uploads uploaded files
     */
    public List<File> getUpload() {
        return this.uploads;
    }

    /**
     * sets file uploads.
     * @param inUploads List
     */
    public void setUpload(List<File> inUploads) {
        this.uploads = inUploads;
    }

    /**
     * returns uploaded file name.
     * @return uploadFileNames
     */
    public List<String> getUploadFileName() {
        return this.uploadFileNames;
    }

    /**
     * sets uploaded file names.
     * @param inUploadFileNames List
     */
    public void setUploadFileName(List<String> inUploadFileNames) {
        this.uploadFileNames = inUploadFileNames;
    }

    /**
     * returns uploaded content type.
     * @return uploadContentTypes
     */
    public List<String> getUploadContentType() {
        return this.uploadContentTypes;
    }

    /**
     * sets upload content type.
     * @param inContentTypes List
     */
    public void setUploadContentType(List<String> inContentTypes) {
        this.uploadContentTypes = inContentTypes;
    }
}

