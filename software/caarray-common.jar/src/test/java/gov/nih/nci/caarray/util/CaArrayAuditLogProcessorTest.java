package gov.nih.nci.caarray.util;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.AbstractProjectDaoTest;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author gax
 */
public class CaArrayAuditLogProcessorTest extends AbstractCaarrayTest {
    private Transaction tx;
    Group g1;
    User u1, u2;
    @BeforeClass
    public static void init() {
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
        tx.commit();
    }

    @Before
    public void setup() {
        UsernameHolder.setUser(STANDARD_USER);
        HibernateUtil.enableFilters(true);
        HibernateUtil.openAndBindSession();
        tx = HibernateUtil.beginTransaction();
   }


    @After
    public void tearDown() {
        try {
            HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
            HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
            tx.commit();
            HibernateUtil.unbindAndCleanupSession();
            HibernateIntegrationTestCleanUpUtility.cleanUp();
            
            
        } catch (HibernateException e) {
            // ok - there was no active transaction
        }
    }

    @Test
    public void testProcessDetailGroupCreate() {

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
        g1.setUsers(new HashSet());
        g1.getUsers().add(u1);

    
        HibernateUtil.getCurrentSession().save(u1);
        HibernateUtil.getCurrentSession().save(u2);
        HibernateUtil.getCurrentSession().save(g1);
        HibernateUtil.getCurrentSession().flush();
        List<AuditLogDetail> l = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(2L, l.size());

    
    }

    @Test
    public void testProcessDetailGroupChange() {
        g1 = (Group) HibernateUtil.getCurrentSession().createCriteria(Group.class).add(Restrictions.eq("groupName", "group1")).uniqueResult();
        u1 = (User) HibernateUtil.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("loginName", "user1")).uniqueResult();
        u2 = (User) HibernateUtil.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("loginName", "user2")).uniqueResult();
        g1.getUsers().add(u2);
        HibernateUtil.getCurrentSession().saveOrUpdate(g1);
        HibernateUtil.getCurrentSession().flush();
        assertEquals(2L, g1.getUsers().size());        

        List<AuditLogDetail> l = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(1L, l.size());
        
        assertEquals(2L, g1.getUsers().size());
        assertTrue(g1.getUsers().remove(u1));
        HibernateUtil.getCurrentSession().saveOrUpdate(g1);
        HibernateUtil.getCurrentSession().flush();
        l = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(2L, l.size());        
    }

    @Test
    public void testSampleSecurityLog() {
        
        ProjectTestHelper helper = new ProjectTestHelper(){};
        helper.setup();
        helper.saveStuff();
        HibernateUtil.getCurrentSession().flush();
        List<AuditLogDetail> l = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        Project p = helper.getDummyProject();
        p.getPublicProfile().getSampleSecurityLevels().put(helper.getDummySample(), SampleSecurityLevel.READ);
        HibernateUtil.getCurrentSession().saveOrUpdate(p);
        HibernateUtil.getCurrentSession().flush();

        List<AuditLogDetail> l2 = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(1L, l2.size() - l.size());
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