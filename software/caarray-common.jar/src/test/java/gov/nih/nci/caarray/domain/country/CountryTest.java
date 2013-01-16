//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.country;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class CountryTest {
    @Test
    public void testCountry() {
        Country country = new Country();
        country.setCode("code");
        country.setIso3("iso3");
        country.setName("name");
        country.setNumcode("numcode");
        country.setPrintableName("printableName");
        assertEquals("code", country.getCode());
        assertEquals("iso3", country.getIso3());
        assertEquals("name", country.getName());
        assertEquals("numcode", country.getNumcode());
        assertEquals("printableName", country.getPrintableName());
    }

    @Test
    public void testCompareTo() {
        Country c1 = new Country();
        Country c2 = new Country();
        Country c3 = new Country();
        Country c4 = new Country();

        c1.setName("country");
        c2.setName("country");
        c3.setName("UNITED STATES");
        c4.setName("Country");
        assertEquals(0, c1.compareTo(c2));
        assertEquals(-1, c3.compareTo(c1));
        assertEquals(1, c1.compareTo(c3));
        assertEquals(0, c1.compareTo(c4));
    }
}
