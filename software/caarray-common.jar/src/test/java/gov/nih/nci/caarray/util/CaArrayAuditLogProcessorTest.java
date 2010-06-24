package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.dao.AbstractDaoTest;
import gov.nih.nci.caarray.dao.AbstractProjectDaoTest;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

/**
 *
 * @author gax
 */
public class CaArrayAuditLogProcessorTest extends AbstractDaoTest {
    Group g1;
    User u1, u2;
    
    private void setupUsersAndGroups() {
        u1 = new User();
        u1.setLoginName("user1");
        u1.setFirstName("fff1"); u1.setLastName("lll1");
        u1.setUpdateDate(new Date());
        u2 = new User();
        u2.setLoginName("user2");
        u2.setFirstName("fff2"); u2.setLastName("lll2");
        u2.setUpdateDate(new Date());

        g1 = new Group();
        g1.setGroupName("group1");
        g1.setApplication(SecurityUtils.getApplication());
        g1.setUpdateDate(new Date());
        g1.setUsers(new HashSet<User>());
        g1.getUsers().add(u1);
        
    }

    @Test
    public void testProcessDetailGroupCreate() {
        Transaction tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().createQuery("delete from " + AuditLogDetail.class.getName()).executeUpdate();

        
        setupUsersAndGroups();
    
        hibernateHelper.getCurrentSession().save(u1);
        hibernateHelper.getCurrentSession().save(u2);
        hibernateHelper.getCurrentSession().save(g1);
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        Criteria c = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class)
                .setProjection(Projections.property("message"));

        List<String> l = c.list();
        assertEquals(2L, l.size());
        assertTrue(l.contains("Group group1 created"));
        assertTrue(l.contains("User user1 added to group group1"));
        tx.commit();

    
    }

    @Test
    public void testSampleSecurityLog() {
        Transaction tx = hibernateHelper.beginTransaction();
        ProjectTestHelper helper = new ProjectTestHelper(){};
        helper.setup();
        ProjectTestHelper.saveStuff();
        hibernateHelper.getCurrentSession().flush();
        List<AuditLogDetail> l = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        Project p = ProjectTestHelper.getDummyProject();
        p.getPublicProfile().getSampleSecurityLevels().put(ProjectTestHelper.getDummySample(), SampleSecurityLevel.READ);
        hibernateHelper.getCurrentSession().saveOrUpdate(p);
        hibernateHelper.getCurrentSession().flush();

        List<AuditLogDetail> l2 = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
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


    private void testSampleSecurityLog_Selective(int expectedCount, SecurityLevel projectLevel, SampleSecurityLevel sampleLevel) {
        Transaction tx = hibernateHelper.beginTransaction();
        ProjectTestHelper helper = new ProjectTestHelper(){};
        helper.setup();
        ProjectTestHelper.saveStuff();
        hibernateHelper.getCurrentSession().flush();
        List<AuditLogDetail> l = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        Project p = ProjectTestHelper.getDummyProject();

        p.getPublicProfile().getSampleSecurityLevels().put(ProjectTestHelper.getDummySample(), sampleLevel);
        p.getPublicProfile().setSecurityLevel(projectLevel);
        hibernateHelper.getCurrentSession().saveOrUpdate(p);
        hibernateHelper.getCurrentSession().flush();

        List<AuditLogDetail> l2 = hibernateHelper.getCurrentSession().createCriteria(AuditLogDetail.class).list();
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
        static void saveStuff() {
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
        }
    }

}