//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertFalse;
import gov.nih.nci.caarray.application.arraydata.AbstractArrayDataServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;

import org.junit.Test;

/**
 * Affymetrix-specific tests of array data system. These tests need to be refactored to be independent of
 * ArrayDataServiceTest
 */
@SuppressWarnings("PMD")
public class AgilentArrayDataServiceTest extends AbstractArrayDataServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AgilentModule());
    }

    @Test
    public void testAgilentRawTextAcghRejectedIfNoMageTab() throws InvalidDataFileException {
        final CaArrayFile expFile = getAgilentRawTextCaArrayFile(AgilentArrayDataFiles.TEST_ACGH_RAW_TEXT,
                AGILENT_ACGH_LSID_OBJECT_ID);
        final MageTabDocumentSet mTabSet = null;
        final FileValidationResult validationResult = this.arrayDataService.validate(expFile, mTabSet, false);
        assertFalse(validationResult.isValid());
    }

    private CaArrayFile getAgilentRawTextCaArrayFile(File cel, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(cel, AgilentRawTextDataHandler.RAW_TXT_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }
}
