//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.UserTransactionStub;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.hibernate.LockMode;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

@SuppressWarnings("PMD")
public class FileManagementServiceTest extends AbstractServiceTest {

    private FileManagementService fileManagementService;
    private final LocalFileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    private final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() {
        FileManagementMDB fileManagementMDB = new FileManagementMDB() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected void handleUnexpectedError(AbstractFileManagementJob job) {
                if (job instanceof AbstractProjectFilesJob) {
                    AbstractProjectFilesJob projectFilesJob = (AbstractProjectFilesJob) job;
                    for (CaArrayFile file : projectFilesJob.getFileSet(projectFilesJob.getProject()).getFiles()) {
                        file.setFileStatus(FileStatus.IMPORT_FAILED);
                    }
                } else if (job instanceof ArrayDesignFileImportJob) {
                    ArrayDesignFileImportJob arrayDesignJob = (ArrayDesignFileImportJob) job;
                    getDaoFactory().getArrayDao().getArrayDesign(
                            arrayDesignJob.getArrayDesignId()).getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected int getBackgroundThreadTransactionTimeout() {
                return 3600;
            }
        };
        fileManagementMDB.setDaoFactory(this.daoFactoryStub);
        fileManagementMDB.setTransaction(new UserTransactionStub());
        DirectJobSubmitter submitter = new DirectJobSubmitter(fileManagementMDB);
        FileManagementServiceBean fileManagementServiceBean = new FileManagementServiceBean();
        fileManagementServiceBean.setSubmitter(submitter);
        fileManagementServiceBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, new LocalArrayDataServiceStub());
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, this.arrayDesignServiceStub);
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, new MageTabTranslatorStub());
        this.fileManagementService = fileManagementServiceBean;
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(
                this.fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }

    @Test
    public void testValidateFiles() {
        Project project = getTgaBroadTestProject();
        this.fileManagementService.validateFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.VALIDATED, file.getFileStatus());
        }
    }

    @Test
    public void testImportFiles() {
        Project project = getTgaBroadTestProject();
        this.fileManagementService.importFiles(project, project.getFileSet(), DataImportOptions
                .getAutoCreatePerFileOptions());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        }
    }

    @Test
    public void testImportMageTabFiles() throws Exception {
        Project project = getMageTabSpecProject();
        this.fileManagementService.importFiles(project, project.getFileSet(), null);
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        }
    }

    @Test
    public void testFindRefFiles() throws Exception {
        Project project = getMageTabSpecProject();

        // add some random file to project
        CaArrayFile nonRelatedFile = new CaArrayFile();
        nonRelatedFile.setName("NonRelatedFile.CEL");
        nonRelatedFile.setFileType(FileType.AFFYMETRIX_CEL);
        nonRelatedFile.setFileStatus(FileStatus.UPLOADED);
        nonRelatedFile.setProject(project);
        project.getFiles().add(nonRelatedFile);

        // find a non idf file
        CaArrayFile nonIdfFile = null;
        for (CaArrayFile caf : project.getFileSet().getFiles()) {
            if (!FileType.MAGE_TAB_IDF.equals(caf.getFileType())) {
                nonIdfFile = caf;
                break;
            }
        }

        // nothing should be selected
        try {
            this.fileManagementService
                .findIdfRefFileNames(nonIdfFile, project);
            fail("non IDF file selected but did not give error.");
        } catch (IllegalArgumentException iae) {
            // do nothing
        }


        // find the idf
        CaArrayFile idfFile = null;
        for (CaArrayFile caf : project.getFileSet().getFiles()) {
            if (FileType.MAGE_TAB_IDF.equals(caf.getFileType())) {
                idfFile = caf;
                break;
            }
        }

        List<String> filenames =
            this.fileManagementService.findIdfRefFileNames(idfFile, project);
        // 15 in all minus original idf = 14
        assertEquals(14, filenames.size());
        assertTrue(!filenames.contains("NonRelatedFile.CEL"));
    }


    @Test
    public void testMultidfRefFiles() throws Exception {
        Project project = new Project();
        // combine two sets of idf and ref files
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET).getFiles());
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET).getFiles());

        // find the idf related to the spec file set
        CaArrayFile idfFile = null;
        for (CaArrayFile caf : project.getFileSet().getFiles()) {
            if (FileType.MAGE_TAB_IDF.equals(caf.getFileType())
                    && MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()
                    .equals(caf.getName())) {
                idfFile = caf;
                break;
            }
        }
        assertNotNull(idfFile);
        List<String> filenames =
            this.fileManagementService.findIdfRefFileNames(idfFile, project);
        // 15 in all minus original idf = 14
        assertEquals(14, filenames.size());
        // only the sdrf associated with the idf submitted should be present.
        assertTrue(!filenames.contains(MageTabDataFiles.TCGA_BROAD_SDRF.getName()));
    }



    @Test
    public void testUpdateAnnotationsFromMageTabFiles() throws Exception {
        Project project = getMageTabSpecProject();
        CaArrayFileSet newFiles = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET);
        addFiles(project, newFiles.getFiles());
        this.fileManagementService.importFiles(project, newFiles, null);
        // import should fail on update_annotations sdrf, but all original spec files should still be uploaded
        for (CaArrayFile file : project.getFiles()) {
            if (file.getName().equals(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_SDRF.getName())) {
                assertEquals(FileStatus.VALIDATION_ERRORS, file.getFileStatus());
            } else if (file.getName().equals(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_IDF.getName())) {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.UPLOADED, file.getFileStatus());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportIllegalState() {
        Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.VALIDATING);
        this.fileManagementService.importFiles(project, project.getFileSet(), DataImportOptions
                .getAutoCreatePerFileOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalState() {
        Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.IMPORTED);
        this.fileManagementService.validateFiles(project, project.getFileSet());
    }

    @Test
    public void testImportDoesntOverwriteExistingExperiment() {
        Project project = getTgaBroadTestProject();
        Experiment experiment = new Experiment();
        String title = "title" + System.currentTimeMillis();
        experiment.setTitle(title);
        project.setExperiment(experiment);
        this.fileManagementService.importFiles(project, project.getFileSet(), DataImportOptions
                .getAutoCreatePerFileOptions());
        assertEquals(title, project.getExperiment().getTitle());
    }

    private Project getTgaBroadTestProject() {
        Project project = new Project();
        this.daoFactoryStub.searchDaoStub.save(project);
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET).getFiles());
        saveFiles(project.getFiles());
        assertEquals(29, project.getFiles().size());
        return project;
    }

    private Project getMageTabSpecProject() throws Exception {
        ArrayDesign design = importArrayDesign("design name", AffymetrixArrayDesignFiles.TEST3_CDF, FileType.AFFYMETRIX_CDF);

        Project project = new Project();
        project.getExperiment().getArrayDesigns().add(design);
        this.daoFactoryStub.searchDaoStub.save(project);
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET).getFiles());
        assertEquals(15, project.getFiles().size());
        return project;
    }

    private ArrayDesign importArrayDesign(String designName, File designFile, FileType designFileType)
            throws InvalidDataFileException, IllegalAccessException {
        ArrayDesign design = new ArrayDesign();
        design.setName(designName);
        this.daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(designFile);
        caArrayFile.setFileType(designFileType);
        design.addDesignFile(caArrayFile);
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        this.fileManagementService.importArrayDesignDetails(design);
        return design;
    }

    private void addFiles(Project project, Set<CaArrayFile> files) {
        for (CaArrayFile file : files) {
            addFile(project, file);
        }
    }

    private void addFile(Project project, CaArrayFile file) {
        project.getFiles().add(file);
        file.setProject(project);
        this.daoFactoryStub.searchDaoStub.save(file);
    }

    private void saveFiles(SortedSet<CaArrayFile> files) {
        for (CaArrayFile file : files) {
            this.daoFactoryStub.searchDaoStub.save(file);
        }
    }

    @Test
    public void testAddSupplementalFiles() {
        Project project = getTgaBroadTestProject();
        this.fileManagementService.addSupplementalFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.SUPPLEMENTAL, file.getFileStatus());
        }
    }

    @Test
    public void testImportArrayDesignFile() throws InvalidDataFileException, IllegalAccessException {
        ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        this.daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CDF);
        design.addDesignFile(caArrayFile);
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        this.fileManagementService.importArrayDesignDetails(design);
        assertTrue(this.arrayDesignServiceStub.importCalled);

        caArrayFile = this.fileAccessServiceStub.add(GenepixArrayDesignFiles.DEMO_GAL);
        caArrayFile.setFileType(FileType.GENEPIX_GAL);
        design.getDesignFiles().clear();
        design.addDesignFile(caArrayFile);
        fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        this.fileManagementService.importArrayDesignDetails(design);
        assertTrue(this.arrayDesignServiceStub.importCalled);
    }

    @Test
    public void testErrorHandling() throws InvalidDataFileException, IllegalAccessException {
        ArrayDesign design = new ArrayDesign();
        design.setName("throw exception");
        this.daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CDF);
        design.addDesignFile(caArrayFile);
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
        try {
            this.fileManagementService.importArrayDesignDetails(design);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(FileStatus.IMPORT_FAILED, caArrayFile.getFileStatus());
    }

    @Test(expected = InvalidDataFileException.class)
    public void testImportArrayDesignFileInvalid() throws InvalidDataFileException, IllegalAccessException {
        ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        this.daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDataFiles.TEST3_CHP);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CHP);
        design.addDesignFile(caArrayFile);
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
    }

    @Test
    public void testValidateMageTabAndUnparsedDataFile() {
        Project project = getTgaBroadTestProject();
        CaArrayFile expFile = this.fileAccessServiceStub.add(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP);
        expFile.setFileType(FileType.AFFYMETRIX_EXP);
        addFile(project, expFile);
        this.fileManagementService.validateFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            if (expFile.equals(file)) {
                assertEquals(FileStatus.VALIDATED_NOT_PARSED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            }
        }
    }

    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {

        @Override
        public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet) {
            if (arrayDataFile.getFileType().isParseableData()) {
                arrayDataFile.setFileStatus(FileStatus.VALIDATED);
            } else {
                arrayDataFile.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
            }
            return new FileValidationResult(new File(arrayDataFile.getName()));
        }

        @Override
        public void importData(CaArrayFile caArrayFile, boolean createAnnotation, DataImportOptions importOptions)
                throws InvalidDataFileException {
            if (caArrayFile.getFileType().isParseableData()) {
                caArrayFile.setFileStatus(FileStatus.IMPORTED);
            } else {
                caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
            }
        }
    }

    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {

        protected boolean importCalled;

        @Override
        public ValidationResult validateDesign(ArrayDesign design) {
            return validateDesign(design.getDesignFiles());
        }

        @Override
        public ValidationResult validateDesign(Set<CaArrayFile> designFiles) {
            CaArrayFile designFile = designFiles.iterator().next();
            File file = new File(designFile.getName());
            ValidationResult result = new ValidationResult();
            if (!designFile.getFileType().isArrayDesign()) {
                result.addMessage(file, Type.ERROR, "Invalid design");
                designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
                designFile.setValidationResult(result.getFileValidationResult(file));
            } else {
                designFile.setFileStatus(FileStatus.VALIDATED);
            }

            return result;
        }

        @Override
        public void importDesign(ArrayDesign arrayDesign) {
            validateDesign(arrayDesign);
        }

        @Override
        public void importDesignDetails(ArrayDesign arrayDesign) {
            if ("throw exception".equals(arrayDesign.getName())) {
                throw new IllegalArgumentException();
            }
            this.importCalled = true;
        }

    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        private final LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
        private final ArrayDao arrayDaoStub = new LocalArrayDao(this.searchDaoStub);

        @Override
        public SearchDao getSearchDao() {
            return this.searchDaoStub;
        }

        @Override
        public ArrayDao getArrayDao() {
            return this.arrayDaoStub;
        }

    }

    private static class LocalArrayDao extends ArrayDaoStub {

        private final LocalSearchDaoStub searchDaoStub;

        public LocalArrayDao(LocalSearchDaoStub searchDaoStub) {
            this.searchDaoStub = searchDaoStub;
        }

        @Override
        public ArrayDesign getArrayDesign(long id) {
            return this.searchDaoStub.retrieve(ArrayDesign.class, id);
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {

        private static long nextId = 1;
        Map<Long, PersistentObject> objectMap = new HashMap<Long, PersistentObject>();

        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            return (T) this.objectMap.get(entityId);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
            return (T) this.objectMap.get(entityId);
        }

        @Override
        public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
            List<T> list = new ArrayList<T>();
            for (Serializable id : ids) {
                T t = this.retrieve(entityClass, (Long) id);
                list.add(t);
            }
            return list;
        }

        @SuppressWarnings("deprecation")
        void save(AbstractCaArrayObject object) {
            object.setId(nextId++);
            this.objectMap.put(object.getId(), object);
        }

    }

    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {
        @Override
        public File getFile(CaArrayFile caArrayFile) {
            if (new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName());
            } else if (new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, caArrayFile.getName());
            } else if (new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName()).exists()) {
                return new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName());
            } else if (new File(MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY, caArrayFile.getName());
            } else if (new File(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_DIRECTORY, caArrayFile.getName());
            } else {
                throw new IllegalArgumentException("Don't know location of " + caArrayFile.getName());
            }
        }
    }

}
