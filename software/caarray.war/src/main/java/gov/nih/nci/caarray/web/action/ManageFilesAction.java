package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.helper.FileEntry;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * Struts execute method.
     * @return success or failure.
     */
    public String execute() {

        HttpServletRequest request = getRequest();
        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);

        return SUCCESS;
    }

    /**
     * edit a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String manageFiles() throws Exception {

        loadFileEntries();

        HttpServletRequest request = getRequest();
        LabelValue labelValue = new LabelValue("Return to Workspace", "listProjects.action");
        request.setAttribute("workspace", labelValue);

        return SUCCESS;
    }

    private void loadFileEntries() {
        fileEntries = new ArrayList<FileEntry>(project.getFiles().size());
        for (CaArrayFile nextCaArrayFile : project.getFilesList()) {
            fileEntries.add(new FileEntry(nextCaArrayFile));
        }
    }
}
