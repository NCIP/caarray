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
import java.text.DecimalFormat;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * Base class for all functional tests that use Selenium Remote Control. Provides proper set up in order to be called by
 * caArray's ant script.
 *
 */
public abstract class AbstractSeleniumTest extends SeleneseTestCase {

    protected DecimalFormat df = new DecimalFormat("0.##");
    private static final int PAGE_TIMEOUT_SECONDS = 180;
    private static final String LOGIN_BUTTON = "//div[2]/form/table/tbody/tr[4]/td/del/ul/li/a/span/span";
    protected static final String TAB_KEY = "\\009";
    protected static int RECORD_TIMEOUT_SECONDS = 240;
    protected static int FIFTEEN_MINUTES = 900;
    protected static final int PAGE_SIZE = 20;
    protected static final String REFRESH_BUTTON = "//a[6]/span/span";
    private static final String UPLOAD_BUTTON = "//ul/a[3]/span/span";
    protected static final String ZERO_COLUMN = "0";
    protected static final String FIRST_COLUMN = "1";
    protected static final int SECOND_COLUMN = 2;
    protected static final int THIRD_COLUMN = 3;
    protected static final String IMPORTED = "Imported";
    protected static final String AFFYMETRIX_PROVIDER = "Affymetrix";
    protected static final String HOMO_SAPIENS_ORGANISM = "Homo sapiens (ncbitax)";

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

    protected void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
     * Login using any of the preloaded users. The password is hardcoded for these users caarrayadmin caarrayuser
     * researchscientist labadministrator labscientist biostatistician systemadministrator collaborator
     */
    protected void loginAs(String userid) {
        selenium.open("/caarray/");
        selenium.type("j_username", userid);
        selenium.type("j_password", "caArray2!");
        clickAndWait(LOGIN_BUTTON);
    }

    protected void loginAs(String userid, String password) {
        selenium.open("/caarray/");
        selenium.type("j_username", userid);
        selenium.type("j_password", password);
        clickAndWait(LOGIN_BUTTON);
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
        selenium.click("link=Upload New File(s)");
        waitForPopup("uploadWindow", timeoutSeconds);
        selenium.selectWindow("uploadWindow");
        String filePath = file.getCanonicalPath().replace('/', File.separatorChar);
        selenium.type("upload", filePath);
        selenium.click(UPLOAD_BUTTON);
        pause(2000);
        waitForDiv("uploadProgressFileList", timeoutSeconds);
        selenium.getAlert();

        if (runAssert) {
            if (file == null) {
                fail("upload file name is null");
            }
            assertTrue(file.getName() + " was not uploaded.", selenium.isTextPresent(file.getName()));
        }

        selenium.click("link=Close Window");
        selenium.selectWindow(null);

        pause(1000);
        selenium.click("link=Data");
        waitForTab();
        waitForSecondLevelTab();
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
        pause(200);
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
                fail("timeout waiting for text " + id + ". Exceeded wait time: " + waitTime);
            try {
                if (selenium.isTextPresent(id))
                    break;
            } catch (Exception e) {
            }
            pause(1000);
        }
    }

    protected void waitForPopup(String id, long waitTime) {
        selenium.waitForPopUp(id, toMillisecondsString(waitTime));
    }

    protected String createExperiment(String title, String arrayDesignName) throws InterruptedException {
        return createExperiment(title, arrayDesignName, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }

    protected String createExperiment(String title) throws InterruptedException {
        String arrayDesignName = null;
        return createExperiment(title, arrayDesignName, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }

    /**
     * @param title
     * @throws InterruptedException
     * @return String Experiment ID of the experiment e.g. admin-002
     */
    protected String createExperiment(String title, String arrayDesignName, String provider, String organism)
            throws InterruptedException {
        String experimentId;
        selenium.click("link=Create/Propose Experiment");
        waitForText("The Overall Experiment Characteristics"); // needed when creating multiple experiments
        // - Type in the Experiment name
        selenium.type("projectForm_project_experiment_title", title);
        // - Description
        selenium.type("projectForm_project_experiment_description", "desc");
        // - Assay Type
        selenium.select("projectForm_project_experiment_assayType", "label=Gene Expression");
        // - Provider
        if (provider == null) {
            provider = AFFYMETRIX_PROVIDER; // default to Affy
        }
       // selenium.select("projectForm_project_experiment_manufacturer", "label=" + provider);
        // ** Neither of the following would wait properly for the list of Array Designs to fill **
        // Thread.sleep(1000);
        // waitForElementWithId("progressMsg");
        selectArrayDesign(arrayDesignName, provider);

        // - Organism
        if (organism == null) {
            organism = HOMO_SAPIENS_ORGANISM;
        }
        selenium.select("projectForm_project_experiment_organism", "label=" + organism);

        // - Save the Experiment
        selenium.click("link=Save");
        waitForAction();
        // get the experiment id
        experimentId = selenium.getText("//tr[4]/td[2]");
        return experimentId;
    }

    /**
     * Waits for the list of Array Designs to fill before making the selection. Test fails if the array design is not in
     * the list
     *
     * @param arrayDesignName
     */
    private void selectArrayDesign(String arrayDesignName, String provider) {
        String[] values;
        boolean found = false;
        selenium.select("projectForm_project_experiment_manufacturer", "label=" + provider);

        if (arrayDesignName == null) {
            return;
        }

        for (int second = 1;; second++) {
            values = selenium.getSelectOptions("projectForm_project_experiment_arrayDesigns");
            // - find the array design in the list of values
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(arrayDesignName)) {
                    selenium.addSelection("projectForm_project_experiment_arrayDesigns", "label=" + arrayDesignName);
                    found = true;
                }
            }

            if (found) {
                return;
            } else {
                pause(1000);
                // - sleep for one second if not found yet
            }
            if (second%5 == 0){
                selenium.select("projectForm_project_experiment_manufacturer", "label=--Select a Provider--");
                pause(1000);
                selenium.select("projectForm_project_experiment_manufacturer", "label=" + provider);
                System.out.println("Reselected provider " + provider);
            }
            if (second > 30) {
               fail("Unable to find the array design " + arrayDesignName + " after " + second + " seconds");
            }
        }
    }

    protected void addArrayDesign(File arrayDesign) {
        String arrayDesignProvider = AFFYMETRIX_PROVIDER;
        String arrayDesignOrganism = HOMO_SAPIENS_ORGANISM;
        addArrayDesign(arrayDesign, arrayDesignProvider, arrayDesignOrganism);
    }

    protected void addArrayDesign(File arrayDesign, String arrayDesignProvider, String arrayDesignOrganism) {
        selenium.click("link=Import a New Array Design");
        waitForText("Array Design Details");
        selenium.select("arrayDesignForm_arrayDesign_assayType", "label=Gene Expression");
        if (arrayDesignProvider == null) {
            arrayDesignProvider = AFFYMETRIX_PROVIDER; // default to Affy
        }
        selenium.select("arrayDesignForm_arrayDesign_provider", "label=" + arrayDesignProvider);

        selenium.type("arrayDesignForm_arrayDesign_version", "100");
        selenium.select("arrayDesignForm_arrayDesign_technologyType", "label=in_situ_oligo_features (MO)");
        if (arrayDesignOrganism == null) {
            // default to Homo sapiens
            arrayDesignOrganism = HOMO_SAPIENS_ORGANISM;
        }
        selenium.select("arrayDesignForm_arrayDesign_organism", "label=" + arrayDesignOrganism);

        selenium.type("arrayDesignForm_upload", arrayDesign.toString());
        selenium.click("link=Save");
        waitForText("found");
    }

    protected void findTitleAcrossMultiPages(String text) {
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
                pause(4000); // TBD - figure out what to "wait" on. All pages are similar. No "waiting" icon
            }
        }
    }

    protected void setExperimentPublic() {
        String makeExperimentPublicButton = "//span/span";
        selenium.click(makeExperimentPublicButton);
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to change the project's status[\\s\\S]$"));
        // making an experiement public will return the user back to their experiment workspace
        waitForText("My Experiment Workspace");
    }

    protected void submitExperiment() {
        String submitExperimentProposalButton = "//span/span";
        // selenium.click("link=Submit Experiment Proposal");
        selenium.click(submitExperimentProposalButton);
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure you want to change the project's status[\\s\\S]$"));
        // making an experiement public will return the user back to the experiment's main page
        waitForText("My Experiment Workspace");
    }

    /**
     *
     * @param seconds
     * @param row
     * @return
     * @throws Exception
     */
    protected boolean waitForArrayDesignImport(int seconds, int row) throws Exception {
        for (int loop = 1; loop < seconds; loop++) {
            selenium.click("link=Manage Array Designs");
            pause(2000);
            waitForText("Edit");
            // done
            String rowText = selenium.getTable("row." + row + ".7");
            if (rowText.equalsIgnoreCase(IMPORTED)) {
                return true;
            } else {
                pause(10000);
            }
        }
        fail("Timeout waiting for Array Design to Import");
        return false;

    }

    /**
     *
     * @param text - value to seach for
     * @param column - Table column which contains the search text
     * @return int the row number
     *
     * Returns the row where the data resides in a particular column For example - find title "Exp 1" in column 1
     * (column 1 holds the experiment names)
     */

    protected int getExperimentRow(String text, String column) {
        int page = 1;
        for (int loop = 1;; loop++) {
            if (text.equalsIgnoreCase(selenium.getTable("row." + loop + "." + column))) {
                return loop;
            }

            if (loop % PAGE_SIZE == 0) {
                System.out.println("page number " + (++page));
                // Moving to next page
                // this will fail once there are no more pages and the text parameter is not found
                try {
                    selenium.click("link=Next");
                } catch (Exception e1) {
                    fail("Did not find " + text);
                }
                waitForDiv("loadingText");
                pause(2000);
                loop = 0;
            }
        }
    }

    protected int getExperimentRow(String text, String column, String textToWaitFor) {
        for (int loop = 1;; loop++) {
            if (text.equalsIgnoreCase(selenium.getTable("row." + loop + "." + column))) {
                return loop;
            }
            if (loop % PAGE_SIZE == 0) {
                // Moving to next page
                // this will fail once there are no more pages and the text parameter is not found
                selenium.click("link=Next");
                waitForText(textToWaitFor);
                pause(1000);
                loop = 0;
            }
        }
    }

    protected boolean waitForImport(String textToWaitFor) {
        int ten_minutes = 60;
        for (int time = 1;; time++) {
            if (time == ten_minutes) {
                fail("Timeout waiting for Import");
                return false;
            }
            if (selenium.isTextPresent("Failed Validation")) {
                fail("Validation Failed during Import");
            }
            if (selenium.isTextPresent("Import Failed")) {
                fail("Import Failed");
            }
            selenium.click(REFRESH_BUTTON);
            if (selenium.isTextPresent(textToWaitFor)) {
                // done
                return true;
            } else {
                pause(10000);
            }
        }
    }

    protected boolean doesArrayDesignExists(String arrayDesignName) {
        return selenium.isTextPresent(arrayDesignName);
    }

    protected void makeExperimentPublic(String experimentId) {

        clickAndWait("link=My Experiment Workspace");
        waitForTab();

        // - Make the experiment public
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        // - Click on the image to enter the edit mode again
        selenium.click("//tr[" + row + "]/td[7]/a/img");
        waitForText("Overall Experiment Characteristics");

        // make experiment public
        setExperimentPublic();
    }

    protected void checkFileStatus(String status, int column, int numFiles) {
        for (int i = 1; i < numFiles; i++) {
            assertEquals(status, selenium.getTable("row." + i + "." + column));
        }
    }

}
