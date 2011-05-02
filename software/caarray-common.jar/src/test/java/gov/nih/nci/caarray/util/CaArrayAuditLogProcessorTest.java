package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.AbstractProjectDaoTest;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;

/**
 * 
 * @author gax
 */
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
        final List<AuditLogDetail> l =
                this.hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        final Project p = ProjectTestHelper.getDummyProject();
        p.getPublicProfile().getSampleSecurityLevels()
                .put(ProjectTestHelper.getDummySample(), SampleSecurityLevel.READ);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        final List<AuditLogDetail> l2 =
                this.hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(2L, l2.size() - l.size());
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

    private void testSampleSecurityLog_Selective(int expectedCount, SecurityLevel projectLevel,
            SampleSecurityLevel sampleLevel) {
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final ProjectTestHelper helper = new ProjectTestHelper() {
        };
        helper.setup();
        helper.saveStuff();
        this.hibernateHelper.getCurrentSession().flush();
        final List<AuditLogDetail> l =
                this.hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        final Project p = ProjectTestHelper.getDummyProject();

        p.getPublicProfile().getSampleSecurityLevels().put(ProjectTestHelper.getDummySample(), sampleLevel);
        p.getPublicProfile().setSecurityLevel(projectLevel);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(p);
        this.hibernateHelper.getCurrentSession().flush();

        final List<AuditLogDetail> l2 =
                this.hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(expectedCount, l2.size() - l.size());
        tx.commit();
    }

    static class ProjectTestHelper extends AbstractProjectDaoTest {
        static Sample getDummySample() {
            return AbstractProjectDaoTest.DUMMY_SAMPLE;
        }

        static Project getDummyProject() {
            return AbstractProjectDaoTest.DUMMY_PROJECT_1;
        }

        void saveStuff() {
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
        }

        @Override
        public void setup() {
            baseIntegrationSetUp();
            super.setup();
        }
    }

}