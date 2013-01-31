//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.List;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.protocol</code> package.
 */
public interface ProtocolDao extends CaArrayDao {

    /**
     * Returns the <code>Protocol</code> with the id given.
     *
     * @param id get <code>Protocol</code> matching this id
     * @return the <code>Protocol</code>.
     */
    Protocol getProtocol(long id);

    /**
     * Get a protocol based off of the fields in its unique constraint.
     * @param name the name of the protocol.
     * @param source the source.
     * @return the protocol, or null if none found.
     */
    Protocol getProtocol(String name, TermSource source);

    /**
     * Get the protocols with given name prefix and type.
     * @param type the protocol type
     * @param name the name the protocol must start with (case insensitive)
     * @return the matching protocols
     */
    List<Protocol> getProtocols(Term type, String name);

    /**
     * Get the parameter with the given characteristics.
     * @param name the name of the param.
     * @param protocol the protocol the param is associated with.
     * @return the param.
     */
    Parameter getParameter(String name, Protocol protocol);
}
