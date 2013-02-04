//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Affymetrix-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of
 * ArrayDesignService.
 * 
 * @author dkokotov
 */
public class AffymetrixArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AffymetrixModule());
    }

    @Test
    public void testValidateDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHG_U133_Plus2() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHuEx() {
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_PGF);
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_CLF);
        final Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
        designFiles.add(pgfDesignFile);
        designFiles.add(clfDesignFile);
        final ValidationResult result = this.arrayDesignService.validateDesign(designFiles);
        assertTrue(result.isValid());
    }

    @Test
    public void testImportDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertEquals("Test3", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Test3", arrayDesign.getLsidObjectId());
        assertEquals(15876, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_AffymetrixTest3() throws PlatformFileReadException {
        final String name = "Test3";
        final File testFile = AffymetrixArrayDesignFiles.TEST3_CDF;
        final int expectedFeatureCount = 15876;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc2() throws PlatformFileReadException {
        final String name = "AGCC_2.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_2_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc3() throws PlatformFileReadException {
        final String name = "AGCC_3.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_3_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgccGcos() throws PlatformFileReadException {
        final String name = "AGCC_GCOS_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_GCOS_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @SuppressWarnings("deprecation")
    private void testImportDesignDetails(final String name, final File testFile, final int expectedFeatureCount) {
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(getAffymetrixCdfCaArrayFile(testFile));
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        assertEquals(name, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(name, design.getLsidObjectId());
        assertEquals(expectedFeatureCount, design.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixMapping10K() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEN_K_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getLsidObjectId());
        assertEquals(506944, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixHuEx() {
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(clfDesignFile);
        arrayDesign.addDesignFile(pgfDesignFile);
        this.arrayDesignService.importDesign(arrayDesign);
        assertTrue(pgfDesignFile.getValidationResult().isValid());
        assertTrue(clfDesignFile.getValidationResult().isValid());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getLsidObjectId());
        assertEquals(1024, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testImportDesignDetails_AffymetrixHuEx() throws PlatformFileReadException {
        final CaArrayFile clfDesignFile = getAffymetrixClfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getAffymetrixPgfCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(clfDesignFile);
        design.addDesignFile(pgfDesignFile);
        this.arrayDesignService.importDesign(design);
        assertTrue(pgfDesignFile.getValidationResult().isValid());
        assertTrue(clfDesignFile.getValidationResult().isValid());
        this.arrayDesignService.importDesignDetails(design);
        final String arrayDesignName = "HuEx-1_0-st-v1-test";
        assertEquals(arrayDesignName, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(arrayDesignName, design.getLsidObjectId());
        assertEquals(1024, design.getNumberOfFeatures().intValue());

        assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
        assertEquals(364, design.getDesignDetails().getProbes().size());
        assertEquals(1024, design.getDesignDetails().getFeatures().size());

        for (final PhysicalProbe probe : design.getDesignDetails().getProbes()) {
            assertTrue(probe.getFeatures().size() > 0);
            assertEquals(design.getDesignDetails(), probe.getArrayDesignDetails());
            for (final Feature abstractFeature : probe.getFeatures()) {
                final Feature feature = abstractFeature;
                assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
            }
        }

        for (final LogicalProbe logicalProbe : design.getDesignDetails().getLogicalProbes()) {
            assertEquals(design.getDesignDetails(), logicalProbe.getArrayDesignDetails());
            assertTrue(logicalProbe.getProbes().size() > 0);
            for (final PhysicalProbe physicalProbe : logicalProbe.getProbes()) {
                assertEquals(design.getDesignDetails(), physicalProbe.getArrayDesignDetails());
                assertTrue(physicalProbe.getFeatures().size() > 0);
                for (final Feature abstractFeature : physicalProbe.getFeatures()) {
                    final Feature feature = abstractFeature;
                    assertEquals(design.getDesignDetails(), feature.getArrayDesignDetails());
                    assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                    assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
                }
            }
        }
    }

    private CaArrayFile getAffymetrixCdfCaArrayFile(final File file) {
        return getCaArrayFile(file, CdfHandler.CDF_FILE_TYPE);
    }

    private CaArrayFile getAffymetrixPgfCaArrayFile(final File file) {
        return getCaArrayFile(file, PgfClfDesignHandler.PGF_FILE_TYPE);
    }

    private CaArrayFile getAffymetrixClfCaArrayFile(final File file) {
        return getCaArrayFile(file, PgfClfDesignHandler.CLF_FILE_TYPE);
    }
}
