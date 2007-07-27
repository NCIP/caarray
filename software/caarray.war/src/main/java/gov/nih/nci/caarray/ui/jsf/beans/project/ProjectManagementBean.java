/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.ui.jsf.beans.project;

import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * JSF managed bean for project workspace functionality.
 */
public final class ProjectManagementBean implements Serializable {

    private static final long serialVersionUID = -1814579234979957046L;
    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(ProjectManagementBean.class);

    private Project project;
    private ProjectManagementService projectManagementService;
    private FileManagementService fileManagementService;
    private UIData projectTable;
    private UploadedFile uploadFile;

    /**
     * @return JSF forward
     */
    public String openWorkspace() {
        return "workspace";
    }

    /**
     * @return all projects
     */
    public List<Project> getProjects() {
        return getProjectManagementService().getAll();
    }

    /**
     * Opens the project for the select project.
     *
     * @return navigation to project page
     */
    public String openProject() {
        project = (Project) projectTable.getRowData();

        return "project";
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return UI component
     */
    public UIData getProjectTable() {
        return projectTable;
    }

    /**
     * @param projectTable UI component
     */
    public void setProjectTable(UIData projectTable) {
        this.projectTable = projectTable;
    }

    /**
     * Take an uploaded file, add to current Project.
     *
     * @return back to manage project files
     */
    public String upload() {
        OutputStream os = null;
        try {
            File f = getFile();

            os = new BufferedOutputStream(new FileOutputStream(f));
            os.write(getUploadFile().getBytes());

            Set<File> files = new HashSet<File>();
            files.add(f);
            getProjectManagementService().addFiles(getProject(), files);
        } catch (IOException e) {
            String msg = "Unable to upload file: " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ""));
            LOG.error(msg, e);
            return "manageProjectFiles";
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    String msg = "Unable to upload file: " + e.getMessage();
                    LOG.error(msg, e);
                }
            }
        }

        return "manageProjectFiles";
    }

    /**
     * @return file based upon the uploaded file name
     * @throws IOException on error
     */
    private File getFile() throws IOException {
        File f;
        final String uploadName = getUploadFile().getName();
        int count = 0;
        do {
            String fileName = (count == 0) ? uploadName : count + "-" + uploadName;
            f = new File(System.getProperty("java.io.tmpdir"), fileName);
            ++count;
        } while (!f.createNewFile());
        return f;
    }

    /**
     * Import files.
     * @return back to manage
     */
    public String importProjectFiles() {
        CaArrayFileSet cafs = new CaArrayFileSet();
        cafs.addAll(project.getFiles());
        getFileManagementService().importFiles(cafs);
        return "manageProjectFiles";
    }

    /**
     * @return uploaded file, from form
     */
    public UploadedFile getUploadFile() {
        return uploadFile;
    }

    /**
     * @param uploadFile file, from form
     */
    public void setUploadFile(UploadedFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    private ProjectManagementService getProjectManagementService() {
        if (projectManagementService == null) {
            projectManagementService =
                (ProjectManagementService) ServiceLocator.getInstance().lookup(ProjectManagementService.JNDI_NAME);
        }
        return projectManagementService;
    }

    private FileManagementService getFileManagementService() {
        if (fileManagementService == null) {
            fileManagementService =
                (FileManagementService) ServiceLocator.getInstance().lookup(FileManagementService.JNDI_NAME);
        }
        return fileManagementService;
    }

}
