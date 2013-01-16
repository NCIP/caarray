//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ProtocolDao;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.List;

/**
 *
 */
public class ProtocolDaoStub extends AbstractDaoStub implements ProtocolDao {
    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Protocol> getProtocols(Term type, String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Parameter getParameter(String name, Protocol protocol) {
        return null;
    }
}
