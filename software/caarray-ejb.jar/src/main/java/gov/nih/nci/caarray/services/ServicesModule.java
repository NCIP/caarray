//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsServiceBean;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.data.DataRetrievalServiceBean;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.impl.DataServiceBean;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import gov.nih.nci.caarray.services.external.v1_0.search.impl.SearchServiceBean;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalServiceBean;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.services.search.CaArraySearchServiceBean;

import com.google.inject.AbstractModule;

/**
 * Guice module for the services package.
 * 
 * @author jscott
 */
public class ServicesModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(ArrayDesignDetailsService.class).to(ArrayDesignDetailsServiceBean.class);
        bind(DataRetrievalService.class).to(DataRetrievalServiceBean.class);
        bind(DataService.class).to(DataServiceBean.class);
        bind(SearchService.class).to(SearchServiceBean.class);
        bind(FileRetrievalService.class).to(FileRetrievalServiceBean.class);
        bind(CaArraySearchService.class).to(CaArraySearchServiceBean.class);
    }

}
