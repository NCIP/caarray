package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.caarray.web.exception.CaArrayException;
import gov.nih.nci.caarray.web.helper.FileEntry;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ManageFilesAction.
 * @author John Hedden
 *
 */
public class ManageFilesAction extends BaseAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Project project;
    private List<FileEntry> fileEntries;
    private FileEntry fileEntry;
    private CaArrayFile caArrayFile;

    /**
     * edit a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String manageFiles() throws Exception {
        HttpServletRequest request = getRequest();
        HttpSession session = getSession();
        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);

        project = (Project) session.getAttribute("project");
        loadFileEntries();
        loadFileTypes();

        return SUCCESS;
    }

    /**
     * uploads file.
     * @return String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String viewMessages() throws Exception {

        HttpSession session = getSession();
        HttpServletRequest request = getRequest();
        String projectFile = (String) request.getParameter("projectFile");
        int projectInt;
        if (projectFile == null) {
            projectInt = 0;
        } else {
            projectInt = new Integer(projectFile).intValue();
        }


        List<FileEntry> fileEntries = (List<FileEntry>) session.getAttribute("fileEntries");

        for (int i = 0; i < fileEntries.size(); i++) {
            if (i == projectInt) {
                fileEntry = fileEntries.get(i);
            }
        }
        request.setAttribute("fileEntry", fileEntry);

        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);
        LabelValue manageFiles = new LabelValue("Manage Files", "manageFiles.action");
        request.setAttribute("manageFiles", manageFiles);
        return SUCCESS;
    }

    /**
     * uploads file.
     * @return String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String validateFile() throws Exception {
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Enumeration<String> myenum = request.getParameterNames();

        while (myenum.hasMoreElements()) {
          String name = (String) myenum.nextElement();
          String values = request.getParameter(name);

          String REGEX = ":";
          Pattern p = Pattern.compile(REGEX);
          String[] items = p.split(name);

          if (items[2].equalsIgnoreCase("selected")){
              String file = items[1];
              Project project = (Project) session.getAttribute("project");
              List<FileEntry> files = (List<FileEntry>) session.getAttribute("fileEntries");
              CaArrayFileSet fileSet = new CaArrayFileSet(project);
              for (int j = 0; j < files.size(); j++) {
                  if (String.valueOf(j).equalsIgnoreCase(file)) {

                      try {
                          fileEntry = files.get(j);
                          fileSet.add(fileEntry.getCaArrayFile());
                          getDelegate().getFileManagementService().validateFiles(fileSet);
                          return SUCCESS;
                      } catch (Exception e)
                      {
                          //e.printStackTrace();
                      }
                      finally {
                         //do nothing
                      }
                  }
              }
          }
        }
        return SUCCESS;
    }

    /**
     * import the file.
     * @return string String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String importFile() throws Exception {
        /*
        HttpSession session = getSession();
        HttpServletRequest request = getRequest();

        Enumeration<String> myenum = request.getParameterNames();

        while (myenum.hasMoreElements()) {
          String name = (String) myenum.nextElement();
          String values = request.getParameter(name);

          String REGEX = ":";
          Pattern p = Pattern.compile(REGEX);
          String[] items = p.split(name);

          if (items[2].equalsIgnoreCase("selected")){
              String file = items[1];
              Project project = (Project) session.getAttribute("project");
              List<FileEntry> files = (List<FileEntry>) session.getAttribute("fileEntries");
              CaArrayFileSet fileSet = new CaArrayFileSet(project);
              for (int j = 0; j < files.size(); j++) {
                  if (String.valueOf(j).equalsIgnoreCase(file)) {

                      try {
                          fileEntry = files.get(j);
                          fileSet.add(fileEntry.getCaArrayFile());
                          getDelegate().getFileManagementService().importFiles(project, fileSet);;
                          return SUCCESS;
                      } catch (Exception e)
                      {
                          //e.printStackTrace();
                      }
                      finally {
                         //do nothing
                      }
                  }
              }
          }
        }
        return SUCCESS;
        */
        addActionError(getText("maxLengthExceeded"));
        return INPUT;
    }

    private void loadFileEntries() {
        fileEntries = new ArrayList<FileEntry>(project.getFiles().size());
        for (CaArrayFile nextCaArrayFile : project.getFilesList()) {
            fileEntries.add(new FileEntry(nextCaArrayFile));
        }
        HttpSession session = getSession();
        session.setAttribute("fileEntries", fileEntries);
    }

    public void loadFileTypes() {
        List<LabelValue> items = new ArrayList<LabelValue>();
        items.add(new LabelValue("", "UNKNOWN"));
        for (FileType fileType : FileType.getTypes()) {
            items.add(new LabelValue(fileType.getName(), fileType.getName()));
        }
        HttpSession session = getSession();
        session.setAttribute("fileTypes", items);
    }
    /**
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     * @throws CaArrayException
     */
    private ProjectDelegate getDelegate() throws CaArrayException {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }
}
