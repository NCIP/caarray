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
 * Quantitation type information for Affymetrix CEL files.
 */
public enum AffymetrixExpressionChpQuantitationType implements QuantitationTypeDescriptor {
    /**
     * CHPDetection.
     */
    CHP_DETECTION("CHPDetection", DataType.STRING),

    /**
     * CHPDetectionPvalue.
     */
    CHP_DETECTION_PVALUE("CHPDetectionPvalue", DataType.FLOAT),

    /**
     * CHPPairs.
     */
    CHP_PAIRS("CHPPairs", DataType.SHORT),

    /**
     * CHPPairsUsed.
     */
    CHP_PAIRS_USED("CHPPairsUsed", DataType.SHORT),

    /**
     * CHPSignal.
     */
    CHP_SIGNAL("CHPSignal", DataType.FLOAT),

    /**
     * CHPChange.
     */
    CHP_CHANGE("CHPChange", DataType.STRING),

    /**
     * CHPChangePvalue.
     */
    CHP_CHANGE_PVALUE("CHPChangePvalue", DataType.FLOAT),

    /**
     * CHPSignalLogRatio.
     */
    CHP_SIGNAL_LOG_RATIO("CHPSignalLogRatio", DataType.FLOAT),

    /**
     * CHPSignalLogRatioHigh.
     */
    CHP_SIGNAL_LOG_RATIO_HIGH("CHPSignalLogRatioHigh", DataType.FLOAT),

    /**
     * CHPSignalLogRatioLow.
     */
    CHP_SIGNAL_LOG_RATIO_LOW("CHPSignalLogRatioLow", DataType.FLOAT),

    /**
     * CHPCommonPairs.
     */
    CHP_COMMON_PAIRS("CHPCommonPairs", DataType.SHORT);

    private final String name;
    private final DataType type;

    AffymetrixExpressionChpQuantitationType(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public DataType getDataType() {
        return type;
    }
}
    
