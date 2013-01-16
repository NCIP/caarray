//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.protocol;

import java.util.List;

/**
 * Interface for entities to which Protocols can be applied.
 *
 * @author dkokotov
 */
public interface ProtocolApplicable {
    /**
     * @return the ProtocolApplications representing the protocols applied to this.
     */
    List<ProtocolApplication> getProtocolApplications();

    /**
     * Add the given protocol application to the protocol applications of this.
     * @param protocolApplication the protocol application to add
     */
    void addProtocolApplication(ProtocolApplication protocolApplication);

    /**
     * Remove all protocol applications of this.
     */
    void clearProtocolApplications();
}
