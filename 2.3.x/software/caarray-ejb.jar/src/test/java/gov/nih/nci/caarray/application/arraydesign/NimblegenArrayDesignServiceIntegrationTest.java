//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class NimblegenArrayDesignServiceIntegrationTest extends AbstractCaarrayTest {

    private ArrayDesignService arrayDesignService;
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static ArrayDesign DUMMY_ARRAY_DESIGN = new ArrayDesign();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() {
        this.arrayDesignService = createArrayDesignService(this.fileAccessServiceStub, this.vocabularyServiceStub);

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

        DUMMY_ARRAY_DESIGN.setId(2L);
        DUMMY_ARRAY_DESIGN.setName("DummyTestArrayDesign1");
        DUMMY_ARRAY_DESIGN.setVersion("2.0");
        DUMMY_ARRAY_DESIGN.setProvider(DUMMY_ORGANIZATION);
        DUMMY_ARRAY_DESIGN.setTechnologyType(DUMMY_TERM);
        DUMMY_ARRAY_DESIGN.setOrganism(DUMMY_ORGANISM);
        DUMMY_ARRAY_DESIGN.addDesignFile(getCaArrayFile(NimblegenArrayDesignFiles.EXPRESSION_DESIGN[0]));
        DUMMY_ARRAY_DESIGN.getDesignFileSet().updateStatus(null);
        HibernateUtil.enableFilters(false);
        HibernateUtil.openAndBindSession();
    }

    @After
    public void tearDown() {
        try {
            Transaction tx = HibernateUtil.getCurrentSession().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (HibernateException e) {
            // ok - there was no active transaction
        }
        HibernateUtil.unbindAndCleanupSession();
        HibernateIntegrationTestCleanUpUtility.cleanUp();
    }

    private static ArrayDesignService createArrayDesignService(final FileAccessServiceStub fileAccessServiceStub,
            VocabularyServiceStub vocabularyServiceStub) {
        ArrayDesignServiceBean bean = new ArrayDesignServiceBean();
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();

        return bean;
    }

    private ArrayDesign setupAndSaveDesign(File... designFiles) throws IllegalAccessException, InvalidDataFileException {
        HibernateUtil.getCurrentSession().save(DUMMY_ORGANIZATION);
        HibernateUtil.getCurrentSession().save(DUMMY_ORGANISM);
        HibernateUtil.getCurrentSession().save(DUMMY_TERM);

        ArrayDesign design = new ArrayDesign();
        design.setName("DummyTestArrayDesign1");
        design.setVersion("2.0");
        design.setProvider(DUMMY_ORGANIZATION);
        design.setLsidForEntity("authority:namespace:" + designFiles[0].getName());
        Set <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        for (File designFile : designFiles) {
            design.addDesignFile(getCaArrayFile(designFile));
        }
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        this.arrayDesignService.saveArrayDesign(design);
        return design;
    }

    @Test
    public void testDeleteArrayDesign() throws Exception {
        Transaction t = HibernateUtil.beginTransaction();
        ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN);
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        Long id = design.getId();
        t.commit();

        t = HibernateUtil.beginTransaction();
        int size = HibernateUtil.getCurrentSession().createCriteria(ArrayDesign.class).list().size();
        design = this.arrayDesignService.getArrayDesign(id);
        Long detailsId = design.getDesignDetails().getId();
        this.arrayDesignService.deleteArrayDesign(design);
        t.commit();

        t = HibernateUtil.beginTransaction();
        assertEquals(size - 1, HibernateUtil.getCurrentSession().createCriteria(ArrayDesign.class).list().size());
        ArrayDesignDetails details = (ArrayDesignDetails) HibernateUtil.getCurrentSession().get(
                ArrayDesignDetails.class, detailsId);
        assertNull(details);
        assertTrue(HibernateUtil.getCurrentSession().createCriteria(CaArrayFile.class).list().isEmpty());
        t.commit();
    }

    @Test
    public void testDeleteLargeArrayDesign() throws Exception {
        Transaction t = HibernateUtil.beginTransaction();
        ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.EXPRESSION_DESIGN[0]);
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        Long id = design.getId();
        t.commit();

        t = HibernateUtil.beginTransaction();
        int size = HibernateUtil.getCurrentSession().createCriteria(ArrayDesign.class).list().size();
        design = this.arrayDesignService.getArrayDesign(id);
        Long detailsId = design.getDesignDetails().getId();
        this.arrayDesignService.deleteArrayDesign(design);
        t.commit();

        t = HibernateUtil.beginTransaction();
        assertEquals(size - 1, HibernateUtil.getCurrentSession().createCriteria(ArrayDesign.class).list().size());
        ArrayDesignDetails details = (ArrayDesignDetails) HibernateUtil.getCurrentSession().get(
                ArrayDesignDetails.class, detailsId);
        assertNull(details);
        assertTrue(HibernateUtil.getCurrentSession().createCriteria(CaArrayFile.class).list().isEmpty());
        t.commit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingHeader() throws Exception {
        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.MISSING_HEADER_NDF);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

        } catch (Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingColumns() throws Exception {
        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.MISSING_COLUMNS_NDF);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

        } catch (Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    private class ArrayDesignServiceLocal extends ArrayDesignServiceBean {
        @Override
        public boolean isArrayDesignLocked(Long id) {
            return true;
        }
    }

    @Test
    public void testImportDesignDetails_NimblegenTestExpression() throws Exception {
        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();
            
            t = HibernateUtil.beginTransaction();
            design = arrayDesignService.getArrayDesign(design.getId());
            assertEquals("2006-08-03_HG18_60mer_expr-short", design.getName());
            assertEquals("caarray.nci.nih.gov", design.getLsidAuthority());
            assertEquals("domain", design.getLsidNamespace());
            assertEquals("2006-08-03_HG18_60mer_expr-short", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            t = HibernateUtil.beginTransaction();
            DesignElementList designElementList =
                AbstractAffymetrixChpDesignElementListUtility.getDesignElementList(design, arrayDesignService);
            //checkDesignElementList(designElementList, NimblegenArrayDesignFiles.EXPRESSION_DESIGN);
            t.commit();
            assertEquals(99, design.getNumberOfFeatures());

            // now try to re-import the design over itself
            // this only works when the delete array design details has been hooked up!
            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = HibernateUtil.beginTransaction();
            design = arrayDesignService.getArrayDesign(design.getId());
            assertEquals("2006-08-03_HG18_60mer_expr-short", design.getName());
            assertEquals("caarray.nci.nih.gov", design.getLsidAuthority());
            assertEquals("domain", design.getLsidNamespace());
            assertEquals("2006-08-03_HG18_60mer_expr-short", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            t = HibernateUtil.beginTransaction();
            designElementList = AbstractAffymetrixChpDesignElementListUtility.getDesignElementList(design, arrayDesignService);
            //checkDesignElementList(designElementList, NimblegenArrayDesignFiles.EXPRESSION_DESIGN[0]);
            t.commit();
            assertEquals(99, design.getNumberOfFeatures());
        } catch (Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    private CaArrayFile getCaArrayFile(File file) {
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        return caArrayFile;
    }

    
//    private void checkDesignElementList(DesignElementList designElementList, File cdfFile) {
//        AffymetrixCdfReader cdfReader = AffymetrixCdfReader.create(cdfFile);
//        assertEquals(cdfReader.getCdfData().getHeader().getNumProbeSets(), designElementList.getDesignElements().size());
//        for (int i = 0; i < designElementList.getDesignElements().size(); i++) {
//            LogicalProbe probe = (LogicalProbe) designElementList.getDesignElements().get(i);
//            assertEquals(cdfReader.getCdfData().getProbeSetName(i), probe.getName());
//        }
//    }

    @Test
    public void testImportDesignDetails_NimblegenPromoter() throws Exception {
        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.SHORT_PROMOTER_DESIGN);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            HibernateUtil.getCurrentSession().getTransaction().commit();

            assertEquals("HG18_Deluxe_Promoter_HX1-short", design.getName());
            assertEquals("caarray.nci.nih.gov", design.getLsidAuthority());
            assertEquals("domain", design.getLsidNamespace());
            assertEquals("HG18_Deluxe_Promoter_HX1-short", design.getLsidObjectId());

            t = HibernateUtil.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(999, design.getNumberOfFeatures());
            assertEquals(797, design.getDesignDetails().getLogicalProbes().size());
            assertEquals(999, design.getDesignDetails().getProbes().size());
            assertEquals(999, design.getDesignDetails().getFeatures().size());
            t.commit();

            t = HibernateUtil.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            ArrayDesign otherDesign = this.arrayDesignService.getArrayDesign("caarray.nci.nih.gov", "domain",
                    "HG18_Deluxe_Promoter_HX1-short");
            assertEquals("HG18_Deluxe_Promoter_HX1-short", otherDesign.getName());
            assertEquals("caarray.nci.nih.gov", otherDesign.getLsidAuthority());
            assertEquals("domain", otherDesign.getLsidNamespace());
            assertEquals("HG18_Deluxe_Promoter_HX1-short", otherDesign.getLsidObjectId());
            assertEquals(999, otherDesign.getNumberOfFeatures());
            assertEquals(797, otherDesign.getDesignDetails().getLogicalProbes().size());
            assertEquals(999, otherDesign.getDesignDetails().getProbes().size());
            assertEquals(999, otherDesign.getDesignDetails().getFeatures().size());
            assertEquals(1, otherDesign.getDesignFiles().size());
            for (CaArrayFile designFile : design.getDesignFiles()) {
                assertTrue(otherDesign.getDesignFiles().contains(designFile));
            }
            t.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    @Test
    public void testImportDesignDetails_NimblegenCGH() throws Exception {
        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(NimblegenArrayDesignFiles.SHORT_CGH_DESIGN);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            HibernateUtil.getCurrentSession().getTransaction().commit();

            assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getName());
            assertEquals("caarray.nci.nih.gov", design.getLsidAuthority());
            assertEquals("domain", design.getLsidNamespace());
            assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getLsidObjectId());

            t = HibernateUtil.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(4999, design.getNumberOfFeatures());
            assertEquals(41, design.getDesignDetails().getLogicalProbes().size());
            assertEquals(4999, design.getDesignDetails().getProbes().size());
            assertEquals(4999, design.getDesignDetails().getFeatures().size());
            t.commit();

            t = HibernateUtil.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            ArrayDesign otherDesign = this.arrayDesignService.getArrayDesign("caarray.nci.nih.gov", "domain",
                    "090210_HG18_WG_CGH_v3.1_HX3-short");
            assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", otherDesign.getName());
            assertEquals("caarray.nci.nih.gov", otherDesign.getLsidAuthority());
            assertEquals("domain", otherDesign.getLsidNamespace());
            assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", otherDesign.getLsidObjectId());
            assertEquals(4999, otherDesign.getNumberOfFeatures());
            assertEquals(41, otherDesign.getDesignDetails().getLogicalProbes().size());
            assertEquals(4999, otherDesign.getDesignDetails().getProbes().size());
            assertEquals(4999, otherDesign.getDesignDetails().getFeatures().size());
            assertEquals(1, otherDesign.getDesignFiles().size());
            for (CaArrayFile designFile : design.getDesignFiles()) {
                assertTrue(otherDesign.getDesignFiles().contains(designFile));
            }
            t.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

}
