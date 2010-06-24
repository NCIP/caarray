package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.staticinjection.CaArrayWarStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Base class for struts action tests involving downloads.
 * 
 * @author shestopalovm
 */
public abstract class AbstractDownloadTest extends AbstractBaseStrutsTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;
    
    protected Transaction tx;
    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies. 
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
    }
    
    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayWarStaticInjectionModule(), new CaArrayHibernateHelperModule());
    }
   
     @Before
     public void preTest() throws Exception {
         TemporaryFileCacheLocator.resetTemporaryFileCache();
         tx = hibernateHelper.beginTransaction();
     }

     @After
     public void postTest() throws Exception {
         tx.rollback();
     }
}
