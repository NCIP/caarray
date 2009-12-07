/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cruk.cri.caarray.services;

import gov.nih.nci.caarray.services.ServerConnectionException;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.cruk.cri.caarray.CaArrayServiceFacade;

/**
 * Client-side representation of a caArray server, used to connect to and
 * access a remote server.
 *
 * Extends NCI CaArrayServer class exposing the additional CaArrayServiceFacade
 * EJB interface.
 *
 * Note that the NCI CaArrayServer had to be modified such that it was no longer
 * marked final to allow this subclass.
 *
 * @author eldrid01
 */
public class CaArrayServer extends gov.nih.nci.caarray.services.CaArrayServer
{
    private CaArrayServiceFacade caArrayServiceFacade;

    /**
     * Creates a new instance configured to attach to the provided hostname and port.
     *
     * @param hostname hostname (or IP address) of the caArray server.
     * @param jndiPort JNDI port of the caArray server.
     */
    public CaArrayServer(final String hostname, final int jndiPort)
    {
        super(hostname, jndiPort);
    }

    /**
     * @return the CaArrayServiceFacade interface
     * @throws ServerConnectionException
     */
    public CaArrayServiceFacade getCaArrayServiceFacade() throws ServerConnectionException
    {
        if (caArrayServiceFacade == null)
        {
            final Hashtable<String, String> namingProperties = new Hashtable<String, String>();
            namingProperties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
            namingProperties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            namingProperties.put("java.naming.provider.url", getJndiUrl());
            try
            {
                InitialContext initialContext = new InitialContext(namingProperties);
                caArrayServiceFacade = (CaArrayServiceFacade)initialContext.lookup(CaArrayServiceFacade.JNDI_NAME);
            } catch (NamingException e)
            {
                throw new ServerConnectionException("Couldn't connect to the caArray server", e);
            }
        }
        return caArrayServiceFacade;
    }
}