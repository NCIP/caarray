//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Client-side representation of a caArray server, used to connect to and
 * access a remote server.
 *
 * @author tavelae
 */
public final class CaArrayServer extends AbstractCaArrayServer {
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
        super(hostname, jndiPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeServiceForCredentialCheck() {
        getSearchService().search((AbstractCaArrayObject) null);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void lookupServices(Context ctx) throws NamingException {
        searchService = (CaArraySearchService) ctx.lookup(CaArraySearchService.JNDI_NAME);
        arrayDesignDetailsService = (ArrayDesignDetailsService)
                            ctx.lookup(ArrayDesignDetailsService.JNDI_NAME);
        dataRetrievalService = (DataRetrievalService) ctx.lookup(DataRetrievalService.JNDI_NAME);
        fileRetrievalService = (FileRetrievalService) ctx.lookup(FileRetrievalService.JNDI_NAME);
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
