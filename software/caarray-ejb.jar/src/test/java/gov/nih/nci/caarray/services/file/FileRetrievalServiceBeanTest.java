//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.file;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 *
 */
public class FileRetrievalServiceBeanTest extends AbstractCaarrayTest {



    private final FileRetrievalServiceBean bean = new FileRetrievalServiceBean();
    private final LocalFileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub serviceLocatorStub = ServiceLocatorStub.registerEmptyLocator();
        serviceLocatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        serviceLocatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        this.bean.setDaoFactory(this.daoFactoryStub);
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.services.file.FileRetrievalServiceBean#readFile(gov.nih.nci.caarray.domain.file.CaArrayFile)}.
     */
    @Test
    public void testReadFile() {
        CaArrayFile caArrayFile = new CaArrayFile();
        byte[] bytes = this.bean.readFile(caArrayFile);
        assertEquals(AffymetrixArrayDesignFiles.TEST3_CDF.length(), (long) bytes.length);
    }

    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {

        @Override
        public File getFile(CaArrayFile caArrayFile) {
            return AffymetrixArrayDesignFiles.TEST3_CDF;
        }

    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub () {

                @Override
                public List<AbstractCaArrayObject> query(final AbstractCaArrayObject entityToMatch) {
                    List<AbstractCaArrayObject> list = new ArrayList<AbstractCaArrayObject>();
                    list.add(entityToMatch);
                    return list;
                };

            };
        }
    }
}
