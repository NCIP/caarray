//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation Types for Illumina Genotyping Processed Matrix data.
 * 
 * @author gax
 * @since 2.4.0
 */
public enum IlluminaGenotypingProcessedMatrixQuantitationType implements QuantitationTypeDescriptor {
    /**
     * Allele.
     */
    ALLELE("Allele", DataType.STRING),
    /**
     * GC_SCORE.
     */
    GC_SCORE("GC_SCORE", DataType.FLOAT),
    /**
     * Theta.
     */
    THETA("Theta", DataType.FLOAT),
    /**
     * R.
     */
    R("R", DataType.FLOAT),
    /**
     * B_Allele_Freq.
     */
    B_ALLELE_FREQ("B_Allele_Freq", DataType.FLOAT),
    /**
     * Log_R_Ratio.
     */
    LOG_R_RATIO("Log_R_Ratio", DataType.FLOAT);


    private final String name;
    private final DataType type;

    IlluminaGenotypingProcessedMatrixQuantitationType(String name, DataType type) {
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
