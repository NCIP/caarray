//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Affymetrix CEL files.
 */
enum AffymetrixSnpChpQuantitationType implements QuantitationTypeDescriptor {
    /**
     * CHPAllele.
     */
    CHP_ALLELE("CHPAllele", DataType.STRING),

    /**
     * CHPAllelePvalue.
     */
    CHP_ALLELE_PVALUE("CHPAllelePvalue", DataType.FLOAT),

    /**
     * CHPAlleleRAS1.
     */
    CHP_RAS1("CHPAlleleRAS1", DataType.FLOAT),

    /**
     * CHPAlleleRAS2.
     */
    CHP_RAS2("CHPAlleleRAS2", DataType.FLOAT);

    private final String name;
    private final DataType type;

    AffymetrixSnpChpQuantitationType(String name, DataType type) {
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
}
