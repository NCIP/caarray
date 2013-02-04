//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
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
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

/**
 * Integration Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private ArrayDesignService arrayDesignService;
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
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
        DUMMY_ARRAY_DESIGN.addDesignFile(getCaArrayFile(new File("design.txt")));
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

    @Override
    protected Injector createInjector() {
        InjectorFactory.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final Multibinder<DesignFileHandler> designFileBinder = Multibinder.newSetBinder(binder(),
                        DesignFileHandler.class);
                designFileBinder.addBinding().to(TestDesignHandler.class);

            }
        });
        return InjectorFactory.getInjector();
    }
    
    
    /**
     * @{inheritDoc}  
     */
    @Override
    protected void levelsetInjector() {
        InjectorFactory.resetInjector();
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
    public void testDeleteArrayDesign() throws Exception {
        Transaction t = this.hibernateHelper.beginTransaction();
        ArrayDesign design = setupAndSaveDesign(new File("design.txt"));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        final Long id = design.getId();
        t.commit();

        t = this.hibernateHelper.beginTransaction();
        final int size = this.hibernateHelper.getCurrentSession().createCriteria(ArrayDesign.class).list().size();
        design = this.arrayDesignService.getArrayDesign(id);
        final Long detailsId = design.getDesignDetails().getId();
        this.arrayDesignService.deleteArrayDesign(design);
        t.commit();

        t = this.hibernateHelper.beginTransaction();
        assertEquals(size - 1, this.hibernateHelper.getCurrentSession().createCriteria(ArrayDesign.class).list().size());
        final ArrayDesignDetails details = (ArrayDesignDetails) this.hibernateHelper.getCurrentSession().get(
                ArrayDesignDetails.class, detailsId);
        assertNull(details);
        assertTrue(this.hibernateHelper.getCurrentSession().createCriteria(CaArrayFile.class).list().isEmpty());
        t.commit();
    }

    @Test(expected = ArrayDesignDeleteException.class)
    public void testDeleteArrayDesignLocked() throws ArrayDesignDeleteException {
        final ArrayDesignService arrayDesignService = new ArrayDesignServiceLocal();
        final ArrayDesign design = DUMMY_ARRAY_DESIGN;
        arrayDesignService.deleteArrayDesign(design);
    }

    @Test(expected = ArrayDesignDeleteException.class)
    public void testDeleteArrayDesignImporting() throws ArrayDesignDeleteException {
        final ArrayDesignService arrayDesignService = new ArrayDesignServiceLocal();
        DUMMY_ARRAY_DESIGN.getFirstDesignFile().setFileStatus(FileStatus.IMPORTING);
        arrayDesignService.deleteArrayDesign(DUMMY_ARRAY_DESIGN);
    }

    private class ArrayDesignServiceLocal extends ArrayDesignServiceBean {
        @Override
        public boolean isArrayDesignLocked(Long id) {
            return true;
        }
    }

    @Test
    public void testImportDesignDetails() throws Exception {
        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(new File("design.txt"));
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(TEST_DESIGN_NAME, design.getName());
            assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
            assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
            assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());

            assertEquals(10, design.getNumberOfFeatures().intValue());
            assertEquals(10, design.getDesignDetails().getProbes().size());
            t.commit();

            // now try to re-import the design over itself
            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(TEST_DESIGN_NAME, design.getName());
            assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
            assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
            assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            assertEquals(10, design.getNumberOfFeatures().intValue());
            assertEquals(10, design.getDesignDetails().getProbes().size());
            t.commit();

        } catch (final Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    private CaArrayFile getCaArrayFile(File file) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setFileType(TEST_DESIGN_TYPE);
        return caArrayFile;
    }

    private static class TestDesignHandler implements DesignFileHandler {
        private CaArrayFile designFile;
        private final ArrayDao arrayDao;

        @Inject
        public TestDesignHandler(ArrayDao arrayDao) {
            this.arrayDao = arrayDao;
        }

        @Override
        public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
            if (designFiles.size() == 1 && designFiles.iterator().next().getFileType().equals(TEST_DESIGN_TYPE)) {
                this.designFile = designFiles.iterator().next();
                return true;
            }
            return false;
        }

        @Override
        public void closeFiles() {
            this.designFile = null;
        }

        @Override
        public boolean parsesData() {
             return true;
        }

        @Override
        public void load(ArrayDesign arrayDesign) throws PlatformFileReadException {
            arrayDesign.setName(TEST_DESIGN_NAME);
            arrayDesign.setLsid(TEST_LSID);
        }

        @Override
        public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
            final ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setNumberOfFeatures(10);
            arrayDesign.setDesignDetails(details);

            for (int i = 0; i < 10; i++) {
                final PhysicalProbe p = new PhysicalProbe(details, null);
                p.setName("Probe" + i);
                details.getProbes().add(p);
            }
            this.arrayDao.save(arrayDesign);
            this.arrayDao.save(details);
        }

        @Override
        public void validate(ValidationResult result) throws PlatformFileReadException {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);
        }

        @Override
        public Set<FileType> getSupportedTypes() {
            return Collections.singleton(TEST_DESIGN_TYPE);
        }

    }
}
