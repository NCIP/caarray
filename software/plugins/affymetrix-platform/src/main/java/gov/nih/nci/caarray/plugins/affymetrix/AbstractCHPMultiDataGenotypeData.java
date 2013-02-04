//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.affymetrix;

import affymetrix.calvin.data.CHPMultiDataData;
import affymetrix.calvin.data.ProbeSetMultiDataGenotypeData;
import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.chp.FusionCHPMultiDataData;

import gov.nih.nci.caarray.application.arraydata.ArrayDataException;
import gov.nih.nci.caarray.application.arraydata.ArrayDataIOException;

import java.io.IOException;

/**
 * Abstract base class supporting Command Console formatted Affymetrix CHP files with MultiData
 * containing genotyping data.
 * @author John Scott
 */
abstract class AbstractCHPMultiDataGenotypeData extends AbstractCHPMultiData<ProbeSetMultiDataGenotypeData> {
    /**
     *
     * @param fusionCHPMultiDataData the FusionCHPMultiDataData obtained from the AffxFusion library.
     */
    AbstractCHPMultiDataGenotypeData(final FusionCHPMultiDataData fusionCHPMultiDataData) {
        super(fusionCHPMultiDataData);
    }

    /**
     * @inheritDoc
     * @param index the index of the probe set
     * @return @inheritDoc
     */
    @Override
    protected ProbeSetMultiDataGenotypeData getEntry(int index) {
         try {
             return getData().getGenotypeEntry(CHPMultiDataData.MultiDataType.GenotypeMultiDataType, index);
         } catch (final IOException e) {
             throw new ArrayDataIOException(e);
         } catch (final UnsignedOutOfLimitsException e) {
             throw new ArrayDataException(e);
         }
     }

}
