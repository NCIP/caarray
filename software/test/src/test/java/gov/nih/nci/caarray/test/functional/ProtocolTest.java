//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class ProtocolTest extends AbstractSeleniumTest {
    private String protocolName = "GROWTH" + System.currentTimeMillis();


    @Test
    public void testProtocol() throws Exception {
        String title = "_uc1" + System.currentTimeMillis();

        loginAsPrincipalInvestigator();
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        createProtocol(protocolName);
        createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());
        // switch to annotations tab and enter in all the annotations
        selenium.click("link=Annotations");
        waitForTab();
        addExperimentDesign();
        addExperimentFactor();
        addSource();
        addSample();
        addExtract();
        addLabeledExtract();
        addHybridizations();

        // validate the sample count is correct on the My Experiment Workspace page
        selenium.click("link=My Experiment Workspace");
        waitForText("Permissions");
        int row = getExperimentRow(title, FIRST_COLUMN);
        assertEquals(title, selenium.getTable("row." + row + "." + FIRST_COLUMN));
        assertEquals("1", selenium.getTable("row." + row + "." + THIRD_COLUMN));
    }

    private void addHybridizations() {
        String name = "Hybridization";
        doClick(name);
        selenium.keyPress("id=labeledExtractPickerAssociatedValueName", TAB_KEY);
        doSave(name);
    }

    private void addLabeledExtract() {
        String name = "Labeled Extract";
        doClick(name);
        selenium.keyPress("id=extractPickerAssociatedValueName", TAB_KEY);
        selenium.keyPress("id=materialTypeSearchInput", TAB_KEY);
        selenium.select("projectForm_protocolType", "label=acclimatization");
        waitForDiv("progressMsg", 3000);
        selenium.keyPress("id=protocolSearchInput", TAB_KEY);
        doSave(name);
    }

    private void addExtract() {
        String name = "Extract";
        doClick(name);
        selenium.keyPress("id=samplePickerAssociatedValueName", TAB_KEY);
        selenium.keyPress("id=materialTypeSearchInput", TAB_KEY);
        selenium.select("projectForm_protocolType", "label=acclimatization");
        waitForDiv("progressMsg", 3000);
        selenium.keyPress("id=protocolSearchInput", TAB_KEY);
        doSave(name);
    }

    private void addSample() {
        String name = "Sample";
        doClick(name);
        selenium.type("projectForm_currentSample_externalSampleId", "External id for Sample One");
        selenium.keyPress("id=sourcePickerAssociatedValueName", TAB_KEY);
        selenium.keyPress("id=materialTypeSearchInput", TAB_KEY);
        selenium.select("projectForm_protocolType", "label=acclimatization");
        waitForDiv("progressMsg", 3000);
        selenium.keyPress("id=protocolSearchInput", TAB_KEY);
        doSave(name);
    }

    private void addExperimentDesign() {
        selenium.setCursorPosition("id=experimentDesignTypesSearchInput", "0");
        selenium.keyPress("id=experimentDesignTypesSearchInput", TAB_KEY);
        selenium.type("projectForm_project_experiment_designDescription", "Experiment Design Description");
        selenium.keyPress("id=qualityControlTypesSearchInput", TAB_KEY);
        selenium.type("projectForm_project_experiment_qualityControlDescription", "Quality Control Description");
        selenium.keyPress("id=replicateTypesSearchInput", TAB_KEY);
        selenium.type("projectForm_project_experiment_replicateDescription", "Replicate Description");
        doSave("Experiment Design");
    }

    private void addExperimentFactor() {
        String name = "Experimental Factor";
        // doClick(name);
        selenium.click("link=Experimental Factors");
        waitForTab();
        assertTrue("Experiment Factor button is missing", selenium.isTextPresent("Add a new Experimental Factor"));
        selenium.click("link=Add a new Experimental Factor");
        waitForTab();
        selenium.setCursorPosition("id=projectForm_currentFactor_name", "0");
        selenium.type("projectForm_currentFactor_name", "Factor One");
        selenium.type("projectForm_currentFactor_description", "Description for Factor One");
        selenium.select("projectForm_currentFactor_type", "label=age");
        doSave(name);
    }

    private void addSource() {
        String name = "Source";
        doClick(name);
        selenium.keyPress("id=tissueSiteSearchInput", TAB_KEY);
        selenium.keyPress("id=materialTypeSearchInput", TAB_KEY);
        selenium.keyPress("id=cellTypeSearchInput", TAB_KEY);
        selenium.keyPress("id=diseaseStateSearchInput", TAB_KEY);
        doSave(name);
    }

    private void doClick(String name) {
        selenium.click("link=" + name + "s");
        waitForTab();
        assertTrue("Add " + name + " button is missing", selenium.isTextPresent("Add a new " + name));
        selenium.click("link=Add a new " + name);
        waitForTab();
        selenium.type("projectForm_current" + StringUtils.deleteWhitespace(name) + "_name", name + " One");
        selenium.type("projectForm_current" + StringUtils.deleteWhitespace(name) + "_description", "Description for "
                + name);

    }

    private void doSave(String name) {
        selenium.click("link=Save");
        waitForAction();
        waitForText("has been successfully saved");
        assertTrue("Did not receive a save message after saving the " + name + ".", selenium
                .isTextPresent("has been successfully saved"));
    }

    private void createProtocol(String protocolName) throws InterruptedException {
        selenium.click("link=Manage Protocols");
        waitForTab();
        assertTrue("Protocol button is missing", selenium.isTextPresent("Add Protocol"));

        selenium.click("link=Add Protocol");
        waitForTab();
        Thread.sleep(2000);// have to wait for the list of Types to load
        selenium.type("protocolForm_protocol_name", protocolName);
        selenium.type("protocolForm_protocol_description", "Test Protocol");
        selenium.keyPress("id=protocolTypeSearchInput", TAB_KEY);
        selenium.type("protocolForm_protocol_contact", "Science Boy");
        selenium.type("protocolForm_protocol_software", "Software Used");
        selenium.type("protocolForm_protocol_hardware", "hardware used");
        selenium.type("protocolForm_protocol_url", "http://someurl/clickhere.asp");
        selenium.select("protocolForm_protocol_source", "label=caArray 2.0");
        selenium.click("link=Save");
        waitForText("has been successfully saved");
    }

}
