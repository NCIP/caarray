//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * The array data types supported for Affymetrix.
 */
enum AffymetrixArrayDataTypes implements ArrayDataTypeDescriptor {    
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
    AFFYMETRIX_CEL("Affymetrix CEL", AffymetrixCelQuantitationType.values()),
    
    /**
     * Affymetrix CHP format (Expression Signal).
     */    
    AFFYMETRIX_SIGNAL_CHP("Affymetrix CHP (Expression Signal)", AffymetrixExpressionSignalChpQuantitationType.values()),
    
    /**
     * Affymetrix CHP format (Expression Signal).
     */    
    AFFYMETRIX_SIGNAL_DETECTION_CHP("Affymetrix CHP (Expression Signal Detection)"
            , AffymetrixExpressionSignalDetectionChpQuantitationType.values()),
       
    /**
     * Affymetrix CHP format (SNP BRLMM).
     */
    AFFYMETRIX_SNP_BRLMM_CHP("Affymetrix CHP (SNP BRLMM)", AffymetrixSnpBrlmmChpQuantitationType.values()),

    /**
     * Affymetrix CHP format (SNP Birdseed).
     */
    AFFYMETRIX_SNP_BIRDSEED_CHP("Affymetrix CHP (SNP Birdseed)", AffymetrixSnpBirdseedChpQuantitationType.values()),

    /**
     * Affymetrix CHP format (SNP AxiomGT).
     */
    AFFYMETRIX_SNP_AXIOMGT_CHP("Affymetrix CHP (SNP AxiomGT)", AffymetrixSnpAxiomGTChpQuantitationType.values()),

    /**
     * Affymetrix CNCHP (Copy Number).
     */
    AFFYMETRIX_COPY_NUMBER_CHP("Affymetrix CNCHP (Copy Number)", CopyNumberQuantitationType.values());

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
