//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import java.util.Arrays;
import java.util.List;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * The array data types supported for Affymetrix.
 */
public enum NimblegenArrayDataTypes implements ArrayDataTypeDescriptor {

    /**
     * Nimblegen raw pair format.
     */
    NIMBLEGEN("Nimblegen Pair", NimblegenQuantitationType.values());

    private final String name;
    private final List<QuantitationTypeDescriptor> quantitationTypes;

    NimblegenArrayDataTypes(String name, QuantitationTypeDescriptor[] quantitationTypes) {
        this.name = name;
        this.quantitationTypes = Arrays.asList(quantitationTypes);
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
    public List<QuantitationTypeDescriptor> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEquivalent(ArrayDataType arrayDataType) {
        return name.equals(arrayDataType.getName());
    }


}
