//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.arraydata.AbstractArrayDataServiceTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Before;

/**
 * Affymetrix-specific tests of array data system. These tests need to be refactored to be independent of
 * ArrayDataServiceTest
 */
@SuppressWarnings("PMD")
public class AffymetrixArrayDataServiceTest extends AbstractArrayDataServiceTest {
    private CaArrayFile focusCel;
    private CaArrayFile focusCalvinCel;
    private CaArrayFile focusChp;
    private CaArrayFile focusCalvinChp;
    private Experiment experiment;

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AffymetrixModule());
    }

    @Before
    public void onSetup() {
        focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CEL,
                AFFY_TEST3_LSID_OBJECT_ID);
        focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID);
        focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP,
                AFFY_TEST3_LSID_OBJECT_ID);

        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
    }

    @Test
    public void testImportRawAndDerivedSameName() throws InvalidDataFileException {
        // tests that imports of raw and derived data files with same base name go
        // to the same hybridization chain
        this.importData(DEFAULT_IMPORT_OPTIONS);
        this.assertData(2, 2, 1, 1, true);
    }

    @Test
    public void testImportSingleAnnotationChain() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        final DataImportOptions options = DataImportOptions.getAutoCreateSingleOptions("TEST_NAME");
        this.importData(options);
        this.assertData(1, 1, 2, 2, false);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImportToTargetSources() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        Source targetSrc1 = this.addSourceToFocusCel(1L, "targetSrc1");
        Source targetSrc2 = this.addSourceToFocusCel(2L, "targetSrc2");
        focusCel.getProject().getExperiment().getSources().addAll(Arrays.asList(targetSrc1, targetSrc2));

        final DataImportOptions options = DataImportOptions.getAssociateToBiomaterialsOptions(
                ExperimentDesignNodeType.SOURCE, Arrays.asList(targetSrc1.getId(), targetSrc2.getId()));
        this.importData(options);

        assertEquals(2, this.experiment.getSources().size());
        assertEquals(2, this.experiment.getSamples().size());
        assertEquals(2, this.experiment.getHybridizations().size());
        assertEquals(2, targetSrc1.getSamples().size());
        assertEquals(2, targetSrc2.getSamples().size());
    }

    private Source addSourceToFocusCel(Long id, String sourceName) {
        Source source = new Source();
        source.setId(id);
        source.setName(sourceName);
        source.setExperiment(this.focusCel.getProject().getExperiment());
        this.searchDaoStub.save(source);

        this.focusCel.getProject().getExperiment().getSources().add(source);
        return source;
    }

    private void importData(DataImportOptions dataImportOptions) throws InvalidDataFileException {
        this.arrayDataService.importData(focusCel, true, dataImportOptions);
        this.arrayDataService.importData(focusCalvinCel, true, dataImportOptions);
        this.arrayDataService.importData(focusChp, true, dataImportOptions);
        this.arrayDataService.importData(focusCalvinChp, true, dataImportOptions);

        this.experiment = focusCel.getProject().getExperiment();
    }

    private void assertData(int expectedSamples, int expectedHybridizations,
        int expectedRawData, int expectedDerivedData, boolean testForImportRawAndDerivedSameName) {
        checkAnnotation(focusCel, expectedSamples);
        assertEquals(expectedHybridizations, this.experiment.getHybridizations().size());
        for (Hybridization h : this.experiment.getHybridizations()) {
            assertEquals(expectedRawData, h.getRawDataCollection().size());
            assertEquals(expectedDerivedData, h.getDerivedDataCollection().size());
            if (testForImportRawAndDerivedSameName) {
                assertForImportRawAndDerivedSameName(h);
            }
        }
    }

    private void assertForImportRawAndDerivedSameName(Hybridization h) {
        if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCel)) {
            assertEquals(focusChp, h.getDerivedDataCollection().iterator().next().getDataFile());
        } else if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCalvinCel)) {
            assertEquals(focusCalvinChp, h.getDerivedDataCollection().iterator().next().getDataFile());
        } else {
            fail("Expected hybridization to be linked to either focus or calvin focus CEL");
        }
    }

    @Test
    public void testCreateAnnotation() throws InvalidDataFileException {
        testExistingAnnotationNotOverwritten();
    }

    private void testExistingAnnotationNotOverwritten() throws InvalidDataFileException {
        final CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        final RawArrayData celData = new RawArrayData();
        final Hybridization hybridization = new Hybridization();
        celData.addHybridization(hybridization);
        hybridization.addArrayData(celData);
        celData.setDataFile(celFile);
        assertNull(celData.getType());
        this.daoFactoryStub.getArrayDao().save(celData);
        this.arrayDataService.importData(celFile, true, DEFAULT_IMPORT_OPTIONS);
        assertNotNull(celData.getType());
        assertEquals(celData, this.daoFactoryStub.getArrayDao().getArrayData(celFile.getId()));
        assertEquals(hybridization, celData.getHybridizations().iterator().next());
    }

    private CaArrayFile getChpCaArrayFile(File chp, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(chp, ChpHandler.CHP_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getCelCaArrayFile(File cel, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(cel, CelHandler.CEL_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }
}
