/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2-branch
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2-branch Software License (the License) is between NCI and You. You (or 
 * Your) shall mean a person or an entity, and all other entities that control, 
 * are controlled by, or are under common control with the entity. Control for 
 * purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares, 
 * or (iii) beneficial ownership of such entity. 
 *
 * This License is granted provided that You agree to the conditions described 
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up, 
 * no-charge, irrevocable, transferable and royalty-free right and license in 
 * its rights in the caArray2-branch Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2-branch Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2-branch Software and any 
 * modifications and derivative works thereof; and (iii) sublicense the 
 * foregoing rights set out in (i) and (ii) to third parties, including the 
 * right to license such rights to further third parties. For sake of clarity, 
 * and not by way of limitation, NCI shall have no right of accounting or right 
 * of payment from You or Your sub-licensees for the rights granted under this 
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the 
 * above copyright notice, this list of conditions and the disclaimer and 
 * limitation of liability of Article 6, below. Your redistributions in object 
 * code form must reproduce the above copyright notice, this list of conditions 
 * and the disclaimer of Article 6 in the documentation and/or other materials 
 * provided with the distribution, if any. 
 *
 * Your end-user documentation included with the redistribution, if any, must 
 * include the following acknowledgment: This product includes software 
 * developed by 5AM and the National Cancer Institute. If You do not include 
 * such end-user documentation, You shall include this acknowledgment in the 
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM" 
 * to endorse or promote products derived from this Software. This License does 
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the 
 * terms of this License. 
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this 
 * Software into Your proprietary programs and into any third party proprietary 
 * programs. However, if You incorporate the Software into third party 
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software 
 * into such third party proprietary programs and for informing Your 
 * sub-licensees, including without limitation Your end-users, of their 
 * obligation to secure any required permissions from such third parties before 
 * incorporating the Software into such third party proprietary software 
 * programs. In the event that You fail to obtain such permissions, You agree 
 * to indemnify NCI for any claims against NCI by such third parties, except to 
 * the extent prohibited by law, resulting from Your failure to obtain such 
 * permissions. 
 *
 * For sake of clarity, and not by way of limitation, You may add Your own 
 * copyright statement to Your modifications and to the derivative works, and 
 * You may provide additional or different license terms and conditions in Your 
 * sublicenses of modifications of the Software, or any derivative works of the 
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR 
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
