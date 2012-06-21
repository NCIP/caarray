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
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dkokotov
 */
public class IlluminaDataHandlerTest extends AbstractHandlerTest {
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();
    private static final String ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID = "Human_WG-6";

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new IlluminaModule());
    }

    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        if (ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 126, 126, IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        } else {
            throw new IllegalArgumentException("Unsupported request design");
        }
    }

    @Test
    public void testIlluminaValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(IlluminaArrayDataFiles.HUMAN_WG6);
        testValidFile(getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
    }

    @Test
    public void testIlluminaDataSmall() throws InvalidDataFileException {
        final CaArrayFile illuminaFile =
                getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6_SMALL, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData illuminaData =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(illuminaFile.getId());
        final DataSet dataSet = illuminaData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = getHybridizationNameToDataMap(dataSet).get("A1");
        assertEquals(4, hybridizationData.getColumns().size());
        final FloatColumn signalColumn =
                (FloatColumn) hybridizationData.getColumn(IlluminaExpressionQuantitationType.AVG_SIGNAL);
        assertNotNull(signalColumn);
        assertEquals(10, signalColumn.getValues().length);
        assertEquals(5.8, signalColumn.getValues()[0], 0.00001);
        assertEquals(3.6, signalColumn.getValues()[9], 0.00001);
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(10, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
        checkAnnotation(illuminaFile, 19);
    }

    @Test
    @Ignore(value = "Not a unit test - low value over small data for build time penality")
    public void testIlluminaDataFull() throws InvalidDataFileException {
        final CaArrayFile illuminaFile =
                getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData illuminaData =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(illuminaFile.getId());
        assertEquals(19, illuminaData.getHybridizations().size());
        final DataSet dataSet = illuminaData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(4, hybridizationData.getColumns().size());
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(47293, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
        checkAnnotation(illuminaFile, 19);
    }

    private CaArrayFile getIlluminaCaArrayFile(File file, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(file, CsvDataHandler.DATA_CSV_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private Map<String, HybridizationData> getHybridizationNameToDataMap(DataSet dataset) {
        Map<String, HybridizationData> map = new HashMap<String, HybridizationData>();
        for (HybridizationData hybData : dataset.getHybridizationDataList()) {
            map.put(hybData.getHybridization().getName(), hybData);
        }
        return map;
    }
}
