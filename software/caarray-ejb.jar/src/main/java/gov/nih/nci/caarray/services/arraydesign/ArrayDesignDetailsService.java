//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;

/**
 * Remote API interface used to retrieve low-level microarray design details.
 */
public interface ArrayDesignDetailsService {
    
    /**
     * The JNDI name to look up the remote <code>ArrayDesignDetailsService</code> service.
     */
    String JNDI_NAME = "caarray/ArrayDesignDetailsServiceBean/remote";

    /**
     * Returns complete details of all design elements and relationships for the
     * requested design.
     *
     * @param design get details for this design
     * @return the design details.
     */
    ArrayDesignDetails getDesignDetails(ArrayDesign design);

}
