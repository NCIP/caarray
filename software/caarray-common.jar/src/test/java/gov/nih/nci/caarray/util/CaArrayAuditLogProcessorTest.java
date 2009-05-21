package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
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
        Transaction tx = HibernateUtil.beginTransaction();
        
        setupUsersAndGroups();
    
        HibernateUtil.getCurrentSession().save(u1);
        HibernateUtil.getCurrentSession().save(u2);
        HibernateUtil.getCurrentSession().save(g1);
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        List<AuditLogDetail> l = HibernateUtil.getCurrentSession().createCriteria(AuditLogDetail.class).list();
        assertEquals(2L, l.size());
        tx.commit();

    
    }

    @Test
    public void testSampleSecurityLog() {
        Transaction tx = HibernateUtil.beginTransaction();        
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