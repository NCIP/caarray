//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Affymetrix CEL files.
 */
public enum IlluminaExpressionQuantitationType implements QuantitationTypeDescriptor {

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
     * Detection.
     */
    DETECTION("Detection", DataType.FLOAT);

    private final String name;
    private final DataType type;

    IlluminaExpressionQuantitationType(String name, DataType type) {
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

    /**
     * Returns all of the quantitation type names.
     * 
     * @return the list of names.
     */
    public static List<String> getTypeNames() {
        List<String> names = new ArrayList<String>(values().length);
        for (IlluminaExpressionQuantitationType type : values()) {
            names.add(type.getName());
        }
        return names;
    }

}
