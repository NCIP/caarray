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
 * Possible data type from a Copy number CHP file.
 * @author gax
 * @since 2.4.0
 */
public enum CopyNumberQuantitationType implements QuantitationTypeDescriptor {
    /**
     * ProbeSetName.
     */
    PROBE_SET_NAME("ProbeSetName", DataType.STRING),
    /**
     * Chromosome. UByte; 1-22, 24 (for X), 25 (for Y) and 26 (for MT) and 255 (for no value)
     */
    CHROMOSOME("Chromosome", DataType.SHORT),
    /**
     * Position. UInt
     */
    POSITION("Position", DataType.INTEGER),
    /**
     * CNState. UByte for CN4 algorithm.
     */
    CN4_CN_STATE("CN4_CNState", DataType.SHORT),
    /**
     * Log2Ratio. float.
     */
    LOG2RATIO("Log2Ratio", DataType.FLOAT),
    /**
     * HmmMedianLog2Ratio. float
     */
    HMM_MEDIAN_LOG2RATIO("HmmMedianLog2Ratio", DataType.FLOAT),
    /**
     * NegLog10PValue. float
     */
    NEG_LOG10PVALUE("NegLog10PValue", DataType.FLOAT),
    /**
     * ChipNum. UByte
     */
    CHIP_NUM("ChipNum", DataType.SHORT),
    /**
     * CNState. float for CN5 algorithm.
     */
    CN5_CN_STATE("CN5_CNState", DataType.FLOAT),
    /**
     * SmoothSignal. float
     */
    SMOOTH_SIGNAL("SmoothSignal", DataType.FLOAT),
    /**
     * LOH. float.
     */
    LOH("LOH", DataType.FLOAT),
    /**
     * Allele Difference. float.
     */
    ALLELE_DIFFERENCE("Allele Difference", DataType.FLOAT);
    
    private final String name;
    private final DataType type;

    private CopyNumberQuantitationType(final String name, final DataType type) {
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
