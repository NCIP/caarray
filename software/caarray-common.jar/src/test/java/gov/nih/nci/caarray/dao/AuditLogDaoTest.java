//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.audit.AuditLogSecurity;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.domain.search.AuditLogSortCriterion;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 *
 * @author gax
 */
public class AuditLogDaoTest extends AbstractProjectDaoTest {
    private Transaction tx;
    private AuditLogRecord r1, r2;
    private AuditLogDetail d1, d2;
    private AuditLogSecurity s1, s2;
    private PageSortParams<AuditLogRecord> sort;

    @SuppressWarnings("deprecation")
    @Before
    public void setup() {
        super.setup();
        tx = this.hibernateHelper.beginTransaction();
        saveSupportingObjects();
        daoObject.save(DUMMY_PROJECT_1);
        daoObject.save(DUMMY_PROJECT_2);
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogSecurity.class.getName()).executeUpdate();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        r1 = new AuditLogRecord(AuditType.UPDATE, "foo", 1L, "user1", new Date(1L));
        d1 = new AuditLogDetail(r1, "bar", null, null);
        s1 = new AuditLogSecurity(AuditLogSecurity.PROJECT, DUMMY_PROJECT_1.getId(), 3L, r1);
        d1.setMessage("bla bla bla aaa bla bla bla");
        r1.getDetails().add(d1);

        r2 = new AuditLogRecord(AuditType.UPDATE, "foo", 2L, "user2", new Date());
        d2 = new AuditLogDetail(r2, "bar", null, null);
        s2 = new AuditLogSecurity(AuditLogSecurity.PROJECT, DUMMY_PROJECT_2.getId(), 3L, r2);
        d2.setMessage("bla bla bla bbb bla bla bla");
        r2.getDetails().add(d2);

        hibernateHelper.getCurrentSession().save(r1);
        hibernateHelper.getCurrentSession().save(r2);
        hibernateHelper.getCurrentSession().save(s1);
        hibernateHelper.getCurrentSession().save(s2);

        sort = new PageSortParams<AuditLogRecord>(20, 0, Collections.<SortCriterion<AuditLogRecord>>emptyList(),
                false);
        sort.setSortCriterion(AuditLogSortCriterion.DATE);
    }

    @After
    public void cleanup() {
        hibernateHelper.getCurrentSession().flush();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogSecurity.class.getName()).executeUpdate();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
        hibernateHelper.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
        tx.commit();
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testSortRecords() {
        AuditLogDaoImpl instance = new AuditLogDaoImpl(hibernateHelper);
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();

        assertEquals(2, instance.getRecordsCount(cr));
        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(2, l.size());

        // test sorting
        sort.setDesc(!sort.isDesc());
        sort.setSortCriterion(AuditLogSortCriterion.DATE);
        List<AuditLogRecord> l2 = instance.getRecords(cr, sort);
        assertEquals(l.get(0), l2.get(1));
        assertEquals(l.get(1), l2.get(0));
    }

    @Test
    public void testUsername() {
        AuditLogDaoImpl instance = new AuditLogDaoImpl(hibernateHelper);
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();
        cr.setUsername("user1");
        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(1, l.size());
        assertEquals(r1.getId(), l.get(0).getId());
    }

    @Test
    public void testMessage() {
        AuditLogDaoImpl instance = new AuditLogDaoImpl(hibernateHelper);
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();
        
        cr.setMessage("bbb");
        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(1, l.size());
        assertTrue(l.get(0).getDetails().iterator().next().getMessage().contains("bbb"));
        assertEquals(r2.getId(), l.get(0).getId());
    }

    @Test
    public void regressiongForge21514_recordShownTooManyTimesFix(){
        hibernateHelper.getCurrentSession().flush();
        AuditLogDetail d3 = new AuditLogDetail(r1, "bar", null, null);
        d3.setMessage("bla bla bla ccc bla bla bla");
        r1.getDetails().add(d3);
        hibernateHelper.getCurrentSession().update(r1);

        AuditLogDaoImpl instance = new AuditLogDaoImpl(hibernateHelper);
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();

        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(2, l.size());
        assertEquals(2, l.get(0).getDetails().size());
    }

    @Test
    public void testSecurity() throws Exception {
        AuditLogDaoImpl instance = new AuditLogDaoImpl(hibernateHelper);
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();
        List<AuditLogRecord> l = null;
        Group group = null;

        try {
            final AuthorizationManager am = SecurityUtils.getAuthorizationManager();
            group = new Group();
            group.setGroupName("Foo");
            group.setGroupDesc("Collaborator Group");
            group.setApplication(SecurityUtils.getApplication());
            am.createGroup(group);
            final String[] groupMembers = { am.getUser("caarrayuser").getUserId().toString() };
            am.assignUsersToGroup(group.getGroupId().toString(), groupMembers);
            
            CollaboratorGroup cg = new CollaboratorGroup(group, CaArrayUsernameHolder.getCsmUser());
            collabDao.save(cg);
            Project p = searchDao.retrieve(Project.class, DUMMY_PROJECT_1.getId());
            final AccessProfile groupProfile = p.addGroupProfile(cg);
            groupProfile.setSecurityLevel(SecurityLevel.READ);
            tx.commit();

            // no permissions
            CaArrayUsernameHolder.setUser("labadministrator");
            tx = hibernateHelper.beginTransaction();
            l = instance.getRecords(cr, sort);
            assertEquals(0, l.size());
            tx.commit();

            // collaborator
            // should not see any permission records
            CaArrayUsernameHolder.setUser("caarrayuser");
            tx = hibernateHelper.beginTransaction();
            l = instance.getRecords(cr, sort);
            assertEquals(1, l.size());
            tx.commit();
            
            // project owner
            // sees both dummy records, project access update
            CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);
            tx = hibernateHelper.beginTransaction();
            l = instance.getRecords(cr, sort);
            assertEquals(3, l.size());
            tx.commit();

            // sysadmin
            // sees both dummy records, project access update, collab group update
            CaArrayUsernameHolder.setUser("systemadministrator");
            tx = hibernateHelper.beginTransaction();
            l = instance.getRecords(cr, sort);
            assertEquals(4, l.size());

        } finally {
            if (group != null && group.getGroupId() != null) {
                SecurityUtils.getAuthorizationManager().removeGroup(group.getGroupId().toString());
            }
        }



    }
}
