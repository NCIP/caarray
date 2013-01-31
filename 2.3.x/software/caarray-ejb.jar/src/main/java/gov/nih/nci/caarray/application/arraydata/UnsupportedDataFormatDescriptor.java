//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import java.util.Collections;
import java.util.List;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Descriptor for data formats that aren't supported.
 */
class UnsupportedDataFormatDescriptor implements ArrayDataTypeDescriptor {

    static final ArrayDataTypeDescriptor INSTANCE = new UnsupportedDataFormatDescriptor();

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Unsupported";
    }

    /**
     * {@inheritDoc}
     */
    public List<QuantitationTypeDescriptor> getQuantitationTypes() {
        return Collections.emptyList();
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
        return this.equals(arrayDataType);
    }

}
