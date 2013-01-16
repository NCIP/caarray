//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.genepix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * GAL-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of ArrayDesignService.
 * 
 * @author dkokotov
 */
public class GalArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new GenepixModule());
    }

    @Test
    public void testImportDesignDetails_Genepix() {
        final ArrayDesign design = new ArrayDesign();
        this.arrayDesignService.importDesignDetails(design);
        assertNull(design.getNumberOfFeatures());

        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "Demo", 8064);
        design.getDesignFiles().clear();
        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "MEEBO", 38880);
    }

    private void checkGenepixDesign(final ArrayDesign design, final String expectedName,
            final int expectedNumberOfFeatures) {
        assertEquals(expectedName, design.getName());
        assertEquals(expectedNumberOfFeatures, design.getNumberOfFeatures().intValue());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getFeatures().size());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getProbes().size());
        final Iterator<PhysicalProbe> probeIt = design.getDesignDetails().getProbes().iterator();
        while (probeIt.hasNext()) {
            final PhysicalProbe probe = probeIt.next();
            assertFalse(StringUtils.isBlank(probe.getName()));
            assertEquals(1, probe.getFeatures().size());
            final Feature feature = probe.getFeatures().iterator().next();
            assertTrue(feature.getBlockColumn() > 0);
            assertTrue(feature.getBlockRow() > 0);
            assertTrue(feature.getColumn() > 0);
            assertTrue(feature.getRow() > 0);
        }
    }

    private CaArrayFile getGenepixCaArrayFile(final File file) {
        return getCaArrayFile(file, GalDesignHandler.GAL_FILE_TYPE);
    }

    @Test
    public void testValidateDesign_Genepix() {
        CaArrayFile designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL);
        ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.TWO_K_GAL);
        result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO);
        result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
    }
}
