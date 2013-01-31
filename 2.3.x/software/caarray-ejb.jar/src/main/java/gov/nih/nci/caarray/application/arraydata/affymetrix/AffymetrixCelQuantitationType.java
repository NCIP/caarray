//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata.affymetrix;

import java.util.Comparator;

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
     * Indicates whether this enumeration value is equivalent to the persistenct
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

    static Comparator<QuantitationType> getComparator() {
        return new AffymetrixCelQuantitationTypeComparator();
    }

    /**
     * <code>Comparator</code> that orders QuantitationTypes that correspond to descriptors in this <code>enum</code>
     * based on the order they are declared.
     */
    private static class AffymetrixCelQuantitationTypeComparator implements Comparator<QuantitationType> {

        /**
         * {@inheritDoc}
         */
        public int compare(QuantitationType quantitationType1, QuantitationType quantitationType2) {
            int index1 = getIndex(quantitationType1);
            int index2 = getIndex(quantitationType2);
            return index1 - index2;
        }

        private int getIndex(QuantitationType quantitationType) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].isEquivalent(quantitationType)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Unsupported QuantitationType " + quantitationType);
        }

    }

}
