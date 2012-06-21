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

import org.junit.Test;

/**
 *
 * Tests browsing experiments and deleting an experiment with cel files.
 *
 */
public class BrowseExperimentTest extends AbstractSeleniumTest {

    @Test
    public void testBrowsing() throws Exception {
        String title = "browsable " + System.currentTimeMillis();
        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());

        // - Create an Experiment
        String experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // - Submit Experiment Proposal
        lockExperimentFromEdits();
        makeExperimentPublic(experimentId);

        // - logout
        selenium.click("link=Logout");
        waitForText("Browse caArray");

        // - Click on Experiments link on the login page
        selenium.click("link=Experiments");
        waitForText("found");
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Organisms
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Organisms");
        waitForText("found");
        // Click on the Homo sapiens tab incase there is more than one tab
        selenium.click("link=Homo sapiens (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Array Providers
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Array Providers");
        waitForText("found");
        // Click on the Affymetrix tab incase there is more than one tab
        selenium.click("link=Affymetrix (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();

        // - Browse by Unique Array Designs
        selenium.click("link=Browse");
        waitForText("Welcome to the caArray Data Portal");
        selenium.click("link=Unique Array Designs");
        waitForText("found");
        // Click on the Affymetrix tab incase there is more than one tab
        selenium.click("link=Test3 (*");
        waitForTab();
        // - Assert the Experiment is visible without logging in
        findTitleAcrossMultiPages(experimentId);
        // - Assert correct columns are displayed as per use case
        assertColumnHeaders();
    }

    @Test
    public void testDeleteProject() throws Exception {
        String title = "delete " + System.currentTimeMillis();
        loginAsPrincipalInvestigator();
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        String experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // file upload based on ImportAffymetrixChpTest.testImportAndRetrieval()
        // - go to the data tab
        pause(1000);
        selenium.click("link=Data");
        waitForTab();
        upload(AffymetrixArrayDataFiles.TEST3_SPECIFICATION_ZIP);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, 1);
        // - Import files
        importData(MAGE_TAB);
        
        selenium.click("link=My Experiment Workspace");
        waitForText("Public Access");

        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        if (row == -1){
            fail("Did not find experiment with id = " + experimentId);
        }
        pause(1000);
        assertTrue("Delete icon is missing at row "+row+" column 10", selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[10]/a/img"));
        selenium.chooseCancelOnNextConfirmation();
        selenium.click("//tr[" + row + "]/td[10]/a/img");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to delete this experiment\\?$"));
        pause(1000);
        assertEquals(row, getExperimentRow(experimentId, ZERO_COLUMN));
        selenium.click("//tr[" + row + "]/td[10]/a/img");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to delete this experiment\\?$"));
        waitForText("Experiment has been deleted.");
        assertFalse(selenium.isTextPresent(experimentId));

        // testing the delete icon is not present when an experiment is in the 'locked' state.
        experimentId = createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());
        lockExperimentFromEdits();
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // ensure the delete icon is not present
        assertFalse(selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[8]/a/img"));
    }

    private void assertColumnHeaders() {
        verifyEquals("Experiment ID", selenium.getText("link=Experiment ID"));
        verifyEquals("Experiment Title", selenium.getText("link=Experiment Title"));
        assertTrue("Primary Contact header not found", selenium.isTextPresent("Primary Contact"));
        verifyEquals("Organism", selenium.getText("link=Organism"));
        assertTrue("Condition/Disease State header not found", selenium.isTextPresent("Condition/Disease State"));
        assertTrue("Samples header not found", selenium.isTextPresent("Samples"));
        verifyEquals("Updated", selenium.getText("link=Updated"));
    }

}
