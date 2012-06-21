/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This test Software License (the License) is between NCI and You. You (or 
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
 * its rights in the test Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and 
 * have distributed to and by third parties the test Software and any 
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
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipFile;

import org.junit.Test;

/**
 * Test case .
 * 
 * Import GSK data
 * 
 */
public class GSKImport extends AbstractSeleniumTest {

    private static String fileExtention = ".zip";

    @Test
    public void testGskImport() throws Exception {
        int numberOfFiles = 0;
        File MAPPING250K_NSP = new File(
                "L:\\NCICB\\caArray\\caArray Files\\Affymetrix_CDFs\\500kSNP\\Mapping250K_Nsp.cdf");
        File MAPPING250K_STY = new File(
                "L:\\NCICB\\caArray\\caArray Files\\Affymetrix_CDFs\\500kSNP\\Mapping250K_Sty.cdf");
        File gskDataDirectory = null;
        String[] directory = { "L:\\NCICB\\caArray\\GSK_data\\groupedZips\\dna",
                "L:\\NCICB\\caArray\\GSK_data\\groupedZips\\rna\\cel" };
        final String MAPPING_250K_NSP_NAME = MAPPING250K_NSP.getName().substring(0,
                MAPPING250K_NSP.getName().indexOf('.'));
        final String MAPPING_250K_STY_NAME = MAPPING250K_STY.getName().substring(0,
                MAPPING250K_STY.getName().indexOf('.'));
        final String HT_HG_U133A_CDF_NAME = AffymetrixArrayDesignFiles.HT_HG_U133A_CDF.getName().substring(0,
                AffymetrixArrayDesignFiles.HT_HG_U133A_CDF.getName().indexOf('.'));
        String HG_U133_PLUS_2_CDF_NAME = AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF.getName().substring(0,
                AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF.getName().indexOf('.'));
        ArrayList<String> arrayDesigns = new ArrayList<String>();

        arrayDesigns.add(MAPPING_250K_NSP_NAME);
        arrayDesigns.add(MAPPING_250K_STY_NAME);
        arrayDesigns.add(HT_HG_U133A_CDF_NAME);
        arrayDesigns.add(HG_U133_PLUS_2_CDF_NAME);

        long startTime = System.currentTimeMillis();
        long endTime = 0;
        String title = "GSK Import" + System.currentTimeMillis();
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        loginAsPrincipalInvestigator();

        // - import array designs
        importArrayDesign(MAPPING250K_NSP, MAPPING_250K_NSP_NAME);
        importArrayDesign(MAPPING250K_STY, MAPPING_250K_STY_NAME);
        importArrayDesign(AffymetrixArrayDesignFiles.HT_HG_U133A_CDF, HT_HG_U133A_CDF_NAME);
        importArrayDesign(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF, HG_U133_PLUS_2_CDF_NAME);
        // - Create Experiment
        createExperiment(title, arrayDesigns);

        // - go to the data tab
        this.selenium.click("link=Data");
        waitForTab();
        for (int i = 0; i < directory.length; i++) {
            FileFilter celFilter = new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(fileExtention);
                }
            };
            gskDataDirectory = new File(directory[i]);
            for (File aZipFile : gskDataDirectory.listFiles(celFilter)) {
                ZipFile zipfile = new ZipFile(aZipFile.getAbsolutePath());
                numberOfFiles += zipfile.size();
                upload(aZipFile);
            }
        }

        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, numberOfFiles);

        // - Import files
        importData(AUTOCREATE_ANNOTATION_SET);

        // - click on the Imported data tab
        selenium.click("link=Import Data");

        checkFileStatus("Imported", SECOND_COLUMN, numberOfFiles);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }
}
