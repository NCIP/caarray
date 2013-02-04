//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileMetadata;
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
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.rmi.RemoteException;
import java.util.List;

/**
 * A client looking up entities using the caArray Java API. This sort of lookup is typically done as a prelude to using
 * one or more of the returned values in further API calls for things like experiment search or file/data download.
 *
 * @author Rashmi Srinivasa
 */
public class LookUpEntities {
    private static SearchService searchService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static long startTime;
    private static long totalTime;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        LookUpEntities entityFinder = new LookUpEntities();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            System.out.println("Looking up various entities by example...");
            entityFinder.lookup();
        } catch (Throwable t) {
            System.out.println("Error while doing lookup.");
            t.printStackTrace();
        }
    }

    private void lookup() throws RemoteException, NoEntityMatchingReferenceException, InvalidInputException {
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

        lookupPersonsByMatchMode();
        lookupExperimentsPageByPage();
    }

    private void lookupArrayDataTypes() throws InvalidInputException {
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

    private void lookupArrayDesigns() throws InvalidInputException {
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

    private void lookupArrayProviders() throws InvalidInputException {
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

    private void lookupAssayTypes() throws InvalidInputException {
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

    private void lookupBiomaterials() throws InvalidInputException {
        ExampleSearchCriteria<Biomaterial> criteria = new ExampleSearchCriteria<Biomaterial>();
        Biomaterial exampleBiomaterial = new Biomaterial();
        criteria.setExample(exampleBiomaterial);
        startTime = System.currentTimeMillis();
//        List<Biomaterial> biomaterials = searchServiceHelper.byExample(criteria).list();
//        totalTime = System.currentTimeMillis() - startTime;
//        System.out.println("Found " + biomaterials.size() + " biomaterials in " + totalTime + " ms.");
        for (Biomaterial biomaterial : searchServiceHelper.byExample(criteria)) {
            System.out.print(biomaterial.getName() + "  ");
        }
        System.out.println("End of biomaterial lookup.");
    }

    private void lookupCategories() throws InvalidInputException {
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

    private void lookupFiles() throws InvalidInputException {
        ExampleSearchCriteria<File> criteria = new ExampleSearchCriteria<File>();
        File exampleFile = new File();
        exampleFile.setMetadata(new FileMetadata());
        exampleFile.getMetadata().setName("aml8_924_hu68_110200_ab.CEL");
        criteria.setExample(exampleFile);
        criteria.setExcludeZeroes(true);
        startTime = System.currentTimeMillis();
        List<File> files = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + files.size() + " files in " + totalTime + " ms.");
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
        }
        System.out.println("End of file lookup.");
    }

    private void lookupExperiments() throws InvalidInputException {
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

    private void lookupExperimentalContacts() throws InvalidInputException {
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

    private void lookupFactors() throws InvalidInputException {
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

    private void lookupFileTypes() throws InvalidInputException {
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

    private void lookupHybridizations() throws InvalidInputException {
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

    private void lookupOrganisms() throws InvalidInputException {
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

    private void lookupPersons() throws InvalidInputException {
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

    private void lookupQuantitationTypes() throws InvalidInputException {
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

    private void lookupTerms() throws InvalidInputException {
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

    private void lookupTermSources() throws InvalidInputException {
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
        List<Person> investigators = searchService.getAllPrincipalInvestigators();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + investigators.size() + " principal investigators in " + totalTime + " ms.");
        for (Person investigator : investigators) {
            System.out.print(investigator.getLastName() + "  ");
        }
        System.out.println("End of principal investigator lookup.");
    }

    private void lookupCharacteristicCategories() throws RemoteException, InvalidInputException {
        CaArrayEntityReference experimentRef = searchForExperiment();
        startTime = System.currentTimeMillis();
        List<Category> categories = searchService.getAllCharacteristicCategories(experimentRef);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + categories.size() + " characteristic categories in " + totalTime + " ms.");
        for (Category category : categories) {
            System.out.print(category.getName() + "  ");
        }
        System.out.println("End of characteristic categories lookup.");
    }

    private void lookupTermsInCategory() throws RemoteException, InvalidInputException {
        CaArrayEntityReference categoryRef = getCategoryReference();
        startTime = System.currentTimeMillis();
        List<Term> terms = searchService.getTermsForCategory(categoryRef, null);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + terms.size() + " terms in the given category in " + totalTime + " ms.");
        for (Term term : terms) {
            System.out.print(term.getValue() + "  ");
        }
        System.out.println("End of terms in category lookup.");
    }

    private void lookupPersonsByMatchMode() throws InvalidInputException {
        ExampleSearchCriteria<Person> criteria = new ExampleSearchCriteria<Person>();
        Person examplePerson = new Person();
        // MatchMode = START
        examplePerson.setLastName("Gan");
        criteria.setExample(examplePerson);
        criteria.setMatchMode(MatchMode.START);
        startTime = System.currentTimeMillis();
        List<Person> persons = searchServiceHelper.byExample(criteria).list();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found " + persons.size() + " persons with last name starting with Gan in " + totalTime
                + " ms.");
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
        System.out
        .println("Found " + persons.size() + " persons with last name ending in ing in " + totalTime + " ms.");
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

    private void lookupExperimentsPageByPage() throws InvalidInputException {
        ExampleSearchCriteria<Experiment> criteria = new ExampleSearchCriteria<Experiment>();
        Experiment exampleExperiment = new Experiment();
        criteria.setExample(exampleExperiment);
        // Get the first (up to) 10 experiments.
        LimitOffset pagingParams = new LimitOffset(10, 0);
        startTime = System.currentTimeMillis();
        SearchResult<Experiment> results = searchService.searchByExample(criteria, pagingParams);
        List<Experiment> experiments = results.getResults();
        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Found the first (up to) 10 experiments in " + totalTime + " ms.");
        for (Experiment experiment : experiments) {
            System.out.print(experiment.getTitle() + "  ");
        }
        System.out.println();

        // Get the next (up to) 10 experiments.
        pagingParams = new LimitOffset(10, 10);
        startTime = System.currentTimeMillis();
        results = searchService.searchByExample(criteria, pagingParams);
        experiments = results.getResults();
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

        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null))
        .getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Multiple experiments with the same name can exist. Here, we're picking the first result.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }

    private CaArrayEntityReference getCategoryReference() throws InvalidInputException {
        String TISSUE_SITE_CATEGORY = "OrganismPart";
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(TISSUE_SITE_CATEGORY);
        criteria.setExample(exampleCategory);
        SearchResult<Category> results = searchService.searchByExample(criteria, null);
        List<Category> categories = results.getResults();
        CaArrayEntityReference categoryRef = categories.get(0).getReference();
        return categoryRef;
    }
}
