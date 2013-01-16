//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.services.AbstractCaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Client-side representation of a caArray server, used to connect to and
 * access a remote server.
 *
 * @author dkokotov
 */
public final class CaArrayServer extends AbstractCaArrayServer {
    private SearchService searchService;
    private DataService dataService;

    /**
     * Creates a new instance configured to attach to the provided hostname and port.
     *
     * @param hostname hostname (or IP address) of the caArray server.
     * @param jndiPort JNDI port of the caArray server.
     */
    public CaArrayServer(final String hostname, final int jndiPort) {
        super(hostname, jndiPort);
    }

    /**
     * Creates a new instance configured to attach to the provided JNDI url.
     *
     * @param jndiUrl the JNDI url, should be in the format jnp://hostname:port
     */
    public CaArrayServer(final String jndiUrl) {
        super(jndiUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeServiceForCredentialCheck() {
        getSearchService().getAllPrincipalInvestigators();        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void lookupServices(Context ctx) throws NamingException {
        searchService = (SearchService) ctx.lookup(SearchService.JNDI_NAME);
        dataService = (DataService) ctx.lookup(DataService.JNDI_NAME);
    }

    /**
     * @return the searchService
     */
    public SearchService getSearchService() {
        return searchService;
    }

    /**
     * @return the dataRetrievalService
     */
    public DataService getDataService() {
        return dataService;
    }
}
