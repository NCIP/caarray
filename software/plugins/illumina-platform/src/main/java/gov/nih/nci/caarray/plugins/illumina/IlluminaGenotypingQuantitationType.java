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
public enum IlluminaGenotypingQuantitationType implements QuantitationTypeDescriptor {

    /**
     * AA_Freq.
     */
    AA_FREQ("AA_Freq", DataType.FLOAT),

    /**
     * AB_Freq.
     */
    AB_FREQ("AB_Freq", DataType.FLOAT),

    /**
     * BB_Freq.
     */
    BB_FREQ("BB_Freq", DataType.FLOAT),

    /**
     * Call_Freq.
     */
    CALL_FREQ("Call_Freq", DataType.FLOAT);


    private final String name;
    private final DataType type;

    IlluminaGenotypingQuantitationType(String name, DataType type) {
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
        for (IlluminaGenotypingQuantitationType type : values()) {
            names.add(type.getName());
        }
        return names;
    }

}
