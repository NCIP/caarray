//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.AbstractProjectDaoTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.junit.Ignore;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * 
 * @author gax
 */
@Ignore
public class CaArrayAuditLogProcessorTest extends AbstractDaoTest {
    Group g1;
    User u1, u2;

    private void setupUsersAndGroups() {
        this.u1 = new User();
        this.u1.setLoginName("user1");
        this.u1.setFirstName("fff1");
        this.u1.setLastName("lll1");
        this.u1.setUpdateDate(new Date());
        this.u2 = new User();
        this.u2.setLoginName("user2");
        this.u2.setFirstName("fff2");
        this.u2.setLastName("lll2");
        this.u2.setUpdateDate(new Date());

        this.g1 = new Group();
        this.g1.setGroupName("group1");
        this.g1.setApplication(SecurityUtils.getApplication());
        this.g1.setUpdateDate(new Date());
        this.g1.setUsers(new HashSet<User>());
        this.g1.getUsers().add(this.u1);

    }

    @Test
    public void testProcessDetailGroupCreate() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().createQuery("delete from " + AuditLogDetail.class.getName())
                .executeUpdate();

        setupUsersAndGroups();

        this.hibernateHelper.getCurrentSession().save(this.u1);
        this.hibernateHelper.getCurrentSession().save(this.u2);
        this.hibernateHelper.getCurrentSession().save(this.g1);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final Criteria c =
                this.hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class)
                        .setProjection(Projections.property("message"));

        final List<String> l = c.list();
        assertEquals(2L, l.size());
        assertTrue(l.contains("Group group1 created"));
        assertTrue(l.contains("User user1 added to group group1"));
        tx.commit();

    }

    @Test
    public void testSampleSecurityLog() {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ProjectTestHelper helper = new ProjectTestHelper() {
        };
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
        int l = getLogMessages().size();
        final Project p = ProjectTestHelper.getDummyProject();
        p.getPublicProfile().getSampleSecurityLevels()
                .put(ProjectTestHelper.getDummySample(), SampleSecurityLevel.READ);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        int l2 = getLogMessages().size();
        assertEquals(2, l2 - l);
        tx.commit();
    }

    @Test
    public void testSampleSecurityLog_Selective_READ_SELECTIVE() {
        testSampleSecurityLog_Selective(1, SecurityLevel.READ_SELECTIVE, SampleSecurityLevel.NONE);
    }

    @Test
    public void testSampleSecurityLog_Selective_NONE() {
        testSampleSecurityLog_Selective(0, SecurityLevel.NO_VISIBILITY, SampleSecurityLevel.NONE);
    }

    @Test
    public void testDeletedFiles() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        // remove files
        Project p = ProjectTestHelper.getDummyProject();
        p.getFiles().clear();
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(3, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + p.getExperiment().getTitle() + ":"));
        assertTrue(messages.contains(" - File " + ProjectTestHelper.getDummyDataFile().getName() + " deleted"));
        assertTrue(messages.contains(" - File " + ProjectTestHelper.getDummySupplementalFile().getName() + " deleted"));
        
        tx.commit();
    }
    
    @Test
    public void updateBiomaterial() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();

        // delete/add sample
        Project p = ProjectTestHelper.getDummyProject();
        Sample mySample = new Sample();
        mySample.setName("mySample");
        mySample.setExperiment(p.getExperiment());
        ProjectTestHelper.getDummySource().getSamples().clear();
        p.getExperiment().getSamples().clear();
        p.getExperiment().getSamples().add(mySample);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(3, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + p.getExperiment().getTitle() + ":"));
        assertTrue(messages.contains(" - Sample " + mySample.getName() + " added"));
        assertTrue(messages.contains(" - Sample " + ProjectTestHelper.getDummySample().getName() + " deleted"));
        
        tx.commit();
    }
    
    @Test
    public void testAddedSupplementalFile() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        // add supplemental file
        Project p = ProjectTestHelper.getDummyProject();
        CaArrayFile supFile = new CaArrayFile();
        supFile.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF.getName());
        supFile.setFileType(FileTypeRegistry.MAGE_TAB_SDRF);
        supFile.setFileStatus(FileStatus.SUPPLEMENTAL);
        p.getFiles().add(supFile);
        supFile.setProject(p);
        supFile.setDataHandle(ProjectTestHelper.getDummyHandle());
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(2, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + p.getExperiment().getTitle() + ":"));
        assertTrue(messages.contains(" - Supplemental File " + supFile.getName() + " added"));

        tx.commit();
    }
    
    @Test
    public void testAddedArrayData() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        Project p = ProjectTestHelper.getDummyProject();
        p.setImportDescription("import description");
        RawArrayData rad = new RawArrayData();
        Hybridization h = ProjectTestHelper.getDummyHybridization();
        CaArrayFile dataFile = new CaArrayFile();
        dataFile.setName("datafile.cel");
        dataFile.setFileStatus(FileStatus.UPLOADED);
        dataFile.setFileType(new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true));
        dataFile.setDataHandle(ProjectTestHelper.getDummyHandle());
        dataFile.setProject(p);

        h.addArrayData(rad);
        rad.addHybridization(h);
        rad.setDataFile(dataFile);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(3, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + p.getExperiment().getTitle() + ":"));
        assertTrue(messages.contains("Import Description: " + p.getImportDescription()));
        assertTrue(messages.contains(" - Data File " + dataFile.getName() + " added"));

        tx.commit();
    }

    @Test
    public void updateExperiment() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        // update experiment fields
        Project p = ProjectTestHelper.getDummyProject();
        Experiment e = p.getExperiment();
        e.setTitle("title new");
        e.setDescription("description new");
        e.getAssayTypes().add(ProjectTestHelper.getDummyAssayType2());
        e.setManufacturer(ProjectTestHelper.getDummyOrganization2());
        e.getArrayDesigns().add(ProjectTestHelper.getDummyArrayDesign());
        e.setOrganism(ProjectTestHelper.getDummyOrganism2());
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(7, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + e.getTitle() + ":"));
        assertTrue(messages.contains(" - Title updated"));
        assertTrue(messages.contains(" - Description updated"));
        assertTrue(messages.contains(" - Assay Types updated"));
        assertTrue(messages.contains(" - Provider updated"));
        assertTrue(messages.contains(" - Array Designs updated"));
        assertTrue(messages.contains(" - Organism updated"));

        tx.commit();
    }

    @Test
    public void updateContacts() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        // add contact
        Project p = ProjectTestHelper.getDummyProject();
        Experiment e = p.getExperiment();
        Person person = new Person();
        person.setFirstName("test");
        person.setLastName("user");
        ExperimentContact contact = new ExperimentContact();
        contact.setPerson(person);
        e.getExperimentContacts().add(contact);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(2, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + e.getTitle() + ":"));
        assertTrue(messages.contains(" - Contact user, test added"));

        tx.commit();
    }
    
    @Test
    public void updatePublications() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        setupHelper();
        int initLogMessages = getLogMessages().size();
        
        // add publication
        Project p = ProjectTestHelper.getDummyProject();
        Experiment e = p.getExperiment();
        Publication publication = new Publication();
        publication.setTitle("testPublication");
        e.getPublications().add(publication);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(2, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + e.getTitle() + ":"));
        assertTrue(messages.contains(" - Publication testPublication added"));

        tx.commit();
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getLogMessages() {
        Criteria c = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class);
        c.setProjection(Projections.property("message"));
        return c.list();

    }
    
    private void testSampleSecurityLog_Selective(int expectedCount, SecurityLevel projectLevel,
            SampleSecurityLevel sampleLevel) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ProjectTestHelper helper = new ProjectTestHelper() {
        };
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
        int l = getLogMessages().size();
        final Project p = ProjectTestHelper.getDummyProject();

        p.getPublicProfile().getSampleSecurityLevels().put(ProjectTestHelper.getDummySample(), sampleLevel);
        p.getPublicProfile().setSecurityLevel(projectLevel);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        int l2 = getLogMessages().size();
        assertEquals(expectedCount, l2 - l);
        tx.commit();
    }

    private void setupHelper() {
        ProjectTestHelper helper = new ProjectTestHelper();
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
    }

    static class ProjectTestHelper extends AbstractProjectDaoTest {
        static final Organization DUMMY_ORGANIZATION2 = new Organization();
        static final Organism DUMMY_ORGANISM2 = new Organism();
        static final ArrayDesign DUMMY_ARRAY_DESIGN = new ArrayDesign();
        static final CaArrayFile DUMMY_DESIGN_FILE = new CaArrayFile();

        static Source getDummySource() {
            return AbstractProjectDaoTest.DUMMY_SOURCE;
        }
        
        static Sample getDummySample() {
            return AbstractProjectDaoTest.DUMMY_SAMPLE;
        }

        static Project getDummyProject() {
            return AbstractProjectDaoTest.DUMMY_PROJECT_1;
        }
        
        static CaArrayFile getDummySupplementalFile() {
            return DUMMY_FILE_2;
        }
        
        static CaArrayFile getDummyDataFile() {
            return DUMMY_DATA_FILE;
        }
        
        static CaArrayFile getDummyOtherFile() {
            return DUMMY_FILE_1;
        }

        static URI getDummyHandle() {
            return DUMMY_HANDLE;
        }
        
        static Hybridization getDummyHybridization() {
            return DUMMY_HYBRIDIZATION;
        }

        static AssayType getDummyAssayType2() {
            return DUMMY_ASSAYTYPE_2;
        }
        
        static Organism getDummyOrganism2() {
            return DUMMY_ORGANISM2;
        }
        
        static Organization getDummyOrganization2() {
            return DUMMY_ORGANIZATION2;
        }
        
        static ArrayDesign getDummyArrayDesign() {
            return DUMMY_ARRAY_DESIGN;
        }

        void saveStuff() {
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
        }
        
        void deleteStuff(PersistentObject persistentObject) {
            this.daoObject.remove(persistentObject);
        }

        @Override
        public void setup() {
            baseIntegrationSetUp();
            super.setup();
            DUMMY_PROJECT_1.getFiles().add(DUMMY_DATA_FILE);
            DUMMY_DATA_FILE.setProject(DUMMY_PROJECT_1);
            DUMMY_DATA_FILE.setFileStatus(FileStatus.IMPORTED);
            DUMMY_ORGANISM2.setScientificName("organism2");
            DUMMY_ORGANISM2.setTermSource(DUMMY_TERM_SOURCE);
            DUMMY_ORGANIZATION2.setName("organization2");
            DUMMY_DESIGN_FILE.setFileStatus(FileStatus.IMPORTED);
            DUMMY_DESIGN_FILE.setDataHandle(DUMMY_HANDLE);
            DUMMY_ARRAY_DESIGN.setName("dummy design");
            DUMMY_ARRAY_DESIGN.setOrganism(DUMMY_ORGANISM);
            DUMMY_ARRAY_DESIGN.setProvider(DUMMY_ORGANIZATION);
            DUMMY_ARRAY_DESIGN.setTechnologyType(DUMMY_REPLICATE_TYPE);
            DUMMY_ARRAY_DESIGN.setVersion("1");
            DUMMY_ARRAY_DESIGN.addDesignFile(DUMMY_DESIGN_FILE);
            DUMMY_ARRAY_DESIGN.getAssayTypes().add(DUMMY_ASSAYTYPE_1);
        }
    }

}
