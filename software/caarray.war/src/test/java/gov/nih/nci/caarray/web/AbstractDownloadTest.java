package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for struts action tests involving downloads.
 * 
 * @author shestopalovm
 */
public abstract class AbstractDownloadTest extends AbstractBaseStrutsTest {
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
