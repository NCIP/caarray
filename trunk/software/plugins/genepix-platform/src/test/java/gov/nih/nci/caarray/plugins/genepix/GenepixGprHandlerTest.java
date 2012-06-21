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
