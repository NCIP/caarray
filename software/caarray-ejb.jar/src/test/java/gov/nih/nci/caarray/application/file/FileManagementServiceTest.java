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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.UserTransactionStub;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorStub;
import gov.nih.nci.caarray.application.util.CaArrayFileSetNonSplitter;
import gov.nih.nci.caarray.application.util.CaArrayFileSetSplitter;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.JobDaoSingleJobStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.hibernate.LockMode;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

@SuppressWarnings("PMD")
public class FileManagementServiceTest extends AbstractServiceTest {
    protected static FileType AFFYMETRIX_CDF = new FileType("AFFYMETRIX_CDF", FileCategory.ARRAY_DESIGN, true, "CDF");
    protected static FileType AFFYMETRIX_CHP = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true, "CHP");
    protected static FileType AFFYMETRIX_DAT = new FileType("AFFYMETRIX_DAT", FileCategory.RAW_DATA, false, "DAT");
    protected static FileType AFFYMETRIX_CEL = new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true, "CEL");

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
            public void handleUnexpectedError(ExecutableJob job) {
                if (job instanceof AbstractProjectFilesJob) {
                    final AbstractProjectFilesJob projectFilesJob = (AbstractProjectFilesJob) job;
                    for (final CaArrayFile file : projectFilesJob.getFileSet().getFiles()) {
                        file.setFileStatus(FileStatus.IMPORT_FAILED);
                    }
                } else if (job instanceof ArrayDesignFileImportJob) {
                    final ArrayDesignFileImportJob arrayDesignJob = (ArrayDesignFileImportJob) job;
                    FileManagementServiceTest.this.daoFactoryStub.getArrayDao()
                            .getArrayDesign(arrayDesignJob.getArrayDesignId()).getDesignFileSet()
                            .updateStatus(FileStatus.IMPORT_FAILED);
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

        fileManagementMDB.setTransaction(new UserTransactionStub());
        final JobQueue jobQueue = new JobDaoSingleJobStub();
        final DirectJobSubmitter submitter = new DirectJobSubmitter(fileManagementMDB, jobQueue);
        final DataStorageFacade dataStorageFacade = this.fileAccessServiceStub.createStorageFacade();

        final FileManagementServiceBean fileManagementServiceBean = new FileManagementServiceBean();
        final Provider<MageTabImporter> mageTabImporterProvider = new Provider<MageTabImporter>() {
            @Override
            public MageTabImporter get() {
                return new MageTabImporterImpl(ServiceLocatorFactory.getMageTabTranslator(),
                        FileManagementServiceTest.this.daoFactoryStub.getSearchDao(),
                        FileManagementServiceTest.this.daoFactoryStub.getProjectDao(), dataStorageFacade);
            }
        };

        final MageTabTranslator mageTabTranslator = new MageTabTranslatorStub();
        final MageTabImporter mageTabImporter =
                new MageTabImporterImpl(mageTabTranslator, this.daoFactoryStub.getSearchDao(),
                        this.daoFactoryStub.getProjectDao(), dataStorageFacade);

        final ArrayDataService arrayDataService = new LocalArrayDataServiceStub();
        final ArrayDataImporter arrayDataImporter =
                new ArrayDataImporterImpl(arrayDataService, this.daoFactoryStub.getFileDao(),
                        this.daoFactoryStub.getProjectDao(), this.daoFactoryStub.getSearchDao());

        final FileAccessService fileAccessService = mock(FileAccessService.class);
        
        final Provider<ArrayDao> arrayDaoProvider = Providers.of(this.daoFactoryStub.getArrayDao());
        final Provider<ArrayDataImporter> arrayDataImporterProvider = Providers.of(arrayDataImporter);
        final Provider<MageTabImporter> mageTabeImporterProvider = Providers.of(mageTabImporter);
        final Provider<FileAccessService> fileAccessServiceProvider = Providers.of(fileAccessService);
        final Provider<ProjectDao> projectDaoProvider = Providers.of(this.daoFactoryStub.getProjectDao());
        final Provider<SearchDao> searchDaoProvider = Providers.of(this.daoFactoryStub.getSearchDao());
        final Provider<UsernameHolder> usernameHolderProvider = Providers.of(mock(UsernameHolder.class));
        
        final CaArrayFileSetSplitter splitter = new CaArrayFileSetNonSplitter();
        final Provider<CaArrayFileSetSplitter> caArrayFileSetSplitterProvider = 
                Providers.of(splitter);
        
        final JobFactory jobFactory =
                new JobFactoryImpl(arrayDaoProvider, arrayDataImporterProvider,
                        mageTabeImporterProvider, fileAccessServiceProvider, projectDaoProvider, searchDaoProvider,
                        caArrayFileSetSplitterProvider);

        fileManagementServiceBean.setArrayDao(this.daoFactoryStub.getArrayDao());
        fileManagementServiceBean.setFileDao(this.daoFactoryStub.getFileDao());
        fileManagementServiceBean.setProjectDao(this.daoFactoryStub.getProjectDao());
        fileManagementServiceBean.setSearchDao(this.daoFactoryStub.getSearchDao());
        fileManagementServiceBean.setJobFactory(jobFactory);
        fileManagementServiceBean.setJobSubmitter(submitter);
        fileManagementServiceBean.setMageTabImporterProvider(mageTabImporterProvider);

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, new LocalArrayDataServiceStub());
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, this.arrayDesignServiceStub);
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, new MageTabTranslatorStub());

        final DesignFileHandler affyDesignHandler = mock(DesignFileHandler.class);
        when(affyDesignHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(AFFYMETRIX_CDF));

        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                // data files
                final Multibinder<DataFileHandler> dataFileBinder =
                        Multibinder.newSetBinder(binder(), DataFileHandler.class);
                dataFileBinder.addBinding().to(TestDataHandler.class);

                // design files
                final Multibinder<DesignFileHandler> designFileBinder =
                        Multibinder.newSetBinder(binder(), DesignFileHandler.class);
                designFileBinder.addBinding().toInstance(affyDesignHandler);

                bind(FileTypeRegistry.class).to(FileTypeRegistryImpl.class);
                bind(DataStorageFacade.class).toInstance(dataStorageFacade);

                bind(MageTabImporter.class).toProvider(mageTabImporterProvider);
                requestStaticInjection(CaArrayFile.class);
                requestStaticInjection(TestMageTabSets.class);

                bind(JobQueue.class).toInstance(jobQueue);
                bind(CaArrayHibernateHelper.class).toInstance(mock(CaArrayHibernateHelper.class));
                bind(UsernameHolder.class).toProvider(usernameHolderProvider);
            }
        }, new UnparsedModule());
        this.fileAccessServiceStub.setTypeRegistry(injector.getInstance(FileTypeRegistry.class));
        injector.injectMembers(fileManagementMDB);
        this.fileManagementService = fileManagementServiceBean;

    }

    @Test
    public void testValidateFiles() {
        final Project project = getTgaBroadTestProject();
        this.fileManagementService.validateFiles(project, project.getFileSet());
        for (final CaArrayFile file : project.getFiles()) {
            assertEquals("Wrong status for file " + file.getName(),
                    file.getFileType().isDataMatrix() ? FileStatus.VALIDATED_NOT_PARSED : FileStatus.VALIDATED,
                    file.getFileStatus());
        }
    }

    @Test
    public void testImportFiles() {
        final Project project = getTgaBroadTestProject();
        this.fileManagementService.importFiles(project, project.getFileSet(),
                DataImportOptions.getAutoCreatePerFileOptions());
        for (final CaArrayFile file : project.getFiles()) {
            assertEquals("Wrong status for file " + file.getName(),
                    file.getFileType().isDataMatrix() ? FileStatus.IMPORTED_NOT_PARSED : FileStatus.IMPORTED,
                    file.getFileStatus());
        }
    }

    @Test
    public void testImportMageTabFiles() throws Exception {
        final Project project = getMageTabSpecProject();
        this.fileManagementService.importFiles(project, project.getFileSet(), null);
        for (final CaArrayFile file : project.getFiles()) {
            assertEquals("Wrong status for file " + file.getName(),
                    file.getFileType().isDataMatrix() ? FileStatus.IMPORTED_NOT_PARSED : FileStatus.IMPORTED,
                    file.getFileStatus());
        }
    }

    @Test
    public void testFindRefFiles() throws Exception {
        final Project project = getMageTabSpecProject();

        // add some random file to project
        final CaArrayFile nonRelatedFile = new CaArrayFile();
        nonRelatedFile.setName("NonRelatedFile.CEL");
        nonRelatedFile.setFileStatus(FileStatus.UPLOADED);
        nonRelatedFile.setProject(project);
        project.getFiles().add(nonRelatedFile);

        // find a non idf file
        CaArrayFile nonIdfFile = null;
        for (final CaArrayFile caf : project.getFileSet().getFiles()) {
            if (!FileTypeRegistry.MAGE_TAB_IDF.equals(caf.getFileType())) {
                nonIdfFile = caf;
                break;
            }
        }

        // nothing should be selected
        try {
            this.fileManagementService.findIdfRefFileNames(nonIdfFile, project);
            fail("non IDF file selected but did not give error.");
        } catch (final IllegalArgumentException iae) {
            // do nothing
        }

        // find the idf
        CaArrayFile idfFile = null;
        for (final CaArrayFile caf : project.getFileSet().getFiles()) {
            if (FileTypeRegistry.MAGE_TAB_IDF.equals(caf.getFileType())) {
                idfFile = caf;
                break;
            }
        }

        final List<String> filenames = this.fileManagementService.findIdfRefFileNames(idfFile, project);
        // 15 in all minus original idf = 14
        assertEquals(14, filenames.size());
        assertTrue(!filenames.contains("NonRelatedFile.CEL"));
    }

    @Test
    public void testMultidfRefFiles() throws Exception {
        final Project project = new Project();
        // combine two sets of idf and ref files
        addFiles(project, getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET, this.fileAccessServiceStub)
                .getFiles());
        addFiles(project, getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET, this.fileAccessServiceStub).getFiles());

        // find the idf related to the spec file set
        CaArrayFile idfFile = null;
        for (final CaArrayFile caf : project.getFileSet().getFiles()) {
            if (FileTypeRegistry.MAGE_TAB_IDF.equals(caf.getFileType())
                    && MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName().equals(caf.getName())) {
                idfFile = caf;
                break;
            }
        }
        assertNotNull(idfFile);
        final List<String> filenames = this.fileManagementService.findIdfRefFileNames(idfFile, project);
        // 15 in all minus original idf = 14
        assertEquals(14, filenames.size());
        // only the sdrf associated with the idf submitted should be present.
        assertTrue(!filenames.contains(MageTabDataFiles.TCGA_BROAD_SDRF.getName()));
    }

    @Test
    public void testUpdateAnnotationsFromMageTabFiles() throws Exception {
        final Project project = getMageTabSpecProject();
        final CaArrayFileSet newFiles =
                getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET,
                        this.fileAccessServiceStub);
        addFiles(project, newFiles.getFiles());
        this.fileManagementService.importFiles(project, newFiles, null);
        // import should pass on update_annotations sdrf, because referenced data files can be imported later
        for (final CaArrayFile file : project.getFiles()) {
            if (file.getName().equals(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_SDRF.getName())) {
                assertEquals(FileStatus.IMPORTED, file.getFileStatus());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportIllegalState() {
        final Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.VALIDATING);
        this.fileManagementService.importFiles(project, project.getFileSet(),
                DataImportOptions.getAutoCreatePerFileOptions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalState() {
        final Project project = getTgaBroadTestProject();
        project.getFiles().iterator().next().setFileStatus(FileStatus.IMPORTED);
        this.fileManagementService.validateFiles(project, project.getFileSet());
    }

    @Test
    public void testImportDoesntOverwriteExistingExperiment() {
        final Project project = getTgaBroadTestProject();
        final Experiment experiment = new Experiment();
        final String title = "title" + System.currentTimeMillis();
        experiment.setTitle(title);
        project.setExperiment(experiment);
        this.fileManagementService.importFiles(project, project.getFileSet(),
                DataImportOptions.getAutoCreatePerFileOptions());
        assertEquals(title, project.getExperiment().getTitle());
    }

    private Project getTgaBroadTestProject() {
        final Project project = new Project();
        this.daoFactoryStub.searchDaoStub.save(project);
        addFiles(project, getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET, this.fileAccessServiceStub).getFiles());
        saveFiles(project.getFiles());
        assertEquals(29, project.getFiles().size());
        return project;
    }

    private Project getMageTabSpecProject() throws Exception {
        final ArrayDesign design =
                importArrayDesign("design name", AffymetrixArrayDesignFiles.TEST3_CDF, AFFYMETRIX_CDF);

        final Project project = new Project();
        project.getExperiment().getArrayDesigns().add(design);
        this.daoFactoryStub.searchDaoStub.save(project);
        addFiles(project, getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET, this.fileAccessServiceStub)
                .getFiles());
        assertEquals(15, project.getFiles().size());
        return project;
    }

    private ArrayDesign importArrayDesign(String designName, File designFile, FileType designFileType)
            throws InvalidDataFileException, IllegalAccessException {
        final ArrayDesign design = new ArrayDesign();
        design.setName(designName);
        this.daoFactoryStub.searchDaoStub.save(design);
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(designFile);
        caArrayFile.setFileType(designFileType);
        design.addDesignFile(caArrayFile);
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        this.fileManagementService.importArrayDesignDetails(design);
        return design;
    }

    private void addFiles(Project project, Set<CaArrayFile> files) {
        for (final CaArrayFile file : files) {
            addFile(project, file);
        }
    }

    private void addFile(Project project, CaArrayFile file) {
        assertNotNull("no type for file " + file.getName(), file.getFileType());
        project.getFiles().add(file);
        file.setProject(project);
        this.daoFactoryStub.searchDaoStub.save(file);
    }

    private void saveFiles(SortedSet<CaArrayFile> files) {
        for (final CaArrayFile file : files) {
            this.daoFactoryStub.searchDaoStub.save(file);
        }
    }

    @Test
    public void testAddSupplementalFiles() {
        final Project project = getTgaBroadTestProject();
        Date lastModified = project.getExperiment().getLastDataModificationDate();
        this.fileManagementService.addSupplementalFiles(project, project.getFileSet());
        assertNotSame(lastModified, project.getExperiment().getLastDataModificationDate());
        for (final CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.SUPPLEMENTAL, file.getFileStatus());
        }
    }

    @Test
    public void testImportArrayDesignFile() throws InvalidDataFileException, IllegalAccessException {
        final ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        this.daoFactoryStub.searchDaoStub.save(design);
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(AFFYMETRIX_CDF);
        design.addDesignFile(caArrayFile);
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        this.fileManagementService.importArrayDesignDetails(design);
        assertTrue(this.arrayDesignServiceStub.importCalled);

        caArrayFile = this.fileAccessServiceStub.add(GenepixArrayDesignFiles.DEMO_GAL);
        caArrayFile.setFileType(AFFYMETRIX_CDF);
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
        final ArrayDesign design = new ArrayDesign();
        design.setName("throw exception");
        this.daoFactoryStub.searchDaoStub.save(design);
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        caArrayFile.setFileType(AFFYMETRIX_CDF);
        design.addDesignFile(caArrayFile);
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
        assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
        try {
            this.fileManagementService.importArrayDesignDetails(design);
            fail("Expected exception");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertEquals(FileStatus.IMPORT_FAILED, caArrayFile.getFileStatus());
    }

    @Test(expected = InvalidDataFileException.class)
    public void testImportArrayDesignFileInvalid() throws InvalidDataFileException, IllegalAccessException {
        final ArrayDesign design = new ArrayDesign();
        design.setName("design name");
        this.daoFactoryStub.searchDaoStub.save(design);
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(AffymetrixArrayDataFiles.TEST3_CHP);
        caArrayFile.setFileType(AFFYMETRIX_CHP);
        design.addDesignFile(caArrayFile);
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        this.fileManagementService.saveArrayDesign(design, fileSet);
    }

    @Test
    public void testValidateMageTabAndUnparsedDataFile() {
        final Project project = getTgaBroadTestProject();
        final CaArrayFile expFile = this.fileAccessServiceStub.add(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP);
        expFile.setFileType(AFFYMETRIX_DAT);
        addFile(project, expFile);
        this.fileManagementService.validateFiles(project, project.getFileSet());
        for (final CaArrayFile file : project.getFiles()) {
            if (expFile.equals(file) || file.getFileType().isDataMatrix()) {
                assertEquals(FileStatus.VALIDATED_NOT_PARSED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            }
        }
    }

    public static CaArrayFileSet getFileSet(MageTabFileSet inputSet, FileAccessServiceStub fasStub) {
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        for (final FileRef file : inputSet.getAllFiles()) {
            // exclude ADF files
            if (!file.getName().toUpperCase().endsWith("ADF")) {
                final CaArrayFile caArrayFile = fasStub.add(file.getAsFile());
                fileSet.add(caArrayFile);
            }
        }
        return fileSet;
    }

    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {
        @Override
        public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet, boolean reimport) {
            if (arrayDataFile.getFileType().isParseableData()) {
                arrayDataFile.setFileStatus(FileStatus.VALIDATED);
            } else {
                arrayDataFile.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
            }
            return new FileValidationResult();
        }

        @Override
        public void importData(CaArrayFile caArrayFile, boolean createAnnotation, DataImportOptions importOptions, MageTabDocumentSet mTabSet)
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
            final CaArrayFile designFile = designFiles.iterator().next();
            final ValidationResult result = new ValidationResult();
            if (!designFile.getFileType().isArrayDesign()) {
                result.addMessage(designFile.getName(), Type.ERROR, "Invalid design");
                designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
                designFile.setValidationResult(result.getFileValidationResult(designFile.getName()));
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
        public <T extends PersistentObject> List<T>
                retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
            final List<T> list = new ArrayList<T>();
            for (final Serializable id : ids) {
                final T t = this.retrieve(entityClass, (Long) id);
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
        public File getFile(CaArrayFile caArrayFile) {
            if (new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY, caArrayFile.getName());
            } else if (new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_DIRECTORY, caArrayFile.getName());
            } else if (new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName()).exists()) {
                return new File(AffymetrixArrayDesignFiles.TEST3_CDF.getParentFile(), caArrayFile.getName());
            } else if (new File(MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY, caArrayFile.getName()).exists()) {
                return new File(MageTabDataFiles.SPECIFICATION_EXAMPLE_DIRECTORY, caArrayFile.getName());
            } else if (new File(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_DIRECTORY, caArrayFile.getName())
                    .exists()) {
                return new File(MageTabDataFiles.SPECIFICATION_UPDATE_ANNOTATIONS_DIRECTORY, caArrayFile.getName());
            } else {
                throw new IllegalArgumentException("Don't know location of " + caArrayFile.getName());
            }
        }
    }

    private static class TestDataHandler extends AbstractDataFileHandler {
        @Inject
        public TestDataHandler(DataStorageFacade dataStorageFacade) {
            super(dataStorageFacade);
        }

        @Override
        public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
            return null;
        }

        @Override
        public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
            return new QuantitationTypeDescriptor[] {};
        }

        @Override
        public List<LSID> getReferencedArrayDesignCandidateIds() throws PlatformFileReadException {
            return Lists.newArrayList();
        }

        @Override
        public Set<FileType> getSupportedTypes() {
            return Sets.newHashSet(AFFYMETRIX_CEL, AFFYMETRIX_CHP);
        }

        @Override
        public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
                throws PlatformFileReadException {
            // do nothing
        }

        @Override
        public boolean requiresMageTab() throws PlatformFileReadException {
            return false;
        }

        @Override
        public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
                throws PlatformFileReadException {
            // do nothing
        }

        @Override
        public boolean parsesData() {
            return true;
        }

    }
}
