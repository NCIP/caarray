package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ManageFilesDelegate;
import gov.nih.nci.caarray.web.util.CacheManager;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.Enumeration;
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
public class ManageFilesAction extends BaseAction implements Preparable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private List<CaArrayFile> files;
    private CaArrayFile file;
    private Project project;
    private String menu;

    private Long fileId;
    private List<LabelValue> navigationList;


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
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
    @SuppressWarnings("PMD")
    public String edit() throws Exception {
        setMenu("FileEditLinks");

        navigationList = new ArrayList<LabelValue>();
        LabelValue manageFiles = new LabelValue("Manage Files",
                "File_manage.action?project.id=" + getProject().getId());
        navigationList.add(manageFiles);

        return SUCCESS;
    }

    /**
     * manage files for a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String manage() throws Exception {
        setMenu("FileManageLinks");
        return SUCCESS;
    }

    /**
     * view messages for file.
     * @return String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String messages() throws Exception {
        setMenu("FileMessagesLinks");

        //remove this too!
        navigationList = new ArrayList<LabelValue>();
        LabelValue manageFiles = new LabelValue("Manage Files",
                "File_manage.action?project.id=" + getProject().getId());
        navigationList.add(manageFiles);

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
    @SuppressWarnings({ "PMD", "unchecked" })
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
    @SuppressWarnings({ "PMD", "unchecked" })
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

    private CaArrayFile getFile(int index) {
        return getFiles().get(index);
    }

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
     * @return the navigationList
     */
    public List<LabelValue> getNavigationList() {
        return navigationList;
    }

    /**
     * @param navigationList the navigationList to set
     */
    public void setNavigationList(List<LabelValue> navigationList) {
        this.navigationList = navigationList;
    }
}
