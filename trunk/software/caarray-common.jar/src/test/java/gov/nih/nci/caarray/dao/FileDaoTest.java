//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
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
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Scott Miller
 *
 */
public class FileDaoTest extends AbstractDaoTest {

    private static final FileDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getFileDao();

    // Experiment
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();
    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Annotations
    private static Term DUMMY_REPLICATE_TYPE = new Term();
    private static Term DUMMY_NORMALIZATION_TYPE = new Term();
    private static Term DUMMY_QUALITY_CTRL_TYPE = new Term();
    
    private static final VocabularyDao VOCABULARY_DAO = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
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
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        setExperimentAnnotations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
    }


    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void setExperimentAnnotations() {
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

    private static void saveSupportingObjects() {
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
    }

    @Test
    public void testSaveAndRemove() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
        DUMMY_FILE_1.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        DUMMY_FILE_1.setFileType(FileType.MAGE_TAB_IDF);
        DUMMY_FILE_1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test blob".getBytes());
        DUMMY_FILE_1.writeContents(in1);

        DUMMY_FILE_1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_1);
        DAO_OBJECT.save(DUMMY_FILE_1);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.deleteHqlBlobsByProjectId(DUMMY_PROJECT_1.getId());
        tx.commit();
    }

    @Test
    public void testUnknownProjectIds() {
        Transaction tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.deleteHqlBlobsByProjectId(12345678L);
        DAO_OBJECT.deleteSqlBlobsByProjectId(12345678L);
        tx.commit();
    }

    @Test
    public void testSearchByCriteria() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        
        
        CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1.idf");
        file1.setFileType(FileType.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test idf".getBytes());
        file1.writeContents(in1);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        Hybridization h1 = new Hybridization();
        h1.setName("h1");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h1);
        h1.setExperiment(DUMMY_EXPERIMENT_1);
        RawArrayData rawArrayData = new RawArrayData();
        h1.addRawArrayData(rawArrayData);
        rawArrayData.addHybridization(h1);
        rawArrayData.setName("h1");
        CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2.cel");
        file2.setFileType(FileType.AFFYMETRIX_CEL);
        file2.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test cel".getBytes());
        file2.writeContents(in1);
        file2.setProject(DUMMY_PROJECT_1);
        rawArrayData.setDataFile(file2);
        DUMMY_PROJECT_1.getFiles().add(file2);
        DAO_OBJECT.save(file2);
        DAO_OBJECT.save(rawArrayData);        
        
        Hybridization h2 = new Hybridization();
        h2.setName("h2");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h2);
        h2.setExperiment(DUMMY_EXPERIMENT_1);
        DerivedArrayData derivedArrayData = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derivedArrayData);
        derivedArrayData.addHybridization(h2);        
        CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3.chp");
        file3.setFileType(FileType.AFFYMETRIX_CHP);
        file3.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test chp".getBytes());
        file3.writeContents(in1);
        file3.setProject(DUMMY_PROJECT_1);
        derivedArrayData.setDataFile(file3);
        DUMMY_PROJECT_1.getFiles().add(file3);
        DAO_OBJECT.save(file3);
        DAO_OBJECT.save(derivedArrayData);

        Source so1 = new Source();
        so1.setName("source");
        DUMMY_EXPERIMENT_1.getSources().add(so1);
        so1.setExperiment(DUMMY_EXPERIMENT_1);
        Sample sa1 = new Sample();
        sa1.setName("sample");
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        DUMMY_EXPERIMENT_1.getSamples().add(sa1);
        sa1.setExperiment(DUMMY_EXPERIMENT_1);
        Extract ex1 = new Extract();
        ex1.setName("extract1");
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa1);
        DUMMY_EXPERIMENT_1.getExtracts().add(ex1);
        ex1.setExperiment(DUMMY_EXPERIMENT_1);
        Extract ex2 = new Extract();
        ex2.setName("extract2");
        DUMMY_EXPERIMENT_1.getExtracts().add(ex2);
        ex2.setExperiment(DUMMY_EXPERIMENT_1);
        LabeledExtract le1 = new LabeledExtract();
        le1.setName("LE1");
        ex1.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex1);
        ex2.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex2);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le1);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le1);
        le1.setExperiment(DUMMY_EXPERIMENT_1);
        LabeledExtract le2 = new LabeledExtract();
        le2.setName("LE2");        
        ex2.getLabeledExtracts().add(le2);
        le2.getExtracts().add(ex2);
        le2.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le2);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le2);
        le2.setExperiment(DUMMY_EXPERIMENT_1);
        LabeledExtract le3 = new LabeledExtract();
        le3.setName("LE3");        
        le3.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le3);
        le3.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le3);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le3);
        le3.setExperiment(DUMMY_EXPERIMENT_1);

        CaArrayFile file4 = new CaArrayFile();
        file4.setName("file4.cdf");
        file4.setFileType(FileType.AFFYMETRIX_CDF);
        file4.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test ad".getBytes());
        file4.writeContents(in1);
        DAO_OBJECT.save(file4);

        CaArrayFile file5 = new CaArrayFile();
        file5.setName("file5.txt");
        file5.setFileStatus(FileStatus.SUPPLEMENTAL);
        ByteArrayInputStream in5 = new ByteArrayInputStream("blah blah".getBytes());
        file5.writeContents(in5);
        file5.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file5);
        DAO_OBJECT.save(file5);

        Hybridization h3 = new Hybridization();
        h3.setName("h3");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h3);
        h3.setExperiment(DUMMY_EXPERIMENT_1);
        LabeledExtract le4 = new LabeledExtract();
        le4.setName("LE4");        
        le4.getHybridizations().add(h3);
        h3.getLabeledExtracts().add(le4);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le4);
        
        CaArrayFile file6 = new CaArrayFile();
        file6.setName("file6.chp");
        file6.setFileType(FileType.AFFYMETRIX_CHP);
        file6.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test chp2".getBytes());
        file6.writeContents(in1);
        DAO_OBJECT.save(file6);


        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Experiment e = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieve(Experiment.class, DUMMY_EXPERIMENT_1.getId());
        
        PageSortParams<CaArrayFile> params = new PageSortParams<CaArrayFile>(5, 0, new AdHocSortCriterion<CaArrayFile>("name"), false);
        FileSearchCriteria criteria = new FileSearchCriteria();
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
        criteria.getTypes().add(FileType.AFFYMETRIX_CEL);
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
        UsernameHolder.setUser(STANDARD_USER);
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        
        CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setFileType(FileType.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test idf".getBytes());
        file1.writeContents(in1);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setFileType(FileType.AFFYMETRIX_CDF);
        file2.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test cdf".getBytes());
        file2.writeContents(in1);
        DAO_OBJECT.save(file2);

        CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setFileType(FileType.AFFYMETRIX_CDF);
        file3.setFileStatus(FileStatus.IMPORTED);
        in1 = new ByteArrayInputStream("test cdf".getBytes());
        file3.writeContents(in1);
        DAO_OBJECT.save(file3);

        tx.commit();

        tx = HibernateUtil.beginTransaction();
        List<CaArrayFile> files = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(3, files.size());
        assertEquals("file1", files.get(0).getName());
        assertEquals("file2", files.get(1).getName());
        assertEquals("file3", files.get(2).getName());
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        files = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(2, files.size());
        assertEquals("file2", files.get(0).getName());
        assertEquals("file3", files.get(1).getName());
        tx.commit();
    }
}
