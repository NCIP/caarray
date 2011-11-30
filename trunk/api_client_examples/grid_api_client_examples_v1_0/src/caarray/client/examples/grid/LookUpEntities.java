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
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;

/**
 * A client looking up entities using the caArray Grid service API.
 * This sort of lookup is typically done as a prelude to using one or more of the returned values
 * in further API calls for things like experiment search or file/data download.
 *
 * @author Rashmi Srinivasa
 */
public class LookUpEntities {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static long startTime;
    private static long totalTime;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        LookUpEntities entityFinder = new LookUpEntities();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Looking up various entities by example...");
            entityFinder.lookup();
        } catch (Throwable t) {
            System.out.println("Error while doing lookup.");
            t.printStackTrace();
        }
    }

    private void lookup() throws RemoteException, DeserializationException, InvalidInputException {
        lookupArrayDataTypes();
        lookupArrayDesigns();
        lookupArrayProviders();
        lookupAssayTypes();
        lookupBiomaterials();
        lookupCategories();
        lookupFiles();
        lookupExperiments();
        lookupExperimentalContacts();
        lookupFactors();
        lookupFileTypes();
        lookupHybridizations();
        lookupOrganisms();
        lookupPersons();
        lookupQuantitationTypes();
        lookupTerms();
        lookupTermSources();
        lookupPrincipalInvestigators();
        lookupCharacteristicCategories();
        lookupTermsInCategory();
        enumerateExperiments();

        lookupPersonsByMatchMode();
        lookupExperimentsPageByPage();
    }

    private void lookupArrayDataTypes() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<ArrayDataType> criteria = new ExampleSearchCriteria<ArrayDataType>();
        ArrayDataType exampleArrayDataType = new ArrayDataType();
        criteria.setExample(exampleArrayDataType);
        startTime = System.currentTimeMillis();
        List<ArrayDataType> arrayDataTypes = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + arrayDataTypes.size() + " array data types in " + totalTime + " ms.");
        for (ArrayDataType arrayDataType : arrayDataTypes) {
            System.out.print(arrayDataType.getName() + "  ");
        }
        System.out.println("End of array data type lookup.");
    }

    private void lookupArrayDesigns() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<ArrayDesign> criteria = new ExampleSearchCriteria<ArrayDesign>();
        ArrayDesign exampleDesign = new ArrayDesign();
        criteria.setExample(exampleDesign);
        startTime = System.currentTimeMillis();
        List<ArrayDesign> arrayDesigns = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + arrayDesigns.size() + " array designs in " + totalTime + " ms.");
        for (ArrayDesign arrayDesign : arrayDesigns) {
            System.out.print(arrayDesign.getName() + "  ");
        }
        System.out.println("End of array design lookup.");
    }

    private void lookupArrayProviders() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<ArrayProvider> criteria = new ExampleSearchCriteria<ArrayProvider>();
        ArrayProvider exampleProvider = new ArrayProvider();
        criteria.setExample(exampleProvider);
        startTime = System.currentTimeMillis();
        List<ArrayProvider> arrayProviders = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + arrayProviders.size() + " array providers in " + totalTime + " ms.");
        for (ArrayProvider arrayProvider : arrayProviders) {
            System.out.print(arrayProvider.getName() + "  ");
        }
        System.out.println("End of array provider lookup.");
    }

    private void lookupAssayTypes() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<AssayType> criteria = new ExampleSearchCriteria<AssayType>();
        AssayType exampleAssayType = new AssayType();
        criteria.setExample(exampleAssayType);
        startTime = System.currentTimeMillis();
        List<AssayType> assayTypes = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + assayTypes.size() + " assay types in " + totalTime + " ms.");
        for (AssayType assayType : assayTypes) {
            System.out.print(assayType.getName() + "  ");
        }
        System.out.println("End of assay type lookup.");
    }

    private void lookupBiomaterials() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Biomaterial> criteria = new ExampleSearchCriteria<Biomaterial>();
        Biomaterial exampleBiomaterial = new Biomaterial();
        criteria.setExample(exampleBiomaterial);
        startTime = System.currentTimeMillis();
        List<Biomaterial> biomaterials = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + biomaterials.size() + " biomaterials in " + totalTime + " ms.");
        for (Biomaterial biomaterial : biomaterials) {
            System.out.print(biomaterial.getName() + "  ");
        }
        System.out.println("End of biomaterial lookup.");
    }

    private void lookupCategories() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        criteria.setExample(exampleCategory);
        startTime = System.currentTimeMillis();
        List<Category> categories = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + categories.size() + " categories in " + totalTime + " ms.");
        for (Category category : categories) {
            System.out.print(category.getName() + "  ");
        }
        System.out.println("End of category lookup.");
    }

    private void lookupFiles() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<File> criteria = new ExampleSearchCriteria<File>();
        File exampleFile = new File();
        criteria.setExample(exampleFile);
        startTime = System.currentTimeMillis();
        List<File> files = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + files.size() + " files in " + totalTime + " ms.");
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
        }
        System.out.println("End of file lookup.");
    }

    private void lookupExperiments() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Experiment> criteria = new ExampleSearchCriteria<Experiment>();
        Experiment exampleExperiment = new Experiment();
        criteria.setExample(exampleExperiment);
        startTime = System.currentTimeMillis();
        List<Experiment> experiments = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + experiments.size() + " experiments in " + totalTime + " ms.");
        for (Experiment experiment : experiments) {
            System.out.print(experiment.getTitle() + "  ");
        }
        System.out.println("End of experiment lookup.");
    }

    private void lookupExperimentalContacts() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<ExperimentalContact> criteria = new ExampleSearchCriteria<ExperimentalContact>();
        ExperimentalContact exampleContact = new ExperimentalContact();
        criteria.setExample(exampleContact);
        startTime = System.currentTimeMillis();
        List<ExperimentalContact> contacts = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + contacts.size() + " experimental contacts in " + totalTime + " ms.");
        for (ExperimentalContact contact : contacts) {
            System.out.print(contact.getPerson().getLastName() + "  ");
        }
        System.out.println("End of experimental contact lookup.");
    }

    private void lookupFactors() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Factor> criteria = new ExampleSearchCriteria<Factor>();
        Factor exampleFactor = new Factor();
        criteria.setExample(exampleFactor);
        startTime = System.currentTimeMillis();
        List<Factor> factors = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + factors.size() + " factors in " + totalTime + " ms.");
        for (Factor factor : factors) {
            System.out.print(factor.getName() + "  ");
        }
        System.out.println("End of factor lookup.");
    }

    private void lookupFileTypes() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        criteria.setExample(exampleFileType);
        startTime = System.currentTimeMillis();
        List<FileType> fileTypes = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + fileTypes.size() + " file types in " + totalTime + " ms.");
        for (FileType fileType : fileTypes) {
            System.out.print(fileType.getName() + "  ");
        }
        System.out.println("End of file type lookup.");
    }

    private void lookupHybridizations() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Hybridization> criteria = new ExampleSearchCriteria<Hybridization>();
        Hybridization exampleHybridization = new Hybridization();
        criteria.setExample(exampleHybridization);
        startTime = System.currentTimeMillis();
        List<Hybridization> hybridizations = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + hybridizations.size() + " hybridizations in " + totalTime + " ms.");
        for (Hybridization hybridization : hybridizations) {
            System.out.print(hybridization.getName() + "  ");
        }
        System.out.println("End of hybridization lookup.");
    }

    private void lookupOrganisms() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Organism> criteria = new ExampleSearchCriteria<Organism>();
        Organism exampleOrganism = new Organism();
        criteria.setExample(exampleOrganism);
        startTime = System.currentTimeMillis();
        List<Organism> organisms = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + organisms.size() + " organisms in " + totalTime + " ms.");
        for (Organism organism : organisms) {
            System.out.print(organism.getScientificName() + "  ");
        }
        System.out.println("End of organism lookup.");
    }

    private void lookupPersons() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Person> criteria = new ExampleSearchCriteria<Person>();
        Person examplePerson = new Person();
        criteria.setExample(examplePerson);
        startTime = System.currentTimeMillis();
        List<Person> persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println("End of person lookup.");
    }

    private void lookupQuantitationTypes() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<QuantitationType> criteria = new ExampleSearchCriteria<QuantitationType>();
        QuantitationType exampleQuantitationType = new QuantitationType();
        exampleQuantitationType.setDataType(DataType.FLOAT);
        criteria.setExample(exampleQuantitationType);
        startTime = System.currentTimeMillis();
        List<QuantitationType> qtypes = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + qtypes.size() + " quantitation types in " + totalTime + " ms.");
        for (QuantitationType qtype : qtypes) {
            System.out.println(qtype);
        }
        System.out.println("End of quantitation type lookup.");
    }

    private void lookupTerms() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Term> criteria = new ExampleSearchCriteria<Term>();
        Term exampleTerm = new Term();
        criteria.setExample(exampleTerm);
        startTime = System.currentTimeMillis();
        List<Term> terms = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + terms.size() + " terms in " + totalTime + " ms.");
        for (Term term : terms) {
            System.out.print(term.getValue() + "  ");
        }
        System.out.println("End of term lookup.");
    }

    private void lookupTermSources() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<TermSource> criteria = new ExampleSearchCriteria<TermSource>();
        TermSource exampleTermSource = new TermSource();
        criteria.setExample(exampleTermSource);
        startTime = System.currentTimeMillis();
        List<TermSource> termSources = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + termSources.size() + " term sources in " + totalTime + " ms.");
        for (TermSource termSource : termSources) {
            System.out.print(termSource.getName() + "  ");
        }
        System.out.println("End of term source lookup.");
    }

    private void lookupPrincipalInvestigators() throws RemoteException {
        startTime = System.currentTimeMillis();
        Person[] investigators = client.getAllPrincipalInvestigators();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + investigators.length + " principal investigators in " + totalTime + " ms.");
        for (Person investigator : investigators) {
            System.out.print(investigator.getLastName() + "  ");
        }
        System.out.println("End of principal investigator lookup.");
    }

    private void lookupCharacteristicCategories() throws RemoteException, InvalidInputException {
        CaArrayEntityReference experimentRef = searchForExperiment();
        startTime = System.currentTimeMillis();
        Category[] categories = client.getAllCharacteristicCategories(experimentRef);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + categories.length + " characteristic categories in " + totalTime + " ms.");
        for (Category category : categories) {
            System.out.print(category.getName() + "  ");
        }
        System.out.println("End of characteristic categories lookup.");
    }

    private void lookupTermsInCategory() throws RemoteException, InvalidInputException {
        CaArrayEntityReference categoryRef = getCategoryReference();
        startTime = System.currentTimeMillis();
        Term[] terms = client.getTermsForCategory(categoryRef, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + terms.length + " terms in the given category in " + totalTime + " ms.");
        for (Term term : terms) {
            System.out.print(term.getValue() + "  ");
        }
        System.out.println("End of terms in category lookup.");
    }

    private void enumerateExperiments() throws RemoteException, DeserializationException {
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        startTime = System.currentTimeMillis();
        EnumerationResponseContainer expEnum = client.enumerateExperiments(experimentSearchCriteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(expEnum, CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        IterationConstraints ic = new IterationConstraints(5, -1, null);
        iter.setIterationConstraints(ic);
        int numExperimentsFound = 0;
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Experiment experiment = (Experiment) ObjectDeserializer.toObject(elem, Experiment.class);
                    System.out.print(experiment.getTitle() + "  ");
                    numExperimentsFound ++;
                }
            } catch (NoSuchElementException e) {
                break;
            }
        }
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("End of experiment enumeration.");
        System.out.println("Found " + numExperimentsFound + " experiments in " + totalTime + " ms.");
    }

    private void lookupPersonsByMatchMode() throws RemoteException, InvalidInputException {
        ExampleSearchCriteria<Person> criteria = new ExampleSearchCriteria<Person>();
        Person examplePerson = new Person();
        // MatchMode = START
        examplePerson.setLastName("Gan");
        criteria.setExample(examplePerson);
        criteria.setMatchMode(MatchMode.START);
        startTime = System.currentTimeMillis();
        List<Person> persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons with last name starting with Gan in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println();

        // MatchMode = END
        examplePerson.setLastName("ing");
        criteria.setMatchMode(MatchMode.END);
        startTime = System.currentTimeMillis();
        persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons with last name ending in ing in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println();

        // MatchMode = EXACT
        examplePerson.setLastName("Gandhi");
        criteria.setMatchMode(MatchMode.EXACT);
        startTime = System.currentTimeMillis();
        persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons with last name Gandhi in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println();

        // MatchMode = ANYWHERE
        examplePerson.setLastName("n");
        criteria.setMatchMode(MatchMode.ANYWHERE);
        startTime = System.currentTimeMillis();
        persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons with last name containing n in " + totalTime + " ms.");
        for (Person person : persons) {
            System.out.print(person.getLastName() + "  ");
        }
        System.out.println("End of person lookup.");
    }

    private void lookupExperimentsPageByPage() throws RemoteException {
        ExampleSearchCriteria<Experiment> criteria = new ExampleSearchCriteria<Experiment>();
        Experiment exampleExperiment = new Experiment();
        criteria.setExample(exampleExperiment);
        // Get the first (up to) 10 experiments.
        LimitOffset pagingParams = new LimitOffset(10, 0);
        startTime = System.currentTimeMillis();
        List<Experiment> experiments = (client.searchByExample(criteria, pagingParams)).getResults();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found the first (up to) 10 experiments in " + totalTime + " ms.");
        for (Experiment experiment : experiments) {
            System.out.print(experiment.getTitle() + "  ");
        }
        System.out.println();

        // Get the next (up to) 10 experiments.
        pagingParams = new LimitOffset(10, 10);
        startTime = System.currentTimeMillis();
        experiments = (client.searchByExample(criteria, pagingParams)).getResults();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found the next (up to) 10 experiments in " + totalTime + " ms.");
        for (Experiment experiment : experiments) {
            System.out.print(experiment.getTitle() + "  ");
        }
        System.out.println("End of experiment lookup.");
    }

    /**
     * Search for an experiment based on its title.
     */
    private CaArrayEntityReference searchForExperiment() throws RemoteException, InvalidInputException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        List<Experiment> experiments = client.searchForExperiments(experimentSearchCriteria, null).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Multiple experiments with the same name can exist. Here, we're picking the first result.
        Experiment experiment = experiments.get(0);
        return experiment.getReference();
    }

    private CaArrayEntityReference getCategoryReference() throws RemoteException {
        String TISSUE_SITE_CATEGORY = "OrganismPart";
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(TISSUE_SITE_CATEGORY);
        criteria.setExample(exampleCategory);
        List<Category> categories = (client.searchByExample(criteria, null)).getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
