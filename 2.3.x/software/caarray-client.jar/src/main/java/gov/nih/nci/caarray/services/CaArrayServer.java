//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import java.util.Hashtable;

import javax.ejb.EJBAccessException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Client-side representation of a caArray server, used to connect to and
 * access a remote server.
 *
 * @author tavelae
 */
public final class CaArrayServer {

    private final String hostname;
    private final int jndiPort;
    private CaArraySearchService searchService;
    private DataRetrievalService dataRetrievalService;
    private ArrayDesignDetailsService arrayDesignDetailsService;
    private FileRetrievalService fileRetrievalService;

    /**
     * Creates a new instance configured to attach to the provided hostname and port.
     *
     * @param hostname hostname (or IP address) of the caArray server.
     * @param jndiPort JNDI port of the caArray server.
     */
    public CaArrayServer(final String hostname, final int jndiPort) {
        this.hostname = hostname;
        this.jndiPort = jndiPort;
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
            getSearchService().search((AbstractCaArrayObject) null);
        } catch (EJBAccessException e) {
            throw new FailedLoginException("Authentication failed: incorrect username or password");
        }
    }

    private void setCredentials(String username, String password) {
        LoginContext loginContext = null;
        try {
            String authConfPath = CaArrayServer.class.getResource("/auth.conf").toExternalForm();
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
        namingProperties.put("java.naming.provider.url", getJndiUrl());
        try {
            InitialContext initialContext = new InitialContext(namingProperties);
            searchService = (CaArraySearchService) initialContext.lookup(CaArraySearchService.JNDI_NAME);
            arrayDesignDetailsService = (ArrayDesignDetailsService)
                                initialContext.lookup(ArrayDesignDetailsService.JNDI_NAME);
            dataRetrievalService = (DataRetrievalService) initialContext.lookup(DataRetrievalService.JNDI_NAME);
            fileRetrievalService = (FileRetrievalService) initialContext.lookup(FileRetrievalService.JNDI_NAME);
        } catch (NamingException e) {
            throw new ServerConnectionException("Couldn't connect to the caArray server", e);
        }
    }

    private String getJndiUrl() {
        return "jnp://" + getHostname() + ":" + getJndiPort();
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * @return the jndiPort
     */
    public int getJndiPort() {
        return this.jndiPort;
    }

    /**
     * @return the searchService
     */
    public CaArraySearchService getSearchService() {
        return searchService;
    }

    /**
     * @return the arrayDesignDetailsService
     */
    public ArrayDesignDetailsService getArrayDesignDetailsService() {
        return arrayDesignDetailsService;
    }

    /**
     * @return the dataRetrievalService
     */
    public DataRetrievalService getDataRetrievalService() {
        return dataRetrievalService;
    }

    /**
     * @return the fileRetrievalService
     */
    public FileRetrievalService getFileRetrievalService() {
        return fileRetrievalService;
    }
}
