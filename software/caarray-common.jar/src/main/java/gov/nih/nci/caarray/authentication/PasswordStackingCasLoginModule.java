//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.authentication;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jasig.cas.client.jaas.CasLoginModule;
import org.jasig.cas.client.validation.Assertion;

/**
 * Performs authentication services with CAS in jaas environment. Adds the JBoss
 * password stacking mechanism to be able to authenticate via CAS but get role
 * information from CSM.
 */
public class PasswordStackingCasLoginModule extends CasLoginModule {

    /**
     * User name parameter name to be used by JBoss for password stacking.
     */
    public static final String JBOSS_PASSWORD_STACKING_USER_PARAM = "javax.security.auth.login.name";

    /**
     * Password parameter name to be used by JBoss for password stacking.
     */
    public static final String JBOSS_PASSWORD_STACKING_PASSWORD_PARAM = "javax.security.auth.login.password";

    /**
     * Value to use for Password in JBoss for password stacking.
     */
    public static final String FAKE_PASSWORD = "FAKE_PASSWORD";    
    
    
    private Map<String, Object> state;

    
    /**
     * Returns a <code>state</code>.
     * @return a <code>state</code>.
     */
    protected Map<String, Object> getState() {
        return state;
    }

    /**
     * Returns an <code>assertion</code>.
     * @return an <code>assertion</code>.
     */
    protected Assertion getAssertion() {
        return this.assertion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        delegateInitialize(subject, callbackHandler, sharedState, options);
        state = sharedState;
    }

    /**
     * Initializes the login module. This method delegates to CAS login module superclass.
     * It is separated for the Mock framework benefit.
     * @param subject Authentication subject.
     * @param callbackHandler Callback handler.
     * @param sharedState Shared state map.
     * @param options Login module options.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void delegateInitialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
    }

    /**
     * {@inheritDoc}
     */
    public boolean login() throws LoginException {
        boolean result = delegateLogin();
        if (result) {
            // These are the two options JBoss will look for with the useFirstPass option
            state.put(JBOSS_PASSWORD_STACKING_USER_PARAM, this.getAssertion().getPrincipal());
            state.put(JBOSS_PASSWORD_STACKING_PASSWORD_PARAM, FAKE_PASSWORD);   // we can't get a hold of this 
                                                                                // from superclass - does not matter
        }
        return result;
    }

    /**
     * Performs login. This method delegates to CAS login module superclass.
     * It is separated for the Mock framework benefit.
     * @return true if login is successful.
     * @throws LoginException by superclass contract.
     */
    protected boolean delegateLogin() throws LoginException {
        return super.login();
    }

}
