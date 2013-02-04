//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.application.arraydata.ArrayDataException;
import gov.nih.nci.caarray.application.arraydata.ArrayDataIOException;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionExpressionProbeSetResults;

/**
 * Supports expression data in legacy Affymetrix CHP file formats.
 * @author John Scott
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
class CHPExpressionMAS5Data extends AbstractCHPLegacyData<FusionExpressionProbeSetResults> {
    private static final Logger LOG = Logger.getLogger(CHPExpressionMAS5Data.class);
    
    /**
     * @param data
     */
    CHPExpressionMAS5Data(final FusionCHPLegacyData data) {
        super(data);
    }

    private static final Map<String, AffymetrixExpressionChpQuantitationType> EXPRESSION_TYPE_MAP =
            new HashMap<String, AffymetrixExpressionChpQuantitationType>();

    private static void initializeExpressionTypeMap() {
        for (final AffymetrixExpressionChpQuantitationType descriptor
                : AffymetrixExpressionChpQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    static {
        initializeExpressionTypeMap();
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    // long switch statement
    protected void setExpressionValue(final AbstractDataColumn column, final int index,
                                      final FusionExpressionProbeSetResults entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final AffymetrixExpressionChpQuantitationType typeDescriptor = getExpressionTypeDescriptor(quantitationType);
        int intValue = Integer.MIN_VALUE;
        switch (typeDescriptor) {
            case CHP_CHANGE:
                ((StringColumn) column).getValues()[index] = entry.getChangeString();
                break;
            case CHP_COMMON_PAIRS:
                intValue = entry.getNumCommonPairs().toInt();
                if (Short.MAX_VALUE < intValue) {
                    LOG.warn("Truncating int value '" + intValue + "' into short value '" + (short) intValue
                            + "' for FusionExpressionProbeSetResult entry.");
                }
                ((ShortColumn) column).getValues()[index] = (short) intValue;
                break;
            case CHP_CHANGE_PVALUE:
                ((FloatColumn) column).getValues()[index] = entry.getChangePValue();
                break;
            case CHP_DETECTION:
                ((StringColumn) column).getValues()[index] = entry.getDetectionString();
                break;
            case CHP_DETECTION_PVALUE:
                ((FloatColumn) column).getValues()[index] = entry.getDetectionPValue();
                break;
            case CHP_PAIRS:
                intValue = entry.getNumCommonPairs().toInt();
                if (Short.MAX_VALUE < intValue) {
                    LOG.warn("Truncating int value '" + intValue + "' into short value '" + (short) intValue
                            + "' for FusionExpressionProbeSetResult entry '" + entry.toString() + "'.");
                }
                ((ShortColumn) column).getValues()[index] = (short) intValue;
                break;
            case CHP_PAIRS_USED:
                intValue = entry.getNumCommonPairs().toInt();
                if (Short.MAX_VALUE < intValue) {
                    LOG.warn("Truncating int value '" + intValue + "' into short value '" + (short) intValue
                            + "' for FusionExpressionProbeSetResult entry '" + entry.toString() + "'.");
                }
                ((ShortColumn) column).getValues()[index] = (short) intValue;
                break;
            case CHP_SIGNAL:
                ((FloatColumn) column).getValues()[index] = entry.getSignal();
                break;
            case CHP_SIGNAL_LOG_RATIO:
                ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatio();
                break;
            case CHP_SIGNAL_LOG_RATIO_HIGH:
                ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatioHigh();
                break;
            case CHP_SIGNAL_LOG_RATIO_LOW:
                ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatioLow();
                break;
            default:
                throw new IllegalArgumentException("Unsupported QuantitationType for expression CHP data: "
                        + quantitationType);
        }
    }

    @Override
    protected FusionExpressionProbeSetResults getEntry(int index) {
        try {
            final FusionExpressionProbeSetResults entry = new FusionExpressionProbeSetResults();
            getData().getExpressionResults(index, entry);
            return entry;
        } catch (final IOException e) {
            throw new ArrayDataIOException(e);
        } catch (final UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        }
    }


    private AffymetrixExpressionChpQuantitationType getExpressionTypeDescriptor(
            final QuantitationType quantitationType) {
        return getExpressionTypeMap().get(quantitationType.getName());
    }

    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AffymetrixExpressionChpQuantitationType.values();
    }

    ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP;
    }

    private Map<String, AffymetrixExpressionChpQuantitationType> getExpressionTypeMap() {
        return EXPRESSION_TYPE_MAP;
    }
}
