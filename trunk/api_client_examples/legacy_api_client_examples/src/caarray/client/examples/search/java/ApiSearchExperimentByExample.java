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
package caarray.client.examples.search.java;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.util.Iterator;
import java.util.List;

import caarray.client.examples.BaseProperties;

/**
 * A client searching for experiments using search-by-example through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiSearchExperimentByExample {
    private static final String PROVIDER_NAME = "Affymetrix";
    private static final String ORGANISM_NAME = "human";

    public static void main(String[] args) {
        ApiSearchExperimentByExample client = new ApiSearchExperimentByExample();
        try {
            // Connect to the server.
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();

            // Get the search service.
            CaArraySearchService searchService = server.getSearchService();

            // Search for experiments.
            System.out.println("Searching for " + ORGANISM_NAME + " experiments run on the " + PROVIDER_NAME
                    + " platform...");
            client.searchExperiments(searchService, PROVIDER_NAME, ORGANISM_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    // Search for experiments based on array provider and organism.
    private void searchExperiments(CaArraySearchService searchService, String providerName, String organismName) {
        // Create an example experiment with the desired array provider and organism.
        Experiment exampleExperiment = createExampleExperiment(providerName, organismName);

        // Call the search service to obtain all experiments similar to the example experiment.
//        List<Experiment> experimentList = searchService.search(exampleExperiment);
//        System.out.println("Retrieved " + experimentList.size() + " experiments.");
//        for (Experiment experiment : experimentList) {
//            System.out.println("Experiment title: " + experiment.getTitle());
//        }
        
        CQLQuery cqlQuery = createCqlQueryArrayDesign("Test3");
        List<Experiment> experimentList = (List<Experiment>) searchService.search(cqlQuery);
        System.out.println("Retrieved " + experimentList.size() + " experiments.");
        for (Experiment experiment : experimentList) {
            System.out.println("Experiment title: " + experiment.getTitle());
        }

        cqlQuery = createCqlQueryTerm("disease_state_design");
        experimentList = (List<Experiment>) searchService.search(cqlQuery);
        System.out.println("Retrieved " + experimentList.size() + " experiments.");
        for (Experiment experiment : experimentList) {
            System.out.println("Experiment title: " + experiment.getTitle());
        }

    }

    // Create an example experiment with the desired array provider and organism.
    private Experiment createExampleExperiment(String providerName, String organismName) {
        Experiment exampleExperiment = new Experiment();

        // Set the array provider.
        Organization organization = new Organization();
        organization.setName(providerName);
        organization.setProvider(true);
        exampleExperiment.setManufacturer(organization);

        // Set the organism.
        Organism organismCriterion = new Organism();
        organismCriterion.setCommonName(organismName);
        exampleExperiment.setOrganism(organismCriterion);

        // Set the assay type.
        // exampleExperiment.setAssayTypeEnum(AssayType.SNP);

        return exampleExperiment;
    }
    
    private CQLQuery createCqlQueryArrayDesign(String arrayDesignName) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association arrayDesignAssociation = new Association();
        arrayDesignAssociation.setName("gov.nih.nci.caarray.domain.array.ArrayDesign");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("name");
        nameAttribute.setValue(arrayDesignName);
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        arrayDesignAssociation.setAttribute(nameAttribute);
        arrayDesignAssociation.setRoleName("arrayDesigns");

        target.setAssociation(arrayDesignAssociation);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private CQLQuery createCqlQueryTerm(String ds) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association arrayDesignAssociation = new Association();
        arrayDesignAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("value");
        nameAttribute.setValue(ds);
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        arrayDesignAssociation.setAttribute(nameAttribute);
        arrayDesignAssociation.setRoleName("experimentDesignTypes");

        target.setAssociation(arrayDesignAssociation);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    // Deserialize the results and retrieve the matching experiments.
    private void parseResults(CQLQueryResults cqlResults) {
        if (cqlResults.getObjectResult() == null) {
            System.out.println("Result was null.");
            return;
        }
        System.out.println("Retrieved " + cqlResults.getObjectResult().length + " experiments.");
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            Experiment retrievedExperiment = (Experiment) (iter.next());
            System.out.println("Experiment title: " + retrievedExperiment.getTitle());
        }
    }
}
