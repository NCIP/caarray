//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quatitation types that will be read from a SampleProbeProfile file.
 * 
 * @author gax
 * @since 2.4.0
 */
public enum SampleProbeProfileQuantitationType implements QuantitationTypeDescriptor {
    /**
     * MIN_Signal.
     */
    MIN_SIGNAL("MIN_Signal", DataType.FLOAT),
    /**
     * AVG_Signal.
     */
    AVG_SIGNAL("AVG_Signal", DataType.FLOAT),
    /**
     * MAX_Signal.
     */
    MAX_SIGNAL("MAX_Signal", DataType.FLOAT),
    /**
     * NARRAYS.
     */
    NARRAYS("NARRAYS", DataType.INTEGER),
    /**
     * ARRAY_STDEV.
     */
    ARRAY_STDEV("ARRAY_STDEV", DataType.FLOAT),
    /**
     * BEAD_STDEV.
     */
    BEAD_STDEV("BEAD_STDEV", DataType.FLOAT),
    /**
     * Avg_NBEADS.
     */
    AVG_NBEADS("Avg_NBEADS", DataType.INTEGER),
    /**
     * Detection, aka Detection Pval or DetectionPval.
     */
    DETECTION("Detection", DataType.FLOAT);

    private final String name;
    private final DataType type;

    private SampleProbeProfileQuantitationType(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return this.type;
    }
}
