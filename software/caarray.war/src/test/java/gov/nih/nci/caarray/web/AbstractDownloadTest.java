//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.staticinjection.CaArrayWarStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Base class for struts action tests involving downloads.
 * 
 * @author shestopalovm
 */
public abstract class AbstractDownloadTest extends AbstractBaseStrutsTest {
    protected static FileType AFFYMETRIX_CHP = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true, "CHP");
    protected static FileType AFFYMETRIX_CEL = new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true, "CEL");
    protected static FileType AFFYMETRIX_EXP = new FileType("AFFYMETRIX_EXP", FileCategory.DERIVED_DATA, false, "EXP");
    protected static FileType GENEPIX_GAL = new FileType("GENEPIX_GAL", FileCategory.ARRAY_DESIGN, true, "GAL");
    protected static FileType AGILENT_CSV = new FileType("AGILENT_CSV", FileCategory.ARRAY_DESIGN, false);
    protected static FileType AFFYMETRIX_CDF = new FileType("AFFYMETRIX_CDF", FileCategory.ARRAY_DESIGN, true, "CDF");

    protected FileAccessServiceStub fasStub;
    protected Transaction tx;

    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @Before
    public void init() {
        this.fasStub = new FileAccessServiceStub();
        final Injector injector = createInjector();
        final CaArrayHibernateHelper hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        this.tx = hibernateHelper.beginTransaction();
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        final DataFileHandler dataHandler = mock(DataFileHandler.class);
        when(dataHandler.getSupportedTypes()).thenReturn(
                Sets.newHashSet(AFFYMETRIX_CHP, AFFYMETRIX_CEL, AFFYMETRIX_EXP));

        final DesignFileHandler designHandler = mock(DesignFileHandler.class);
        when(designHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(AFFYMETRIX_CDF, AGILENT_CSV, GENEPIX_GAL));

        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Sets.newHashSet(dataHandler),
                Sets.newHashSet(designHandler));
        this.fasStub.setTypeRegistry(typeRegistry);

        return Guice.createInjector(new CaArrayWarStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(DataStorageFacade.class).toInstance(
                                AbstractDownloadTest.this.fasStub.createStorageFacade());
                        bind(FileTypeRegistry.class).toInstance(typeRegistry);
                        requestStaticInjection(CaArrayFile.class);
                    }
                });
    }

    @After
    public void postTest() throws Exception {
        this.tx.rollback();
    }
}
