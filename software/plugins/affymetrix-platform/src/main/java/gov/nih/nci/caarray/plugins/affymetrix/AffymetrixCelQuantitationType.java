//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Affymetrix CEL files.
 */
public enum AffymetrixCelQuantitationType implements QuantitationTypeDescriptor {
    
    /**
     * CELX quantitation type.
     */
    CEL_X("CELX", DataType.SHORT),
    
    /**
     * CELY quantitation type.
     */
    CEL_Y("CELY", DataType.SHORT),
    
    /**
     * CELIntensity quantitation type.
     */
    CEL_INTENSITY("CELIntensity", DataType.FLOAT),
    
    /**
     * CELIntensityStdev quantitation type.
     */
    CEL_INTENSITY_STD_DEV("CELIntensityStdev", DataType.FLOAT),
    
    /**
     * CELMask quantitation type.
     */
    CEL_MASK("CELMask", DataType.BOOLEAN),
    
    /**
     * CELOutlier quantitation type.
     */
    CEL_OUTLIER("CELOutlier", DataType.BOOLEAN),
    
    /**
     * CELPixels quantitation type.
     */
    CEL_PIXELS("CELPixels", DataType.SHORT);

    private final String name;
    private final DataType type;

    AffymetrixCelQuantitationType(String name, DataType type) {
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
     * Indicates whether this enumeration value is equivalent to the persistent
     * <code>QuantitationType</code> object.
     * 
     * @param quantitationType type to compare to
     * @return true if equivalent, false otherwise.
     */
    public boolean isEquivalent(QuantitationType quantitationType) {
        return quantitationType != null
        && quantitationType.getName() != null
        && quantitationType.getName().equals(name);
    }
}
