//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.genepix;

import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author dkokotov
 * 
 */
public class GenepixGprHandlerTest extends AbstractHandlerTest {
    private static final String GAL_DERISI_LSID_OBJECT_ID = "JoeDeRisi-fix";
    private static final String GAL_YEAST1_LSID_OBJECT_ID = "Yeast1";
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new GenepixModule());
    }

    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        if (GAL_DERISI_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 126, 126, null);
        } else if (GAL_YEAST1_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 126, 126, null);
        } else {
            throw new IllegalArgumentException("Unsupported request design");
        }
    }

    @Test
    public void testImportGenepix() throws InvalidDataFileException {
        final GenepixQuantitationType[] expectedTypes =
                new GenepixQuantitationType[] {X, Y, DIA, F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN,
                        B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT, F532_MEDIAN, F532_MEAN,
                        F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD,
                        F532_PERCENT_SAT, RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532,
                        MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532, RGN_R2_635_532, F_PIXELS,
                        B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532, F635_MEDIAN_B635,
                        F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, FLAGS };
        testImportGenepixFile(GenepixArrayDataFiles.GPR_3_0_6, expectedTypes, 1);
    }

    private void testImportGenepixFile(File gprFile, GenepixQuantitationType[] expectedTypes,
            int expectedNumberOfSamples) throws InvalidDataFileException {
        final CaArrayFile gprCaArrayFile = getGprCaArrayFile(gprFile, GAL_DERISI_LSID_OBJECT_ID);
        this.arrayDataService.importData(gprCaArrayFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData data =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(gprCaArrayFile.getId());
        assertNotNull(data);
        checkAnnotation(gprCaArrayFile, expectedNumberOfSamples);
        checkColumnTypes(data.getDataSet(), expectedTypes);
    }

    @Test
    public void testGenepixValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(GenepixArrayDataFiles.GPR_3_0_6);
        fileList.add(GenepixArrayDataFiles.GPR_4_0_1);
        fileList.add(GenepixArrayDataFiles.GPR_4_1_1);
        fileList.add(GenepixArrayDataFiles.GPR_5_0_1);
        fileList.add(GenepixArrayDataFiles.EXPORTED_IDF);
        fileList.add(GenepixArrayDataFiles.EXPORTED_SDRF);
        fileList.add(AffymetrixArrayDataFiles.TEST3_CEL);

        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_3_0_6, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_0_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_1_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_YEAST1_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
        testInvalidFile(getGprCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
    }

    @Test
    public void testGenepixNoMageTabValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(GenepixArrayDataFiles.GPR_3_0_6);
        fileList.add(GenepixArrayDataFiles.GPR_4_0_1);
        fileList.add(GenepixArrayDataFiles.GPR_4_1_1);
        fileList.add(GenepixArrayDataFiles.GPR_5_0_1);

        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_3_0_6, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_0_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_1_1, GAL_DERISI_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
        testInvalidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_YEAST1_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
    }

    @Test
    public void testGenepixData() throws InvalidDataFileException {
        final CaArrayFile gprFile = getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1, GAL_DERISI_LSID_OBJECT_ID);
        this.arrayDataService.importData(gprFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData gprData =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(gprFile.getId());
        final DataSet dataSet = gprData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(51, hybridizationData.getColumns().size());
        final IntegerColumn f635MedianColumn =
                (IntegerColumn) hybridizationData.getColumn(GenepixQuantitationType.F635_MEDIAN);
        assertNotNull(f635MedianColumn);
        assertEquals(6528, f635MedianColumn.getValues().length);
        assertEquals(138, f635MedianColumn.getValues()[0]);
        assertEquals(6, f635MedianColumn.getValues()[6527]);
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    private CaArrayFile getGprCaArrayFile(File gpr, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(gpr, GprHandler.GPR_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }
}
