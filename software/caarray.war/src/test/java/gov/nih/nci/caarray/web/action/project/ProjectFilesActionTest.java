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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.opensymphony.xwork2.Action;


/**
 * @author Scott Miller
 *
 */
public class ProjectFilesActionTest {

    private static final String LIST_IMPORTED = "listImported";
    private static final String LIST_UNIMPORTED = "listUnimported";
    private static final String LIST_SUPPLEMENTAL = "listSupplemental";
    private static final String UPLOAD = "upload";
    private static final ProjectManagementServiceStub projectManagementServiceStub = new ProjectManagementServiceStub();
    private static final FileManagementServiceStub fileManagementServiceStub = new FileManagementServiceStub();
    private static final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    ProjectFilesAction action = new ProjectFilesAction();

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        stub.addLookup(FileManagementService.JNDI_NAME, fileManagementServiceStub);
        stub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
    }

    @Before
    public void before() {
        projectManagementServiceStub.reset();
        fileManagementServiceStub.reset();
        fileAccessServiceStub.reset();

        this.action = new ProjectFilesAction() {
            private static final long serialVersionUID = 1L;

            /**
             * {@inheritDoc}
             */
            @Override
            protected void refreshProject() {
                // empty on purpose
            }
        };
        Project project = new Project();
        CaArrayFile file = new CaArrayFile();
        file.setName("testfile1.cel");
        file.setProject(project);
        project.getFiles().add(file);
        this.action.setProject(project);
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @Test
    public void testUpload() throws Exception {
        this.action.setUpload(new ArrayList<File>());
        assertEquals(UPLOAD, this.action.upload());
        assertEquals(0, projectManagementServiceStub.getFilesAddedCount());

        List<String> fileNames = new ArrayList<String>();
        fileNames.add("testfile1.cel");
        this.action.setUploadFileName(fileNames);

        List<File> files = new ArrayList<File>();
        files.add(new File("testfile1.cel"));
        this.action.setUpload(files);

        assertEquals(UPLOAD, this.action.upload());
        assertEquals(0, projectManagementServiceStub.getFilesAddedCount());

        fileNames.add("  ");
        files.add(new File("other.cel"));

        assertEquals(UPLOAD, this.action.upload());
        assertEquals(0, projectManagementServiceStub.getFilesAddedCount());

        fileNames.add("other.cel");
        files.add(new File("other.cel"));

        assertEquals(UPLOAD, this.action.upload());
        assertEquals(1, projectManagementServiceStub.getFilesAddedCount());
    }

    @Test
    @SuppressWarnings("PMD")
    public void testValidate() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, fileManagementServiceStub.getValidatedFileCount());
    }

    @Test
    @SuppressWarnings("PMD")
    public void testImport() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(2, fileManagementServiceStub.getImportedFilecCount());
    }

    @Test
    @SuppressWarnings("PMD")
    public void testDelete() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.deleteFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.deleteFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, fileAccessServiceStub.getRemovedFileCount());
    }
    @Test
    @SuppressWarnings("PMD")
    public void testDeleteSupplemental() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());
        assertEquals(2, fileAccessServiceStub.getRemovedFileCount());
    }

    @Test
    @SuppressWarnings("PMD")
    public void testSave() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setValidationResult(new FileValidationResult(new File("test")));
        file.getValidationResult().addMessage(Type.ERROR, "foo");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(6, fileAccessServiceStub.getSavedFileCount());
        assertEquals(FileStatus.UPLOADED, selectedFiles.get(0).getFileStatus());
        assertEquals(0, selectedFiles.get(0).getValidationResult().getMessages().size());
    }

    @Test
    public void testEdit() {
        assertEquals(Action.SUCCESS, this.action.editFiles());
    }

    @Test
    public void validateMessages() {
        assertEquals(Action.SUCCESS, this.action.validationMessages());
    }

    @Test
    public void validateDownloadfiles() {
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
    }

    @Test
    public void testLoadListActions() {
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals("table", this.action.listUnimportedTable());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());

        assertEquals(LIST_IMPORTED, this.action.listImported());
        assertEquals(LIST_IMPORTED, this.action.getListAction());
        assertEquals(LIST_SUPPLEMENTAL, this.action.listSupplemental());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());
        assertEquals("table", this.action.listImportedTable());
        assertEquals(LIST_IMPORTED, this.action.getListAction());
        assertEquals("table", this.action.listSupplementalTable());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());
    }

    @Test
    public void testFilterActions() {
        assertEquals(Action.SUCCESS, action.downloadFilesList());
        assertEquals(Action.SUCCESS, action.downloadFilesListTable());
        assertTrue(action.getUploadContentType().isEmpty());
        action.getUploadContentType().add("Test");
        assertTrue(!action.getUploadContentType().isEmpty());
        action.setUploadContentType(new ArrayList<String>());
        assertTrue(action.getUploadContentType().isEmpty());

        assertTrue(!action.getAllExtensions().isEmpty());
        action.setAllExtensions(new HashSet<String>());
        assertTrue(action.getAllExtensions().isEmpty());

        assertNull(action.getFileType());
        action.setFileType(FileType.AFFYMETRIX_CDF);
        assertEquals(FileType.AFFYMETRIX_CDF, action.getFileType());

    }
}
