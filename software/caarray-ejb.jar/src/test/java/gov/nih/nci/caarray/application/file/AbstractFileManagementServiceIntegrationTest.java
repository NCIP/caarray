//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import static org.mockito.Mockito.mock;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.TranslationModule;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorBean;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceBean;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.JobDaoSingleJobStub;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedDataHandler;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Modules;
import com.google.inject.util.Providers;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public abstract class AbstractFileManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private FileManagementService fileManagementService;
    private FileAccessServiceStub fileAccessService;

    private Organism testOrganism;
    private Organization testOrganization;
    private Project testProject;
    private Experiment testExperiment;
    private TermSource testTermSource;
    private Category testCategory;
    private Term testTerm;
    private JobFactory jobFactory;

    protected Organism getTestOrganism() {
        this.testOrganism =
                (Organism) this.hibernateHelper.getCurrentSession().load(Organism.class, this.testOrganism.getId());
        return this.testOrganism;
    }

    private Organization getTestOrganization() {
        this.testOrganization =
                (Organization) this.hibernateHelper.getCurrentSession().load(Organization.class,
                        this.testOrganization.getId());
        return this.testOrganization;
    }

    protected Project getTestProject() {
        this.testProject =
                (Project) this.hibernateHelper.getCurrentSession().load(Project.class, this.testProject.getId());
        return this.testProject;
    }

    protected Experiment getTestExperiment() {
        this.testExperiment =
                (Experiment) this.hibernateHelper.getCurrentSession().load(Experiment.class,
                        this.testExperiment.getId());
        return this.testExperiment;
    }

    private Term getTestTerm() {
        this.testTerm = (Term) this.hibernateHelper.getCurrentSession().load(Term.class, this.testTerm.getId());
        return this.testTerm;
    }

    protected FileManagementService getFileManagementService() {
        return this.fileManagementService;
    }

    private FileAccessServiceStub getFileAccessService() {
        return this.fileAccessService;
    }

    @Before
    public void setUp() {
        this.fileManagementService = createFileManagementService();

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ArrayDataService ads = ServiceLocatorFactory.getArrayDataService();
        ads.initialize();
        tx.commit();

        resetSupportingObjects();
        saveSupportingObjects();
        reloadSupportingObjects();

        this.jobFactory = this.injector.getInstance(JobFactory.class);
    }

    private void resetSupportingObjects() {
        this.testOrganism = new Organism();
        this.testOrganization = new Organization();
        this.testProject = new Project();
        this.testExperiment = new Experiment();
        this.testTermSource = new TermSource();
        this.testCategory = new Category();
        this.testTerm = new Term();

        initializeSupportingObjects();
    }

    private void initializeSupportingObjects() {
        setExperimentSummary();
        this.testTermSource.setName("dummy source");
        this.testTermSource.setUrl("test url");
        this.testCategory.setName("Dummy Category");
        this.testCategory.setSource(this.testTermSource);
        this.testOrganism.setScientificName("Foo");
        this.testOrganism.setTermSource(this.testTermSource);
        this.testTerm.setValue("testval");
        this.testTerm.setCategory(this.testCategory);
        this.testTerm.setSource(this.testTermSource);

        this.testProject.setExperiment(this.testExperiment);
        this.testExperiment.setOrganism(this.testOrganism);
        this.testExperiment.setManufacturer(this.testOrganization);
        this.testExperiment.setProject(this.testProject);
        this.testProject.setLocked(false);
    }

    private void setExperimentSummary() {
        this.testExperiment.setTitle("DummyExperiment1");
        this.testExperiment.setDescription("DummyExperiment1Desc");
        final Date currDate = new Date();
        this.testExperiment.setDate(currDate);
        this.testExperiment.setPublicReleaseDate(currDate);
        this.testExperiment.setDesignDescription("Working on it");
    }

    private void saveSupportingObjects() {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Session currentSession = this.hibernateHelper.getCurrentSession();

        final TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        final VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(ExampleSearchCriteria.forEntity(caarraySource)
                .includeNulls().excludeProperties("url"), Order.desc("version"))) == null) {
            currentSession.save(caarraySource);
        }

        final TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology =
                CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                        ExampleSearchCriteria.forEntity(mgedOntology).includeNulls().excludeProperties("url"),
                        Order.desc("version")));
        if (savedMgedOntology == null) {
            currentSession.save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            final Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            currentSession.save(unknownProtocolType);
        }

        if (vocabularyDao.getTerm(savedMgedOntology, "mm") == null) {
            final Term mm = new Term();
            mm.setValue("mm");
            mm.setSource(savedMgedOntology);
            currentSession.save(mm);
        }

        currentSession.save(this.testOrganization);
        currentSession.save(this.testOrganism);
        currentSession.save(this.testTerm);
        currentSession.save(this.testProject);

        tx.commit();
    }

    private void reloadSupportingObjects() {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        this.testOrganism =
                (Organism) this.hibernateHelper.getCurrentSession().load(Organism.class, getTestOrganism().getId());
        this.testOrganization =
                (Organization) this.hibernateHelper.getCurrentSession().load(Organization.class,
                        this.testOrganization.getId());
        this.testProject =
                (Project) this.hibernateHelper.getCurrentSession().load(Project.class, this.testProject.getId());
        this.testExperiment =
                (Experiment) this.hibernateHelper.getCurrentSession().load(Experiment.class,
                        this.testExperiment.getId());
        this.testTermSource =
                (TermSource) this.hibernateHelper.getCurrentSession().load(TermSource.class,
                        this.testTermSource.getId());
        this.testCategory =
                (Category) this.hibernateHelper.getCurrentSession().load(Category.class, this.testCategory.getId());
        this.testTerm = (Term) this.hibernateHelper.getCurrentSession().load(Term.class, this.testTerm.getId());
        tx.commit();
    }

    protected void importDesignAndDataFilesIntoProject(File arrayDesignFile, FileType dataFilesType, File... dataFiles)
            throws Exception {
        final ArrayDesign design = importArrayDesign(arrayDesignFile);
        importDataFilesIntoProject(design, dataFilesType, dataFiles);
    }

    protected void importDataFilesIntoProject(ArrayDesign design, FileType dataFilesType, File... dataFiles)
            throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();
        this.testExperiment =
                (Experiment) this.hibernateHelper.getCurrentSession().load(Experiment.class,
                        this.testExperiment.getId());
        this.testProject =
                (Project) this.hibernateHelper.getCurrentSession().load(Project.class, this.testProject.getId());
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        this.testExperiment.getArrayDesigns().add(design);
        this.hibernateHelper.getCurrentSession().save(this.testProject);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        Project project =
                (Project) this.hibernateHelper.getCurrentSession().load(Project.class, getTestProject().getId());
        final CaArrayFileSet fileSet = uploadFiles(project, dataFiles);
        for (final CaArrayFile file : fileSet.getFiles()) {
            if (!file.getName().endsWith(".sdrf") && !file.getName().endsWith(".idf")) {
                file.setFileType(dataFilesType);
            }
        }
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        tx.commit();
    }

    protected void importFiles(Project project, MageTabFileSet fileSet, FileType dataMatrixFileType) throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        final CaArrayFileSet caarrayFileSet = uploadFiles(project, fileSet, dataMatrixFileType);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, caarrayFileSet, null);
        this.hibernateHelper.getCurrentSession().getTransaction().commit();
    }

    protected void uploadAndValidateFiles(Map<File, FileType> files) throws Exception {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        helpValidateFiles(project, fileSet);
    }

    private void helpValidateFiles(Project project, CaArrayFileSet fileSet) throws Exception {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        project = (Project) this.hibernateHelper.getCurrentSession().load(Project.class, project.getId());
        validateFiles(project, fileSet);
        tx.commit();
    }

    /**
     * "Upload" files to a project, returning the CaArrayFileSet containing those files.
     * 
     * @param project project to upload to
     * @param fileSet MageTabFileSet containing the files to upload (should correspond to the files in the file set)
     * @return
     */
    protected CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet) {
        return uploadFiles(project, fileSet, UnparsedDataHandler.FILE_TYPE_MAGE_TAB_DATA_MATRIX);
    }

    protected CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet, FileType dataMatrixFileType) {
        final CaArrayFileSet fset = new CaArrayFileSet(project);
        for (final FileRef file : fileSet.getAllFiles()) {
            final CaArrayFile caArrayFile = this.fileAccessService.add(file.getAsFile());
            if (TestMageTabSets.isDataMatrix(file, fileSet)) {
                caArrayFile.setFileType(dataMatrixFileType);
            }
            caArrayFile.setProject(project);
            project.getFiles().add(caArrayFile);
            this.hibernateHelper.getCurrentSession().save(caArrayFile);
            fset.add(caArrayFile);
        }
        this.hibernateHelper.getCurrentSession().update(project);
        return fset;
    }

    private CaArrayFileSet uploadFiles(Project project, File... files) {
        final MageTabFileSet inputFiles = new MageTabFileSet();
        for (final File f : files) {
            inputFiles.addNativeData(new JavaIOFileRef(f));
        }
        return uploadFiles(project, inputFiles);
    }

    protected CaArrayFileSet uploadFiles(Project project, Map<File, FileType> files) {
        for (final File file : files.keySet()) {
            final CaArrayFile caArrayFile = this.getFileAccessService().add(file);
            caArrayFile.setProject(project);
            caArrayFile.setFileType(files.get(file));
            project.getFiles().add(caArrayFile);
            this.hibernateHelper.getCurrentSession().save(caArrayFile);
        }

        this.hibernateHelper.getCurrentSession().update(project);
        return project.getFileSet();
    }

    protected Source findSource(Project project, String name) {
        return CaArrayDaoFactory.INSTANCE.getProjectDao().getSourceForExperiment(project.getExperiment(), name);
    }

    protected void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions)
            throws Exception {
        final ProjectFilesImportJob job =
                this.jobFactory.createProjectFilesImportJob(CaArrayUsernameHolder.getUser(), targetProject, fileSet,
                        dataImportOptions);
        try {
            job.execute();
        } catch (final Exception e) {
            job.getUnexpectedErrorPreparedStatement(this.hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private void validateFiles(Project targetProject, CaArrayFileSet fileSet) throws Exception {
        final ProjectFilesValidationJob job =
                this.jobFactory
                        .createProjectFilesValidationJob(CaArrayUsernameHolder.getUser(), targetProject, fileSet);
        try {
            job.execute();
        } catch (final Exception e) {
            job.getUnexpectedErrorPreparedStatement(this.hibernateHelper.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    protected ArrayDesign importArrayDesign(File designFile) throws IllegalAccessException, InvalidDataFileException {
        return importArrayDesign(designFile, null);
    }

    protected ArrayDesign importArrayDesign(File designFile, FileType fileType) throws IllegalAccessException,
            InvalidDataFileException {
        return importArrayDesign(designFile, fileType, "authority:namespace:");
    }

    protected ArrayDesign importArrayDesign(File designFile, FileType fileType, String namespace)
            throws IllegalAccessException, InvalidDataFileException {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ArrayDesign design = doImportDesign(designFile, fileType, namespace);
        tx.commit();
        return design;
    }

    private ArrayDesign doImportDesign(File designFile, FileType fileType, String namespace) {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setGeoAccession("GPL0000");
        design.setProvider(getTestOrganization());
        design.setLsidForEntity(namespace + designFile.getName());
        final CaArrayFile caArrayDesignFile = this.getFileAccessService().add(designFile);
        if (fileType != null) {
            caArrayDesignFile.setFileType(fileType);
        }
        design.addDesignFile(caArrayDesignFile);
        design.setTechnologyType(getTestTerm());
        design.setOrganism(getTestOrganism());

        final AssayType at1 = new AssayType("Gene Expression");
        design.getAssayTypes().add(at1);

        this.hibernateHelper.getCurrentSession().save(at1);
        this.hibernateHelper.getCurrentSession().save(design);

        final ArrayDesignService arrayDesignService = ServiceLocatorFactory.getArrayDesignService();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        design = (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        return design;
    }

    @Override
    protected Injector createInjector() {
        final Module overrideModule = Modules.override(InjectorFactory.getModule()).with(new AbstractModule() {

            @Override
            protected void configure() {
                install(new TranslationModule());
                bind(MageTabImporter.class).to(MageTabImporterImpl.class);
                bind(ArrayDataImporter.class).to(ArrayDataImporterImpl.class);
                bind(JobFactory.class).to(JobFactoryImpl.class);

                final DirectJobSubmitter submitter = createJobSubmitter();
                bind(FileManagementJobSubmitter.class).toInstance(submitter);
            }

            private DirectJobSubmitter createJobSubmitter() {
                final JobQueue jobQueue = new JobDaoSingleJobStub();
                final Provider<UsernameHolder> usernameHolderProvider = Providers.of(mock(UsernameHolder.class));
                final FileManagementMDB mdb = new FileManagementMDB(hibernateHelper, jobQueue, usernameHolderProvider );
                final UserTransaction ut = mock(UserTransaction.class);
                mdb.setTransaction(ut);
                final DirectJobSubmitter submitter = new DirectJobSubmitter(mdb, jobQueue);
                return submitter;
            }
        });

        return Guice.createInjector(overrideModule);
    }

    @BeforeClass
    public static void registerFileAccessServiceStub() {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, new FileAccessServiceStub());
        InjectorFactory.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final MapBinder<String, DataStorage> mapBinder =
                        MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME)
                        .toProvider(
                                ServiceLocatorFactory.serviceProvider(FileAccessServiceStub.class,
                                        FileAccessService.JNDI_NAME));
            }
        });
    }

    private FileManagementService createFileManagementService() {
        this.fileAccessService = this.injector.getInstance(FileAccessServiceStub.class);
        final ServiceLocatorStub locatorStub = (ServiceLocatorStub) ServiceLocatorFactory.getLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessService);

        final MageTabTranslator mtt = new MageTabTranslatorBean();
        this.injector.injectMembers(mtt);
        locatorStub.addLookup(MageTabTranslator.JNDI_NAME, mtt);

        final ArrayDesignService ads = new ArrayDesignServiceBean();
        this.injector.injectMembers(ads);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, ads);

        final ArrayDataService datas = new ArrayDataServiceBean();
        this.injector.injectMembers(datas);
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, datas);

        final VocabularyService vs = new VocabularyServiceBean();
        this.injector.injectMembers(vs);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vs);

        final FileManagementServiceBean bean = new FileManagementServiceBean();
        this.injector.injectMembers(bean);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, bean);

        return bean;
    }

    protected void importFiles(MageTabFileSet fileSet) throws Exception {
        importFiles(this.testProject, fileSet, UnparsedDataHandler.FILE_TYPE_MAGE_TAB_DATA_MATRIX);
    }

    protected void importFiles(MageTabFileSet fileSet, FileType dataMatrixFileType) throws Exception {
        importFiles(this.testProject, fileSet, dataMatrixFileType);
    }

    protected void addDesignToExperiment(ArrayDesign design) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ArrayDesign sessionArrayDesign =
                (ArrayDesign) this.hibernateHelper.getCurrentSession().load(ArrayDesign.class, design.getId());
        getTestExperiment().getArrayDesigns().add(sessionArrayDesign);
        this.hibernateHelper.getCurrentSession().save(getTestProject());
        tx.commit();
    }

    protected CaArrayFileSet uploadFiles(Map<File, FileType> files) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();
        return fileSet;
    }

    protected CaArrayFileSet uploadFiles(MageTabFileSet mageTabFileSet) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final CaArrayFileSet fileSet = uploadFiles(project, mageTabFileSet);
        tx.commit();
        return fileSet;
    }

    protected void importFiles(CaArrayFileSet fileSet) throws Exception {
        importFiles(fileSet, DataImportOptions.getAutoCreatePerFileOptions());
    }

    protected void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions) throws Exception {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        importFiles(getTestProject(), fileSet, dataImportOptions);
        tx.commit();
    }
}
