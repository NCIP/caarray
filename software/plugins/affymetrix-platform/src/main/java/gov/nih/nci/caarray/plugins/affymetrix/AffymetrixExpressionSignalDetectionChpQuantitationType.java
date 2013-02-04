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
 * Signal type information for Affymetrix CHP files containing RMA/PLIER results with detection data.
 * @author John Scott
 */
enum AffymetrixExpressionSignalDetectionChpQuantitationType implements QuantitationTypeDescriptor {
    /**
     * CHPDetectionPvalue.
     */
    CHP_DETECTION_PVALUE("CHPDetectionPvalue", DataType.FLOAT),
    
    /**
     * CHPDetection.
     */
    CHP_PROBE_SET_NAME("CHPProbeSetName", DataType.STRING),

    /**
     * CHPSignal.
     */
    CHP_SIGNAL("CHPSignal", DataType.FLOAT);

    private final String name;
    private final DataType type;

    AffymetrixExpressionSignalDetectionChpQuantitationType(final String name, final DataType type) {
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

