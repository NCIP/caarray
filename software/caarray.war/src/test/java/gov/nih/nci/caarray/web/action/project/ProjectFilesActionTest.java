//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.web.AbstractDownloadTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.opensymphony.xwork2.Action;

@SuppressWarnings({"serial", "deprecation"})
public class ProjectFilesActionTest extends AbstractDownloadTest {

    private static final String LIST_IMPORTED = "listImported";
    private static final String LIST_UNIMPORTED = "listUnimported";
    private static final String LIST_IMPORTED_FORM = "listImportedForm";
    private static final String LIST_UNIMPORTED_FORM = "listUnimportedForm";

    private static final String LIST_SUPPLEMENTAL = "listSupplemental";

    private final ProjectManagementServiceStub projectManagementServiceStub = new ProjectManagementServiceStub();
    private final LocalFileManagementServiceStub fileManagementServiceStub = new LocalFileManagementServiceStub();
    private final LocalGenericDataServiceStub dataServiceStub = new LocalGenericDataServiceStub();
    ProjectFilesAction action = new ProjectFilesAction();

    @Before
    public void before() {
        final ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, this.projectManagementServiceStub);
        stub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementServiceStub);
        stub.addLookup(GenericDataService.JNDI_NAME, this.dataServiceStub);
        stub.addLookup(FileAccessServiceStub.JNDI_NAME, this.fasStub);

        this.projectManagementServiceStub.reset();
        this.fileManagementServiceStub.reset();
        this.fileManagementServiceStub.reimportCount = 0;

        this.action = new ProjectFilesAction() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void refreshProject() {
                // empty on purpose
            }
        };
        final Project project = new Project();
        project.getExperiment().setPublicIdentifier("publicId");
        final CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.UPLOADED);
        file.setName("testfile1.cel");
        file.setProject(project);
        project.getFiles().add(file);
        this.action.setProject(project);
        CaArrayUsernameHolder.setUser(STANDARD_USER);
    }

    @Test
    public void testZipUploadAndUnpack() throws Exception {
        assertEquals(LIST_UNIMPORTED, this.action.unpackFiles());
        assertTrue(ActionHelper.getMessages().get(0).contains("0 file(s) unpacked"));
        final CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
    }

    @Test
    public void testUnpackUploadingFile() {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        
        assertEquals(LIST_UNIMPORTED, this.action.unpackFiles());
        assertEquals("project.fileUnpack.error.uploading", ActionHelper.getMessages().get(0));
    }
    
    @Test
    public void testValidateSelectedImportFiles() {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CHP);
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
        file.setFileType(AFFYMETRIX_CDF);
        selectedFiles.add(file);
        // invalid status
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);

        response = new LocalHttpServletResponse();
        ServletActionContext.setResponse(response);
        this.action.validateSelectedImportFiles();
        jsonResult = JSONObject.fromObject(response.getResponseText());
        assertTrue(jsonResult.has("confirmMessage"));
    }

    @Test
    public void testValidate() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, this.fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(AFFYMETRIX_CDF);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, this.fileManagementServiceStub.getValidatedFileCount());
    }

    @Test
    public void testSelectRefFiles() throws Exception {
        final List<CaArrayFile> projectFiles = new ArrayList<CaArrayFile>();
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        final List<CaArrayFile> wrongFiles = new ArrayList<CaArrayFile>();

        // load up the project

        CaArrayFile file = new CaArrayFile();
        file.setName("DummyFile1.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile2.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile3.CEL");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CEL);
        projectFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile.sdrf");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileTypeRegistry.MAGE_TAB_SDRF);
        projectFiles.add(file);
        wrongFiles.add(file);

        file = new CaArrayFile();
        file.setName("DummyFile.idf");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
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

    @Test
    public void testValidateUnparsedDataFiles() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_EXP);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(1, this.fileManagementServiceStub.getValidatedFileCount());

        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(FileTypeRegistry.MAGE_TAB_SDRF);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.validateFiles());
        assertEquals(3, this.fileManagementServiceStub.getValidatedFileCount());
    }

    @Test
    public void testImport() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, this.fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        file.setFileType(AFFYMETRIX_CDF);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        file.setFileType(AFFYMETRIX_CHP);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.importFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, this.fileManagementServiceStub.getImportedFilecCount());
    }

    @Test
    public void testAddSupplemental() {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
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
        assertEquals(2, this.fileManagementServiceStub.getSupplementalFileCount());
    }

    @Test
    public void testAddSupplementalUploadingFile() {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);

        assertEquals(LIST_UNIMPORTED, this.action.addSupplementalFiles());
        assertEquals(1, this.fileManagementServiceStub.getSupplementalFileCount());
        assertEquals(2, ActionHelper.getMessages().size());
        assertEquals("project.fileImport.error.invalidStatus", ActionHelper.getMessages().get(0));
        assertEquals("1 supplemental file(s) added to project.", ActionHelper.getMessages().get(1));
    }

    @Test
    public void testDeleteImported() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_IMPORTED, this.action.deleteImportedFiles());
        assertEquals(0, this.fileManagementServiceStub.getValidatedFileCount());

        // make this file associated with a hyb
        final CaArrayFile celFile = this.fasStub.add(AffymetrixArrayDataFiles.TEST3_CEL);
        this.fasStub.setDeletableStatus(celFile, false);
        assertEquals(LIST_IMPORTED, this.action.deleteImportedFiles());
        assertEquals(0, this.fasStub.getRemovedFileCount());
    }

    @Test
    public void testDeleteSupplemental() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());

        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        this.fasStub.setDeletableStatus(file, true);
        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.SUPPLEMENTAL);
        selectedFiles.add(file);
        this.fasStub.setDeletableStatus(file, true);

        assertEquals(LIST_SUPPLEMENTAL, this.action.deleteSupplementalFiles());
        assertEquals(LIST_SUPPLEMENTAL, this.action.getListAction());
        assertEquals(2, this.fasStub.getRemovedFileCount());
    }

    @Test
    public void testSave() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, this.fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();
        file.setId(1L);
        file.setValidationResult(new FileValidationResult());
        file.getValidationResult().addMessage(Type.ERROR, "foo");
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setId(2L);
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setId(3L);
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTING);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setId(4L);
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setId(5L);
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.UPLOADED);
        selectedFiles.add(file);
        file = new CaArrayFile();
        file.setId(6L);
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.VALIDATION_ERRORS);
        selectedFiles.add(file);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(6, this.dataServiceStub.getSavedCount(CaArrayFile.class));
        assertEquals(FileStatus.UPLOADED, selectedFiles.get(0).getFileStatus());
        assertEquals(0, selectedFiles.get(0).getValidationResult().getMessages().size());
    }

    @Test
    public void testChangeFileTypes() throws Exception {
        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        assertEquals(LIST_UNIMPORTED, this.action.saveFiles());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(0, this.fileManagementServiceStub.getValidatedFileCount());

        CaArrayFile file = new CaArrayFile();

        for (int i = 0; i < 3; i++) {
            file = new CaArrayFile();
            file.setId((long) i);
            file.setProject(this.action.getProject());
            file.setFileStatus(FileStatus.UPLOADED);
            file.setFileType(null);
            selectedFiles.add(file);
        }

        this.action.setChangeToFileType("AFFYMETRIX_CDF");
        assertEquals(LIST_UNIMPORTED, this.action.changeFileType());
        assertEquals(LIST_UNIMPORTED, this.action.getListAction());
        assertEquals(3, this.dataServiceStub.getSavedCount(CaArrayFile.class));

        assertEquals(FileStatus.UPLOADED, selectedFiles.get(0).getFileStatus());
        this.action.prepare();
        for (final CaArrayFile caf : this.action.getProject().getFiles()) {
            assertEquals(AFFYMETRIX_CDF, caf.getFileType());
        }
    }

    @Test
    public void testEdit() {
        assertEquals(Action.SUCCESS, this.action.editFiles());
    }

    @Test
    public void testDownload() throws Exception {
        final CaArrayFile f1 = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile f2 = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        f1.setId(1L);
        f1.setCompressedSize(1024 * 1024 * 1024);
        f2.setId(2L);
        f2.setCompressedSize(1024 * 1024 * 384);

        final Set<Long> l = new HashSet<Long>();
        l.add(1L);
        l.add(2L);
        // need to catch exception as these are test files and will not be retrieved
        try {
            this.action.setSelectedFileIds(l);
        } catch (final Exception e) {
            // NOOP
        }

        this.action.setSelectedFiles(Arrays.asList(f1, f2));
        this.action.setProject(p);

        final String result = this.action.download();
        assertNull(result);

        assertEquals("application/zip", this.mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", this.mockResponse.getHeader("Content-disposition"));

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
                this.mockResponse.getContentAsByteArray()));
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
        final FileAccessServiceStub fas = new FileAccessServiceStub();
        final CaArrayFile f1 = fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile f2 = fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        f1.setId(1L);
        f1.setCompressedSize(1024 * 1024 * 1024);
        f2.setId(2L);
        f2.setCompressedSize(1024 * 1024 * 1024);
        final Set<Long> l = new HashSet<Long>();
        l.add(1L);
        l.add(2L);
        l.add(3L);
        // need to catch exception as these are test files and will not be retrieved
        try {
            this.action.setSelectedFileIds(l);
        } catch (final Exception e) {
            // NOOP
        }
        this.action.setSelectedFiles(Arrays.asList(f1, f2));
        this.action.setProject(p);
        final String result = this.action.download();
        assertEquals("denied", result);
    }

    @Test
    public void validateMessages() {
        assertEquals(Action.SUCCESS, this.action.validationMessages());
    }

    @Test
    public void validateDownloadfiles() throws IOException {
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        final SortedSet<CaArrayFile> fileSet = getDownloadedFileSet();

        final TestProject project = new TestProject();
        this.action.setProject(project);
        project.setFiles(fileSet);

        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(4, this.action.getFiles().size());

        this.action.setFileType(AGILENT_CSV.getName());
        this.action.setFileStatus(FileStatus.IMPORTED.name());
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileType(GENEPIX_GAL.getName());
        this.action.setFileStatus(FileStatus.VALIDATED_NOT_PARSED.name());
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileStatus(null);
        this.action.setFileType(AFFYMETRIX_EXP.getName());
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(0, this.action.getFiles().size());

        this.action.setFileType(null);
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(4, this.action.getFiles().size());

        this.action.setFileType(UNKNOWN_FILE_TYPE);
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileType(KNOWN_FILE_TYPE);
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(3, this.action.getFiles().size());
    }

    @Test
    public void validateListImportedForm() throws IOException {
        final SortedSet<CaArrayFile> fileSet = getImportedFileSet();

        final TestProject project = new TestProject();
        assertEquals(0, this.action.getFiles().size());
        project.setImportedFiles(fileSet);
        this.action.setProject(project);

        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(3, this.action.getFiles().size());

        this.action.setFileType(AGILENT_CSV.getName());
        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileType(AFFYMETRIX_EXP.getName());
        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(0, this.action.getFiles().size());

        this.action.setFileType(null);
        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(3, this.action.getFiles().size());

        this.action.setFileType(UNKNOWN_FILE_TYPE);
        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileType(KNOWN_FILE_TYPE);
        assertEquals(LIST_IMPORTED_FORM, this.action.listImportedForm());
        assertEquals(2, this.action.getFiles().size());
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
    public void testExperimentTreeJson() throws Exception {
        final Experiment exp = this.action.getProject().getExperiment();

        final Source src1 = new Source();
        src1.setName("Src1");
        src1.setId(252L);
        src1.setExperiment(exp);
        this.dataServiceStub.save(src1);
        exp.getSources().add(src1);

        final Sample smp1 = new Sample();
        smp1.setName("Smp1");
        smp1.setId(781L);
        exp.getSamples().add(smp1);
        src1.getSamples().add(smp1);
        src1.setExperiment(exp);
        smp1.setExperiment(exp);
        this.dataServiceStub.save(smp1);

        this.action.setNodeType(ExperimentDesignTreeNodeType.ROOT);
        this.action.importTreeNodesJson();
        String expected = "[{\"id\":\"Sources\",\"text\":\"Sources\",\"sort\":\"1\",\"nodeType\":\"EXPERIMENT_SOURCES\",\"leaf\":false},"
                + "{\"id\":\"Samples\",\"text\":\"Samples\",\"sort\":\"2\",\"nodeType\":\"EXPERIMENT_SAMPLES\",\"leaf\":false},"
                + "{\"id\":\"Extracts\",\"text\":\"Extracts\",\"sort\":\"3\",\"nodeType\":\"EXPERIMENT_EXTRACTS\",\"leaf\":true},"
                + "{\"id\":\"LabeledExtracts\",\"text\":\"Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"EXPERIMENT_LABELED_EXTRACTS\",\"leaf\":true},"
                + "{\"id\":\"Hybridizations\",\"text\":\"Hybridizations\",\"sort\":\"5\",\"nodeType\":\"EXPERIMENT_HYBRIDIZATIONS\",\"leaf\":true}]";
        assertEquals(expected, this.mockResponse.getContentAsString());

        this.mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(this.mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_SOURCES);
        this.action.setNode("ROOT_Sources");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Sources_252\",\"entityId\":252,\"text\":\"Src1\",\"sort\":\"Src1\",\"nodeType\":\"SOURCE\",\"iconCls\":\"source_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Sources_252_Samples\",\"text\":\"Associated Samples\",\"sort\":\"2\",\"nodeType\":\"BIOMATERIAL_SAMPLES\",\"leaf\":false},{\"id\":\"ROOT_Sources_252_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, this.mockResponse.getContentAsString());

        this.mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(this.mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_SAMPLES);
        this.action.setNode("ROOT_Samples");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Samples_781\",\"entityId\":781,\"text\":\"Smp1\",\"sort\":\"Smp1\",\"nodeType\":\"SAMPLE\",\"iconCls\":\"sample_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Samples_781_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Samples_781_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Samples_781_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, this.mockResponse.getContentAsString());

        this.mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(this.mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.EXPERIMENT_EXTRACTS);
        this.action.setNode("ROOT_Extracts");
        this.action.importTreeNodesJson();
        assertEquals("[]", this.mockResponse.getContentAsString());

        this.mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(this.mockResponse);
        this.action.setNodeType(ExperimentDesignTreeNodeType.BIOMATERIAL_SAMPLES);
        this.action.setNode("ROOT_Sources_252_Samples");
        this.action.importTreeNodesJson();
        expected = "[{\"id\":\"ROOT_Sources_252_Samples_781\",\"entityId\":781,\"text\":\"Smp1\",\"sort\":\"Smp1\",\"nodeType\":\"SAMPLE\",\"iconCls\":\"sample_node\",\"checked\":false,\"children\":[{\"id\":\"ROOT_Sources_252_Samples_781_Extracts\",\"text\":\"Associated Extracts\",\"sort\":\"3\",\"nodeType\":\"BIOMATERIAL_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Samples_781_LabeledExtracts\",\"text\":\"Associated Labeled Extracts\",\"sort\":\"4\",\"nodeType\":\"BIOMATERIAL_LABELED_EXTRACTS\",\"leaf\":true},{\"id\":\"ROOT_Sources_252_Samples_781_Hybridizations\",\"text\":\"Associated Hybridizations\",\"sort\":\"5\",\"nodeType\":\"BIOMATERIAL_HYBRIDIZATIONS\",\"leaf\":true}]}]";
        assertEquals(expected, this.mockResponse.getContentAsString());

        this.mockResponse = new MockHttpServletResponse();
        this.action.setNodeType(ExperimentDesignTreeNodeType.BIOMATERIAL_EXTRACTS);
        ServletActionContext.setResponse(this.mockResponse);
        this.action.setNode("ROOT_Sources_252_Samples_781_Extracts");
        this.action.importTreeNodesJson();
        assertEquals("[]", this.mockResponse.getContentAsString());
    }

    private static final String UNKNOWN_FILE_TYPE = "(Unknown File Types)";
    private static final String KNOWN_FILE_TYPE = "(Supported File Types)";

    @Test
    public void testListUnimported() throws IOException {

        assertEquals(0, this.action.getFiles().size());
        final SortedSet<CaArrayFile> fileSet = getUnimportedFileSet();

        final TestProject project = new TestProject();
        project.setUnimportedFiles(fileSet);
        this.action.setProject(project);

        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(7, this.action.getFiles().size());
        assertEquals(3, this.action.getFileStatusCountMap().get(FileStatus.UPLOADED).intValue());
        assertEquals(3, this.action.getFileStatusCountMap().get(FileStatus.IMPORT_FAILED).intValue());
        assertEquals(1, this.action.getFileStatusCountMap().get(FileStatus.UPLOADING).intValue());

        this.action.setFileType(AGILENT_CSV.getName());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(2, this.action.getFiles().size());

        this.action.setFileType(null);
        this.action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(FileStatus.UPLOADED.name(), this.action.getFileStatus());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(3, this.action.getFiles().size());

        this.action.setFileType(AGILENT_CSV.getName());
        assertEquals(AGILENT_CSV.getName(), this.action.getFileType());
        this.action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileType(UNKNOWN_FILE_TYPE);
        this.action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(1, this.action.getFiles().size());

        this.action.setFileStatus(FileStatus.IN_QUEUE.name());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(0, this.action.getFiles().size());

        this.action.setFileType(KNOWN_FILE_TYPE);
        this.action.setFileStatus(null);
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(5, this.action.getFiles().size());

        this.action.setFileType(KNOWN_FILE_TYPE);
        this.action.setFileStatus(FileStatus.UPLOADED.name());
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(2, this.action.getFiles().size());
        assertEquals(2, this.action.getFileStatusCountMap().get(FileStatus.UPLOADED).intValue());
    }

    @Test
    public void testReparseFilesNoDesign() {
        final TestProject project = new TestProject();
        project.setImportedFiles(new TreeSet<CaArrayFile>());
        final ArrayDesign design = new ArrayDesign() {
            @Override
            public boolean isImportedAndParsed() {
                return false;
            }
        };
        project.getExperiment().getArrayDesigns().add(design);
        this.action.setProject(project);

        final String result = this.action.reparseFiles();
        assertEquals(LIST_IMPORTED, result);
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("project.fileReparse.error.noParsedDesigns", ActionHelper.getMessages().get(0));
    }

    @Test
    public void testReparseFilesIneligible() {
        final TestProject project = new TestProject();
        project.setUnimportedFiles(new TreeSet<CaArrayFile>());
        this.action.setProject(project);
        final ArrayDesign design = new ArrayDesign() {
            @Override
            public boolean isImportedAndParsed() {
                return true;
            }
        };
        project.getExperiment().getArrayDesigns().add(design);

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setFileType(AFFYMETRIX_CEL);
        file1.setFileStatus(FileStatus.UPLOADED);
        this.action.getSelectedFiles().add(file1);

        final String result = this.action.reparseFiles();
        assertEquals(LIST_UNIMPORTED, result);
        assertEquals(2, ActionHelper.getMessages().size());
        assertEquals("project.fileReparse.error.notEligible", ActionHelper.getMessages().get(0));
    }

    @Test
    public void testReparseFilesOk() {
        final TestProject project = new TestProject();
        project.setUnimportedFiles(new TreeSet<CaArrayFile>());
        this.action.setProject(project);
        final ArrayDesign design = new ArrayDesign() {
            @Override
            public boolean isImportedAndParsed() {
                return true;
            }
        };
        project.getExperiment().getArrayDesigns().add(design);

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setFileType(AFFYMETRIX_CHP);
        file1.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        this.action.getSelectedFiles().add(file1);

        final String result = this.action.reparseFiles();
        assertEquals(LIST_UNIMPORTED, result);
        assertEquals(1, ActionHelper.getMessages().size());
        assertEquals("project.fileImport.success", ActionHelper.getMessages().get(0));
        assertEquals(1, this.fileManagementServiceStub.reimportCount);
    }

    private SortedSet<CaArrayFile> getUnimportedFileSet() throws IOException {
        final SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setType(AFFYMETRIX_CDF.getName());
        file1.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file1);

        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setType(AFFYMETRIX_CDF.getName());
        file2.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file2);

        final CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setType(AGILENT_CSV.getName());
        file3.setFileStatus(FileStatus.IMPORT_FAILED);
        fileSet.add(file3);

        final CaArrayFile file4 = new CaArrayFile();
        file4.setName("file4");
        file4.setType(AGILENT_CSV.getName());
        file4.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file4);

        final CaArrayFile file5 = new CaArrayFile();
        file5.setName("file5");
        file5.setFileType(GENEPIX_GAL);
        file5.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file5);

        final CaArrayFile file6 = new CaArrayFile();
        file6.setName("file6");
        file6.setFileType(null);
        file6.setFileStatus(FileStatus.UPLOADED);
        fileSet.add(file6);

        final CaArrayFile file7 = new CaArrayFile();
        file7.setName("file7");
        file7.setFileType(null);
        file7.setFileStatus(FileStatus.UPLOADING);
        fileSet.add(file7);
        return fileSet;
    }

    private SortedSet<CaArrayFile> getImportedFileSet() throws IOException {
        final SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setType(AFFYMETRIX_CDF.getName());
        file1.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file1);

        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setType(AGILENT_CSV.getName());
        file2.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file2);

        final CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setType(null);
        file3.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file3);
        return fileSet;
    }

    private SortedSet<CaArrayFile> getDownloadedFileSet() throws IOException {
        final SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setType(AFFYMETRIX_CDF.getName());
        file1.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        fileSet.add(file1);

        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setType(AGILENT_CSV.getName());
        file2.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file2);

        final CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setType(null);
        file3.setFileStatus(FileStatus.VALIDATED);
        fileSet.add(file3);

        final CaArrayFile file4 = new CaArrayFile();
        file4.setName("file4");
        file4.setType(GENEPIX_GAL.getName());
        file4.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        fileSet.add(file4);
        return fileSet;
    }

    @Test
    public void testDownloadOptions() throws IOException {
        CaArrayFile file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(GENEPIX_GAL);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(AFFYMETRIX_CEL);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(AFFYMETRIX_CHP);
        this.action.getProject().getFiles().add(file);

        file = new CaArrayFile();
        file.setProject(this.action.getProject());
        file.setFileStatus(FileStatus.IMPORTED);
        file.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        this.action.getProject().getFiles().add(file);

        final String result = this.action.downloadOptions();
        assertEquals("success", result);

        assertEquals(2, this.action.getFileTypes().size());
        assertTrue(this.action.getFileTypes().contains(AFFYMETRIX_CEL.getName()));
        assertTrue(this.action.getFileTypes().contains(AFFYMETRIX_CHP.getName()));
    }

    @Test
    public void testUserVisibleFiles() {
        final SortedSet<CaArrayFile> fileSet = new TreeSet<CaArrayFile>();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setType(AFFYMETRIX_CDF.getName());
        file1.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file1);

        final CaArrayFile file1child = new CaArrayFile(file1);
        file1child.setName("file1 child");
        file1child.setType(AFFYMETRIX_CDF.getName());
        file1child.setFileStatus(FileStatus.IMPORTED);
        fileSet.add(file1child);

        final TestProject project = new TestProject();
        assertEquals(0, this.action.getFiles().size());
        this.action.setProject(project);

        project.setImportedFiles(fileSet);
        assertEquals(LIST_IMPORTED, this.action.listImported());
        assertEquals(1, this.action.getFiles().size());

        project.setUnimportedFiles(fileSet);
        assertEquals(LIST_UNIMPORTED, this.action.listUnimported());
        assertEquals(1, this.action.getFiles().size());

        project.setFiles(fileSet);
        assertEquals(Action.SUCCESS, this.action.downloadFiles());
        assertEquals(1, this.action.getFiles().size());

        final List<CaArrayFile> selectedFiles = new ArrayList<CaArrayFile>();
        this.action.setSelectedFiles(selectedFiles);
        selectedFiles.add(file1);
        assertEquals(LIST_UNIMPORTED, this.action.addSupplementalFiles());
        assertEquals(1, this.fileManagementServiceStub.getSupplementalFileCount());
    }

    private class LocalHttpServletResponse extends MockHttpServletResponse {
        private final StringWriter out = new StringWriter();

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(this.out);
        }

        public String getResponseText() {
            return this.out.toString();
        }
    }

    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        private final Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public void save(PersistentObject object) {
            super.save(object);
            this.objMap.put(object.getId(), object);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            final Object candidate = this.objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }

        public int getSavedCount(final Class<?> ofType) {
            return Iterables.size(Iterables.filter(this.objMap.values(), Predicates.instanceOf(ofType)));
        }
    }

    private class TestProject extends Project {

        private static final long serialVersionUID = 1L;
        SortedSet<CaArrayFile> unimportedFiles;
        SortedSet<CaArrayFile> importedFiles;
        SortedSet<CaArrayFile> files;

        @Override
        public SortedSet<CaArrayFile> getUnImportedFiles() {
            return this.unimportedFiles;
        }

        public void setUnimportedFiles(SortedSet<CaArrayFile> unimportedFiles) {
            this.unimportedFiles = unimportedFiles;
        }

        @Override
        public SortedSet<CaArrayFile> getImportedFiles() {
            return this.importedFiles;
        }

        public void setImportedFiles(SortedSet<CaArrayFile> importedFiles) {
            this.importedFiles = importedFiles;
        }

        @Override
        public SortedSet<CaArrayFile> getFiles() {
            return this.files;
        }

        public void setFiles(SortedSet<CaArrayFile> files) {
            this.files = files;
        }

    }

    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
        private int reimportCount = 0;

        @Override
        public void reimportAndParseProjectFiles(Project targetProject, CaArrayFileSet fileSet) {
            this.reimportCount++;
        }

        @Override
        public List<String> findIdfRefFileNames(CaArrayFile idfFile, Project project) {
            final List<String> filenames = new ArrayList<String>();
            for (final CaArrayFile caf : project.getFileSet().getFiles()) {
                if (FileTypeRegistry.MAGE_TAB_SDRF.equals(caf.getFileType())
                        || AFFYMETRIX_CEL.equals(caf.getFileType())) {
                    filenames.add(caf.getName());
                }
            }

            return filenames;
        }
    }
}
