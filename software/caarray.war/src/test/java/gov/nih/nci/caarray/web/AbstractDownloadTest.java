package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractDownloadTest extends AbstractCaarrayTest {
    protected Transaction tx;
     @Before
     public void preTest() throws Exception {
         TemporaryFileCacheLocator.resetTemporaryFileCache();
         tx = HibernateUtil.beginTransaction();
     }

     @After
     public void postTest() throws Exception {
         tx.rollback();
     }
}
