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
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * Test case #7959.
 *
 * Requirements: Loaded test data set includes test user and referenced Affymetrix array design.
 */
public class ImportStandardMageTabSetTest extends AbstractSeleniumTest {

    private static final int NUMBER_OF_FILES = 30;
    private static final int TWENTY_MINUTES = 120;

    @Test
    public void testImportAndRetrieval() throws Exception {
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        String title = "Standard mage" + System.currentTimeMillis();
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        loginAsPrincipalInvestigator();
        importArrayDesign("HT_HG-U133A", AffymetrixArrayDesignFiles.HT_HG_U133A_CDF);
        // - Create Experiment
        String experimentId = createExperiment(title, "HT_HG-U133A");

        // - go to the data tab
        this.selenium.click("link=Data");
        waitForTab();

        // - start the upload
        this.selenium.click("link=Upload New File(s)");

        // Upload the following files:
        // - MAGE-TAB IDF
        // - MAGE-TAB SDRF (with references to included native CEL files and corresponding Affymetrix array design)
        // - MAGE-TAB Derived Data Matrix
        // - CEL files referenced in SDRF
        upload(MageTabDataFiles.TCGA_BROAD_IDF);
        upload(MageTabDataFiles.TCGA_BROAD_SDRF);
        upload(MageTabDataFiles.TCGA_BROAD_DATA_MATRIX);
        FileFilter celFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".cel");
            }
        };
        for (File celFile : MageTabDataFiles.TCGA_BROAD_DATA_DIRECTORY.listFiles(celFilter)) {
            upload(celFile);
        }

        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN);
        waitForAction();
        assertTrue(selenium.isTextPresent("files uploaded"));

        // - Import files
        selenium.click("selectAllCheckbox");
        selenium.click("link=Import");
        waitForAction();

        // - hit the refresh button until files are imported
        waitForImport("Nothing found to display");

        // - click on the Imported data tab
        selenium.click("link=Imported Data");
        waitForText("29 items found");

        // make experiment public
        submitExperiment();
        makeExperimentPublic(experimentId);
        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime)/60000f);
        System.out.println("total time = " + totalTime);
    }


    private void importArrayDesign(String arrayDesignName, File arrayDesign) throws Exception {
        selenium.click("link=Manage Array Designs");
        selenium.waitForPageToLoad("30000");
        if (doesArrayDesignExists(arrayDesignName)) {
            assertTrue(arrayDesignName + " is present", 1 == 1);
        } else {
            addArrayDesign(arrayDesign, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
            // get the array design row so we do not find the wrong Imported text
            int row = getExperimentRow(arrayDesignName, ZERO_COLUMN);
            // wait for array design to be imported
            waitForArrayDesignImport(TWENTY_MINUTES, row);
        }
    }

     private void checkFileStatus(String status, int column) {
        System.out.println("statu = " + status);
        for (int row = 1; row < NUMBER_OF_FILES; row++) {
            System.out.println("row = " + row);
            assertEquals(status, selenium.getTable("row." + row + "." + column));
        }
    }


}
