//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import java.io.Serializable;

/**
 * Looks up EJBs, Queues, etc. on behalf of clients.
 */
public interface ServiceLocator extends Serializable {

    /**
     * Returns the resource at the JNDI name given.
     * 
     * @param jndiName get the resource for this name.
     * @return the resource.
     */
    Object lookup(String jndiName);

}
