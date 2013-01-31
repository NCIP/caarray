//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.AuditLogSearchCriteria;
import gov.nih.nci.caarray.domain.search.AuditLogSortCriterion;
import gov.nih.nci.caarray.util.HibernateUtil;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class AuditLogDaoTest extends AbstractDaoTest {

    private Transaction tx;
    private AuditLogRecord r1, r2;
    private AuditLogDetail d1, d2;
    private PageSortParams<AuditLogRecord> sort;

    @BeforeClass
    public static void init() {
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
        tx.commit();
    }

    @Before
    public void setup() {
        tx = HibernateUtil.beginTransaction();
        r1 = new AuditLogRecord(AuditType.UPDATE, "foo", 1L, "user1", new Date(1L));
        d1 = new AuditLogDetail(r1, "bar", null, null);
        d1.setMessage("bla bla bla aaa bla bla bla");
        r1.getDetails().add(d1);

        r2 = new AuditLogRecord(AuditType.UPDATE, "foo", 2L, "user2", new Date());
        d2 = new AuditLogDetail(r2, "bar", null, null);
        d2.setMessage("bla bla bla bbb bla bla bla");
        r2.getDetails().add(d2);

        HibernateUtil.getCurrentSession().save(r1);
        HibernateUtil.getCurrentSession().save(r2);

        sort = new PageSortParams<AuditLogRecord>(20, 0, Collections.EMPTY_LIST, false);
        sort.setSortCriterion(AuditLogSortCriterion.DATE);
    }

    @After
    public void cleanup() {
        HibernateUtil.getCurrentSession().flush();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogDetail.class.getName()).executeUpdate();
        HibernateUtil.getCurrentSession().createQuery("delete from "+AuditLogRecord.class.getName()).executeUpdate();
        tx.commit();
    }
    
    @Test
    public void testSortRecords() {

        AuditLogDaoImpl instance = new AuditLogDaoImpl();
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
        AuditLogDaoImpl instance = new AuditLogDaoImpl();
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();
        cr.setUsername("user1");
        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(1, l.size());
        assertEquals(r1.getId(), l.get(0).getId());
    }

    @Test
    public void testMessage() {
        AuditLogDaoImpl instance = new AuditLogDaoImpl();
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();
        
        cr.setMessage("bbb");
        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(1, l.size());
        assertTrue(l.get(0).getDetails().iterator().next().getMessage().contains("bbb"));
        assertEquals(r2.getId(), l.get(0).getId());

    }

    @Test
    public void regressiongForge21514_recordShownTooManyTimesFix(){
        AuditLogDetail d3 = new AuditLogDetail(r1, "bar", null, null);
        d3.setMessage("bla bla bla ccc bla bla bla");
        r1.getDetails().add(d3);
        HibernateUtil.getCurrentSession().update(r1);

        AuditLogDaoImpl instance = new AuditLogDaoImpl();
        AuditLogSearchCriteria cr = new AuditLogSearchCriteria();

        List<AuditLogRecord> l = instance.getRecords(cr, sort);
        assertEquals(2, l.size());
        assertEquals(2, l.get(0).getDetails().size());
    }

}
