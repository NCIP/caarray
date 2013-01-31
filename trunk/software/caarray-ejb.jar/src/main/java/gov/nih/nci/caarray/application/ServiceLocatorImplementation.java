//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Looks up EJBs, Queues, etc. on behalf of clients.
 */
final class ServiceLocatorImplementation implements ServiceLocator {

    private static final long serialVersionUID = 7010735119922566807L;

    private static final Logger LOG = Logger.getLogger(ServiceLocatorImplementation.class);

    ServiceLocatorImplementation() {
        super();
    }

    /**
     * Returns the resource at the JNDI name given.
     *
     * @param jndiName get the resource for this name.
     * @return the resource.
     */
    public Object lookup(String jndiName) {
        try {
            InitialContext initialContext = new InitialContext();
            return initialContext.lookup(jndiName);
        } catch (NamingException e) {
            LOG.error("Couldn't get InitialContex", e);
            throw new IllegalStateException(e);
        }
    }

}
