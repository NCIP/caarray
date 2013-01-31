//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.SessionContextStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@SuppressWarnings("PMD")
public class ProjectManagementServiceTest extends AbstractCaarrayTest {

    private ProjectManagementService projectManagementService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();
    private final GenericDataService genericDataService = new GenericDataServiceStub();
    private final LocalSessionContextStub sessionContextStub = new LocalSessionContextStub();
    private Transaction transaction;

    @Before
    public void setUpService() {
        UsernameHolder.setUser("caarrayadmin");
        ProjectManagementServiceBean projectManagementServiceBean = new ProjectManagementServiceBean();
        projectManagementServiceBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessService);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, this.genericDataService);
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        projectManagementServiceBean.setSessionContext(this.sessionContextStub);
        this.projectManagementService = projectManagementServiceBean;
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectManagementService);
        HibernateUtil.enableFilters(false);
        transaction = HibernateUtil.beginTransaction();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessService));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }

    @After
    public void tearDown() {
        if (transaction != null) {
            transaction.rollback();
        }
    }

    @Test
    public void testGetWorkspaceProjects() {
        List<Project> projects =
                this.projectManagementService.getMyProjects(false, new PageSortParams<Project>(10000, 1,
                        ProjectSortCriterion.PUBLIC_ID, false));
        assertNotNull(projects);
    }

    @Test
    public void testGetProject() {
        Project project = this.projectManagementService.getProject(123L);
        assertNotNull(project);
        assertEquals(123L, project.getId());
    }

    @Test
    public void testAddFile() throws Exception {
        Project project = this.projectManagementService.getProject(123L);
        CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName(), file.getName());
        assertEquals(1, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
    }


    @Test
    public void testUploadFiles() throws Exception {
        Project project = this.projectManagementService.getProject(123L);
        List<String> fileNames = new ArrayList<String>();
        List<File> files = new ArrayList<File>();
        List<String> filesToUnpack = new ArrayList<String>();

        // add a zip file which contains some non-zip files and 1 zip file (11 in total)
        files.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP);
        fileNames.add("specification.zip");
        // the top layer zip will be unpacked
        filesToUnpack.add("specification.zip");
        // also attempt to add a file with a blank file name
        files.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileNames.add("  ");
        // attempt to add a file that is already inside nested zip
        // this should be fine (this file plus contents of zip will make the total 12)
        files.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE);
        fileNames.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
        // attempt to add file that is already part of the zip
        // this should cause a conflict
        files.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        fileNames.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        FileProcessingResult result = FileUploadUtils.uploadFiles(project, files, fileNames, filesToUnpack);
        assertEquals(12, result.getCount());
        assertEquals(12, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), "Test1.zip");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
        assertEquals(1, result.getConflictingFiles().size());
        assertTrue(result.getConflictingFiles().contains(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()));
        // attempt to re-upload a bunch of the files that were just uploaded to get
        // to get back nothing, showing that if a file has the same name, we do not add it.
        result = FileUploadUtils.uploadFiles(project, files, fileNames, filesToUnpack);
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
        // testing upload of a zip file and a txt file in that order
        // the zip file does not contain the txt file.
        // the txt file should be unknown.
        Project project = this.projectManagementService.getProject(123L);
        List<String> fileNames = new ArrayList<String>();
        List<File> files = new ArrayList<File>();
        files.add(MageTabDataFiles.SPECIFICATION_ZIP);
        fileNames.add("specification.zip");

        files.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE);
        fileNames.add("Test1.txt");

        List<String> filesToUnpack = new ArrayList<String>();
        filesToUnpack.add("specification.zip");
        FileProcessingResult result = FileUploadUtils.uploadFiles(project, files, fileNames, filesToUnpack);
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
        Project project = this.projectManagementService.getProject(123L);
        CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_ZIP);
        assertEquals(MageTabDataFiles.SPECIFICATION_ZIP.getName(), file.getName());
        assertEquals(1, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP.getName());
        // unpack zip file

        CaArrayFile myFile = project.getFiles().first();

        // now do the unpack
        List<CaArrayFile> cFileList = new ArrayList<CaArrayFile>();
        cFileList.add(myFile);

        FileProcessingResult result = FileUploadUtils.unpackFiles(project, cFileList);
        // the unpack should have added 10 files and removed the zip archive
        assertEquals(16, result.getCount());
        assertEquals(15, project.getFiles().size() - this.fileAccessService.getRemovedFileCount());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertNotContains(project.getFiles(), "specification.zip");
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
    }


    private void assertContains(Set<CaArrayFile> caArrayFiles, String file) {
        for (CaArrayFile caArrayFile : caArrayFiles) {
            if (file.equals(caArrayFile.getName())) {
                return;
            }
        }
        fail("CaArrayFileSet did not contain " + file);
    }

    private void assertNotContains(Set<CaArrayFile> caArrayFiles, String file) {
        for (CaArrayFile caArrayFile : caArrayFiles) {
            if (file.equals(caArrayFile.getName())) {
                fail("CaArrayFileSet contains " + file);
            }
        }
        return;
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test(expected=PermissionDeniedException.class)
    public void testSaveProject() throws Exception {
        Project project = new Project();
        AbstractCaArrayEntity e = new Sample();
        this.projectManagementService.saveProject(project, e);
        assertEquals(project, this.daoFactoryStub.projectDao.lastSaved);
        assertEquals(e, this.daoFactoryStub.projectDao.lastDeleted);

        project = new Project();
        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        this.projectManagementService.saveProject(project);
        fail("anonymous user should not have been allowed to save a project");
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test
    public void testSaveProjectWithInconsistentArrayDesigns() throws Exception {
        Project project = new Project();
        ArrayDesign ad1 = new ArrayDesign();
        ad1.setName("Test1");
        ArrayDesign ad2 = new ArrayDesign();
        ad2.setName("Test2");
        project.getExperiment().getArrayDesigns().add(ad1);
        project.getExperiment().getArrayDesigns().add(ad2);

        Hybridization h1 = new Hybridization();
        Array a1 = new Array();
        a1.setDesign(ad2);
        h1.setArray(a1);
        project.getExperiment().getHybridizations().add(h1);

        Hybridization h2 = new Hybridization();
        Array a2 = new Array();
        h2.setArray(a2);
        project.getExperiment().getHybridizations().add(h2);
        this.projectManagementService.saveProject(project);

        project.getExperiment().getArrayDesigns().remove(ad2);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.INCONSISTENT_ARRAY_DESIGNS, e.getReason());
            assertEquals(1, e.getArguments().length);
            assertEquals("Test2", e.getArguments()[0]);
        }
    }

    /**
     * Test method for {@link ProjectManagementService#saveProject(Project, PersistentObject...)}.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testSaveProjectWithImportingFiles() throws Exception {
        Project project = new Project();
        CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        project.getFiles().add(file1);
        CaArrayFile file2 = new CaArrayFile();
        file2.setProject(project);
        project.getFiles().add(file2);
        Source source = new Source();
        source.setId(1L);
        source.setExperiment(project.getExperiment());
        project.getExperiment().getSources().add(source);

        this.projectManagementService.saveProject(project);

        file1.setFileStatus(FileStatus.IMPORTING);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        }
        try {
            this.projectManagementService.copySource(project, 1L);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        }

        file1.setFileStatus(FileStatus.IMPORTED);
        this.projectManagementService.saveProject(project);
        this.projectManagementService.copySource(project, 1L);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDeleteProject() throws Exception {
        Project project = new Project();
        project.setId(1L);
        this.projectManagementService.saveProject(project);
        assertEquals(project, this.daoFactoryStub.projectDao.lastSaved);

        Project retrieved = this.projectManagementService.getProject(1L);
        assertEquals(project, retrieved);

        this.projectManagementService.deleteProject(project);
        retrieved = this.projectManagementService.getProject(1L);
        assertNull(retrieved.getExperiment().getTitle());
    }

    @Test(expected = ProposalWorkflowException.class)
    @SuppressWarnings("deprecation")
    public void testDeleteNonDraftProject() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProposalStatus.IN_PROGRESS);
        this.projectManagementService.saveProject(project);
        this.projectManagementService.deleteProject(project);
    }

    @Test(expected = PermissionDeniedException.class)
    @SuppressWarnings("deprecation")
    public void testDeleteUnownedProject() throws Exception {
        Project project = new Project();
        project.setId(1L);
        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        this.projectManagementService.saveProject(project);
        this.projectManagementService.deleteProject(project);
    }

    /**
     * Test method for {@link ProjectManagementService#copyFactor(Project, long)}.
     */
    @Test
    public void testCopyFactor() throws Exception {
        Project project = new Project();
        this.projectManagementService.saveProject(project);
        Factor factor = this.projectManagementService.copyFactor(project, 1);
        assertNotNull(factor);
        assertEquals("Test2", factor.getName());
        assertEquals(1, project.getExperiment().getFactors().size());
    }

    /**
     * Test method for {@link ProjectManagementService#copySource(Project, long)}.
     */
    @Test
    public void testCopySource() throws Exception {
        Project project = new Project();
        this.projectManagementService.saveProject(project);
        Source source = this.projectManagementService.copySource(project, 1);
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
        Project project = new Project();
        this.projectManagementService.saveProject(project);
        Sample sample = this.projectManagementService.copySample(project, 1);
        assertOnAbstractBioMaterialCopy(sample);
        assertEquals(1, project.getExperiment().getSamples().size());
        assertTrue(!sample.getSources().isEmpty());
    }

    @Test
    public void testCopyExtract() throws ProposalWorkflowException, InconsistentProjectStateException {
        Project project = new Project();
        this.projectManagementService.saveProject(project);
        Extract e = this.projectManagementService.copyExtract(project, 1);
        assertOnAbstractBioMaterialCopy(e);
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertTrue(!e.getSamples().isEmpty());
    }

    private void assertOnAbstractBioMaterialCopy(AbstractBioMaterial abm) {
        assertNotNull(abm);
        assertEquals("Test2", abm.getName());
        assertEquals("Test", abm.getDescription());
    }

    /**
     * @param project
     */
    private void createProtectionGroup(Project project) {
        // Perform voodoo magic
        try {
            Method m = SecurityUtils.class.getDeclaredMethod("createProtectionGroup", Protectable.class, User.class);
            m.setAccessible(true);
            m.invoke(null, project, UsernameHolder.getCsmUser());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSetUseTcgaPolicy() throws Exception {
        Project project = this.projectManagementService.getProject(123L);
        UsernameHolder.setUser("caarrayadmin");
        createProtectionGroup(project);
        assertFalse(project.isUseTcgaPolicy());
        this.projectManagementService.setUseTcgaPolicy(123L, true);
        project = this.projectManagementService.getProject(123L);
        assertTrue(project.isUseTcgaPolicy());
    }

    @Test  (expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testGetSampleByExternalIdReturnsNonUniqueResult() throws Exception {
        ProjectManagementServiceBean bean = (ProjectManagementServiceBean) this.projectManagementService;
        bean.setDaoFactory(new DaoFactoryStub(){
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub(){
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
                        ArrayList<Sample> arrayList = new ArrayList<Sample>();
                        arrayList.add(new Sample());
                        arrayList.add(new Sample());
                        return (List<T>) arrayList;
                    };
                };
            }
        });
        Project project = new Project();

        assertNull(this.projectManagementService.getSampleByExternalId(project, "def"));
    }
    @Test
    public void testGetSampleByExternalIdReturnsNull() throws Exception {
        ProjectManagementServiceBean bean = (ProjectManagementServiceBean) this.projectManagementService;
        bean.setDaoFactory(new DaoFactoryStub(){
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub(){
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
                        return null;
                    };
                };
            }
        });
        Project project = new Project();

        assertNull(this.projectManagementService.getSampleByExternalId(project, "abc"));
    }
    @Test
    public void testGetSampleByExternalId() throws Exception {
        ProjectManagementServiceBean bean = (ProjectManagementServiceBean) this.projectManagementService;
        bean.setDaoFactory(new DaoFactoryStub(){
            @Override
            public SearchDao getSearchDao() {
                return new SearchDaoStub(){
                    public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
                        ArrayList<T> arrayList = new ArrayList<T>();
                        arrayList.add(entityToMatch);
                        return arrayList;
                    };
                };
            }
        });
        Project project = new Project();

        Sample sampleByExternalId = this.projectManagementService.getSampleByExternalId(project, "abc");
        assertNotNull(sampleByExternalId);
        assertSame(project.getExperiment(), sampleByExternalId.getExperiment());
    }

    @Test
    public void testSearchByCategory() {
        assertEquals(0, this.projectManagementService.searchByCategory(null, "test",
            SearchSampleCategory.values()).size());

    }

    @Test
    public void testSearchSamplesByCharacteristicCategory() {
        assertEquals(Collections.EMPTY_LIST, this.projectManagementService.searchSamplesByCharacteristicCategory(null, "test"));
    }

    @Test
    public void testSearchSourcesByCharacteristicCategory() {
        assertEquals(Collections.EMPTY_LIST, this.projectManagementService.searchSourcesByCharacteristicCategory(null, "test"));
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
        assertEquals(0, this.projectManagementService.searchCount("test", SearchSampleCategory.values()));
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
        public void save(PersistentObject caArrayObject) {
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Project> getProjectsForCurrentUser(boolean showPublic, PageSortParams<Project> pageSortParams) {
            return new ArrayList<Project>();
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.lastDeleted = caArrayEntity;
            this.savedObjects.remove(caArrayEntity.getId());
        }

        public PersistentObject getLastDeleted() {
            return this.lastDeleted;
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
            PersistentObject po = this.projectDao.savedObjects.get(entityId);
            if (po != null) {
                return (T) po;
            }
            if (Sample.class.equals(entityClass)) {
                Sample s = getSample(entityId);
                return (T) s;
            }
            else if (Source.class.equals(entityClass)) {
                Source s = getSource(entityId);
                return (T) s;
            }
            else if (Factor.class.equals(entityClass)) {
                Factor f = getFactor(entityId);
                return (T) f;
            } else if (Extract.class.equals(entityClass)) {
                Extract e = getExtract(entityId);
                return (T) e;
            } else if (Project.class.equals(entityClass)) {
                return (T) getProject(entityId);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("deprecation")
        private Project getProject(Long id) {
            if (this.projectDao.savedObjects.containsKey(id)) {
                return (Project) this.projectDao.savedObjects.get(id);
            }
            Project project = new Project();
            project.setId(id);
            this.projectDao.save(project);
            return project;
        }

        private Extract getExtract(Long entityId) {
            Extract e = new Extract();
            setABM(e, entityId);
            Sample s = getSample(entityId++);
            e.getSamples().add(s);
            s.getExtracts().add(e);
            return e;
        }

        private Sample getSample(Long entityId) {
            Sample s = new Sample();
            setABM(s, entityId);
            Source source = getSource(entityId++);
            s.getSources().add(source);
            source.getSamples().add(s);
            return s;
        }

        private Source getSource(Long entityId) {
            Source s = new Source();
            setABM(s, entityId);
            return s;
        }

        @SuppressWarnings("deprecation")
        private Factor getFactor(Long entityId) {
            Factor f = new Factor();
            f.setName("Test");
            f.setId(entityId);
            return f;
        }

        @SuppressWarnings("deprecation")
        private void setABM(AbstractBioMaterial abm, Long entityId) {
            abm.setName("Test");
            abm.setDescription("Test");
            abm.setId(entityId);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
            List<T> results = new ArrayList<T>();
            if (entityToMatch instanceof Sample) {
                Sample sampleToMatch = (Sample) entityToMatch;
                for (PersistentObject po : this.projectDao.savedObjects.values()) {
                    Project p = (Project) po;
                    if (sampleToMatch.getExperiment().getProject().getId().equals(p.getId())) {
                        for (Sample s : p.getExperiment().getSamples()) {
                            if (sampleToMatch.getExternalSampleId().equals(s.getExternalSampleId())) {
                                results.add((T) s);
                            }
                        }
                    }
                }
            }
            return results;
        }
    }

    private static class LocalSessionContextStub extends SessionContextStub {

        @Override
        public Principal getCallerPrincipal() {
            return new Principal() {
                public String getName() {
                    return "testusername";
                }
            };

        }

    }
}
