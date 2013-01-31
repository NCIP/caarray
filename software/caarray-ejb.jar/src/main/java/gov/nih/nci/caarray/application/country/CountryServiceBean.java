//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.country;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.CountryDao;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.util.Collections;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

/**
 * @author John Hedden
 *
 */
@Local(CountryService.class)
@Stateless
public class CountryServiceBean implements CountryService {

    private static final Logger LOG = Logger.getLogger(CountryServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public List<Country> getCountries() {
        LogUtil.logSubsystemEntry(LOG);
        List<Country> result = getCountryDao().getCountries();
        Collections.sort(result);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private CountryDao getCountryDao() {
        return getDaoFactory().getCountryDao();
    }
}
