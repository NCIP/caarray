//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author wcheng
 *
 */
public class LogoutActionTest extends AbstractBaseStrutsTest {
    private static final String LOGOUT_URL = "https://localhost:8443/cas/logout";
    
    @Mock
    private ServletContext servletContext;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ServletActionContext.setServletContext(servletContext);
    }

    @Test
    public void testLogout() throws Exception {
        LogoutAction logoutAction = new LogoutAction();
        assertEquals("success", logoutAction.logout());
    }

    @Test
    public void testCasLogout() throws Exception {
        when(servletContext.getInitParameter("casServerLogoutUrl")).thenReturn(LOGOUT_URL);
        LogoutAction logoutAction = new LogoutAction();
        assertEquals("casLogout", logoutAction.logout());
        assertEquals(LOGOUT_URL, logoutAction.getCasServerLogoutUrl());
    }
}
