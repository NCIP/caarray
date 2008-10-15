/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This test Software License (the License) is between NCI and You. You (or
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
 * its rights in the test Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and
 * have distributed to and by third parties the test Software and any
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
package gov.nih.nci.caarray.test.api.java.search;

import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * A client searching for experiments using search-by-example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiSearchExperimentByExample extends AbstractApiTest {
    private static final String[] MANUFACTURER_NAMES = {
        "Affymetrix",
        "Illumina"
    };
    private static final String[] ORGANISM_NAMES = {
        "human",
        "black rat"
    };

    @Test
    public void testSearchExperiment() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Searching by Example for Experiments...");
            int i = 0;
            for (String manufacturerName : MANUFACTURER_NAMES) {
                String organismName = ORGANISM_NAMES[i++];
                boolean resultIsOkay = searchExperiments(searchService, manufacturerName, organismName);
                assertTrue("Error: Response did not match request.", resultIsOkay);
            }
        } catch (ServerConnectionException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Server connection exception: " + e + "\nTrace: " + trace);
            assertTrue("Server connection exception: " + e, false);
        } catch (RuntimeException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Runtime exception: " + e + "\nTrace: " + trace);
            assertTrue("Runtime exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private boolean searchExperiments(CaArraySearchService searchService, String manufacturerName, String organismName) {
        Experiment exampleExperiment = createExampleExperiment(manufacturerName, organismName);
        List<Experiment> experimentList = searchService.search(exampleExperiment);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
        boolean resultIsOkay = isResultOkay(experimentList, manufacturerName, organismName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + experimentList.size()
                    + " experiments with array provider " + manufacturerName + " and organism "
                    + organismName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request. Retrieved " + experimentList.size()
                    + " experiments.");
        }
        for (Experiment experiment : experimentList) {
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getTitle(): " + experiment.getTitle());
        }
        return resultIsOkay;
    }

    private Experiment createExampleExperiment(String manufacturerName, String organismName) {
        Experiment exampleExperiment = new Experiment();

        Organization organization = new Organization();
        organization.setName(manufacturerName);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organismName);
        exampleExperiment.setOrganism(organismCriterion);

        return exampleExperiment;
    }

    private boolean isResultOkay(List<Experiment> experimentList, String manufacturerName, String organismName) {
        if (experimentList.isEmpty()) {
            return false;
        }

        Iterator<Experiment> i = experimentList.iterator();
        while (i.hasNext()) {
            Experiment retrievedExperiment = i.next();
            // Check if retrieved experiment matches requested search criteria.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getManufacturer().getName(): "
                    + retrievedExperiment.getManufacturer().getName());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getOrganism().getCommonName(): "
                    + retrievedExperiment.getOrganism().getCommonName());
            if ((!manufacturerName.equals(retrievedExperiment.getManufacturer().getName()))
                    || (!organismName.equals(retrievedExperiment.getOrganism().getCommonName()))) {
                return false;
            }
            // Check if retrieved experiment has mandatory fields.
            if ((retrievedExperiment.getTitle() == null) || (retrievedExperiment.getServiceType() == null)
                    || (retrievedExperiment.getAssayType() == null)) {
                return false;
            }
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getServiceType().getResourceKey(): "
                    + retrievedExperiment.getServiceType().getResourceKey());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getAssayType(): "
                    + retrievedExperiment.getAssayType());
        }
        return true;
    }
}
