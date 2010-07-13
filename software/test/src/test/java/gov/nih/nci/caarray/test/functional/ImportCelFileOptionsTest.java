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
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * 
 * Tests the import options.
 */
public class ImportCelFileOptionsTest extends AbstractSeleniumTest {

    @Test
    public void testImportCelFileOptions() throws Exception {
        String title = "ImportOptions " + System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        // Create experiment
        createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        // autocreats the biomaterial chain - Source -> Sample -> Extract -> Labeled Extract -> Hybridization
        optionTest(AUTOCREATE_ANNOTATION_SET, AffymetrixArrayDataFiles.TEST3_CEL, 1, AffymetrixArrayDataFiles.TEST3_CEL
                .getName());
        verifyImport(AffymetrixArrayDataFiles.TEST3_CEL.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CEL.getName().indexOf('.')));
        selenium.click("link=Data");
        waitForTab();

        // Autocreate a single annotation set for a named annotation
        optionTest(AUTOCREATE_SINGLE_ANNOTATION, AffymetrixArrayDataFiles.TEST3_CHP, 1,
                AffymetrixArrayDataFiles.TEST3_CHP.getName());
        verifyImport(AffymetrixArrayDataFiles.TEST3_CHP.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CHP.getName().indexOf('.')));
        selenium.click("link=Data");
        waitForTab();

        // Associates selected file(s) to existing biomaterial or hybridization
        optionTest(ASSOCIATE_TO_EXISTING_BIOMATERIAL, AffymetrixArrayDataFiles.TEST3_CALVIN_CEL, 1,
                AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName());
        verifyImportToExistingBiomaterial(AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName().lastIndexOf('.')));

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }

    /**
     * Check if the biomaterial got imported
     * 
     * @param string bioMaterialName look for this name on the Source tab
     */
    private void verifyImport(String bioMaterialName) {
        selenium.click("link=Annotations");
        waitForText("Experiment Design Types");
        selenium.click("link=Sources");
        waitForText(bioMaterialName);
        assertTrue("Unable to find " + bioMaterialName + " on the Source tab", selenium.isTextPresent(bioMaterialName));
    }

    private void verifyImportToExistingBiomaterial(String bioMaterialName) {
        String relatedSample = null;
        String source = null;
     
        selenium.click("link=Annotations");
        waitForText("Quality Control Description");
        selenium.click("link=Sources");
        waitForText("Related Samples");
        boolean foundBioMaterialNameInSamplesColumn = false;
        for (int i = 1; i < 3; i++) {
            relatedSample = selenium.getTable("row." + i + ".3");
            source = selenium.getTable("row." + i + ".0");
            // ensure the biomaterial is not in the Source column
            assertFalse(bioMaterialName + " was found as a Source and should not be", source.equalsIgnoreCase(bioMaterialName));
            // check if the bioMaterial is in the Related Samples column
            if (relatedSample.contains(bioMaterialName)){
                foundBioMaterialNameInSamplesColumn = true;
            }
        }
        assertTrue("Unable to find " + bioMaterialName + " in the Related Samples column", foundBioMaterialNameInSamplesColumn);
        
        // Check the files got linked together.  See if two files are available for download
        selenium.click("link=" + DEFAULT_SOURCE_NAME);
        waitForText("Uncompressed Size");
        assertTrue("failed to find file Test3-1-121502.CHP", selenium.isTextPresent("Test3-1-121502.CHP"));
        assertTrue("failed to find file Test3-1-121502.calvin.CEL", selenium.isTextPresent("Test3-1-121502.calvin.CEL"));
    }

    /**
     * Upload the file using the various upload options
     * 
     * @param forThisOption
     * @param thisFile
     * @param numberOfFiles
     * @param lookForThisText
     * @throws IOException
     */
    private void optionTest(int forThisOption, File thisFile, int numberOfFiles, String lookForThisText)
            throws IOException {
        upload(thisFile);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, numberOfFiles);
        importData(forThisOption);
        // - click on the Imported data tab and re-click until data
        // - can be found
        // - validate the status
        checkFileStatus("Imported", THIRD_COLUMN, numberOfFiles);
    }
}
