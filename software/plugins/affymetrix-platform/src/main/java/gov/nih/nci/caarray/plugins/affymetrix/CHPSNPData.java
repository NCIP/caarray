//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionGenotypeProbeSetResults;
import gov.nih.nci.caarray.application.arraydata.ArrayDataException;
import gov.nih.nci.caarray.application.arraydata.ArrayDataIOException;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.StringColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Supports SNP data in legacy Affymetrix CHP file formats.
 * @author John Scott
 */
class CHPSNPData extends AbstractCHPLegacyData<FusionGenotypeProbeSetResults> {
    private static final Map<String, AffymetrixSnpChpQuantitationType> SNP_TYPE_MAP =
            new HashMap<String, AffymetrixSnpChpQuantitationType>();

    private static void initializeSnpTypeMap() {
        for (final AffymetrixSnpChpQuantitationType descriptor : AffymetrixSnpChpQuantitationType.values()) {
            SNP_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    static {
        initializeSnpTypeMap();
    }

    /**
     * @param fusionCHPLegacyData
     */
    CHPSNPData(final FusionCHPLegacyData fusionCHPLegacyData) {
        super(fusionCHPLegacyData);
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    // long switch statement
    protected void setExpressionValue(AbstractDataColumn column, int index, FusionGenotypeProbeSetResults entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final AffymetrixSnpChpQuantitationType typeDescriptor = getSnpTypeDescriptor(quantitationType);
        switch (typeDescriptor) {
            case CHP_ALLELE:
                ((StringColumn) column).getValues()[index] = entry.getAlleleCallString();
                break;
            case CHP_ALLELE_PVALUE:
                ((FloatColumn) column).getValues()[index] = getAllelePValue(entry);
                break;
            case CHP_RAS1:
                ((FloatColumn) column).getValues()[index] = entry.getRAS1();
                break;
            case CHP_RAS2:
                ((FloatColumn) column).getValues()[index] = entry.getRAS2();
                break;
            default:
                throw new IllegalArgumentException("Unsupported QuantitationType for SNP CHP data: "
                        + quantitationType);
        }
    }

    private float getAllelePValue(final FusionGenotypeProbeSetResults entry) {
        switch (entry.getAlleleCall()) {
            case FusionGenotypeProbeSetResults.ALLELE_A_CALL:
                return entry.getPValue_AA();
            case FusionGenotypeProbeSetResults.ALLELE_AB_CALL:
                return entry.getPValue_AB();
            case FusionGenotypeProbeSetResults.ALLELE_B_CALL:
                return entry.getPValue_BB();
            default:
                return entry.getPValue_NoCall();
        }
    }

    private AffymetrixSnpChpQuantitationType getSnpTypeDescriptor(final QuantitationType quantitationType) {
        return SNP_TYPE_MAP.get(quantitationType.getName());
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AffymetrixSnpChpQuantitationType.values();
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
         return AffymetrixArrayDataTypes.AFFYMETRIX_SNP_CHP;
    }

    @Override
    protected FusionGenotypeProbeSetResults getEntry(int index) {
        try {
            final FusionGenotypeProbeSetResults entry = new FusionGenotypeProbeSetResults();
            getData().getGenotypingResults(index, entry);
            return entry;
        } catch (final UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        } catch (final IOException e) {
            throw new ArrayDataIOException(e);
        }
    }
}
