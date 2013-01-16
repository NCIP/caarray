//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.CaArrayUtils;

/**
 * @author Scott Miller
 *
 */
public class FileDaoTest extends AbstractDaoTest {
    private FileDao DAO_OBJECT;

    // Experiment
    private Organism DUMMY_ORGANISM;
    private Organization DUMMY_PROVIDER;
    private Project DUMMY_PROJECT_1;
    private Experiment DUMMY_EXPERIMENT_1;
    private TermSource DUMMY_TERM_SOURCE;
    private Category DUMMY_CATEGORY;

    // Annotations
    private Term DUMMY_REPLICATE_TYPE;
    private Term DUMMY_NORMALIZATION_TYPE;
    private Term DUMMY_QUALITY_CTRL_TYPE;

    private QuantitationType DUMMY_QUANT_TYPE;

    private URI DUMMY_DATA_HANDLE;

    private VocabularyDao VOCABULARY_DAO;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        initializeDao();
        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Annotations
        DUMMY_REPLICATE_TYPE = new Term();
        DUMMY_NORMALIZATION_TYPE = new Term();
        DUMMY_QUALITY_CTRL_TYPE = new Term();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();

        DUMMY_QUANT_TYPE = new QuantitationType();
        DUMMY_QUANT_TYPE.setDataType(DataType.BOOLEAN);
        DUMMY_QUANT_TYPE.setName("dummy_quant");

        DUMMY_DATA_HANDLE = CaArrayUtils.makeUriQuietly("file-system:foo");

    }

    // Has dependency on AbstractHibernateTest.baseIntegrationSetUp() being called first !!!
    // Otherwise CaArrayDaoFactoryImpl.CaArrayHibernateHelper will not have been initialized
    // via Guice injection, and the DAO created will not have a hibernateHelper
    private void initializeDao() {
        if (DAO_OBJECT == null) {
            DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getFileDao();
        }

        if (VOCABULARY_DAO == null) {
            VOCABULARY_DAO = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
        }
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        setExperimentAnnotations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setProject(DUMMY_PROJECT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
    }

    private void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        final Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private void setExperimentAnnotations() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_EXPERIMENT_1.getReplicateTypes().add(DUMMY_REPLICATE_TYPE);
        DUMMY_EXPERIMENT_1.getNormalizationTypes().add(DUMMY_NORMALIZATION_TYPE);
        DUMMY_EXPERIMENT_1.getQualityControlTypes().add(DUMMY_QUALITY_CTRL_TYPE);
    }

    private void saveSupportingObjects() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        VOCABULARY_DAO.save(DUMMY_REPLICATE_TYPE);
        VOCABULARY_DAO.save(DUMMY_QUALITY_CTRL_TYPE);
        VOCABULARY_DAO.save(DUMMY_NORMALIZATION_TYPE);
        DAO_OBJECT.save(DUMMY_PROJECT_1);
        assertTrue(hibernateHelper.getCurrentSession().contains(DUMMY_EXPERIMENT_1));
        DAO_OBJECT.save(DUMMY_QUANT_TYPE);
        DAO_OBJECT.flushSession();
    }

    @Test
    public void testSaveAndRemove() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();
        final CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
        DUMMY_FILE_1.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        DUMMY_FILE_1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        DUMMY_FILE_1.setFileStatus(FileStatus.UPLOADED);
        DUMMY_FILE_1.setDataHandle(DUMMY_DATA_HANDLE);
        DUMMY_FILE_1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_1);
        DAO_OBJECT.save(DUMMY_FILE_1);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        CaArrayFile retrieved =
                (CaArrayFile) this.hibernateHelper.getCurrentSession().get(CaArrayFile.class, DUMMY_FILE_1.getId());
        assertNotNull(retrieved);
        assertEquals(DUMMY_DATA_HANDLE, retrieved.getDataHandle());
        DAO_OBJECT.remove(retrieved);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        retrieved = (CaArrayFile) this.hibernateHelper.getCurrentSession().get(CaArrayFile.class, DUMMY_FILE_1.getId());
        assertNull(retrieved);
        tx.commit();

    }

    @Test
    public void testSearchByCriteria() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1.idf");
        file1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setDataHandle(DUMMY_DATA_HANDLE);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        final Hybridization h1 = new Hybridization();
        h1.setName("h1");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h1);
        h1.setExperiment(DUMMY_EXPERIMENT_1);
        final RawArrayData rawArrayData = new RawArrayData();
        h1.addArrayData(rawArrayData);
        rawArrayData.addHybridization(h1);
        rawArrayData.setName("h1");
        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2.cel");
        file2.setFileType(AFFYMETRIX_CEL);
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setDataHandle(DUMMY_DATA_HANDLE);
        file2.setProject(DUMMY_PROJECT_1);
        rawArrayData.setDataFile(file2);
        DUMMY_PROJECT_1.getFiles().add(file2);
        DAO_OBJECT.save(file2);
        DAO_OBJECT.save(rawArrayData);

        final Hybridization h2 = new Hybridization();
        h2.setName("h2");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h2);
        h2.setExperiment(DUMMY_EXPERIMENT_1);
        final DerivedArrayData derivedArrayData = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derivedArrayData);
        derivedArrayData.addHybridization(h2);
        final CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3.chp");
        file3.setFileType(AFFYMETRIX_CHP);
        file3.setFileStatus(FileStatus.UPLOADED);
        file3.setDataHandle(DUMMY_DATA_HANDLE);
        file3.setProject(DUMMY_PROJECT_1);
        derivedArrayData.setDataFile(file3);
        DUMMY_PROJECT_1.getFiles().add(file3);
        DAO_OBJECT.save(file3);
        DAO_OBJECT.save(derivedArrayData);

        final Source so1 = new Source();
        so1.setName("source");
        DUMMY_EXPERIMENT_1.getSources().add(so1);
        so1.setExperiment(DUMMY_EXPERIMENT_1);
        final Sample sa1 = new Sample();
        sa1.setName("sample");
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        DUMMY_EXPERIMENT_1.getSamples().add(sa1);
        sa1.setExperiment(DUMMY_EXPERIMENT_1);
        final Extract ex1 = new Extract();
        ex1.setName("extract1");
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa1);
        DUMMY_EXPERIMENT_1.getExtracts().add(ex1);
        ex1.setExperiment(DUMMY_EXPERIMENT_1);
        final Extract ex2 = new Extract();
        ex2.setName("extract2");
        DUMMY_EXPERIMENT_1.getExtracts().add(ex2);
        ex2.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le1 = new LabeledExtract();
        le1.setName("LE1");
        ex1.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex1);
        ex2.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex2);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le1);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le1);
        le1.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le2 = new LabeledExtract();
        le2.setName("LE2");
        ex2.getLabeledExtracts().add(le2);
        le2.getExtracts().add(ex2);
        le2.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le2);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le2);
        le2.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le3 = new LabeledExtract();
        le3.setName("LE3");
        le3.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le3);
        le3.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le3);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le3);
        le3.setExperiment(DUMMY_EXPERIMENT_1);
        DAO_OBJECT.save(DUMMY_EXPERIMENT_1);

        final CaArrayFile file4 = new CaArrayFile();
        file4.setName("file4.cdf");
        file4.setFileType(AFFYMETRIX_CDF);
        file4.setFileStatus(FileStatus.UPLOADED);
        file4.setDataHandle(DUMMY_DATA_HANDLE);
        DAO_OBJECT.save(file4);

        final CaArrayFile file5 = new CaArrayFile();
        file5.setName("file5.txt");
        file5.setFileStatus(FileStatus.SUPPLEMENTAL);
        file5.setDataHandle(DUMMY_DATA_HANDLE);
        file5.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file5);
        DAO_OBJECT.save(file5);

        final Hybridization h3 = new Hybridization();
        h3.setName("h3");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h3);
        h3.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le4 = new LabeledExtract();
        le4.setName("LE4");
        le4.getHybridizations().add(h3);
        h3.getLabeledExtracts().add(le4);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le4);

        final CaArrayFile file6 = new CaArrayFile();
        file6.setName("file6.chp");
        file6.setFileType(AFFYMETRIX_CHP);
        file6.setFileStatus(FileStatus.UPLOADED);
        file6.setDataHandle(DUMMY_DATA_HANDLE);
        DAO_OBJECT.save(file6);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final Experiment e =
                CaArrayDaoFactory.INSTANCE.getSearchDao().retrieve(Experiment.class, DUMMY_EXPERIMENT_1.getId());

        final PageSortParams<CaArrayFile> params =
                new PageSortParams<CaArrayFile>(5, 0, new AdHocSortCriterion<CaArrayFile>("name"), false);
        final FileSearchCriteria criteria = new FileSearchCriteria();
        criteria.getCategories().add(FileCategory.RAW_DATA);
        criteria.getCategories().add(FileCategory.DERIVED_DATA);

        List<CaArrayFile> files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(3, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());
        assertEquals(file6.getName(), files.get(2).getName());

        criteria.setExtension("CHP");
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file3.getName(), files.get(0).getName());
        assertEquals(file6.getName(), files.get(1).getName());

        criteria.getExperimentNodes().add(ex2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());

        criteria.setExtension(null);
        criteria.getExperimentNodes().clear();
        criteria.setExperiment(e);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.getCategories().remove(FileCategory.RAW_DATA);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());

        criteria.getCategories().add(FileCategory.OTHER);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file3.getName(), files.get(0).getName());
        assertEquals(file5.getName(), files.get(1).getName());

        criteria.getExperimentNodes().add(h1);
        criteria.getExperimentNodes().add(h2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());

        criteria.getCategories().add(FileCategory.RAW_DATA);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(h1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(h3);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(0, files.size());

        criteria.getExperimentNodes().clear();
        criteria.setExtension("CHP");
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());
        criteria.setExtension(".CHP");
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());

        criteria.setExtension(null);
        criteria.getTypes().add(AFFYMETRIX_CEL);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());

        criteria.getTypes().clear();
        criteria.getExperimentNodes().add(so1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());

        criteria.getExperimentNodes().add(sa1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(ex1);
        criteria.getExperimentNodes().add(ex2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(le3);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(le4);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(0, files.size());

        criteria.getExperimentNodes().clear();
        criteria.getExperimentNodes().add(le3);
        criteria.getExperimentNodes().add(h2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        tx.commit();
    }

    @Test
    public void testFilePermissions() throws Exception {
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();

        final CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setDataHandle(DUMMY_DATA_HANDLE);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        final CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setFileType(AFFYMETRIX_CDF);
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setDataHandle(DUMMY_DATA_HANDLE);
        DAO_OBJECT.save(file2);

        final CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setFileType(AFFYMETRIX_CDF);
        file3.setFileStatus(FileStatus.IMPORTED);
        file3.setDataHandle(DUMMY_DATA_HANDLE);
        DAO_OBJECT.save(file3);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        List<CaArrayFile> files =
                CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(3, files.size());
        assertEquals("file1", files.get(0).getName());
        assertEquals("file2", files.get(1).getName());
        assertEquals("file3", files.get(2).getName());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        CaArrayUsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        files = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(2, files.size());
        assertEquals("file2", files.get(0).getName());
        assertEquals("file3", files.get(1).getName());
        tx.commit();
    }

    @Test
    public void testGetDeletableFiles() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();
        final CaArrayFile f1 = new CaArrayFile();
        f1.setName("dummy1");
        f1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        f1.setFileStatus(FileStatus.UPLOADED);
        f1.setDataHandle(DUMMY_DATA_HANDLE);
        f1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(f1);
        DAO_OBJECT.save(f1);

        final Hybridization h1 = new Hybridization();
        h1.setName("h1");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h1);
        h1.setExperiment(DUMMY_EXPERIMENT_1);
        final DerivedArrayData arrayData = new DerivedArrayData();
        h1.addArrayData(arrayData);
        arrayData.addHybridization(h1);
        arrayData.setName("h1");
        final CaArrayFile f2 = new CaArrayFile();
        f2.setName("dummy2");
        f2.setFileType(AFFYMETRIX_DAT);
        f2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        f2.setDataHandle(DUMMY_DATA_HANDLE);
        f2.setProject(DUMMY_PROJECT_1);
        arrayData.setDataFile(f2);
        DUMMY_PROJECT_1.getFiles().add(f2);
        DAO_OBJECT.save(f2);
        DAO_OBJECT.save(arrayData);

        final CaArrayFile f3 = new CaArrayFile();
        f3.setName("dummy3");
        f3.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        f3.setFileStatus(FileStatus.IMPORTED);
        f3.setDataHandle(DUMMY_DATA_HANDLE);
        f3.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(f3);
        DAO_OBJECT.save(f3);

        h1.setExperiment(DUMMY_EXPERIMENT_1);
        final DerivedArrayData arrayData2 = new DerivedArrayData();
        h1.addArrayData(arrayData2);
        arrayData2.addHybridization(h1);
        arrayData.setName("h1");
        final CaArrayFile f4 = new CaArrayFile();
        f4.setName("dummy4");
        f4.setFileType(AFFYMETRIX_CHP);
        f4.setFileStatus(FileStatus.IMPORTED);
        f4.setProject(DUMMY_PROJECT_1);
        f4.setDataHandle(DUMMY_DATA_HANDLE);
        arrayData2.setDataFile(f4);
        DUMMY_PROJECT_1.getFiles().add(f4);
        DAO_OBJECT.save(f4);
        DAO_OBJECT.save(arrayData2);

        final CaArrayFile f5 = new CaArrayFile();
        f5.setName("dummy5");
        f5.setFileStatus(FileStatus.SUPPLEMENTAL);
        f5.setDataHandle(DUMMY_DATA_HANDLE);
        f5.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(f5);
        DAO_OBJECT.save(f5);

        final CaArrayFile f6 = new CaArrayFile();
        f6.setName("dummy6");
        f6.setFileType(AFFYMETRIX_CDF);
        f6.setFileStatus(FileStatus.IMPORTED);
        f6.setDataHandle(DUMMY_DATA_HANDLE);
        DAO_OBJECT.save(f6);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final List<CaArrayFile> files = DAO_OBJECT.getDeletableFiles(DUMMY_PROJECT_1.getId());
        assertEquals(4, files.size());
        assertEquals(f1, files.get(0));
        assertEquals(f2, files.get(1));
        assertEquals(f3, files.get(2));
        assertEquals(f5, files.get(3));
        tx.commit();
    }

    @Test
    public void testCleanupUnreferencedChildren() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        final CaArrayFile f1 = createDummyFile("dummy1", null);
        final CaArrayFile parent = createDummyFile("parent", null);
        final CaArrayFile child1 = createDummyFile("child1", parent);
        final CaArrayFile child2 = createDummyFile("child2", parent);
        DAO_OBJECT.save(f1);
        DAO_OBJECT.save(parent);
        DAO_OBJECT.save(child1);
        DAO_OBJECT.save(child2);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        List<URI> allFiles = DAO_OBJECT.getAllFileHandles();
        assertEquals(4, allFiles.size());

        DAO_OBJECT.cleanupUnreferencedChildren();

        allFiles = DAO_OBJECT.getAllFileHandles();
        assertEquals(2, allFiles.size());
        tx.commit();
    }
    
    @Test
    public void testGetPartialFile() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();
        final CaArrayFile f1 = new CaArrayFile();
        f1.setName("dummy1");
        f1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        f1.setFileStatus(FileStatus.UPLOADING);
        f1.setDataHandle(DUMMY_DATA_HANDLE);
        f1.setProject(DUMMY_PROJECT_1);
        f1.setUncompressedSize(12345);
        DUMMY_PROJECT_1.getFiles().add(f1);
        DAO_OBJECT.save(f1);
        
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        CaArrayFile file = DAO_OBJECT.getPartialFile(DUMMY_PROJECT_1.getId(), "dummy1", 12345);
        CaArrayFile file2 = DAO_OBJECT.getPartialFile(DUMMY_PROJECT_1.getId(), "dummy1", 12346);
        assertEquals(f1, file);
        assertNull(file2);
        tx.commit();
    }

    private CaArrayFile createDummyFile(String name, CaArrayFile parent) {
        CaArrayFile file = new CaArrayFile(parent);
        file.setName(name);
        file.setFileStatus(FileStatus.UPLOADED);
        file.setDataHandle(DUMMY_DATA_HANDLE);
        return file;
    }
}
