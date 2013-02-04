//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.data;

import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;

/**
 * Returns requested array data from caArray.
 */
public interface DataRetrievalService {

    /**
     * The JNDI name to look up the remote <code>ArrayDesignDetailsService</code> service.
     */
    String JNDI_NAME = "caarray/DataRetrievalServiceBean/remote";

    /**
     * Returns the requested data as configured within the request object.
     *
     * The returned <code>DataSet</code> and the associated object graph will be fully populated
     * up to any associated <code>Hybridizations</code> which will have their attributes populated but
     * their associations trimmed (i.e. all <code>Hybridizations</code> will be leaf nodes.
     *
     * @param request specifies precisely which data to retrieve.
     * @return the corresponding data values.
     */
    DataSet getDataSet(DataRetrievalRequest request);
}
