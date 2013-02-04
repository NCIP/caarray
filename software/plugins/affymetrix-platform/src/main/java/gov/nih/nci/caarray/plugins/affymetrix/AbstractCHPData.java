//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

import java.util.Set;

/**
 * Abstract base class supporting all formats and versions of Affymetrix CHP files.
 *
 * @author John Scott
 * @param <TFusionDataEntry> an AffxFusion library class containing information about an entry in an Affymetrix file
 */
abstract class AbstractCHPData<TFusionDataEntry> {
    /**
     * @return the number of probe sets in the CHP file
     */
    abstract int getNumProbeSets();

    /**
     * @return the chip type used to produce the data in the CHP file
     */
    abstract String getChipType();

    /**
     * @param typeSet           the applicable quantitation types
     * @param hybridizationData the data to be set
     */
    void loadData(final Set<QuantitationType> typeSet, final HybridizationData hybridizationData) {
        final int numberOfProbeSets = getNumProbeSets();
        for (int probeSetIndex = 0; probeSetIndex < numberOfProbeSets; probeSetIndex++) {
            TFusionDataEntry entry = getEntry(probeSetIndex);
            handleEntry(hybridizationData, entry, typeSet, probeSetIndex);
        }
    }

    /**
     * @return the quantitation types supported by the CHP file
     */
    abstract QuantitationTypeDescriptor[] getQuantitationTypeDescriptors();

    /**
     * @return the type descriptor for the data supported by the CHP file
     */
    abstract ArrayDataTypeDescriptor getArrayDataTypeDescriptor();

    /**
     * @param hybridizationData the data to be set
     * @param entry             the entry containing the data to be retrieved
     * @param typeSet           the applicable quantitation types
     * @param probeSetIndex     the index of the entry
     */
    private void handleEntry(final HybridizationData hybridizationData, final TFusionDataEntry entry,
                             final Set<QuantitationType> typeSet, final int probeSetIndex) {
        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                setExpressionValue(column, probeSetIndex, entry);
            }
        }
    }

    /**
     * @param column the column containing the value to set
     * @param index  the row
     * @param entry  the entry containing the value to retrieve
     */
    protected abstract void setExpressionValue(AbstractDataColumn column, int index,
                                               TFusionDataEntry entry);

    /**
     * Get the entry for the given probe set index.
     *
     * @param index the index of the probe set
     * @return the entry for the probe set
     */
    protected abstract TFusionDataEntry getEntry(int index);
}
