//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.AbstractProjectDaoTest;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
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
        ProjectTestHelper helper = new ProjectTestHelper();
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
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
    public void testAddSample() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        ProjectTestHelper helper = new ProjectTestHelper();
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
        int initLogMessages = getLogMessages().size();

        // add sample
        Project p = ProjectTestHelper.getDummyProject();
        Sample mySample = new Sample();
        mySample.setName("mySample");
        mySample.setExperiment(p.getExperiment());
        p.getExperiment().getSamples().add(mySample);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        // check logs
        List<String> messages = getLogMessages();
        assertEquals(2, messages.size() - initLogMessages);
        assertTrue(messages.contains("Experiment " + p.getExperiment().getTitle() + ":"));
        assertTrue(messages.contains(" - Sample " + mySample.getName() + " added"));
        
        tx.commit();
    }
    
    @Test
    public void testAddedSupplementalFile() {
        Transaction tx = this.hibernateHelper.beginTransaction();

        // setup
        ProjectTestHelper helper = new ProjectTestHelper();
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
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
        ProjectTestHelper helper = new ProjectTestHelper();
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
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

    static class ProjectTestHelper extends AbstractProjectDaoTest {
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
        }
    }

}
