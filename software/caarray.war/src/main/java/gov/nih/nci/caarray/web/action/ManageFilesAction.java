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
import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Manages Files for a particular project.
 * @author John Hedden
 *
 */
@Validation
public class ManageFilesAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private List<LabelValue> fileTypes;
    private List<CaArrayFile> files;
    private CaArrayFile file;
    private Long fileId;

    private Project project;
    private Long projectId;

    private String menu;

    /**
     * edit files associated with a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String edit() throws Exception {
        setMenu("FileEditLinks");
        HttpSession session = getSession();
        setProject(getDelegate().getProjectManagementService().getProject(projectId));
        session.setAttribute("myProject", getProject());

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
        HttpSession session = getSession();
        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);
        loadFileEntries();

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
        HttpSession session = getSession();
        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);
        loadFileEntries();

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
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();

        /**
         * Could have gone through getFiles() get files in list but
         * CaArrayFile class does NOT have "selected" attribute. Thus
         * Enumeration made sense.
         **/
        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
          String name = myenum.nextElement();

          String myREGEX = ":";
          Pattern p = Pattern.compile(myREGEX);
          String[] items = p.split(name);

          //if pattern is like fileEntries:0:selected we know a checkbox has been selected
          if (items[0].equalsIgnoreCase("files") && items[2].equalsIgnoreCase("selected")) {
              String fileIndex = items[1];
              CaArrayFileSet fileSet = getSelectedFiles(fileIndex);
              getDelegate().getFileManagementService().validateFiles(fileSet);
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
    public String importFile() throws Exception {
        setMenu("FileManageLinks");
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();

        /**
         * Could have gone through getFiles() get files in list but
         * CaArrayFile class does NOT have "selected" attribute. Thus
         * Enumeration made sense.
         **/
        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
          String name = myenum.nextElement();

          String myREGEX = ":";
          Pattern p = Pattern.compile(myREGEX);
          String[] items = p.split(name);

          //if pattern is like fileEntries:0:selected we know a checkbox has been selected
          if (items[0].equalsIgnoreCase("files") && items[2].equalsIgnoreCase("selected")) {
              String fileIndex = items[1];
              CaArrayFileSet fileSet = getSelectedFiles(fileIndex);
              getDelegate().getFileManagementService().importFiles(getProject(), fileSet);
          }
        }
        return SUCCESS;
    }

    /**
     * get selected fileset.
     * @param fileIndex String
     * @return CaArrayFileSet CaArrayFileSet
     */
    public CaArrayFileSet getSelectedFiles(String fileIndex) {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        CaArrayFile caArrayFile = getFile(Integer.parseInt(fileIndex));
        fileSet.add(caArrayFile);

        return fileSet;
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
    public Long getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
     * overloaded method to go file entry at index.
     * @param index int
     * @return fileEntry FileEntry
     */
    public CaArrayFile getFile(int index) {
        return getFiles().get(index);
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
}
