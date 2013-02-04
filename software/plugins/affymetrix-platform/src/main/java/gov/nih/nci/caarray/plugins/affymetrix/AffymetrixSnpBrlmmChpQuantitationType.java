//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;


/**
 * Signal type information for Affymetrix CHP files containing BRLMM results.
 * @author John Scott
 */
enum AffymetrixSnpBrlmmChpQuantitationType implements QuantitationTypeDescriptor {
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
     * CHPContrast.
     */
    CHP_CONTRAST("CHPContrast", DataType.FLOAT),
    
    /**
     * CHPStrength.
     */
    CHP_STRENGTH("CHPStrength", DataType.FLOAT);

    private final String name;
    private final DataType type;

    AffymetrixSnpBrlmmChpQuantitationType(final String name, final DataType type) {
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
