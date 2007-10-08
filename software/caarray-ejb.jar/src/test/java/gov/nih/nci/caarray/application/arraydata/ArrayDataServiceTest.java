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
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest {

    private ArrayDataService arrayDataService;
    FileAccessServiceStub fileAccessServiceStub = new LocalFileAccessServiceStub();
    LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    ArrayDesignService arrayDesignService =
        ArrayDesignServiceTest.createArrayDesignService(daoFactoryStub, fileAccessServiceStub, new VocabularyServiceStub());

    @Before
    public void setUp() throws Exception {
        ArrayDataServiceBean arrayDataServiceBean = new ArrayDataServiceBean();
        arrayDataServiceBean.setArrayDesignService(arrayDesignService);
        arrayDataServiceBean.setFileAccessService(fileAccessServiceStub);
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
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        assertEquals(FileStatus.UPLOADED, celData.getDataFile().getFileStatus());
        assertNull(celData.getDataSet());
        arrayDataService.importData(celData);
        assertEquals(FileStatus.IMPORTED, celData.getDataFile().getFileStatus());
        assertNotNull(celData.getDataSet());
        DataSet dataSet = celData.getDataSet();
        assertNotNull(dataSet.getHybridizationDatas());
        assertEquals(1, dataSet.getHybridizationDatas().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDatas().get(0);
        assertEquals(celData.getHybridization(), hybridizationData.getHybridization());
        assertEquals(7, hybridizationData.getColumns().size());
        assertEquals(7, dataSet.getQuantitationTypes().size());
        checkCelColumnTypes(dataSet);
    }
    
    @Test
    public void testValidate() {
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
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        arrayDataService.importData(celData);
        DataSet dataSet = arrayDataService.getData(celData);
        checkCelData(AffymetrixArrayDataFiles.TEST3_CEL, dataSet);
    }

    private void checkCelData(File celFile, DataSet dataSet) {
        assertNotNull(dataSet.getHybridizationDatas());
        assertEquals(1, dataSet.getHybridizationDatas().size());
        HybridizationData hybridizationData = dataSet.getHybridizationDatas().get(0);
        assertEquals(7, hybridizationData.getColumns().size());
        assertEquals(7, dataSet.getQuantitationTypes().size());
        checkCelColumnTypes(dataSet);
//        FusionCELData fusionCelData = new FusionCELData();
//        fusionCelData.setFileName(celFile.getAbsolutePath());
//        fusionCelData.read();
//        FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
//        for (int rowIndex = 0; rowIndex < fusionCelData.getCells(); rowIndex++) {
//            fusionCelData.getEntry(rowIndex, fusionCelEntry);
//            FloatColumn intensityColumn = (FloatColumn) hybridizationData.getColumns().get(2);
//            assertEquals(fusionCelData.indexToX(rowIndex), dataValues.getValues().get(0).getValue());
//            assertEquals(fusionCelData.indexToY(rowIndex), dataValues.getValues().get(1).getValue());
//            assertEquals(fusionCelEntry.getIntensity(), intensityColumn.getValues()[rowIndex]);
//            assertEquals(fusionCelEntry.getStdv(), dataValues.getValues().get(3).getValue());
//            assertEquals(fusionCelData.isMasked(rowIndex), dataValues.getValues().get(4).getValue());
//            assertEquals(fusionCelData.isOutlier(rowIndex), dataValues.getValues().get(5).getValue());
//            assertEquals(fusionCelEntry.getPixels(), dataValues.getValues().get(6).getValue());
//        }
    }
    
    private void checkCelColumnTypes(DataSet dataSet) {
        assertTrue(AffymetrixCelQuantitationType.CEL_X.isEquivalent(dataSet.getQuantitationTypes().get(0)));
        assertTrue(AffymetrixCelQuantitationType.CEL_Y.isEquivalent(dataSet.getQuantitationTypes().get(1)));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(dataSet.getQuantitationTypes().get(2)));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV.isEquivalent(dataSet.getQuantitationTypes().get(3)));
        assertTrue(AffymetrixCelQuantitationType.CEL_MASK.isEquivalent(dataSet.getQuantitationTypes().get(4)));
        assertTrue(AffymetrixCelQuantitationType.CEL_OUTLIER.isEquivalent(dataSet.getQuantitationTypes().get(5)));
        assertTrue(AffymetrixCelQuantitationType.CEL_PIXELS.isEquivalent(dataSet.getQuantitationTypes().get(6)));

        assertTrue(AffymetrixCelQuantitationType.CEL_X.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(0).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_Y.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(1).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(2).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(3).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_MASK.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(4).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_OUTLIER.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(5).getQuantitationType()));
        assertTrue(AffymetrixCelQuantitationType.CEL_PIXELS.isEquivalent(dataSet.getHybridizationDatas().get(0).getColumns().get(6).getQuantitationType()));
}

    private ArrayDesignDetails getArrayDesignDetails(AbstractArrayData arrayData) {
        ArrayDesign design = ((RawArrayData) arrayData).getHybridization().getArray().getDesign();
        return arrayDesignService.getDesignDetails(design);
    }

    private RawArrayData getCelData(File cdf, File cel) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = fileAccessServiceStub.add(cdf);
        designFile.setType(FileType.AFFYMETRIX_CDF);
        arrayDesign.setDesignFile(designFile);
        Array array = new Array();
        array.setDesign(arrayDesign);
        Hybridization hybridization = new Hybridization();
        hybridization.setArray(array);
        RawArrayData celData = new RawArrayData();
        celData.setDataFile(getCelCaArrayFile(cel));
        celData.setHybridization(hybridization);
        celData.setType(daoFactoryStub.getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CEL));
        hybridization.setArrayData(celData);
        return celData;
    }
    
    private CaArrayFile getCelCaArrayFile(File cel) {
        CaArrayFile celDataFile = fileAccessServiceStub.add(cel);
        celDataFile.setType(FileType.AFFYMETRIX_CEL);
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

    private static class LocalFileAccessServiceStub extends FileAccessServiceStub {
        @Override
        public File getFile(CaArrayFile caArrayFile) {
            if (caArrayFile.getName().equals(AffymetrixArrayDesignFiles.TEST3_CDF.getName())) {
                return AffymetrixArrayDesignFiles.TEST3_CDF;
            } else if (caArrayFile.getName().equals(AffymetrixArrayDataFiles.TEST3_CEL.getName())) {
                return AffymetrixArrayDataFiles.TEST3_CEL;
            } else if (caArrayFile.getName().equals(AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName())) {
                return AffymetrixArrayDataFiles.TEST3_CALVIN_CEL;
            } else {
                throw new IllegalArgumentException("Don't know location of " + caArrayFile.getName());
}
        }
    }
}
