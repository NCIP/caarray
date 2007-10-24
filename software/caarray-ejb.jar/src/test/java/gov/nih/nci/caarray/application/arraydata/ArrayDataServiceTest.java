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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixCelQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixExpressionChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpChpQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
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
    ArrayDesignService arrayDesignService =
        ArrayDesignServiceTest.createArrayDesignService(daoFactoryStub, fileAccessServiceStub, new VocabularyServiceStub());

    @Before
    public void setUp() throws Exception {
        ArrayDataServiceBean arrayDataServiceBean = new ArrayDataServiceBean();
        arrayDataServiceBean.setArrayDesignService(arrayDesignService);
        ServiceLocatorStub locatorStub = new ServiceLocatorStub();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        arrayDataServiceBean.setServiceLocator(locatorStub);
        arrayDataServiceBean.setDaoFactory(daoFactoryStub);
        arrayDataService = arrayDataServiceBean;
    }

    @Test
    public void testInitialize() {
        arrayDataService.initialize();
        assertTrue(daoFactoryStub.dataTypeMap.containsKey(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CEL));
        assertTrue(daoFactoryStub.quantitationTypeMap.keySet().containsAll(Arrays.asList(AffymetrixCelQuantitationType.values())));
    }

    @Test
    public void testImport() throws InvalidDataFileException {
        testImportCel();
        testImportExpressionChp();
        testImportSnpChp();
    }

    private void testImportExpressionChp() throws InvalidDataFileException {
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        testImportExpressionChp(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
    }

    private void testImportExpressionChp(File cdfFile, File chpFile) throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(cdfFile, chpFile);
        assertEquals(FileStatus.UPLOADED, chpData.getDataFile().getFileStatus());
        assertNull(chpData.getDataSet());
        arrayDataService.importData(chpData);
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
        arrayDataService.importData(chpData);
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
        arrayDataService.importData(celData);
        assertEquals(FileStatus.IMPORTED, celData.getDataFile().getFileStatus());
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

    @Test
    public void testValidate() {
        testCelValidation();
        testChpValidation();
    }

    private void testChpValidation() {
        testValidChp(AffymetrixArrayDataFiles.TEST3_CALVIN_CHP);
        testValidChp(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP);
        testValidChp(AffymetrixArrayDataFiles.HG_FOCUS_CHP);
        testValidChp(AffymetrixArrayDataFiles.TEST3_CHP);
        CaArrayFile badChpFile = getChpCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        assertEquals(FileStatus.UPLOADED, badChpFile.getFileStatus());
        arrayDataService.validate(badChpFile);
        assertEquals(FileStatus.VALIDATION_ERRORS, badChpFile.getFileStatus());
    }

    private void testValidChp(File file) {
        CaArrayFile chpFile = getChpCaArrayFile(file);
        assertEquals(FileStatus.UPLOADED, chpFile.getFileStatus());
        arrayDataService.validate(chpFile);
        assertEquals(FileStatus.VALIDATED, chpFile.getFileStatus());
    }

    private void testCelValidation() {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        assertEquals(FileStatus.UPLOADED, celFile.getFileStatus());
        arrayDataService.validate(celFile);
        assertEquals(FileStatus.VALIDATED, celFile.getFileStatus());
        CaArrayFile badCelFile = getCelCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        assertEquals(FileStatus.UPLOADED, badCelFile.getFileStatus());
        arrayDataService.validate(badCelFile);
        assertEquals(FileStatus.VALIDATION_ERRORS, badCelFile.getFileStatus());
    }

    @Test
    public void testGetData() throws InvalidDataFileException {
        testCelData();
        testExpressionChpData();
        testSnpChpData();
    }

    private void testExpressionChpData() throws InvalidDataFileException {
        DerivedArrayData chpData = getChpData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CHP);
        arrayDataService.importData(chpData);
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
        arrayDataService.importData(chpData);
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
        arrayDataService.importData(celData);
        DataSet dataSet = arrayDataService.getData(celData);
        checkCelData(AffymetrixArrayDataFiles.TEST3_CEL, dataSet);
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
        celData.setType(daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CEL));
        celData.setDataFile(getCelCaArrayFile(cel));
        celData.setHybridization(hybridization);
        hybridization.setArrayData(celData);
        return celData;
    }

    private Hybridization createAffyHybridization(File cdf) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = fileAccessServiceStub.add(cdf);
        designFile.setType(FileType.AFFYMETRIX_CDF);
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
        return chpData;
    }

    private CaArrayFile getCelCaArrayFile(File cel) {
        CaArrayFile celDataFile = fileAccessServiceStub.add(cel);
        celDataFile.setType(FileType.AFFYMETRIX_CEL);
        return celDataFile;
    }

    private CaArrayFile getChpCaArrayFile(File chp) {
        CaArrayFile celDataFile = fileAccessServiceStub.add(chp);
        celDataFile.setType(FileType.AFFYMETRIX_CHP);
        return celDataFile;
    }

    private static final class LocalDaoFactoryStub extends DaoFactoryStub {

        private Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
            new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
            new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

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

            };
        }
    }

}
