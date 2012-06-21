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
        waitForText("Experiment Design Description");
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
        waitForText("Selected Labeled Extracts");
        selenium.type("projectForm_currentHybridization_name", "Test Hybridization Name");
        selenium.type("projectForm_currentHybridization_description", "Test Hybridization Description");
        selenium.click("//div[@id='labeledExtractPickerAutocompleteDiv']/ul/li[1]");
        doSave(name);
    }

    private void addLabeledExtract() {
        String name = "Labeled Extract";
        doClick(name);
        waitForText("Selected Material Type");
        selenium.type("projectForm_currentLabeledExtract_name", "Test Labelled Extract Name");
        selenium.type("projectForm_currentLabeledExtract_description", "Test Labelled Extract Description");
        selenium.type("projectForm_currentLabeledExtract_externalId", "Test Labelled Extract External ID");
        selenium.click("//div[@id='extractPickerAutocompleteDiv']/ul/li[1]");
        doSave(name);
    }

    private void addExtract() {
        String name = "Extract";
        doClick(name);
        waitForText("Selected Material Type");
        selenium.type("projectForm_currentExtract_name", "Test Extract Name");
        selenium.type("projectForm_currentExtract_description", "Test Extract Name");
        selenium.click("//div[@id='samplePickerAutocompleteDiv']/ul/li[1]");
        doSave(name);
    }

    private void addSample() {
        String name = "Sample";
        doClick(name);
        waitForText("External ID");
        selenium.type("projectForm_currentSample_name", "Sample name for Sample One");
        selenium.type("projectForm_currentSample_description", "Sample description for Sample One");
        selenium.click("//div[@id='sourcePickerAutocompleteDiv']/ul/li[1]");
        selenium.type("projectForm_currentSample_externalId", "External id for Sample One");
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
        selenium.click("//div[@id='experimentDesignTypesAutocompleteDiv']/ul/li[1]");
        selenium.type("projectForm_project_experiment_designDescription", "Experiment Design Description");
        selenium.keyPress("id=qualityControlTypesSearchInput", TAB_KEY);
        selenium.type("projectForm_project_experiment_qualityControlDescription", "Quality Control Description");
        selenium.keyPress("id=replicateTypesSearchInput", TAB_KEY);
        selenium.type("projectForm_project_experiment_replicateDescription", "Replicate Description");
        doSave("Experiment Design");
    }

    private void addExperimentFactor() {
        String name = "Experimental Factor";
        selenium.click("link=Experimental Factors");
        waitForText("Add a new Experimental Factor");
        assertTrue("Experiment Factor button is missing", selenium.isTextPresent("Add a new Experimental Factor"));
        selenium.click("link=Add a new Experimental Factor");
        waitForText("Factor Name");
        selenium.type("projectForm_currentFactor_name", "Factor One");
        selenium.type("projectForm_currentFactor_description", "Description for Factor One");
        selenium.select("projectForm_currentFactor_type", "label=age");
        doSave(name);
    }

    private void addSource() {
        String name = "Test Source";
        selenium.click("link=Annotations");
        waitForText("Experiment Design Description");
        selenium.click("link=Sources");
        waitForText("Add a new Source");
        selenium.click("link=Add a new Source");
        waitForText("Brain");
        selenium.click("//div[@id='tissueSiteAutocompleteDiv']/ul/li[1]");
        selenium.type("projectForm_currentSource_name", name);
        doSave(name);
        waitForText("Source created successfully");
    }

    private void doClick(String name) {
        selenium.click("link=" + name + "s");
        waitForText("Add a new " + name);
        assertTrue("Add " + name + " button is missing", selenium.isTextPresent("Add a new " + name));
        selenium.click("link=Add a new " + name);
        waitForTab();
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
        waitForText("Add Protocol");
        assertTrue("Protocol button is missing", selenium.isTextPresent("Add Protocol"));

        selenium.click("link=Add Protocol");
        waitForTab();
        Thread.sleep(2000);// have to wait for the list of Types to load
        selenium.type("protocolForm_protocol_name", protocolName);
        selenium.type("protocolForm_protocol_description", "Test Protocol");
        selenium.keyPress("id=protocolTypeSearchInput", TAB_KEY);
        selenium.click("//div[@id='protocolTypeAutocompleteDiv']/ul/li[54]");
        selenium.type("protocolForm_protocol_contact", "Science Boy");
        selenium.type("protocolForm_protocol_software", "Software Used");
        selenium.type("protocolForm_protocol_hardware", "hardware used");
        selenium.type("protocolForm_protocol_url", "http://someurl/clickhere.asp");
        selenium.select("protocolForm_protocol_source", "label=caArray 2.0");
        selenium.click("link=Save");
        waitForText("has been successfully saved");
    }

}
