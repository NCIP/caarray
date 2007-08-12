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

import static org.junit.Assert.*;

import java.io.File;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import org.junit.Before;
import org.junit.Test;

import affymetrix.fusion.cel.FusionCELData;
import affymetrix.fusion.cel.FusionCELFileEntryType;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest {
    
    private ArrayDataService arrayDataService;
    ArrayDesignService arrayDesignService = ArrayDesignServiceTest.getArrayDesignService();
    FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();

    @Before
    public void setUp() throws Exception {
        ArrayDataServiceBean arrayDataServiceBean = new ArrayDataServiceBean();
        arrayDataServiceBean.setArrayDesignService(arrayDesignService);
        arrayDataServiceBean.setFileAccessService(fileAccessServiceStub);
        arrayDataService = arrayDataServiceBean;
    }

    @Test
    public void testAffymetrixCelData() {
        RawArrayData celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CEL);
        arrayDataService.importData(celData);
        ArrayDataValues values = arrayDataService.getDataValues(celData, null, null);
        checkCelValues(values, AffymetrixArrayDataFiles.TEST3_CEL);

        celData = getCelData(AffymetrixArrayDesignFiles.TEST3_CDF, AffymetrixArrayDataFiles.TEST3_CALVIN_CEL);
        arrayDataService.importData(celData);
        values = arrayDataService.getDataValues(celData, null, null);
        checkCelValues(values, AffymetrixArrayDataFiles.TEST3_CALVIN_CEL);
    }
    
    private void checkCelValues(ArrayDataValues values, File file) {
        FusionCELData fusionCelData = new FusionCELData();
        fusionCelData.setFileName(file.getAbsolutePath());
        fusionCelData.read();
        assertEquals(AffymetrixCelQuantitationType.CEL_X, values.getQuantitationTypes().get(0));
        assertEquals(AffymetrixCelQuantitationType.CEL_Y, values.getQuantitationTypes().get(1));
        assertEquals(AffymetrixCelQuantitationType.CEL_INTENSITY, values.getQuantitationTypes().get(2));
        assertEquals(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV, values.getQuantitationTypes().get(3));
        assertEquals(AffymetrixCelQuantitationType.CEL_MASK, values.getQuantitationTypes().get(4));
        assertEquals(AffymetrixCelQuantitationType.CEL_OUTLIER, values.getQuantitationTypes().get(5));
        assertEquals(AffymetrixCelQuantitationType.CEL_PIXELS, values.getQuantitationTypes().get(6));
        ArrayDesignDetails designDetails = getArrayDesignDetails(values.getArrayData());
        int rowCount = values.getValues().length;
        assertEquals(designDetails.getFeatures().size(), rowCount);
        FusionCELFileEntryType fusionCelEntry = new FusionCELFileEntryType();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            fusionCelData.getEntry(rowIndex, fusionCelEntry);
            assertNotNull(values.getElements().get(rowIndex));
            Object[] rowValues = values.getValues()[rowIndex];
            assertEquals(7, rowValues.length);
            Feature feature = (Feature) values.getElements().get(rowIndex);
            assertEquals(feature.getColumn(), rowValues[0]);
            assertEquals(fusionCelData.indexToX(rowIndex), rowValues[0]);
            assertEquals(fusionCelData.indexToY(rowIndex), rowValues[1]);
            assertEquals(fusionCelEntry.getIntensity(), rowValues[2]);
            assertEquals(fusionCelEntry.getStdv(), rowValues[3]);
            assertEquals(fusionCelData.isMasked(rowIndex), rowValues[4]);
            assertEquals(fusionCelData.isOutlier(rowIndex), rowValues[5]);
            assertEquals(fusionCelEntry.getPixels(), rowValues[6]);
        }
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
        CaArrayFile celDataFile = fileAccessServiceStub.add(cel);
        celDataFile.setType(FileType.AFFYMETRIX_CEL);
        celData.setDataFile(celDataFile);
        celData.setHybridization(hybridization);
        hybridization.setArrayData(celData);
        return celData;
    }

}
