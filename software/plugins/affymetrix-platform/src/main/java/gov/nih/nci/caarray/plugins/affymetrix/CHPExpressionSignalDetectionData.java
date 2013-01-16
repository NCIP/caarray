//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

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

import affymetrix.calvin.data.ProbeSetQuantificationDetectionData;
import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.chp.FusionCHPQuantificationDetectionData;

/**
 * Supports expression signal detection data in Affymetrix CHP file formats.
 * @author John Scott
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
class CHPExpressionSignalDetectionData extends AbstractCHPData<ProbeSetQuantificationDetectionData> {
    /**
     * @param FusionCHPQuantificationDetectionData
     */
    CHPExpressionSignalDetectionData(final FusionCHPQuantificationDetectionData fusionCHPQuantificationDetectionData) {
        this.fusionCHPQuantificationDetectionData = fusionCHPQuantificationDetectionData;
    }

    private static final Map<String, AffymetrixExpressionSignalDetectionChpQuantitationType> EXPRESSION_TYPE_MAP =
            new HashMap<String, AffymetrixExpressionSignalDetectionChpQuantitationType>();

    private static void initializeExpressionTypeMap() {
        for (final AffymetrixExpressionSignalDetectionChpQuantitationType descriptor
                : AffymetrixExpressionSignalDetectionChpQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    static {
        initializeExpressionTypeMap();
    }

    private final FusionCHPQuantificationDetectionData fusionCHPQuantificationDetectionData;

    protected FusionCHPQuantificationDetectionData getData() {
        return fusionCHPQuantificationDetectionData;
    }

    String getChipType() {
        return getData().getArrayType();
    }

    int getNumProbeSets() {
        return getData().getEntryCount();
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    // long switch statement
    protected void setExpressionValue(final AbstractDataColumn column, final int index,
            final ProbeSetQuantificationDetectionData entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final AffymetrixExpressionSignalDetectionChpQuantitationType typeDescriptor = 
            getExpressionTypeDescriptor(quantitationType);
        switch (typeDescriptor) {
        case CHP_PROBE_SET_NAME:
            ((StringColumn) column).getValues()[index] = entry.getName();
            break;
        case CHP_SIGNAL:
            ((FloatColumn) column).getValues()[index] = entry.getQuantification();
            break;
        case CHP_DETECTION_PVALUE:
            ((FloatColumn) column).getValues()[index] = entry.getPValue();
            break;
        default:
            throw new IllegalArgumentException("Unsupported QuantitationType for expression CHP data: "
                    + quantitationType);
        }
    }

    @Override
    protected ProbeSetQuantificationDetectionData getEntry(int index) {
        try {
            return getData().getQuantificationDetectionEntry(index);
        } catch (final IOException e) {
            throw new ArrayDataIOException(e);
        } catch (final UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        }
    }

    private AffymetrixExpressionSignalDetectionChpQuantitationType getExpressionTypeDescriptor(
            final QuantitationType quantitationType) {
        return getExpressionTypeMap().get(quantitationType.getName());
    }

    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AffymetrixExpressionSignalDetectionChpQuantitationType.values();
    }

    ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return AffymetrixArrayDataTypes.AFFYMETRIX_SIGNAL_CHP;
    }

    Map<String, AffymetrixExpressionSignalDetectionChpQuantitationType> getExpressionTypeMap() {
        return EXPRESSION_TYPE_MAP;
    }
}
