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
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import org.junit.Test;

public class ClickAllSortLinksTest extends AbstractSeleniumTest {

    @Test
    public void testSortLinks() throws Exception {
        String title = "click test" + System.currentTimeMillis();

        // - Login
        loginAsPrincipalInvestigator();
         // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        // Create project
        createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());
        // annotation tab
        annotationLinks();
        // data tab
        dataLinks();
        // browse links
        clickBrowseLinks();
        // Left menu
        clickArrayDesignLinks();
        clickMangeProtocolLinks();
        clickManageVocabularyTabs();
        clickManageCollaborationGroupLinks();
        clickMyExperimentWorkspace();
    }

    private void clickBrowseLinks() {
        selenium.click("link=Browse");
        waitForText("Search caArray");
        selenium.click("link=Experiments");
        waitForTab();
        assertTrue("Browse Experiment link failed", selenium.isTextPresent("Condition/Disease State"));
        selenium.click("link=Browse");
        waitForText("Search caArray");
        selenium.click("link=Organisms");
        waitForTab();
        assertTrue("Browse Organism link failed", selenium.isTextPresent("Condition/Disease State"));
        selenium.click("link=Browse");
        waitForText("Search caArray");
        selenium.click("link=Array Providers");
        waitForTab();
        assertTrue("Browse Array Providers link failed", selenium.isTextPresent("Condition/Disease State"));
        selenium.click("link=Browse");
        waitForText("Search caArray");
        selenium.click("link=Unique Array Designs");
        waitForTab();
        assertTrue("Browse Unique Array Designs link failed", selenium.isTextPresent("Condition/Disease State"));
    }

    // data tab
    private void dataLinks() {
        selenium.click("link=Data");
        waitForText("File Name");
        clickManageDataLinks();
        clickImportedDataLinks();
        clickSupplementalFilesLinks();
        clickDownloadDataLinks();
        clickPublicationsLinks();
    }

    private void clickManageDataLinks() {
        selenium.click("link=Manage Data");
        waitForText("Upload New File(s)");
        assertTrue("Upload file button is missing", selenium.isTextPresent("Upload New File(s)"));
        selenium.click("link=File Name");
        waitForTab();
        selenium.click("link=File Type");
        waitForTab();
        selenium.click("link=Status");
        waitForTab();
    }

    private void clickImportedDataLinks() {
        selenium.click("link=Imported Data");
        waitForText("Status");
        assertTrue("Import Data tab -  Status is missing", selenium.isTextPresent("Status"));
        selenium.click("link=File Name");
        waitForTab();
        selenium.click("link=File Type");
        waitForTab();
        selenium.click("link=Status");
        waitForTab();
    }

    private void clickSupplementalFilesLinks() {
        selenium.click("link=Supplemental Files");
        waitForText("Status");
        assertTrue("Supplemental Data tab -  Status is missing", selenium.isTextPresent("Status"));
        selenium.click("link=File Name");
        waitForTab();
        selenium.click("link=File Type");
        waitForTab();
        selenium.click("link=Status");
        waitForTab();
    }

    private void clickDownloadDataLinks() {
        selenium.click("link=Download Data");
        waitForText("Download Experiment File Packages");
        assertTrue("Download -  Download Experiment File Packages is missing", selenium.isTextPresent("Download Experiment File Packages"));
        assertTrue("Download -  Download Selected Experiment Files is missing", selenium.isTextPresent("Download Selected Experiment Files"));
        assertTrue("Download -  Export Experiment Annotation Packages is missing", selenium.isTextPresent("Export Experiment Annotation Packages"));
    }

    private void clickPublicationsLinks() {
        selenium.click("link=Publications");
        waitForText("Add a new Publication");
        assertTrue("Publication button is missing", selenium.isTextPresent("Add a new Publication"));
        selenium.click("link=Title");
        waitForTab();
        selenium.click("link=Authors");
        waitForTab();
        selenium.click("link=URL");
        waitForTab();
    }

    // END data tab

    // Start annotations tab
    private void annotationLinks() throws InterruptedException {
        selenium.click("link=Annotations");
        waitForText("Experimental Design");
        waitForTab();
        clickExperimentalFactorsLinks();
        clickSourcesLinks();
        clickSamplesLinks();
        clickExtractsLinks();
        clickLabeledExtractsLinks();
        clickHybridizationsLinks();
    }

    private void clickExperimentalFactorsLinks() {
        selenium.click("link=Experimental Factors");
        waitForText("Add a new Experimental Factor");
        assertTrue("Experimental Factor is missing", selenium.isTextPresent("Add a new Experimental Factor"));
        selenium.click("link=Factor Name");
        waitForTab();
        selenium.click("link=Category");
        waitForTab();
    }

    private void clickSourcesLinks() throws InterruptedException {
        selenium.click("link=Sources");
        waitForText("Add a new Source");
        assertTrue("Source button is missing", selenium.isTextPresent("Add a new Source"));
        selenium.click("link=Source Name");
        waitForTab();
        selenium.click("link=Description");
        waitForTab();
    }

    private void clickSamplesLinks() throws InterruptedException {
        selenium.click("link=Samples");
        waitForText("Add a new Sample");
        assertTrue("Sample button is missing", selenium.isTextPresent("Add a new Sample"));
        selenium.click("link=Sample Name");
        waitForTab();
        selenium.click("link=Description");
        waitForTab();
    }

    private void clickExtractsLinks() {
        selenium.click("link=Extracts");
        waitForText("Add a new Extract");
        assertTrue("Extract button is missing", selenium.isTextPresent("Add a new Extract"));
        selenium.click("link=Extract Name");
        waitForTab();
        selenium.click("link=Description");
        waitForTab();
    }

    private void clickLabeledExtractsLinks() {
        selenium.click("link=Labeled Extracts");
        waitForText("Add a new Labeled Extract");
        assertTrue("Labeled Extract button is missing", selenium.isTextPresent("Add a new Labeled Extract"));
        selenium.click("link=Labeled Extract Name");
        waitForTab();
        selenium.click("link=Description");
        waitForTab();
    }

    private void clickHybridizationsLinks() {
        selenium.click("link=Hybridizations");
        waitForText("Add a new Hybridization");
        assertTrue("Add hyb button is not present", selenium.isTextPresent("Add a new Hybridization"));
        selenium.click("link=Hybridization Name");
        waitForTab();
    }

    // END annotations tab

    // start left hand menu
    private void clickArrayDesignLinks() throws InterruptedException {
        selenium.click("link=Manage Array Designs");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout - failed loading Manage Array Designs page");
            try {
                System.out.println("second " + second);
                if (selenium.isTextPresent("Import a New Array Design")) {
                    System.out.println("found link : assay type");
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        assertTrue("Did not get to Array Design Page", selenium.isTextPresent("Import a New Array Design"));
        selenium.click("link=Array Design Name");
        selenium.click("link=Provider");
        selenium.click("link=Version Number");
        selenium.click("link=Feature Type");
        selenium.click("link=Organism");
        selenium.click("link=Status");
    }

    private void clickExperimentSortLinks() {
        selenium.click("link=Experiment ID");
        waitForText("Experiment ID");
        selenium.click("link=Experiment Title");
        waitForText("Experiment Title");
   }

    private void clickManageCollaborationGroupLinks() {
        System.out.println("Clicking on Manage Collaboration Groups");
        selenium.click("link=Manage Collaboration Groups");
        waitForText("Group Members");
        selenium.click("link=Collaboration Group Name");
    }

    private void clickManageVocabularyTabs() {

        System.out.println("Clicking on Manage Vocabulary");
        selenium.click("link=Manage Vocabulary");
        waitForText("Manage Tissue Sites");
        clickTissueSiteLinks();
        clickMaterialTypeLinks();
        clickCellTypeLinks();
        clickConditionDiseaseStateLinks();
    }

    private void clickConditionDiseaseStateLinks() {
        selenium.click("link=Conditions/Disease States");
        waitForText("Value");              
        assertTrue(selenium.isTextPresent("Add Conditions/Disease States"));
        selenium.click("link=Value");
        clickCommonHeaders();
    }

    private void clickCellTypeLinks() {
        System.out.println("Clicking on Cell Types");
        selenium.click("link=Cell Types");
        waitForText("Add Cell Types");
        assertTrue(selenium.isTextPresent("Add Cell Types"));
        waitForText("Value");
        clickCommonHeaders();
    }

    private void clickTissueSiteLinks() {
        waitForText("Value");
        assertTrue(selenium.isTextPresent("Add Tissue Sites"));
        clickCommonHeaders();
    }

    private void clickMaterialTypeLinks() {
        System.out.println("Clicking on Material Types");
        selenium.click("link=Material Types");
        waitForText("Add Material Types");
        assertTrue(selenium.isTextPresent("Add Material Types"));
        clickCommonHeaders();
    }

    private void clickCommonHeaders() {
        selenium.click("link=Value");
        waitForText("Value");
        selenium.click("link=Description");
        waitForText("Description");
        selenium.click("link=Source");
        waitForText("Source");
    }

    private void clickMangeProtocolLinks() {
        System.out.println("Clicking on Manage Protocols");
        selenium.click("link=Manage Protocols");
        clickProtocolLinks();
        clickProtocolTypes();
    }

    private void clickProtocolLinks() {
        waitForText("Description");
        waitForText("Add Protocol");
        assertTrue(selenium.isTextPresent("Add Protocol"));
        selenium.click("link=Name");
        waitForTab();
        selenium.click("link=Type");
        waitForTab();
        selenium.click("link=Source");
        waitForTab();
        selenium.click("link=Description");
        waitForTab();
        selenium.click("link=Contact");
        waitForTab();
        selenium.click("link=Url");
        waitForTab();
    }

    private void clickProtocolTypes() {
        System.out.println("Clicking on Protocol Types");
        selenium.click("link=Protocol Types");
        waitForText("Add Protocol Types");
        assertTrue(selenium.isTextPresent("Add Protocol Types"));
        clickCommonHeaders();
    }

    private void clickMyExperimentWorkspace() {
        selenium.click("link=My Experiment Workspace");
        waitForText("Status");
        selenium.click("link=Status");
        clickExperimentSortLinks();
    }

}
