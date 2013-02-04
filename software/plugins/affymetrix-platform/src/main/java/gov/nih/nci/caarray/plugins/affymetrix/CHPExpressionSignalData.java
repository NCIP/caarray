//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import affymetrix.calvin.data.ProbeSetQuantificationData;
import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.chp.FusionCHPQuantificationData;
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
 * Supports expression data in legacy Affymetrix CHP file formats.
 * @author John Scott
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
class CHPExpressionSignalData extends AbstractCHPData<ProbeSetQuantificationData> {
    /**
     * @param fusionCHPQuantificationData
     */
    CHPExpressionSignalData(final FusionCHPQuantificationData fusionCHPQuantificationData) {
        this.fusionCHPQuantificationData = fusionCHPQuantificationData;
    }

    private static final Map<String, AffymetrixExpressionSignalChpQuantitationType> EXPRESSION_TYPE_MAP =
            new HashMap<String, AffymetrixExpressionSignalChpQuantitationType>();

    private static void initializeExpressionTypeMap() {
        for (final AffymetrixExpressionSignalChpQuantitationType descriptor
                : AffymetrixExpressionSignalChpQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    static {
        initializeExpressionTypeMap();
    }

    private final FusionCHPQuantificationData fusionCHPQuantificationData;

    protected FusionCHPQuantificationData getData() {
        return fusionCHPQuantificationData;
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
            final ProbeSetQuantificationData entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final AffymetrixExpressionSignalChpQuantitationType typeDescriptor = 
            getExpressionTypeDescriptor(quantitationType);
        switch (typeDescriptor) {
        case CHP_PROBE_SET_NAME:
            ((StringColumn) column).getValues()[index] = entry.getName();
            break;
        case CHP_SIGNAL:
            ((FloatColumn) column).getValues()[index] = entry.getQuantification();
            break;
        default:
            throw new IllegalArgumentException("Unsupported QuantitationType for expression CHP data: "
                    + quantitationType);
        }
    }

    @Override
    protected ProbeSetQuantificationData getEntry(int index) {
        try {
            return getData().getQuantificationEntry(index);
        } catch (final IOException e) {
            throw new ArrayDataIOException(e);
        } catch (final UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        }
    }

    private AffymetrixExpressionSignalChpQuantitationType getExpressionTypeDescriptor(
            final QuantitationType quantitationType) {
        return getExpressionTypeMap().get(quantitationType.getName());
    }

    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AffymetrixExpressionSignalChpQuantitationType.values();
    }

    ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return AffymetrixArrayDataTypes.AFFYMETRIX_SIGNAL_CHP;
    }

    Map<String, AffymetrixExpressionSignalChpQuantitationType> getExpressionTypeMap() {
        return EXPRESSION_TYPE_MAP;
    }
}
