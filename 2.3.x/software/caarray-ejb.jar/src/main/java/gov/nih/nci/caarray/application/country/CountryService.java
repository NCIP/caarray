//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.country;

import gov.nih.nci.caarray.domain.country.Country;

import java.util.List;

/**
 * @author John Hedden
 *
 */
public interface CountryService {

    /**
     * The default JNDI name to use to lookup <code>CountryService</code>.
     */
    String JNDI_NAME = "caarray/CountryServiceBean/local";

    /**
     * Gets all countries.
     * @return all countries.
     */
    List<Country> getCountries();

}
