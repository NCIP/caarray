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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.web.AbstractDownloadTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;


/**
 * @author Scott Miller
 *
 */
@SuppressWarnings("PMD")
public class ProjectFilesActionTest extends AbstractDownloadTest {

    private static final String LIST_IMPORTED = "listImported";
    private static final String LIST_UNIMPORTED = "listUnimported";
    private static final String LIST_IMPORTED_FORM = "listImportedForm";
    private static final String LIST_UNIMPORTED_FORM = "listUnimportedForm";

    private static final String LIST_SUPPLEMENTAL = "listSupplemental";
    private static final String UPLOAD = "upload";
    private static final ProjectManagementServiceStub projectManagementServiceStub = new ProjectManagementServiceStub();
    private static final FileManagementServiceStub fileManagementServiceStub = new FileManagementServiceStub();
    private static final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private static final GenericDataServiceStub dataServiceStub = new LocalGenericDataServiceStub();
    ProjectFilesAction action = new ProjectFilesAction();

    @BeforeClass
    public static void beforeClass() {
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        stub.addLookup(FileManagementService.JNDI_NAME, fileManagementServiceStub);
        stub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        stub.addLookup(GenericDataService.JNDI_NAME, dataServiceStub);
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
        project.getExperiment().setPublicIdentifier("publicId");
        CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.UPLOADED);
        file.setName("testfile1.cel");
        file.setProject(project);
        project.getFiles().add(file);
        this.action.setProject(project);
    }

    @Test
    public void testZipUpload() throws Exception {
        assertEquals(UPLOAD, this.action.upload());
        assertTrue(ActionHelper.getMessages().get(0).contains("fileRequired"));

        File file = File.createTempFile("tmp", ".zip");

        List<File> files = new ArrayList<File>();
        List<String> fileNames = new ArrayList<String>();
        List<String> contentTypes = new ArrayList<String>();
        files.add(file);
        fileNames.add(file.getName());
        contentTypes.add("test");
        this.action.setUpload(files);
        this.action.setUploadFileName(fileNames);
        assertEquals(UPLOAD, this.action.upload());
        assertEquals(1, projectManagementServiceStub.getFilesAddedCount());
    }

    @Test
    public void testZipUploadAndUnpack() throws Exception {
        assertEquals(LIST_UNIMPORTED, this.action.unpackFiles());
        assertTrue(ActionHelper.getMessages().get(0).contains("0 file(s) unpacked"));
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
    }

    @Test
    public void testValidateSelectedImportFiles() {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);

        LocalHttpServletResponse response = new LocalHttpServletResponse();
        ServletActionContext.setResponse(response);
        this.action.validateSelectedImportFiles();
        JSONObject jsonResult = JSONObject.fromObject(response.getResponseText());
        assertTrue(jsonResult.has("validated"));

        // unknown file
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        // array design
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CDF);
        selectedFiles.add(file);
        // invalid status
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);

        response = new LocalHttpServletResponse();
        ServletActionContext.setResponse(response);
        this.action.validateSelectedImportFiles();
        jsonResult = JSONObject.fromObject(response.getResponseText());
        assertTrue(jsonResult.has("confirmMessage"));
    }

    @Test
    public void testValidate() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(FileType.AFFYMETRIX_CDF);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, fileManagementServiceStub.getValidatedFileCount());
    }

    @Test
    public void testSelectRefFiles() throws Exception {
        List<CaArrayFile> projectFiles = new ArrayList<CaArrayFile>();
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        List<CaArrayFile> wrongFiles = new ArrayList<CaArrayFile>();

        // load up the project

        CaArrayFile file = new CaArrayFile();
        file.setName("DummyFile1.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile2.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile3.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile.sdrf");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.MAGE_TAB_SDRF);
        projectFiles.add(file);
        wrongFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile.idf");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.MAGE_TAB_IDF);
        projectFiles.add(file);
        selectedFiles.add(file);

        this.action.getProject().getFiles().addAll(projectFiles);
        this.action.getProject().getFileSet().addAll(projectFiles);

        // after running selected files with sdrf
        // nothing should be sellected.
        this.action.setSelectedFiles(wrongFiles);
        assertEquals(LIST_UNIMPORTED, this.action.findRefFiles());
        assertEquals(1, this.action.getSelectedFiles().size());

        // after running selected files with idf, 3 dummy cels and 1 sdrf
        // should be selected.
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.findRefFiles());
        assertEquals(5, this.action.getSelectedFiles().size());
    }

    private void setCompressedSize(CaArrayFile f, int size) {
        try {
            Method m = CaArrayFile.class.getDeclaredMethod("setCompressedSize", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(f, size);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void setId(CaArrayFile f, Long id) {
        try {
            Method m = CaArrayFile.class.getSuperclass().getSuperclass().getDeclaredMethod("setId", Long.class);
            m.setAccessible(true);
            m.invoke(f, id);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    public void testValidateUnparsedDataFiles() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_RPT);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.MAGE_TAB_SDRF);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(2, fileManagementServiceStub.getValidatedFileCount());
    }

    @Test
    public void testImport() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(FileType.AFFYMETRIX_CDF);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, fileManagementServiceStub.getImportedFilecCount());
    }

    @Test
    public void testAddSupplemental() {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.addSupplementalFiles());
        assertEquals(2, fileManagementServiceStub.getSupplementalFileCount());
    }

    @Test
    public void testDeleteImported() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_IMPORTED, this.action.deleteImportedFiles());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        // make this file associated with a hyb
        CaArrayFile celFile = fileAccessServiceStub.add(AffymetrixArrayDataFiles.TEST3_CEL);
        fileAccessServiceStub.setDeletableStatus(celFile, false);
        assertEquals(LIST_IMPORTED, this.action.deleteImportedFiles());
        assertEquals(0, fileAccessServiceStub.getRemovedFileCount());
    }

    @Test
    public void testDeleteSupplemental() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        fileAccessServiceStub.setDeletableStatus(file, true);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        fileAccessServiceStub.setDeletableStatus(file, true);
        
        
        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());
        assertEquals(2, fileAccessServiceStub.getRemovedFileCount());
    }

    @Test
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
    public void testChangeFileTypes() throws Exception {
        List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();

        for (int i=0; i < 3; i++) {
            file = new CaArrayFile();
            file.setProject(this.action.getProject());
            file.setFileStatus(FileStatus.UPLOADED);
            file.setFileType(null);
            selectedFiles.add(file);
        }

        this.action.setChangeToFileType("AFFYMETRIX_CDF");
        assertEquals(LIST_UNIMPORTED, this.action.changeFileType());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, fileAccessServiceStub.getSavedFileCount());

        assertEquals(FileStatus.UPLOADED, selectedFiles.get(0).getFileStatus());
        this.action.prepare();
        for (CaArrayFile caf: this.action.getProject().getFiles()) {
            assertEquals(FileType.AFFYMETRIX_CDF,caf.getFileType());
        }
    }

    @Test
    public void testEdit() {
        assertEquals(Action.SUCCESS, this.action.editFiles());
    }

    @Test
    public void testDownload() throws Exception {
        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        fas.add(MageTabDataFiles.CAARRAY1X_IDF);


        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        CaArrayFile f1 = new CaArrayFile();
        f1.writeContents(IOUtils.toInputStream(""));
        setId(f1, 1L);
        setCompressedSize(f1, 1024 * 1024 * 1024);
        f1.setName("missing_term_source.idf");
        CaArrayFile f2 = new CaArrayFile();
        f2.writeContents(IOUtils.toInputStream(""));
        setId(f2, 2L);
        setCompressedSize(f2, 1024 * 1024 * 384);
        f2.setName("missing_term_source.sdrf");

        Set<Long> l = new HashSet<Long>();
        l.add(1L);
        l.add(2L);
        // need to catch exception as these are test files and will not be retrieved
        try {
            action.setSelectedFileIds(l);
        } catch(Exception e) {
            //NOOP
        }

        action.setSelectedFiles(Arrays.asList(f1, f2));
        action.setProject(p);

        String result = action.download();
        assertNull(result);

        assertEquals("application/zip", mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", mockResponse.getHeader("Content-disposition"));

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.idf", ze.getName());
        ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.sdrf", ze.getName());
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testSessionTimeoutDuringDownload() throws Exception {
        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        fas.add(MageTabDataFiles.CAARRAY1X_IDF);


        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        CaArrayFile f1 = new CaArrayFile();
        f1.writeContents(IOUtils.toInputStream(""));
        setId(f1, 1L);
        setCompressedSize(f1, 1024 * 1024 * 1024);
        f1.setName("missing_term_source.idf");
        CaArrayFile f2 = new CaArrayFile();
        f2.writeContents(IOUtils.toInputStream(""));
        setId(f2, 2L);
        setCompressedSize(f2, 1024 * 1024 * 384);
        f2.setName("missing_term_source.sdrf");
        Set<Long> l = new HashSet<Long>();
        l.add(1L);
        l.add(2L);
        l.add(3L);
        // need to catch exception as these are test files and will not be retrieved
        try {
            action.setSelectedFileIds(l);
        } catch(Exception e) {
            //NOOP
        }
        action.setSelectedFiles(Arrays.asList(f1, f2));
        action.setProject(p);
        String result = action.download();
        assertEquals("denied",result);
    }

    @Test
    public void validateMessages() {
        assertEquals(Action.SUCCESS, this.action.validationMessages());
    }

    @Test
    public void validateDownloadfiles() throws IOException {
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        final SortedSet<CaArrayFile> fileSet = getDownloadedFileSet();

        TestProject project = new TestProject();
        action.setProject(project);
        project.setFiles(fileSet);

        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(4,action.getFiles().size());

        action.setFileType(FileType.AGILENT_CSV.name());
        action.setFileStatus(FileStatus.IMPORTED.name());
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(1,action.getFiles().size());

        action.setFileType(FileType.GENEPIX_GAL.name());
        action.setFileStatus(FileStatus.VALIDATED_NOT_PARSED.name());
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(1,action.getFiles().size());

        action.setFileStatus(null);
        action.setFileType(FileType.AFFYMETRIX_EXP.name());
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(0,action.getFiles().size());

        action.setFileType(null);
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(4,action.getFiles().size());

        action.setFileType(UNKNOWN_FILE_TYPE);
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(1,action.getFiles().size());

        action.setFileType(KNOWN_FILE_TYPE);
        assertEquals(Action.SUCCESS,action.downloadFiles());
        assertEquals(3,action.getFiles().size());
    }

    @Test
    public void validateListImportedForm() throws IOException {
        final SortedSet<CaArrayFile> fileSet = getImportedFileSet();

        TestProject project = new TestProject();
        assertEquals(0,action.getFiles().size());
        project.setImportedFiles(fileSet);
        action.setProject(project);

        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(3,action.getFiles().size());

        action.setFileType(FileType.AGILENT_CSV.name());
        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(1,action.getFiles().size());

        action.setFileType(FileType.AFFYMETRIX_EXP.name());
        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(0,action.getFiles().size());

        action.setFileType(null);
        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(3,action.getFiles().size());

        action.setFileType(UNKNOWN_FILE_TYPE);
        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(1,action.getFiles().size());

        action.setFileType(KNOWN_FILE_TYPE);
        assertEquals(LIST_IMPORTED_FORM,action.listImportedForm());
        assertEquals(2,action.getFiles().size());
    }

    @Test
    public void validateListUnimportedForm() {
        assertEquals(LIST_UNIMPORTED_FORM, this.action.listUnimportedForm());
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
        assertTrue(action.getUploadFileName().isEmpty());
        action.getUploadFileName().add("Test");
        assertTrue(!action.getUploadFileName().isEmpty());
        action.setUploadFileName(new ArrayList<String>());
        assertTrue(action.getUploadFileName().isEmpty());

        assertNull(action.getFileType());
        action.setFileType(FileType.AFFYMETRIX_CDF.toString());
        assertEquals(FileType.AFFYMETRIX_CDF.toString(), action.getFileType());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testExperimentTreeJson() throws Exception {
        Experiment exp = this.action.getProject().getExperiment();

        Source src1 = new Source();
        src1.setName("Src1");
        src1.setId(252L);
        src1.setExperiment(exp);
        dataServiceStub.save(src1);
        exp.getSources().add(src1);

        Sample smp1  = new Sample();
        smp1.setName("Smp1");
        smp1.setId(781L);
        exp.getSamples().add(smp1);
        src1.getSamples().add(smp1);
        src1.setExperiment(exp);
        smp1.setExperiment(exp);
        dataServiceStub.save(smp1);

        this.action.setNodeType(ExperimentDesignTreeNodeType.ROOT);
        this.action.importTreeNodesJson();
        String expected = "[{\"id\":\"Sources\",\"text\":\"Sources\",\"sort\":\"1\",\"nodeType\":\"EXPERIMENT_SOURCES\",\"leaf\":false}," +
            "{\"id\":\"Samples\",\"text\":\"Samples\",\"sort\":\"2\",\"nodeType\":\"EXPERIMENT_SAMPLES\",\"leaf\":false}," +
            "{\"id\":\"Extracts\",\"text\":\"Extracts\",\"sort\":\"3\",\"nodeType\":\"EXPERIMENT_EXTRACTS\",\"leaf\":true}," +
            "{\"id\":\"LabeledExtracts\",\"text\":\"Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"EXPERIMENT_LABELED_EXTRACTS\",\"leaf\":true}," +
            "{\"id\":\"Hybridizations\",\"text\":\"Hybridizations\",\"sort\":\"5\",\"nodeType\":\"EXPERIMENT_HYBRIDIZATIONS\",\"leaf\":true}]";
        assertEquals(expected, mockResponse.getContentAsString());

        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_SOURCES);
        this.action.setNode("ROOT_Sources");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Sources_252\",\"entityId\":252,\"text\":\"Src1\",\"sort\":\"Src1\",\"nodeType\":\"SOURCE\",\"iconCls\":\"source_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Sources_252_Samples\",\"text\":\"Associated Samples\",\"sort\":\"2\",\"nodeType\":\"BIOMATERIAL_SAMPLES\",\"leaf\":false},{\"id\":\"ROOT_Sources_252_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, mockResponse.getContentAsString());

        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_SAMPLES);
        this.action.setNode("ROOT_Samples");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Samples_781\",\"entityId\":781,\"text\":\"Smp1\",\"sort\":\"Smp1\",\"nodeType\":\"SAMPLE\",\"iconCls\":\"sample_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Samples_781_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Samples_781_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Samples_781_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, mockResponse.getContentAsString());

        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_EXTRACTS);
        this.action.setNode("ROOT_Extracts");
        this.action.importTreeNodesJson();
        assertEquals("[]", mockResponse.getContentAsString());

        mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.BIOMATERIAL_SAMPLES);
        this.action.setNode("ROOT_Sources_252_Samples");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Sources_252_Samples_781\",\"entityId\":781,\"text\":\"Smp1\",\"sort\":\"Smp1\",\"nodeType\":\"SAMPLE\",\"iconCls\":\"sample_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Sources_252_Samples_781_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Samples_781_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Samples_781_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, mockResponse.getContentAsString());

        mockResponse = new MockHttpServletResponse();
        this.action.setNodeType(ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS);
        ServletActionContext.setResponse(mockResponse);
        this.action.setNode("ROOT_Sources_252_Samples_781_Extracts");
        this.action.importTreeNodesJson();
        assertEquals("[]", mockResponse.getContentAsString());
    }

    private static final String UNKNOWN_FILE_TYPE = "(Unknown File Types)";
    private static final String KNOWN_FILE_TYPE = "(Supported File Types)";

    @Test
    public void testListUnimported() throws IOException{

        assertEquals(0,action.getFiles().size());
        final SortedSet<CaArrayFile> fileSet = getUnimportedFileSet();

        TestProject project = new TestProject();
        project.setUnimportedFiles(fileSet);
        action.setProject(project);

        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(6,action.getFiles().size());
        assertEquals(3,action.getFileStatusCountMap().get(FileStatus.UPLOADED).intValue());
        assertEquals(3,action.getFileStatusCountMap().get(FileStatus.IMPORT_FAILED).intValue());

        action.setFileType(FileType.AGILENT_CSV.name());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(2,action.getFiles().size());

        action.setFileType(null);
        action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(FileStatus.UPLOADED.name(), action.getFileStatus());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(3,action.getFiles().size());

        action.setFileType(FileType.AGILENT_CSV.name());
        assertEquals(FileType.AGILENT_CSV.name(), action.getFileType());
        action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(1,action.getFiles().size());

        action.setFileType(UNKNOWN_FILE_TYPE);
        action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(1,action.getFiles().size());

        action.setFileStatus(FileStatus.IN_QUEUE.name());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(0,action.getFiles().size());

        action.setFileType(KNOWN_FILE_TYPE);
        action.setFileStatus(null);
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(5,action.getFiles().size());

        action.setFileType(KNOWN_FILE_TYPE);
        action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED,action.listUnimported());
        assertEquals(2,action.getFiles().size());
        assertEquals(2,action.getFileStatusCountMap().get(FileStatus.UPLOADED).intValue());
    }

    private SortedSet<CaArrayFile> getUnimportedFileSet() throws IOException {
        SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        CaArrayFile file1 = new CaArrayFile();
        file1.writeContents(IOUtils.toInputStream(""));
        file1.setName("file1");
        file1.setType(FileType.AFFYMETRIX_CDF.name());
        file1.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file1);

        CaArrayFile file2 = new CaArrayFile();
        file2.writeContents(IOUtils.toInputStream(""));
        file2.setName("file2");
        file2.setType(FileType.AFFYMETRIX_CDF.name());
        file2.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file2);

        CaArrayFile file3 = new CaArrayFile();
        file3.writeContents(IOUtils.toInputStream(""));
        file3.setName("file3");
        file3.setType(FileType.AGILENT_CSV.name());
        file3.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file3);

        CaArrayFile file4 = new CaArrayFile();
        file4.writeContents(IOUtils.toInputStream(""));
        file4.setName("file4");
        file4.setType(FileType.AGILENT_CSV.name());
        file4.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file4);

        CaArrayFile file5 = new CaArrayFile();
        file5.writeContents(IOUtils.toInputStream(""));
        file5.setName("file5");
        file5.setFileType(FileType.MAGE_TAB_ADF);
        file5.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file5);

        CaArrayFile file6 = new CaArrayFile();
        file6.writeContents(IOUtils.toInputStream(""));
        file6.setName("file6");
        file6.setFileType(null);
        file6.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file6);
        return fileSet;
    }

    private SortedSet<CaArrayFile> getImportedFileSet() throws IOException {
        SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        CaArrayFile file1 = new CaArrayFile();
        file1.writeContents(IOUtils.toInputStream(""));
        file1.setName("file1");
        file1.setType(FileType.AFFYMETRIX_CDF.name());
        file1.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file1);

        CaArrayFile file2 = new CaArrayFile();
        file2.writeContents(IOUtils.toInputStream(""));
        file2.setName("file2");
        file2.setType(FileType.AGILENT_CSV.name());
        file2.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file2);

        CaArrayFile file3 = new CaArrayFile();
        file3.writeContents(IOUtils.toInputStream(""));
        file3.setName("file3");
        file3.setType(null);
        file3.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file3);
        return fileSet;
    }

    private SortedSet<CaArrayFile> getDownloadedFileSet() throws IOException {
        SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        CaArrayFile file1 = new CaArrayFile();
        file1.writeContents(IOUtils.toInputStream(""));
        file1.setName("file1");
        file1.setType(FileType.AFFYMETRIX_CDF.name());
        file1.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        fileSet.add(file1);

        CaArrayFile file2 = new CaArrayFile();
        file2.writeContents(IOUtils.toInputStream(""));
        file2.setName("file2");
        file2.setType(FileType.AGILENT_CSV.name());
        file2.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file2);

        CaArrayFile file3 = new CaArrayFile();
        file3.writeContents(IOUtils.toInputStream(""));
        file3.setName("file3");
        file3.setType(null);
        file3.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file3);

        CaArrayFile file4 = new CaArrayFile();
        file4.writeContents(IOUtils.toInputStream(""));
        file4.setName("file4");
        file4.setType(FileType.GENEPIX_GAL.name());
        file4.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        fileSet.add(file4);
        return fileSet;
    }


    @Test
    public void testDownloadOptions() throws IOException {
        CaArrayFile file = new CaArrayFile();
        file.writeContents(IOUtils.toInputStream(""));
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.GEO_GPL);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.writeContents(IOUtils.toInputStream(""));
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.AFFYMETRIX_CEL);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.writeContents(IOUtils.toInputStream(""));
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.AFFYMETRIX_CHP);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.writeContents(IOUtils.toInputStream(""));
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileType.MAGE_TAB_IDF);
        this.action.getProject().getFiles().add(file);

        String result = this.action.downloadOptions();
        assertEquals("success", result);

        assertEquals(2, this.action.getFileTypes().size());
        assertTrue(this.action.getFileTypes().contains(FileType.AFFYMETRIX_CEL.name()));
        assertTrue(this.action.getFileTypes().contains(FileType.AFFYMETRIX_CHP.name()));
    }


    private class LocalHttpServletResponse extends MockHttpServletResponse {
        private StringWriter out = new StringWriter();

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(out);
        }

        public String getResponseText() {
            return out.toString();
        }
    }

    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        private Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public void save(PersistentObject object) {
            super.save(object);
            objMap.put(object.getId(), object);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            Object candidate = objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }
    }

    private class TestProject extends Project {

        private static final long serialVersionUID = 1L;
        SortedSet<CaArrayFile> unimportedFiles;
        SortedSet<CaArrayFile> importedFiles;
        SortedSet<CaArrayFile> files;

        public SortedSet<CaArrayFile> getUnImportedFiles() {
            return unimportedFiles;
        }

        public void setUnimportedFiles(SortedSet<CaArrayFile> unimportedFiles) {
            this.unimportedFiles = unimportedFiles;
        }

        public SortedSet<CaArrayFile> getImportedFiles() {
            return importedFiles;
        }

        public void setImportedFiles(SortedSet<CaArrayFile> importedFiles) {
            this.importedFiles = importedFiles;
        }

        public SortedSet<CaArrayFile> getFiles() {
            return files;
        }

        public void setFiles(SortedSet<CaArrayFile> files) {
            this.files = files;
        }

    }
}
