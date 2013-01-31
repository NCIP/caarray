//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.country;

import gov.nih.nci.caarray.domain.country.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Hedden (Amentra, Inc.)
 *
 */
public class CountryServiceStub implements CountryService {

    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<Country>();
        for (int i=0; i<1; i++) {
            countries.add(new Country());
        }
        return countries;
    }
}
