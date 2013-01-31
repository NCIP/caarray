//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.registration;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.country.CountryService;
import gov.nih.nci.caarray.application.country.CountryServiceStub;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.registration.RegistrationServiceStub;
import gov.nih.nci.caarray.application.state.StateService;
import gov.nih.nci.caarray.application.state.StateServiceStub;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;


/**
 * @author Winston Cheng
 *
 */
public class RegistrationActionTest extends AbstractCaarrayTest {
    private final RegistrationAction registrationAction = new RegistrationAction();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(StateService.JNDI_NAME, new StateServiceStub());
        locatorStub.addLookup(CountryService.JNDI_NAME, new CountryServiceStub());
        locatorStub.addLookup(RegistrationService.JNDI_NAME, new RegistrationServiceStub());

        MockServletContext context = new MockServletContext();
        context.addInitParameter("ldap.install", "false");
        ServletActionContext.setServletContext(context);
    }

    @Test
    public void testPrepare() throws Exception {
        MockServletContext context = (MockServletContext) ServletActionContext.getServletContext();
        context.addInitParameter("some.other.param", "false");
        registrationAction.prepare();
        assertEquals(50,registrationAction.getStateList().size());
        assertEquals(1,registrationAction.getCountryList().size());
    }

    @Test
    public void testValidate() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setLoginName("login");
        request.setEmail("test@email.com");
        registrationAction.setRegistrationRequest(request);
        registrationAction.prepare();
        registrationAction.validate();

        MockServletContext context = new MockServletContext();
        context.addInitParameter("ldap.install", "true");
        ServletActionContext.setServletContext(context);
        registrationAction.prepare();
        registrationAction.validate();
        
        registrationAction.setLdapAuthenticate(false);
        registrationAction.validate();
    }

}
