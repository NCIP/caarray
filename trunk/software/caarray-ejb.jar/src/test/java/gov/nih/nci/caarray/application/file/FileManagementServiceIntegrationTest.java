//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class FileManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    @SuppressWarnings("unused")
    private FileManagementService fileManagementService;
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();

    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();

    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();

    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();
    private static Term DUMMY_TERM = new Term();

    @Before
    public void setUp() {
        this.fileManagementService = createFileManagementService(fileAccessService);
        resetData();
    }
    
    private static void resetData() {
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();

        DUMMY_EXPERIMENT_1 = new Experiment();

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();
        DUMMY_TERM = new Term();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("dummy source");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(DUMMY_CATEGORY);
        DUMMY_TERM.setSource(DUMMY_TERM_SOURCE);

        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
        DUMMY_PROJECT_1.setLocked(false);
    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void saveSupportingObjects() {
        TermSource caarraySource = new TermSource();
        caarraySource.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarraySource.setVersion(ExperimentOntology.CAARRAY.getVersion());

        VocabularyDao vocabularyDao = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        if (CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(ExampleSearchCriteria.forEntity(caarraySource)
                .includeNulls().excludeProperties("url"), Order.desc("version"))) == null) {
            HibernateUtil.getCurrentSession().save(caarraySource);
        }

        TermSource mgedOntology = new TermSource();
        mgedOntology.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mgedOntology.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());

        TermSource savedMgedOntology = CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(mgedOntology).includeNulls().excludeProperties("url"), Order
                        .desc("version")));
        if (savedMgedOntology == null) {
            HibernateUtil.getCurrentSession().save(mgedOntology);
            savedMgedOntology = mgedOntology;
        }

        if (vocabularyDao.getTerm(savedMgedOntology, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME) == null) {
            Term unknownProtocolType = new Term();
            unknownProtocolType.setValue(VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
            unknownProtocolType.setSource(savedMgedOntology);
            HibernateUtil.getCurrentSession().save(unknownProtocolType);
        }

        HibernateUtil.getCurrentSession().save(DUMMY_PROVIDER);
        HibernateUtil.getCurrentSession().save(DUMMY_ORGANISM);
        HibernateUtil.getCurrentSession().save(DUMMY_TERM);
    }

    @Test
    public void testImportMageTabSpecificationAndUpdateCharacteristics() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        importArrayDesign(AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("MDR1 overexpression."));
        assertEquals(1, project.getExperiment().getFactors().size());
        assertEquals(8, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        Source testSource = null;
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(testSource.getTissueSite());
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();

        // now try to update annotations of existing biomaterials
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(findSource(project, "TK6neo replicate 3"));
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();

        // now try to add a new biomaterial while update existing biomaterials
        importFiles(project, TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(7, project.getExperiment().getSources().size());
        assertEquals(7, project.getExperiment().getSamples().size());
        assertEquals(7, project.getExperiment().getExtracts().size());
        assertEquals(7, project.getExperiment().getLabeledExtracts().size());
        assertEquals(7, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast3", testSource.getCellType().getValue());
        testSource = findSource(project, "TK6neo replicate 3");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("HG-Focus", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray().getDesign().getName());
        tx.commit();
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();        
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef2() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        for (CaArrayFile file : fileSet.getFilesByType(FileType.AFFYMETRIX_CEL)) {
            file.setFileType(FileType.AFFYMETRIX_DAT);
        }
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, null);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }
    
    @Test
    public void testImportNonMageTabWithoutArrayDesign() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        MageTabFileSet inputFiles = new MageTabFileSet();
        for (File f : TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET.getAllFiles()) {
            if (f.getName().endsWith("CEL")) {
                inputFiles.addNativeData(f);
            }
        }
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET); 
        docSet.getIdfDocuments().clear();
        docSet.getSdrfDocuments().clear();

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        CaArrayFileSet fileSet = uploadFiles(project, inputFiles);
        for (CaArrayFile file : fileSet.getFilesByType(FileType.AFFYMETRIX_CEL)) {
            file.setFileType(FileType.AFFYMETRIX_DAT);
        }
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        assertEquals(3, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testValidateDefect18625Hybes() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();
        Map<File, FileType> files = new HashMap<File, FileType>();
        files.put(IlluminaArrayDataFiles.DEFECT_18652_IDF, FileType.MAGE_TAB_IDF);
        files.put(IlluminaArrayDataFiles.DEFECT_18652_SDRF, FileType.MAGE_TAB_SDRF);
        files.put(IlluminaArrayDataFiles.HUMAN_WG6_SMALL, FileType.ILLUMINA_DATA_CSV);

        uploadAndValidateFiles(DUMMY_PROJECT_1, files);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        for (CaArrayFile file : project.getFiles()) {
            if (!file.getFileType().equals(FileType.MAGE_TAB_SDRF)) {
                assertEquals(FileStatus.VALIDATED, file.getFileStatus());
            } else {
                assertEquals(FileStatus.VALIDATION_ERRORS, file.getFileStatus());
                assertEquals(1, file.getValidationResult().getMessages().size());
                assertTrue(file.getValidationResult()
                        .getMessages().get(0).getMessage().contains("WRONG"));
            }
        }
        tx.commit();

    }

    @Test
    public void testUpdateBioMaterialChain() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getExtracts().size());
        assertEquals(2, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add new biomaterials in the middle of the existing chains
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        Set<Extract> extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add a data files to existing hybs
        importFiles(project, TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();
    }

    @Test
    public void testUpdateFiles() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        saveSupportingObjects();
        ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DUMMY_EXPERIMENT_1.getArrayDesigns().add(design);
        HibernateUtil.getCurrentSession().save(DUMMY_PROJECT_1);
        tx.commit();

        importFiles(DUMMY_PROJECT_1, TestMageTabSets.UPDATE_FILES_BASELINE_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        Project project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertEquals(1, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        RawArrayData raw = project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().iterator().next();
        DerivedArrayData derived = project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals(1, derived.getDerivedFromArrayDataCollection().size());
        assertEquals(raw, derived.getDerivedFromArrayDataCollection().iterator().next());
        tx.commit();

        // now try to add data files to existing hybs, which also reference existing data files
        importFiles(project, TestMageTabSets.UPDATE_FILES_NEW_INPUT_SET);

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        derived = project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals("BM1.EXP", derived.getDataFile().getName());
        assertEquals(2, derived.getDerivedFromArrayDataCollection().size());
        for (RawArrayData oneRaw : project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection()) {
            assertTrue(Arrays.asList("BM1.CEL", "BM1a.CEL").contains(oneRaw.getDataFile().getName()));
            assertTrue("BM1.EXP is not derived from " + oneRaw.getName(), derived.getDerivedFromArrayDataCollection()
                    .contains(oneRaw));
        }

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 2").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 2").getDerivedDataCollection().size());
        raw = project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().iterator().next();
        assertEquals("BM2.CEL", raw.getDataFile().getName());
        for (DerivedArrayData oneDerived : project.getExperiment().getHybridizationByName("Hyb 2")
                .getDerivedDataCollection()) {
            assertTrue(Arrays.asList("BM2.EXP", "BM2a.EXP").contains(oneDerived.getDataFile().getName()));
            assertEquals(1, oneDerived.getDerivedFromArrayDataCollection().size());
            assertEquals(raw, oneDerived.getDerivedFromArrayDataCollection().iterator().next());
        }

        tx.commit();
    }

    @SuppressWarnings("PMD")
    private void importFiles(Project project, MageTabFileSet fileSet) throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        CaArrayFileSet caarrayFileSet = uploadFiles(project, fileSet);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        importFiles(project, caarrayFileSet, null);
        HibernateUtil.getCurrentSession().getTransaction().commit();
    }

    @SuppressWarnings("PMD")
    private void uploadAndValidateFiles(Project project, Map<File, FileType> files) throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        CaArrayFileSet fileSet = uploadFiles(project, files);
        tx.commit();

        helpValidateFiles(tx, project, fileSet);
    }

    @SuppressWarnings("PMD")
    private void helpValidateFiles(Transaction tx, Project project, CaArrayFileSet fileSet) throws Exception {
        tx = HibernateUtil.beginTransaction();
        project = (Project) HibernateUtil.getCurrentSession().load(Project.class, project.getId());
        validateFiles(project, fileSet);
        tx.commit();
    }

    /**
     * "Upload" files to a project, returning the CaArrayFileSet containing those files.
     * @param project project to upload to
     * @param fileSet MageTabFileSet containing the files to upload (should correspond to the files in the file set)
     * @return
     */
    private CaArrayFileSet uploadFiles(Project project, MageTabFileSet fileSet) {
        for (File file : fileSet.getAllFiles()) {
            this.fileAccessService.add(file);
        }
        CaArrayFileSet caarrayFileSet = TestMageTabSets.getFileSet(fileSet);
        for (CaArrayFile file : caarrayFileSet.getFiles()) {
            file.setProject(project);
            project.getFiles().add(file);
            HibernateUtil.getCurrentSession().save(file);
        }
        HibernateUtil.getCurrentSession().update(project);
        return caarrayFileSet;
    }

    private CaArrayFileSet uploadFiles(Project project, Map<File, FileType> files) {
        for (File file : files.keySet()) {
            CaArrayFile caArrayFile = this.fileAccessService.add(file);
            caArrayFile.setProject(project);
            caArrayFile.setFileType(files.get(file));
            project.getFiles().add(caArrayFile);
            HibernateUtil.getCurrentSession().save(caArrayFile);
        }

        HibernateUtil.getCurrentSession().update(project);
        return project.getFileSet();
    }



    private Source findSource(Project project, String name) {
        return CaArrayDaoFactory.INSTANCE.getProjectDao().getSourceForExperiment(project.getExperiment(), name);
    }

    private void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) throws Exception {
        ProjectFilesImportJob job = new ProjectFilesImportJob(UsernameHolder.getUser(), targetProject, fileSet,
                dataImportOptions);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        try {
            job.execute();
        } catch (Exception e) {
            job.getUnexpectedErrorPreparedStatement(HibernateUtil.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private void validateFiles(Project targetProject, CaArrayFileSet fileSet) throws Exception {
        ProjectFilesValidationJob job = new ProjectFilesValidationJob(UsernameHolder.getUser(), targetProject, fileSet);
        job.setDaoFactory(CaArrayDaoFactory.INSTANCE);
        try {
            job.execute();
        } catch (Exception e) {
            job.getUnexpectedErrorPreparedStatement(HibernateUtil.getCurrentSession().connection()).execute();
            throw e;
        }
    }

    private ArrayDesign importArrayDesign(File designFile) throws IllegalAccessException, InvalidDataFileException {
        ArrayDesign design = new ArrayDesign();
        design.setName(designFile.getName());
        design.setVersion("2.0");
        design.setGeoAccession("GPL0000");
        design.setProvider(DUMMY_PROVIDER);
        design.setLsidForEntity("authority:namespace:" + designFile.getName());
        design.addDesignFile(this.fileAccessService.add(designFile));
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        HibernateUtil.getCurrentSession().save(design);

        ArrayDesignServiceBean arrayDesignService = new ArrayDesignServiceBean();
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);

        return design;
    }

    private static FileManagementService createFileManagementService(final FileAccessServiceStub fileAccessServiceStub) {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerActualImplementations();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);

        FileManagementServiceBean bean = new FileManagementServiceBean();
        DirectJobSubmitter submitter = new DirectJobSubmitter(new FileManagementMDB());
        bean.setSubmitter(submitter);

        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));

        return bean;
    }



}
