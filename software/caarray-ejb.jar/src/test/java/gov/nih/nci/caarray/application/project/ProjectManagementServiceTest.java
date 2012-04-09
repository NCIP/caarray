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
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@SuppressWarnings({ "deprecation", "serial" })
public class ProjectManagementServiceTest extends AbstractServiceTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    private ProjectManagementService projectManagementService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private final GenericDataService genericDataService = new GenericDataServiceStub();
    private Transaction transaction;

    private ServiceLocatorStub locatorStub;
    
    @Mock
    private FileDao fileDao;
    @Mock
    private FileAccessService fileAccessService;

    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                requestStaticInjection(gov.nih.nci.caarray.security.AuthorizationManagerExtensions.class);
                requestStaticInjection(gov.nih.nci.caarray.security.SecurityUtils.class);
                requestStaticInjection(gov.nih.nci.caarray.domain.permissions.CollaboratorGroup.class);
            }
        });
    }

    @Before
    public void setUpService() {
        MockitoAnnotations.initMocks(this);
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        final ProjectManagementServiceBean pmsBean = new ProjectManagementServiceBean();

        pmsBean.setProjectDao(this.daoFactoryStub.getProjectDao());
        pmsBean.setFileDao(fileDao);
        pmsBean.setSampleDao(this.daoFactoryStub.getSampleDao());
        pmsBean.setSearchDao(this.daoFactoryStub.getSearchDao());
        pmsBean.setVocabularyDao(this.daoFactoryStub.getVocabularyDao());

        locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, this.genericDataService);
        final MysqlDataSource ds = new MysqlDataSource();
        final Configuration config = hibernateHelper.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        this.projectManagementService = pmsBean;
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectManagementService);
        hibernateHelper.setFiltersEnabled(false);
        this.transaction = hibernateHelper.beginTransaction();
    }

    @After
    public void tearDown() {
        if (this.transaction != null) {
            this.transaction.rollback();
        }
    }

    @Test
    public void testGetWorkspaceProjects() {
        final List<Project> projects =
                this.projectManagementService.getMyProjects(new PageSortParams<Project>(10000, 1,
                        ProjectSortCriterion.PUBLIC_ID, false));
        assertNotNull(projects);
    }

    @Test
    public void testGetProjectsForOwner() {
        final User u = CaArrayUsernameHolder.getCsmUser();
        final List<Project> projects = this.projectManagementService.getProjectsForOwner(u);
        assertSame(Collections.EMPTY_LIST, projects);
    }

    @Test
    public void testAddFile() throws Exception {
        final Project project = this.daoFactoryStub.getSearchDao().retrieve(Project.class, 123L);
        final CaArrayFile file =
                this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName(), file.getName());
        assertEquals(1, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
    }
    
    @Test
    public void testAddFileChunk() throws Exception {
        // Trying to get rid of stub classes.  Using a mock fileAccessService here.
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessService);
        Project project = new Project();
        CaArrayFile partialFile = new CaArrayFile();
        File file = mock(File.class);
        String fileName = "testFile";
        long fileSize = 1234L;

        when(fileDao.getPartialFile(project.getId(), fileName, fileSize)).thenReturn(partialFile);
        when(fileAccessService.addChunk(file, fileName, fileSize, partialFile)).thenReturn(partialFile);
        
        CaArrayFile result = projectManagementService.addFileChunk(project, file, fileName, fileSize);
        assertEquals(partialFile, result);
        assertEquals(project,partialFile.getProject());
        verify(fileDao).getPartialFile(project.getId(), fileName, fileSize);
        verify(fileAccessService).addChunk(file, fileName, fileSize, partialFile);
        verify(fileDao).save(partialFile);
    }

    private FileWrapper createFileWrapper(File file, String fileName, boolean compressed) {
        FileWrapper fileWrapper = new FileWrapper();
        fileWrapper.setFile(file);
        fileWrapper.setFileName(fileName);
        fileWrapper.setCompressed(compressed);
        fileWrapper.setTotalFileSize(file.length());
        return fileWrapper;
    }
    
    @Test
    public void testUploadFiles() throws Exception {
        final Project project = this.daoFactoryStub.getSearchDao().retrieve(Project.class, 123L);
        final List<FileWrapper> fileWrappers = new ArrayList<FileWrapper>();
        final DataStorageFacade dataStorageFacade = mock(DataStorageFacade.class);
        FileUploadUtils fileUploadUtils = new FileUploadUtils(dataStorageFacade);

        // add a zip file which contains some non-zip files and 1 zip file (11 in total)
        // the top layer zip will be unpacked
        fileWrappers.add(createFileWrapper(
                MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP, "specification.zip", true));
        // also attempt to add a file with a blank file name
        fileWrappers.add(createFileWrapper(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, "  ", false));
        // attempt to add a file that is already inside nested zip
        // this should be fine (this file plus contents of zip will make the total 12)
        fileWrappers.add(createFileWrapper(
                MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE,
                MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName(), false));
        // attempt to add file that is already part of the zip
        // this should cause a conflict
        fileWrappers.add(createFileWrapper(
                MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName(), false));
        FileProcessingResult result = fileUploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(12, result.getCount());
        assertEquals(12, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), "Test1.zip");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
        assertEquals(1, result.getConflictingFiles().size());
        assertTrue(result.getConflictingFiles().contains(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()));
        // attempt to re-upload a bunch of the files that were just uploaded to get
        // to get back nothing, showing that if a file has the same name, we do not add it.
        fileUploadUtils = new FileUploadUtils(dataStorageFacade);
        result = fileUploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(0, result.getCount());
        assertEquals(12, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), "Test1.zip");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
        assertEquals(12, result.getConflictingFiles().size());
        assertTrue(result.getConflictingFiles().contains(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()));
    }

    @Test
    public void testUploadFilesDefect13744() throws Exception {
        final DataStorageFacade dataStorageFacade = mock(DataStorageFacade.class);
        final FileUploadUtils fileUploadUtils = new FileUploadUtils(dataStorageFacade);

        // testing upload of a zip file and a txt file in that order
        // the zip file does not contain the txt file.
        // the txt file should be unknown.
        final Project project = this.daoFactoryStub.getSearchDao().retrieve(Project.class, 123L);
        final List<FileWrapper> fileWrappers = new ArrayList<FileWrapper>();
        fileWrappers.add(createFileWrapper(MageTabDataFiles.SPECIFICATION_ZIP, "specification.zip", true));
        fileWrappers.add(createFileWrapper(
                MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE, "Test1.txt", false));
        final FileProcessingResult result = fileUploadUtils.uploadFiles(project, fileWrappers);
        assertEquals(17, result.getCount());
        assertEquals(17, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), "Test1.txt");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());

    }

    @Test
    public void testUnpackFiles() throws Exception {
        // testing unpacking of a file already in the project
        // add a file
        final Project project = this.daoFactoryStub.getSearchDao().retrieve(Project.class, 123L);
        final CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_ZIP);
        this.fileAccessServiceStub.setDeletableStatus(file, true);
        assertEquals(MageTabDataFiles.SPECIFICATION_ZIP.getName(), file.getName());
        assertEquals(1, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP.getName());
        // unpack zip file

        final CaArrayFile myFile = project.getFiles().first();

        final DataStorageFacade dataStorageFacade = mock(DataStorageFacade.class);
        when(dataStorageFacade.openInputStream(myFile.getDataHandle(), false)).thenReturn(
                FileUtils.openInputStream(MageTabDataFiles.SPECIFICATION_ZIP));
        final FileUploadUtils fileUploadUtils = new FileUploadUtils(dataStorageFacade);

        // now do the unpack
        final List<CaArrayFile> cFileList = new ArrayList<CaArrayFile>();
        cFileList.add(myFile);

        final FileProcessingResult result = fileUploadUtils.unpackFiles(project, cFileList);
        // the unpack should have added 10 files and removed the zip archive
        assertEquals(16, result.getCount());
        assertEquals(1, this.fileAccessServiceStub.getRemovedFileCount());
        assertEquals(16, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertNotContains(project.getFiles(), "specification.zip");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
    }

    private void assertContains(Set<CaArrayFile> caArrayFiles, String file) {
        for (final CaArrayFile caArrayFile : caArrayFiles) {
            if (file.equals(caArrayFile.getName())) {
                return;
            }
        }
        fail("CaArrayFileSet did not contain " + file);
    }

    private void assertNotContains(Set<CaArrayFile> caArrayFiles, String file) {
        for (final CaArrayFile caArrayFile : caArrayFiles) {
            if (file.equals(caArrayFile.getName())) {
                fail("CaArrayFileSet contains " + file);
            }
        }
        return;
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test(expected = PermissionDeniedException.class)
    public void testSaveProject() throws Exception {
        Project project = new Project();
        final AbstractCaArrayEntity e = new Sample();
        this.projectManagementService.saveProject(project, e);
        assertEquals(project, this.daoFactoryStub.projectDao.lastSaved);
        assertEquals(e, this.daoFactoryStub.projectDao.lastDeleted);

        project = new Project();
        CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        this.projectManagementService.saveProject(project);
        fail("anonymous user should not have been allowed to save a project");
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test
    public void testSaveProjectWithInconsistentArrayDesigns() throws Exception {
        final Project project = new Project();
        final ArrayDesign ad1 = new ArrayDesign();
        ad1.setName("Test1");
        final ArrayDesign ad2 = new ArrayDesign();
        ad2.setName("Test2");
        project.getExperiment().getArrayDesigns().add(ad1);
        project.getExperiment().getArrayDesigns().add(ad2);

        final Hybridization h1 = new Hybridization();
        final Array a1 = new Array();
        a1.setDesign(ad2);
        h1.setArray(a1);
        project.getExperiment().getHybridizations().add(h1);

        final Hybridization h2 = new Hybridization();
        final Array a2 = new Array();
        h2.setArray(a2);
        project.getExperiment().getHybridizations().add(h2);
        this.projectManagementService.saveProject(project);

        project.getExperiment().getArrayDesigns().remove(ad2);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (final InconsistentProjectStateException e) {
            assertEquals(Reason.INCONSISTENT_ARRAY_DESIGNS, e.getReason());
            assertEquals(1, e.getArguments().length);
            assertEquals("Test2", e.getArguments()[0]);
        }
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test
    public void testSaveProjectWithImportingFiles() throws Exception {
        final Project project = new Project() {
            @Override
            public boolean isImportingData() {
                for (final CaArrayFile f : this.getFiles()) {
                    if (f.getFileStatus() == FileStatus.IMPORTING) {
                        return true;
                    }
                }
                return false;
            }
        };
        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("File1");
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setProject(project);
        project.getFiles().add(file1);
        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("File2");
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setProject(project);
        project.getFiles().add(file2);
        final Source source = new Source();
        source.setId(1L);
        source.setExperiment(project.getExperiment());
        project.getExperiment().getSources().add(source);

        this.projectManagementService.saveProject(project);

        file1.setFileStatus(FileStatus.IMPORTING);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (final InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        }
        try {
            this.projectManagementService.copySource(project, 1L);
            fail("Expected to throw inconsistent state exception");
        } catch (final InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        }

        file1.setFileStatus(FileStatus.IMPORTED);
        this.projectManagementService.saveProject(project);
        this.projectManagementService.copySource(project, 1L);
    }

    /**
     * Test method for {@link ProjectManagementService#copyFactor(Project, long)}.
     */
    @Test
    public void testCopyFactor() throws Exception {
        final Project project = new Project();
        this.projectManagementService.saveProject(project);
        final Factor factor = this.projectManagementService.copyFactor(project, 1);
        assertNotNull(factor);
        assertEquals("Test2", factor.getName());
        assertEquals(1, project.getExperiment().getFactors().size());
    }

    /**
     * Test method for {@link ProjectManagementService#copySource(Project, long)}.
     */
    @Test
    public void testCopySource() throws Exception {
        final Project project = new Project();
        this.projectManagementService.saveProject(project);
        final Source source = this.projectManagementService.copySource(project, 1);
        assertNotNull(source);
        assertEquals("Test2", source.getName());
        assertEquals("Test", source.getDescription());
        assertEquals(1, project.getExperiment().getSources().size());
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testCopySample() throws Exception {
        final Project project = new Project();
        this.projectManagementService.saveProject(project);
        final Sample sample = this.projectManagementService.copySample(project, 1);
        assertOnAbstractBioMaterialCopy(sample);
        assertEquals(1, project.getExperiment().getSamples().size());
        assertTrue(!sample.getSources().isEmpty());
    }

    @Test
    public void testCopyExtract() throws ProposalWorkflowException, InconsistentProjectStateException {
        final Project project = new Project();
        this.projectManagementService.saveProject(project);
        final Extract e = this.projectManagementService.copyExtract(project, 1);
        assertOnAbstractBioMaterialCopy(e);
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertTrue(!e.getSamples().isEmpty());
    }

    private void assertOnAbstractBioMaterialCopy(AbstractBioMaterial abm) {
        assertNotNull(abm);
        assertEquals("Test2", abm.getName());
        assertEquals("Test", abm.getDescription());
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testGetSampleByExternalIdReturnsNonUniqueResult() throws Exception {
        final CaArrayDaoFactory daoFactory = new DaoFactoryStub() {
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub() {
                    @Override
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(
                            T entityToMatch) {
                        final ArrayList<Sample> arrayList = new ArrayList<Sample>();
                        arrayList.add(new Sample());
                        arrayList.add(new Sample());
                        return (List<T>) arrayList;
                    };
                };
            }
        };
        final ProjectManagementServiceBean pmsBean = new ProjectManagementServiceBean();
        pmsBean.setProjectDao(daoFactory.getProjectDao());
        pmsBean.setFileDao(daoFactory.getFileDao());
        pmsBean.setSampleDao(daoFactory.getSampleDao());
        pmsBean.setSearchDao(daoFactory.getSearchDao());
        pmsBean.setVocabularyDao(daoFactory.getVocabularyDao());

        final Project project = new Project();
        assertNull(pmsBean.getBiomaterialByExternalId(project, "def", Sample.class));
    }

    @Test
    public void testGetSampleByExternalIdReturnsNull() throws Exception {
        final CaArrayDaoFactory daoFactory = new DaoFactoryStub() {
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub() {
                    @Override
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(
                            T entityToMatch) {
                        return null;
                    };
                };
            }
        };
        final ProjectManagementServiceBean pmsBean = new ProjectManagementServiceBean();
        pmsBean.setProjectDao(daoFactory.getProjectDao());
        pmsBean.setFileDao(daoFactory.getFileDao());
        pmsBean.setSampleDao(daoFactory.getSampleDao());
        pmsBean.setSearchDao(daoFactory.getSearchDao());
        pmsBean.setVocabularyDao(daoFactory.getVocabularyDao());

        final Project project = new Project();

        assertNull(pmsBean.getBiomaterialByExternalId(project, "abc", Sample.class));
    }

    @Test
    public void testGetSampleByExternalId() throws Exception {
        final CaArrayDaoFactory daoFactory = new DaoFactoryStub() {
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub() {
                    @Override
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(
                            T entityToMatch) {
                        final ArrayList<T> arrayList = new ArrayList<T>();
                        arrayList.add(entityToMatch);
                        return arrayList;
                    };
                };
            }
        };
        final ProjectManagementServiceBean pmsBean = new ProjectManagementServiceBean();
        pmsBean.setProjectDao(daoFactory.getProjectDao());
        pmsBean.setFileDao(daoFactory.getFileDao());
        pmsBean.setSampleDao(daoFactory.getSampleDao());
        pmsBean.setSearchDao(daoFactory.getSearchDao());
        pmsBean.setVocabularyDao(daoFactory.getVocabularyDao());

        final Project project = new Project();

        final Sample sampleByExternalId = pmsBean.getBiomaterialByExternalId(project, "abc", Sample.class);
        assertNotNull(sampleByExternalId);
        assertSame(project.getExperiment(), sampleByExternalId.getExperiment());
    }

    @Test
    public void testSearchByCategory() {
        assertEquals(
                0,
                this.projectManagementService.searchByCategory(null, "test", Sample.class,
                        SearchSampleCategory.values()).size());

    }

    @Test
    public void testSearchSamplesByCharacteristicCategory() {
        assertEquals(Collections.EMPTY_LIST,
                this.projectManagementService.searchSamplesByCharacteristicCategory(null, null, "test"));
    }

    @Test
    public void testSearchSourcesByCharacteristicCategory() {
        assertEquals(Collections.EMPTY_LIST,
                this.projectManagementService.searchSourcesByCharacteristicCategory(null, null, "test"));
    }

    @Test
    public void countSamplesByCharacteristicCategory() {
        assertEquals(0, this.projectManagementService.countSamplesByCharacteristicCategory(null, "test"));
    }

    @Test
    public void testCountSourcesByCharacteristicCategory() {
        assertEquals(0, this.projectManagementService.countSourcesByCharacteristicCategory(null, "test"));
    }

    @Test
    public void testSearchCount() {
        assertEquals(0, this.projectManagementService.searchCount("test", Sample.class, SearchSampleCategory.values()));
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalProjectDaoStub projectDao = new LocalProjectDaoStub();

        @Override
        public ProjectDao getProjectDao() {
            return this.projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new LocalSearchDaoStub(this.projectDao);
        }
    }

    private static class LocalProjectDaoStub extends ProjectDaoStub {

        final HashMap<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();
        PersistentObject lastSaved;
        PersistentObject lastDeleted;

        @Override
        public Long save(PersistentObject caArrayObject) {
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
            return caArrayObject.getId();
        }

        @Override
        public List<Project> getProjectsForCurrentUser(PageSortParams<Project> pageSortParams) {
            return new ArrayList<Project>();
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.lastDeleted = caArrayEntity;
            this.savedObjects.remove(caArrayEntity.getId());
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        private final LocalProjectDaoStub projectDao;

        /**
         * @param projectDao
         */
        public LocalSearchDaoStub(LocalProjectDaoStub projectDao) {
            this.projectDao = projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            final PersistentObject po = this.projectDao.savedObjects.get(entityId);
            if (po != null) {
                return (T) po;
            }
            if (Sample.class.equals(entityClass)) {
                final Sample s = getSample(entityId);
                return (T) s;
            } else if (Source.class.equals(entityClass)) {
                final Source s = getSource(entityId);
                return (T) s;
            } else if (Factor.class.equals(entityClass)) {
                final Factor f = getFactor(entityId);
                return (T) f;
            } else if (Extract.class.equals(entityClass)) {
                final Extract e = getExtract(entityId);
                return (T) e;
            } else if (Project.class.equals(entityClass)) {
                return (T) getProject(entityId);
            }
            return null;
        }

        private Project getProject(Long id) {
            if (this.projectDao.savedObjects.containsKey(id)) {
                return (Project) this.projectDao.savedObjects.get(id);
            }
            final Project project = new Project();
            project.setId(id);
            this.projectDao.save(project);
            return project;
        }

        private Extract getExtract(Long entityId) {
            final Extract e = new Extract();
            setABM(e, entityId);
            final Sample s = getSample(entityId++);
            e.getSamples().add(s);
            s.getExtracts().add(e);
            return e;
        }

        private Sample getSample(Long entityId) {
            final Sample s = new Sample();
            setABM(s, entityId);
            final Source source = getSource(entityId++);
            s.getSources().add(source);
            source.getSamples().add(s);
            return s;
        }

        private Source getSource(Long entityId) {
            final Source s = new Source();
            setABM(s, entityId);
            return s;
        }

        private Factor getFactor(Long entityId) {
            final Factor f = new Factor();
            f.setName("Test");
            f.setId(entityId);
            return f;
        }

        private void setABM(AbstractBioMaterial abm, Long entityId) {
            abm.setName("Test");
            abm.setDescription("Test");
            abm.setId(entityId);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
            final List<T> results = new ArrayList<T>();
            if (entityToMatch instanceof Sample) {
                final Sample sampleToMatch = (Sample) entityToMatch;
                for (final PersistentObject po : this.projectDao.savedObjects.values()) {
                    final Project p = (Project) po;
                    if (sampleToMatch.getExperiment().getProject().getId().equals(p.getId())) {
                        for (final Sample s : p.getExperiment().getSamples()) {
                            if (sampleToMatch.getExternalId().equals(s.getExternalId())) {
                                results.add((T) s);
                            }
                        }
                    }
                }
            }
            return results;
        }
    }
}
