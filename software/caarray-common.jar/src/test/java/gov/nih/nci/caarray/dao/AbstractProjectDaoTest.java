//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

public class AbstractProjectDaoTest extends AbstractDaoTest {
    protected static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");

    protected static AssayType DUMMY_ASSAYTYPE_1;
    protected static AssayType DUMMY_ASSAYTYPE_2;

    // Experiment
    protected static Organism DUMMY_ORGANISM = new Organism();
    protected static Organization DUMMY_PROVIDER = new Organization();
    protected static Project DUMMY_PROJECT_1 = new Project();
    protected static Project DUMMY_PROJECT_2 = new Project();
    protected static Project DUMMY_PROJECT_3 = new Project();
    protected static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    protected static Experiment DUMMY_EXPERIMENT_2 = new Experiment();
    protected static Experiment DUMMY_EXPERIMENT_3 = new Experiment();
    protected static TermSource DUMMY_TERM_SOURCE = new TermSource();
    protected static Category DUMMY_CATEGORY = new Category();

    // Contacts
    protected static ExperimentContact DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
    protected static Person DUMMY_PERSON = new Person();
    protected static Organization DUMMY_ORGANIZATION = new Organization();

    // Annotations
    protected static Term DUMMY_REPLICATE_TYPE = new Term();
    protected static Term DUMMY_NORMALIZATION_TYPE = new Term();
    protected static Term DUMMY_QUALITY_CTRL_TYPE = new Term();

    // Factors
    protected static Term DUMMY_FACTOR_TYPE_1 = new Term();
    protected static Term DUMMY_FACTOR_TYPE_2 = new Term();
    protected static Factor DUMMY_FACTOR_1 = new Factor();
    protected static Factor DUMMY_FACTOR_2 = new Factor();

    // Publications
    protected static Publication DUMMY_PUBLICATION_1 = new Publication();
    protected static Publication DUMMY_PUBLICATION_2 = new Publication();
    protected static Term DUMMY_PUBLICATION_STATUS = new Term();

    protected static CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
    protected static CaArrayFile DUMMY_FILE_2 = new CaArrayFile();

    protected static Source DUMMY_SOURCE;
    protected static Sample DUMMY_SAMPLE;
    protected static Extract DUMMY_EXTRACT;
    protected static LabeledExtract DUMMY_LABELED_EXTRACT;
    protected static Hybridization DUMMY_HYBRIDIZATION;
    protected static RawArrayData DUMMY_RAW_ARRAY_DATA;
    protected static CaArrayFile DUMMY_DATA_FILE;

    protected ProjectDao daoObject;
    protected VocabularyDao vocabularyDao;
    protected SearchDao searchDao;
    protected CollaboratorGroupDao collabDao;

    protected static final PageSortParams<Project> ALL_BY_ID = new PageSortParams<Project>(10000, 0,
            ProjectSortCriterion.TITLE, false);

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        this.daoObject = new ProjectDaoImpl(this.hibernateHelper, this.typeRegistry);
        this.vocabularyDao = new VocabularyDaoImpl(this.hibernateHelper);
        this.searchDao = new SearchDaoImpl(this.hibernateHelper);
        this.collabDao = new CollaboratorGroupDaoImpl(this.hibernateHelper);

        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_PROJECT_2 = new Project();
        DUMMY_PROJECT_3 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_EXPERIMENT_2 = new Experiment();
        DUMMY_EXPERIMENT_3 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();
        DUMMY_ASSAYTYPE_1 = new AssayType("aCGH");
        DUMMY_ASSAYTYPE_2 = new AssayType("Methylation");

        // Contacts
        DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
        DUMMY_PERSON = new Person();
        DUMMY_ORGANIZATION = new Organization();
        DUMMY_PERSON.setAddress(new Address());
        DUMMY_ORGANIZATION.setAddress(new Address());

        // Annotations
        DUMMY_REPLICATE_TYPE = new Term();
        DUMMY_NORMALIZATION_TYPE = new Term();
        DUMMY_QUALITY_CTRL_TYPE = new Term();

        // Factors
        DUMMY_FACTOR_TYPE_1 = new Term();
        DUMMY_FACTOR_TYPE_2 = new Term();
        DUMMY_FACTOR_1 = new Factor();
        DUMMY_FACTOR_2 = new Factor();

        // Publications
        DUMMY_PUBLICATION_1 = new Publication();
        DUMMY_PUBLICATION_2 = new Publication();
        DUMMY_PUBLICATION_STATUS = new Term();

        DUMMY_FILE_1 = new CaArrayFile();
        DUMMY_FILE_2 = new CaArrayFile();

        DUMMY_SOURCE = new Source();
        DUMMY_SAMPLE = new Sample();
        DUMMY_EXTRACT = new Extract();
        DUMMY_LABELED_EXTRACT = new LabeledExtract();
        DUMMY_HYBRIDIZATION = new Hybridization();
        DUMMY_RAW_ARRAY_DATA = new RawArrayData();
        DUMMY_DATA_FILE = new CaArrayFile();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    protected static void initializeProjects() {
        setExperimentSummary();
        setExperimentContacts();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        setExperimentAnnotations();
        setExperimentalFactors();
        setPublications();
        setFiles();
        setBioMaterials();
        setHybridizations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
        DUMMY_EXPERIMENT_1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_2.setExperiment(DUMMY_EXPERIMENT_2);
        DUMMY_EXPERIMENT_2.setProject(DUMMY_PROJECT_2);
        DUMMY_PROJECT_3.setExperiment(DUMMY_EXPERIMENT_3);
        DUMMY_EXPERIMENT_3.setProject(DUMMY_PROJECT_3);
    }

    protected static void setHybridizations() {
        DUMMY_LABELED_EXTRACT.getHybridizations().add(DUMMY_HYBRIDIZATION);
        DUMMY_HYBRIDIZATION.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_HYBRIDIZATION.addArrayData(DUMMY_RAW_ARRAY_DATA);
        DUMMY_HYBRIDIZATION.setName("Dummy Hyb");
        DUMMY_RAW_ARRAY_DATA.addHybridization(DUMMY_HYBRIDIZATION);
        DUMMY_RAW_ARRAY_DATA.setDataFile(DUMMY_DATA_FILE);
    }

    protected static void setBioMaterials() {
        DUMMY_SOURCE.setName("DummySource");
        DUMMY_SOURCE.setDescription("DummySourceDescription");
        TermBasedCharacteristic characteristic = new TermBasedCharacteristic();
        characteristic.setCategory(DUMMY_CATEGORY);
        characteristic.setTerm(DUMMY_REPLICATE_TYPE);
        DUMMY_SOURCE.getCharacteristics().add(characteristic);
        DUMMY_SOURCE.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_SAMPLE.setName("DummySample");
        DUMMY_SAMPLE.setDescription("DummySampleDescription");
        characteristic = new TermBasedCharacteristic();
        characteristic.setCategory(DUMMY_CATEGORY);
        characteristic.setTerm(DUMMY_NORMALIZATION_TYPE);
        DUMMY_SAMPLE.getCharacteristics().add(characteristic);
        DUMMY_SAMPLE.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXTRACT.setName("DummyExtract");
        DUMMY_EXTRACT.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_LABELED_EXTRACT.setName("DummyLabeledExtract");
        DUMMY_LABELED_EXTRACT.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.getSources().add(DUMMY_SOURCE);
        DUMMY_EXPERIMENT_1.getSamples().add(DUMMY_SAMPLE);
        DUMMY_EXPERIMENT_1.getExtracts().add(DUMMY_EXTRACT);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_SOURCE.getSamples().add(DUMMY_SAMPLE);
        DUMMY_SAMPLE.getExtracts().add(DUMMY_EXTRACT);
        DUMMY_EXTRACT.getLabeledExtracts().add(DUMMY_LABELED_EXTRACT);
        DUMMY_LABELED_EXTRACT.getExtracts().add(DUMMY_EXTRACT);
    }

    protected static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        final Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAYTYPE_1);
        DUMMY_EXPERIMENT_1.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
        DUMMY_EXPERIMENT_1.setPublicIdentifier("admin-00001");

        DUMMY_EXPERIMENT_2.setTitle("New DummyExperiment2");
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAYTYPE_1);
        assayTypes.add(DUMMY_ASSAYTYPE_2);
        DUMMY_EXPERIMENT_2.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_2.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_2.setPublicIdentifier("admin-00002");

        DUMMY_EXPERIMENT_3.setTitle("Ahab DummyExperiment3");
        DUMMY_EXPERIMENT_3.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_3.setManufacturer(DUMMY_PROVIDER);
        DUMMY_EXPERIMENT_3.setPublicIdentifier("admin-00003");
    }

    protected static void setExperimentContacts() {
        DUMMY_ORGANIZATION.setName("DummyOrganization1");
        DUMMY_PERSON.setFirstName("DummyFirstName1");
        DUMMY_PERSON.setLastName("DummyLastName1");
        DUMMY_PERSON.getAffiliations().add(DUMMY_ORGANIZATION);
        DUMMY_EXPERIMENT_CONTACT.setContact(DUMMY_PERSON);
        DUMMY_EXPERIMENT_1.getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
    }

    protected static void setExperimentAnnotations() {
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

    protected static void setExperimentalFactors() {
        DUMMY_FACTOR_TYPE_1.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_1.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_1.setValue("Dummy Factor Type 1");
        DUMMY_FACTOR_TYPE_2.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_2.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_2.setValue("Dummy Factor Type 2");
        DUMMY_FACTOR_1.setName("Dummy Factor 1");
        DUMMY_FACTOR_1.setType(DUMMY_FACTOR_TYPE_1);
        DUMMY_FACTOR_2.setName("Dummy Factor 2");
        DUMMY_FACTOR_2.setType(DUMMY_FACTOR_TYPE_2);
        DUMMY_EXPERIMENT_1.getFactors().add(DUMMY_FACTOR_1);
        DUMMY_EXPERIMENT_1.getFactors().add(DUMMY_FACTOR_2);
    }

    protected static void setFiles() {
        DUMMY_FILE_1.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        DUMMY_FILE_1.setFileType(FileTypeRegistry.MAGE_TAB_IDF);
        DUMMY_FILE_1.setFileStatus(FileStatus.UPLOADED);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_1);
        DUMMY_FILE_1.setProject(DUMMY_PROJECT_1);
        DUMMY_FILE_1.setDataHandle(DUMMY_HANDLE);

        DUMMY_FILE_2.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF.getName());
        DUMMY_FILE_2.setFileType(FileTypeRegistry.MAGE_TAB_SDRF);
        DUMMY_FILE_2.setFileStatus(FileStatus.SUPPLEMENTAL);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_2);
        DUMMY_FILE_2.setProject(DUMMY_PROJECT_1);
        DUMMY_FILE_2.setDataHandle(DUMMY_HANDLE);

        DUMMY_DATA_FILE.setName("dummy.cel");
        DUMMY_DATA_FILE.setFileType(new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true));
        DUMMY_DATA_FILE.setFileStatus(FileStatus.UPLOADED);
        DUMMY_DATA_FILE.setProject(DUMMY_PROJECT_1);
        DUMMY_DATA_FILE.setDataHandle(DUMMY_HANDLE);
    }

    protected static void setPublications() {
        DUMMY_PUBLICATION_1.setTitle("DummyPublicationTitle1");
        DUMMY_PUBLICATION_1.setAuthors("DummyAuthors1");
        DUMMY_PUBLICATION_1.setDoi("DummyDoi1");
        DUMMY_PUBLICATION_1.setPubMedId("DummyPubMedId1");
        DUMMY_PUBLICATION_2.setTitle("DummyPublicationTitle2");
        DUMMY_PUBLICATION_2.setAuthors("DummyAuthors2");
        DUMMY_PUBLICATION_2.setDoi("DummyDoi2");
        DUMMY_PUBLICATION_2.setPubMedId("DummyPubMedId2");

        DUMMY_PUBLICATION_STATUS.setCategory(DUMMY_CATEGORY);
        DUMMY_PUBLICATION_STATUS.setSource(DUMMY_TERM_SOURCE);
        DUMMY_PUBLICATION_STATUS.setValue("Dummy Status: Published");
        DUMMY_PUBLICATION_1.setStatus(DUMMY_PUBLICATION_STATUS);
        DUMMY_PUBLICATION_2.setStatus(DUMMY_PUBLICATION_STATUS);

        DUMMY_EXPERIMENT_1.getPublications().add(DUMMY_PUBLICATION_1);
        DUMMY_EXPERIMENT_1.getPublications().add(DUMMY_PUBLICATION_2);
    }

    protected void saveSupportingObjects() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_1.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_1.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_1.setValue("Dummy Factor Type 1");
        DUMMY_FACTOR_TYPE_2.setCategory(DUMMY_CATEGORY);
        DUMMY_FACTOR_TYPE_2.setSource(DUMMY_TERM_SOURCE);
        DUMMY_FACTOR_TYPE_2.setValue("Dummy Factor Type 2");
        this.vocabularyDao.save(DUMMY_REPLICATE_TYPE);
        this.vocabularyDao.save(DUMMY_QUALITY_CTRL_TYPE);
        this.vocabularyDao.save(DUMMY_NORMALIZATION_TYPE);
        this.vocabularyDao.save(DUMMY_FACTOR_TYPE_1);
        this.vocabularyDao.save(DUMMY_FACTOR_TYPE_2);
        this.daoObject.save(DUMMY_ASSAYTYPE_1);
        this.daoObject.save(DUMMY_ASSAYTYPE_2);
    }

}
