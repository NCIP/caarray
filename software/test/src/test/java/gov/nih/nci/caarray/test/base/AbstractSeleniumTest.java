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
package gov.nih.nci.caarray.test.base;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * Base class for all functional tests that use Selenium Remote Control. Provides proper set up in order to be called by
 * caArray's ant script.
 * 
 */
public abstract class AbstractSeleniumTest extends SeleneseTestCase {

    private static final int PAGE_TIMEOUT_SECONDS = 180;
    private static final String LOGIN_BUTTON = "//div[2]/form/table/tbody/tr[4]/td/del/ul/li/a/span/span";
    protected static final String TAB_KEY = "\\009";
    protected static int RECORD_TIMEOUT_SECONDS = 240;
    protected static final int PAGE_SIZE = 20;
    protected static final String REFRESH_BUTTON = "//a[6]/span/span";
    private static final String UPLOAD_BUTTON = "//ul/a[3]/span/span";
    protected static final String ZERO_COLUMN = "0";
    protected static final String FIRST_COLUMN = "1";
    protected static final int SECOND_COLUMN = 2;
    protected static final int THIRD_COLUMN = 3;
    protected static final String IMPORTED = "Imported";

    @Override
    public void setUp() throws Exception {
        System.setProperty("selenium.port", "" + TestProperties.getSeleniumServerPort());
        String hostname = TestProperties.getServerHostname();
        int port = TestProperties.getServerPort();
        String browser = System.getProperty("test.browser", "*chrome");
        if (port == 0) {
            super.setUp("http://" + hostname, browser);
        } else {
            super.setUp("http://" + hostname + ":" + port, browser);

        }
        selenium.setTimeout(toMillisecondsString(PAGE_TIMEOUT_SECONDS));
    }

    private String toMillisecondsString(long seconds) {
        return String.valueOf(seconds * 1000);
    }

    protected void waitForPageToLoad() {
        selenium.waitForPageToLoad(toMillisecondsString(PAGE_TIMEOUT_SECONDS));
    }

    protected void clickAndWait(String linkOrButton) {
        selenium.click(linkOrButton);
        waitForPageToLoad();
    }

    protected void loginAsPrincipalInvestigator() {
        selenium.open("/caarray/");

        selenium.type("j_username", "caarrayadmin");
        selenium.type("j_password", "caArray2!");
        clickAndWait(LOGIN_BUTTON);
    }

    protected void upload(File file) throws IOException {
        upload(file, RECORD_TIMEOUT_SECONDS);
    }

    protected void upload(File file, int timeoutSeconds) throws IOException {
        upload(file, timeoutSeconds, true);
    }

    protected void upload(File file, long timeoutSeconds, boolean runAssert) throws IOException {
        String filePath = file.getCanonicalPath().replace('/', File.separatorChar);
        selenium.type("upload", filePath);
        // selenium.click("link=Upload");
        selenium.click(UPLOAD_BUTTON);
        waitForAction(timeoutSeconds);
        if (runAssert) {
            assertTrue(selenium.isTextPresent(file.getName()));
        }
    }

    protected void waitForElementWithId(String id, long timeoutSeconds) {
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementById('" + id
                + "') != null", toMillisecondsString(timeoutSeconds));
    }

    protected void waitForElementWithId(String id) {
        waitForElementWithId(id, PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForAction() {
        waitForAction(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForAction(long timeoutSeconds) {
        waitForDiv("submittingText", timeoutSeconds);
    }

    protected void waitForTab() {
        waitForTab(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForTab(long timeoutSeconds) {
        waitForDiv("loadingText", timeoutSeconds);
    }

    protected void waitForDiv(String divId) {
        waitForDiv(divId, PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForDiv(String divId, long timeoutSeconds) {
        selenium.waitForCondition("element = selenium.browserbot.getCurrentWindow().document.getElementById('" + divId
                + "'); element != null && element.style.display == 'none';", toMillisecondsString(timeoutSeconds));
    }

    protected void waitForSecondLevelTab() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitForSecondLevelTab(PAGE_TIMEOUT_SECONDS);
    }

    protected void waitForSecondLevelTab(int timeoutSeconds) {
        waitForElementWithId("tabboxlevel2wrapper", timeoutSeconds);
    }

    protected void waitForText(String id) {
        waitForText(id, Integer.valueOf(RECORD_TIMEOUT_SECONDS));
    }

    protected void waitForText(String id, int waitTime) {
        for (int second = 0;; second++) {
            if (second >= Integer.valueOf(waitTime))
                fail("timeout");
            try {
                if (selenium.isTextPresent(id))
                    break;
            } catch (Exception e) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void createExperiment(String title) throws InterruptedException {
        String arrayDesignName = null;
        createExperiment(title, arrayDesignName);
    }

    /**
     * @param title
     * @throws InterruptedException
     */
    protected void createExperiment(String title, String arrayDesignName) throws InterruptedException {
        Thread.sleep(1000);
        selenium.click("link=Create/Propose Experiment");
        waitForElementWithId("projectForm_project_experiment_title");
        // - Type in the Experiment name
        selenium.type("projectForm_project_experiment_title", title);
        // - Description
        selenium.type("projectForm_project_experiment_description", "desc");
        // - Assay Type
        selenium.select("projectForm_project_experiment_assayType", "label=Gene Expression");
       // waitForElementWithId("progressMsg"); -- does not work
        Thread.sleep(1000);
        // - Provider
        selenium.select("projectForm_project_experiment_manufacturer", "label=Affymetrix");
        //waitForElementWithId("progressMsg");
        Thread.sleep(1000);
        // - Array Design - correct array design must be associated with the experiment
        if (arrayDesignName != null) {
            selenium.addSelection("projectForm_project_experiment_arrayDesigns", "label=" + arrayDesignName);
        }

        // - Organism
        selenium.select("projectForm_project_experiment_organism", "label=Homo sapiens (ncbitax)");
        // - Save the Experiment
        selenium.click("link=Save");
        waitForAction();

    }

    protected void addArrayDesign(String arrayDesignName, File arrayDesign) {
        selenium.click("link=Import a New Array Design");
        waitForText("Array Design Details");
        // selenium.type("arrayDesignForm_arrayDesign_name", arrayDesignName);
        selenium.select("arrayDesignForm_arrayDesign_assayType", "label=Gene Expression");
        selenium.select("arrayDesignForm_arrayDesign_provider", "label=Affymetrix");
        selenium.type("arrayDesignForm_arrayDesign_version", "100");
        selenium.select("arrayDesignForm_arrayDesign_technologyType", "label=in_situ_oligo_features (MO)");
        selenium.select("arrayDesignForm_arrayDesign_organism", "label=Homo sapiens (ncbitax)");
        selenium.type("arrayDesignForm_upload", arrayDesign.toString());
        selenium.click("link=Save");
        waitForText("found");
    }

    protected void findTitleAcrossMultiPages(String text) throws Exception {
        for (int page = 1;; page++) {
            // - Safety catch
            if (page == 50) {
                fail("Did not find title after searching " + page + " pages");
                break;
            }
            if (selenium.isTextPresent(text)) {
                assertTrue(1 == 1);
                break;
            } else {
                // Moving to next page
                selenium.click("link=Next");
                Thread.sleep(4000); // TBD - figure out what to "wait" on. All pages are similar. No "waiting" icon
            }
        }
    }

    protected void setExperimentPublic() {
        selenium.click("link=Make Experiment Public");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to change the project's status[\\s\\S]$"));
    }

    protected void submitExperiment() {
        selenium.click("link=Submit Experiment Proposal");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to change the project's status[\\s\\S]$"));
        waitForText("Permissions");
    }

    protected boolean waitForArrayDesignImport(int seconds, int row) throws Exception {
        for (int loop = 1; loop < seconds; loop++) {
            selenium.click("link=Manage Array Designs");
            // done
            String rowText = selenium.getTable("row." + row + ".7");
            if (rowText.equalsIgnoreCase(IMPORTED)) {
                return true;
            } else {
                Thread.sleep(10000);
            }
        }
        fail("Timeout waiting for Array Design to Import");
        return false;

    }

    protected int getExperimentRow(String text, String column) {
        for (int loop = 1;; loop++) {
            if (loop % PAGE_SIZE != 0) {
                if (text.equalsIgnoreCase(selenium.getTable("row." + loop + "." + column))) {
                    return loop;
                }
            } else {
                // Moving to next page
                // (this will fail once there are no more pages
                selenium.click("link=Next");
                waitForAction();
                loop = 1;
            }
        }
    }

    protected boolean waitForImport(String textToWaitFor) throws Exception {
        int ten_minutes = 60;
        for (int time = 1;; time++) {
            if (time == ten_minutes) {
                fail("Timeout waiting for Import");
                return false;
            }
            if (selenium.isTextPresent("Failed Validation")) {
                fail("Validation Failed during Import");
            }
            selenium.click(REFRESH_BUTTON);
            if (selenium.isTextPresent(textToWaitFor)) {
                // done
                return true;
            } else {
                Thread.sleep(10000);
            }
        }
    }

}
