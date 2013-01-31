//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.country.Country;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author John Hedden
 *
 */
public class CountryDaoImpl extends AbstractCaArrayDaoImpl implements CountryDao {
    private static final Logger LOG = Logger.getLogger(CountryDaoImpl.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Country> getCountries() {
        String query = "from " + Country.class.getName() + " c order by c.name asc";
        return getCurrentSession().createQuery(query).setCacheable(true).list();
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
