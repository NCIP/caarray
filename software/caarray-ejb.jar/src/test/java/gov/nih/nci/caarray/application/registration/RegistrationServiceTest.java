//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;

import org.junit.Before;
import org.junit.Test;

/**
 * @author John Hedden (Amentra, Inc.)
 * 
 */
public class RegistrationServiceTest extends AbstractServiceTest {

    private RegistrationService registrationService;

    @Before
    public void setUp() {
        final DaoFactoryStub daoFactory = new DaoFactoryStub();

        final RegistrationServiceBean registrationServiceBean = new RegistrationServiceBean();
        registrationServiceBean.setSearchDao(daoFactory.getSearchDao());

        this.registrationService = registrationServiceBean;
    }

    @Test
    public void testRegister() {
        final RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("test");
        registrationRequest.setLastName("test");
        registrationRequest.setAddress1("test address1");
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setPhone("555-1212");
        registrationRequest.setOrganization("test org");
        registrationRequest.setRole("test role");
        registrationRequest.setCity("test city");
        registrationRequest.setZip("12345");
        final Country country = new Country();
        country.setCode("US");
        registrationRequest.setCountry(country);
        this.registrationService.register(registrationRequest);
    }
}
