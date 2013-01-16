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
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import affymetrix.calvin.data.ColumnInfo;
import affymetrix.calvin.data.DataGroupHeader;
import affymetrix.calvin.data.DataSetHeader;
import affymetrix.calvin.data.FileHeader;
import affymetrix.calvin.data.GenericDataHeader;
import affymetrix.calvin.data.ProbeSetMultiDataCopyNumberData;
import affymetrix.calvin.data.CHPMultiDataData.MultiDataType;
import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.calvin.parameter.ParameterNameValue;
import affymetrix.fusion.chp.FusionCHPMultiDataData;

/**
 * Supports Copy Number Affymetrix CHP file formats.
 * @author gax
 * @since 2.4.0
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
class CnchpData extends AbstractCHPMultiData<ProbeSetMultiDataCopyNumberData> {

    private final Map<String, CopyNumberQuantitationType> qTypes;
    /**
     * @param fusionCHPMultiDataData
     */
    public CnchpData(final FusionCHPMultiDataData fusionCHPMultiDataData,
            Map<String, CopyNumberQuantitationType> qTypes) {
        super(fusionCHPMultiDataData);
        this.qTypes = qTypes;
    }

    static final Map<String, CopyNumberQuantitationType> CN4_TYPE_MAP = getCN4Map();
    static final Map<String, CopyNumberQuantitationType> CN5_TYPE_MAP = getCN5Map();

    private static Map<String, CopyNumberQuantitationType> getCN4Map() {
        Map<String, CopyNumberQuantitationType> tmp = new HashMap<String, CopyNumberQuantitationType>();
        // CN4 types
        addMap(tmp, CopyNumberQuantitationType.PROBE_SET_NAME);
        addMap(tmp, CopyNumberQuantitationType.CHROMOSOME);
        addMap(tmp, CopyNumberQuantitationType.POSITION);
        addMap(tmp, CopyNumberQuantitationType.CN4_CN_STATE);
        tmp.put("CNState", CopyNumberQuantitationType.CN4_CN_STATE);
        addMap(tmp, CopyNumberQuantitationType.LOG2RATIO);
        addMap(tmp, CopyNumberQuantitationType.HMM_MEDIAN_LOG2RATIO);
        addMap(tmp, CopyNumberQuantitationType.NEG_LOG10PVALUE);
        addMap(tmp, CopyNumberQuantitationType.CHIP_NUM);
        return Collections.unmodifiableMap(tmp);
    }
    private static Map<String, CopyNumberQuantitationType> getCN5Map() {
        Map<String, CopyNumberQuantitationType> tmp = new HashMap<String, CopyNumberQuantitationType>();
        // CN5 types
        addMap(tmp, CopyNumberQuantitationType.PROBE_SET_NAME);
        addMap(tmp, CopyNumberQuantitationType.CHROMOSOME);
        addMap(tmp, CopyNumberQuantitationType.POSITION);
        addMap(tmp, CopyNumberQuantitationType.CN5_CN_STATE);
        tmp.put("CNState", CopyNumberQuantitationType.CN5_CN_STATE);
        addMap(tmp, CopyNumberQuantitationType.LOG2RATIO);
        addMap(tmp, CopyNumberQuantitationType.SMOOTH_SIGNAL);
        addMap(tmp, CopyNumberQuantitationType.LOH);
        addMap(tmp, CopyNumberQuantitationType.ALLELE_DIFFERENCE);
        return Collections.unmodifiableMap(tmp);
    }

    private static void addMap(Map<String, CopyNumberQuantitationType> map, CopyNumberQuantitationType qType) {
        map.put(qType.getName(), qType);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    // long switch statement
    @Override
    protected void setExpressionValue(final AbstractDataColumn column, final int index,
                                    final ProbeSetMultiDataCopyNumberData entry) {
        final QuantitationType quantitationType = column.getQuantitationType();
        final CopyNumberQuantitationType typeDescriptor
            = getExpressionTypeDescriptor(quantitationType);
        try {
            Map<String, ParameterNameValue> metricMap = getMetricMap(entry);

            switch (typeDescriptor) {
                case ALLELE_DIFFERENCE:
                    ((FloatColumn) column).getValues()[index]
                          = metricMap.get(CopyNumberQuantitationType.ALLELE_DIFFERENCE.getName()).getValueFloat();
                    break;
                case CHIP_NUM:
                    ((ShortColumn) column).getValues()[index] =
                            metricMap.get(CopyNumberQuantitationType.CHIP_NUM.getName()).getValueUInt8().toShort();
                    break;
                case CHROMOSOME:
                    // CHECKSTYLE:OFF
                    // 0xff is not really a magic number
                    ((ShortColumn) column).getValues()[index] = (short) (0xff & entry.getChr());
                    // CHECKSTYLE:ON
                    break;
                case CN4_CN_STATE:
                    ((ShortColumn) column).getValues()[index] = 
                            metricMap.get("CNState").getValueUInt8().toShort();
                    break;
                case CN5_CN_STATE:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get("CNState").getValueFloat();
                    break;
                case HMM_MEDIAN_LOG2RATIO:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get(CopyNumberQuantitationType.HMM_MEDIAN_LOG2RATIO.getName()).getValueFloat();
                    break;
                case LOG2RATIO:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get(CopyNumberQuantitationType.LOG2RATIO.getName()).getValueFloat();
                    break;
                case LOH:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get(CopyNumberQuantitationType.LOH.getName()).getValueFloat();
                    break;
                case NEG_LOG10PVALUE:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get(CopyNumberQuantitationType.NEG_LOG10PVALUE.getName()).getValueFloat();
                    break;
                case POSITION:
                    ((IntegerColumn) column).getValues()[index] = entry.getPosition();
                    break;
                case PROBE_SET_NAME:
                    ((StringColumn) column).getValues()[index] = entry.getName();
                    break;
                case SMOOTH_SIGNAL:
                    ((FloatColumn) column).getValues()[index] 
                            = metricMap.get(CopyNumberQuantitationType.SMOOTH_SIGNAL.getName()).getValueFloat();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported QuantitationType for CNCHP data: "
                            + quantitationType);
            }
        } catch (UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChipType() {
        String key;
        FusionCHPMultiDataData d = getData();
        if ("CN4".equals(d.getAlgName())) {
            key = "affymetrix-algorithm-param-ChipType1";
        } else {
            key = "affymetrix-array-type";
        }
        FileHeader h = d.getGenericData().getHeader();
        GenericDataHeader gh = h.getGenericDataHdr();
        for (ParameterNameValue nv : gh.getNameValParams()) {
            if (key.equals(nv.getName())) {
                return nv.getValueText();
            }
        }
        return null;
    }

    private CopyNumberQuantitationType getExpressionTypeDescriptor(
            final QuantitationType quantitationType) {
        return getExpressionTypeMap().get(quantitationType.getName());
    }

    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        DataSetHeader dsh = getDataSet();
        int count = getDataSet().getColumnCnt();
        ArrayList<CopyNumberQuantitationType> desc = new ArrayList<CopyNumberQuantitationType>();
        for (int i = 0; i < count; i++) {
            ColumnInfo col = dsh.getColumnInfo(i);
            CopyNumberQuantitationType qt = getExpressionTypeMap().get(col.getName());
            if (qt != null) {
                desc.add(qt);
            }
        }
        return desc.toArray(new CopyNumberQuantitationType[desc.size()]);
    }

    @Override
    protected DataSetHeader getDataSet() {
        DataGroupHeader hdr = getData().getGenericData().findDataGroupHeader("MultiData");
        return hdr.findDataSetHeader("CopyNumber");
    }



    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
         return AffymetrixArrayDataTypes.AFFYMETRIX_COPY_NUMBER_CHP;
    }

    private Map<String, CopyNumberQuantitationType> getExpressionTypeMap() {
        return qTypes;
    }

    
    @Override
    protected ProbeSetMultiDataCopyNumberData getEntry(int index) {
        try {
            return getData().getCopyNumberEntry(MultiDataType.CopyNumberMultiDataType, index);
        } catch (final IOException e) {
            throw new ArrayDataIOException(e);
        } catch (final UnsignedOutOfLimitsException e) {
            throw new ArrayDataException(e);
        }
    }

   
}
