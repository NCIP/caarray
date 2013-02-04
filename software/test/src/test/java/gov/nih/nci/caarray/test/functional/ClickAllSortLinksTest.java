//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
