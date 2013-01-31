//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;

import org.junit.Before;
import org.junit.Test;

/**
 * @author John Hedden (Amentra, Inc.)
 *
 */
public class RegistrationServiceTest extends AbstractCaarrayTest {

    private RegistrationService registrationService;

    @Before
    public void setUp() {
        RegistrationServiceBean registrationServiceBean = new RegistrationServiceBean();
        registrationServiceBean.setDaoFactory(new DaoFactoryStub());
        this.registrationService = registrationServiceBean;
    }

    @Test
    public void testRegister() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("test");
        registrationRequest.setLastName("test");
        registrationRequest.setAddress1("test address1");
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setPhone("555-1212");
        registrationRequest.setOrganization("test org");
        registrationRequest.setRole("test role");
        registrationRequest.setCity("test city");
        registrationRequest.setZip("12345");
        Country country = new Country();
        country.setCode("US");
        registrationRequest.setCountry(country);
        this.registrationService.register(registrationRequest);
    }
}
