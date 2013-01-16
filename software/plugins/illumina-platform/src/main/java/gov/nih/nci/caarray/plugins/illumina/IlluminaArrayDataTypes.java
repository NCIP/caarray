//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

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
     * Illumina Genotyping CSV format.
     */
    ILLUMINA_GENOTYPING("Illumina CSV (Genotyping)", IlluminaGenotypingQuantitationType.values()),

    /**
     * Illumina Genotyping TSV format.
     * @since 2.4.0
     */
    ILLUMINA_GENOTYPING_PROCESSED_MATRIX("Illumina TSV (Genotyping Processed Matrix)",
        IlluminaGenotypingProcessedMatrixQuantitationType.values()),

    /**
     * Illumina Sample Probe Profile TSV format.
     * @since 2.4.0
     */
    ILLUMINA_SAMPLE_PROBE_PROFILE("Illumina Sample Probe Profile", SampleProbeProfileQuantitationType.values());


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
