//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.staticinjection.CaArrayCommonStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Base class for tests that require hibernate backed by an actual database. Handles setting up hibernate and cleaning
 * up the database when done.
 * 
 * @author Steve Lustbader
 */
public abstract class AbstractHibernateTest extends AbstractCaarrayTest {
    protected static final FileType AFFYMETRIX_CEL = new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true);
    protected static final FileType AFFYMETRIX_CHP = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true);
    protected static final FileType AFFYMETRIX_DAT = new FileType("AFFYMETRIX_DAT", FileCategory.RAW_DATA, false);
    protected static final FileType AFFYMETRIX_CDF = new FileType("AFFYMETRIX_CDF", FileCategory.ARRAY_DESIGN, true);

    protected FileTypeRegistry typeRegistry;
    protected Injector injector;
    protected CaArrayHibernateHelper hibernateHelper;
    private final boolean enableFilters;

    /**
     * Subclasses can override this to configure a custom injector, e.g. by overriding some modules with stubbed out
     * functionality.
     * 
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        return Guice.createInjector(new CaArrayCommonStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(FileTypeRegistry.class).toInstance(AbstractHibernateTest.this.typeRegistry);
                        requestStaticInjection(CaArrayFile.class);
                    }
                });
    }
    
    /**
     * Called during test tear down. If necessary subclasses can override this to undo 
     * the effects of createInjector() so it does not affect subsequent tests. 
     */
    protected void levelsetInjector() {
        // NOP
    }

    protected AbstractHibernateTest(boolean enableFilters) {
        this.enableFilters = enableFilters;
    }

    @Before
    public void baseIntegrationSetUp() {
        System.out.println("Creating injector");
        final DataFileHandler affyDataHandler = mock(DataFileHandler.class);
        when(affyDataHandler.getSupportedTypes()).thenReturn(
                Sets.newHashSet(AFFYMETRIX_CEL, AFFYMETRIX_CHP, AFFYMETRIX_DAT));
        final DesignFileHandler affyDesignHandler = mock(DesignFileHandler.class);
        when(affyDesignHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(AFFYMETRIX_CDF));
        this.typeRegistry = new FileTypeRegistryImpl(Sets.newHashSet(affyDataHandler),
                Sets.newHashSet(affyDesignHandler));

        this.injector = createInjector();
        this.hibernateHelper = this.injector.getInstance(CaArrayHibernateHelper.class);
        System.out.println("Creating hibernate helper: " + this.hibernateHelper);
        assertNotNull(this.hibernateHelper);

        CaArrayUsernameHolder.setUser(AbstractCaarrayTest.STANDARD_USER);

        this.hibernateHelper.setFiltersEnabled(this.enableFilters);
        this.hibernateHelper.openAndBindSession();
    }

    @After
    public void baseIntegrationTearDown() {
        try {
            final Transaction tx = this.hibernateHelper.getCurrentSession().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (final HibernateException e) {
            // ok - there was no active transaction
        }
        this.hibernateHelper.unbindAndCleanupSession();
        HibernateIntegrationTestCleanUpUtility.cleanUp();
        levelsetInjector();
    }

}
