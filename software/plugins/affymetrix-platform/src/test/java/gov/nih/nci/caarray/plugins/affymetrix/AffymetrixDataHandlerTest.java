//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import affymetrix.calvin.exception.UnsignedOutOfLimitsException;
import affymetrix.fusion.cel.FusionCELData;
import affymetrix.fusion.cel.FusionCELFileEntryType;
import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionExpressionProbeSetResults;
import affymetrix.fusion.chp.FusionGenotypeProbeSetResults;

/**
 * @author dkokotov
 * 
 */
public class AffymetrixDataHandlerTest extends AbstractHandlerTest {
    private static final String AFFY_TEST3_LSID_OBJECT_ID = "Test3";
    private static final String AFFY_TEN_K_LSID_OBJECT_ID = "Mapping10K_Xba131";
    private static final String AFFY_TEN_K__ALT_LSID_OBJECT_ID = "Mapping10K_Xba131-xda";
    private static final String AFFY_FIFTY_K_HIND_LSID_OBJECT_ID = "Mapping50K_Hind240";
    private static final String BIRDSEED_SNP_TEST_LSID_OBJECT_ID = "BirdseedSNPTest";
    private static final String HG_FOCUS_LSID_OBJECT_ID = "HG-Focus";
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AffymetrixModule());
    }

    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        if (AFFY_TEST3_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createAffymetrixArrayDesign(lsidObjectId, AffymetrixArrayDesignFiles.TEST3_CDF);
        } else if (AFFY_TEN_K_LSID_OBJECT_ID.equals(lsidObjectId)
                || AFFY_TEN_K__ALT_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createAffymetrixArrayDesign(lsidObjectId, AffymetrixArrayDesignFiles.TEN_K_CDF);
        } else if (HG_FOCUS_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 448, 448, AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        } else if (BIRDSEED_SNP_TEST_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 448, 448, AffymetrixArrayDesignFiles.HG_FOCUS_CDF);
        } else {
            final ArrayDesign ad = new ArrayDesign();
            final CaArrayFile f = new CaArrayFile();
            f.setFileStatus(FileStatus.IMPORTED);
            f.setFileType(CdfHandler.CDF_FILE_TYPE);
            ad.addDesignFile(f);
            return ad;
        }
    }

    @SuppressWarnings("deprecation")
    private ArrayDesign createAffymetrixArrayDesign(String lsidObjectId, File cdfFile) {
        try {
            final CdfReader reader = new CdfReader(cdfFile);
            final int rows = reader.getCdfData().getHeader().getRows();
            final int columns = reader.getCdfData().getHeader().getCols();
            final ArrayDesign design = createArrayDesign(lsidObjectId, rows, columns, cdfFile);
            // loading probe sets in reverse order to ensure that LogicalProbe DesignElementList loading
            // in AffymetrixChpHandler sorts list correctly.
            for (int i = reader.getCdfData().getHeader().getNumProbeSets() - 1; i >= 0; i--) {
                final LogicalProbe probeSet = new LogicalProbe();
                probeSet.setName(reader.getCdfData().getProbeSetName(i));
                design.getDesignDetails().getLogicalProbes().add(probeSet);
            }
            return design;
        } catch (final PlatformFileReadException e) {
            throw new IllegalStateException("Unexpected read exception", e);
        }
    }

    @Test
    public void testExpressionChpData() throws InvalidDataFileException {
        final DerivedArrayData chpData =
                getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        checkExpressionData(AffymetrixArrayDataFiles.TEST3_CHP, dataSet);
    }

    private void checkExpressionData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        final FusionCHPLegacyData chpData =
                FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        final FusionExpressionProbeSetResults results = new FusionExpressionProbeSetResults();
        final FloatColumn signalColumn =
                (FloatColumn) hybridizationData.getColumn(AffymetrixExpressionChpQuantitationType.CHP_SIGNAL);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            try {
                chpData.getExpressionResults(i, results);
            } catch (final UnsignedOutOfLimitsException e) {
                fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
            assertEquals(results.getSignal(), signalColumn.getValues()[i], 0);
        }
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    @Test
    public void testSnpChpData() throws InvalidDataFileException {
        final DerivedArrayData chpData =
                getChpData(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP);
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        checkSnpData(AffymetrixArrayDataFiles.TEN_K_1_CHP, dataSet);
    }

    private void checkSnpData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        final FusionCHPLegacyData chpData =
                FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        final FusionGenotypeProbeSetResults results = new FusionGenotypeProbeSetResults();
        final StringColumn alleleColumn =
                (StringColumn) hybridizationData.getColumn(AffymetrixSnpChpQuantitationType.CHP_ALLELE);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            try {
                chpData.getGenotypingResults(i, results);
            } catch (final UnsignedOutOfLimitsException e) {
                fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
            assertEquals(results.getAlleleCallString(), alleleColumn.getValues()[i]);
        }
        assertNotNull(hybridizationData.getHybridization().getArray());
    }

    @Test
    public void testCelData() throws InvalidDataFileException {
        final RawArrayData celData =
                getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        this.arrayDataService.importData(celData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        final DataSet dataSet = celData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        checkCelData(AffymetrixArrayDataFiles.TEST3_CEL, dataSet);
    }

    private void checkCelData(File celFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        final FusionCELData fusionCelData = new FusionCELData();
        fusionCelData.setFileName(celFile.getAbsolutePath());
        fusionCelData.read();
        final FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
        final ShortColumn xColumn = (ShortColumn) hybridizationData.getColumns().get(0);
        final ShortColumn yColumn = (ShortColumn) hybridizationData.getColumns().get(1);
        final FloatColumn intensityColumn = (FloatColumn) hybridizationData.getColumns().get(2);
        final FloatColumn stdDevColumn = (FloatColumn) hybridizationData.getColumns().get(3);
        final BooleanColumn isMaskedColumn = (BooleanColumn) hybridizationData.getColumns().get(4);
        final BooleanColumn isOutlierColumn = (BooleanColumn) hybridizationData.getColumns().get(5);
        final ShortColumn numPixelsColumn = (ShortColumn) hybridizationData.getColumns().get(6);
        assertNotNull(hybridizationData.getHybridization().getArray());
        for (int rowIndex = 0; rowIndex < fusionCelData.getCells(); rowIndex++) {
            try {
                fusionCelData.getEntry(rowIndex, fusionCelEntry);
            } catch (final UnsignedOutOfLimitsException e) {
                fail(e.toString());
            } catch (final IOException e) {
                fail(e.toString());
            }
            assertEquals(fusionCelData.indexToX(rowIndex), xColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.indexToY(rowIndex), yColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getIntensity(), intensityColumn.getValues()[rowIndex], 0);
            assertEquals(fusionCelEntry.getStdv(), stdDevColumn.getValues()[rowIndex], 0);
            assertEquals(fusionCelData.isMasked(rowIndex), isMaskedColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.isOutlier(rowIndex), isOutlierColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getPixels(), numPixelsColumn.getValues()[rowIndex]);
        }
    }

    @Test
    public void testImportExpressionChp() throws InvalidDataFileException, PlatformFileReadException {
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
    }

    private void testImportExpressionChp(File cdfFile, File chpFile) throws InvalidDataFileException,
            PlatformFileReadException {
        final DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length, hybridizationData.getColumns().size());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length, dataSet.getQuantitationTypes().size());
        for (final AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements()) {
            assertNotNull(element);
        }
        checkChpExpresionColumnTypes(dataSet);
    }

    private void checkChpExpresionColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixExpressionChpQuantitationType.values());
    }

    @Test
    public void testImportDabgChp() throws InvalidDataFileException, PlatformFileReadException {
        testImportDabgChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.DABG_CHP);
    }

    private void testImportDabgChp(File cdfFile, File chpFile) throws InvalidDataFileException,
            PlatformFileReadException {
        final DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixExpressionSignalDetectionChpQuantitationType.values().length, hybridizationData
                .getColumns().size());
        assertEquals(AffymetrixExpressionSignalDetectionChpQuantitationType.values().length, dataSet
                .getQuantitationTypes().size());
        for (final AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements()) {
            assertNotNull(element);
        }
        checkColumnTypes(dataSet, AffymetrixExpressionSignalDetectionChpQuantitationType.values());
        ;
    }

    @Test
    public void testImportSnpChp() throws InvalidDataFileException, PlatformFileReadException {
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP,
                AffymetrixSnpChpQuantitationType.values());
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CALVIN_CHP,
                AffymetrixSnpChpQuantitationType.values());
        testImportSnpChp(AffymetrixArrayDesignFiles.BIRDSEED_SNP_TEST_CDF,
                AffymetrixArrayDataFiles.BIRDSEED_SNP_TEST_CHP, AffymetrixSnpBirdseedChpQuantitationType.values());
    }

    private void testImportSnpChp(File cdfFile, File chpFile, QuantitationTypeDescriptor[] quantitationTypeDescriptors)
            throws InvalidDataFileException, PlatformFileReadException {
        final DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(quantitationTypeDescriptors.length, hybridizationData.getColumns().size());
        assertEquals(quantitationTypeDescriptors.length, dataSet.getQuantitationTypes().size());
        checkChpSnpColumnTypes(dataSet, quantitationTypeDescriptors);
    }

    private void checkChpSnpColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        checkColumnTypes(dataSet, descriptors);
    }

    @Test
    public void testImportCN4Chp() throws InvalidDataFileException {
        final DerivedArrayData chpData =
                getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.COPY_NUMBER_4_CHP);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        final Set<CopyNumberQuantitationType> expected =
                new HashSet<CopyNumberQuantitationType>(CnchpData.CN4_TYPE_MAP.values());
        assertEquals(expected.size(), hybridizationData.getColumns().size());
        assertEquals(expected.size(), dataSet.getQuantitationTypes().size());
        for (final AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements()) {
            assertNotNull(element);
        }
    }

    @Test
    public void testImportCN5Chp() throws InvalidDataFileException {
        final File f = new File("/path/to/copy-number-5.cn5.cnchp");
        if (!f.exists()) {
            System.out
                    .println("Skipping test: no public domain cn5.cnchp files available.  find one and replace the path");
            return;
        }
        final DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, f);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        this.arrayDataService.importData(chpData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        final DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(CnchpData.CN5_TYPE_MAP.size(), hybridizationData.getColumns().size());
        assertEquals(CnchpData.CN5_TYPE_MAP.size(), dataSet.getQuantitationTypes().size());
        for (final AbstractDesignElement element : dataSet.getDesignElementList().getDesignElements()) {
            assertNotNull(element);
        }
    }

    @Test
    public void testImportCel() throws InvalidDataFileException {
        final RawArrayData celData =
                getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        assertEquals(FileStatus.UPLOADED, celData.getDataFile().getFileStatus());
        assertNull(celData.getDataSet());
        this.arrayDataService.importData(celData.getDataFile(), false, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED, celData.getDataFile().getFileStatus());
        assertEquals(AffymetrixArrayDataFiles.TEST3_CEL.getName(), celData.getName());
        assertNotNull(celData.getDataSet());
        final DataSet dataSet = celData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(celData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixCelQuantitationType.values().length, hybridizationData.getColumns().size());
        assertEquals(AffymetrixCelQuantitationType.values().length, dataSet.getQuantitationTypes().size());
        checkCelColumnTypes(dataSet);
    }

    private void checkCelColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixCelQuantitationType.values());
    }

    private RawArrayData getCelData(File cdf, File cel) {
        final Hybridization hybridization = createAffyHybridization(cdf);
        final RawArrayData celData = new RawArrayData();
        celData.setType(this.daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_CEL));
        celData.setDataFile(getCelCaArrayFile(cel, getCdfObjectId(cdf)));
        celData.addHybridization(hybridization);
        this.daoFactoryStub.addData(celData);
        hybridization.addArrayData(celData);
        return celData;
    }

    private DerivedArrayData getChpData(File cdf, File file) {
        final Hybridization hybridization = createAffyHybridization(cdf);
        final DerivedArrayData chpData = new DerivedArrayData();
        chpData.setType(this.daoFactoryStub.getArrayDao().getArrayDataType(
                AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP));
        chpData.setDataFile(getChpCaArrayFile(file, getCdfObjectId(cdf)));
        chpData.getHybridizations().add(hybridization);
        hybridization.getDerivedDataCollection().add(chpData);
        this.daoFactoryStub.addData(chpData);
        return chpData;
    }

    @Test
    public void testChpValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
        fileList.add(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP);
        fileList.add(AffymetrixArrayDataFiles.HG_FOCUS_CHP);
        fileList.add(AffymetrixArrayDataFiles.TEST3_CHP);
        fileList.add(AffymetrixArrayDesignFiles.TEST3_CDF);

        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), false);
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), false);
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), false);
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), false);
        testInvalidFile(getChpCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {});
    }

    @Test
    public void testCelValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(AffymetrixArrayDataFiles.TEST3_CEL);
        fileList.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        fileList.add(AffymetrixArrayDataFiles.TEST3_INVALID_DATA_CEL);
        fileList.add(AffymetrixArrayDataFiles.TEST3_INVALID_HEADER_CEL);

        testValidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_DATA_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
        testInvalidFile(
                getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_HEADER_CEL, AFFY_TEST3_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), new String[] {PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT });
    }

    @Test
    public void testCreateAnnotationCel() throws InvalidDataFileException {
        final CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        this.arrayDataService.importData(celFile, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(celFile, 1);
    }

    @Test
    public void testCreateAnnotationChp() throws InvalidDataFileException {
        final CaArrayFile chpFile = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP, AFFY_TEST3_LSID_OBJECT_ID);
        this.arrayDataService.importData(chpFile, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(chpFile, 1);
    }

    private Hybridization createAffyHybridization(File cdf) {
        return createHybridization(cdf, CdfHandler.CDF_FILE_TYPE);
    }

    private String getCdfObjectId(File cdfFile) {
        try {
            final CdfReader reader = new CdfReader(cdfFile);
            final String objectId = reader.getCdfData().getChipType();
            reader.close();
            return objectId;
        } catch (final PlatformFileReadException e) {
            throw new IllegalStateException("Couldn't read CDF", e);
        }
    }

    private CaArrayFile getCelCaArrayFile(File cel, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(cel, CelHandler.CEL_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getChpCaArrayFile(File chp, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(chp, ChpHandler.CHP_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }
}
