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

import org.junit.Test;

/**
 * 
 * Use Case UC#7231. Test Case #10320, #10321, #10324, #10325 Requirements: Browse by Experiments, Organism, Array
 * Providers, and Unique Array Designs
 * 
 */
public class PermissionsTest extends AbstractSeleniumTest {
    private final String ADD_USER_BUTTON = "//img[@alt='Add']";
    private final String FILTER_BUTON = "//li[2]/a/span/span";
    private final String EDIT_ACCESS_CONTROL_BUTTON = "//form[@id='collaborators_form']/a/span/span";
    private final String SAVE_BUTTON = "//ul[@id='btnrow']/li[2]/a/span/span";
    @Test
    public void testPermissions() throws Exception {
        String groupName = "fellows " + System.currentTimeMillis();
        String experimentName = "Permission Test"+ System.currentTimeMillis();
        
        // - Login and create an experiment
        loginAsPrincipalInvestigator();
        String experimentId = createExperiment(experimentName);
        
        logout();
        loginAs("caarrayuser");

        // makes sure the experiment cannot be viewed by other users
        int row = getExperimentRow(experimentId, ZERO_COLUMN);
       // String value = selenium.is
        assertTrue("Experiment " + experimentId +" was found",row == -1);
        
        logout();
        // log back in as PI and submit the experiment and set permissions
        loginAsPrincipalInvestigator();
        
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // click the edit icon
        selenium.click("//table[@id='row']/tbody/tr[" + row + "]/td[9]/a/img");
        waitForText("Overall Experiment Characteristics");
        lockExperimentFromEdits();
        
        createCollaborationGroup(groupName);
        addUsers();
        
        // set the new group on the experiment
        selenium.click("link=My Experiment Workspace");
        waitForText("Assay Type");
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        // click the permission icon
        selenium.click("//table[@id='row']/tbody/tr["+row+"]/td[7]/a/img");
        waitForText("Who May Access this Experiment");
        // select the collaboration group for the experiment
        selenium.select("collaborators_form_collaboratorGroup_id", "label=" + groupName);
        
        // set the permission
        selenium.click(EDIT_ACCESS_CONTROL_BUTTON);
        waitForText("Read/Write Selective");
        
        // Experiment Access ddw
        selenium.select("profileForm_accessProfile_securityLevel", "label=Read"); 
        selenium.click(SAVE_BUTTON);
        waitForText("The permissions have been updated");
    
        logout();
        loginAs("caarrayuser");
        
        // makes sure the experiment can be viewed by other users
        row = getExperimentRow(experimentId, ZERO_COLUMN);
        
        // experiment was found
        assertTrue("Experiment " + experimentId +" was found",row != -1);
        
        // the permission is read only so ensure the edit icon is not avaliable
        assertFalse(selenium.isElementPresent("//table[@id='row']/tbody/tr[" + row + "]/td[9]/a/img"));
        logout();
    }


    private void logout() {
        selenium.click("link=Logout");
        waitForText("Browse caArray");    }


    private void addUsers() {
        selenium.type("targetUserLastName", "user");
        selenium.type("targetUserFirstName", "caarray");
        clickAndWait(FILTER_BUTON);
        clickAndWait(ADD_USER_BUTTON);
        waitForText("caArray User (caarrayuser) successfully added to group");
    }

}
