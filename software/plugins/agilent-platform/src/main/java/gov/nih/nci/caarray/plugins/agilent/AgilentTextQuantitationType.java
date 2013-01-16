//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Agilent text files.
 * 
 * @author jscott
 * @author gax
 */
enum AgilentTextQuantitationType implements QuantitationTypeDescriptor {
    /**
     * logRatio quantitation type.
     */
    LOG_RATIO("logRatio", DataType.FLOAT),
                                         
    /**
     * logRatioError quantitation type.
     */
    LOG_RATIO_ERROR("logRatioError", DataType.FLOAT),
                                        
    /**
     * pValueLogRatio quantitation type.
     */
    P_VALUE_LOG_RATIO("pValueLogRatio", DataType.FLOAT),
                                         
    /**
     * gProcessedSignal quantitation type.
     */
    G_PROCESSED_SIGNAL("gProcessedSignal", DataType.FLOAT),
                                        
    /**
     * rProcessedSignal quantitation type.
     */
    R_PROCESSED_SIGNAL("rProcessedSignal", DataType.FLOAT),
                                         
    /**
     * gProcessedSigError quantitation type.
     */
    G_PROCESSED_SIG_ERROR("gProcessedSigError", DataType.FLOAT),
                                         
    /**
     * rProcessedSigError quantitation type.
     */
    R_PROCESSED_SIG_ERROR("rProcessedSigError", DataType.FLOAT),
                                        
    /**
     * gMedianSignal quantitation type.
     */
    G_MEDIAN_SIGNAL("gMedianSignal", DataType.FLOAT),
                                         
    /**
     * rMedianSignal quantitation type.
     */
    R_MEDIAN_SIGNAL("rMedianSignal", DataType.FLOAT),

    /**
     * gTotalProbeSignal q. t.
     */
    G_TOTAL_PROBE_SIGNAL("gTotalProbeSignal", DataType.FLOAT),
    
    /**
     * gTotalProbeError q. t.
     */
    G_TOTAL_PROBE_ERROR("gTotalProbeError", DataType.FLOAT),

    /**
     * gTotalGeneSignal q. t.
     */
     G_TOTAL_GENE_SIGNAL("gTotalGeneSignal", DataType.FLOAT),

     /**
     * gTotalGeneError q. t.
     */
     G_TOTAL_GENE_ERROR("gTotalGeneError", DataType.FLOAT),

     /**
     * gIsGeneDetected q. t.
     */
     G_IS_GENE_DETECTED("gIsGeneDetected", DataType.BOOLEAN);

    private final String name;
    private final DataType type;

    AgilentTextQuantitationType(String name, DataType type) {
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
