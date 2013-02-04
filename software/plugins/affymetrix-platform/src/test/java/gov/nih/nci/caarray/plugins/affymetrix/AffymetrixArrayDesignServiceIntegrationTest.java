//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.platforms.LocalSessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;

/**
 * Integration Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class AffymetrixArrayDesignServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private ArrayDesignService arrayDesignService;
    private FileAccessServiceStub fileAccessServiceStub;
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static ArrayDesign DUMMY_ARRAY_DESIGN = new ArrayDesign();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");

    private static FileType TEST_DESIGN_TYPE = new FileType("TEST_DESIGN_TYPE", FileCategory.ARRAY_DESIGN, true);
    private static FileType TEST_NONDESIGN_TYPE = new FileType("TEST_NONDESIGN_TYPE", FileCategory.RAW_DATA, true);
    private static LSID TEST_LSID = new LSID("TestAuthority", "TestNamespace", "TestId");
    private static String TEST_DESIGN_NAME = "TestName";

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() {
        this.fileAccessServiceStub = this.injector.getInstance(FileAccessServiceStub.class);

        this.arrayDesignService = createArrayDesignService(this.fileAccessServiceStub, this.vocabularyServiceStub);

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

        DUMMY_ARRAY_DESIGN.setId(2L);
        DUMMY_ARRAY_DESIGN.setName("DummyTestArrayDesign1");
        DUMMY_ARRAY_DESIGN.setVersion("2.0");
        DUMMY_ARRAY_DESIGN.setGeoAccession("GPL0000");
        DUMMY_ARRAY_DESIGN.setProvider(DUMMY_ORGANIZATION);
        DUMMY_ARRAY_DESIGN.setTechnologyType(DUMMY_TERM);
        DUMMY_ARRAY_DESIGN.setOrganism(DUMMY_ORGANISM);
        DUMMY_ARRAY_DESIGN.addDesignFile(getCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        DUMMY_ARRAY_DESIGN.getDesignFileSet().updateStatus(null);
    }

    private ArrayDesignService createArrayDesignService(final FileAccessServiceStub fileAccessServiceStub,
            VocabularyServiceStub vocabularyServiceStub) {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);

        final ArrayDesignServiceBean bean = new ArrayDesignServiceBean();
        this.injector.injectMembers(bean);

        return bean;
    }

    @BeforeClass
    public static void registerAffymetrix() {
        InjectorFactory.setPlatformTransactionModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SessionTransactionManager.class).to(LocalSessionTransactionManager.class);
            }
        });
        InjectorFactory.addPlatform(new AffymetrixModule());
        InjectorFactory.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final MapBinder<String, DataStorage> mapBinder = MapBinder.newMapBinder(binder(), String.class,
                        DataStorage.class);
                bind(FileAccessServiceStub.class).in(Scopes.SINGLETON);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).to(FileAccessServiceStub.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected Injector createInjector() {
        return InjectorFactory.getInjector();
    }

    private ArrayDesign setupAndSaveDesign(File... designFiles) throws IllegalAccessException, InvalidDataFileException {
        this.hibernateHelper.getCurrentSession().save(DUMMY_ORGANIZATION);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ORGANISM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_TERM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);

        final ArrayDesign design = new ArrayDesign();
        design.setName("DummyTestArrayDesign1");
        design.setVersion("2.0");
        design.setProvider(DUMMY_ORGANIZATION);
        design.setLsidForEntity("authority:namespace:" + designFiles[0].getName());
        design.getAssayTypes().add(DUMMY_ASSAY_TYPE);
        final Set<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        for (final File designFile : designFiles) {
            design.addDesignFile(getCaArrayFile(designFile));
        }
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        try {
            this.arrayDesignService.saveArrayDesign(design);
        } catch (final InvalidStateException e) {
            e.printStackTrace();
            for (final InvalidValue iv : e.getInvalidValues()) {
                System.out.println("Invalid value: " + iv);
            }
        }
        return design;
    }

    @Test
    public void testImportDesignDetails_AffymetrixTest3() throws Exception {
        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertNotNull(designFile.getFileType());
            }
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertEquals("", designFile.getValidationResult().toString());
            }

            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals("Test3", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("Test3", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            assertEquals(15876, design.getNumberOfFeatures().intValue());

            // now try to re-import the design over itself
            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertEquals("", designFile.getValidationResult().toString());
            }
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals("Test3", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("Test3", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            assertEquals(15876, design.getNumberOfFeatures().intValue());
        } catch (final Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    private CaArrayFile getCaArrayFile(File file) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        return caArrayFile;
    }

    @Test
    public void testImportDesignDetails_AffymetrixHuEx() throws Exception {
        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(AffymetrixArrayDesignFiles.HUEX_TEST_CLF,
                    AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            this.hibernateHelper.getCurrentSession().getTransaction().commit();

            assertEquals("HuEx-1_0-st-v1-test", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("HuEx-1_0-st-v1-test", design.getLsidObjectId());

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(1024, design.getNumberOfFeatures().intValue());
            assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
            assertEquals(364, design.getDesignDetails().getProbes().size());
            assertEquals(1024, design.getDesignDetails().getFeatures().size());
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            final ArrayDesign otherDesign = this.arrayDesignService.getArrayDesign("Affymetrix.com",
                    "PhysicalArrayDesign", "HuEx-1_0-st-ta1-test");
            assertEquals("HuEx-1_0-st-ta1-test", otherDesign.getName());
            assertEquals("Affymetrix.com", otherDesign.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", otherDesign.getLsidNamespace());
            assertEquals("HuEx-1_0-st-ta1-test", otherDesign.getLsidObjectId());
            assertEquals(1024, otherDesign.getNumberOfFeatures().intValue());
            assertEquals(94, otherDesign.getDesignDetails().getLogicalProbes().size());
            assertEquals(364, otherDesign.getDesignDetails().getProbes().size());
            assertEquals(1024, otherDesign.getDesignDetails().getFeatures().size());
            assertEquals(2, otherDesign.getDesignFiles().size());
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertTrue(otherDesign.getDesignFiles().contains(designFile));
            }
            t.commit();

        } catch (final Exception e) {
            e.printStackTrace();
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }
}
