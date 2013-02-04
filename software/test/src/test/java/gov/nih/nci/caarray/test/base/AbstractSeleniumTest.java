//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.base;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * Base class for all functional tests that use Selenium Remote Control. Provides proper set up in order to be called by
 * caArray's ant script.
 *
 */
public abstract class AbstractSeleniumTest extends SeleneseTestCase {

    protected DecimalFormat df = new DecimalFormat("0.##");
    private static final int PAGE_TIMEOUT_SECONDS = 180;
    //private static final int PAGE_TIMEOUT_SECONDS = 5;
    private static final String LOGIN_BUTTON = "//div[2]/form/table/tbody/tr[4]/td/del/ul/li/a/span/span";
    protected static final String TAB_KEY = "\\009";
    protected static int RECORD_TIMEOUT_SECONDS = 30;
    protected static int FIFTEEN_MINUTES = 900;
    protected static final int PAGE_SIZE = 20;
    private static final String UPLOAD_BUTTON = "//ul/a[3]/span/span";
    protected static final String ZERO_COLUMN = "0";
    protected static final String FIRST_COLUMN = "1";
    protected static final int SECOND_COLUMN = 2;
    protected static final int THIRD_COLUMN = 3;
    protected static final String IMPORTED = "Imported";
    protected static final String AFFYMETRIX_PROVIDER = "Affymetrix";
    protected static final String HOMO_SAPIENS_ORGANISM = "Homo sapiens (ncbitax)";
    private static final int FORTY_MINUTES = 2400;
    protected static final int AUTOCREATE_ANNOTATION_SET = 1;
    protected static final int AUTOCREATE_SINGLE_ANNOTATION = 2;
    protected static final int ASSOCIATE_TO_EXISTING_BIOMATERIAL = 3;
    protected static final int MAGE_TAB = 4;
    protected static final String DEFAULT_SOURCE_NAME= "seleniumTest";

    @Override
    public void setUp() throws Exception {
        System.setProperty("selenium.port", "" + TestProperties.getSeleniumServerPort());
        String hostname = TestProperties.getServerHostname();
        int port = TestProperties.getServerPort();
        String browser = System.getProperty("test.browser", "*firefox");
        System.out.println("AbstractSeleniumTest: hostname =" + hostname + "=");
        System.out.println("AbstractSeleniumTest: port =" + port + "=");
        System.out.println("AbstractSeleniumTest: browser =" + browser + "=");
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
        waitForText("caArray Login");
        selenium.type("j_username", userid);
        selenium.type("j_password", "caArray2!");
        clickAndWait(LOGIN_BUTTON);
        pause(1000);
    }

    protected void loginAs(String userid, String password) {
        selenium.open("/caarray/");
        waitForText("caArray Login");
        selenium.type("j_username", userid);
        selenium.type("j_password", password);
        clickAndWait(LOGIN_BUTTON);
        pause(1000);
    }

    protected void loginAsPrincipalInvestigator() {
        selenium.open("/caarray/");
        waitForText("caArray Login");
        selenium.type("j_username", "caarrayadmin");
        selenium.type("j_password", "caArray2!");
        clickAndWait(LOGIN_BUTTON);
        pause(1000);
    }

    protected void upload(File file) throws IOException {
        upload(file, RECORD_TIMEOUT_SECONDS);
    }

    protected void upload(File file, int timeoutSeconds) throws IOException {
        waitForText("Upload New File(s)");
        selenium.click("link=Upload New File(s)");
        waitForPopup("uploadWindow", timeoutSeconds);
        selenium.selectWindow("uploadWindow");
        String filePath = file.getCanonicalPath().replace('/', File.separatorChar);
        selenium.type("upload", filePath);
        selenium.click(UPLOAD_BUTTON);
        waitForDiv("uploadProgressFileList", timeoutSeconds);
        waitForText("Close Window and go to Experiment Data", timeoutSeconds);
        selenium.getAlert();

        selenium.click("link=Close Window");
        selenium.selectWindow(null);

        waitForText("Data");
        selenium.click("link=Data");
        waitForTab();
        waitForText("File Status Summary");
    }

    protected void waitForElementWithId(String id, long timeoutSeconds) {
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementById('" + id
                + "') != null && element.style.display == 'none';", toMillisecondsString(timeoutSeconds));
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
                + "'); element != null", toMillisecondsString(timeoutSeconds));
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

    protected void reClickForText(String id, String link, int times, int waitTime) {
        selenium.click(link);
        waitForTab();
        if (!selenium.isTextPresent(id)) {
            for (int clicked = 0; clicked < times; clicked++) {
                selenium.click(link);
                pause(waitTime);
                if (selenium.isTextPresent(id)) {
                    return;
                }
            }
            fail("timeout waiting for text " + id + ". Exceeded wait time (sec): " + (times * waitTime) / 1000);
        }
    }

    protected void waitForText(String id, int waitTime) {
        for (int second = 0;; second++) {
            if (second >= Integer.valueOf(waitTime))
                fail("timeout waiting for text " + id + ". Exceeded wait time (sec): " + waitTime);
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

    /**
     * Create an experiment with an array design and the default Afftmetrix provider and human organism
     *
     * @param title
     * @param arrayDesignName
     * @return String Experiment Identifier of the experiment e.g. admin-002
     * @throws InterruptedException
     */
    protected String createExperiment(String title, String arrayDesignName) throws InterruptedException {
        return createExperiment(title, arrayDesignName, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }

    protected String createExperiment(String title, List<String> arrayDesignNames) throws InterruptedException {
        return createExperiment(title, arrayDesignNames, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }
    /**
     * Create an experiment without an array design and the default Afftmetrix provider and human organism
     *
     * @param title
     * @return String Experiment Identifier of the experiment e.g. admin-002
     * @throws InterruptedException
     */
    protected String createExperiment(String title) throws InterruptedException {
        String arrayDesignName = null;
        return createExperiment(title, arrayDesignName, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }
    protected String createExperiment(String title, String arrayDesignName, String provider, String organism) throws InterruptedException {
        List<String> arrayDesignNames = new ArrayList<String>();
        if (arrayDesignName != null){
            arrayDesignNames.add(arrayDesignName);
        }
        return createExperiment(title, arrayDesignNames, provider, organism);
    }
    /**
     * Create an experiment
     *
     * @param title
     * @throws InterruptedException
     * @return String Experiment Identifier of the experiment e.g. admin-002
     */
    protected String createExperiment(String title, List<String> arrayDesignName, String provider, String organism)
            throws InterruptedException {
        String experimentId;
        selenium.click("link=Create/Propose Experiment");
        waitForText("The Overall Experiment Characteristics"); // needed when creating multiple experiments
        // - Type in the Experiment name
        selenium.type("projectForm_project_experiment_title", title);
        // - Description
        selenium.type("projectForm_project_experiment_description", "desc");
        // - Assay Types
        selenium.keyPress("id=assayTypesSearchInput", TAB_KEY);
        // - Provider
        if (provider == null) {
            provider = AFFYMETRIX_PROVIDER; // default to Affy
        }
        selectArrayDesign(arrayDesignName, provider);

        // - Organism
        if (organism == null) {
            organism = HOMO_SAPIENS_ORGANISM;
        }
        selenium.select("projectForm_project_experiment_organism", "label=" + organism);

        // - Save the Experiment
        selenium.click("link=Save");
        // get the experiment identifier that starts with "EXP-"
        waitForText("EXP-");
        experimentId = selenium.getText("//tr[4]/td[2]");
        return experimentId;
    }

    /**
     * Waits for the list of Array Designs to fill before making the selection. Test fails if the array design is not in
     * the list
     *
     * @param arrayDesignName
     * @param provider
     */
    private void selectArrayDesign(List<String> arrayDesignName, String provider) {
        String[] values;
        boolean found = false;
        selenium.select("projectForm_project_experiment_manufacturer", "label=" + provider);

        if (arrayDesignName.isEmpty()) {
            return;
        }
        for (int second = 1;second < 30; second++) {
            values = selenium.getSelectOptions("projectForm_project_experiment_arrayDesigns");
            // - find the array design in the list of values
            for (int i = 0; i < values.length; i++) {
                for (Iterator iter = arrayDesignName.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    if (values[i].equalsIgnoreCase(element)) {
                        selenium.addSelection("projectForm_project_experiment_arrayDesigns", "label=" + element);
                        found = true;
                        break;
                    }

                }
            }

            if (found) {
                return;
            } else {
                pause(1000);
                // - sleep for one second if not found yet
            }
            if (second % 5 == 0) {
                selenium.select("projectForm_project_experiment_manufacturer", "label=--Select a Provider--");
                pause(1000);
                selenium.select("projectForm_project_experiment_manufacturer", "label=" + provider);
                System.out.println("Reselected provider " + provider);
            }

        }
    }

   // selenium.addSelection("projectForm_project_experiment_arrayDesigns", "label=Test3");

    /**
     * Import array design with default assay type (gene expression) provider and organism
     *
     * @param arrayDesignFile
     * @param arrayDesignName
     * @throws Exception
     */
    protected void importArrayDesign(File arrayDesignFile, String arrayDesignName) throws Exception {
        importArrayDesign(arrayDesignFile, arrayDesignName, AFFYMETRIX_PROVIDER, HOMO_SAPIENS_ORGANISM);
    }

    /**
     * Import the array design
     *
     * @param arrayDesignFile - file to be imported
     * @param arrayDesignName - name of array design
     * @param provider - array design provider
     * @param Organism - array design organism
     * @throws Exception
     */
    protected void importArrayDesign(File arrayDesignFile, String arrayDesignName,  String provider, String Organism)
            throws Exception {
selenium.click("link=Manage Array Designs");
waitForText("Array Design Name");
if (!doesArrayDesignExists(arrayDesignName)) {
    addArrayDesign(arrayDesignFile, provider, Organism);
    // the file is uploaded. this may take a while. after it is uploaded
    // we must click on the OKAY box to close the popup.
    selenium.selectWindow(null);
    selenium.waitForPageToLoad("9000");
    // get the array design row so we do not find the wrong Imported text
    int row = getExperimentRow(arrayDesignName, ZERO_COLUMN);
    // wait for array design to be imported
    waitForArrayDesignImport(FORTY_MINUTES, row);
}
}

    private void addArrayDesign(File arrayDesign, String arrayDesignProvider, String arrayDesignOrganism) {
        selenium.click("link=Import a New Array Design");
        waitForPopup("uploadWindow", 3000);
        selenium.selectWindow("uploadWindow");
        waitForText("Array Design Details");
        // assert the Use Case required fields are present
        assertArrayDesignMetaRequiredFields();
        selenium.keyPress("id=assayTypesSearchInput", TAB_KEY);
        
        // select gene expression assay type
        waitForText("Gene Expression");
        selenium.click("//div[@id='assayTypesAutocompleteDiv']/ul/li[3]");
        
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
        selenium.click("link=Next");
        waitForText("New Array Design (Step 2)");
        assertArrayDesignFileRequiredFields();
        selenium.type("upload0", arrayDesign.toString());
        selenium.click("link=Save");
        // we should now be at the
        // all-good page
        waitForText("has been successfully uploaded.", FORTY_MINUTES);
        selenium.click("link=Close Window and go to Manage Array Designs");
        selenium.selectWindow(null);
        waitForText("Provider");
    }

    /**
     * assert the required field for Array Designs conform to the use case
     *
     */
    private void assertArrayDesignMetaRequiredFields() {
        assertTrue(selenium.isTextPresent("Provider*"));
        assertTrue(selenium.isTextPresent("Version Number*"));
        assertTrue(selenium.isTextPresent("Feature Type*"));
        assertTrue(selenium.isTextPresent("Organism*"));
        assertTrue(selenium.isTextPresent("Assay Type*"));
    }

    private void assertArrayDesignFileRequiredFields() {
        assertTrue(selenium.isTextPresent("Browse to File*"));
    }

    protected void findTitleAcrossMultiPages(String text) {
        waitForText("Experiment Title");
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

    protected void lockExperimentFromEdits() {
        waitForText("Lock Experiment from Edits");
        selenium.click("link=Lock Experiment from Edits");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().contains("Are you sure you want to lock this experiment from edits?"));
        // locking an experiement will return the user back to their experiment workspace
        waitForText("Permissions");
    }

    /**
     *
     * @param seconds
     * @param row
     * @return
     * @throws Exception
     */
    protected boolean waitForArrayDesignImport(int seconds, int row) throws Exception {
        pause(2000);
        for (int loop = 1; loop < seconds; loop++) {
            selenium.click("link=Manage Array Designs");
            waitForPageToLoad();
            // done
            String rowText = selenium.getText("//table[id('row')]/tbody/tr[" + row + "]/td[11]");
            if (rowText.equalsIgnoreCase("Import Failed")){
                fail("Array Design import failed");
            }
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
        String tblValue = null;
        for (int row = 1;; row++) {
            tblValue = null;
            try {
                tblValue = selenium.getTable("row." + row + "." + column);
             } catch (RuntimeException e) {
                 if (tblValue == null){
                     // there are no rows on the page
                     return -1;
                 }
                System.out.println("problem looking for " + text + " at (" + row + "," + column + ") Stopped at : " + tblValue);
                throw e;
            }
            if (text.equalsIgnoreCase(tblValue)) {
                return row;
            }

            if (row % PAGE_SIZE == 0) {
                // Moving to next page
                // this will fail once there are no more pages and the text parameter is not found
                try {
                    selenium.click("link=Next");
                } catch (Exception e1) {
                    fail("Did not find " + text);
                }
                waitForDiv("loadingText");
                pause(2000);
                row = 0;
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

    private boolean waitForImport(final int specifiedTimeToWaitInMinutes) {
        int tenMinutesOfSeconds = 600;
        int tenSeconds = 10;
        int oneThousandMilliseconds = 1000;
        int timeToWaitInSeconds = specifiedTimeToWaitInMinutes * 60;
        if (0 >= timeToWaitInSeconds) {
        	timeToWaitInSeconds = tenMinutesOfSeconds;
        }
        boolean completedWithinTimeout = false;
        for (int elapsedTimeInSeconds = 0; elapsedTimeInSeconds <= timeToWaitInSeconds; elapsedTimeInSeconds+=tenSeconds) {
            if (selenium.isTextPresent("Failed Validation")) {
                fail("Validation Failed during Import");
                break;
            }
            else if (selenium.isTextPresent("Import Failed")) {
                fail("Import Failed");
                break;
            }
            selenium.click("link=Manage Data");
            waitForText("File Status Summary");
            if (selenium.isTextPresent("Importing") || selenium.isTextPresent("In Queue")) {
                pause(tenSeconds * oneThousandMilliseconds);
            } else {
                completedWithinTimeout = true;
                break;
            }
        }
        if (!completedWithinTimeout) {
        	fail("Timeout waiting for Import");
        }
        return completedWithinTimeout;
    }

    protected boolean doesArrayDesignExists(String arrayDesignName) {
        return selenium.isTextPresent(arrayDesignName);
    }

    protected void makeExperimentPublic(String experimentId) {
        clickAndWait("link=My Experiment Workspace");
        // - Make the experiment public
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
        selenium.click("//table[@id='row']/tbody/tr[" + row + "]/td[7]/a/img");
        waitForText("Who May Access this Experiment");
        selenium.click("//div/table/tbody/tr[1]/td/a");
        waitForText("Control Access to Specific Content for The Public");
        selenium.select("id=profileForm_accessProfile_securityLevel", "label=Read");
        selenium.click("link=Save");
        waitForText("The permissions have been updated");
        clickAndWait("link=My Experiment Workspace");
    }

    protected void checkFileStatus(String status, int column, int numFiles) {
        int rowCount = 0;

        for (int row = 1; row < numFiles; row++) {
            if (rowCount == numFiles) {
                break;
            }
            rowCount++;
            assertEquals(status, selenium.getTable("row." + row + "." + column));
        }
    }
    // defaults a name for named annotations
    protected void importData(int type) {
        importData(type, DEFAULT_SOURCE_NAME);
    }

    /**
     * Handles the 3 types of file import cases
     *
     * @param type the type of import
     */
    protected void importData(int type, String annotationName) {
        String importButton = "//button[text()='Import']";
        String treeSourceIcon = "//img[@class='x-tree-ec-icon x-tree-elbow-plus']";
        String treeSouceCheckbox = "//*[@class='x-tree-node-cb']";

        selenium.click("selectAllCheckbox");
        selenium.click("link=Import");

        switch (type) {
        case AUTOCREATE_ANNOTATION_SET:
            waitForText("Import Options");
            selenium.click("create_choice_autocreate_per_file");
            selenium.click(importButton);
            break;
        case AUTOCREATE_SINGLE_ANNOTATION:
            waitForText("Import Options");
            selenium.click("create_choice_autocreate_single");
            selenium.type("autocreate_single_annotation_name", annotationName);
            selenium.click(importButton);
            break;
        case ASSOCIATE_TO_EXISTING_BIOMATERIAL:
            waitForText("Import Options");
            selenium.click("create_choice_associate_to_biomaterials");
            assertTrue("No Sources to associate Biomaterial with", selenium.isElementPresent(treeSourceIcon));
            selenium.click(treeSourceIcon);
            pause(1000);
            selenium.click(treeSouceCheckbox);
            selenium.click(importButton);
            break;
        case MAGE_TAB: // mage tab import
            pause(1000); // allow time for the confirmation to popup
            if (selenium.isConfirmationPresent()) {
                assertTrue(selenium
                        .getConfirmation()
                        .matches(
                                "^1 Array Design\\(s\\) cannot not imported\\.  Please use Manage Array Designs\\.\nWould you like to continue importing the remaining 15 file\\(s\\)[\\s\\S]$"));
                // Once the Unpack Archive button is back on the page the import is complete.
                selenium.click("link=Manage Data");
                waitForText("Importing");
            }
            break;
        default:
            // should never get here
            return;
        }

        waitForAction();
        // - hit the refresh button until files are imported
        waitForImport(45);
    }
    /**
     *
     * @param groupName
     */
    protected void createCollaborationGroup(String groupName) {
        selenium.click("link=Manage Collaboration Groups");
        waitForText("Group Members");
        selenium.click("link=Add a New Collaboration Group");
        waitForText("Choose a name for the group.");
        selenium.type("newGroupForm_groupName", groupName);
        selenium.click("link=Save");
        waitForText("Collaboration group with name " + groupName + " has been successfully saved.");
        // - find the added group and click the edit icon
        int row = getExperimentRow(groupName, ZERO_COLUMN);
        // edit icon to add members
        selenium.click("//tr[" + row + "]/td[3]/a/img");
        waitForText("Add a New Group Member");
        selenium.click("link=Add a New Group Member");
        waitForText("Search for users by choosing filter criteria");
        // add all the users
    }
}
