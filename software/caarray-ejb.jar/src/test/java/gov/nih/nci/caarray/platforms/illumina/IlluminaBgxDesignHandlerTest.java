
package gov.nih.nci.caarray.platforms.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignModule;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.JobQueueDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
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
import gov.nih.nci.caarray.platforms.LocalSessionTransactionManager;
import gov.nih.nci.caarray.platforms.MockFileManager;
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
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 *
 * @author gax
 */
public class IlluminaBgxDesignHandlerTest extends AbstractServiceTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper; 

    private ArrayDesignService arrayDesignService;
    private final static ArrayDesignServiceTest.LocalDaoFactoryStub caArrayDaoFactoryStub = new ArrayDesignServiceTest.LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private ArrayDesign arrayDesign;
    private Transaction transaction;
    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");
    private static TreeSet<AssayType> assayTypes = new TreeSet<AssayType>();
    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies. 
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        
        createDummies();
    }
    
    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        final Module testArrayDesignModule = Modules.override(new ArrayDesignModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(UsernameHolder.class).toInstance(mock(UsernameHolder.class));
                bind(JobQueueDao.class).toInstance(mock(JobQueueDao.class));
                bind(ContactDao.class).toInstance(caArrayDaoFactoryStub.getContactDao());
                bind(SearchDao.class).toInstance(caArrayDaoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(caArrayDaoFactoryStub.getArrayDao());
                bind(VocabularyDao.class).toInstance(caArrayDaoFactoryStub.getVocabularyDao());
                
                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
            }
        });
        return Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                testArrayDesignModule);
    }
    
     public static void createDummies() {
        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANISM.setScientificName("Homo sapiens");
        TermSource ts = new TermSource();
        ts.setName("TS 1");
        Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        DUMMY_ORGANISM.setTermSource(ts);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(cat);
        DUMMY_TERM.setSource(ts);
        assayTypes.add(DUMMY_ASSAY_TYPE);
        Transaction transaction = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);
        transaction.commit();
    }

    @Before
    public void setUp() {
        hibernateHelper.setFiltersEnabled(false);
        arrayDesign = new ArrayDesign();
        arrayDesign.setName("foo"+System.identityHashCode(arrayDesign));
        arrayDesign.setVersion("ver");
        arrayDesign.setTechnologyType(DUMMY_TERM);
        arrayDesign.setOrganism(DUMMY_ORGANISM);
        arrayDesign.setProvider(DUMMY_ORGANIZATION);
        arrayDesign.setAssayTypes(assayTypes);
        arrayDesign.setTechnologyType(DUMMY_TERM);
        this.arrayDesignService = createArrayDesignService(injector);
    }
    
    public ArrayDesignService createArrayDesignService(final Injector injector) {       
        final ArrayDesignServiceBean bean = (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        return bean;
    }

    @After
    public void clean() {
        caArrayDaoFactoryStub.clear();
    }

    private CaArrayFile getFile(File f) {
        return fileAccessServiceStub.add(f);
    }

    private BgxDesignHandler getHandler(File f) throws PlatformFileReadException {
        BgxDesignHandler h = new BgxDesignHandler(new LocalSessionTransactionManager(),
                new MockFileManager(fileAccessServiceStub), caArrayDaoFactoryStub.getArrayDao(), caArrayDaoFactoryStub
                        .getSearchDao());
        h.openFiles(Collections.singleton(getFile(f)));        
        return h;
    }

    @Test
    public void testImportDesignDetails_IlluminaHUMANWG_6_V2_0_R3_11223189_A_BGX() {
        this.transaction = hibernateHelper.beginTransaction();

        CaArrayFile designFile = getFile(IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX);
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        arrayDesignService.importDesignDetails(arrayDesign);
        assertTrue(48701 + 1426 == arrayDesign.getDesignDetails().getProbes().size());
        int mainCount = 0, contolCount = 0;
        for (PhysicalProbe probe : arrayDesign.getDesignDetails().getProbes()) {
            assertNotNull(probe);
            assertNotNull(probe.getName());
            assertNotNull(probe.getAnnotation());
            ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
            Gene gene = annotation.getGene();
            assertNotNull(gene);
            assertEquals(arrayDesign.getDesignDetails(), probe.getArrayDesignDetails());
            if (probe.getProbeGroup() != null) {
                assertNotNull("null probe group", probe.getProbeGroup());
            if (probe.getProbeGroup().getName().equals("Control")) contolCount ++;
            else if (probe.getProbeGroup().getName().equals("Main")) mainCount ++;
            else fail("unexpected group name "+probe.getProbeGroup().getName());
            }
        }
        assertTrue(48701 == mainCount);
        assertTrue(1426 == contolCount);
        
        transaction.rollback();
    }
    
    @Test
    public void testLoad() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        BgxDesignHandler instance = getHandler(f);
        instance.load(arrayDesign);
        assertEquals("HumanWG-6_V2_0_R3_11223189_A", arrayDesign.getName());
        assertEquals("URN:LSID:illumina.com:PhysicalArrayDesign:HumanWG-6_V2_0_R3_11223189_A", arrayDesign.getLsid());
        assertEquals(new Integer(48701 + 1426), arrayDesign.getNumberOfFeatures());
        this.transaction.rollback();
    }

    @Test
    public void testCreateDesignDetails() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().save(arrayDesign);
        hibernateHelper.getCurrentSession().flush();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        BgxDesignHandler instance = getHandler(f);
        instance.createDesignDetails(arrayDesign);
        ArrayDesignDetails details = arrayDesign.getDesignDetails();
        assertNotNull(details);
        Set<PhysicalProbe> probes = details.getProbes();
        int mainCount = 0, contolCount = 0;
        for (PhysicalProbe p : probes) {
            assertNotNull("null probe group", p.getProbeGroup());
            if (p.getProbeGroup().getName().equals("Control")) contolCount ++;
            else if (p.getProbeGroup().getName().equals("Main")) mainCount ++;
            else fail("unexpected group name "+p.getProbeGroup().getName());
        }
        assertTrue(48701 == mainCount);
        assertTrue(1426 == contolCount);
        transaction.rollback();
    }

    @Test
    public void testValidate() throws PlatformFileReadException {
        this.transaction = hibernateHelper.beginTransaction();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        BgxDesignHandler instance = getHandler(f);
        ValidationResult result = new ValidationResult();
        instance.validate(result);

        for (ValidationMessage m : result.getMessages()) {
            System.out.println(m.getMessage());
        }
        assertTrue(result.isValid());
        assertTrue(result.getMessages().isEmpty());
        transaction.rollback();
    }

}