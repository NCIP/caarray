//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * Descriptor for Agilient raw text data format.
 * 
 * @author jscott
 */
enum AgilentArrayDataTypes implements ArrayDataTypeDescriptor {
    /**
     * Agilent Raw Text.
     */
    AGLIENT_RAW_TEXT_ACGH("Agilent Raw Text", AgilentTextQuantitationType.values());

    private final String name;
    private final List<QuantitationTypeDescriptor> quantitationTypes;
    
    AgilentArrayDataTypes(String name, QuantitationTypeDescriptor[] quantitationTypes) {
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
    public String getVersion() {
        return null;
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
    public boolean isEquivalent(ArrayDataType arrayDataType) {
        return arrayDataType != null && name.equals(arrayDataType.getName());
    }
}
