//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata.nimblegen;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Nimblegen CEL files.
 */
public enum NimblegenQuantitationType implements QuantitationTypeDescriptor {

    /**
     * MATCH_INDEX.
     */
    MATCH_INDEX(DataType.FLOAT),

    /**
     * Perfect Match.
     */
    PM(DataType.FLOAT),

    /**
     * Mismatch.
     */
    MM(DataType.FLOAT),

    /**
     * X.
     */
    X(DataType.INTEGER),

    /**
     * Y.
     */
    Y(DataType.INTEGER);

    private final DataType type;

    NimblegenQuantitationType(DataType type) {
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
        return name();
    }

    /**
     * Returns all of the quantitation type names.
     * 
     * @return the list of names.
     */
    public static List<String> getTypeNames() {
        List<String> names = new ArrayList<String>();
        for (NimblegenQuantitationType type : values()) {
            names.add(type.name());
        }
        return names;
    }


}
