/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarrayGridClientExamples_v1_0
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarrayGridClientExamples_v1_0 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarrayGridClientExamples_v1_0 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarrayGridClientExamples_v1_0 Software; (ii) distribute and
 * have distributed to and by third parties the caarrayGridClientExamples_v1_0 Software and any
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

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * A client looking up entities using the caArray Java API. This sort of lookup is typically done as a prelude to using
 * one or more of the returned values in further API calls for things like experiment search or file/data download.
 *
 * @author Rashmi Srinivasa
 */
public class LookUpEntities {
    private static SearchService searchService = null;
    private static long startTime;
    private static long totalTime;

    public static void main(String[] args) {
        LookUpEntities entityFinder = new LookUpEntities();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            System.out.println("Looking up various entities by example...");
            entityFinder.lookup();
        } catch (Throwable t) {
            System.out.println("Error while doing lookup.");
            t.printStackTrace();
        }
    }

    private void lookup() throws RemoteException, NoEntityMatchingReferenceException {
        lookupArrayDataTypes();
        lookupArrayDesigns();
        lookupBiomaterials();
        lookupCategories();
        lookupFiles();
        lookupExperiments();
        lookupExperimentalContacts();
        lookupFactors();
        lookupFileTypes();
        lookupHybridizations();
        lookupPersons();
        lookupQuantitationTypes();
        lookupTerms();
        lookupTermSources();
        lookupPrincipalInvestigators();
        lookupEntityByReference();
        lookupEntitiesByReference();
        // lookupAnnotationCategories();
        // lookupAnnotationValues(category);
    }

    private void lookupArrayDataTypes() {
        ArrayDataType exampleArrayDataType = new ArrayDataType();
        startTime = System.currentTimeMillis();
        List<ArrayDataType> arrayDataTypes = searchService.searchByExample(exampleArrayDataType, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + arrayDataTypes.size() + " array data types in " + totalTime + " ms.");
        for (ArrayDataType arrayDataType : arrayDataTypes) {
            System.out.print(arrayDataType.getName() + "  ");
        }
        System.out.println("End of array data type lookup.");
    }

    private void lookupArrayDesigns() {
        ArrayDesign exampleDesign = new ArrayDesign();
        startTime = System.currentTimeMillis();
        List<ArrayDesign> arrayDesigns = searchService.searchByExample(exampleDesign, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + arrayDesigns.size() + " array designs in " + totalTime + " ms.");
        for (ArrayDesign arrayDesign : arrayDesigns) {
            System.out.print(arrayDesign.getName() + "  ");
        }
        System.out.println("End of array design lookup.");
    }

    private void lookupBiomaterials() {
        Biomaterial exampleBiomaterial = new Biomaterial();
        startTime = System.currentTimeMillis();
        List<Biomaterial> biomaterials = searchService.searchByExample(exampleBiomaterial, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + biomaterials.size() + " biomaterials in " + totalTime + " ms.");
        for (Biomaterial biomaterial : biomaterials) {
            System.out.print(biomaterial.getName() + "  ");
        }
        System.out.println("End of biomaterial lookup.");
    }

    private void lookupCategories() {
        Category exampleCategory = new Category();
        startTime = System.currentTimeMillis();
        List<Category> categories = searchService.searchByExample(exampleCategory, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + categories.size() + " categories in " + totalTime + " ms.");
        for (Category category : categories) {
            System.out.print(category.getName() + "  ");
        }
        System.out.println("End of category lookup.");
    }

    private void lookupFiles() {
        DataFile exampleFile = new DataFile();
        startTime = System.currentTimeMillis();
        List<DataFile> files = searchService.searchByExample(exampleFile, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + files.size() + " files in " + totalTime + " ms.");
        for (DataFile file : files) {
            System.out.print(file.getName() + "  ");
        }
        System.out.println("End of file lookup.");
    }

    private void lookupExperiments() {
        Experiment exampleExperiment = new Experiment();
        startTime = System.currentTimeMillis();
        List<Experiment> experiments = searchService.searchByExample(exampleExperiment, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + experiments.size() + " experiments in " + totalTime + " ms.");
        for (Experiment experiment : experiments) {
            System.out.print(experiment.getTitle() + "  ");
        }
        System.out.println("End of experiment lookup.");
    }

    private void lookupExperimentalContacts() {
        ExperimentalContact exampleContact = new ExperimentalContact();
        startTime = System.currentTimeMillis();
        List<ExperimentalContact> contacts = searchService.searchByExample(exampleContact, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + contacts.size() + " experimental contacts in " + totalTime + " ms.");
        for (ExperimentalContact contact : contacts) {
            System.out.print(contact.getPerson().getLastName() + "  ");
        }
        System.out.println("End of experimental contact lookup.");
    }

    private void lookupFactors() {
        Factor exampleFactor = new Factor();
        startTime = System.currentTimeMillis();
        List<Factor> factors = searchService.searchByExample(exampleFactor, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + factors.size() + " factors in " + totalTime + " ms.");
        for (Factor factor : factors) {
            System.out.print(factor.getName() + "  ");
        }
        System.out.println("End of factor lookup.");
    }

    private void lookupFileTypes() {
        FileType exampleFileType = new FileType();
        startTime = System.currentTimeMillis();
        List<FileType> fileTypes = searchService.searchByExample(exampleFileType, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + fileTypes.size() + " file types in " + totalTime + " ms.");
        for (FileType fileType : fileTypes) {
            System.out.print(fileType.getName() + "  ");
        }
        System.out.println("End of file type lookup.");
    }

    private void lookupHybridizations() {
        Hybridization exampleHybridization = new Hybridization();
        startTime = System.currentTimeMillis();
        List<Hybridization> hybridizations = searchService.searchByExample(exampleHybridization, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + hybridizations.size() + " hybridizations in " + totalTime + " ms.");
        for (Hybridization hybridization : hybridizations) {
            System.out.print(hybridization.getName() + "  ");
        }
        System.out.println("End of hybridization lookup.");
    }

    private void lookupPersons() {
        Person examplePerson = new Person();
        startTime = System.currentTimeMillis();
        List<Person> persons = searchService.searchByExample(examplePerson, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println("End of person lookup.");
    }

    private void lookupQuantitationTypes() {
        QuantitationType exampleQuantitationType = new QuantitationType();
        startTime = System.currentTimeMillis();
        List<QuantitationType> qtypes = searchService.searchByExample(exampleQuantitationType, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + qtypes.size() + " quantitation types in " + totalTime + " ms.");
        for (QuantitationType qtype : qtypes) {
            System.out.print(qtype.getName() + "  ");
        }
        System.out.println("End of quantitation type lookup.");
    }

    private void lookupTerms() {
        Term exampleTerm = new Term();
        startTime = System.currentTimeMillis();
        List<Term> terms = searchService.searchByExample(exampleTerm, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + terms.size() + " terms in " + totalTime + " ms.");
        for (Term term : terms) {
            System.out.print(term.getValue() + "  ");
        }
        System.out.println("End of term lookup.");
    }

    private void lookupTermSources() {
        TermSource exampleTermSource = new TermSource();
        startTime = System.currentTimeMillis();
        List<TermSource> termSources = searchService.searchByExample(exampleTermSource, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + termSources.size() + " term sources in " + totalTime + " ms.");
        for (TermSource termSource : termSources) {
            System.out.print(termSource.getName() + "  ");
        }
        System.out.println("End of term source lookup.");
    }

    private void lookupPrincipalInvestigators() throws RemoteException {
        startTime = System.currentTimeMillis();
        List<Person> investigators = searchService.getAllPrincipalInvestigators(null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + investigators.size() + " principal investigators in " + totalTime + " ms.");
        for (Person investigator : investigators) {
            System.out.print(investigator.getLastName() + "  ");
        }
        System.out.println("End of principal investigator lookup.");
    }

    private void lookupEntityByReference() throws RemoteException, NoEntityMatchingReferenceException {
        CaArrayEntityReference organismRef = new CaArrayEntityReference(
                "URN:LSID:gov.nih.nci.caarray.external.v1_0.experiment.Organism:5");
        startTime = System.currentTimeMillis();
        Organism organism = (Organism) searchService.getByReference(organismRef);
        totalTime = System.currentTimeMillis() - startTime;
        if (organism == null) {
            System.out.println("Could not find organism.");
        } else {
            System.out.println("Found organism " + organism.getScientificName() + " in " + totalTime + " ms.");
        }
    }

    private void lookupEntitiesByReference() throws RemoteException, NoEntityMatchingReferenceException {
        List<CaArrayEntityReference> organismRefs = new ArrayList<CaArrayEntityReference>();
        organismRefs
                .add(new CaArrayEntityReference("URN:LSID:gov.nih.nci.caarray.external.v1_0.experiment.Organism:5"));
        organismRefs
                .add(new CaArrayEntityReference("URN:LSID:gov.nih.nci.caarray.external.v1_0.experiment.Organism:3"));
        startTime = System.currentTimeMillis();
        List<AbstractCaArrayEntity> organisms = searchService.getByReferences(organismRefs);
        totalTime = System.currentTimeMillis() - startTime;
        if (organisms == null || organisms.size() <= 0) {
            System.out.println("Could not find organisms.");
        } else {
            for (AbstractCaArrayEntity entity : organisms) {
                Organism organism = (Organism) entity;
                System.out.println("Found organism: " + organism.getScientificName());
            }
            System.out.println("Found " + organisms.size() + " organisms in " + totalTime + " ms.");
        }
    }
}
