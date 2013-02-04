//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import affymetrix.calvin.data.ProbeSetMultiDataGenotypeData;
import affymetrix.calvin.parameter.ParameterNameValue;
import affymetrix.fusion.chp.FusionCHPMultiDataData;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.StringColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * Supports SNP data derived by the AxiomGT algorithm in Affymetrix CHP file formats.
 * @author John Scott
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
class CHPSnpAxiomGTData extends AbstractCHPMultiDataGenotypeData {
    /**
     * @param fusionCHPMultiDataData
     */
    CHPSnpAxiomGTData(final FusionCHPMultiDataData fusionCHPMultiDataData) {
        super(fusionCHPMultiDataData);
    }

    private static final Map<String, AffymetrixSnpAxiomGTChpQuantitationType> EXPRESSION_TYPE_MAP =
            new HashMap<String, AffymetrixSnpAxiomGTChpQuantitationType>();

    private static void initializeExpressionTypeMap() {
        for (final AffymetrixSnpAxiomGTChpQuantitationType descriptor
                : AffymetrixSnpAxiomGTChpQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    static {
        initializeExpressionTypeMap();
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    // long switch statement
    protected void setExpressionValue(final AbstractDataColumn column, final int index,
                                    final ProbeSetMultiDataGenotypeData entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final AffymetrixSnpAxiomGTChpQuantitationType typeDescriptor
            = getExpressionTypeDescriptor(quantitationType);

        Map<String, ParameterNameValue> metricMap = getMetricMap(entry);

        switch (typeDescriptor) {
        case CHP_PROBE_SET_NAME:
            ((StringColumn) column).getValues()[index] = entry.getName();
            break;
        case CHP_CALL:
            ((StringColumn) column).getValues()[index]
                = ProbeSetMultiDataGenotypeData.genotypeCallToString(entry.getCall());
             break;
        case CHP_CONFIDENCE:
            ((FloatColumn) column).getValues()[index] = entry.getConfidence();
            break;
        case CHP_FORCED_CALL:
            ((StringColumn) column).getValues()[index]
                = ProbeSetMultiDataGenotypeData.genotypeCallToString(metricMap.get("Forced Call").getValueInt8());
            break;
        case CHP_STRENGTH:
            ((FloatColumn) column).getValues()[index] = metricMap.get("Strength").getValueFloat();
            break;
        case CHP_LOG_RATIO:
            ((FloatColumn) column).getValues()[index] = metricMap.get("Log Ratio").getValueFloat();
            break;
       default:
            throw new IllegalArgumentException("Unsupported QuantitationType for SNP CHP data: " + quantitationType);
        }
    }


    private AffymetrixSnpAxiomGTChpQuantitationType getExpressionTypeDescriptor(
            final QuantitationType quantitationType) {
        return getExpressionTypeMap().get(quantitationType.getName());
    }

    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AffymetrixSnpAxiomGTChpQuantitationType.values();
    }

    ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
         return AffymetrixArrayDataTypes.AFFYMETRIX_SNP_AXIOMGT_CHP;
    }

    private Map<String, AffymetrixSnpAxiomGTChpQuantitationType> getExpressionTypeMap() {
        return EXPRESSION_TYPE_MAP;
    }
}
