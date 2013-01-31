//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceBean;
import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.project.Project;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Test for the persistent object type converter.
 * @author Scott Miller
 */
public class PersistentObjectTypeConverterTest extends AbstractCaarrayTest {

    @Test
    public void testConverter() {
        PersistentObjectTypeConverter converter = new PersistentObjectTypeConverter();
        PersistentObjectTypeConverter.setServiceLocator(new MockServiceLocator());

        Project p = (Project) converter.convertFromString(null, new String[] {"2"}, Project.class);
        assertEquals(null, p);

        p = (Project) converter.convertFromString(null, new String[] {"1"}, Project.class);
        assertEquals("FOO", p.getCaBigId());

        String idString = converter.convertToString(null, p);
        assertEquals("1", idString);

        idString = converter.convertToString(null, new Project());
        assertEquals(null, idString);

        assertNull(converter.convertFromString(null, new String[] {null }, Project.class));
        assertNull(converter.convertFromString(null, new String[] {" " }, Project.class));
        assertNull(converter.convertFromString(null, new String[] {"foo" }, Project.class));
        assertNull(converter.convertToString(null, String.class));
    }

    private final class MockServiceLocator implements ServiceLocator {

        private static final long serialVersionUID = 1L;
        private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

        /**
         * {@inheritDoc}
         */
        public Object lookup(String jndiName) {
            if (GenericDataService.JNDI_NAME.equals(jndiName)) {
                GenericDataServiceBean serviceBean = new GenericDataServiceBean();
                serviceBean.setDaoFactory(this.daoFactoryStub);
                return serviceBean;
            }
            return null;
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        LocalSearchDaoStub searchDao;

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            if (this.searchDao == null) {
                this.searchDao = new LocalSearchDaoStub();
            }
            return this.searchDao;
        }
    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings({ "unchecked", "deprecation" })
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            if (Project.class.equals(entityClass) && Long.valueOf(1L).equals(entityId)) {
                Project p = new Project();
                p.setCaBigId("FOO");
                p.setId(1l);
                return (T) p;
            }
            return null;
        }
    }
}
