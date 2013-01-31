//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata.illumina;

import java.util.Arrays;
import java.util.List;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * The array data types supported for Affymetrix.
 */
public enum IlluminaArrayDataTypes implements ArrayDataTypeDescriptor {

    /**
     * Illumina gene Expression CSV format.
     */
    ILLUMINA_EXPRESSION("Illumina CSV (Gene Expression)", IlluminaExpressionQuantitationType.values()),

    /**
     * Illumina Gene Expression CSV format.
     */
    ILLUMINA_GENOTYPING("Illumina CSV (Genotyping)", IlluminaGenotypingQuantitationType.values());


    private final String name;
    private final List<QuantitationTypeDescriptor> quantitationTypes;

    IlluminaArrayDataTypes(String name, QuantitationTypeDescriptor[] quantitationTypes) {
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
