/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
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
public class FileManagementServiceTest {

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
                    getDaoFactory().getArrayDao().getArrayDesign(arrayDesignJob.getArrayDesignId()).getDesignFile()
                            .setFileStatus(FileStatus.IMPORT_FAILED);
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
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_SET).getFiles());
        saveFiles(project.getFiles());
        assertEquals(29, project.getFiles().size());
        return project;
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
        design.setDesignFile(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, caArrayFile);
        this.fileManagementService.importArrayDesignDetails(design);
        assertTrue(this.arrayDesignServiceStub.importCalled);

        caArrayFile = this.fileAccessServiceStub.add(GenepixArrayDesignFiles.DEMO_GAL);
        caArrayFile.setFileType(FileType.GENEPIX_GAL);
        design.setDesignFile(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, caArrayFile);
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
        design.setDesignFile(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, caArrayFile);
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
        design.setDesignFile(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, caArrayFile);
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

    @Test
    public void testImportMageTabAndUnparsedDataFile() {
        Project project = getTgaBroadTestProject();
        CaArrayFile expFile = this.fileAccessServiceStub.add(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP);
        expFile.setFileType(FileType.AFFYMETRIX_EXP);
        addFile(project, expFile);
        this.fileManagementService.importFiles(project, project.getFileSet(), DataImportOptions
                .getAutoCreatePerFileOptions());
        for (CaArrayFile file : project.getFiles()) {
            if (expFile.equals(file)) {
                assertEquals(FileStatus.IMPORTED_NOT_PARSED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.IMPORTED, file.getFileStatus());
            }
        }
    }

    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {

        @Override
        public FileValidationResult validate(CaArrayFile arrayDataFile) {
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
        public FileValidationResult validateDesign(ArrayDesign design) {
            return validateDesign(design.getDesignFile());
        }

        @Override
        public FileValidationResult validateDesign(CaArrayFile designFile) {
            FileValidationResult result = new FileValidationResult(new File(designFile.getName()));
            if (!designFile.getFileType().isArrayDesign()) {
                result.addMessage(Type.ERROR, "Invalid design");
                designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
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
            } else {
                throw new IllegalArgumentException("Don't know location of " + caArrayFile.getName());
            }
        }
    }

}
