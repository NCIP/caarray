//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.cagrid.caarray.util;

import java.io.IOException;

/**
 * An interface to be implemented by clients who which to to process data retrieved via a Grid Transfer
 * enabled service as a stream. 
 * 
 * @author dkokotov
 */
public interface GridTransferResultHandler {   
    /**
     * Process the data incoming via the grid transfer service. The implementation does not need 
     * to close the stream at the end.
     * 
     * @param stream the InputStream representing the data coming over via the grid transfer service.
     * @return the handler may optionally return some result of its processing. The semantics of
     * such a result is up to individual implementations.
     * @throws IOException in case of error reading from the stream
     */
    Object processRetrievedData(java.io.InputStream stream) throws IOException;
}
