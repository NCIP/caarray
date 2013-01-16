//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;


/**
 * Signal type information for Affymetrix CHP files containing Birdseed results.
 * @author John Scott
 */
enum AffymetrixSnpBirdseedChpQuantitationType implements QuantitationTypeDescriptor {
    /**
     * CHPDetection.
     */
    CHP_PROBE_SET_NAME("CHPProbeSetName", DataType.STRING),

    /**
     * CHPCall.
     */
    CHP_CALL("CHPCall", DataType.STRING),

    /**
     * CHPConfidence.
     */
    CHP_CONFIDENCE("CHPConfidence", DataType.FLOAT),

    /**
     * CHPForcedCall.
     */
    CHP_FORCED_CALL("CHPForcedCall", DataType.STRING),

    /**
     * CHPSignalA.
     */
    CHP_SIGNAL_A("CHPSignalA", DataType.FLOAT),

    /**
     * CHPSignalB.
     */
    CHP_SIGNAL_B("CHPSignalB", DataType.FLOAT);

    private final String name;
    private final DataType type;

    AffymetrixSnpBirdseedChpQuantitationType(final String name, final DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public DataType getDataType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }
}
