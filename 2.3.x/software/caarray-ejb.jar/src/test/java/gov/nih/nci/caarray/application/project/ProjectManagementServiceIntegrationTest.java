//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Integration Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ProjectManagementServiceIntegrationTest extends AbstractCaarrayTest {

    private ProjectManagementService projectManagementService;
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();
    private final GenericDataService genericDataService = new GenericDataServiceStub();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();

    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();

    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Contacts
    private static ExperimentContact DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
    private static Person DUMMY_PERSON = new Person();
    private static Organization DUMMY_ORGANIZATION = new Organization();

    private static AssayType DUMMY_ASSAY_TYPE;

    private static CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
    private static CaArrayFile DUMMY_FILE_2 = new CaArrayFile();


    @Before
    public void setUp() {
        UsernameHolder.setUser("caarrayadmin");
        this.projectManagementService = createProjectManagementService(fileAccessService, this.genericDataService);

        HibernateUtil.enableFilters(false);
        HibernateUtil.openAndBindSession();

        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_ASSAY_TYPE = new AssayType();

        DUMMY_EXPERIMENT_1 = new Experiment();

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Contacts
        DUMMY_EXPERIMENT_CONTACT = new ExperimentContact();
        DUMMY_PERSON = new Person();
        DUMMY_ORGANIZATION = new Organization();
        DUMMY_PERSON.setAddress(new Address());
        DUMMY_ORGANIZATION.setAddress(new Address());

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().save(DUMMY_ASSAY_TYPE);
        tx.commit();

    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        setExperimentContacts();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);

        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
        DUMMY_PROJECT_1.setStatus(ProposalStatus.IN_PROGRESS);

    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        DUMMY_ASSAY_TYPE.setName("Gene Expression");
        assayTypes.add(DUMMY_ASSAY_TYPE);
        DUMMY_EXPERIMENT_1.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_1.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void setExperimentContacts() {
        DUMMY_ORGANIZATION.setName("DummyOrganization1");
        DUMMY_PERSON.setFirstName("DummyFirstName1");
        DUMMY_PERSON.setLastName("DummyLastName1");
        DUMMY_PERSON.getAffiliations().add(DUMMY_ORGANIZATION);
        DUMMY_EXPERIMENT_CONTACT.setContact(DUMMY_PERSON);
        DUMMY_EXPERIMENT_1.getExperimentContacts().add(DUMMY_EXPERIMENT_CONTACT);
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

    private static ProjectManagementService createProjectManagementService(final FileAccessServiceStub fileAccessServiceStub,
            GenericDataService genericDataServiceStub) {
        ProjectManagementServiceBean bean = new ProjectManagementServiceBean();
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);

        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();

        return bean;
    }


    @Test
    public void testDeleteProject() throws Exception {

        File file1 = File.createTempFile("blob1", ".ext");
        file1.deleteOnExit();
        CaArrayFile caArrayFile1 = new CaArrayFile();
        caArrayFile1.setName("blob1.ext");
        caArrayFile1.setFileType(FileType.AFFYMETRIX_CDF);
        ByteArrayInputStream in1 = new ByteArrayInputStream(caArrayFile1.getName().getBytes());
        caArrayFile1.writeContents(in1);

        File file2 = File.createTempFile("blob2", ".ext");
        file2.deleteOnExit();
        CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName("blob2.ext");
        caArrayFile2.setFileType(FileType.AFFYMETRIX_CDF);
        ByteArrayInputStream in2 = new ByteArrayInputStream(caArrayFile2.getName().getBytes());
        caArrayFile2.writeContents(in2);

        DUMMY_PROJECT_1.getFiles().add(caArrayFile1);
        DUMMY_PROJECT_1.getFiles().add(caArrayFile2);

        caArrayFile1.setProject(DUMMY_PROJECT_1);
        caArrayFile2.setProject(DUMMY_PROJECT_1);

        Transaction t = null;
        try {
            t = HibernateUtil.beginTransaction();
            this.projectManagementService.saveProject(DUMMY_PROJECT_1);
            t.commit();

            DUMMY_PROJECT_1.setStatus(ProposalStatus.DRAFT);

            t = HibernateUtil.beginTransaction();
            this.projectManagementService.saveProject(DUMMY_PROJECT_1);
            t.commit();

            t = HibernateUtil.beginTransaction();
            this.projectManagementService.deleteProject(DUMMY_PROJECT_1);
            t.commit();

            t = HibernateUtil.beginTransaction();
            Project retrieved = this.projectManagementService.getProject(DUMMY_PROJECT_1.getId());
            t.commit();
            assertNull(retrieved);

        } catch (Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            e.printStackTrace();
            fail("unexpected error: " + e);
        }
    }


}
