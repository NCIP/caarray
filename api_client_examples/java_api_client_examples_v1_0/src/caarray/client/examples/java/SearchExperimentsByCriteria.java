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
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.rmi.RemoteException;
import java.util.List;

/**
 * A client searching for experiments using various criteria via the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class SearchExperimentsByCriteria {
    private static SearchService searchService = null;
    private static final String PROVIDER_NAME = "Affymetrix";
    private static final String ORGANISM_NAME = "human";
    private static final String TISSUE_SITE_CATEGORY = "OrganismPart";
    private static final String TISSUE_SITE_VALUE = "Brain";
    private static final String PI_NAME = "Golub";
    private static final String ASSAY_TYPE = "geneExpression";

    public static void main(String[] args) {
        SearchExperimentsByCriteria seeker = new SearchExperimentsByCriteria();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            System.out.println("Searching for " + ORGANISM_NAME + " " + TISSUE_SITE_VALUE + " " + PROVIDER_NAME + " "
                    + ASSAY_TYPE + " experiments by " + PI_NAME + "...");
            seeker.search();
        } catch (Throwable t) {
            System.out.println("Error during experiment search by criteria.");
            t.printStackTrace();
        }
    }

    private void search() throws RemoteException, InvalidReferenceException {
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();

        // Select array provider. (See LookUpEntities example client to see how to get list of all array providers.)
        ExampleSearchCriteria<ArrayProvider> providerCriteria = new ExampleSearchCriteria<ArrayProvider>();
        ArrayProvider exampleProvider = new ArrayProvider();
        exampleProvider.setName(PROVIDER_NAME);
        providerCriteria.setExample(exampleProvider);
        List<ArrayProvider> arrayProviders = searchService.searchByExample(providerCriteria, null).getResults();
        if (arrayProviders == null || arrayProviders.size() <= 0) {
            System.err.println("Could not find array provider called " + PROVIDER_NAME);
            return;
        }
        CaArrayEntityReference providerRef = arrayProviders.get(0).getReference();
        experimentSearchCriteria.setArrayProvider(providerRef);

        // Select organism. (See LookUpEntities example client to see how to get list of all organisms.)
        ExampleSearchCriteria<Organism> organismCriteria = new ExampleSearchCriteria<Organism>();
        Organism exampleOrganism = new Organism();
        exampleOrganism.setCommonName(ORGANISM_NAME);
        organismCriteria.setExample(exampleOrganism);
        List<Organism> organisms = searchService.searchByExample(organismCriteria, null).getResults();
        if (organisms == null || organisms.size() <= 0) {
            System.err.println("Could not find organism with common name = " + ORGANISM_NAME);
            return;
        }
        CaArrayEntityReference organismRef = organisms.get(0).getReference();
        experimentSearchCriteria.setOrganism(organismRef);

        // Select assay type. (See LookUpEntities example client to see how to get list of all assay types.)
        ExampleSearchCriteria<AssayType> assayTypeCriteria = new ExampleSearchCriteria<AssayType>();
        AssayType exampleAssayType = new AssayType();
        exampleAssayType.setName(ASSAY_TYPE);
        assayTypeCriteria.setExample(exampleAssayType);
        List<AssayType> assayTypes = searchService.searchByExample(assayTypeCriteria, null).getResults();
        if (assayTypes == null || assayTypes.size() <= 0) {
            System.err.println("Could not find assay type " + ASSAY_TYPE);
            return;
        }
        CaArrayEntityReference assayTypeRef = assayTypes.get(0).getReference();
        experimentSearchCriteria.setAssayType(assayTypeRef);

        // Select principal investigator.
        // Typically, the client application will search for all principal investigators, display the list of
        // PIs to the user and let the user pick one. But in this sample code, we are picking one.
        List<Person> investigators = searchService.getAllPrincipalInvestigators();
        for (Person investigator : investigators) {
            if (PI_NAME.equalsIgnoreCase(investigator.getLastName())) {
                experimentSearchCriteria.setPrincipalInvestigator(investigator.getReference());
                break;
            }
        }

        // Select tissue site.
        // Typically, the client application might search for all annotation values in the tissue site ("OrganismPart")
        // category, display the list of values to the user and let the user pick one. But in this sample code,
        // we are picking one ("Brain").
        AnnotationCriterion tissueSite = new AnnotationCriterion();
        tissueSite.setValue(TISSUE_SITE_VALUE);
        CaArrayEntityReference categoryRef = getCategoryReference(TISSUE_SITE_CATEGORY);
        tissueSite.setCategory(categoryRef);
        experimentSearchCriteria.getAnnotationCriterions().add(tissueSite);

        // Search for experiments that satisfy all of the above criteria.
        long startTime = System.currentTimeMillis();
        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null)).getResults();
        long totalTime = System.currentTimeMillis() - startTime;
        if (experiments == null || experiments.size() <= 0) {
            System.out.println("No experiments found matching the requested criteria.");
        } else {
            System.out.println("Retrieved " + experiments.size() + " experiments in " + totalTime + " ms.");
        }
    }

    private CaArrayEntityReference getCategoryReference(String categoryName) {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        List<Category> categories = searchService.searchByExample(criteria, null).getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
