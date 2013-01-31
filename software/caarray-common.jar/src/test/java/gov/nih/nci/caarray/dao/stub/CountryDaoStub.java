//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.CountryDao;
import gov.nih.nci.caarray.domain.country.Country;

import java.util.Collections;
import java.util.List;

/**
 * @author John Hedden
 *
 */
public class CountryDaoStub extends AbstractDaoStub implements CountryDao {

    public List<Country> getCountries() {
        return Collections.emptyList();
    }
}
