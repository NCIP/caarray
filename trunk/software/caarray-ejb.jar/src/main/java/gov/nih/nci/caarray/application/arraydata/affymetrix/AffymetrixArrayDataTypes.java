//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata.affymetrix;

import java.util.Arrays;
import java.util.List;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * The array data types supported for Affymetrix.
 */
public enum AffymetrixArrayDataTypes implements ArrayDataTypeDescriptor {
    
    /**
     * Affymetrix CHP format (Gene Expression).
     */
    AFFYMETRIX_EXPRESSION_CHP("Affymetrix CHP (Gene Expression)", AffymetrixExpressionChpQuantitationType.values()),
    
    /**
     * Affymetrix CHP format (SNP).
     */
    AFFYMETRIX_SNP_CHP("Affymetrix CHP (SNP)", AffymetrixSnpChpQuantitationType.values()),
    
    /**
     * Affymetrix CEL format.
     */
    AFFYMETRIX_CEL("Affymetrix CEL", AffymetrixCelQuantitationType.values());
    
    private final String name;
    private final List<QuantitationTypeDescriptor> quantitationTypes;

    AffymetrixArrayDataTypes(String name, QuantitationTypeDescriptor[] quantitationTypes) {
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
