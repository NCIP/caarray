/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.arraydata;

import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixCelQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixExpressionChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaExpressionQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import affymetrix.fusion.cel.FusionCELData;
import affymetrix.fusion.cel.FusionCELFileEntryType;
import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionExpressionProbeSetResults;
import affymetrix.fusion.chp.FusionGenotypeProbeSetResults;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest {

    private ArrayDataService arrayDataService;
    FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() throws Exception {
        ArrayDataServiceBean arrayDataServiceBean = new ArrayDataServiceBean();
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        ArrayDesignServiceBean arrayDesignServiceBean = new ArrayDesignServiceBean();
        arrayDesignServiceBean.setDaoFactory(daoFactoryStub);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceBean);
        arrayDataServiceBean.setDaoFactory(daoFactoryStub);
        arrayDataService = arrayDataServiceBean;
    }

    @Test
    public void testInitialize() {
        arrayDataService.initialize();
        assertTrue(daoFactoryStub.dataTypeMap.containsKey(AffymetrixArrayDataTypes.AFFYMETRIX_CEL));
        assertTrue(daoFactoryStub.quantitationTypeMap.keySet().containsAll(Arrays.asList(AffymetrixCelQuantitationType.values())));
    }

    // IMPORT

    @Test
    public void testImport() throws InvalidDataFileException {
        testImportCel();
        testImportExpressionChp();
        testImportSnpChp();
        testImportGenepix();
        testCreateAnnotation();
    }

    private void testCreateAnnotation() throws InvalidDataFileException {
        testCreateAnnotationCel();
        testCreateAnnotationChp();
        testCreateAnnotationIllumina();
        testExistingAnnotationNotOverwritten();
    }

    private void testExistingAnnotationNotOverwritten() throws InvalidDataFileException {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        RawArrayData celData = new RawArrayData();
        Hybridization hybridization = new Hybridization();
        celData.setHybridization(hybridization);
        hybridization.setArrayData(celData);
        celData.setDataFile(celFile);
        daoFactoryStub.getArrayDao().save(celData);
        arrayDataService.importData(celFile, true);
        assertEquals(celData, daoFactoryStub.getArrayDao().getRawArrayData(celFile));
        assertEquals(hybridization, celData.getHybridization());
    }

    private void testCreateAnnotationIllumina() throws InvalidDataFileException {
        CaArrayFile illuminaFile = getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6);
        arrayDataService.importData(illuminaFile, true);
        checkAnnotation(illuminaFile, 19);
    }

    private void testCreateAnnotationCel() throws InvalidDataFileException {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        arrayDataService.importData(celFile, true);
        checkAnnotation(celFile, 1);
    }

    private void testCreateAnnotationChp() throws InvalidDataFileException {
        CaArrayFile chpFile = getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP);
        arrayDataService.importData(chpFile, true);
        checkAnnotation(chpFile, 1);
    }

    private void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    private void testImportGenepix() throws InvalidDataFileException {
        QuantitationTypeDescriptor[] expectedTypes = new QuantitationTypeDescriptor[] {
                X, Y, DIA,
                F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN, B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT,
                F532_MEDIAN, F532_MEAN, F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD, F532_PERCENT_SAT,
                RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532, MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532,
                RGN_R2_635_532, F_PIXELS, B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532,
                F635_MEDIAN_B635, F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, FLAGS
        };
        testImportGenepixFile(GenepixArrayDataFiles.GPR_3_0_6, expectedTypes, 2);
    }

    private void testImportGenepixFile(File gprFile, QuantitationTypeDescriptor[] expectedTypes, int expectedNumberOfSamples) throws InvalidDataFileException {
        CaArrayFile gprCaArrayFile = getGprCaArrayFile(gprFile);
        arrayDataService.importData(gprCaArrayFile, true);
        DerivedArrayData data = daoFactoryStub.getArrayDao().getDerivedArrayData(gprCaArrayFile);
        assertNotNull(data);
        checkAnnotation(gprCaArrayFile, expectedNumberOfSamples);
        checkColumnTypes(data.getDataSet(), expectedTypes);
    }

    private void testImportExpressionChp() throws InvalidDataFileException {
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
    }

    private void testImportExpressionChp(File cdfFile, File chpFile) throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        arrayDataService.importData(chpData.getDataFile(), false);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length,
                hybridizationData.getColumns().size());
        assertEquals(AffymetrixExpressionChpQuantitationType.values().length,
                dataSet.getQuantitationTypes().size());
        checkChpExpresionColumnTypes(dataSet);
    }

    private void checkChpExpresionColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixExpressionChpQuantitationType.values());
    }

    private void checkColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            checkType(descriptors[i], dataSet.getQuantitationTypes().get(i));
            checkType(descriptors[i], dataSet.getHybridizationDataList().get(0).getColumns().get(i).getQuantitationType());
        }
    }

    private void checkType(QuantitationTypeDescriptor typeDescriptor, QuantitationType type) {
        assertEquals(typeDescriptor.getName(), type.getName());
    }

    private void testImportSnpChp() throws InvalidDataFileException {
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP);
        testImportSnpChp(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CALVIN_CHP);
    }

    private void testImportSnpChp(File cdfFile, File chpFile) throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        arrayDataService.importData(chpData.getDataFile(), false);
        assertEquals(FileStatus.IMPORTED, chpData.getDataFile().getFileStatus());
        assertNotNull(chpData.getDataSet());
        DataSet dataSet = chpData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(chpData.getHybridizations().iterator().next(), hybridizationData.getHybridization());
        assertEquals(AffymetrixSnpChpQuantitationType.values().length,
                hybridizationData.getColumns().size());
        assertEquals(AffymetrixSnpChpQuantitationType.values().length,
                dataSet.getQuantitationTypes().size());
        checkChpSnpColumnTypes(dataSet);
    }

    private void checkChpSnpColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixSnpChpQuantitationType.values());
    }

    private void testImportCel() throws InvalidDataFileException {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        assertEquals(FileStatus.UPLOADED, celData.getDataFile().getFileStatus());
        assertNull(celData.getDataSet());
        arrayDataService.importData(celData.getDataFile(), false);
        assertEquals(FileStatus.IMPORTED, celData.getDataFile().getFileStatus());
        assertEquals(AffymetrixArrayDataFiles.TEST3_CEL.getName(), celData.getName());
        assertNotNull(celData.getDataSet());
        DataSet dataSet = celData.getDataSet();
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(celData.getHybridization(), hybridizationData.getHybridization());
        assertEquals(AffymetrixCelQuantitationType.values().length,
                hybridizationData.getColumns().size());
        assertEquals(AffymetrixCelQuantitationType.values().length,
                dataSet.getQuantitationTypes().size());
        checkCelColumnTypes(dataSet);
    }

    // VALIDATION

    @Test
    public void testValidate() {
        testCelValidation();
        testChpValidation();
        testIlluminaValidation();
        testGenepixValidation();
    }

    private void testGenepixValidation() {
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_3_0_6));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_0_1));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_4_1_1));
        testValidFile(getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1));
        testInvalidFile(getGprCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL));
    }


    private void testIlluminaValidation() {
        testValidFile(getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6));
    }

    private void testValidFile(CaArrayFile caArrayFile) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        arrayDataService.validate(caArrayFile);
        if (FileStatus.VALIDATION_ERRORS.equals(caArrayFile.getFileStatus())) {
            System.out.println(caArrayFile.getValidationResult());
        }
        System.out.println(caArrayFile.getValidationResult());
        assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
    }

    private void testInvalidFile(CaArrayFile caArrayFile) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        arrayDataService.validate(caArrayFile);
        assertEquals(FileStatus.VALIDATION_ERRORS, caArrayFile.getFileStatus());
    }

    private void testChpValidation() {
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP));
        testValidFile(getChpCaArrayFile(AffymetrixArrayDataFiles.TEST3_CHP));
        testInvalidFile(getChpCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
    }

    private void testCelValidation() {
        testValidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_DATA_CEL));
        testInvalidFile(getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_INVALID_HEADER_CEL));
    }

    @Test
    public void testGetData() throws InvalidDataFileException {
        testCelData();
        testExpressionChpData();
        testSnpChpData();
        testIlluminaData();
        testGenepixData();
        // The following test is commented out due to the change to parse on import.
        // It may be re-incorporated when parse on demand is re-instituted.
        // testCelDataForSelectedQuantitationTypes();
    }

    private void testGenepixData() throws InvalidDataFileException {
        CaArrayFile gprFile = getGprCaArrayFile(GenepixArrayDataFiles.GPR_5_0_1);
        arrayDataService.importData(gprFile, true);
        DerivedArrayData gprData = daoFactoryStub.getArrayDao().getDerivedArrayData(gprFile);
        DataSet dataSet = arrayDataService.getData(gprData);
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(51, hybridizationData.getColumns().size());
        IntegerColumn f635MedianColumn = (IntegerColumn) hybridizationData.getColumn(GenepixQuantitationType.F635_MEDIAN);
        assertNotNull(f635MedianColumn);
        assertEquals(6528, f635MedianColumn.getValues().length);
        assertEquals(138, f635MedianColumn.getValues()[0]);
        assertEquals(6, f635MedianColumn.getValues()[6527]);
    }

    private void testIlluminaData() throws InvalidDataFileException {
        CaArrayFile illuminaFile = getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6_SMALL);
        arrayDataService.importData(illuminaFile, true);
        DerivedArrayData illuminaData = daoFactoryStub.getArrayDao().getDerivedArrayData(illuminaFile);
        DataSet dataSet = arrayDataService.getData(illuminaData);
        assertEquals(19, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(4, hybridizationData.getColumns().size());
        FloatColumn signalColumn = (FloatColumn) hybridizationData.getColumn(IlluminaExpressionQuantitationType.AVG_SIGNAL);
        assertNotNull(signalColumn);
        assertEquals(10, signalColumn.getValues().length);
        assertEquals(5.8, signalColumn.getValues()[0]);
        assertEquals(3.6, signalColumn.getValues()[9]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDataIllegalArguments() {
        arrayDataService.getData(null);
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        celData.setDataFile(null);
        arrayDataService.getData(celData);

    }

    private void testExpressionChpData() throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        arrayDataService.importData(chpData.getDataFile(), false);
        DataSet dataSet = arrayDataService.getData(chpData);
        checkExpressionData(AffymetrixArrayDataFiles.TEST3_CHP, dataSet);
    }

    private void checkExpressionData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCHPLegacyData chpData = FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        FusionExpressionProbeSetResults results = new FusionExpressionProbeSetResults();
        FloatColumn signalColumn = (FloatColumn) hybridizationData.getColumn(AffymetrixExpressionChpQuantitationType.CHP_SIGNAL);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            chpData.getExpressionResults(i, results);
            assertEquals(results.getSignal(), signalColumn.getValues()[i]);
        }
    }

    private void testSnpChpData() throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEN_K_CDF, AffymetrixArrayDataFiles.TEN_K_1_CHP);
        arrayDataService.importData(chpData.getDataFile(), false);
        DataSet dataSet = arrayDataService.getData(chpData);
        checkSnpData(AffymetrixArrayDataFiles.TEN_K_1_CHP, dataSet);
    }

    private void checkSnpData(File chpFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCHPLegacyData chpData = FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        FusionGenotypeProbeSetResults results = new FusionGenotypeProbeSetResults();
        StringColumn alleleColumn = (StringColumn) hybridizationData.getColumn(AffymetrixSnpChpQuantitationType.CHP_ALLELE);
        for (int i = 0; i < chpData.getHeader().getNumProbeSets(); i++) {
            chpData.getGenotypingResults(i, results);
            assertEquals(results.getAlleleCallString(), alleleColumn.getValues()[i]);
        }
    }

    private void testCelData() throws InvalidDataFileException {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        arrayDataService.importData(celData.getDataFile(), false);
        DataSet dataSet = arrayDataService.getData(celData);
        checkCelData(AffymetrixArrayDataFiles.TEST3_CEL, dataSet);
    }

    private void testCelDataForSelectedQuantitationTypes() throws InvalidDataFileException {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        arrayDataService.importData(celData.getDataFile(), false);
        List<QuantitationType> types = new ArrayList<QuantitationType>();
        types.add(daoFactoryStub.getArrayDao().getQuantitationType(AffymetrixCelQuantitationType.CEL_INTENSITY));
        DataSet dataSet = arrayDataService.getData(celData, types);
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        ShortColumn xColumn = (ShortColumn) hybridizationData.getColumns().get(0);
        ShortColumn yColumn = (ShortColumn) hybridizationData.getColumns().get(1);
        FloatColumn intensityColumn = (FloatColumn) hybridizationData.getColumns().get(2);
        FloatColumn stdDevColumn = (FloatColumn) hybridizationData.getColumns().get(3);
        BooleanColumn isMaskedColumn = (BooleanColumn) hybridizationData.getColumns().get(4);
        BooleanColumn isOutlierColumn = (BooleanColumn) hybridizationData.getColumns().get(5);
        ShortColumn numPixelsColumn = (ShortColumn) hybridizationData.getColumns().get(6);
        assertNull(xColumn.getValues());
        assertNull(yColumn.getValues());
        assertNull(stdDevColumn.getValues());
        assertNull(isMaskedColumn.getValues());
        assertNull(isOutlierColumn.getValues());
        assertNull(numPixelsColumn.getValues());
        FusionCELData fusionCelData = new FusionCELData();
        fusionCelData.setFileName(AffymetrixArrayDataFiles.TEST3_CEL.getAbsolutePath());
        fusionCelData.read();
        FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
        for (int rowIndex = 0; rowIndex < fusionCelData.getCells(); rowIndex++) {
            fusionCelData.getEntry(rowIndex, fusionCelEntry);
            assertEquals(fusionCelEntry.getIntensity(), intensityColumn.getValues()[rowIndex]);
        }
    }

    private void checkCelData(File celFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDataList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        FusionCELData fusionCelData = new FusionCELData();
        fusionCelData.setFileName(celFile.getAbsolutePath());
        fusionCelData.read();
        FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
        ShortColumn xColumn = (ShortColumn) hybridizationData.getColumns().get(0);
        ShortColumn yColumn = (ShortColumn) hybridizationData.getColumns().get(1);
        FloatColumn intensityColumn = (FloatColumn) hybridizationData.getColumns().get(2);
        FloatColumn stdDevColumn = (FloatColumn) hybridizationData.getColumns().get(3);
        BooleanColumn isMaskedColumn = (BooleanColumn) hybridizationData.getColumns().get(4);
        BooleanColumn isOutlierColumn = (BooleanColumn) hybridizationData.getColumns().get(5);
        ShortColumn numPixelsColumn = (ShortColumn) hybridizationData.getColumns().get(6);
        for (int rowIndex = 0; rowIndex < fusionCelData.getCells(); rowIndex++) {
            fusionCelData.getEntry(rowIndex, fusionCelEntry);
            assertEquals(fusionCelData.indexToX(rowIndex), xColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.indexToY(rowIndex), yColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getIntensity(), intensityColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getStdv(), stdDevColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.isMasked(rowIndex), isMaskedColumn.getValues()[rowIndex]);
            assertEquals(fusionCelData.isOutlier(rowIndex), isOutlierColumn.getValues()[rowIndex]);
            assertEquals(fusionCelEntry.getPixels(), numPixelsColumn.getValues()[rowIndex]);
        }
    }

    private void checkCelColumnTypes(DataSet dataSet) {
        checkColumnTypes(dataSet, AffymetrixCelQuantitationType.values());
    }

    private RawArrayData getCelData(File cdf, File cel) {
        Hybridization hybridization = createAffyHybridization(cdf);
        RawArrayData celData = new RawArrayData();
        celData.setType(daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_CEL));
        celData.setDataFile(getCelCaArrayFile(cel));
        celData.setHybridization(hybridization);
        daoFactoryStub.addData(celData);
        hybridization.setArrayData(celData);
        return celData;
    }

    private Hybridization createAffyHybridization(File cdf) {
        return createHybridization(cdf, FileType.AFFYMETRIX_CDF);
    }

    private Hybridization createHybridization(File design, FileType type) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = fileAccessServiceStub.add(design);
        designFile.setFileType(type);
        arrayDesign.setDesignFile(designFile);
        Array array = new Array();
        array.setDesign(arrayDesign);
        Hybridization hybridization = new Hybridization();
        hybridization.setArray(array);
        return hybridization;
    }

    private DerivedArrayData getChpData(File cdf, File file) {
        Hybridization hybridization = createAffyHybridization(cdf);
        DerivedArrayData chpData = new DerivedArrayData();
        chpData.setType(daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP));
        chpData.setDataFile(getChpCaArrayFile(file));
        chpData.getHybridizations().add(hybridization);
        hybridization.getDerivedDataCollection().add(chpData);
        daoFactoryStub.addData(chpData);
        return chpData;
    }

    private CaArrayFile getGprCaArrayFile(File gpr) {
        return getDataCaArrayFile(gpr, FileType.GENEPIX_GPR);
    }

    private CaArrayFile getCelCaArrayFile(File cel) {
        return getDataCaArrayFile(cel, FileType.AFFYMETRIX_CEL);
    }

    private CaArrayFile getChpCaArrayFile(File chp) {
        return getDataCaArrayFile(chp, FileType.AFFYMETRIX_CHP);
    }

    private CaArrayFile getIlluminaCaArrayFile(File file) {
        return getDataCaArrayFile(file, FileType.ILLUMINA_DATA_CSV);
    }

    private CaArrayFile getDataCaArrayFile(File file, FileType type) {
        CaArrayFile caArrayFile = fileAccessServiceStub.add(file);
        caArrayFile.setFileType(type);
        caArrayFile.setProject(new Project());
        caArrayFile.getProject().setExperiment(new Experiment());
        return caArrayFile;
    }

    private static final class LocalDaoFactoryStub extends DaoFactoryStub {

        private final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
            new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
            new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        private final Map<CaArrayFile, AbstractArrayData> fileDataMap = new HashMap<CaArrayFile, AbstractArrayData>();

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    if ("Test3".equals(lsidObjectId)) {
                        return createArrayDesign(126, 126);
                    } else if ("HG-Focus".equals(lsidObjectId)) {
                        return createArrayDesign(448, 448);
                    } else {
                        return null;
                    }
                }

                @SuppressWarnings("deprecation")
                private ArrayDesign createArrayDesign(int rows, int columns) {
                    ArrayDesign arrayDesign = new ArrayDesign();
                    ArrayDesignDetails details = new ArrayDesignDetails();
                    for (short row = 0; row < rows; row++) {
                        for (short column = 0; column < columns; column++) {
                            Feature feature = new Feature();
                            feature.setRow(row);
                            feature.setColumn(column);
                            details.getFeatures().add(feature);
                        }
                    }
                    arrayDesign.setDesignDetails(details);
                    return arrayDesign;
                }

                @Override
                public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
                    if (dataTypeMap.containsKey(descriptor)) {
                        return dataTypeMap.get(descriptor);
                    }
                    ArrayDataType arrayDataType = new ArrayDataType();
                    arrayDataType.setName(descriptor.getName());
                    arrayDataType.setVersion(descriptor.getVersion());
                    addQuantitationTypes(arrayDataType, descriptor);
                    dataTypeMap.put(descriptor, arrayDataType);
                    return arrayDataType;
                }

                private void addQuantitationTypes(ArrayDataType arrayDataType, ArrayDataTypeDescriptor descriptor) {
                    for (QuantitationTypeDescriptor quantitationTypeDescriptor : descriptor.getQuantitationTypes()) {
                        arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
                    }
                }

                @Override
                public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
                    if (quantitationTypeMap.containsKey(descriptor)) {
                        return quantitationTypeMap.get(descriptor);
                    }
                    QuantitationType quantitationType = new QuantitationType();
                    quantitationType.setName(descriptor.getName());
                    quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
                    quantitationTypeMap.put(descriptor, quantitationType);
                    return quantitationType;
                }

                @Override
                public RawArrayData getRawArrayData(CaArrayFile file) {
                    return (RawArrayData) fileDataMap.get(file);
                }

                @Override
                public DerivedArrayData getDerivedArrayData(CaArrayFile file) {
                    return (DerivedArrayData) fileDataMap.get(file);
                }

                @Override
                public void save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof AbstractArrayData) {
                        addData((AbstractArrayData) caArrayEntity);
                    }
                }

            };
        }

        void addData(AbstractArrayData arrayData) {
            fileDataMap.put(arrayData.getDataFile(), arrayData);
        }
    }

}
