//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.copynumber;

import java.util.Arrays;
import java.util.List;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Description for MAGE-TAB data matrix copy number data.
 * @author dharley
 *
 */
enum DataMatrixCopyNumberDataTypes implements ArrayDataTypeDescriptor {
    
    MAGE_TAB_DATA_MATRIX_COPY_NUMBER("Copy Number (MAGE-TAB Data Matrix)",
            DataMatrixCopyNumberQuantitationTypes.values());

    private final String name;
    private final List<QuantitationTypeDescriptor> quantitationTypes;
    
    DataMatrixCopyNumberDataTypes(String name, QuantitationTypeDescriptor[] quantitationTypes) {
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
        return arrayDataType != null && name.equals(arrayDataType.getName());
    }

}
