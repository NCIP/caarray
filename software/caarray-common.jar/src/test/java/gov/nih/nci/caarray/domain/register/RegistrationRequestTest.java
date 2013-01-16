//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.register;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.state.State;

import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class RegistrationRequestTest {
    @Test
    public void testRegistrationRequest() {
        RegistrationRequest r = new RegistrationRequest();
        Country c = new Country();
        State s = new State();
        r.setAddress1("address1");
        r.setAddress2("address2");
        r.setCity("city");
        r.setCountry(c);
        r.setEmail("email");
        r.setFax("fax");
        r.setFirstName("firstName");
        r.setLastName("lastName");
        r.setLoginName("loginName");
        r.setMiddleInitial("middleInitial");
        r.setOrganization("organization");
        r.setPhone("phone");
        r.setProvince("province");
        r.setRole("role");
        r.setState(s);
        r.setZip("zip");
        assertEquals("address1", r.getAddress1());
        assertEquals("address2", r.getAddress2());
        assertEquals("city", r.getCity());
        assertEquals(c, r.getCountry());
        assertEquals("email", r.getEmail());
        assertEquals("fax", r.getFax());
        assertEquals("firstName", r.getFirstName());
        assertEquals("lastName", r.getLastName());
        assertEquals("loginName", r.getLoginName());
        assertEquals("middleInitial", r.getMiddleInitial());
        assertEquals("organization", r.getOrganization());
        assertEquals("phone", r.getPhone());
        assertEquals("province", r.getProvince());
        assertEquals("role", r.getRole());
        assertEquals(s, r.getState());
        assertEquals("zip", r.getZip());
    }
}
