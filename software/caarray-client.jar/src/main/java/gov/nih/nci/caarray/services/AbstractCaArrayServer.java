//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import java.util.Hashtable;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * @author dkokotov
 *
 */
public abstract class AbstractCaArrayServer {
    private final String jndiUrl;
 
    /**
     * Creates a new instance configured to attach to the provided hostname and port.
     *
     * @param hostname hostname (or IP address) of the caArray server.
     * @param jndiPort JNDI port of the caArray server.
     */
    public AbstractCaArrayServer(final String hostname, final int jndiPort) {
        this.jndiUrl = "jnp://" + hostname + ":" + jndiPort;
    }

    /**
     * Creates a new instance configured to attach to the provided JNDI url.
     *
     * @param jndiUrl the JNDI url, should be in the format jnp://hostname:port
     */
    public AbstractCaArrayServer(final String jndiUrl) {
        this.jndiUrl = jndiUrl;
    }
    
    /**
     * Starts a new session with this server for an anonymous (unauthenticated) user.
     *
     * @throws ServerConnectionException thrown when the connection is dropped/missing
     */
    public void connect() throws ServerConnectionException {
        connectToServer();
    }

    /**
     * Starts a new session with this server.
     *
     * @param username username to use to authenticate
     * @param password password to use to authenticate
     * @throws ServerConnectionException thrown when the connection is dropped/missing
     * @throws FailedLoginException thrown when there is a problem with the login credentials
     */
    public void connect(String username, String password) throws ServerConnectionException, FailedLoginException {
        setCredentials(username, password);
        connectToServer();
        checkCredentials();
    }

    private void checkCredentials() throws FailedLoginException {
        try {
            executeServiceForCredentialCheck();
        } catch (EJBAccessException e) {
            throw new FailedLoginException("Authentication failed: incorrect username or password"); // NOPMD
        }
    }
    
    /**
     * Execute some method in a remote EJB service that forces the authentication credentials to be checked. 
     * This method must be side effect free, and should ideally be simple and fast, as the only purpose of
     * executing it is to force the credential check.
     * Subclasses should override this to invoke the appropriate API method.
     */
    protected abstract void executeServiceForCredentialCheck();

    private void setCredentials(String username, String password) {
        LoginContext loginContext = null;
        try {
            String authConfPath = AbstractCaArrayServer.class.getResource("/auth.conf").toExternalForm();
            System.setProperty("java.security.auth.login.config", authConfPath);
            final CaArrayCallbackHandler handler = new CaArrayCallbackHandler(username, password);
            loginContext = new LoginContext("client-login", handler);
            loginContext.login();
        } catch (LoginException e) {
            throw new IllegalStateException("The caarray login module could not be found.", e);
        }
    }

    @SuppressWarnings("PMD.ReplaceHashtableWithMap") // needed for API compatability
    private void connectToServer() throws ServerConnectionException {
        final Hashtable<String, String> namingProperties = new Hashtable<String, String>();
        namingProperties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        namingProperties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        namingProperties.put("java.naming.provider.url", this.jndiUrl);
        try {
            InitialContext initialContext = new InitialContext(namingProperties);
            lookupServices(initialContext);
        } catch (NamingException e) {
            throw new ServerConnectionException("Couldn't connect to the caArray server", e);
        }
    }
    
    /**
     * Method called after obtaining a JNDI context from the remote EJB server. 
     * Subclasses should override this method and lookup remote EJB references in this context.
     * @param ctx the JNDI context where remote EJB service references can be looked up
     * @throws NamingException if there is an error looking up a remote EJB in the context
     */
    protected abstract void lookupServices(Context ctx) throws NamingException;
}
