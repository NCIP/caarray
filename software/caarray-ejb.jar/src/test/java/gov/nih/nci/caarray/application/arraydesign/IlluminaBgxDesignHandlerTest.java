
package gov.nih.nci.caarray.application.arraydesign;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
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
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class IlluminaBgxDesignHandlerTest extends AbstractServiceTest {

    private ArrayDesignService arrayDesignService;
    private final ArrayDesignServiceTest.LocalDaoFactoryStub caArrayDaoFactoryStub = new ArrayDesignServiceTest.LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    private ArrayDesign arrayDesign;
    private Transaction transaction;
    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");
    private static TreeSet<AssayType> assayTypes = new TreeSet<AssayType>();

    @BeforeClass
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
        Transaction transaction = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().save(DUMMY_ASSAY_TYPE);
        transaction.commit();
    }

    @Before
    public void setUp() {
        HibernateUtil.setFiltersEnabled(false);
        arrayDesign = new ArrayDesign();
        arrayDesign.setName("foo"+System.identityHashCode(arrayDesign));
        arrayDesign.setVersion("ver");
        arrayDesign.setTechnologyType(DUMMY_TERM);
        arrayDesign.setOrganism(DUMMY_ORGANISM);
        arrayDesign.setProvider(DUMMY_ORGANIZATION);
        arrayDesign.setAssayTypes(assayTypes);
        arrayDesign.setTechnologyType(DUMMY_TERM);
        this.arrayDesignService = ArrayDesignServiceTest.createArrayDesignService(this.caArrayDaoFactoryStub, this.fileAccessServiceStub, this.vocabularyServiceStub);
    }

    @After
    public void clean() {
        caArrayDaoFactoryStub.clear();
    }

    private CaArrayFile getFile(File f) {
        return fileAccessServiceStub.add(f);
    }

    private IlluminaBgxDesignHandler getHandler(File f) {
        return new IlluminaBgxDesignHandler(vocabularyServiceStub, caArrayDaoFactoryStub, getFile(f));
    }

    @Test
    public void testImportDesignDetails_IlluminaHUMANWG_6_V2_0_R3_11223189_A_BGX() {
        this.transaction = HibernateUtil.beginTransaction();

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
    public void testLoad() {
        this.transaction = HibernateUtil.beginTransaction();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        IlluminaBgxDesignHandler instance = getHandler(f);
        instance.load(arrayDesign);
        assertEquals("HumanWG-6_V2_0_R3_11223189_A", arrayDesign.getName());
        assertEquals("URN:LSID:illumina.com:PhysicalArrayDesign:HumanWG-6_V2_0_R3_11223189_A", arrayDesign.getLsid());
        assertEquals(new Integer(48701 + 1426), arrayDesign.getNumberOfFeatures());
        this.transaction.rollback();
    }

    @Test
    public void testCreateDesignDetails() {
        this.transaction = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().save(arrayDesign);
        HibernateUtil.getCurrentSession().flush();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        IlluminaBgxDesignHandler instance = getHandler(f);
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
    public void testValidate() {
        this.transaction = HibernateUtil.beginTransaction();
        File f = IlluminaArrayDesignFiles.HUMANWG_6_V2_0_R3_11223189_A_BGX;
        IlluminaBgxDesignHandler instance = getHandler(f);
        ValidationResult result = instance.validate();

        for (ValidationMessage m : result.getMessages()) {
            System.out.println(m.getMessage());
        }
        assertTrue(result.isValid());
        assertTrue(result.getMessages().isEmpty());
        transaction.rollback();
    }

}