//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import java.util.HashMap;
import java.util.Map;

import affymetrix.calvin.data.DataSetHeader;
import affymetrix.calvin.data.ProbeSetMultiDataBase;
import affymetrix.calvin.parameter.ParameterNameValue;
import affymetrix.fusion.chp.FusionCHPMultiDataData;

/**
 * Abstract base class supporting Command Console formatted Affymetrix CHP files with MultiData.
 * @author John Scott
 * @param <TFusionDataEntry> an AffxFusion library class containing information about an entry in an Affymetrix file
 */
abstract class AbstractCHPMultiData<TFusionDataEntry> extends AbstractCHPData<TFusionDataEntry> {
    private final FusionCHPMultiDataData fusionCHPMultiDataData;

    /**
     *
     * @param fusionCHPMultiDataData the FusionCHPMultiDataData obtained from the AffxFusion library
     */
    AbstractCHPMultiData(final FusionCHPMultiDataData fusionCHPMultiDataData) {
        this.fusionCHPMultiDataData = fusionCHPMultiDataData;
    }

    /**
     * @return @inheritDoc
     */
    protected FusionCHPMultiDataData getData() {
        return getFusionCHPMultiDataData();
    }

    /**
     * @return @inheritDoc
     */
    String getChipType() {
         return getData().getArrayType();
    }

    /**
     * @return @inheritDoc
     */
    int getNumProbeSets() {
        return getDataSet().getRowCnt();
    }

    /**
     *
     * @return the single data set from the single data group in the CHP file
     */
    protected DataSetHeader getDataSet() {
         return getData().getGenericData().findDataGroupHeader(0).getDataSet(0);
     }

    /**
     * 
     * @param entry A ProbeSetMultiDataGenotypeData
     * @return a mapping from name to ParameterNameValue for the metric values in entry.
     */
    protected Map<String, ParameterNameValue> getMetricMap(final ProbeSetMultiDataBase entry) {
        Map<String, ParameterNameValue> metricMap = new HashMap<String, ParameterNameValue>();
        for (ParameterNameValue metric : entry.getMetrics()) {
            metricMap.put(metric.getName(), metric);
        }
        return metricMap;
    }

    /**
     * 
     * @return  the FusionCHPMultiDataData obtained from the AffxFusion library
     */
    FusionCHPMultiDataData getFusionCHPMultiDataData() {
        return fusionCHPMultiDataData;
    }
}
