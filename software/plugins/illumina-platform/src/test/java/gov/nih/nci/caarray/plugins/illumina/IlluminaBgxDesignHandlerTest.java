//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * 
 * @author gax
 */
public class IlluminaBgxDesignHandlerTest extends AbstractServiceTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    private ArrayDesignService arrayDesignService;
    private final static ArrayDesignServiceTest.LocalDaoFactoryStub caArrayDaoFactoryStub =
            new ArrayDesignServiceTest.LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private ArrayDesign arrayDesign;
    private Transaction transaction;
    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        final Module testArrayDesignModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(UsernameHolder.class).toInstance(mock(UsernameHolder.class));
                bind(JobQueue.class).toInstance(mock(JobQueue.class));
                bind(ContactDao.class).toInstance(caArrayDaoFactoryStub.getContactDao());
                bind(SearchDao.class).toInstance(caArrayDaoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(caArrayDaoFactoryStub.getArrayDao());
                bind(VocabularyDao.class).toInstance(caArrayDaoFactoryStub.getVocabularyDao());
                bind(MultipartBlobDao.class).toInstance(mock(MultipartBlobDao.class));

                final MapBinder<String, DataStorage> mapBinder =
                        MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).toInstance(
                        IlluminaBgxDesignHandlerTest.this.fileAccessServiceStub);

                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
                bind(SessionTransactionManager.class).to(SessionTransactionManagerNoOpImpl.class);

                bind(String.class).annotatedWith(Names.named(FileSystemStorageModule.BASE_DIR_KEY)).toInstance(
                        System.getProperty("java.io.tmpdir"));
                bind(String.class).annotatedWith(Names.named(DataStorageModule.FILE_DATA_ENGINE)).toInstance(
                        "file-system");
                bind(String.class).annotatedWith(Names.named(DataStorageModule.PARSED_DATA_ENGINE)).toInstance(
                        "file-system");
            }
        };
        final PlatformModule platformModule = new PlatformModule();
        platformModule.addPlatform(new IlluminaModule());

        return Guice.createInjector(new CaArrayHibernateHelperModule(), new CaArrayEjbStaticInjectionModule(),
                new DataStorageModule(), platformModule, testArrayDesignModule);
    }

    public static void createDummies() {
        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANISM.setScientificName("Homo sapiens");
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        DUMMY_ORGANISM.setTermSource(ts);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(cat);
        DUMMY_TERM.setSource(ts);
        final Transaction transaction = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);
        transaction.commit();
    }

    @Before
    public void setUp() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);

        createDummies();

        hibernateHelper.setFiltersEnabled(false);
        this.arrayDesign = new ArrayDesign();
        this.arrayDesign.setName("foo" + System.identityHashCode(this.arrayDesign));
        this.arrayDesign.setVersion("ver");
        this.arrayDesign.setTechnologyType(DUMMY_TERM);
        this.arrayDesign.setOrganism(DUMMY_ORGANISM);
        this.arrayDesign.setProvider(DUMMY_ORGANIZATION);
        this.arrayDesign.getAssayTypes().add(DUMMY_ASSAY_TYPE);
        this.arrayDesign.setTechnologyType(DUMMY_TERM);
        this.arrayDesignService = createArrayDesignService(injector);
    }

    public ArrayDesignService createArrayDesignService(final Injector injector) {
        final ArrayDesignServiceBean bean = (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        return bean;
    }

    @After
    public void clean() {
        caArrayDaoFactoryStub.clear();
    }

    private CaArrayFile getFile(File f) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(f);
        caArrayFile.setFileType(BgxDesignHandler.BGX_FILE_TYPE);
        return caArrayFile;
    }

    private BgxDesignHandler getHandler(File f) throws PlatformFileReadException {
        final DataStorageFacade dataStorageFacade = injector.getInstance(DataStorageFacade.class);
        final BgxDesignHandler h =
                new BgxDesignHandler(new SessionTransactionManagerNoOpImpl(), dataStorageFacade,
                        caArrayDaoFactoryStub.getArrayDao(), caArrayDaoFactoryStub.getSearchDao());
        final boolean opened = h.openFiles(Collections.singleton(getFile(f)));
        assertTrue(opened);
        return h;
    }

    @Test
    public void testImportDesignDetails_IlluminaHUMANWG_6_V2_0_R3_11223189_A_BGX() {
        this.transaction = hibernateHelper.beginTransaction();

        final CaArrayFile designFile = getFile(IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX);
        this.arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(this.arrayDesign);
        this.arrayDesignService.importDesignDetails(this.arrayDesign);
        assertTrue(48701 + 1426 == this.arrayDesign.getDesignDetails().getProbes().size());
        int mainCount = 0, contolCount = 0;
        for (final PhysicalProbe probe : this.arrayDesign.getDesignDetails().getProbes()) {
            assertNotNull(probe);
            assertNotNull(probe.getName());
            assertNotNull(probe.getAnnotation());
            final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
            final Gene gene = annotation.getGene();
            assertNotNull(gene);
            assertEquals(this.arrayDesign.getDesignDetails(), probe.getArrayDesignDetails());
            if (probe.getProbeGroup() != null) {
                assertNotNull("null probe group", probe.getProbeGroup());
                if (probe.getProbeGroup().getName().equals("Control")) {
                    contolCount++;
                } else if (probe.getProbeGroup().getName().equals("Main")) {
                    mainCount++;
                } else {
                    fail("unexpected group name " + probe.getProbeGroup().getName());
                }
            }
        }
        assertTrue(48701 == mainCount);
        assertTrue(1426 == contolCount);

        this.transaction.rollback();
    }

    @Test
    public void testLoad() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        final File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        final BgxDesignHandler instance = getHandler(f);
        instance.load(this.arrayDesign);
        assertEquals("HumanWG-6_V2_0_R3_11223189_A", this.arrayDesign.getName());
        assertEquals("URN:LSID:illumina.com:PhysicalArrayDesign:HumanWG-6_V2_0_R3_11223189_A",
                this.arrayDesign.getLsid());
        assertEquals(new Integer(48701 + 1426), this.arrayDesign.getNumberOfFeatures());
        this.transaction.rollback();
    }

    @Test
    public void testCreateDesignDetails() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        final File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        final CaArrayFile caArrayDesignFile = this.fileAccessServiceStub.add(f);
        caArrayDesignFile.setFileType(BgxDesignHandler.BGX_FILE_TYPE);
        this.arrayDesign.addDesignFile(caArrayDesignFile);
        hibernateHelper.getCurrentSession().save(this.arrayDesign);
        hibernateHelper.getCurrentSession().flush();
        final BgxDesignHandler instance = getHandler(f);
        instance.createDesignDetails(this.arrayDesign);
        final ArrayDesignDetails details = this.arrayDesign.getDesignDetails();
        assertNotNull(details);
        final Set<PhysicalProbe> probes = details.getProbes();
        int mainCount = 0, contolCount = 0;
        for (final PhysicalProbe p : probes) {
            assertNotNull("null probe group", p.getProbeGroup());
            if (p.getProbeGroup().getName().equals("Control")) {
                contolCount++;
            } else if (p.getProbeGroup().getName().equals("Main")) {
                mainCount++;
            } else {
                fail("unexpected group name " + p.getProbeGroup().getName());
            }
        }
        assertTrue(48701 == mainCount);
        assertTrue(1426 == contolCount);
        this.transaction.rollback();
    }

    @Test
    public void testValidate() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        final File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        final BgxDesignHandler instance = getHandler(f);
        final ValidationResult result = new ValidationResult();
        instance.validate(result);

        for (final ValidationMessage m : result.getMessages()) {
            System.out.println(m.getMessage());
        }
        assertTrue(result.isValid());
        assertTrue(result.getMessages().isEmpty());
        this.transaction.rollback();
    }

}
