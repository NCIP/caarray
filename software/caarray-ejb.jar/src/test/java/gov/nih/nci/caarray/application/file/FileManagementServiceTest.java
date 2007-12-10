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
import gov.nih.nci.caarray.application.UserTransactionStub;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorStub;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

public class FileManagementServiceTest {

    private FileManagementService fileManagementService;
    private final LocalFileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    private final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() {
        FileManagementMDB fileManagementMDB = new FileManagementMDB();
        fileManagementMDB.setDaoFactory(daoFactoryStub);
        fileManagementMDB.setTransaction(new UserTransactionStub());
        DirectJobSubmitter submitter = new DirectJobSubmitter(fileManagementMDB);
        FileManagementServiceBean fileManagementServiceBean = new FileManagementServiceBean();
        fileManagementServiceBean.setSubmitter(submitter);
        fileManagementServiceBean.setDaoFactory(daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, new LocalArrayDataServiceStub());
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceStub);
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, new MageTabTranslatorStub());
        fileManagementService = fileManagementServiceBean;
    }

    @Test
    public void testValidateFiles() {
        Project project = getTgaBroadTestProject();
        fileManagementService.validateFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.VALIDATED, file.getFileStatus());
        }
    }

    @Test
    public void testImportFiles() {
        Project project = getTgaBroadTestProject();
        fileManagementService.importFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportIllegalState() {
        Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.VALIDATING);
        fileManagementService.importFiles(project, project.getFileSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalState() {
        Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.IMPORTED);
        fileManagementService.validateFiles(project, project.getFileSet());
    }

    @Test
    public void testImportDoesntOverwriteExistingExperiment() {
        Project project = getTgaBroadTestProject();
        Experiment experiment = new Experiment();
        String title = "title" + System.currentTimeMillis();
        experiment.setTitle(title);
        project.setExperiment(experiment);
        fileManagementService.importFiles(project, project.getFileSet());
        assertEquals(title, project.getExperiment().getTitle());
    }

    private Project getTgaBroadTestProject() {
        Project project = new Project();
        daoFactoryStub.searchDaoStub.save(project);
        addFiles(project, TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_SET).getFiles());
        saveFiles(project.getFiles());
        assertEquals(29, project.getFiles().size());
        return project;
    }

    private void addFiles(Project project, Set<CaArrayFile> files) {
        for (CaArrayFile file : files) {
            project.getFiles().add(file);
            file.setProject(project);
        }
    }

    private void saveFiles(SortedSet<CaArrayFile> files) {
        for (CaArrayFile file : files) {
            daoFactoryStub.searchDaoStub.save(file);
        }
    }

    @Test
    public void testAddSupplementalFiles() {
        Project project = getTgaBroadTestProject();
        fileManagementService.addSupplementalFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.SUPPLEMENTAL, file.getFileStatus());
        }
    }

    @Test
    public void testImportArrayDesignFile() throws InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CDF);
        design.setDesignFile(caArrayFile);
        fileManagementService.addArrayDesign(design, caArrayFile);
        fileManagementService.importArrayDesignDetails(design);
        assertTrue(arrayDesignServiceStub.importCalled);
    }

    @Test
    public void testImportArrayDesign() {
        Project project = getTgaBroadTestProject();
        project.getFiles().clear();
        CaArrayFile caArrayFile = fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CDF);
        caArrayFile.setProject(project);
        project.getFiles().add(caArrayFile);
        daoFactoryStub.searchDaoStub.save(caArrayFile);
        fileManagementService.importFiles(project, project.getFileSet());
        assertTrue(arrayDesignServiceStub.importCalled);
    }

    @Test(expected = InvalidDataFileException.class)
    public void testImportArrayDesignFileInvalid() throws InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = fileAccessServiceStub.add(AffymetrixArrayDataFiles.TEST3_CHP);
        caArrayFile.setFileType(FileType.AFFYMETRIX_CHP);
        design.setDesignFile(caArrayFile);
        fileManagementService.addArrayDesign(design, caArrayFile);
    }


    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {

        @Override
        public FileValidationResult validate(CaArrayFile arrayDataFile) {
            arrayDataFile.setFileStatus(FileStatus.VALIDATED);
            return new FileValidationResult(new File(arrayDataFile.getName()));
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
            importCalled = true;
        }

        @Override
        public ArrayDesign importDesign(CaArrayFile designFile) {
            importCalled = true;
            return new ArrayDesign();
        }

    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        private final LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();

        @Override
        public SearchDao getSearchDao() {
            return searchDaoStub;
        }
    }

    private static class LocalSearchDaoStub extends SearchDaoStub {

        private static long nextId = 1;
        Map<Long, PersistentObject> objectMap = new HashMap<Long, PersistentObject>();

        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            return (T) objectMap.get(entityId);
        }

        @SuppressWarnings("deprecation")
        void save(AbstractCaArrayObject object) {
            object.setId(nextId++);
            objectMap.put(object.getId(), object);
        }

    }

    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {
        @Override
        public File getFile(CaArrayFile caArrayFile) {
            if (new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName());
            } else if (new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName()).exists()) {
                return new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName());
            } else {
                throw new IllegalArgumentException("Don't know location of " + caArrayFile.getName());
            }
        }
    }

}
