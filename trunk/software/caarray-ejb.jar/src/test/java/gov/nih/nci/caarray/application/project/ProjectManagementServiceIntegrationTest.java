//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Integration Test class for ProjectManagementService subsystem.
 */
@SuppressWarnings("PMD")
public class ProjectManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
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

    @Before
    public void setUp() {
        this.projectManagementService = createProjectManagementService(fileAccessService, this.genericDataService);

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
        DUMMY_PROJECT_1.setLocked(false);

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

    private static ProjectManagementService createProjectManagementService(
            final FileAccessServiceStub fileAccessServiceStub, GenericDataService genericDataServiceStub) {
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

        TemporaryFileCacheLocator
                .setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
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
        caArrayFile1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream(caArrayFile1.getName().getBytes());
        caArrayFile1.writeContents(in1);

        File file2 = File.createTempFile("blob2", ".ext");
        file2.deleteOnExit();
        CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName("blob2.ext");
        caArrayFile2.setFileType(FileType.AFFYMETRIX_CDF);
        caArrayFile2.setFileStatus(FileStatus.UPLOADED);
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

            t = HibernateUtil.beginTransaction();
            DUMMY_PROJECT_1 = (Project) HibernateUtil.getCurrentSession().load(Project.class, DUMMY_PROJECT_1.getId());
            DUMMY_PROJECT_1.setLocked(false);
            HibernateUtil.getCurrentSession().refresh(DUMMY_PROJECT_1);
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
            throw e;
        }
    }

    @Test
    public void testChangeProjectOwner() throws Exception {
        UsernameHolder.setUser(STANDARD_USER);
        Transaction tx = HibernateUtil.beginTransaction();
        this.projectManagementService.saveProject(DUMMY_PROJECT_1);
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        assertNotNull(DUMMY_PROJECT_1.getId());
        Set<User> owners = DUMMY_PROJECT_1.getOwners();
        assertEquals(1, owners.size());
        assertEquals(STANDARD_USER, owners.iterator().next().getLoginName());
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        this.projectManagementService.changeOwner(DUMMY_PROJECT_1.getId(), "caarrayuser");
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().clear();
        UsernameHolder.setUser("caarrayuser");
        Project retrieved = this.projectManagementService.getProject(DUMMY_PROJECT_1.getId());
        assertNotNull(retrieved);
        assertNotNull(retrieved.getExperiment());
        assertEquals(DUMMY_PROJECT_1.getExperiment().getTitle(), retrieved.getExperiment().getTitle());
        owners = retrieved.getOwners();
        assertEquals(1, owners.size());
        assertEquals("caarrayuser", owners.iterator().next().getLoginName());
        tx.commit();
    }    
    
    @Test(expected = PermissionDeniedException.class)
    public void testDeleteUnownedProject() throws Exception {
        UsernameHolder.setUser("caarrayuser");
        Transaction tx = HibernateUtil.beginTransaction();
        this.projectManagementService.saveProject(DUMMY_PROJECT_1);
        tx.commit();
        
        UsernameHolder.setUser(STANDARD_USER);
        tx = HibernateUtil.beginTransaction();
        this.projectManagementService.deleteProject(DUMMY_PROJECT_1);
        tx.commit();
    }
}
