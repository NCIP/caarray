//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.SNPProbeAnnotation;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

/**
 * Illumina-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of
 * ArrayDesignService.
 *
 * @author dkokotov
 */
public class IlluminaArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new IlluminaModule());
    }

    @Test
    public void testValidateDesign_IlluminaHumanWG6() {
        this.testValidDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        this.testInValidDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_HEADERS, true);
        this.testInValidDesign(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_CONTENT, false);
        this.testInValidDesign(AffymetrixArrayDataFiles.TEST3_CEL, false);
    }

    private void testValidDesign(File file) {
        this.testValidOrInvalidDesgin(file, true, false);
    }

    private void testInValidDesign(File file, boolean assertValidationResultMessages) {
        this.testValidOrInvalidDesgin(file, false, assertValidationResultMessages);
    }

    private void testValidOrInvalidDesgin(File file, boolean isValid, boolean assertValidationResultMessages) {
        final CaArrayFile designFile = getIlluminaCaArrayFile(file);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        if (isValid) {
            assertTrue(result.isValid());
        } else {
            assertFalse(result.isValid());
        }

        if (assertValidationResultMessages) {
            assertEquals(1, result.getFileValidationResults().size());
            assertEquals("Unable to read file", result.getFileValidationResults().get(0).getMessages().get(0).getMessage());
            assertEquals("Unable to read file", designFile.getValidationResult().getMessages().get(0).getMessage());
        }
    }

    @Test
    public void testImportDesignAndDetails_IlluminaHumanHap300() {
        this.testImportDesignAndDetails(IlluminaArrayDesignFiles.HUMAN_HAP_300_SMALL_CSV, "HumanHap300v2_A_small",
                "HumanHap300v2_A_small", 18237, 18237, true);
    }

    @Test
    public void testImportDesignAndDetails_IlluminaHumanWG6() {
        this.testImportDesignAndDetails(IlluminaArrayDesignFiles.HUMAN_WG6_CSV, "Human_WG-6",
                "Human_WG-6", 47296, 47296, false);
    }

    private void testImportDesignAndDetails(File file, String name, String lsidObjectId, int expectedFeatures,
            int expectedProbes, boolean validateFile) {
        final CaArrayFile designFile = getIlluminaCaArrayFile(file);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        if (validateFile) {
            assertTrue(result.isValid());
        }

        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(arrayDesign);
        this.arrayDesignService.importDesignDetails(arrayDesign);

        assertEquals(name, arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals(lsidObjectId, arrayDesign.getLsidObjectId());
        assertEquals(expectedFeatures, arrayDesign.getNumberOfFeatures().intValue());
        assertEquals(expectedProbes, arrayDesign.getDesignDetails().getProbes().size());

        for (final LogicalProbe probe : arrayDesign.getDesignDetails().getLogicalProbes()) {
            assertNotNull(probe);
            assertNotNull(probe.getAnnotation());
            final SNPProbeAnnotation annotation = (SNPProbeAnnotation) probe.getAnnotation();
            assertNotNull(annotation.getDbSNPId());
            assertNotNull(annotation.getDbSNPVersion());
            assertNotNull(annotation.getAlleleA());
            assertNotNull(annotation.getAlleleB());
            assertNotNull(annotation.getPhysicalPosition());
            assertEquals(probe.getName(), annotation.getDbSNPId());
            assertEquals(arrayDesign.getDesignDetails(), probe.getArrayDesignDetails());
        }
    }

    private CaArrayFile getIlluminaCaArrayFile(final File file) {
        return getCaArrayFile(file, IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
    }
}
