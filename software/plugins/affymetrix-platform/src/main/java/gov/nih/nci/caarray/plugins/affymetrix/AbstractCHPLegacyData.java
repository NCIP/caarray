//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import affymetrix.fusion.chp.FusionCHPLegacyData;

/**
 * Supports legacy Affymetrix CHP file formats.
 * @author John Scott
 * @param <TFusionDataEntry> an AffxFusion library class containing information about an entry in an Affymetrix file
 */
abstract class AbstractCHPLegacyData<TFusionDataEntry> extends AbstractCHPData<TFusionDataEntry> {

     private final FusionCHPLegacyData fusionCHPLegacyData;

    AbstractCHPLegacyData(final FusionCHPLegacyData fusionCHPLegacyData) {
        this.fusionCHPLegacyData = fusionCHPLegacyData;
    }

    protected FusionCHPLegacyData getData() {
        return fusionCHPLegacyData;
    }

    String getChipType() {
        return getData().getHeader().getChipType();
    }

    int getNumProbeSets() {
        return getData().getHeader().getNumProbeSets();
    }
}
