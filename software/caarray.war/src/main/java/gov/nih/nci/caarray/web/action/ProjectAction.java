package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.PaymentMechanism;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.SecurityInterceptor;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * ProjectAction.
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 1L;

    public static final String LIST_RESULT = "list";
    public static final String WORKSPACE_RESULT = "workspace";
    public static final String DETAILS_RESULT = "details";

    private static final String SAVE_MODE_DRAFT = "save_draft";
    private static final String SAVE_MODE_SUBMIT = "save_submit";

    private String menu = null;
    private Project project = Project.createNew();

    private List<Project> projects = new ArrayList<Project>();
    private List<Organism> organisms = new ArrayList<Organism>();
    private List<Organization> manufacturers = new ArrayList<Organization>();
    private List<PaymentMechanism> paymentMechanisms = new ArrayList<PaymentMechanism>();
    private Map<Organization, List<ArrayDesign>> arrayDesignsByManufacturer;
    private List<Term> tissueSites;
    private List<Term> tissueTypes;
    private List<Term> cellTypes;
    private List<Term> conditions;
    private List<Term> experimentDesignTypes;
    private List<Term> qualityControlTypes;
    private List<Term> replicateTypes;

    private User user;
    private String saveMode;
    private boolean initialSave;

    private boolean piIsMainPoc;
    private Person primaryInvestigator;
    private Person mainPointOfContact;

    private Source currentSource = new Source();
    private Sample currentSample = new Sample();
    private Factor currentFactor = new Factor();

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        if (this.project.getId() != null) {
            this.project = getDelegate().getProjectManagementService().getProject(this.project.getId());
        }

        if (this.currentSource.getId() != null) {
            this.currentSource = getGenericDataService().retrieveEnity(Source.class, this.currentSource.getId());
        }

        if (this.currentSample.getId() != null) {
            this.currentSample = getGenericDataService().retrieveEnity(Sample.class, this.currentSample.getId());
        }

        if (this.currentFactor.getId() != null) {
            this.currentFactor = getGenericDataService().retrieveEnity(Factor.class, this.currentFactor.getId());
        }
    }

    /**
     * create new project.
     *
     * @return path String
     */
    @SkipValidation
    public String list() {
        setMenu("ProjectListLinks");
        setProjects(getDelegate().getProjectManagementService().getWorkspaceProjects());
        return LIST_RESULT;
    }

    /**
     * load the overview tab
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String overviewLoadTab() throws VocabularyServiceException {
        setupOverviewTab();
        return loadTab();
    }

    /**
     * set up needed lists for overview tab
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public void setupOverviewTab() throws VocabularyServiceException {
        VocabularyService vocabService = getVocabularyService();
        this.tissueSites = vocabService.getTerms(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName());
        this.tissueTypes = vocabService.getTerms(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName());
        this.cellTypes = vocabService.getTerms(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
        this.conditions = vocabService.getTerms(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());
        this.organisms = vocabService.getOrganisms();

        ArrayDesignService arrayDesignService = getArrayDesignService();
        this.arrayDesignsByManufacturer = arrayDesignService.getArrayDesignsByOrganization();
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String contactsLoadTab() {
        setupContactsTab();
        return loadTab();
    }

    /**
     * setup contacts tab
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public void setupContactsTab() {
        AuthorizationManager am = SecurityInterceptor.getAuthorizationManager();
        String username = UsernameHolder.getUser();
        this.user = am.getUser(username);

        ExperimentContact pi = getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            this.primaryInvestigator = (Person) pi.getContact();
            if (pi.isMainPointOfContact()) {
                this.piIsMainPoc = true;
                this.mainPointOfContact = new Person();
            } else {
                this.piIsMainPoc = false;
                ExperimentContact mainPoc = getProject().getExperiment().getMainPointOfContact();
                if (mainPoc != null) {
                    this.mainPointOfContact = (Person) mainPoc.getContact();
                } else {
                    this.mainPointOfContact = new Person();
                }
            }
        } else {
            this.primaryInvestigator = new Person();
            this.primaryInvestigator.setFirstName(this.user.getFirstName());
            this.primaryInvestigator.setLastName(this.user.getLastName());
            this.primaryInvestigator.setEmail(this.user.getEmailId());
            this.piIsMainPoc = true;
            this.mainPointOfContact = new Person();
        }
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String experimentalDesignLoadTab() throws VocabularyServiceException {
        setupExperimentalDesignTab();
        return loadTab();
    }

    /**
     * set up experimental design tab
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public void setupExperimentalDesignTab() throws VocabularyServiceException {
        VocabularyService vocabService = getVocabularyService();
        this.experimentDesignTypes = vocabService.getTerms(ExperimentOntologyCategory.EXPERIMENT_DESIGN_TYPE
                .getCategoryName());
        this.qualityControlTypes = vocabService.getTerms(ExperimentOntologyCategory.QUALITY_CONTROL_TYPE
                .getCategoryName());
        this.replicateTypes = vocabService.getTerms(ExperimentOntologyCategory.REPLICATE_TYPE.getCategoryName());
    }

    /**
     * Loads the manage data tab.
     *
     * @return name of result to forward to
     */
    public String manageDataLoadTab() {
        return loadTab();
    }

    /**
     * Loads the download data tab.
     *
     * @return name of result to forward to
     */
    public String downloadDataLoadTab() {
        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String loadTab() {
        return INPUT;
    }

    /**
     * Method to cleanup a set with null elements.  This method will be replaced by something else soon.
     * @param s the set to clean.
     */
    @Deprecated
    private void removeNullEntities(Set s) {
        for (Object o: s) {
            if (o == null) {
                s.remove(o);
            }
        }
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws VocabularyServiceException if there is an error retrieving terms
     * @throws Exception Exception
     */
    public String overviewSaveTab() throws VocabularyServiceException {
        removeNullEntities(getProject().getExperiment().getTissueTypes());
        removeNullEntities(getProject().getExperiment().getTissueSites());
        removeNullEntities(getProject().getExperiment().getCellTypes());
        removeNullEntities(getProject().getExperiment().getConditions());
        removeNullEntities(getProject().getExperiment().getArrayDesigns());
        String result = saveTab();
        setupOverviewTab();
        return result;
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String contactsSaveTab() {
        VocabularyService vocabService = getVocabularyService();
        TermSource mged = vocabService.getSource(ExperimentOntology.MGED.getOntologyName());
        Category roleCat = vocabService.getCategory(mged, ExperimentOntologyCategory.ROLES.getCategoryName());
        Term piRole = vocabService.getTerm(mged, roleCat, ExperimentContact.PI_ROLE);
        Term mainPocRole = vocabService.getTerm(mged, roleCat, ExperimentContact.MAIN_POC_ROLE);

        ExperimentContact pi = getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            try {
                this.primaryInvestigator.setId(pi.getContact().getId());
                PropertyUtils.copyProperties(pi.getContact(), this.primaryInvestigator);
            } catch (IllegalAccessException e) {
                // cannot happen
            } catch (InvocationTargetException e) {
                // cannot happen
            } catch (NoSuchMethodException e) {
                // cannot happen
            }
            if (this.piIsMainPoc && !pi.isMainPointOfContact()) {
                getProject().getExperiment().removeSeparateMainPointOfContact();
                pi.getRoles().add(mainPocRole);
            }
            if (!this.piIsMainPoc) {
                ExperimentContact mainPoc = getProject().getExperiment().getMainPointOfContact();
                if (pi.isMainPointOfContact()) {
                    pi.removeMainPointOfContactRole();
                    mainPoc = new ExperimentContact();
                    mainPoc.getRoles().add(mainPocRole);
                    mainPoc.setContact(new Person());
                    getProject().getExperiment().getExperimentContacts().add(mainPoc);
                    mainPoc.setExperiment(getProject().getExperiment());
                }
                try {
                    this.mainPointOfContact.setId(mainPoc.getContact().getId());
                    PropertyUtils.copyProperties(mainPoc.getContact(), this.mainPointOfContact);
                } catch (IllegalAccessException e) {
                    // cannot happen
                } catch (InvocationTargetException e) {
                    // cannot happen
                } catch (NoSuchMethodException e) {
                    // cannot happen
                }
            }
        } else {
            pi = new ExperimentContact();
            pi.setContact(this.primaryInvestigator);
            pi.getRoles().add(piRole);
            getProject().getExperiment().getExperimentContacts().add(pi);
            pi.setExperiment(getProject().getExperiment());
            if (this.piIsMainPoc) {
                pi.getRoles().add(mainPocRole);
            } else {
                ExperimentContact mainPoc = new ExperimentContact();
                mainPoc.setContact(this.mainPointOfContact);
                mainPoc.getRoles().add(mainPocRole);
                getProject().getExperiment().getExperimentContacts().add(mainPoc);
                mainPoc.setExperiment(getProject().getExperiment());
            }
        }

        String result = saveTab();
        setupContactsTab();
        return result;
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws VocabularyServiceException if there is an error retrieving terms
     * @throws Exception Exception
     */
    public String experimentalDesignSaveTab() throws VocabularyServiceException {
        removeNullEntities(getProject().getExperiment().getQualityControlTypes());
        removeNullEntities(getProject().getExperiment().getReplicateTypes());
        String result = saveTab();
        setupExperimentalDesignTab();
        return result;
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    public String saveTab() {
        String result = SUCCESS;
        if (SAVE_MODE_DRAFT.equals(this.saveMode) || SAVE_MODE_SUBMIT.equals(this.saveMode)) {
            try {
                if (SAVE_MODE_DRAFT.equals(this.saveMode)) {
                    this.initialSave = getProject().getId() == null;
                    getDelegate().getProjectManagementService().saveDraftProject((getProject()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProject().getExperiment().getTitle());
                    saveMessage(getText("project.saved", args));
                } else {
                    getDelegate().getProjectManagementService().submitProject((getProject()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProject().getExperiment().getTitle());
                    saveMessage(getText("project.submitted", args));
                    result = WORKSPACE_RESULT;
                }
            } catch (ProposalWorkflowException e) {
                List<String> args = new ArrayList<String>();
                args.add(e.getMessage());
                saveMessage(getText("project.workflowProblem", args));
            }
        } else {
            saveMessage(getText("project.updated"));
        }
        return result;
    }

    /**
     * create new project
     *
     * @return path String
     */
    @SkipValidation
    public String create() {
        setMenu("ProjectCreateLinks");
        return INPUT;
    }

    /**
     * edit existing project
     *
     * @return path String
     */
    @SkipValidation
    public String edit() {
        setMenu("ProjectEditLinks");
        return INPUT;
    }

    /**
     * edit existing project permissions
     *
     * @return path String
     */
    @SkipValidation
    public String editPermissions() {
        setMenu("ProjectEditLinks");
        return "permissions";
    }

    /**
     * show details for a project
     *
     * @return path String
     */
    @SkipValidation
    public String details() {
        setMenu("ProjectEditLinks");
        setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
        return DETAILS_RESULT;
    }

    /**
     * Toggles the browsability status.
     *
     * @return success
     */
    @SkipValidation
    public String toggle() {
        setMenu("ProjectEditLinks");
        getDelegate().getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return WORKSPACE_RESULT;
    }

    /**
     * Saves the source editing tab.
     *
     * @return the string indicating the result to use.
     */
    public String sourceEditSaveTab() {
        if (this.getCurrentSource().getId() == null) {
            getProject().getExperiment().getSources().add(getCurrentSource());
            saveMessage(getText("experiment.sources.created"));
        } else {
            saveMessage(getText("experiment.sources.updated"));
        }
        saveTab();
        return "sourcesTab";
    }

    /**
     * remove the source with the given id from the set of sources.
     *
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String sourceRemoval() {
        getProject().getExperiment().getSources().remove(getCurrentSource());
        setSaveMode(SAVE_MODE_DRAFT);
        saveTab();
        saveMessage(getText("experiment.sources.deleted"));
        return SUCCESS;
    }

    /**
     * Copy the soucre to a new object and forward to the edit screen.
     *
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String sourceCopy() {
        Source source = new Source();
        source.setDescription(getCurrentSource().getDescription());
        source.setMaterialType(getCurrentSource().getMaterialType());
        source.setName(getText("experiment.sources.copy.of") + " " + getCurrentSource().getName());
        source.setOrganism(getCurrentSource().getOrganism());
        source.setTissueSite(getCurrentSource().getTissueSite());
        setCurrentSource(source);
        saveMessage(getText("experiment.sources.copied"));
        return SUCCESS;
    }

    /**
     * Saves the samples editing tab.
     *
     * @return the string indicating the result to use.
     */
    public String sampleEditSaveTab() {
        if (getCurrentSample().getId() == null) {
            getProject().getExperiment().getSamples().add(getCurrentSample());
            saveMessage(getText("experiment.samples.created"));
        } else {
            saveMessage(getText("experiment.samples.updated"));
        }
        saveTab();
        return "samplesTab";
    }

    /**
     * remove the sample with the given id from the set of samples.
     *
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String sampleRemoval() {
        getProject().getExperiment().getSamples().remove(getCurrentSample());
        setSaveMode(SAVE_MODE_DRAFT);
        saveTab();
        saveMessage(getText("experiment.samples.deleted"));
        return SUCCESS;
    }

    /**
     * Copy the sample to a new object and forward to the edit screen.
     *
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String sampleCopy() {
        Sample sample = new Sample();
        sample.setDescription(getCurrentSample().getDescription());
        sample.setMaterialType(getCurrentSample().getMaterialType());
        sample.setName(getText("experiment.samples.copy.of") + " " + getCurrentSample().getName());
        sample.setOrganism(getCurrentSample().getOrganism());
        sample.setSpecimen(getCurrentSample().getSpecimen());
        setCurrentSample(sample);
        saveMessage(getText("experiment.samples.copied"));
        return SUCCESS;
    }

    /**
     * Saves the experimentalFactor editing tab.
     *
     * @return the string indicating the result to use.
     */
    public String factorEditSaveTab() {
        if (getCurrentFactor().getId() == null) {
            getProject().getExperiment().getFactors().add(getCurrentFactor());
            saveMessage(getText("experiment.factors.created"));
        } else {
            saveMessage(getText("experiment.factors.updated"));
        }
        saveTab();
        return "factorsTab";
    }

    /**
     * remove the factor with the given id from the set of factors.
     *
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String factorRemoval() {
        getProject().getExperiment().getFactors().remove(getCurrentFactor());
        setSaveMode(SAVE_MODE_DRAFT);
        saveTab();
        saveMessage(getText("experiment.factors.deleted"));
        return SUCCESS;
    }

    /**
     * Copy the factor to a new object and forward to the edit screen.
     *
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String factorCopy() {
        Factor factor = new Factor();
        factor.setName(getText("experiment.factors.copy.of") + " " + getCurrentFactor().getName());
        factor.setType(getCurrentFactor().getType());
        setCurrentFactor(factor);
        saveMessage(getText("experiment.factors.copied"));
        return SUCCESS;
    }

    /**
     * gets the delegate from factory.
     *
     * @return Delegate ProjectDelegate
     */
    public ProjectDelegate getDelegate() {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return this.menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the VocabularyService
     */
    private VocabularyService getVocabularyService() {
        return (VocabularyService) ServiceLocator.INSTANCE.lookup(VocabularyService.JNDI_NAME);
    }

    private GenericDataService getGenericDataService() {
        return (GenericDataService) ServiceLocator.INSTANCE.lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * @return the VocabularyService
     */
    private ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocator.INSTANCE.lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * @return the organisms
     */
    public List<Organism> getOrganisms() {
        return this.organisms;
    }

    /**
     * @param organisms the organisms to set
     */
    public void setOrganisms(List<Organism> organisms) {
        this.organisms = organisms;
    }

    /**
     * @return the tissueSites
     */
    public List<Term> getTissueSites() {
        return this.tissueSites;
    }

    /**
     * @param tissueSites the tissueSites to set
     */
    public void setTissueSites(List<Term> tissueSites) {
        this.tissueSites = tissueSites;
    }

    /**
     * @return the tissueTypes
     */
    public List<Term> getTissueTypes() {
        return this.tissueTypes;
    }

    /**
     * @param tissueTypes the tissueTypes to set
     */
    public void setTissueTypes(List<Term> tissueTypes) {
        this.tissueTypes = tissueTypes;
    }

    /**
     * @return the cellTypes
     */
    public List<Term> getCellTypes() {
        return this.cellTypes;
    }

    /**
     * @param cellTypes the cellTypes to set
     */
    public void setCellTypes(List<Term> cellTypes) {
        this.cellTypes = cellTypes;
    }

    /**
     * @return the conditions
     */
    public List<Term> getConditions() {
        return this.conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(List<Term> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the manufacturers
     */
    public List<Organization> getManufacturers() {
        return this.manufacturers;
    }

    /**
     * @param manufacturers the manufacturers to set
     */
    public void setManufacturers(List<Organization> manufacturers) {
        this.manufacturers = manufacturers;
    }

    /**
     * @return the paymentMechanisms
     */
    public List<PaymentMechanism> getPaymentMechanisms() {
        return this.paymentMechanisms;
    }

    /**
     * @param paymentMechanisms the paymentMechanisms to set
     */
    public void setPaymentMechanisms(List<PaymentMechanism> paymentMechanisms) {
        this.paymentMechanisms = paymentMechanisms;
    }

    /**
     * @return the experimentDesignTypes
     */
    public List<Term> getExperimentDesignTypes() {
        return this.experimentDesignTypes;
    }

    /**
     * @param experimentDesignTypes the experimentDesignTypes to set
     */
    public void setExperimentDesignTypes(List<Term> experimentDesignTypes) {
        this.experimentDesignTypes = experimentDesignTypes;
    }

    /**
     * @return the qualityControlTypes
     */
    public List<Term> getQualityControlTypes() {
        return this.qualityControlTypes;
    }

    /**
     * @param qualityControlTypes the qualityControlTypes to set
     */
    public void setQualityControlTypes(List<Term> qualityControlTypes) {
        this.qualityControlTypes = qualityControlTypes;
    }

    /**
     * @return the replicateTypes
     */
    public List<Term> getReplicateTypes() {
        return this.replicateTypes;
    }

    /**
     * @param replicateTypes the replicateTypes to set
     */
    public void setReplicateTypes(List<Term> replicateTypes) {
        this.replicateTypes = replicateTypes;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the saveMode
     */
    public String getSaveMode() {
        return this.saveMode;
    }

    /**
     * @param saveMode the saveMode to set
     */
    public void setSaveMode(String saveMode) {
        this.saveMode = saveMode;
    }

    /**
     * @return the arrayDesignsByManufacturer
     */
    public Map<Organization, List<ArrayDesign>> getArrayDesignsByManufacturer() {
        return this.arrayDesignsByManufacturer;
    }

    /**
     * @param arrayDesignsByManufacturer the arrayDesignsByManufacturer to set
     */
    public void setArrayDesignsByManufacturer(Map<Organization, List<ArrayDesign>> arrayDesignsByManufacturer) {
        this.arrayDesignsByManufacturer = arrayDesignsByManufacturer;
    }

    /**
     * @return the currentSource
     */
    public Source getCurrentSource() {
        return this.currentSource;
    }

    /**
     * @param currentSource the currentSource to set
     */
    public void setCurrentSource(Source currentSource) {
        this.currentSource = currentSource;
    }

    /**
     * @return the currentSample
     */
    public Sample getCurrentSample() {
        return this.currentSample;
    }

    /**
     * @param currentSample the currentSample to set
     */
    public void setCurrentSample(Sample currentSample) {
        this.currentSample = currentSample;
    }

    /**
     * @return the currentFactor
     */
    public Factor getCurrentFactor() {
        return this.currentFactor;
    }

    /**
     * @param currentFactor the currentFactor to set
     */
    public void setCurrentFactor(Factor currentFactor) {
        this.currentFactor = currentFactor;
    }

    /**
     * @return the piIsMainPoc
     */
    public boolean isPiIsMainPoc() {
        return this.piIsMainPoc;
    }

    /**
     * @param piIsMainPoc the piIsMainPoc to set
     */
    public void setPiIsMainPoc(boolean piIsMainPoc) {
        this.piIsMainPoc = piIsMainPoc;
    }

    /**
     * @return the primaryInvestigator
     */
    public Person getPrimaryInvestigator() {
        return this.primaryInvestigator;
    }

    /**
     * @param primaryInvestigator the primaryInvestigator to set
     */
    public void setPrimaryInvestigator(Person primaryInvestigator) {
        this.primaryInvestigator = primaryInvestigator;
    }

    /**
     * @return the mainPointOfContact
     */
    public Person getMainPointOfContact() {
        return this.mainPointOfContact;
    }

    /**
     * @param mainPointOfContact the mainPointOfContact to set
     */
    public void setMainPointOfContact(Person mainPointOfContact) {
        this.mainPointOfContact = mainPointOfContact;
    }

    /**
     * @return the initialSave
     */
    public boolean isInitialSave() {
        return this.initialSave;
    }

    /**
     * @param initialSave the initialSave to set
     */
    public void setInitialSave(boolean initialSave) {
        this.initialSave = initialSave;
    }
}
