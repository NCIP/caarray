//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.SNPProbeAnnotation;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceTest extends AbstractServiceTest {

    private ArrayDesignService arrayDesignService;
    private final LocalDaoFactoryStub caArrayDaoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    private Transaction transaction;

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");

    @Before
    public void setUp() {
        caArrayDaoFactoryStub.clear();
        this.arrayDesignService = createArrayDesignService(this.caArrayDaoFactoryStub, this.fileAccessServiceStub, this.vocabularyServiceStub);
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
        HibernateUtil.setFiltersEnabled(false);
        this.transaction = HibernateUtil.beginTransaction();
    }

    private static ArrayDesignService createArrayDesignService(DaoFactoryStub caArrayDaoFactoryStub,
            final FileAccessServiceStub fileAccessServiceStub, VocabularyServiceStub vocabularyServiceStub) {
        ArrayDesignServiceBean bean = new ArrayDesignServiceBean();
        bean.setDaoFactory(caArrayDaoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        return bean;
    }

    @After
    public void tearDown() {
        if (this.transaction != null && this.transaction.isActive()) {
            this.transaction.rollback();
        }
    }


    @Test
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesign() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        this.arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
        assertNull(design.getDescription());

        design.setDescription("new description");
        this.arrayDesignService.saveArrayDesign(design);
        ArrayDesign updatedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals("Test3", updatedDesign.getName());
        assertEquals("Affymetrix.com", updatedDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", updatedDesign.getLsidNamespace());
        assertEquals("Test3", updatedDesign.getLsidObjectId());
        assertEquals("new description", updatedDesign.getDescription());

        // "lock" this design by setting the ID to 2 but can still update description of locked designs
        design.setId(2L);
        this.caArrayDaoFactoryStub.getArrayDao().save(design);
        design.setDescription("another description");
        this.arrayDesignService.saveArrayDesign(design);
        ArrayDesign updatedLockedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals("Test3", updatedLockedDesign.getName());
        assertEquals("Affymetrix.com", updatedLockedDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", updatedLockedDesign.getLsidNamespace());
        assertEquals("Test3", updatedLockedDesign.getLsidObjectId());
        assertEquals("another description", updatedLockedDesign.getDescription());
    }

    @Test(expected=IllegalAccessException.class)
    public void testSaveArrayDesignWhileImporting() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        this.arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
        assertNull(design.getDescription());

        design.getFirstDesignFile().setFileStatus(FileStatus.IMPORTING);
        design.setName("new name");
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeOrganization() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        design.setProvider(new Organization());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeAssayType() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        design.setAssayTypes(assayTypes);
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeDesignFile() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        design.getDesignFiles().clear();
        design.addDesignFile(new CaArrayFile());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test
    public void testImportDesignDetails_Genepix() {
        ArrayDesign design = new ArrayDesign();
        this.arrayDesignService.importDesignDetails(design);
        assertNull(design.getNumberOfFeatures());

        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "Demo", 8064, 4, 4);
        design.getDesignFiles().clear();
        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "MEEBO", 38880, 4, 12);
    }

    private void checkGenepixDesign(ArrayDesign design, String expectedName, int expectedNumberOfFeatures,
            int largestExpectedBlockColumn, int largestExpectedBlockRow) {
        assertEquals(expectedName, design.getName());
        assertEquals(expectedNumberOfFeatures, design.getNumberOfFeatures().intValue());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getFeatures().size());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getProbes().size());
        Iterator<PhysicalProbe> probeIt = design.getDesignDetails().getProbes().iterator();
        while (probeIt.hasNext()) {
            PhysicalProbe probe = probeIt.next();
            assertFalse(StringUtils.isBlank(probe.getName()));
            assertEquals(1, probe.getFeatures().size());
            Feature feature = probe.getFeatures().iterator().next();
            assertTrue(feature.getBlockColumn() > 0);
            assertTrue(feature.getBlockRow() > 0);
            assertTrue(feature.getBlockColumn() <= largestExpectedBlockColumn);
            assertTrue(feature.getBlockRow() <= largestExpectedBlockRow);
            assertTrue(feature.getColumn() > 0);
            assertTrue(feature.getRow() > 0);
        }
    }

    @Test
    public void testImportDesign_ArrayDesign() {
        ArrayDesign design = new ArrayDesign();
        this.arrayDesignService.importDesign(design);
        assertNull(design.getName());

        design.addDesignFile(getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        this.arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
    }

    @Test
    public void testImportDesign_AffymetrixTest3() {
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Test3", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Test3", arrayDesign.getLsidObjectId());
        assertEquals(15876, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testImportDesignDetails_AffymetrixTest3() throws AffymetrixArrayDesignReadException {
        ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
        assertEquals(15876, design.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixMapping10K() {
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEN_K_CDF);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getLsidObjectId());
        assertEquals(506944, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixHuEx() {
        CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(clfDesignFile);
        arrayDesign.addDesignFile(pgfDesignFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getLsidObjectId());
        assertEquals(1024, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testImportDesignDetails_AffymetrixHuEx() throws AffymetrixArrayDesignReadException {
        CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(clfDesignFile);
        design.addDesignFile(pgfDesignFile);
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        final String arrayDesignName = "HuEx-1_0-st-v1-test";
        assertEquals(arrayDesignName, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(arrayDesignName, design.getLsidObjectId());
        assertEquals(1024, design.getNumberOfFeatures().intValue());

        assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
        assertEquals(364, design.getDesignDetails().getProbes().size());
        assertEquals(1024, design.getDesignDetails().getFeatures().size());

        for (PhysicalProbe probe : design.getDesignDetails().getProbes()) {
            assertTrue(probe.getFeatures().size() > 0);
            assertEquals(design.getDesignDetails(), probe.getArrayDesignDetails());
            for (Feature feature : probe.getFeatures()) {
                assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
            }
        }

        for (LogicalProbe logicalProbe : design.getDesignDetails().getLogicalProbes()) {
            assertEquals(design.getDesignDetails(), logicalProbe.getArrayDesignDetails());
            assertTrue(logicalProbe.getProbes().size() > 0);
            for (PhysicalProbe physicalProbe : logicalProbe.getProbes()) {
                assertEquals(design.getDesignDetails(), physicalProbe.getArrayDesignDetails());
                assertTrue(physicalProbe.getFeatures().size() > 0);
                for (Feature feature : physicalProbe.getFeatures()) {
                    assertEquals(design.getDesignDetails(), feature.getArrayDesignDetails());
                    assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                    assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
                }
            }
        }
    }

    @Test
    public void testImportDesign_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Human_WG-6", arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Human_WG-6", arrayDesign.getLsidObjectId());
        assertEquals(47296, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        arrayDesignService.importDesignDetails(arrayDesign);
        assertEquals(47296, arrayDesign.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_IlluminaHumanHap300() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());
        assertEquals(318237, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_IlluminaHumanHap300() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        arrayDesignService.importDesignDetails(arrayDesign);
        assertEquals(318237, arrayDesign.getDesignDetails().getLogicalProbes().size());
        for (LogicalProbe probe : arrayDesign.getDesignDetails().getLogicalProbes()) {
            assertNotNull(probe);
            assertNotNull(probe.getAnnotation());
            SNPProbeAnnotation annotation = (SNPProbeAnnotation) probe.getAnnotation();
            assertNotNull(annotation.getDbSNPId());
            assertNotNull(annotation.getDbSNPVersion());
            assertNotNull(annotation.getAlleleA());
            assertNotNull(annotation.getAlleleB());
            assertNotNull(annotation.getPhysicalPosition());
            assertEquals(probe.getName(), annotation.getDbSNPId());
            assertEquals(arrayDesign.getDesignDetails(), probe.getArrayDesignDetails());
        }
    }

    @Test
    public void testImportDesign_NimblegenTestExpression() throws Exception {
        CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.EXPRESSION_DESIGN[0]);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        assertEquals("2006-08-03_HG18_60mer_expr", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("2006-08-03_HG18_60mer_expr", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(391973, design.getDesignDetails().getProbes().size());
        assertEquals(47686, design.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_NimblegenTestCGH() throws Exception {
        CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.CGH_DESIGN[0]);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(732623, design.getDesignDetails().getProbes().size());
        assertEquals(70, design.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_UnsupportedVendors() {
        // The specific file doesn't matter, because the type will determine how the file is handled
        // Once these file types can be properly parsed, they should be pulled out into their own tests
        CaArrayFile designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.AGILENT_CSV);
        ArrayDesign arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("Agilent.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.AGILENT_XML);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("Agilent.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.IMAGENE_TPL);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.UCSF_SPOT_SPT);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF, FileType.MAGE_TAB_ADF);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("a-mexp-58f_excerpt_v1.0", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("a-mexp-58f_excerpt_v1.0", arrayDesign.getLsidObjectId());
    }

    @Test
    public void testValidateDesign_AffymetrixTest3() {
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ValidationResult result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHG_U133_Plus2() {
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF);
        ValidationResult result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHuEx() {
        CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_PGF);
        CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_CLF);
        Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
        designFiles.add(pgfDesignFile);
        designFiles.add(clfDesignFile);
        ValidationResult result = this.arrayDesignService.validateDesign(designFiles);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_Genepix() {
        CaArrayFile designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL);
        ValidationResult result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.TWO_K_GAL);
        result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO);
        result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.toString(), result.isValid());
    }

    @Test
    public void testValidateDesign_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ValidationResult result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_CONTENT);
        result = this.arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_HEADERS);
        result = this.arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
        designFile = getIlluminaCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        result = this.arrayDesignService.validateDesign(designFile);
        assertFalse(result.isValid());
    }

    @Test
    public void testValidateDesign_IlluminaHumanHap300() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        ValidationResult result = this.arrayDesignService.validateDesign(designFile);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_InvalidFileType() {
        CaArrayFile invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, FileType.AFFYMETRIX_CEL);
        ValidationResult result = this.arrayDesignService.validateDesign(invalidDesignFile);
        assertFalse(result.isValid());
        invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, FileType.AGILENT_CSV);
        result = this.arrayDesignService.validateDesign(invalidDesignFile);
        assertTrue(result.isValid());
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, invalidDesignFile.getFileStatus());
        invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, null);
        result = this.arrayDesignService.validateDesign(invalidDesignFile);
        assertFalse(result.isValid());
    }

    @Test
    public void testDuplicateArrayDesign() {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        this.arrayDesignService.importDesign(design);
        @SuppressWarnings("unused")
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ValidationResult result = this.arrayDesignService.validateDesign(design);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("has already been imported"));

        ArrayDesign design2 = createDesign(null, null, null, getCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF,
                FileType.UCSF_SPOT_SPT));
        this.arrayDesignService.importDesign(design2);
        result = this.arrayDesignService.validateDesign(design2);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("design already exists with the name"));
    }

    private ArrayDesign createDesign(Organization provider, Organism organism, SortedSet<AssayType> assayTypes,
            CaArrayFile caArrayFile) {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setName("Dummy Design");

        if (provider == null) {
            provider = DUMMY_ORGANIZATION;
        }
        arrayDesign.setProvider(provider);

        if (organism == null) {
            organism = DUMMY_ORGANISM;
        }
        arrayDesign.setOrganism(organism);

        if (assayTypes == null) {
            assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAY_TYPE);
        }
        arrayDesign.setAssayTypes(assayTypes);

        if (caArrayFile == null) {
            caArrayFile = new CaArrayFile();
        }
        arrayDesign.addDesignFile(caArrayFile);

        arrayDesign.setTechnologyType(DUMMY_TERM);
        return arrayDesign;
    }


    private CaArrayFile getGenepixCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.GENEPIX_GAL);
    }

    private CaArrayFile getAffymetrixCdfCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.AFFYMETRIX_CDF);
    }

    private CaArrayFile getIlluminaCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.ILLUMINA_DESIGN_CSV);
    }

    private CaArrayFile getNimblegenCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.NIMBLEGEN_NDF);
    }

    /**
     * Sets file type based on extension.
     */
    private CaArrayFile getCaArrayFile(File file) {
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        return caArrayFile;
    }

    private CaArrayFile getCaArrayFile(File file, FileType type) {
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setFileType(type);
        return caArrayFile;
    }

    @Test
    public void testOrganizations() {
        assertEquals(0, this.arrayDesignService.getAllProviders().size());
        Organization o = new Organization();
        o.setName("Foo");
        o.setProvider(true);
        this.caArrayDaoFactoryStub.getSearchDao().save(o);
        assertEquals(1, this.arrayDesignService.getAllProviders().size());
        assertEquals("Foo", this.arrayDesignService.getAllProviders().get(0).getName());
    }

    private void checkChpDesignElementList(DesignElementList designElementList, File cdfFile)
            throws AffymetrixArrayDesignReadException {
        AffymetrixCdfReader cdfReader = AffymetrixCdfReader.create(cdfFile);
        assertEquals(cdfReader.getCdfData().getHeader().getNumProbeSets(), designElementList.getDesignElements().size());
        for (int i = 0; i < designElementList.getDesignElements().size(); i++) {
            LogicalProbe probe = (LogicalProbe) designElementList.getDesignElements().get(i);
            assertEquals(cdfReader.getCdfData().getProbeSetName(i), probe.getName());
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        private final Map<String, AbstractCaArrayEntity> lsidEntityMap = new HashMap<String, AbstractCaArrayEntity>();
        private final Map<Long, PersistentObject> objectMap = new HashMap<Long, PersistentObject>();
        private static long nextId = 0;
        private static long nextFeatureId = 1;

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {
                @Override
                public Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names) {
                    Map<String, Long> map = new HashMap<String, Long>();
                    for (LogicalProbe lp : design.getDesignDetails().getLogicalProbes()) {
                        if (names.contains(lp.getName())) {
                            map.put(lp.getName(), lp.getId());
                        }
                    }
                    return map;
                }

                @Override
                public List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly) {
                    List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                    for (PersistentObject entity : LocalDaoFactoryStub.this.objectMap.values()) {
                        if (entity instanceof ArrayDesign) {
                            ArrayDesign design = (ArrayDesign) entity;
                            if (ObjectUtils.equals(provider, design.getProvider())
                                    && (!importedOnly
                                            || design.getDesignFileSet().getStatus() == FileStatus.IMPORTED
                                            || design.getDesignFileSet().getStatus() == FileStatus.IMPORTED_NOT_PARSED)) {
                                designs.add(design);
                            }
                        }
                    }
                    return designs;
                }

                @Override
                public List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params) {
                    List<Long> ids = new ArrayList<Long>();
                    for (Entry<Long, PersistentObject> entry : objectMap.entrySet()) {
                        if (entry.getValue() instanceof LogicalProbe) {
                            ids.add(entry.getKey());
                        }
                    }
                    Collections.sort(ids);
                    int startIndex = params.getIndex();
                    int toIndex = Math.min(ids.size(), startIndex + params.getPageSize());
                    if (startIndex > ids.size()) {
                        return new ArrayList<Long>();
                    }
                    return ids.subList(startIndex, toIndex);
                }

                @SuppressWarnings("deprecation")
                @Override
                public void save(PersistentObject object) {
                    if (object instanceof AbstractCaArrayObject) {
                        AbstractCaArrayObject caArrayObject = (AbstractCaArrayObject) object;
                        if (caArrayObject.getId() == null && !(caArrayObject instanceof Feature)) {
                            caArrayObject.setId(nextId++);
                        } else if (caArrayObject.getId() == null && caArrayObject instanceof Feature) {
                            caArrayObject.setId(nextFeatureId++);
                            nextId = nextFeatureId;
                        }
                        if (caArrayObject instanceof AbstractCaArrayEntity) {
                            AbstractCaArrayEntity caArrayEntity = (AbstractCaArrayEntity) object;
                            LocalDaoFactoryStub.this.lsidEntityMap.put(caArrayEntity.getLsid(), caArrayEntity);
                        }
                        LocalDaoFactoryStub.this.objectMap.put(caArrayObject.getId(), caArrayObject);
                    }
                    // manually create reverse association automatically created by database fk relationship
                    if (object instanceof LogicalProbe) {
                        LogicalProbe probe = (LogicalProbe) object;
                        probe.getArrayDesignDetails().getLogicalProbes().add(probe);
                    } else if (object instanceof PhysicalProbe) {
                        PhysicalProbe probe = (PhysicalProbe) object;
                        probe.getArrayDesignDetails().getProbes().add(probe);
                    } else if (object instanceof Feature) {
                        Feature feature = (Feature) object;
                        feature.getArrayDesignDetails().getFeatures().add(feature);
                    }
                }

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    return (ArrayDesign)
                        LocalDaoFactoryStub.this.lsidEntityMap.get("URN:LSID:" + lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    return (DesignElementList)
                    LocalDaoFactoryStub.this.lsidEntityMap.get("URN:LSID:" + lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public ArrayDesign getArrayDesign(long id) {
                    return (ArrayDesign) LocalDaoFactoryStub.this.objectMap.get(id);
                }

                @Override
                public <T extends PersistentObject> List<T> queryEntityByExample(T entityToMatch, Order... order) {
                    List<T> entities = new ArrayList<T>();
                    entities.add(entityToMatch);
                    return entities;
                }

                @Override
                public boolean isArrayDesignLocked(Long id) {
                    return id.equals(2L);
                }

                @Override
                public Long getFirstFeatureId(ArrayDesignDetails designDetails) {
                    return NumberUtils.LONG_ONE;
                }

                @Override
                public void createFeatures(int rows, int cols, ArrayDesignDetails designDetails) {
                    for (int y = 0; y < rows; y++) {
                        for (int x = 0; x < cols; x++) {
                            Feature feature = new Feature(designDetails);
                            feature.setColumn((short) x);
                            feature.setRow((short) y);
                            getArrayDao().save(feature);
                        }
                    }
                }

            };
        }

        public void clear() {
            lsidEntityMap.clear();
            objectMap.clear();
            nextId = 0;
            nextFeatureId = 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub() {
                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
                    return (T) LocalDaoFactoryStub.this.objectMap.get(entityId);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> T retrieveUnsecured(Class<T> entityClass, Long entityId) {
                    return (T) LocalDaoFactoryStub.this.objectMap.get(entityId);
                }

                @Override
                public void save(PersistentObject object) {
                    LocalDaoFactoryStub.this.objectMap.put(object.getId(), object);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ContactDao getContactDao() {
            return new ContactDaoStub() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public List<Organization> getAllProviders() {
                    List<Organization> orgs = new ArrayList<Organization>();
                    CollectionUtils.select(LocalDaoFactoryStub.this.objectMap.values(), PredicateUtils.instanceofPredicate(Organization.class), orgs);
                    return orgs;
                }
            };
        }

    }
}
