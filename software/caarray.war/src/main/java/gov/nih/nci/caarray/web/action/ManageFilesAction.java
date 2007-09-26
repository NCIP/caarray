package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ManageFilesDelegate;
import gov.nih.nci.caarray.web.exception.CaArrayException;
import gov.nih.nci.caarray.web.helper.FileEntry;
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

    private Project project;
    private List<FileEntry> fileEntries;
    private List<LabelValue> fileTypes;
    private FileEntry fileEntry;
    private CaArrayFile caArrayFile;
    private Long id;
    private List<LabelValue> navigationList;

    /**
     * edit files associated with a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String edit() throws Exception {
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue manageFiles = new LabelValue("Manage Files", "File_manage.action");
        LabelValue labelValue = new LabelValue("Return to Workspace", "Project_list.action");
        navigationList.add(manageFiles);
        navigationList.add(labelValue);

        HttpSession session = getSession();
        setProject(getDelegate().getProjectManagementService().getProject(id));
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
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue labelValue = new LabelValue("Return to Workspace", "Project_list.action");
        navigationList.add(labelValue);

        HttpSession session = getSession();
        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();
        loadFileTypes();

        return SUCCESS;
    }

    /**
     * view messages for file.
     * @return String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String messages() throws Exception {
        //Action menu: to be removed at some point when have better idea.
        navigationList = new ArrayList<LabelValue>();
        LabelValue manageFiles = new LabelValue("Manage Files", "File_manage.action");
        LabelValue labelValue = new LabelValue("Return to Workspace", "Project_list.action");
        navigationList.add(manageFiles);
        navigationList.add(labelValue);

        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();

        String fileId = request.getParameter("fileId");
        for (FileEntry myFileEntry : getFileEntries()) {
            if (myFileEntry.getCaArrayFile().getId() == Long.parseLong(fileId)) {
                setFileEntry(myFileEntry);
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
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();

        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
          String name = myenum.nextElement();

          String myREGEX = ":";
          Pattern p = Pattern.compile(myREGEX);
          String[] items = p.split(name);

          //if pattern is like fileEntries:0:selected we know a checkbox has been selected
          if (items[0].equalsIgnoreCase("fileEntries") && items[2].equalsIgnoreCase("selected")) {
              String fileId = items[1];
              CaArrayFileSet fileSet = getSelectedFiles(fileId);
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
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Project myProject = (Project) session.getAttribute("myProject");
        setProject(myProject);

        loadFileEntries();

        Enumeration<String> myenum = request.getParameterNames();
        while (myenum.hasMoreElements()) {
          String name = myenum.nextElement();

          String myREGEX = ":";
          Pattern p = Pattern.compile(myREGEX);
          String[] items = p.split(name);

          //if pattern is like fileEntries:0:selected we know a checkbox has been selected
          if (items[0].equalsIgnoreCase("fileEntries") && items[2].equalsIgnoreCase("selected")) {
              String fileId = items[1];
              CaArrayFileSet fileSet = getSelectedFiles(fileId);
              getDelegate().getFileManagementService().importFiles(getProject(), fileSet);
          }
        }
        return SUCCESS;
    }

    /**
     * get selected fileset.
     * @param fileId String
     * @return CaArrayFileSet CaArrayFileSet
     */
    public CaArrayFileSet getSelectedFiles(String fileId) {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        for (FileEntry myFileEntry : getFileEntries()) {
            if (String.valueOf(myFileEntry.getCaArrayFile().getId()).equalsIgnoreCase(fileId)) {
                fileSet.add(myFileEntry.getCaArrayFile());
            }
        }
        return fileSet;
    }

    /**
     * loads the file entries.
     */
    private void loadFileEntries() {
        setFileEntries(new ArrayList<FileEntry>(getProject().getFiles().size()));
        for (CaArrayFile nextCaArrayFile : getProject().getFilesList()) {
            fileEntries.add(new FileEntry(nextCaArrayFile));
        }
    }

    /**
     * Returns all <code>FileTypes</code> as <code>SelectItem</code>.
     * Put these in application scope at some pt.
     */
    public void loadFileTypes() {
        List<LabelValue> items = new ArrayList<LabelValue>();
        items.add(new LabelValue("", "UNKNOWN"));
        for (FileType fileType : FileType.getTypes()) {
            items.add(new LabelValue(fileType.getName(), fileType.getName()));
        }
        setFileTypes(items);
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
     * @return the fileEntries
     */
    public List<FileEntry> getFileEntries() {
        return fileEntries;
    }

    /**
     * @param fileEntries the fileEntries to set
     */
    public void setFileEntries(List<FileEntry> fileEntries) {
        this.fileEntries = fileEntries;
    }

    /**
     * overloaded method to go file entry at index.
     * @param index int
     * @return fileEntry FileEntry
     */
    public FileEntry getFileEntries(int index) {
        return getFileEntries().get(index);
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the caArrayFile
     */
    public CaArrayFile getCaArrayFile() {
        return caArrayFile;
    }

    /**
     * @param caArrayFile the caArrayFile to set
     */
    public void setCaArrayFile(CaArrayFile caArrayFile) {
        this.caArrayFile = caArrayFile;
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

    /**
     * @return the fileEntry
     */
    public FileEntry getFileEntry() {
        return fileEntry;
    }

    /**
     * @param fileEntry the fileEntry to set
     */
    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry;
    }

    /**
     * @return the fileTypes
     */
    public List<LabelValue> getFileTypes() {
        return fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(List<LabelValue> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     * @throws CaArrayException
     */
    private ManageFilesDelegate getDelegate() throws CaArrayException {
        return (ManageFilesDelegate) DelegateFactory.getDelegate(DelegateFactory.MANAGE_FILES);
    }
}
