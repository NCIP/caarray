//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.registration;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.registration.RegistrationServiceStub;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.domain.state.State;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;


/**
 * @author Winston Cheng
 */
public class RegistrationActionTest extends AbstractBaseStrutsTest {
    private final RegistrationAction registrationAction = new RegistrationAction();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(RegistrationService.JNDI_NAME, new RegistrationServiceStub());
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataServiceStub());

        MockServletContext context = new MockServletContext();
        context.addInitParameter("ldap.install", "false");
        ServletActionContext.setServletContext(context);
    }

    @Test
    public void testPrepare() throws Exception {
        MockServletContext context = (MockServletContext) ServletActionContext.getServletContext();
        context.addInitParameter("some.other.param", "false");
        registrationAction.prepare();
        assertEquals(2,registrationAction.getStateList().size());
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

    private static final class LocalGenericDataServiceStub extends GenericDataServiceStub {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
            
            if (Country.class.equals(entityClass)) {
                Country c = new Country();
                c.setName("USA");
                return (List<T>) Collections.singletonList(c);
            } else if (State.class.equals(entityClass)) {
                State s1 = new State();
                s1.setName("MD");
                State s2 = new State();
                s2.setName("DC");
                return (List<T>) Arrays.asList(s1, s2);
            } else {
                return super.retrieveAll(entityClass, orders);
            }            
        }
    }
}
