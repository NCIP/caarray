//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.country;

import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.CountryDao;
import gov.nih.nci.caarray.dao.stub.CountryDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.country.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;


/**
 * @author John Hedden (Amentra, Inc.)
 *
 */
public class CountryServiceTest extends AbstractCaarrayTest {

    private CountryService countryService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() {
        CountryServiceBean countryServiceBean = new CountryServiceBean();
        countryServiceBean.setDaoFactory(this.daoFactoryStub);
        this.countryService = countryServiceBean;
    }

    @Test
    public void testGetCountries() {
        List<Country> countries = this.countryService.getCountries();
        assertNotNull(countries);
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalCountryDaoStub countryDao;

        @Override
        public CountryDao getCountryDao() {
            if (this.countryDao == null) {
                this.countryDao = new LocalCountryDaoStub();
            }
            return this.countryDao;
        }

    }

    private static class LocalCountryDaoStub extends CountryDaoStub {

        private final Map<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();

        @Override
        public void save(PersistentObject caArrayObject) {
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
        }
    }
}
