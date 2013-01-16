//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.authentication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.junit.Before;
import org.junit.Test;


public class PasswordStackingCasLoginModuleTest {

    private PasswordStackingCasLoginModule loginModule;

    @Before
    public void setUp() {
        loginModule = mock( PasswordStackingCasLoginModule.class );
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void initializeSuccess() {
        Subject subject = new Subject();    // Mockito cannot mock final classes
        CallbackHandler callbackHandler = mock(CallbackHandler.class);
        Map sharedState = new HashMap();
        Map options = new HashMap();
        doCallRealMethod().when(loginModule).initialize(subject, callbackHandler, sharedState, options);        
        doNothing().when(loginModule).delegateInitialize(subject, callbackHandler, sharedState, options);        
        loginModule.initialize(subject, callbackHandler, sharedState, options);
        when(loginModule.getState()).thenCallRealMethod();
        assertSame(loginModule.getState(), sharedState);
    }

    @SuppressWarnings({ "rawtypes" })
    private void doLogin( boolean success ) throws LoginException {
        AttributePrincipal principal = mock( AttributePrincipal.class );
        when(principal.toString()).thenReturn("johndoe");
        Assertion assertion = mock(Assertion.class);
        when(assertion.getPrincipal()).thenReturn(principal);
        when(loginModule.getAssertion()).thenReturn(assertion);
        Map sharedState = new HashMap();
        doCallRealMethod().when(loginModule).initialize(null, null, sharedState, null);        
        doNothing().when(loginModule).delegateInitialize(null, null, sharedState, null);        
        loginModule.initialize(null, null, sharedState, null);
        doCallRealMethod().when(loginModule).login();        
        doReturn(success).when(loginModule).delegateLogin();   // Stubbing CAS ticket validation success
        loginModule.login();
    }

    @Test
    public void loginSuccess() throws LoginException {
        doLogin(true);
        when(loginModule.getState()).thenCallRealMethod();
        assertTrue(
                loginModule.getAssertion().getPrincipal().equals(
                loginModule.getState()
                        .get(PasswordStackingCasLoginModule.JBOSS_PASSWORD_STACKING_USER_PARAM)));
        when(loginModule.getState()).thenCallRealMethod();
        assertTrue(
                PasswordStackingCasLoginModule.FAKE_PASSWORD.equals(
                loginModule.getState()
                        .get(PasswordStackingCasLoginModule.JBOSS_PASSWORD_STACKING_PASSWORD_PARAM)));
    }

    @Test
    public void loginFailure() throws LoginException {
        doLogin(false);
        when(loginModule.getState()).thenCallRealMethod();
        assertFalse(
                loginModule.getAssertion().getPrincipal().equals(
                loginModule.getState()
                        .get(PasswordStackingCasLoginModule.JBOSS_PASSWORD_STACKING_USER_PARAM)));
        when(loginModule.getState()).thenCallRealMethod();
        assertFalse(
                PasswordStackingCasLoginModule.FAKE_PASSWORD.equals(
                loginModule.getState()
                        .get(PasswordStackingCasLoginModule.JBOSS_PASSWORD_STACKING_PASSWORD_PARAM)));
    }

}
